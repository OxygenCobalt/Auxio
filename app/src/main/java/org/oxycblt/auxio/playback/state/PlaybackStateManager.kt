/*
 * Copyright (c) 2021 Auxio Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.playback.state

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

/**
 * Master class (and possible god object) for the playback state.
 *
 * This should ***NOT*** be used outside of the playback module.
 * - If you want to use the playback state in the UI, use
 * [org.oxycblt.auxio.playback.PlaybackViewModel] as it can withstand volatile UIs.
 * - If you want to use the playback state with the ExoPlayer instance or system-side things, use
 * [org.oxycblt.auxio.playback.system.PlaybackService].
 *
 * All access should be done with [PlaybackStateManager.getInstance].
 * @author OxygenCobalt
 */
class PlaybackStateManager private constructor() {
    private val musicStore = MusicStore.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    // Playback
    private var mSong: Song? = null
    private var mParent: MusicParent? = null

    // Queue
    private var mQueue = mutableListOf<Song>()
    private var mIndex = 0

    // State
    private var mIsPlaying = false
    private var mPosition: Long = 0
    private var mIsShuffling = false
    private var mLoopMode = LoopMode.NONE

    private var mIsRestored = false
    private var mHasPlayed = false

    /** The currently playing song. Null if there isn't one */
    val song: Song?
        get() = mSong
    /** The parent the queue is based on, null if all_songs */
    val parent: MusicParent?
        get() = mParent
    /** The current queue determined by [parent] */
    val queue: List<Song>
        get() = mQueue
    /** The current position in the queue */
    val index: Int
        get() = mIndex

    /** Whether playback is paused or not */
    val isPlaying: Boolean
        get() = mIsPlaying
    /** The current playback progress */
    val position: Long
        get() = mPosition
    /** The current [LoopMode] */
    val loopMode: LoopMode
        get() = mLoopMode
    /** Whether the queue is shuffled */
    val isShuffling: Boolean
        get() = mIsShuffling

    /** Whether this instance has already been restored */
    val isRestored: Boolean
        get() = mIsRestored
    /** Whether playback has begun in this instance during **PlaybackService's Lifecycle.** */
    val hasPlayed: Boolean
        get() = mHasPlayed

    // --- CALLBACKS ---

    private val callbacks = mutableListOf<Callback>()

    /**
     * Add a [PlaybackStateManager.Callback] to this instance. Make sure to remove the callback with
     * [removeCallback] when done.
     */
    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    /** Remove a [PlaybackStateManager.Callback] bound to this instance. */
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    // --- PLAYING FUNCTIONS ---

    /**
     * Play a [song].
     * @param mode The [PlaybackMode] to construct the queue off of.
     */
    fun playSong(song: Song, mode: PlaybackMode) {
        logD("Updating song to ${song.rawName} and mode to $mode")

        when (mode) {
            PlaybackMode.ALL_SONGS -> {
                val musicStore = musicStore.library ?: return
                mParent = null
                mQueue = musicStore.songs.toMutableList()
            }
            PlaybackMode.IN_GENRE -> {
                mParent = song.genre
                mQueue = song.genre.songs.toMutableList()
            }
            PlaybackMode.IN_ARTIST -> {
                mParent = song.album.artist
                mQueue = song.album.artist.songs.toMutableList()
            }
            PlaybackMode.IN_ALBUM -> {
                mParent = song.album
                mQueue = song.album.songs.toMutableList()
            }
        }

        notifyParentChanged()

        updatePlayback(song)
        // Keep shuffle on, if enabled
        setShuffling(settingsManager.keepShuffle && isShuffling, keepSong = true)
    }

    /**
     * Play a [parent], such as an artist or album.
     * @param shuffled Whether the queue is shuffled or not
     */
    fun playParent(parent: MusicParent, shuffled: Boolean) {
        logD("Playing ${parent.rawName}")

        mParent = parent
        notifyParentChanged()
        mIndex = 0

        mQueue =
            when (parent) {
                is Album -> {
                    parent.songs.toMutableList()
                }
                is Artist -> {
                    parent.songs.toMutableList()
                }
                is Genre -> {
                    parent.songs.toMutableList()
                }
            }

        setShuffling(shuffled, keepSong = false)
        updatePlayback(mQueue[0])
    }

    /** Shuffle all songs. */
    fun shuffleAll() {
        val library = musicStore.library ?: return

        mQueue = library.songs.toMutableList()
        mParent = null

        setShuffling(true, keepSong = false)
        updatePlayback(mQueue[0])
    }

    /** Update the playback to a new [song], doing all the required logic. */
    private fun updatePlayback(song: Song, shouldPlay: Boolean = true) {
        mSong = song
        mPosition = 0
        notifySongChanged()
        notifyPositionChanged()
        setPlaying(shouldPlay)
    }

    // --- QUEUE FUNCTIONS ---

    /** Go to the next song, along with doing all the checks that entails. */
    fun next() {
        // Increment the index, if it cannot be incremented any further, then
        // loop and pause/resume playback depending on the setting
        if (mIndex < mQueue.lastIndex) {
            mIndex = mIndex.inc()
            updatePlayback(mQueue[mIndex])
        } else {
            mIndex = 0
            updatePlayback(mQueue[mIndex], shouldPlay = loopMode == LoopMode.ALL)
        }

        notifyQueueChanged()
    }

    /** Go to the previous song, doing any checks that are needed. */
    fun prev() {
        // If enabled, rewind before skipping back if the position is past 3 seconds [3000ms]
        if (settingsManager.rewindWithPrev && mPosition >= REWIND_THRESHOLD) {
            rewind()
        } else {
            // Only decrement the index if there's a song to move back to
            if (mIndex > 0) {
                mIndex = mIndex.dec()
            }

            updatePlayback(mQueue[mIndex])
            notifyQueueChanged()
        }
    }

    // --- QUEUE EDITING FUNCTIONS ---

    /** Remove a queue item at [index]. Will ignore invalid indexes. */
    fun removeQueueItem(index: Int): Boolean {
        if (index > mQueue.size || index < 0) {
            logE("Index is out of bounds, did not remove queue item")
            return false
        }

        logD("Removing item ${mQueue[index].rawName}")
        mQueue.removeAt(index)
        notifyQueueChanged()
        return true
    }

    /** Move a queue item at [from] to a position at [to]. Will ignore invalid indexes. */
    fun moveQueueItems(from: Int, to: Int): Boolean {
        if (from > mQueue.size || from < 0 || to > mQueue.size || to < 0) {
            logE("Indices were out of bounds, did not move queue item")
            return false
        }

        logD("Moving item $from to position $to")
        mQueue.add(to, mQueue.removeAt(from))
        notifyQueueChanged()
        return true
    }

    /** Add a [song] to the top of the queue. */
    fun playNext(song: Song) {
        if (mQueue.isEmpty()) {
            return
        }

        mQueue.add(mIndex + 1, song)
        notifyQueueChanged()
    }

    /** Add a list of [songs] to the top of the queue. */
    fun playNext(songs: List<Song>) {
        if (mQueue.isEmpty()) {
            return
        }

        mQueue.addAll(mIndex + 1, songs)
        notifyQueueChanged()
    }

    /** Add a [song] to the end of the queue. */
    fun addToQueue(song: Song) {
        mQueue.add(song)
        notifyQueueChanged()
    }

    /** Add a list of [songs] to the end of the queue. */
    fun addToQueue(songs: List<Song>) {
        mQueue.addAll(songs)
        notifyQueueChanged()
    }

    // --- SHUFFLE FUNCTIONS ---

    /**
     * Set whether this instance is [shuffled]. Updates the queue accordingly.
     * @param keepSong Whether the current song should be kept as the queue is shuffled/un-shuffled
     */
    fun setShuffling(shuffled: Boolean, keepSong: Boolean) {
        mIsShuffling = shuffled
        notifyShufflingChanged()

        if (mIsShuffling) {
            genShuffle(keepSong)
        } else {
            resetShuffle(keepSong)
        }
    }

    /**
     * Generate a new shuffled queue.
     * @param keepSong Whether the current song should be kept as the queue is shuffled
     */
    private fun genShuffle(keepSong: Boolean) {
        val lastSong = mSong

        logD("Shuffling queue")

        mQueue.shuffle()
        mIndex = 0

        // If specified, make the current song the first member of the queue.
        if (keepSong) {
            moveQueueItems(mQueue.indexOf(lastSong), 0)
        } else {
            // Otherwise, just start from the zeroth position in the queue.
            mSong = mQueue[0]
        }

        notifyQueueChanged()
    }

    /**
     * Reset the queue to its normal, ordered state.
     * @param keepSong Whether the current song should be kept as the queue is un-shuffled
     */
    private fun resetShuffle(keepSong: Boolean) {
        val library = musicStore.library ?: return
        val lastSong = mSong

        val parent = parent
        mQueue =
            when (parent) {
                null -> settingsManager.libSongSort.songs(library.songs).toMutableList()
                is Album -> settingsManager.detailAlbumSort.album(parent).toMutableList()
                is Artist -> settingsManager.detailArtistSort.artist(parent).toMutableList()
                is Genre -> settingsManager.detailGenreSort.genre(parent).toMutableList()
            }

        if (keepSong) {
            mIndex = mQueue.indexOf(lastSong)
        }

        notifyQueueChanged()
    }

    // --- STATE FUNCTIONS ---

    /** Set whether this instance is currently [playing]. */
    fun setPlaying(playing: Boolean) {
        if (mIsPlaying != playing) {
            if (playing) {
                mHasPlayed = true
            }

            mIsPlaying = playing

            for (callback in callbacks) {
                callback.onPlayingChanged(playing)
            }
        }
    }

    /**
     * Update the current [position]. Will not notify listeners of a seek event.
     * @param position The new position in millis.
     * @see seekTo
     */
    fun synchronizePosition(position: Long) {
        mSong?.let { song ->
            // Don't accept any bugged positions that are over the duration of the song.
            if (position <= song.duration) {
                mPosition = position
                notifyPositionChanged()
            }
        }
    }

    /**
     * **Seek** to a [position], this calls [PlaybackStateManager.Callback.onSeek] to notify
     * elements that rely on that.
     * @param position The position to seek to in millis.
     */
    fun seekTo(position: Long) {
        mPosition = position
        notifyPositionChanged()
        callbacks.forEach { it.onSeek(position) }
    }

    /** Rewind to the beginning of a song. */
    fun rewind() {
        seekTo(0)
        setPlaying(true)
    }

    /** Loop playback around to the beginning. */
    fun loop() {
        seekTo(0)
        setPlaying(!settingsManager.pauseOnLoop)
    }

    /** Set the [LoopMode] to [mode]. */
    fun setLoopMode(mode: LoopMode) {
        mLoopMode = mode
        notifyLoopModeChanged()
    }

    /** Mark whether this instance has played or not */
    fun setHasPlayed(hasPlayed: Boolean) {
        mHasPlayed = hasPlayed
    }

    /** Mark this instance as restored. */
    fun markRestored() {
        mIsRestored = true
    }

    // --- PERSISTENCE FUNCTIONS ---

    /**
     * Save the current state to the database.
     * @param context [Context] required
     */
    suspend fun saveStateToDatabase(context: Context) {
        logD("Saving state to DB")

        // Pack the entire state and save it to the database.
        withContext(Dispatchers.IO) {
            val start = System.currentTimeMillis()
            val database = PlaybackStateDatabase.getInstance(context)

            val playbackMode =
                when (parent) {
                    is Album -> PlaybackMode.IN_ALBUM
                    is Artist -> PlaybackMode.IN_ARTIST
                    is Genre -> PlaybackMode.IN_GENRE
                    null -> PlaybackMode.ALL_SONGS
                }

            database.writeState(
                PlaybackStateDatabase.SavedState(
                    song,
                    position,
                    parent,
                    index,
                    playbackMode,
                    isShuffling,
                    loopMode,
                ))

            database.writeQueue(mQueue)

            this@PlaybackStateManager.logD(
                "State save completed successfully in ${System.currentTimeMillis() - start}ms")
        }
    }

    /**
     * Restore the state from the database
     * @param context [Context] required.
     */
    suspend fun restoreFromDatabase(context: Context) {
        logD("Getting state from DB")

        val library = musicStore.library ?: return
        val start: Long
        val playbackState: PlaybackStateDatabase.SavedState?
        val queue: MutableList<Song>

        withContext(Dispatchers.IO) {
            start = System.currentTimeMillis()
            val database = PlaybackStateDatabase.getInstance(context)
            playbackState = database.readState(library)
            queue = database.readQueue(library)
        }

        // Get off the IO coroutine since it will cause LiveData updates to throw an exception

        if (playbackState != null) {
            unpackFromPlaybackState(playbackState)
            mQueue = queue
            notifyQueueChanged()
            doParentSanityCheck(playbackState.playbackMode)
            doIndexSanityCheck()
        }

        logD("State load completed successfully in ${System.currentTimeMillis() - start}ms")

        markRestored()
    }

    /** Unpack a [playbackState] into this instance. */
    private fun unpackFromPlaybackState(playbackState: PlaybackStateDatabase.SavedState) {
        // Do queue setup first
        mParent = playbackState.parent
        mIndex = playbackState.queueIndex

        // Then set up the current state
        mSong = playbackState.song
        mLoopMode = playbackState.loopMode
        mIsShuffling = playbackState.isShuffling

        notifySongChanged()
        notifyParentChanged()
        seekTo(playbackState.position)
        notifyShufflingChanged()
        notifyLoopModeChanged()
    }

    /** Do a sanity check to make sure the parent was not lost in the restore process. */
    private fun doParentSanityCheck(playbackMode: PlaybackMode) {
        // Check if the parent was lost while in the DB.
        if (mSong != null && mParent == null && playbackMode != PlaybackMode.ALL_SONGS) {
            logD("Parent lost, attempting restore")

            mParent =
                when (playbackMode) {
                    PlaybackMode.ALL_SONGS -> null
                    PlaybackMode.IN_ALBUM -> mQueue.firstOrNull()?.album
                    PlaybackMode.IN_ARTIST -> mQueue.firstOrNull()?.album?.artist
                    PlaybackMode.IN_GENRE -> mQueue.firstOrNull()?.genre
                }

            notifyParentChanged()
        }
    }

    /** Do a sanity check to make sure that the index lines up with the current song. */
    private fun doIndexSanityCheck() {
        // Be careful with how we handle the queue since a possible index de-sync
        // could easily result in an OOB crash.
        if (mSong != null && mSong != mQueue.getOrNull(mIndex)) {
            val correctedIndex = mQueue.wobblyIndexOfFirst(mIndex, mSong)
            if (correctedIndex > -1) {
                logD("Correcting malformed index to $correctedIndex")
                mIndex = correctedIndex
                notifyQueueChanged()
            }
        }
    }

    /**
     * Finds the index of an item through a sort-of "wobbly" search where it progressively searches
     * for item away from the [start] index, instead of from position zero. This is useful, as it
     * increases the likelihood that the correct index was found instead of the index of a
     * duplicate.
     */
    private fun <T> List<T>.wobblyIndexOfFirst(start: Int, item: T): Int {
        if (start !in indices) {
            return -1
        }

        var idx = start
        var multiplier = -1
        var delta = -1

        while (true) {
            idx += delta

            if (idx !in indices) {
                if (-idx !in indices) {
                    return -1
                }
            } else if (this.getOrNull(idx) == item) {
                return idx
            }

            delta = -delta
            multiplier = -multiplier
            delta += multiplier
        }
    }

    // --- CALLBACKS ---

    private fun notifySongChanged() {
        for (callback in callbacks) {
            callback.onSongChanged(song)
        }
    }

    private fun notifyParentChanged() {
        for (callback in callbacks) {
            callback.onParentChanged(parent)
        }
    }

    private fun notifyPositionChanged() {
        for (callback in callbacks) {
            callback.onPositionChanged(position)
        }
    }

    private fun notifyLoopModeChanged() {
        for (callback in callbacks) {
            callback.onLoopModeChanged(loopMode)
        }
    }

    private fun notifyShufflingChanged() {
        for (callback in callbacks) {
            callback.onShuffleChanged(isShuffling)
        }
    }

    /** Force any callbacks to receive a queue update. */
    private fun notifyQueueChanged() {
        for (callback in callbacks) {
            callback.onQueueChanged(mQueue, mIndex)
        }
    }

    /**
     * The interface for receiving updates from [PlaybackStateManager]. Add the callback to
     * [PlaybackStateManager] using [addCallback], remove them on destruction with [removeCallback].
     */
    interface Callback {
        fun onSongChanged(song: Song?) {}
        fun onParentChanged(parent: MusicParent?) {}
        fun onPositionChanged(position: Long) {}
        fun onQueueChanged(queue: List<Song>, index: Int) {}
        fun onPlayingChanged(isPlaying: Boolean) {}
        fun onShuffleChanged(isShuffling: Boolean) {}
        fun onLoopModeChanged(loopMode: LoopMode) {}
        fun onSeek(position: Long) {}
    }

    companion object {
        private const val REWIND_THRESHOLD = 3000L

        @Volatile private var INSTANCE: PlaybackStateManager? = null

        /** Get/Instantiate the single instance of [PlaybackStateManager]. */
        fun getInstance(): PlaybackStateManager {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = PlaybackStateManager()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

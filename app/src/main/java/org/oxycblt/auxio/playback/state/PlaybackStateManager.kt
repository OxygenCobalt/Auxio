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
import kotlin.math.max
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
    private var mutableQueue = mutableListOf<Song>()

    /** The currently playing song. Null if there isn't one */
    val song
        get() = queue.getOrNull(index)
    /** The parent the queue is based on, null if all songs */
    var parent: MusicParent? = null
        private set
    /** The current queue determined by [parent] */
    val queue
        get() = mutableQueue
    /** The current position in the queue */
    var index = -1
        private set

    /** Whether playback is playing or not */
    var isPlaying = false
        set(value) {
            field = value
            notifyPlayingChanged()
        }
    /** The current playback progress */
    var positionMs = 0L
        private set
    /** The current [LoopMode] */
    var loopMode = LoopMode.NONE
        set(value) {
            field = value
            notifyLoopModeChanged()
        }
    /** Whether the queue is shuffled */
    var isShuffled = false
        private set

    /** Whether this instance has already been restored */
    var isRestored = false
        private set

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
     * @param playbackMode The [PlaybackMode] to construct the queue off of.
     */
    fun play(song: Song, playbackMode: PlaybackMode) {
        val library = musicStore.library ?: return

        parent =
            when (playbackMode) {
                PlaybackMode.ALL_SONGS -> null
                PlaybackMode.IN_ALBUM -> song.album
                PlaybackMode.IN_ARTIST -> song.album.artist
                PlaybackMode.IN_GENRE -> song.genre
            }

        applyNewQueue(library, settingsManager.keepShuffle && isShuffled, song, true)
        notifyNewPlayback()
        notifyShuffledChanged()
        isPlaying = true
    }

    /**
     * Play a [parent], such as an artist or album.
     * @param shuffled Whether the queue is shuffled or not
     */
    fun play(parent: MusicParent?, shuffled: Boolean) {
        val library = musicStore.library ?: return
        this.parent = parent
        applyNewQueue(library, shuffled, null, true)
        notifyNewPlayback()
        notifyShuffledChanged()
        isPlaying = true
    }

    /** Shuffle all songs. */
    fun shuffleAll() {
        val library = musicStore.library ?: return
        parent = null
        applyNewQueue(library, true, null, true)
        notifyNewPlayback()
        notifyShuffledChanged()
        isPlaying = true
    }

    // --- QUEUE FUNCTIONS ---

    /** Go to the next song, along with doing all the checks that entails. */
    fun next() {
        // Increment the index, if it cannot be incremented any further, then
        // loop and pause/resume playback depending on the setting
        if (index < mutableQueue.lastIndex) {
            goto(++index, true)
        } else {
            goto(0, loopMode == LoopMode.ALL)
        }
    }

    /** Go to the previous song, doing any checks that are needed. */
    fun prev() {
        // If enabled, rewind before skipping back if the position is past 3 seconds [3000ms]
        if (settingsManager.rewindWithPrev && positionMs >= REWIND_THRESHOLD) {
            rewind()
            isPlaying = true
        } else {
            goto(max(--index, 0), true)
        }
    }

    private fun goto(idx: Int, play: Boolean) {
        index = idx
        notifyIndexMoved()
        isPlaying = play
    }

    /** Add a [song] to the top of the queue. */
    fun playNext(song: Song) {
        mutableQueue.add(++index, song)
        notifyQueueChanged()
    }

    /** Add a list of [songs] to the top of the queue. */
    fun playNext(songs: List<Song>) {
        mutableQueue.addAll(++index, songs)
        notifyQueueChanged()
    }

    /** Add a [song] to the end of the queue. */
    fun addToQueue(song: Song) {
        mutableQueue.add(song)
        notifyQueueChanged()
    }

    /** Add a list of [songs] to the end of the queue. */
    fun addToQueue(songs: List<Song>) {
        mutableQueue.addAll(songs)
        notifyQueueChanged()
    }

    /** Move a queue item at [from] to a position at [to]. Will ignore invalid indexes. */
    fun moveQueueItem(from: Int, to: Int) {
        logD("Moving item $from to position $to")
        mutableQueue.add(to, mutableQueue.removeAt(from))
        notifyQueueChanged()
    }

    /** Remove a queue item at [index]. Will ignore invalid indexes. */
    fun removeQueueItem(index: Int) {
        logD("Removing item ${mutableQueue[index].rawName}")
        mutableQueue.removeAt(index)
        notifyQueueChanged()
    }

    /** Set whether this instance is [shuffled]. Updates the queue accordingly. */
    fun reshuffle(shuffled: Boolean) {
        val library = musicStore.library ?: return
        val song = song ?: return
        applyNewQueue(library, shuffled, song, false)
        notifyQueueChanged()
        notifyShuffledChanged()
    }

    private fun applyNewQueue(
        library: MusicStore.Library,
        shuffled: Boolean,
        keep: Song?,
        regenShuffledQueue: Boolean
    ) {
        if (shuffled) {
            if (regenShuffledQueue) {
                mutableQueue =
                    parent
                        .let { parent ->
                            when (parent) {
                                null -> library.songs
                                is Album -> parent.songs
                                is Artist -> parent.songs
                                is Genre -> parent.songs
                            }
                        }
                        .toMutableList()
            }

            mutableQueue.shuffle()

            if (keep != null) {
                mutableQueue.add(0, mutableQueue.removeAt(mutableQueue.indexOf(keep)))
            }

            index = 0
        } else {
            mutableQueue =
                parent
                    .let { parent ->
                        when (parent) {
                            null -> settingsManager.libSongSort.songs(library.songs)
                            is Album -> settingsManager.detailAlbumSort.album(parent)
                            is Artist -> settingsManager.detailArtistSort.artist(parent)
                            is Genre -> settingsManager.detailGenreSort.genre(parent)
                        }
                    }
                    .toMutableList()

            index = keep?.let(queue::indexOf) ?: 0
        }

        isShuffled = shuffled
    }

    // --- STATE FUNCTIONS ---

    /**
     * Update the current [positionMs]. Will not notify listeners of a seek event.
     * @param positionMs The new position in millis.
     * @see seekTo
     */
    fun synchronizePosition(positionMs: Long) {
        // Don't accept any bugged positions that are over the duration of the song.
        val maxDuration = song?.duration ?: -1
        if (positionMs <= maxDuration) {
            this.positionMs = positionMs
            notifyPositionChanged()
        }
    }

    /**
     * **Seek** to a [positionMs], this calls [PlaybackStateManager.Callback.onSeek] to notify
     * elements that rely on that.
     * @param positionMs The position to seek to in millis.
     */
    fun seekTo(positionMs: Long) {
        this.positionMs = positionMs
        notifySeekEvent()
        notifyPositionChanged()
    }

    /** Rewind to the beginning of a song. */
    fun rewind() = seekTo(0)

    /** Loop playback around to the beginning. */
    fun loop() = seekTo(0)

    // TODO: Rework these methods eventually

    /** Mark this instance as restored. */
    fun markRestored() {
        isRestored = true
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
                    positionMs,
                    parent,
                    index,
                    playbackMode,
                    isShuffled,
                    loopMode,
                ))

            database.writeQueue(mutableQueue)

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
            parent = playbackState.parent
            mutableQueue = queue
            index = playbackState.queueIndex
            loopMode = playbackState.loopMode
            isShuffled = playbackState.isShuffled

            notifyNewPlayback()
            seekTo(playbackState.positionMs)
            notifyLoopModeChanged()
            notifyShuffledChanged()
        }

        logD("State load completed successfully in ${System.currentTimeMillis() - start}ms")

        markRestored()
    }

    // --- CALLBACKS ---

    private fun notifyIndexMoved() {
        for (callback in callbacks) {
            callback.onIndexMoved(index)
        }
    }

    private fun notifyQueueChanged() {
        for (callback in callbacks) {
            callback.onQueueChanged(index, queue)
        }
    }

    private fun notifyNewPlayback() {
        for (callback in callbacks) {
            callback.onNewPlayback(index, queue, parent)
        }
    }

    private fun notifyPlayingChanged() {
        for (callback in callbacks) {
            callback.onPlayingChanged(isPlaying)
        }
    }

    private fun notifyPositionChanged() {
        for (callback in callbacks) {
            callback.onPositionChanged(positionMs)
        }
    }

    private fun notifyLoopModeChanged() {
        for (callback in callbacks) {
            callback.onLoopModeChanged(loopMode)
        }
    }

    private fun notifyShuffledChanged() {
        for (callback in callbacks) {
            callback.onShuffledChanged(isShuffled)
        }
    }

    private fun notifySeekEvent() {
        for (callback in callbacks) {
            callback.onSeek(positionMs)
        }
    }

    /**
     * The interface for receiving updates from [PlaybackStateManager]. Add the callback to
     * [PlaybackStateManager] using [addCallback], remove them on destruction with [removeCallback].
     */
    interface Callback {
        fun onIndexMoved(index: Int) {}
        fun onQueueChanged(index: Int, queue: List<Song>) {}
        fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {}

        fun onPlayingChanged(isPlaying: Boolean) {}
        fun onPositionChanged(positionMs: Long) {}
        fun onLoopModeChanged(loopMode: LoopMode) {}
        fun onShuffledChanged(isShuffled: Boolean) {}

        fun onSeek(positionMs: Long) {}
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

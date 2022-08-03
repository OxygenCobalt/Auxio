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

import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * Master class (and possible god object) for the playback state.
 *
 * Whereas other apps centralize the playback state around the MediaSession, Auxio does not, as
 * MediaSession is a terrible API that prevents nice features like better album cover loading or a
 * reasonable queue system.
 *
 * This should ***NOT*** be used outside of the playback module.
 * - If you want to use the playback state in the UI, use
 * [org.oxycblt.auxio.playback.PlaybackViewModel] as it can withstand volatile UIs.
 * - If you want to use the playback state with the ExoPlayer instance or system-side things, use
 * [org.oxycblt.auxio.playback.system.PlaybackService].
 *
 * Internal consumers should usually use [Callback], however the component that manages the player
 * itself should instead operate as a [Controller].
 *
 * All access should be done with [PlaybackStateManager.getInstance].
 * @author OxygenCobalt
 */
class PlaybackStateManager private constructor() {
    private val musicStore = MusicStore.getInstance()

    /** The currently playing song. Null if there isn't one */
    val song
        get() = queue.getOrNull(index)
    /** The parent the queue is based on, null if all songs */
    var parent: MusicParent? = null
        private set
    private var _queue = mutableListOf<Song>()
    /** The current queue determined by [parent] */
    val queue
        get() = _queue
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
    private var positionMs = 0L
    /** The current [RepeatMode] */
    var repeatMode = RepeatMode.NONE
        set(value) {
            field = value
            notifyRepeatModeChanged()
        }
    /** Whether the queue is shuffled */
    var isShuffled = false
        private set

    /** Whether this instance has played something or restored a state. */
    var isInitialized = false
        private set

    // --- CALLBACKS ---

    private val callbacks = mutableListOf<Callback>()
    private var controller: Controller? = null

    /** Add a callback to this instance. Make sure to remove it when done. */
    @Synchronized
    fun addCallback(callback: Callback) {
        if (isInitialized) {
            callback.onNewPlayback(index, queue, parent)
            callback.onPositionChanged(positionMs)
            callback.onPlayingChanged(isPlaying)
            callback.onRepeatChanged(repeatMode)
            callback.onShuffledChanged(isShuffled)
        }

        callbacks.add(callback)
    }

    /** Remove a [Callback] bound to this instance. */
    @Synchronized
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    /** Register a [Controller] with this instance. */
    @Synchronized
    fun registerController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller != null) {
            logW("Controller is already registered")
            return
        }

        if (isInitialized) {
            controller.loadSong(song)
            controller.seekTo(positionMs)
            controller.onPlayingChanged(isPlaying)
            controller.onPlayingChanged(isPlaying)
        }

        this.controller = controller
    }

    /** Unregister a [Controller] with this instance. */
    @Synchronized
    fun unregisterController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller !== controller) {
            logW("Given controller did not match current controller")
            return
        }

        this.controller = null
    }

    // --- PLAYING FUNCTIONS ---

    /** Play a [song]. */
    @Synchronized
    fun play(song: Song, playbackMode: PlaybackMode, settings: Settings) {
        val library = musicStore.library ?: return

        parent =
            when (playbackMode) {
                PlaybackMode.ALL_SONGS -> null
                PlaybackMode.IN_ALBUM -> song.album
                PlaybackMode.IN_ARTIST -> song.album.artist
                PlaybackMode.IN_GENRE -> song.genre
            }

        applyNewQueue(library, settings, settings.keepShuffle && isShuffled, song)
        seekTo(0)
        notifyNewPlayback()
        notifyShuffledChanged()
        isPlaying = true
        isInitialized = true
    }

    /** Play a [parent], such as an artist or album. */
    @Synchronized
    fun play(parent: MusicParent, shuffled: Boolean, settings: Settings) {
        val library = musicStore.library ?: return
        this.parent = parent
        applyNewQueue(library, settings, shuffled, null)
        seekTo(0)
        notifyNewPlayback()
        notifyShuffledChanged()
        isPlaying = true
        isInitialized = true
    }

    /** Shuffle all songs. */
    @Synchronized
    fun shuffleAll(settings: Settings) {
        val library = musicStore.library ?: return
        parent = null
        applyNewQueue(library, settings, true, null)
        seekTo(0)
        notifyNewPlayback()
        notifyShuffledChanged()
        isPlaying = true
        isInitialized = true
    }

    // --- QUEUE FUNCTIONS ---

    /** Go to the next song, along with doing all the checks that entails. */
    @Synchronized
    fun next() {
        // Increment the index, if it cannot be incremented any further, then
        // repeat and pause/resume playback depending on the setting
        if (index < _queue.lastIndex) {
            gotoImpl(index + 1, true)
        } else {
            gotoImpl(0, repeatMode == RepeatMode.ALL)
        }
    }

    /** Go to the previous song, doing any checks that are needed. */
    @Synchronized
    fun prev() {
        // If enabled, rewind before skipping back if the position is past 3 seconds [3000ms]
        if (controller?.shouldPrevRewind() == true) {
            rewind()
            isPlaying = true
        } else {
            gotoImpl(max(index - 1, 0), true)
        }
    }

    @Synchronized
    fun goto(index: Int) {
        gotoImpl(index, true)
    }

    private fun gotoImpl(idx: Int, play: Boolean) {
        index = idx
        seekTo(0)
        notifyIndexMoved()
        isPlaying = play
    }

    /** Add a [song] to the top of the queue. */
    @Synchronized
    fun playNext(song: Song) {
        _queue.add(index + 1, song)
        notifyQueueChanged()
    }

    /** Add a list of [songs] to the top of the queue. */
    @Synchronized
    fun playNext(songs: List<Song>) {
        _queue.addAll(index + 1, songs)
        notifyQueueChanged()
    }

    /** Add a [song] to the end of the queue. */
    @Synchronized
    fun addToQueue(song: Song) {
        _queue.add(song)
        notifyQueueChanged()
    }

    /** Add a list of [songs] to the end of the queue. */
    @Synchronized
    fun addToQueue(songs: List<Song>) {
        _queue.addAll(songs)
        notifyQueueChanged()
    }

    /** Move a queue item at [from] to a position at [to]. Will ignore invalid indexes. */
    @Synchronized
    fun moveQueueItem(from: Int, to: Int) {
        logD("Moving item $from to position $to")
        _queue.add(to, _queue.removeAt(from))
        notifyQueueChanged()
    }

    /** Remove a queue item at [index]. Will ignore invalid indexes. */
    @Synchronized
    fun removeQueueItem(index: Int) {
        logD("Removing item ${_queue[index].rawName}")
        _queue.removeAt(index)
        notifyQueueChanged()
    }

    /** Set whether this instance is [shuffled]. Updates the queue accordingly. */
    @Synchronized
    fun reshuffle(shuffled: Boolean, settings: Settings) {
        val library = musicStore.library ?: return
        val song = song ?: return
        applyNewQueue(library, settings, shuffled, song)
        notifyQueueReworked()
        notifyShuffledChanged()
    }

    private fun applyNewQueue(
        library: MusicStore.Library,
        settings: Settings,
        shuffled: Boolean,
        keep: Song?
    ) {
        val newQueue = (parent?.songs ?: library.songs).toMutableList()
        val newIndex: Int

        if (shuffled) {
            newQueue.shuffle()

            if (keep != null) {
                newQueue.add(0, newQueue.removeAt(newQueue.indexOf(keep)))
            }

            newIndex = 0
        } else {
            val sort =
                parent.let { parent ->
                    when (parent) {
                        null -> settings.libSongSort
                        is Album -> settings.detailAlbumSort
                        is Artist -> settings.detailArtistSort
                        is Genre -> settings.detailGenreSort
                    }
                }

            sort.songsInPlace(newQueue)

            newIndex = keep?.let(newQueue::indexOf) ?: 0
        }

        _queue = newQueue
        index = newIndex
        isShuffled = shuffled
    }

    // --- STATE FUNCTIONS ---

    /** Update the current [positionMs]. Only meant for use by [Controller] */
    @Synchronized
    fun synchronizePosition(controller: Controller, positionMs: Long) {
        if (BuildConfig.DEBUG && this.controller !== controller) {
            logW("Given controller did not match current controller")
            return
        }

        // Don't accept any bugged positions that are over the duration of the song.
        val maxDuration = song?.durationMs ?: -1
        if (positionMs <= maxDuration) {
            this.positionMs = positionMs
            notifyPositionChanged()
        }
    }

    /**
     * **Seek** to a [positionMs].
     * @param positionMs The position to seek to in millis.
     */
    @Synchronized
    fun seekTo(positionMs: Long) {
        this.positionMs = positionMs
        controller?.seekTo(positionMs)
        notifyPositionChanged()
    }

    /** Rewind to the beginning of a song. */
    fun rewind() = seekTo(0)

    // --- PERSISTENCE FUNCTIONS ---

    /** Restore the state from the [database]. Returns if a state was restored. */
    suspend fun restoreState(database: PlaybackStateDatabase): Boolean {
        val library = musicStore.library ?: return false
        val state = withContext(Dispatchers.IO) { database.read(library) }

        synchronized(this) {
            val exists =
                if (state != null && !isInitialized) {
                    // Continuing playback while also possibly doing drastic state updates is
                    // a bad idea, so pause.
                    isPlaying = false

                    index = state.index
                    parent = state.parent
                    _queue = state.queue.toMutableList()
                    repeatMode = state.repeatMode
                    isShuffled = state.isShuffled

                    notifyNewPlayback()
                    seekTo(state.positionMs)
                    notifyRepeatModeChanged()
                    notifyShuffledChanged()

                    true
                } else {
                    false
                }

            isInitialized = true

            return exists
        }
    }

    /** Save the current state to the [database]. */
    suspend fun saveState(database: PlaybackStateDatabase) {
        logD("Saving state to DB")
        val state = synchronized(this) { makeStateImpl() }
        withContext(Dispatchers.IO) { database.write(state) }
    }

    suspend fun wipeState(database: PlaybackStateDatabase) {
        logD("Wiping state")
        withContext(Dispatchers.IO) { database.write(null) }
    }

    /** Sanitize the state with [newLibrary]. */
    @Synchronized
    fun sanitize(newLibrary: MusicStore.Library) {
        if (!isInitialized) {
            logD("Not initialized, no need to sanitize")
            return
        }

        logD("Sanitizing state")

        // While we could just save and reload the state, we instead sanitize the state
        // at runtime for better efficiency (and to sidestep a co-routine on behalf of the caller).

        val oldSongId = song?.id
        val oldPosition = positionMs

        parent =
            parent?.let {
                when (it) {
                    is Album -> newLibrary.sanitize(it)
                    is Artist -> newLibrary.sanitize(it)
                    is Genre -> newLibrary.sanitize(it)
                }
            }

        _queue = newLibrary.sanitize(_queue).toMutableList()

        while (song?.id != oldSongId && index > -1) {
            index--
        }

        // Continuing playback while also possibly doing drastic state updates is
        // a bad idea, so pause.
        isPlaying = false
        notifyNewPlayback()

        if (index > -1) {
            // Controller may have reloaded the media item, re-seek to the previous position
            seekTo(oldPosition)
        }
    }

    private fun makeStateImpl() =
        PlaybackStateDatabase.SavedState(
            index = index,
            parent = parent,
            queue = _queue,
            positionMs = positionMs,
            isShuffled = isShuffled,
            repeatMode = repeatMode)

    // --- CALLBACKS ---

    private fun notifyIndexMoved() {
        controller?.loadSong(song)
        for (callback in callbacks) {
            callback.onIndexMoved(index)
        }
    }

    private fun notifyQueueChanged() {
        for (callback in callbacks) {
            callback.onQueueChanged(queue)
        }
    }

    private fun notifyQueueReworked() {
        for (callback in callbacks) {
            callback.onQueueReworked(index, queue)
        }
    }

    private fun notifyNewPlayback() {
        controller?.loadSong(song)
        for (callback in callbacks) {
            callback.onNewPlayback(index, queue, parent)
        }
    }

    private fun notifyPlayingChanged() {
        controller?.onPlayingChanged(isPlaying)
        for (callback in callbacks) {
            callback.onPlayingChanged(isPlaying)
        }
    }

    private fun notifyPositionChanged() {
        for (callback in callbacks) {
            callback.onPositionChanged(positionMs)
        }
    }

    private fun notifyRepeatModeChanged() {
        for (callback in callbacks) {
            callback.onRepeatChanged(repeatMode)
        }
    }

    private fun notifyShuffledChanged() {
        for (callback in callbacks) {
            callback.onShuffledChanged(isShuffled)
        }
    }

    /** Represents a class capable of managing the internal player. */
    interface Controller {
        /** Called when a new song should be loaded into the player. */
        fun loadSong(song: Song?)

        /** Seek to [positionMs] in the player. */
        fun seekTo(positionMs: Long)

        /** Called when the class wants to determine whether it should rewind or skip back. */
        fun shouldPrevRewind(): Boolean

        /** Called when the playing state is changed. */
        fun onPlayingChanged(isPlaying: Boolean)

        //        /** Called when the repeat mode is changed. */
        //        fun onRepeatChanged(repeatMode: RepeatMode)
        //
        //        /** Called when the shuffled state is changed. */
        //        fun onShuffledChanged(isShuffled: Boolean)
    }

    /**
     * The interface for receiving updates from [PlaybackStateManager]. Add the callback to
     * [PlaybackStateManager] using [addCallback], remove them on destruction with [removeCallback].
     */
    interface Callback {
        /** Called when the index is moved, but the queue does not change. This changes the song. */
        fun onIndexMoved(index: Int) {}

        /** Called when the queue has changed in a way that does not change the index or song. */
        fun onQueueChanged(queue: List<Song>) {}

        /** Called when the queue and index has changed, but the song has not changed. */
        fun onQueueReworked(index: Int, queue: List<Song>) {}

        /** Called when playback is changed completely, with a new index, queue, and parent. */
        fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {}

        /** Called when the playing state is changed. */
        fun onPlayingChanged(isPlaying: Boolean) {}

        /** Called when the position is re-synchronized by the controller. */
        fun onPositionChanged(positionMs: Long) {}

        /** Called when the repeat mode is changed. */
        fun onRepeatChanged(repeatMode: RepeatMode) {}

        /** Called when the shuffled state is changed. */
        fun onShuffledChanged(isShuffled: Boolean) {}
    }

    companion object {
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

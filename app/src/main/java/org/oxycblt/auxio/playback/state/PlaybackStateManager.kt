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

    /** Whether this instance has been initialized */
    var isInitialized = false
        private set

    // --- CALLBACKS ---

    private val callbacks = mutableListOf<Callback>()
    private var controller: Controller? = null

    /** Add a callback to this instance. Make sure to remove it when done. */
    fun addCallback(callback: Callback) {
        synchronized(this) {
            if (isInitialized) {
                callback.onNewPlayback(index, queue, parent)
                callback.onPositionChanged(positionMs)
                callback.onPlayingChanged(isPlaying)
                callback.onRepeatChanged(repeatMode)
                callback.onShuffledChanged(isShuffled)
            }

            callbacks.add(callback)
        }
    }

    /** Remove a [Callback] bound to this instance. */
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    /** Register a [Controller] with this instance. */
    fun registerController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller != null) {
            logW("Controller is already registered")
            return
        }

        synchronized(this) {
            if (isInitialized) {
                controller.loadSong(song)
                controller.seekTo(positionMs)
                controller.onPlayingChanged(isPlaying)
                controller.onPlayingChanged(isPlaying)
            }

            this.controller = controller
        }
    }

    /** Unregister a [Controller] with this instance. */
    fun unregisterController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller !== controller) {
            logW("Given controller did not match current controller")
            return
        }

        this.controller = null
    }

    // --- PLAYING FUNCTIONS ---

    /**
     * Play a [song].
     * @param playbackMode The [PlaybackMode] to construct the queue off of.
     */
    fun play(song: Song, playbackMode: PlaybackMode, settings: Settings) {
        val library = musicStore.library ?: return

        synchronized(this) {
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
    }

    /**
     * Play a [parent], such as an artist or album.
     * @param shuffled Whether the queue is shuffled or not
     */
    fun play(parent: MusicParent, shuffled: Boolean, settings: Settings) {
        val library = musicStore.library ?: return

        synchronized(this) {
            this.parent = parent
            applyNewQueue(library, settings, shuffled, null)
            seekTo(0)
            notifyNewPlayback()
            notifyShuffledChanged()
            isPlaying = true
            isInitialized = true
        }
    }

    /** Shuffle all songs. */
    fun shuffleAll(settings: Settings) {
        val library = musicStore.library ?: return
        synchronized(this) {
            parent = null
            applyNewQueue(library, settings, true, null)
            seekTo(0)
            notifyNewPlayback()
            notifyShuffledChanged()
            isPlaying = true
            isInitialized = true
        }
    }

    // --- QUEUE FUNCTIONS ---

    /** Go to the next song, along with doing all the checks that entails. */
    fun next() {
        synchronized(this) {
            // Increment the index, if it cannot be incremented any further, then
            // repeat and pause/resume playback depending on the setting
            if (index < _queue.lastIndex) {
                goto(index + 1, true)
            } else {
                goto(0, repeatMode == RepeatMode.ALL)
            }
        }
    }

    /** Go to the previous song, doing any checks that are needed. */
    fun prev() {
        synchronized(this) {
            // If enabled, rewind before skipping back if the position is past 3 seconds [3000ms]
            if (controller?.shouldPrevRewind() == true) {
                rewind()
                isPlaying = true
            } else {
                goto(max(index - 1, 0), true)
            }
        }
    }

    private fun goto(idx: Int, play: Boolean) {
        index = idx
        seekTo(0)
        notifyIndexMoved()
        isPlaying = play
    }

    /** Add a [song] to the top of the queue. */
    fun playNext(song: Song) {
        synchronized(this) {
            _queue.add(index + 1, song)
            notifyQueueChanged()
        }
    }

    /** Add a list of [songs] to the top of the queue. */
    fun playNext(songs: List<Song>) {
        synchronized(this) {
            _queue.addAll(index + 1, songs)
            notifyQueueChanged()
        }
    }

    /** Add a [song] to the end of the queue. */
    fun addToQueue(song: Song) {
        synchronized(this) {
            _queue.add(song)
            notifyQueueChanged()
        }
    }

    /** Add a list of [songs] to the end of the queue. */
    fun addToQueue(songs: List<Song>) {
        synchronized(this) {
            _queue.addAll(songs)
            notifyQueueChanged()
        }
    }

    /** Move a queue item at [from] to a position at [to]. Will ignore invalid indexes. */
    fun moveQueueItem(from: Int, to: Int) {
        logD("Moving item $from to position $to")

        synchronized(this) {
            _queue.add(to, _queue.removeAt(from))
            notifyQueueChanged()
        }
    }

    /** Remove a queue item at [index]. Will ignore invalid indexes. */
    fun removeQueueItem(index: Int) {
        logD("Removing item ${_queue[index].rawName}")

        synchronized(this) {
            _queue.removeAt(index)
            notifyQueueChanged()
        }
    }

    /** Set whether this instance is [shuffled]. Updates the queue accordingly. */
    fun reshuffle(shuffled: Boolean, settings: Settings) {
        val library = musicStore.library ?: return
        synchronized(this) {
            val song = song ?: return
            applyNewQueue(library, settings, shuffled, song)
            notifyQueueChanged()
            notifyShuffledChanged()
        }
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

    /**
     * Update the current [positionMs]. Will not notify listeners of a seek event.
     * @param positionMs The new position in millis.
     * @see seekTo
     */
    fun synchronizePosition(controller: Controller, positionMs: Long) {
        if (BuildConfig.DEBUG && this.controller !== controller) {
            logW("Given controller did not match current controller")
            return
        }

        synchronized(this) {
            // Don't accept any bugged positions that are over the duration of the song.
            val maxDuration = song?.durationMs ?: -1
            if (positionMs <= maxDuration) {
                this.positionMs = positionMs
                notifyPositionChanged()
            }
        }
    }

    /**
     * **Seek** to a [positionMs].
     * @param positionMs The position to seek to in millis.
     */
    fun seekTo(positionMs: Long) {
        synchronized(this) {
            this.positionMs = positionMs
            controller?.seekTo(positionMs)
            notifyPositionChanged()
        }
    }

    /** Rewind to the beginning of a song. */
    fun rewind() = seekTo(0)

    // --- PERSISTENCE FUNCTIONS ---

    /** Restore the state from the [database] */
    suspend fun restoreState(database: PlaybackStateDatabase) {
        val library = musicStore.library ?: return
        val start: Long
        val state: PlaybackStateDatabase.SavedState?

        logD("Getting state from DB")

        withContext(Dispatchers.IO) {
            start = System.currentTimeMillis()
            state = database.read(library)
        }

        logD("State read completed successfully in ${System.currentTimeMillis() - start}ms")

        synchronized(this) {
            if (state != null) {
                index = state.index
                parent = state.parent
                _queue = state.queue.toMutableList()
                repeatMode = state.repeatMode
                isShuffled = state.isShuffled

                notifyNewPlayback()
                seekTo(state.positionMs)
                notifyRepeatModeChanged()
                notifyShuffledChanged()
            }

            isInitialized = true
        }
    }

    /** Save the current state to the [database]. */
    suspend fun saveState(database: PlaybackStateDatabase) {
        logD("Saving state to DB")

        // Pack the entire state and save it to the database.
        withContext(Dispatchers.IO) {
            val start = System.currentTimeMillis()

            database.write(
                synchronized(this) {
                    PlaybackStateDatabase.SavedState(
                        index = index,
                        parent = parent,
                        queue = _queue,
                        positionMs = positionMs,
                        isShuffled = isShuffled,
                        repeatMode = repeatMode)
                })

            this@PlaybackStateManager.logD(
                "State save completed successfully in ${System.currentTimeMillis() - start}ms")
        }
    }

    // --- CALLBACKS ---

    private fun notifyIndexMoved() {
        controller?.loadSong(song)
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

        /** Called when the queue and/or index changed, but the song has not. */
        fun onQueueChanged(index: Int, queue: List<Song>) {}

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

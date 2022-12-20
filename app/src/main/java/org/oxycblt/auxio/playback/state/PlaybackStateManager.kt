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
import org.oxycblt.auxio.playback.state.PlaybackStateManager.Callback
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW

/**
 * Core playback controller class.
 *
 * Whereas other apps centralize the playback state around the MediaSession, Auxio does not, as
 * MediaSession is poorly designed. We use our own playback state system instead.
 *
 * This should ***NOT*** be used outside of the playback module.
 * - If you want to use the playback state in the UI, use
 * [org.oxycblt.auxio.playback.PlaybackViewModel] as it can withstand volatile UIs.
 * - If you want to use the playback state with the ExoPlayer instance or system-side things, use
 * [org.oxycblt.auxio.playback.system.PlaybackService].
 *
 * Internal consumers should usually use [Callback], however the component that manages the player
 * itself should instead operate as a [InternalPlayer].
 *
 * All access should be done with [PlaybackStateManager.getInstance].
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaybackStateManager private constructor() {
    private val musicStore = MusicStore.getInstance()
    private val callbacks = mutableListOf<Callback>()
    private var internalPlayer: InternalPlayer? = null

    /** The currently playing song. Null if there isn't one */
    val song
        get() = queue.getOrNull(index)

    /** The parent the queue is based on, null if all songs */
    var parent: MusicParent? = null
        private set
    private var _queue = mutableListOf<Song>()

    private val orderedQueue = listOf<Song>()
    private val shuffledQueue = listOf<Song>()

    /** The current queue determined by [parent] */
    val queue
        get() = _queue

    /** The current position in the queue */
    var index = -1
        private set

    /** The current state of the internal player. */
    var playerState = InternalPlayer.State.new(isPlaying = false, isAdvancing = false, 0)
        private set

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

    /** The current audio session ID of the internal player. Null if no internal player present. */
    val currentAudioSessionId: Int?
        get() = internalPlayer?.audioSessionId

    /** An action that is awaiting the internal player instance to consume it. */
    private var pendingAction: InternalPlayer.Action? = null

    /** Add a callback to this instance. Make sure to remove it when done. */
    @Synchronized
    fun addCallback(callback: Callback) {
        if (isInitialized) {
            callback.onNewPlayback(index, queue, parent)
            callback.onRepeatChanged(repeatMode)
            callback.onShuffledChanged(isShuffled)
            callback.onStateChanged(playerState)
        }

        callbacks.add(callback)
    }

    /** Remove a [Callback] bound to this instance. */
    @Synchronized
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    /** Register a [InternalPlayer] with this instance. */
    @Synchronized
    fun registerInternalPlayer(internalPlayer: InternalPlayer) {
        if (BuildConfig.DEBUG && this.internalPlayer != null) {
            logW("Internal player is already registered")
            return
        }

        if (isInitialized) {
            internalPlayer.loadSong(song, playerState.isPlaying)
            internalPlayer.seekTo(playerState.calculateElapsedPosition())
            requestAction(internalPlayer)
            synchronizeState(internalPlayer)
        }

        this.internalPlayer = internalPlayer
    }

    /** Unregister a [InternalPlayer] with this instance. */
    @Synchronized
    fun unregisterInternalPlayer(internalPlayer: InternalPlayer) {
        if (BuildConfig.DEBUG && this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        this.internalPlayer = null
    }

    // --- PLAYING FUNCTIONS ---

    /** Play a song from a parent that contains the song. */
    @Synchronized
    fun play(
        song: Song?,
        parent: MusicParent?,
        settings: Settings,
        shuffled: Boolean = settings.keepShuffle && isShuffled
    ) {
        val internalPlayer = internalPlayer ?: return
        val library = musicStore.library ?: return

        this.parent = parent
        _queue = (parent?.songs ?: library.songs).toMutableList()
        orderQueue(settings, shuffled, song)

        notifyNewPlayback()
        notifyShuffledChanged()

        internalPlayer.loadSong(this.song, true)

        isInitialized = true
    }

    // --- QUEUE FUNCTIONS ---

    /** Go to the next song, along with doing all the checks that entails. */
    @Synchronized
    fun next() {
        val internalPlayer = internalPlayer ?: return

        // Increment the index, if it cannot be incremented any further, then
        // repeat and pause/resume playback depending on the setting
        if (index < _queue.lastIndex) {
            gotoImpl(internalPlayer, index + 1, true)
        } else {
            gotoImpl(internalPlayer, 0, repeatMode == RepeatMode.ALL)
        }
    }

    /** Go to the previous song, doing any checks that are needed. */
    @Synchronized
    fun prev() {
        val internalPlayer = internalPlayer ?: return

        // If enabled, rewind before skipping back if the position is past 3 seconds [3000ms]
        if (internalPlayer.shouldRewindWithPrev) {
            rewind()
            changePlaying(true)
        } else {
            gotoImpl(internalPlayer, max(index - 1, 0), true)
        }
    }

    @Synchronized
    fun goto(index: Int) {
        val internalPlayer = internalPlayer ?: return
        gotoImpl(internalPlayer, index, true)
    }

    private fun gotoImpl(internalPlayer: InternalPlayer, idx: Int, play: Boolean) {
        index = idx
        notifyIndexMoved()
        internalPlayer.loadSong(song, play)
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
        val song = song ?: return
        orderQueue(settings, shuffled, song)
        notifyQueueReworked()
        notifyShuffledChanged()
    }

    private fun orderQueue(settings: Settings, shuffled: Boolean, keep: Song?) {
        val newIndex: Int

        if (shuffled) {
            _queue.shuffle()

            if (keep != null) {
                _queue.add(0, _queue.removeAt(_queue.indexOf(keep)))
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

            sort.songsInPlace(_queue)
            newIndex = keep?.let(_queue::indexOf) ?: 0
        }

        _queue = queue
        index = newIndex
        isShuffled = shuffled
    }

    // --- INTERNAL PLAYER FUNCTIONS ---

    @Synchronized
    fun synchronizeState(internalPlayer: InternalPlayer) {
        if (BuildConfig.DEBUG && this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        val newState = internalPlayer.makeState(song?.durationMs ?: 0)
        if (newState != playerState) {
            playerState = newState
            notifyStateChanged()
        }
    }

    @Synchronized
    fun startAction(action: InternalPlayer.Action) {
        val internalPlayer = internalPlayer
        if (internalPlayer == null || !internalPlayer.onAction(action)) {
            logD("Internal player not present or did not consume action, waiting")
            pendingAction = action
        }
    }

    /** Request the stored [InternalPlayer.Action] */
    @Synchronized
    fun requestAction(internalPlayer: InternalPlayer) {
        if (BuildConfig.DEBUG && this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        if (pendingAction?.let(internalPlayer::onAction) == true) {
            logD("Pending action consumed")
            pendingAction = null
        }
    }

    /** Change the current playing state. */
    fun changePlaying(isPlaying: Boolean) {
        internalPlayer?.changePlaying(isPlaying)
    }

    /**
     * **Seek** to a [positionMs].
     * @param positionMs The position to seek to in millis.
     */
    @Synchronized
    fun seekTo(positionMs: Long) {
        internalPlayer?.seekTo(positionMs)
    }

    /** Rewind to the beginning of a song. */
    fun rewind() = seekTo(0)

    // --- PERSISTENCE FUNCTIONS ---

    /** Restore the state from the [database]. Returns if a state was restored. */
    suspend fun restoreState(database: PlaybackStateDatabase, force: Boolean): Boolean {
        if (isInitialized && !force) {
            return false
        }

        val library = musicStore.library ?: return false
        val internalPlayer = internalPlayer ?: return false
        val state =
            try {
                withContext(Dispatchers.IO) { database.read(library) }
            } catch (e: Exception) {
                logE("Unable to restore playback state.")
                logE(e.stackTraceToString())
                return false
            }

        synchronized(this) {
            if (state != null && (!isInitialized || force)) {
                // Continuing playback while also possibly doing drastic state updates is
                // a bad idea, so pause.
                index = state.index
                parent = state.parent
                _queue = state.queue.toMutableList()
                repeatMode = state.repeatMode
                isShuffled = state.isShuffled

                notifyNewPlayback()
                notifyRepeatModeChanged()
                notifyShuffledChanged()

                internalPlayer.loadSong(song, false)
                internalPlayer.seekTo(state.positionMs)

                isInitialized = true

                return true
            } else {
                return false
            }
        }
    }

    /** Save the current state to the [database]. */
    suspend fun saveState(database: PlaybackStateDatabase): Boolean {
        logD("Saving state to DB")

        val state = synchronized(this) { makeStateImpl() }
        return try {
            withContext(Dispatchers.IO) { database.write(state) }
            true
        } catch (e: Exception) {
            logE("Unable to save playback state.")
            logE(e.stackTraceToString())
            false
        }
    }

    /** Wipe the current state. */
    suspend fun wipeState(database: PlaybackStateDatabase): Boolean {
        logD("Wiping state")

        return try {
            withContext(Dispatchers.IO) { database.write(null) }
            true
        } catch (e: Exception) {
            logE("Unable to wipe playback state.")
            logE(e.stackTraceToString())
            false
        }
    }

    /** Sanitize the state with [newLibrary]. */
    @Synchronized
    fun sanitize(newLibrary: MusicStore.Library) {
        if (!isInitialized) {
            logD("Not initialized, no need to sanitize")
            return
        }

        val internalPlayer = internalPlayer ?: return

        logD("Sanitizing state")

        // While we could just save and reload the state, we instead sanitize the state
        // at runtime for better performance (and to sidestep a co-routine on behalf of the caller).

        val oldSongUid = song?.uid
        val oldPosition = playerState.calculateElapsedPosition()

        parent =
            parent?.let {
                when (it) {
                    is Album -> newLibrary.sanitize(it)
                    is Artist -> newLibrary.sanitize(it)
                    is Genre -> newLibrary.sanitize(it)
                }
            }

        _queue = _queue.mapNotNullTo(mutableListOf()) { newLibrary.sanitize(it) }

        while (song?.uid != oldSongUid && index > -1) {
            index--
        }

        notifyNewPlayback()

        // Continuing playback while also possibly doing drastic state updates is
        // a bad idea, so pause.
        internalPlayer.loadSong(song, false)

        if (index > -1) {
            // Internal player may have reloaded the media item, re-seek to the previous position
            seekTo(oldPosition)
        }
    }

    private fun makeStateImpl() =
        PlaybackStateDatabase.SavedState(
            index = index,
            parent = parent,
            queue = _queue,
            positionMs = playerState.calculateElapsedPosition(),
            isShuffled = isShuffled,
            repeatMode = repeatMode)

    // --- CALLBACKS ---

    private fun notifyIndexMoved() {
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
        for (callback in callbacks) {
            callback.onNewPlayback(index, queue, parent)
        }
    }

    private fun notifyStateChanged() {
        for (callback in callbacks) {
            callback.onStateChanged(playerState)
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

        /** Called when the state of the internal player changes. */
        fun onStateChanged(state: InternalPlayer.State) {}

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

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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.library.Library
import org.oxycblt.auxio.playback.state.PlaybackStateManager.Listener
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * Core playback state controller class.
 *
 * Whereas other apps centralize the playback state around the MediaSession, Auxio does not, as
 * MediaSession is poorly designed. This class instead ful-fills this role.
 *
 * This should ***NOT*** be used outside of the playback module.
 * - If you want to use the playback state in the UI, use
 * [org.oxycblt.auxio.playback.PlaybackViewModel] as it can withstand volatile UIs.
 * - If you want to use the playback state with the ExoPlayer instance or system-side things, use
 * [org.oxycblt.auxio.playback.system.PlaybackService].
 *
 * Internal consumers should usually use [Listener], however the component that manages the player
 * itself should instead use [InternalPlayer].
 *
 * All access should be done with [PlaybackStateManager.getInstance].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaybackStateManager private constructor() {
    private val musicStore = MusicStore.getInstance()
    private val listeners = mutableListOf<Listener>()
    @Volatile private var internalPlayer: InternalPlayer? = null
    @Volatile private var pendingAction: InternalPlayer.Action? = null
    @Volatile private var isInitialized = false

    /** The current [Queue]. */
    val queue = Queue()
    /** The [MusicParent] currently being played. Null if playback is occurring from all songs. */
    @Volatile
    var parent: MusicParent? = null // FIXME: Parent is interpreted wrong when nothing is playing.
        private set

    /** The current [InternalPlayer] state. */
    @Volatile
    var playerState = InternalPlayer.State.from(isPlaying = false, isAdvancing = false, 0)
        private set
    /** The current [RepeatMode] */
    @Volatile
    var repeatMode = RepeatMode.NONE
        set(value) {
            field = value
            notifyRepeatModeChanged()
        }
    /**
     * The current audio session ID of the internal player. Null if [InternalPlayer] is unavailable.
     */
    val currentAudioSessionId: Int?
        get() = internalPlayer?.audioSessionId

    /**
     * Add a [Listener] to this instance. This can be used to receive changes in the playback state.
     * Will immediately invoke [Listener] methods to initialize the instance with the current state.
     * @param listener The [Listener] to add.
     * @see Listener
     */
    @Synchronized
    fun addListener(listener: Listener) {
        if (isInitialized) {
            listener.onNewPlayback(queue, parent)
            listener.onRepeatChanged(repeatMode)
            listener.onStateChanged(playerState)
        }

        listeners.add(listener)
    }

    /**
     * Remove a [Listener] from this instance, preventing it from receiving any further updates.
     * @param listener The [Listener] to remove. Does nothing if the [Listener] was never added in
     * the first place.
     * @see Listener
     */
    @Synchronized
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    /**
     * Register an [InternalPlayer] for this instance. This instance will handle translating the
     * current playback state into audio playback. There can be only one [InternalPlayer] at a time.
     * Will invoke [InternalPlayer] methods to initialize the instance with the current state.
     * @param internalPlayer The [InternalPlayer] to register. Will do nothing if already
     * registered.
     */
    @Synchronized
    fun registerInternalPlayer(internalPlayer: InternalPlayer) {
        if (this.internalPlayer != null) {
            logW("Internal player is already registered")
            return
        }

        if (isInitialized) {
            internalPlayer.loadSong(queue.currentSong, playerState.isPlaying)
            internalPlayer.seekTo(playerState.calculateElapsedPositionMs())
            // See if there's any action that has been queued.
            requestAction(internalPlayer)
            // Once initialized, try to synchronize with the player state it has created.
            synchronizeState(internalPlayer)
        }

        this.internalPlayer = internalPlayer
    }

    /**
     * Unregister the [InternalPlayer] from this instance, prevent it from recieving any further
     * commands.
     * @param internalPlayer The [InternalPlayer] to unregister. Must be the current
     * [InternalPlayer]. Does nothing if invoked by another [InternalPlayer] implementation.
     */
    @Synchronized
    fun unregisterInternalPlayer(internalPlayer: InternalPlayer) {
        if (this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        this.internalPlayer = null
    }

    // --- PLAYING FUNCTIONS ---

    /**
     * Start new playback.
     * @param song A particular [Song] to play, or null to play the first [Song] in the new queue.
     * @param queue The queue of [Song]s to play from.
     * @param parent The [MusicParent] to play from, or null if to play from an non-specific
     * collection of "All [Song]s".
     * @param shuffled Whether to shuffle or not.
     */
    @Synchronized
    fun play(song: Song?, parent: MusicParent?, queue: List<Song>, shuffled: Boolean) {
        val internalPlayer = internalPlayer ?: return
        // Set up parent and queue
        this.parent = parent
        this.queue.start(song, queue, shuffled)
        // Notify components of changes
        notifyNewPlayback()
        internalPlayer.loadSong(this.queue.currentSong, true)
        // Played something, so we are initialized now
        isInitialized = true
    }

    // --- QUEUE FUNCTIONS ---

    /**
     * Go to the next [Song] in the queue. Will go to the first [Song] in the queue if there is no
     * [Song] ahead to skip to.
     */
    @Synchronized
    fun next() {
        val internalPlayer = internalPlayer ?: return
        var play = true
        if (!queue.goto(queue.index + 1)) {
            queue.goto(0)
            play = false
        }
        notifyIndexMoved()
        internalPlayer.loadSong(queue.currentSong, play)
    }

    /**
     * Go to the previous [Song] in the queue. Will rewind if there are no previous [Song]s to skip
     * to, or if configured to do so.
     */
    @Synchronized
    fun prev() {
        val internalPlayer = internalPlayer ?: return

        // If enabled, rewind before skipping back if the position is past 3 seconds [3000ms]
        if (internalPlayer.shouldRewindWithPrev) {
            rewind()
            setPlaying(true)
        } else {
            if (!queue.goto(queue.index - 1)) {
                queue.goto(0)
            }
            notifyIndexMoved()
            internalPlayer.loadSong(queue.currentSong, true)
        }
    }

    /**
     * Play a [Song] at the given position in the queue.
     * @param index The position of the [Song] in the queue to start playing.
     */
    @Synchronized
    fun goto(index: Int) {
        val internalPlayer = internalPlayer ?: return
        if (queue.goto(index)) {
            notifyIndexMoved()
            internalPlayer.loadSong(queue.currentSong, true)
        }
    }

    /**
     * Add a [Song] to the top of the queue.
     * @param song The [Song] to add.
     */
    @Synchronized fun playNext(song: Song) = playNext(listOf(song))

    /**
     * Add [Song]s to the top of the queue.
     * @param songs The [Song]s to add.
     */
    @Synchronized
    fun playNext(songs: List<Song>) {
        val internalPlayer = internalPlayer ?: return
        when (queue.playNext(songs)) {
            Queue.ChangeResult.MAPPING -> notifyQueueChanged(Queue.ChangeResult.MAPPING)
            Queue.ChangeResult.SONG -> {
                // Enqueueing actually started a new playback session from all songs.
                parent = null
                internalPlayer.loadSong(queue.currentSong, true)
                notifyNewPlayback()
            }
            Queue.ChangeResult.INDEX -> error("Unreachable")
        }
    }

    /**
     * Add a [Song] to the end of the queue.
     * @param song The [Song] to add.
     */
    @Synchronized fun addToQueue(song: Song) = addToQueue(listOf(song))

    /**
     * Add [Song]s to the end of the queue.
     * @param songs The [Song]s to add.
     */
    @Synchronized
    fun addToQueue(songs: List<Song>) {
        val internalPlayer = internalPlayer ?: return
        when (queue.addToQueue(songs)) {
            Queue.ChangeResult.MAPPING -> notifyQueueChanged(Queue.ChangeResult.MAPPING)
            Queue.ChangeResult.SONG -> {
                // Enqueueing actually started a new playback session from all songs.
                parent = null
                internalPlayer.loadSong(queue.currentSong, true)
                notifyNewPlayback()
            }
            Queue.ChangeResult.INDEX -> error("Unreachable")
        }
    }

    /**
     * Move a [Song] in the queue.
     * @param src The position of the [Song] to move in the queue.
     * @param dst The destination position in the queue.
     */
    @Synchronized
    fun moveQueueItem(src: Int, dst: Int) {
        logD("Moving item $src to position $dst")
        notifyQueueChanged(queue.move(src, dst))
    }

    /**
     * Remove a [Song] from the queue.
     * @param at The position of the [Song] to remove in the queue.
     */
    @Synchronized
    fun removeQueueItem(at: Int) {
        val internalPlayer = internalPlayer ?: return
        logD("Removing item at $at")
        val change = queue.remove(at)
        if (change == Queue.ChangeResult.SONG) {
            internalPlayer.loadSong(queue.currentSong, playerState.isPlaying)
        }
        notifyQueueChanged(change)
    }

    /**
     * (Re)shuffle or (Re)order this instance.
     * @param shuffled Whether to shuffle the queue or not.
     */
    @Synchronized
    fun reorder(shuffled: Boolean) {
        queue.reorder(shuffled)
        notifyQueueReordered()
    }

    // --- INTERNAL PLAYER FUNCTIONS ---

    /**
     * Synchronize the state of this instance with the current [InternalPlayer].
     * @param internalPlayer The [InternalPlayer] to synchronize with. Must be the current
     * [InternalPlayer]. Does nothing if invoked by another [InternalPlayer] implementation.
     */
    @Synchronized
    fun synchronizeState(internalPlayer: InternalPlayer) {
        if (BuildConfig.DEBUG && this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        val newState = internalPlayer.getState(queue.currentSong?.durationMs ?: 0)
        if (newState != playerState) {
            playerState = newState
            notifyStateChanged()
        }
    }

    /**
     * Start a [InternalPlayer.Action] for the current [InternalPlayer] to handle eventually.
     * @param action The [InternalPlayer.Action] to perform.
     */
    @Synchronized
    fun startAction(action: InternalPlayer.Action) {
        val internalPlayer = internalPlayer
        if (internalPlayer == null || !internalPlayer.performAction(action)) {
            logD("Internal player not present or did not consume action, waiting")
            pendingAction = action
        }
    }

    /**
     * Request that the pending [InternalPlayer.Action] (if any) be passed to the given
     * [InternalPlayer].
     * @param internalPlayer The [InternalPlayer] to synchronize with. Must be the current
     * [InternalPlayer]. Does nothing if invoked by another [InternalPlayer] implementation.
     */
    @Synchronized
    fun requestAction(internalPlayer: InternalPlayer) {
        if (BuildConfig.DEBUG && this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        if (pendingAction?.let(internalPlayer::performAction) == true) {
            logD("Pending action consumed")
            pendingAction = null
        }
    }

    /**
     * Update whether playback is ongoing or not.
     * @param isPlaying Whether playback is ongoing or not.
     */
    fun setPlaying(isPlaying: Boolean) {
        internalPlayer?.setPlaying(isPlaying)
    }

    /**
     * Seek to the given position in the currently playing [Song].
     * @param positionMs The position to seek to, in milliseconds.
     */
    @Synchronized
    fun seekTo(positionMs: Long) {
        internalPlayer?.seekTo(positionMs)
    }

    /** Rewind to the beginning of the currently playing [Song]. */
    fun rewind() = seekTo(0)

    // --- PERSISTENCE FUNCTIONS ---

    /**
     * Restore the previously saved state (if any) and apply it to the playback state.
     * @param database The [PlaybackStateDatabase] to load from.
     * @param force Whether to do a restore regardless of any prior playback state.
     * @return If the state was restored, false otherwise.
     */
    suspend fun restoreState(database: PlaybackStateDatabase, force: Boolean): Boolean {
        if (isInitialized && !force) {
            // Already initialized and not forcing a restore, nothing to do.
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

        // Translate the state we have just read into a usable playback state for this
        // instance.
        return synchronized(this) {
            // State could have changed while we were loading, so check if we were initialized
            // now before applying the state.
            if (state != null && (!isInitialized || force)) {
                parent = state.parent
                queue.applySavedState(state.queueState)
                repeatMode = state.repeatMode
                notifyNewPlayback()
                notifyRepeatModeChanged()
                // Continuing playback after drastic state updates is a bad idea, so pause.
                internalPlayer.loadSong(queue.currentSong, false)
                internalPlayer.seekTo(state.positionMs)
                isInitialized = true
                true
            } else {
                false
            }
        }
    }

    /**
     * Save the current state.
     * @param database The [PlaybackStateDatabase] to save the state to.
     * @return If state was saved, false otherwise.
     */
    suspend fun saveState(database: PlaybackStateDatabase): Boolean {
        logD("Saving state to DB")
        // Create the saved state from the current playback state.
        val state =
            synchronized(this) {
                queue.toSavedState()?.let {
                    PlaybackStateDatabase.SavedState(
                        parent = parent,
                        queueState = it,
                        positionMs = playerState.calculateElapsedPositionMs(),
                        repeatMode = repeatMode)
                }
            }
        return try {
            withContext(Dispatchers.IO) { database.write(state) }
            true
        } catch (e: Exception) {
            logE("Unable to save playback state.")
            logE(e.stackTraceToString())
            false
        }
    }

    /**
     * Clear the current state.
     * @param database The [PlaybackStateDatabase] to clear te state from
     * @return If the state was cleared, false otherwise.
     */
    suspend fun wipeState(database: PlaybackStateDatabase) =
        try {
            logD("Wiping state")
            withContext(Dispatchers.IO) { database.write(null) }
            true
        } catch (e: Exception) {
            logE("Unable to wipe playback state.")
            logE(e.stackTraceToString())
            false
        }

    /**
     * Update the playback state to align with a new [Library].
     * @param newLibrary The new [Library] that was recently loaded.
     */
    @Synchronized
    fun sanitize(newLibrary: Library) {
        if (!isInitialized) {
            // Nothing playing, nothing to do.
            logD("Not initialized, no need to sanitize")
            return
        }

        val internalPlayer = internalPlayer ?: return

        logD("Sanitizing state")

        // While we could just save and reload the state, we instead sanitize the state
        // at runtime for better performance (and to sidestep a co-routine on behalf of the caller).

        // Sanitize parent
        parent =
            parent?.let {
                when (it) {
                    is Album -> newLibrary.sanitize(it)
                    is Artist -> newLibrary.sanitize(it)
                    is Genre -> newLibrary.sanitize(it)
                }
            }

        // Sanitize the queue.
        queue.toSavedState()?.let { state ->
            queue.applySavedState(state.remap { newLibrary.sanitize(unlikelyToBeNull(it)) })
        }

        notifyNewPlayback()

        val oldPosition = playerState.calculateElapsedPositionMs()
        // Continuing playback while also possibly doing drastic state updates is
        // a bad idea, so pause.
        internalPlayer.loadSong(queue.currentSong, false)
        if (queue.currentSong != null) {
            // Internal player may have reloaded the media item, re-seek to the previous position
            seekTo(oldPosition)
        }
    }

    // --- CALLBACKS ---

    private fun notifyIndexMoved() {
        for (callback in listeners) {
            callback.onIndexMoved(queue)
        }
    }

    private fun notifyQueueChanged(change: Queue.ChangeResult) {
        for (callback in listeners) {
            callback.onQueueChanged(queue, change)
        }
    }

    private fun notifyQueueReordered() {
        for (callback in listeners) {
            callback.onQueueReordered(queue)
        }
    }

    private fun notifyNewPlayback() {
        for (callback in listeners) {
            callback.onNewPlayback(queue, parent)
        }
    }

    private fun notifyStateChanged() {
        for (callback in listeners) {
            callback.onStateChanged(playerState)
        }
    }

    private fun notifyRepeatModeChanged() {
        for (callback in listeners) {
            callback.onRepeatChanged(repeatMode)
        }
    }

    /**
     * The interface for receiving updates from [PlaybackStateManager]. Add the listener to
     * [PlaybackStateManager] using [addListener], remove them on destruction with [removeListener].
     */
    interface Listener {
        /**
         * Called when the position of the currently playing item has changed, changing the current
         * [Song], but no other queue attribute has changed.
         * @param queue The new [Queue].
         */
        fun onIndexMoved(queue: Queue) {}

        /**
         * Called when the [Queue] changed in a manner outlined by the given [Queue.ChangeResult].
         * @param queue The new [Queue].
         * @param change The type of [Queue.ChangeResult] that occurred.
         */
        fun onQueueChanged(queue: Queue, change: Queue.ChangeResult) {}

        /**
         * Called when the [Queue] has changed in a non-trivial manner (such as re-shuffling), but
         * the currently playing [Song] has not.
         * @param queue The new [Queue].
         */
        fun onQueueReordered(queue: Queue) {}

        /**
         * Called when a new playback configuration was created.
         * @param queue The new [Queue].
         * @param parent The new [MusicParent] being played from, or null if playing from all songs.
         */
        fun onNewPlayback(queue: Queue, parent: MusicParent?) {}

        /**
         * Called when the state of the [InternalPlayer] changes.
         * @param state The new state of the [InternalPlayer].
         */
        fun onStateChanged(state: InternalPlayer.State) {}

        /**
         * Called when the [RepeatMode] changes.
         * @param repeatMode The new [RepeatMode].
         */
        fun onRepeatChanged(repeatMode: RepeatMode) {}
    }

    companion object {
        @Volatile private var INSTANCE: PlaybackStateManager? = null

        /**
         * Get a singleton instance.
         * @return The (possibly newly-created) singleton instance.
         */
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

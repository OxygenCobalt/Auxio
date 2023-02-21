/*
 * Copyright (c) 2023 Auxio Project
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

import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.queue.EditableQueue
import org.oxycblt.auxio.playback.queue.Queue
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * Core playback state controller class.
 *
 * Whereas other apps centralize the playback state around the MediaSession, Auxio does not, as
 * MediaSession is poorly designed. This class instead ful-fills this role.
 *
 * This should ***NOT*** be used outside of the playback module.
 * - If you want to use the playback state in the UI, use PlaybackViewModel as it can withstand
 * volatile UIs.
 * - If you want to use the playback state with the ExoPlayer instance or system-side things, use
 * PlaybackService.
 *
 * Internal consumers should usually use [Listener], however the component that manages the player
 * itself should instead use [InternalPlayer].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface PlaybackStateManager {
    /** The current [Queue]. */
    val queue: Queue
    /** The [MusicParent] currently being played. Null if playback is occurring from all songs. */
    val parent: MusicParent?
    /** The current [InternalPlayer] state. */
    val playerState: InternalPlayer.State
    /** The current [RepeatMode] */
    var repeatMode: RepeatMode
    /** The audio session ID of the internal player. Null if no internal player exists. */
    val currentAudioSessionId: Int?

    /**
     * Add a [Listener] to this instance. This can be used to receive changes in the playback state.
     * Will immediately invoke [Listener] methods to initialize the instance with the current state.
     * @param listener The [Listener] to add.
     * @see Listener
     */
    fun addListener(listener: Listener)

    /**
     * Remove a [Listener] from this instance, preventing it from receiving any further updates.
     * @param listener The [Listener] to remove. Does nothing if the [Listener] was never added in
     * the first place.
     * @see Listener
     */
    fun removeListener(listener: Listener)

    /**
     * Register an [InternalPlayer] for this instance. This instance will handle translating the
     * current playback state into audio playback. There can be only one [InternalPlayer] at a time.
     * Will invoke [InternalPlayer] methods to initialize the instance with the current state.
     * @param internalPlayer The [InternalPlayer] to register. Will do nothing if already
     * registered.
     */
    fun registerInternalPlayer(internalPlayer: InternalPlayer)

    /**
     * Unregister the [InternalPlayer] from this instance, prevent it from receiving any further
     * commands.
     * @param internalPlayer The [InternalPlayer] to unregister. Must be the current
     * [InternalPlayer]. Does nothing if invoked by another [InternalPlayer] implementation.
     */
    fun unregisterInternalPlayer(internalPlayer: InternalPlayer)

    /**
     * Start new playback.
     * @param song A particular [Song] to play, or null to play the first [Song] in the new queue.
     * @param queue The queue of [Song]s to play from.
     * @param parent The [MusicParent] to play from, or null if to play from an non-specific
     * collection of "All [Song]s".
     * @param shuffled Whether to shuffle or not.
     */
    fun play(song: Song?, parent: MusicParent?, queue: List<Song>, shuffled: Boolean)

    /**
     * Go to the next [Song] in the queue. Will go to the first [Song] in the queue if there is no
     * [Song] ahead to skip to.
     */
    fun next()

    /**
     * Go to the previous [Song] in the queue. Will rewind if there are no previous [Song]s to skip
     * to, or if configured to do so.
     */
    fun prev()

    /**
     * Play a [Song] at the given position in the queue.
     * @param index The position of the [Song] in the queue to start playing.
     */
    fun goto(index: Int)

    /**
     * Add [Song]s to the top of the queue.
     * @param songs The [Song]s to add.
     */
    fun playNext(songs: List<Song>)

    /**
     * Add a [Song] to the top of the queue.
     * @param song The [Song] to add.
     */
    fun playNext(song: Song) = playNext(listOf(song))

    /**
     * Add [Song]s to the end of the queue.
     * @param songs The [Song]s to add.
     */
    fun addToQueue(songs: List<Song>)

    /**
     * Add a [Song] to the end of the queue.
     * @param song The [Song] to add.
     */
    fun addToQueue(song: Song) = addToQueue(listOf(song))

    /**
     * Move a [Song] in the queue.
     * @param src The position of the [Song] to move in the queue.
     * @param dst The destination position in the queue.
     */
    fun moveQueueItem(src: Int, dst: Int)

    /**
     * Remove a [Song] from the queue.
     * @param at The position of the [Song] to remove in the queue.
     */
    fun removeQueueItem(at: Int)

    /**
     * (Re)shuffle or (Re)order this instance.
     * @param shuffled Whether to shuffle the queue or not.
     */
    fun reorder(shuffled: Boolean)

    /**
     * Synchronize the state of this instance with the current [InternalPlayer].
     * @param internalPlayer The [InternalPlayer] to synchronize with. Must be the current
     * [InternalPlayer]. Does nothing if invoked by another [InternalPlayer] implementation.
     */
    fun synchronizeState(internalPlayer: InternalPlayer)

    /**
     * Start a [InternalPlayer.Action] for the current [InternalPlayer] to handle eventually.
     * @param action The [InternalPlayer.Action] to perform.
     */
    fun startAction(action: InternalPlayer.Action)

    /**
     * Request that the pending [InternalPlayer.Action] (if any) be passed to the given
     * [InternalPlayer].
     * @param internalPlayer The [InternalPlayer] to synchronize with. Must be the current
     * [InternalPlayer]. Does nothing if invoked by another [InternalPlayer] implementation.
     */
    fun requestAction(internalPlayer: InternalPlayer)

    /**
     * Update whether playback is ongoing or not.
     * @param isPlaying Whether playback is ongoing or not.
     */
    fun setPlaying(isPlaying: Boolean)

    /**
     * Seek to the given position in the currently playing [Song].
     * @param positionMs The position to seek to, in milliseconds.
     */
    fun seekTo(positionMs: Long)

    /** Rewind to the beginning of the currently playing [Song]. */
    fun rewind() = seekTo(0)

    /**
     * Converts the current state of this instance into a [SavedState].
     * @return An immutable [SavedState] that is analogous to the current state, or null if nothing
     * is currently playing.
     */
    fun toSavedState(): SavedState?

    /**
     * Restores this instance from the given [SavedState].
     * @param savedState The [SavedState] to restore from.
     * @param destructive Whether to disregard the prior playback state and overwrite it with this
     * [SavedState].
     */
    fun applySavedState(savedState: SavedState, destructive: Boolean)

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

    /**
     * A condensed representation of the playback state that can be persisted.
     * @param parent The [MusicParent] item currently being played from.
     * @param queueState The [Queue.SavedState]
     * @param positionMs The current position in the currently played song, in ms
     * @param repeatMode The current [RepeatMode].
     */
    data class SavedState(
        val parent: MusicParent?,
        val queueState: Queue.SavedState,
        val positionMs: Long,
        val repeatMode: RepeatMode,
    )
}

class PlaybackStateManagerImpl @Inject constructor() : PlaybackStateManager {
    private val listeners = mutableListOf<PlaybackStateManager.Listener>()
    @Volatile private var internalPlayer: InternalPlayer? = null
    @Volatile private var pendingAction: InternalPlayer.Action? = null
    @Volatile private var isInitialized = false

    override val queue = EditableQueue()
    @Volatile
    override var parent: MusicParent? =
        null // FIXME: Parent is interpreted wrong when nothing is playing.
        private set
    @Volatile
    override var playerState = InternalPlayer.State.from(isPlaying = false, isAdvancing = false, 0)
        private set
    @Volatile
    override var repeatMode = RepeatMode.NONE
        set(value) {
            field = value
            notifyRepeatModeChanged()
        }
    override val currentAudioSessionId: Int?
        get() = internalPlayer?.audioSessionId

    @Synchronized
    override fun addListener(listener: PlaybackStateManager.Listener) {
        if (isInitialized) {
            listener.onNewPlayback(queue, parent)
            listener.onRepeatChanged(repeatMode)
            listener.onStateChanged(playerState)
        }

        listeners.add(listener)
    }

    @Synchronized
    override fun removeListener(listener: PlaybackStateManager.Listener) {
        listeners.remove(listener)
    }

    @Synchronized
    override fun registerInternalPlayer(internalPlayer: InternalPlayer) {
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

    @Synchronized
    override fun unregisterInternalPlayer(internalPlayer: InternalPlayer) {
        if (this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        this.internalPlayer = null
    }

    // --- PLAYING FUNCTIONS ---

    @Synchronized
    override fun play(song: Song?, parent: MusicParent?, queue: List<Song>, shuffled: Boolean) {
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

    @Synchronized
    override fun next() {
        val internalPlayer = internalPlayer ?: return
        var play = true
        if (!queue.goto(queue.index + 1)) {
            queue.goto(0)
            play = repeatMode == RepeatMode.ALL
        }
        notifyIndexMoved()
        internalPlayer.loadSong(queue.currentSong, play)
    }

    @Synchronized
    override fun prev() {
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

    @Synchronized
    override fun goto(index: Int) {
        val internalPlayer = internalPlayer ?: return
        if (queue.goto(index)) {
            notifyIndexMoved()
            internalPlayer.loadSong(queue.currentSong, true)
        }
    }

    @Synchronized
    override fun playNext(songs: List<Song>) {
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

    @Synchronized
    override fun addToQueue(songs: List<Song>) {
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

    @Synchronized
    override fun moveQueueItem(src: Int, dst: Int) {
        logD("Moving item $src to position $dst")
        notifyQueueChanged(queue.move(src, dst))
    }

    @Synchronized
    override fun removeQueueItem(at: Int) {
        val internalPlayer = internalPlayer ?: return
        logD("Removing item at $at")
        val change = queue.remove(at)
        if (change == Queue.ChangeResult.SONG) {
            internalPlayer.loadSong(queue.currentSong, playerState.isPlaying)
        }
        notifyQueueChanged(change)
    }

    @Synchronized
    override fun reorder(shuffled: Boolean) {
        queue.reorder(shuffled)
        notifyQueueReordered()
    }

    // --- INTERNAL PLAYER FUNCTIONS ---

    @Synchronized
    override fun synchronizeState(internalPlayer: InternalPlayer) {
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

    @Synchronized
    override fun startAction(action: InternalPlayer.Action) {
        val internalPlayer = internalPlayer
        if (internalPlayer == null || !internalPlayer.performAction(action)) {
            logD("Internal player not present or did not consume action, waiting")
            pendingAction = action
        }
    }

    @Synchronized
    override fun requestAction(internalPlayer: InternalPlayer) {
        if (BuildConfig.DEBUG && this.internalPlayer !== internalPlayer) {
            logW("Given internal player did not match current internal player")
            return
        }

        if (pendingAction?.let(internalPlayer::performAction) == true) {
            logD("Pending action consumed")
            pendingAction = null
        }
    }

    @Synchronized
    override fun setPlaying(isPlaying: Boolean) {
        internalPlayer?.setPlaying(isPlaying)
    }

    @Synchronized
    override fun seekTo(positionMs: Long) {
        internalPlayer?.seekTo(positionMs)
    }

    // --- PERSISTENCE FUNCTIONS ---

    @Synchronized
    override fun toSavedState() =
        queue.toSavedState()?.let {
            PlaybackStateManager.SavedState(
                parent = parent,
                queueState = it,
                positionMs = playerState.calculateElapsedPositionMs(),
                repeatMode = repeatMode)
        }

    @Synchronized
    override fun applySavedState(
        savedState: PlaybackStateManager.SavedState,
        destructive: Boolean
    ) {
        if (isInitialized && !destructive) {
            return
        }
        val internalPlayer = internalPlayer ?: return
        logD("Restoring state $savedState")

        parent = savedState.parent
        queue.applySavedState(savedState.queueState)
        repeatMode = savedState.repeatMode
        notifyNewPlayback()

        // Continuing playback while also possibly doing drastic state updates is
        // a bad idea, so pause.
        internalPlayer.loadSong(queue.currentSong, false)
        if (queue.currentSong != null) {
            // Internal player may have reloaded the media item, re-seek to the previous position
            seekTo(savedState.positionMs)
        }
        isInitialized = true
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
}

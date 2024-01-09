/*
 * Copyright (c) 2023 Auxio Project
 * PlaybackStateManager.kt is part of Auxio.
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
 *   volatile UIs.
 * - If you want to use the playback state with the ExoPlayer instance or system-side things, use
 *   PlaybackService.
 *
 * Internal consumers should usually use [Listener], however the component that manages the player
 * itself should instead use [PlaybackStateHolder].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface PlaybackStateManager {
    /** The current [Progression] state. */
    val progression: Progression

    val repeatMode: RepeatMode

    val parent: MusicParent?

    val currentSong: Song?

    val queue: List<Song>

    val index: Int

    val isShuffled: Boolean

    /** The audio session ID of the internal player. Null if no internal player exists. */
    val currentAudioSessionId: Int?

    /**
     * Add a [Listener] to this instance. This can be used to receive changes in the playback state.
     * Will immediately invoke [Listener] methods to initialize the instance with the current state.
     *
     * @param listener The [Listener] to add.
     * @see Listener
     */
    fun addListener(listener: Listener)

    /**
     * Remove a [Listener] from this instance, preventing it from receiving any further updates.
     *
     * @param listener The [Listener] to remove. Does nothing if the [Listener] was never added in
     *   the first place.
     * @see Listener
     */
    fun removeListener(listener: Listener)

    /**
     * Register an [PlaybackStateHolder] for this instance. This instance will handle translating
     * the current playback state into audio playback. There can be only one [PlaybackStateHolder]
     * at a time. Will invoke [PlaybackStateHolder] methods to initialize the instance with the
     * current state.
     *
     * @param stateHolder The [PlaybackStateHolder] to register. Will do nothing if already
     *   registered.
     */
    fun registerStateHolder(stateHolder: PlaybackStateHolder)

    /**
     * Unregister the [PlaybackStateHolder] from this instance, prevent it from receiving any
     * further commands.
     *
     * @param stateHolder The [PlaybackStateHolder] to unregister. Must be the current
     *   [PlaybackStateHolder]. Does nothing if invoked by another [PlaybackStateHolder]
     *   implementation.
     */
    fun unregisterStateHolder(stateHolder: PlaybackStateHolder)

    /**
     * Start new playback.
     *
     * @param song A particular [Song] to play, or null to play the first [Song] in the new queue.
     * @param queue The queue of [Song]s to play from.
     * @param parent The [MusicParent] to play from, or null if to play from an non-specific
     *   collection of "All [Song]s".
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
     *
     * @param index The position of the [Song] in the queue to start playing.
     */
    fun goto(index: Int)

    /**
     * Add [Song]s to the top of the queue.
     *
     * @param songs The [Song]s to add.
     */
    fun playNext(songs: List<Song>)

    /**
     * Add a [Song] to the top of the queue.
     *
     * @param song The [Song] to add.
     */
    fun playNext(song: Song) = playNext(listOf(song))

    /**
     * Add [Song]s to the end of the queue.
     *
     * @param songs The [Song]s to add.
     */
    fun addToQueue(songs: List<Song>)

    /**
     * Add a [Song] to the end of the queue.
     *
     * @param song The [Song] to add.
     */
    fun addToQueue(song: Song) = addToQueue(listOf(song))

    /**
     * Move a [Song] in the queue.
     *
     * @param src The position of the [Song] to move in the queue.
     * @param dst The destination position in the queue.
     */
    fun moveQueueItem(src: Int, dst: Int)

    /**
     * Remove a [Song] from the queue.
     *
     * @param at The position of the [Song] to remove in the queue.
     */
    fun removeQueueItem(at: Int)

    /**
     * (Re)shuffle or (Re)order this instance.
     *
     * @param shuffled Whether to shuffle the queue or not.
     */
    fun shuffled(shuffled: Boolean)

    fun dispatchEvent(stateHolder: PlaybackStateHolder, event: StateEvent)

    /**
     * Start a [DeferredPlayback] for the current [PlaybackStateHolder] to handle eventually.
     *
     * @param action The [DeferredPlayback] to perform.
     */
    fun playDeferred(action: DeferredPlayback)

    /**
     * Request that the pending [PlaybackStateHolder.Action] (if any) be passed to the given
     * [PlaybackStateHolder].
     *
     * @param stateHolder The [PlaybackStateHolder] to synchronize with. Must be the current
     *   [PlaybackStateHolder]. Does nothing if invoked by another [PlaybackStateHolder]
     *   implementation.
     */
    fun requestAction(stateHolder: PlaybackStateHolder)

    /**
     * Update whether playback is ongoing or not.
     *
     * @param isPlaying Whether playback is ongoing or not.
     */
    fun playing(isPlaying: Boolean)

    fun repeatMode(repeatMode: RepeatMode)

    /**
     * Seek to the given position in the currently playing [Song].
     *
     * @param positionMs The position to seek to, in milliseconds.
     */
    fun seekTo(positionMs: Long)

    /** Rewind to the beginning of the currently playing [Song]. */
    fun rewind() = seekTo(0)

    /**
     * Converts the current state of this instance into a [SavedState].
     *
     * @return An immutable [SavedState] that is analogous to the current state, or null if nothing
     *   is currently playing.
     */
    fun toSavedState(): SavedState?

    /**
     * Restores this instance from the given [SavedState].
     *
     * @param savedState The [SavedState] to restore from.
     * @param destructive Whether to disregard the prior playback state and overwrite it with this
     *   [SavedState].
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
         */
        fun onIndexMoved(index: Int) {}

        /**
         * Called when the [Queue] changed in a manner outlined by the given [Queue.Change].
         *
         * @param queue The new [Queue].
         * @param change The type of [Queue.Change] that occurred.
         */
        fun onQueueChanged(queue: List<Song>, index: Int, change: QueueChange) {}

        /**
         * Called when the [Queue] has changed in a non-trivial manner (such as re-shuffling), but
         * the currently playing [Song] has not.
         *
         * @param queue The new [Queue].
         */
        fun onQueueReordered(queue: List<Song>, index: Int, isShuffled: Boolean) {}

        /**
         * Called when a new playback configuration was created.
         *
         * @param queue The new [Queue].
         * @param parent The new [MusicParent] being played from, or null if playing from all songs.
         */
        fun onNewPlayback(
            parent: MusicParent?,
            queue: List<Song>,
            index: Int,
            isShuffled: Boolean
        ) {}

        /**
         * Called when the state of the [InternalPlayer] changes.
         *
         * @param progression The new state of the [InternalPlayer].
         */
        fun onProgressionChanged(progression: Progression) {}

        /**
         * Called when the [RepeatMode] changes.
         *
         * @param repeatMode The new [RepeatMode].
         */
        fun onRepeatModeChanged(repeatMode: RepeatMode) {}
    }

    /**
     * A condensed representation of the playback state that can be persisted.
     *
     * @param parent The [MusicParent] item currently being played from.
     * @param queueState The [SavedQueue]
     * @param positionMs The current position in the currently played song, in ms
     * @param repeatMode The current [RepeatMode].
     */
    data class SavedState(
        val parent: MusicParent?,
        val queueState: SavedQueue,
        val positionMs: Long,
        val repeatMode: RepeatMode,
    )
}

class PlaybackStateManagerImpl @Inject constructor() : PlaybackStateManager {
    private data class StateMirror(
        val progression: Progression,
        val repeatMode: RepeatMode,
        val parent: MusicParent?,
        val queue: List<Song>,
        val index: Int,
        val isShuffled: Boolean,
    )

    private val listeners = mutableListOf<PlaybackStateManager.Listener>()

    @Volatile
    private var stateMirror =
        StateMirror(
            progression = Progression.nil(),
            repeatMode = RepeatMode.NONE,
            parent = null,
            queue = emptyList(),
            index = -1,
            isShuffled = false,
        )
    @Volatile private var stateHolder: PlaybackStateHolder? = null
    @Volatile private var pendingDeferredPlayback: DeferredPlayback? = null
    @Volatile private var isInitialized = false

    override val progression
        get() = stateMirror.progression

    override val repeatMode
        get() = stateMirror.repeatMode

    override val parent
        get() = stateMirror.parent

    override val currentSong
        get() = stateMirror.queue.getOrNull(stateMirror.index)

    override val queue
        get() = stateMirror.queue

    override val index
        get() = stateMirror.index

    override val isShuffled
        get() = stateMirror.isShuffled

    override val currentAudioSessionId: Int?
        get() = stateHolder?.audioSessionId

    @Synchronized
    override fun addListener(listener: PlaybackStateManager.Listener) {
        logD("Adding $listener to listeners")
        listeners.add(listener)

        if (isInitialized) {
            logD("Sending initial state to $listener")
            listener.onNewPlayback(
                stateMirror.parent, stateMirror.queue, stateMirror.index, stateMirror.isShuffled)
            listener.onProgressionChanged(stateMirror.progression)
            listener.onRepeatModeChanged(stateMirror.repeatMode)
        }
    }

    @Synchronized
    override fun removeListener(listener: PlaybackStateManager.Listener) {
        logD("Removing $listener from listeners")
        if (!listeners.remove(listener)) {
            logW("Listener $listener was not added prior, cannot remove")
        }
    }

    @Synchronized
    override fun registerStateHolder(stateHolder: PlaybackStateHolder) {
        if (this.stateHolder != null) {
            logW("Internal player is already registered")
            return
        }

        this.stateHolder = stateHolder
    }

    @Synchronized
    override fun unregisterStateHolder(stateHolder: PlaybackStateHolder) {
        if (this.stateHolder !== stateHolder) {
            logW("Given internal player did not match current internal player")
            return
        }

        logD("Unregistering internal player $stateHolder")

        this.stateHolder = null
    }

    // --- PLAYING FUNCTIONS ---

    @Synchronized
    override fun play(song: Song?, parent: MusicParent?, queue: List<Song>, shuffled: Boolean) {
        val stateHolder = stateHolder ?: return
        logD("Playing $song from $parent in ${queue.size}-song queue [shuffled=$shuffled]")
        // Played something, so we are initialized now
        isInitialized = true
        stateHolder.newPlayback(queue, song, parent, shuffled, true)
    }

    // --- QUEUE FUNCTIONS ---

    @Synchronized
    override fun next() {
        val stateHolder = stateHolder ?: return
        logD("Going to next song")
        stateHolder.next()
    }

    @Synchronized
    override fun prev() {
        val stateHolder = stateHolder ?: return
        logD("Going to previous song")
        stateHolder.prev()
    }

    @Synchronized
    override fun goto(index: Int) {
        val stateHolder = stateHolder ?: return
        logD("Going to index $index")
        stateHolder.goto(index)
    }

    @Synchronized
    override fun playNext(songs: List<Song>) {
        if (currentSong == null) {
            logD("Nothing playing, short-circuiting to new playback")
            play(songs[0], null, songs, false)
        } else {
            val stateHolder = stateHolder ?: return
            logD("Adding ${songs.size} songs to start of queue")
            stateHolder.playNext(songs)
        }
    }

    @Synchronized
    override fun addToQueue(songs: List<Song>) {
        if (currentSong == null) {
            logD("Nothing playing, short-circuiting to new playback")
            play(songs[0], null, songs, false)
        } else {
            val stateHolder = stateHolder ?: return
            logD("Adding ${songs.size} songs to end of queue")
            stateHolder.addToQueue(songs)
        }
    }

    @Synchronized
    override fun moveQueueItem(src: Int, dst: Int) {
        val stateHolder = stateHolder ?: return
        logD("Moving item $src to position $dst")
        stateHolder.move(src, dst)
    }

    @Synchronized
    override fun removeQueueItem(at: Int) {
        val stateHolder = stateHolder ?: return
        logD("Removing item at $at")
        stateHolder.remove(at)
    }

    @Synchronized
    override fun shuffled(shuffled: Boolean) {
        val stateHolder = stateHolder ?: return
        logD("Reordering queue [shuffled=$shuffled]")
        stateHolder.reorder(shuffled)
    }

    // --- INTERNAL PLAYER FUNCTIONS ---

    @Synchronized
    override fun playDeferred(action: DeferredPlayback) {
        val stateHolder = stateHolder
        if (stateHolder == null || !stateHolder.handleDeferred(action)) {
            logD("Internal player not present or did not consume action, waiting")
            pendingDeferredPlayback = action
        }
    }

    @Synchronized
    override fun requestAction(stateHolder: PlaybackStateHolder) {
        if (BuildConfig.DEBUG && this.stateHolder !== stateHolder) {
            logW("Given internal player did not match current internal player")
            return
        }

        if (pendingDeferredPlayback?.let(stateHolder::handleDeferred) == true) {
            logD("Pending action consumed")
            pendingDeferredPlayback = null
        }
    }

    @Synchronized
    override fun playing(isPlaying: Boolean) {
        val stateHolder = stateHolder ?: return
        logD("Updating playing state to $isPlaying")
        stateHolder.playing(isPlaying)
    }

    @Synchronized
    override fun repeatMode(repeatMode: RepeatMode) {
        val stateHolder = stateHolder ?: return
        logD("Updating repeat mode to $repeatMode")
        stateHolder.repeatMode(repeatMode)
    }

    @Synchronized
    override fun seekTo(positionMs: Long) {
        val stateHolder = stateHolder ?: return
        logD("Seeking to ${positionMs}ms")
        stateHolder.seekTo(positionMs)
    }

    @Synchronized
    override fun dispatchEvent(stateHolder: PlaybackStateHolder, event: StateEvent) {
        if (BuildConfig.DEBUG && this.stateHolder !== stateHolder) {
            logW("Given internal player did not match current internal player")
            return
        }

        when (event) {
            is StateEvent.IndexMoved -> {
                stateMirror =
                    stateMirror.copy(
                        index = stateHolder.resolveIndex(),
                    )
                listeners.forEach { it.onIndexMoved(stateMirror.index) }
            }
            is StateEvent.QueueChanged -> {
                val instructions = event.instructions
                val newIndex = stateHolder.resolveIndex()
                val changeType =
                    when {
                        event.songChanged -> {
                            QueueChange.Type.SONG
                        }
                        stateMirror.index != newIndex -> QueueChange.Type.INDEX
                        else -> QueueChange.Type.MAPPING
                    }
                stateMirror = stateMirror.copy(queue = stateHolder.resolveQueue(), index = newIndex)
                val change = QueueChange(changeType, instructions)
                listeners.forEach {
                    it.onQueueChanged(stateMirror.queue, stateMirror.index, change)
                }
            }
            is StateEvent.QueueReordered -> {
                stateMirror =
                    stateMirror.copy(
                        queue = stateHolder.resolveQueue(),
                        index = stateHolder.resolveIndex(),
                        isShuffled = stateHolder.isShuffled,
                    )
                listeners.forEach {
                    it.onQueueReordered(
                        stateMirror.queue, stateMirror.index, stateMirror.isShuffled)
                }
            }
            is StateEvent.NewPlayback -> {
                stateMirror =
                    stateMirror.copy(
                        parent = stateHolder.parent,
                        queue = stateHolder.resolveQueue(),
                        index = stateHolder.resolveIndex(),
                        isShuffled = stateHolder.isShuffled,
                    )
                listeners.forEach {
                    it.onNewPlayback(
                        stateMirror.parent,
                        stateMirror.queue,
                        stateMirror.index,
                        stateMirror.isShuffled)
                }
            }
            is StateEvent.ProgressionChanged -> {
                stateMirror =
                    stateMirror.copy(
                        progression = stateHolder.progression,
                    )
                listeners.forEach { it.onProgressionChanged(stateMirror.progression) }
            }
            is StateEvent.RepeatModeChanged -> {
                stateMirror =
                    stateMirror.copy(
                        repeatMode = stateHolder.repeatMode,
                    )
                listeners.forEach { it.onRepeatModeChanged(stateMirror.repeatMode) }
            }
        }
    }

    // --- PERSISTENCE FUNCTIONS ---

    @Synchronized override fun toSavedState() = null
    //        queue.toSavedState()?.let {
    //            PlaybackStateManager.SavedState(
    //                parent = parent,
    //                queueState = it,
    //                positionMs = progression.calculateElapsedPositionMs(),
    //                repeatMode = repeatMode)
    //        }

    @Synchronized
    override fun applySavedState(
        savedState: PlaybackStateManager.SavedState,
        destructive: Boolean
    ) {
        //        if (isInitialized && !destructive) {
        //            logW("Already initialized, cannot apply saved  state")
        //            return
        //        }
        //        val stateHolder = stateHolder ?: return
        //        logD("Applying state $savedState")
        //
        //        val lastSong = queue.currentSong
        //        parent = savedState.parent
        //        queue.applySavedState(savedState.queueState)
        //        repeatMode = savedState.repeatMode
        //        notifyNewPlayback()
        //
        //        // Check if we need to reload the player with a new music file, or if we can just
        // leave
        //        // it be. Specifically done so we don't pause on music updates that don't really
        // change
        //        // what's playing (ex. playlist editing)
        //        if (lastSong != queue.currentSong) {
        //            logD("Song changed, must reload player")
        //            // Continuing playback while also possibly doing drastic state updates is
        //            // a bad idea, so pause.
        //            stateHolder.loadSong(queue.currentSong, false)
        //            if (queue.currentSong != null) {
        //                logD("Seeking to saved position ${savedState.positionMs}ms")
        //                // Internal player may have reloaded the media item, re-seek to the
        // previous
        //                // position
        //                seekTo(savedState.positionMs)
        //            }
        //        }
        isInitialized = true
    }
}

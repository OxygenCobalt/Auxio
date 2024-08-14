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
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager.Listener
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
    /** The current [Progression] of the audio player */
    val progression: Progression

    /** The current [RepeatMode]. */
    val repeatMode: RepeatMode

    /** The current [MusicParent] being played from */
    val parent: MusicParent?

    /** The current [Song] being played. Null if nothing is playing. */
    val currentSong: Song?

    /** The current queue of [Song]s. */
    val queue: List<Song>

    /** The index of the currently playing [Song] in the queue. */
    val index: Int

    /** Whether the queue is shuffled or not. */
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
     * @param command The parameters to start playback with.
     */
    fun play(command: PlaybackCommand)

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

    /**
     * Acknowledges that an event has happened that modified the state held by the current
     * [PlaybackStateHolder].
     *
     * @param stateHolder The [PlaybackStateHolder] to synchronize with. Must be the current
     *   [PlaybackStateHolder]. Does nothing if invoked by another [PlaybackStateHolder]
     *   implementation.
     *     @param ack The [StateAck] to acknowledge.
     */
    fun ack(stateHolder: PlaybackStateHolder, ack: StateAck)

    /**
     * Start a [DeferredPlayback] for the current [PlaybackStateHolder] to handle eventually.
     *
     * @param action The [DeferredPlayback] to perform.
     */
    fun playDeferred(action: DeferredPlayback)

    /**
     * Request that the pending [DeferredPlayback] (if any) be passed to the given
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

    /**
     * Update the current [RepeatMode].
     *
     * @param repeatMode The new [RepeatMode].
     */
    fun repeatMode(repeatMode: RepeatMode)

    /**
     * Seek to the given position in the currently playing [Song].
     *
     * @param positionMs The position to seek to, in milliseconds.
     */
    fun seekTo(positionMs: Long)

    fun endSession()

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
         *
         * @param index The new index of the currently playing [Song].
         */
        fun onIndexMoved(index: Int) {}

        /**
         * Called when the queue changed in a manner outlined by the given [DeferredPlayback].
         *
         * @param queue The songs of the new queue.
         * @param index The new index of the currently playing [Song].
         * @param change The [QueueChange] that occurred.
         */
        fun onQueueChanged(queue: List<Song>, index: Int, change: QueueChange) {}

        /**
         * Called when the queue has changed in a non-trivial manner (such as re-shuffling), but the
         * currently playing [Song] has not.
         *
         * @param queue The songs of the new queue.
         * @param index The new index of the currently playing [Song].
         * @param isShuffled Whether the queue is shuffled or not.
         */
        fun onQueueReordered(queue: List<Song>, index: Int, isShuffled: Boolean) {}

        /**
         * Called when a new playback configuration was created.
         *
         * @param parent The [MusicParent] item currently being played from.
         * @param queue The queue of [Song]s to play from.
         * @param index The index of the currently playing [Song].
         * @param isShuffled Whether the queue is shuffled or not.
         */
        fun onNewPlayback(
            parent: MusicParent?,
            queue: List<Song>,
            index: Int,
            isShuffled: Boolean
        ) {}

        /**
         * Called when the state of the audio player changes.
         *
         * @param progression The new state of the audio player.
         */
        fun onProgressionChanged(progression: Progression) {}

        /**
         * Called when the [RepeatMode] changes.
         *
         * @param repeatMode The new [RepeatMode].
         */
        fun onRepeatModeChanged(repeatMode: RepeatMode) {}

        fun onSessionEnded() {}
    }

    /**
     * A condensed representation of the playback state that can be persisted.
     *
     * @param parent The [MusicParent] item currently being played from.
     * @param positionMs The current position in the currently played song, in ms
     * @param repeatMode The current [RepeatMode].
     */
    data class SavedState(
        val positionMs: Long,
        val repeatMode: RepeatMode,
        val parent: MusicParent?,
        val heap: List<Song?>,
        val shuffledMapping: List<Int>,
        val index: Int,
        val songUid: Music.UID,
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
        val rawQueue: RawQueue
    )

    private val listeners = mutableListOf<Listener>()

    @Volatile
    private var stateMirror =
        StateMirror(
            progression = Progression.nil(),
            repeatMode = RepeatMode.NONE,
            parent = null,
            queue = emptyList(),
            index = -1,
            isShuffled = false,
            rawQueue = RawQueue.nil())
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
    override fun addListener(listener: Listener) {
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
    override fun removeListener(listener: Listener) {
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
        if (isInitialized && currentSong != null) {
            stateHolder.applySavedState(
                stateMirror.parent,
                stateMirror.rawQueue,
                stateMirror.progression.calculateElapsedPositionMs(),
                stateMirror.repeatMode,
                null)
        }
        pendingDeferredPlayback?.let(stateHolder::handleDeferred)
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
    override fun play(command: PlaybackCommand) {
        val stateHolder = stateHolder ?: return
        logD("Playing $command")
        // Played something, so we are initialized now
        isInitialized = true
        stateHolder.newPlayback(command)
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
            play(QueueCommand(songs))
        } else {
            val stateHolder = stateHolder ?: return
            logD("Adding ${songs.size} songs to start of queue")
            stateHolder.playNext(songs, StateAck.PlayNext(stateMirror.index + 1, songs.size))
        }
    }

    @Synchronized
    override fun addToQueue(songs: List<Song>) {
        if (currentSong == null) {
            logD("Nothing playing, short-circuiting to new playback")
            play(QueueCommand(songs))
        } else {
            val stateHolder = stateHolder ?: return
            logD("Adding ${songs.size} songs to end of queue")
            stateHolder.addToQueue(songs, StateAck.AddToQueue(queue.size, songs.size))
        }
    }

    private class QueueCommand(override val queue: List<Song>) : PlaybackCommand {
        override val song: Song? = null
        override val parent: MusicParent? = null
        override val shuffled = false
    }

    @Synchronized
    override fun moveQueueItem(src: Int, dst: Int) {
        val stateHolder = stateHolder ?: return
        logD("Moving item $src to position $dst")
        stateHolder.move(src, dst, StateAck.Move(src, dst))
    }

    @Synchronized
    override fun removeQueueItem(at: Int) {
        val stateHolder = stateHolder ?: return
        logD("Removing item at $at")
        stateHolder.remove(at, StateAck.Remove(at))
    }

    @Synchronized
    override fun shuffled(shuffled: Boolean) {
        val stateHolder = stateHolder ?: return
        logD("Reordering queue [shuffled=$shuffled]")
        stateHolder.shuffled(shuffled)
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
    override fun endSession() {
        val stateHolder = stateHolder ?: return
        logD("Ending session")
        stateHolder.endSession()
    }

    @Synchronized
    override fun ack(stateHolder: PlaybackStateHolder, ack: StateAck) {
        if (BuildConfig.DEBUG && this.stateHolder !== stateHolder) {
            logW("Given internal player did not match current internal player")
            return
        }

        when (ack) {
            is StateAck.IndexMoved -> {
                val rawQueue = stateHolder.resolveQueue()
                stateMirror = stateMirror.copy(index = rawQueue.resolveIndex(), rawQueue = rawQueue)
                listeners.forEach { it.onIndexMoved(stateMirror.index) }
            }
            is StateAck.PlayNext -> {
                val rawQueue = stateHolder.resolveQueue()
                val change =
                    QueueChange(QueueChange.Type.MAPPING, UpdateInstructions.Add(ack.at, ack.size))
                stateMirror =
                    stateMirror.copy(
                        queue = rawQueue.resolveSongs(),
                        rawQueue = rawQueue,
                    )
                listeners.forEach {
                    it.onQueueChanged(stateMirror.queue, stateMirror.index, change)
                }
            }
            is StateAck.AddToQueue -> {
                val rawQueue = stateHolder.resolveQueue()
                val change =
                    QueueChange(QueueChange.Type.MAPPING, UpdateInstructions.Add(ack.at, ack.size))
                stateMirror =
                    stateMirror.copy(
                        queue = rawQueue.resolveSongs(),
                        rawQueue = rawQueue,
                    )
                listeners.forEach {
                    it.onQueueChanged(stateMirror.queue, stateMirror.index, change)
                }
            }
            is StateAck.Move -> {
                val rawQueue = stateHolder.resolveQueue()
                val newIndex = rawQueue.resolveIndex()
                val change =
                    QueueChange(
                        if (stateMirror.index != newIndex) QueueChange.Type.INDEX
                        else QueueChange.Type.MAPPING,
                        UpdateInstructions.Move(ack.from, ack.to))

                stateMirror =
                    stateMirror.copy(
                        queue = rawQueue.resolveSongs(),
                        index = newIndex,
                        rawQueue = rawQueue,
                    )

                listeners.forEach {
                    it.onQueueChanged(stateMirror.queue, stateMirror.index, change)
                }
            }
            is StateAck.Remove -> {
                val rawQueue = stateHolder.resolveQueue()
                val newIndex = rawQueue.resolveIndex()
                val change =
                    QueueChange(
                        when {
                            ack.index == stateMirror.index -> QueueChange.Type.SONG
                            stateMirror.index != newIndex -> QueueChange.Type.INDEX
                            else -> QueueChange.Type.MAPPING
                        },
                        UpdateInstructions.Remove(ack.index, 1))

                stateMirror =
                    stateMirror.copy(
                        queue = rawQueue.resolveSongs(),
                        index = newIndex,
                        rawQueue = rawQueue,
                    )

                listeners.forEach {
                    it.onQueueChanged(stateMirror.queue, stateMirror.index, change)
                }
            }
            is StateAck.QueueReordered -> {
                val rawQueue = stateHolder.resolveQueue()
                stateMirror =
                    stateMirror.copy(
                        queue = rawQueue.resolveSongs(),
                        index = rawQueue.resolveIndex(),
                        isShuffled = rawQueue.isShuffled,
                        rawQueue = rawQueue)
                listeners.forEach {
                    it.onQueueReordered(
                        stateMirror.queue, stateMirror.index, stateMirror.isShuffled)
                }
            }
            is StateAck.NewPlayback -> {
                val rawQueue = stateHolder.resolveQueue()
                stateMirror =
                    stateMirror.copy(
                        parent = stateHolder.parent,
                        queue = rawQueue.resolveSongs(),
                        index = rawQueue.resolveIndex(),
                        isShuffled = rawQueue.isShuffled,
                        rawQueue = rawQueue)
                listeners.forEach {
                    it.onNewPlayback(
                        stateMirror.parent,
                        stateMirror.queue,
                        stateMirror.index,
                        stateMirror.isShuffled)
                }
            }
            is StateAck.ProgressionChanged -> {
                stateMirror =
                    stateMirror.copy(
                        progression = stateHolder.progression,
                    )
                listeners.forEach { it.onProgressionChanged(stateMirror.progression) }
            }
            is StateAck.RepeatModeChanged -> {
                stateMirror =
                    stateMirror.copy(
                        repeatMode = stateHolder.repeatMode,
                    )
                listeners.forEach { it.onRepeatModeChanged(stateMirror.repeatMode) }
            }
            is StateAck.SessionEnded -> {
                listeners.forEach { it.onSessionEnded() }
            }
        }
    }

    // --- PERSISTENCE FUNCTIONS ---

    @Synchronized
    override fun toSavedState(): PlaybackStateManager.SavedState? {
        val currentSong = currentSong ?: return null
        return PlaybackStateManager.SavedState(
            positionMs = stateMirror.progression.calculateElapsedPositionMs(),
            repeatMode = stateMirror.repeatMode,
            parent = stateMirror.parent,
            heap = stateMirror.rawQueue.heap,
            shuffledMapping = stateMirror.rawQueue.shuffledMapping,
            index = stateMirror.index,
            songUid = currentSong.uid,
        )
    }

    @Synchronized
    override fun applySavedState(
        savedState: PlaybackStateManager.SavedState,
        destructive: Boolean
    ) {
        if (isInitialized && !destructive) {
            logW("Already initialized, cannot apply saved state")
            return
        }

        val stateHolder = stateHolder ?: return

        // The heap may not be the same if the song composition changed between state saves/reloads.
        // This also means that we must modify the shuffled mapping as well, in what it points to
        // and it's general composition.
        val heap = mutableListOf<Song>()
        val adjustments = mutableListOf<Int?>()
        var currentShift = 0
        for (song in savedState.heap) {
            if (song != null) {
                heap.add(song)
                adjustments.add(currentShift)
            } else {
                adjustments.add(null)
                currentShift -= 1
            }
        }

        logD("Created adjustment mapping [max shift=$currentShift]")

        val shuffledMapping =
            savedState.shuffledMapping.mapNotNullTo(mutableListOf()) { index ->
                adjustments[index]?.let { index + it }
            }

        // Make sure we re-align the index to point to the previously playing song.
        fun pointingAtSong(index: Int): Boolean {
            val currentSong =
                if (shuffledMapping.isNotEmpty()) {
                    shuffledMapping.getOrNull(index)?.let { heap.getOrNull(it) }
                } else {
                    heap.getOrNull(index)
                }
            logD(currentSong)

            return currentSong?.uid == savedState.songUid
        }

        var index = savedState.index
        while (!pointingAtSong(index) && index > -1) {
            index--
        }

        logD("Corrected index: ${savedState.index} -> $index")

        check(shuffledMapping.all { it in heap.indices }) {
            "Queue inconsistency detected: Shuffled mapping indices out of heap bounds"
        }

        if (index < 0) {
            stateHolder.reset(StateAck.NewPlayback)
            return
        }

        val rawQueue =
            RawQueue(
                heap = heap,
                shuffledMapping = shuffledMapping,
                heapIndex =
                    if (shuffledMapping.isNotEmpty()) {
                        shuffledMapping[index]
                    } else {
                        index
                    })

        stateHolder.applySavedState(
            savedState.parent,
            rawQueue,
            savedState.positionMs,
            savedState.repeatMode,
            StateAck.NewPlayback)

        isInitialized = true
    }
}

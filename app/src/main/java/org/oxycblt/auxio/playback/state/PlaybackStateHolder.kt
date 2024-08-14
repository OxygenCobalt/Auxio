/*
 * Copyright (c) 2024 Auxio Project
 * PlaybackStateHolder.kt is part of Auxio.
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

import android.net.Uri
import android.os.SystemClock
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song

/**
 * The designated "source of truth" for the current playback state. Should only be used by
 * [PlaybackStateManager], which mirrors a more refined version of the state held here.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface PlaybackStateHolder {
    /** The current [Progression] state of the audio player. */
    val progression: Progression

    /** The current [RepeatMode] of the audio player. */
    val repeatMode: RepeatMode

    /** The current [MusicParent] being played from. Null if playing from all songs. */
    val parent: MusicParent?

    /**
     * Resolve the current queue state as a [RawQueue].
     *
     * @return The current queue state.
     */
    fun resolveQueue(): RawQueue

    /** The current audio session ID of the audio player. */
    val audioSessionId: Int

    /** Applies a completely new playback state to the holder. */
    fun newPlayback(command: PlaybackCommand)

    /**
     * Update the playing state of the audio player.
     *
     * @param playing Whether the player should be playing audio.
     */
    fun playing(playing: Boolean)

    /**
     * Seek to a position in the current song.
     *
     * @param positionMs The position to seek to, in milliseconds.
     */
    fun seekTo(positionMs: Long)

    /**
     * Update the repeat mode of the audio player.
     *
     * @param repeatMode The new repeat mode.
     */
    fun repeatMode(repeatMode: RepeatMode)

    /** Go to the next song in the queue. */
    fun next()

    /** Go to the previous song in the queue. */
    fun prev()

    /**
     * Go to a specific index in the queue.
     *
     * @param index The index to go to. Should be in the queue.
     */
    fun goto(index: Int)

    /**
     * Add songs to the currently playing item in the queue.
     *
     * @param songs The songs to add.
     * @param ack The [StateAck] to return to [PlaybackStateManager].
     */
    fun playNext(songs: List<Song>, ack: StateAck.PlayNext)

    /**
     * Add songs to the end of the queue.
     *
     * @param songs The songs to add.
     * @param ack The [StateAck] to return to [PlaybackStateManager].
     */
    fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue)

    /**
     * Move a song in the queue to a new position.
     *
     * @param from The index of the song to move.
     * @param to The index to move the song to.
     * @param ack The [StateAck] to return to [PlaybackStateManager].
     */
    fun move(from: Int, to: Int, ack: StateAck.Move)

    /**
     * Remove a song from the queue.
     *
     * @param at The index of the song to remove.
     * @param ack The [StateAck] to return to [PlaybackStateManager].
     * @return The [Song] that was removed.
     */
    fun remove(at: Int, ack: StateAck.Remove)

    /**
     * Reorder the queue.
     *
     * @param shuffled Whether the queue should be shuffled.
     */
    fun shuffled(shuffled: Boolean)

    /**
     * Handle a deferred playback action.
     *
     * @param action The action to handle.
     * @return Whether the action could be handled, or if it should be deferred for later.
     */
    fun handleDeferred(action: DeferredPlayback): Boolean

    /**
     * Override the current held state with a saved state.
     *
     * @param parent The parent to play from.
     * @param rawQueue The queue to use.
     * @param ack The [StateAck] to return to [PlaybackStateManager]. If null, do not return any
     *   ack.
     */
    fun applySavedState(
        parent: MusicParent?,
        rawQueue: RawQueue,
        positionMs: Long,
        repeatMode: RepeatMode,
        ack: StateAck.NewPlayback?
    )

    /** End whatever ongoing playback session may be going on */
    fun endSession()

    /** Reset this instance to an empty state. */
    fun reset(ack: StateAck.NewPlayback)
}

/**
 * An acknowledgement that the state of the [PlaybackStateHolder] has changed. This is sent back to
 * [PlaybackStateManager] once an operation in [PlaybackStateHolder] has completed so that the new
 * state can be mirrored to the rest of the application.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface StateAck {
    /**
     * @see PlaybackStateHolder.next
     * @see PlaybackStateHolder.prev
     * @see PlaybackStateHolder.goto
     */
    data object IndexMoved : StateAck

    /** @see PlaybackStateHolder.playNext */
    data class PlayNext(val at: Int, val size: Int) : StateAck

    /** @see PlaybackStateHolder.addToQueue */
    data class AddToQueue(val at: Int, val size: Int) : StateAck

    /** @see PlaybackStateHolder.move */
    data class Move(val from: Int, val to: Int) : StateAck

    /** @see PlaybackStateHolder.remove */
    data class Remove(val index: Int) : StateAck

    /** @see PlaybackStateHolder.shuffled */
    data object QueueReordered : StateAck

    /**
     * @see PlaybackStateHolder.newPlayback
     * @see PlaybackStateHolder.applySavedState
     */
    data object NewPlayback : StateAck

    /**
     * @see PlaybackStateHolder.playing
     * @see PlaybackStateHolder.seekTo
     */
    data object ProgressionChanged : StateAck

    /** @see PlaybackStateHolder.repeatMode */
    data object RepeatModeChanged : StateAck

    data object SessionEnded : StateAck
}

/**
 * The queue as it is represented in the audio player held by [PlaybackStateHolder]. This should not
 * be used as anything but a container. Use the provided fields to obtain saner queue information.
 *
 * @param heap The ordered list of all [Song]s in the queue.
 * @param shuffledMapping A list of indices that remap the songs in [heap] into a shuffled queue.
 *   Empty if the queue is not shuffled.
 * @param heapIndex The index of the current song in [heap]. Note that if shuffled, this will be a
 *   nonsensical value that cannot be used to obtain next and last songs without first resolving the
 *   queue.
 */
data class RawQueue(
    val heap: List<Song>,
    val shuffledMapping: List<Int>,
    val heapIndex: Int,
) {
    /** Whether the queue is currently shuffled. */
    val isShuffled = shuffledMapping.isNotEmpty()

    /**
     * Resolve and return the exact [Song] sequence in the queue.
     *
     * @return The [Song]s in the queue, in order.
     */
    fun resolveSongs() =
        if (isShuffled) {
            shuffledMapping.map { heap[it] }
        } else {
            heap
        }

    /**
     * Resolve and return the current index of the queue.
     *
     * @return The current index of the queue.
     */
    fun resolveIndex() =
        if (isShuffled) {
            shuffledMapping.indexOf(heapIndex)
        } else {
            heapIndex
        }

    companion object {
        /** Create a blank instance. */
        fun nil() = RawQueue(emptyList(), emptyList(), -1)
    }
}

/**
 * Represents the possible changes that can occur during certain queue mutation events.
 *
 * @param type The [Type] of the change to the internal queue state.
 * @param instructions The update done to the resolved queue list.
 */
data class QueueChange(val type: Type, val instructions: UpdateInstructions) {
    enum class Type {
        /** Only the mapping has changed. */
        MAPPING,

        /** The mapping has changed, and the index also changed to align with it. */
        INDEX,

        /**
         * The current song has changed, possibly alongside the mapping and index depending on the
         * context.
         */
        SONG
    }
}

/** Possible long-running background tasks handled by the background playback task. */
sealed interface DeferredPlayback {
    /** Restore the previously saved playback state. */
    data object RestoreState : DeferredPlayback

    /**
     * Start shuffled playback of the entire music library. Analogous to the "Shuffle All" shortcut.
     */
    data object ShuffleAll : DeferredPlayback

    /**
     * Start playing an audio file at the given [Uri].
     *
     * @param uri The [Uri] of the audio file to start playing.
     */
    data class Open(val uri: Uri) : DeferredPlayback
}

/** A representation of the current state of audio playback. Use [from] to create an instance. */
class Progression
private constructor(
    /** Whether the player is actively playing audio or set to play audio in the future. */
    val isPlaying: Boolean,
    /** Whether the player is actively playing audio in this moment. */
    private val isAdvancing: Boolean,
    /** The position when this instance was created, in milliseconds. */
    private val initPositionMs: Long,
    /** The time this instance was created, as a unix epoch timestamp. */
    private val creationTime: Long
) {
    /**
     * Calculate the "real" playback position this instance contains, in milliseconds.
     *
     * @return If paused, the original position will be returned. Otherwise, it will be the original
     *   position plus the time elapsed since this state was created.
     */
    fun calculateElapsedPositionMs() =
        if (isAdvancing) {
            initPositionMs + (SystemClock.elapsedRealtime() - creationTime)
        } else {
            // Not advancing due to buffering or some unrelated pausing, such as
            // a transient audio focus change.
            initPositionMs
        }

    // Equality ignores the creation time to prevent functionally identical states
    // from being non-equal.

    override fun equals(other: Any?) =
        other is Progression &&
            isPlaying == other.isPlaying &&
            isAdvancing == other.isAdvancing &&
            initPositionMs == other.initPositionMs

    override fun hashCode(): Int {
        var result = isPlaying.hashCode()
        result = 31 * result + isAdvancing.hashCode()
        result = 31 * result + initPositionMs.hashCode()
        return result
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param isPlaying Whether the player is actively playing audio or set to play audio in the
         *   future.
         * @param isAdvancing Whether the player is actively playing audio in this moment.
         * @param positionMs The current position of the player.
         */
        fun from(isPlaying: Boolean, isAdvancing: Boolean, positionMs: Long) =
            Progression(
                isPlaying,
                // Minor sanity check: Make sure that advancing can't occur if already paused.
                isPlaying && isAdvancing,
                positionMs,
                SystemClock.elapsedRealtime())

        fun nil() =
            Progression(
                isPlaying = false,
                isAdvancing = false,
                initPositionMs = 0,
                creationTime = SystemClock.elapsedRealtime())
    }
}

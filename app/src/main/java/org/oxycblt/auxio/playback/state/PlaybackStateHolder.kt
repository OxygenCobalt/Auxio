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
import android.support.v4.media.session.PlaybackStateCompat
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song

interface PlaybackStateHolder {
    val progression: Progression

    val repeatMode: RepeatMode

    val parent: MusicParent?

    fun resolveQueue(): RawQueue

    val audioSessionId: Int

    fun newPlayback(queue: List<Song>, start: Song?, parent: MusicParent?, shuffled: Boolean)

    fun playing(playing: Boolean)

    fun seekTo(positionMs: Long)

    fun repeatMode(repeatMode: RepeatMode)

    fun next()

    fun prev()

    fun goto(index: Int)

    fun playNext(songs: List<Song>, ack: StateAck.PlayNext)

    fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue)

    fun move(from: Int, to: Int, ack: StateAck.Move)

    fun remove(at: Int, ack: StateAck.Remove)

    fun shuffled(shuffled: Boolean)

    fun handleDeferred(action: DeferredPlayback): Boolean

    fun applySavedState(parent: MusicParent?, rawQueue: RawQueue)
}

sealed interface StateAck {
    data object IndexMoved : StateAck

    data class PlayNext(val at: Int, val size: Int) : StateAck

    data class AddToQueue(val at: Int, val size: Int) : StateAck

    data class Move(val from: Int, val to: Int) : StateAck

    data class Remove(val index: Int) : StateAck

    data object QueueReordered : StateAck

    data object NewPlayback : StateAck

    data object ProgressionChanged : StateAck

    data object RepeatModeChanged : StateAck
}

data class RawQueue(
    val heap: List<Song>,
    val shuffledMapping: List<Int>,
    val heapIndex: Int,
) {
    val isShuffled = shuffledMapping.isNotEmpty()

    fun resolveSongs() =
        if (isShuffled) {
            shuffledMapping.map { heap[it] }
        } else {
            heap
        }

    fun resolveIndex() =
        if (isShuffled) {
            shuffledMapping.indexOf(heapIndex)
        } else {
            heapIndex
        }

    companion object {
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

    /**
     * Load this instance into a [PlaybackStateCompat].
     *
     * @param builder The [PlaybackStateCompat.Builder] to mutate.
     * @return The same [PlaybackStateCompat.Builder] for easy chaining.
     */
    fun intoPlaybackState(builder: PlaybackStateCompat.Builder): PlaybackStateCompat.Builder =
        builder.setState(
            // State represents the user's preference, not the actual player state.
            // Doing this produces a better experience in the media control UI.
            if (isPlaying) {
                PlaybackStateCompat.STATE_PLAYING
            } else {
                PlaybackStateCompat.STATE_PAUSED
            },
            initPositionMs,
            if (isAdvancing) {
                1f
            } else {
                // Not advancing, so don't move the position.
                0f
            },
            creationTime)

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

        fun nil() = Progression(false, false, 0, SystemClock.elapsedRealtime())
    }
}

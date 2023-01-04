/*
 * Copyright (c) 2022 Auxio Project
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
import org.oxycblt.auxio.music.Song

/**
 * An interface for internal audio playback. This can be used to coordinate what occurs in the
 * background playback task.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface InternalPlayer {
    /** The ID of the audio session started by this instance. */
    val audioSessionId: Int

    /** Whether the player should rewind before skipping back. */
    val shouldRewindWithPrev: Boolean

    /**
     * Load a new [Song] into the internal player.
     * @param song The [Song] to load, or null if playback should stop entirely.
     * @param play Whether to start playing when the [Song] is loaded.
     */
    fun loadSong(song: Song?, play: Boolean)

    /**
     * Called when an [Action] has been queued and this [InternalPlayer] is available to handle it.
     * @param action The [Action] to perform.
     * @return true if the action was handled, false otherwise.
     */
    fun performAction(action: Action): Boolean

    /**
     * Get a [State] corresponding to the current player state.
     * @param durationMs The duration of the currently playing track, in milliseconds. Required
     * since the internal player cannot obtain an accurate duration itself.
     */
    fun getState(durationMs: Long): State

    /**
     * Seek to a given position in the internal player.
     * @param positionMs The position to seek to, in milliseconds.
     */
    fun seekTo(positionMs: Long)

    /**
     * Set whether the player should play or not.
     * @param isPlaying Whether to play or pause the current playback.
     */
    fun setPlaying(isPlaying: Boolean)

    /** Possible long-running background tasks handled by the background playback task. */
    sealed class Action {
        /** Restore the previously saved playback state. */
        object RestoreState : Action()

        /**
         * Start shuffled playback of the entire music library. Analogous to the "Shuffle All"
         * shortcut.
         */
        object ShuffleAll : Action()

        /**
         * Start playing an audio file at the given [Uri].
         * @param uri The [Uri] of the audio file to start playing.
         */
        data class Open(val uri: Uri) : Action()
    }

    /**
     * A representation of the current state of audio playback. Use [from] to create an instance.
     */
    class State
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
         * @return If paused, the original position will be returned. Otherwise, it will be the
         * original position plus the time elapsed since this state was created.
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
            other is State &&
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
             * @param isPlaying Whether the player is actively playing audio or set to play audio in
             * the future.
             * @param isAdvancing Whether the player is actively playing audio in this moment.
             * @param positionMs The current position of the player.
             */
            fun from(isPlaying: Boolean, isAdvancing: Boolean, positionMs: Long) =
                State(
                    isPlaying,
                    // Minor sanity check: Make sure that advancing can't occur if already paused.
                    isPlaying && isAdvancing,
                    positionMs,
                    SystemClock.elapsedRealtime())
        }
    }
}

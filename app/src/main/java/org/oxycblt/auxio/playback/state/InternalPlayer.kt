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

/** Represents a class capable of managing the internal player. */
interface InternalPlayer {
    /** The audio session ID of the player instance. */
    val audioSessionId: Int

    /** Whether the player should rewind instead of going to the previous song. */
    val shouldRewindWithPrev: Boolean

    val currentState: State

    /** Called when a new song should be loaded into the player. */
    fun loadSong(song: Song?, play: Boolean)

    /** Seek to [positionMs] in the player. */
    fun seekTo(positionMs: Long)

    /** Called when the playing state needs to be changed. */
    fun changePlaying(isPlaying: Boolean)

    /**
     * Called when [PlaybackStateManager] desires some [Action] to be completed. Returns true if the
     * action was consumed, false otherwise.
     */
    fun onAction(action: Action): Boolean

    class State
    private constructor(
        /**
         * Whether the user has actually chosen to play this audio. The player might not actually be
         * playing at this time.
         */
        val isPlaying: Boolean,
        /** Whether the player is actually advancing through the audio. */
        private val isAdvancing: Boolean,
        /** The initial position at update time. */
        private val initPositionMs: Long,
        /** The time this instance was created. */
        private val creationTime: Long
    ) {
        /**
         * Calculate the estimated position that the player is now at. If the player's position is
         * not advancing, this will be the initial position. Otherwise, this will be the position
         * plus the elapsed time since this state was uploaded.
         */
        fun calculateElapsedPosition() =
            if (isAdvancing) {
                initPositionMs + (SystemClock.elapsedRealtime() - creationTime)
            } else {
                // Not advancing due to buffering or some unrelated pausing, such as
                // a transient audio focus change.
                initPositionMs
            }

        /** Load this state into the analogous [PlaybackStateCompat.Builder]. */
        fun intoPlaybackState(builder: PlaybackStateCompat.Builder): PlaybackStateCompat.Builder =
            builder.setState(
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
            /** Create a new instance of this state. */
            fun new(isPlaying: Boolean, isAdvancing: Boolean, positionMs: Long) =
                State(
                    // Minor sanity check: Make sure that advancing can't occur if the
                    // main playing value is paused.
                    isPlaying,
                    isPlaying && isAdvancing,
                    positionMs,
                    SystemClock.elapsedRealtime())
        }
    }

    sealed class Action {
        object RestoreState : Action()
        object ShuffleAll : Action()
        data class Open(val uri: Uri) : Action()
    }
}

/*
 * Copyright (c) 2026 Auxio Project
 * AudioFocusPolicy.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.service

/** Pure decision helper for standard Android audio focus transitions. */
object AudioFocusPolicy {
    private const val DUCKED_VOLUME = 0.2f
    private const val NORMAL_VOLUME = 1f

    data class State(val wasPlayingBeforeTransientLoss: Boolean = false)

    enum class Event {
        LOSS,
        LOSS_TRANSIENT,
        LOSS_TRANSIENT_CAN_DUCK,
        GAIN,
    }

    data class Decision(
        val pause: Boolean = false,
        val resume: Boolean = false,
        val volume: Float = NORMAL_VOLUME,
        val rememberTransientPlayback: Boolean? = null,
    )

    fun decide(event: Event, state: State, isPlaying: Boolean): Decision =
        when (event) {
            Event.LOSS ->
                Decision(pause = true, volume = NORMAL_VOLUME, rememberTransientPlayback = false)
            Event.LOSS_TRANSIENT ->
                Decision(
                    pause = true,
                    volume = NORMAL_VOLUME,
                    rememberTransientPlayback = isPlaying,
                )
            Event.LOSS_TRANSIENT_CAN_DUCK ->
                if (isPlaying) {
                    Decision(
                        pause = false,
                        volume = DUCKED_VOLUME,
                        rememberTransientPlayback = true,
                    )
                } else {
                    Decision(pause = false, volume = NORMAL_VOLUME)
                }
            Event.GAIN ->
                Decision(
                    resume = state.wasPlayingBeforeTransientLoss,
                    volume = NORMAL_VOLUME,
                    rememberTransientPlayback = false,
                )
        }

    fun shouldResumePlayback(
        decision: Decision,
        playWhenReady: Boolean,
        sessionOngoing: Boolean,
        hasCurrentSong: Boolean,
    ): Boolean = decision.resume && !playWhenReady && sessionOngoing && hasCurrentSong

    fun shouldHandleMediaButton(
        isFocusHeld: Boolean,
        hasCurrentSong: Boolean,
        sessionOngoing: Boolean,
    ): Boolean = isFocusHeld && hasCurrentSong && sessionOngoing
}

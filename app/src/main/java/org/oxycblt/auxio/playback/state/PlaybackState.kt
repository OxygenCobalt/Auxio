/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackState.kt is part of Auxio.
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

/**
 * A database entity that stores a compressed variant of the current playback state.
 * @property id - The database key for this state
 * @property songHash - The hash for the currently playing song
 * @property parentHash - The hash for the currently playing parent
 * @property index - The current index in the queue.
 * @property mode - The integer form of the current [org.oxycblt.auxio.playback.state.PlaybackMode]
 * @property isShuffling - A bool for if the queue was shuffled
 * @property loopMode - The integer form of the current [org.oxycblt.auxio.playback.state.LoopMode]
 * @property inUserQueue - A bool for if the state was currently playing from the user queue.
 * @author OxygenCobalt
 */
data class PlaybackState(
    val id: Long = 0L,
    val songHash: Int,
    val position: Long,
    val parentHash: Int,
    val index: Int,
    val mode: Int,
    val isShuffling: Boolean,
    val loopMode: Int,
    val inUserQueue: Boolean
) {
    companion object {
        const val COLUMN_ID = "state_id"
        const val COLUMN_SONG_HASH = "song"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PARENT_HASH = "parent"
        const val COLUMN_INDEX = "_index"
        const val COLUMN_MODE = "mode"
        const val COLUMN_IS_SHUFFLING = "is_shuffling"
        const val COLUMN_LOOP_MODE = "loop_mode"
        const val COLUMN_IN_USER_QUEUE = "is_user_queue"
    }
}

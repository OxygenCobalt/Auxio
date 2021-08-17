/*
 * Copyright (c) 2021 Auxio Project
 * QueueItem.kt is part of Auxio.
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
 * A database entity that stores a simplified representation of a song in a queue.
 * @property id The database entity's id
 * @property songHash The hash for the song represented
 * @property albumHash The hash for the album represented
 * @property isUserQueue A bool for if this queue item is a user queue item or not
 * @author OxygenCobalt
 */
data class DatabaseQueueItem(
    var id: Long = 0L,
    val songHash: Int,
    val albumHash: Int,
    val isUserQueue: Boolean = false
) {
    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_HASH = "song"
        const val COLUMN_ALBUM_HASH = "album"
        const val COLUMN_IS_USER_QUEUE = "is_user_queue"
    }
}

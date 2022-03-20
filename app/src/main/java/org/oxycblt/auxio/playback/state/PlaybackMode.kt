/*
 * Copyright (c) 2021 Auxio Project
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

import org.oxycblt.auxio.IntegerTable

/**
 * Enum that indicates how the queue should be constructed.
 * @author OxygenCobalt
 */
enum class PlaybackMode {
    /** Construct the queue from the genre's songs */
    ALL_SONGS,
    /** Construct the queue from the artist's songs */
    IN_ALBUM,
    /** Construct the queue from the album's songs */
    IN_ARTIST,
    /** Construct the queue from all songs */
    IN_GENRE;

    /**
     * Convert the mode into an int constant, to be saved in PlaybackStateDatabase
     * @return The constant for this mode,
     */
    val intCode: Int
        get() =
            when (this) {
                ALL_SONGS -> IntegerTable.PLAYBACK_MODE_ALL_SONGS
                IN_ALBUM -> IntegerTable.PLAYBACK_MODE_IN_ALBUM
                IN_ARTIST -> IntegerTable.PLAYBACK_MODE_IN_ARTIST
                IN_GENRE -> IntegerTable.PLAYBACK_MODE_IN_GENRE
            }

    companion object {
        /**
         * Get a [PlaybackMode] for an int [constant]
         * @return The mode, null if there isn't one for this.
         */
        fun fromInt(constant: Int): PlaybackMode? {
            return when (constant) {
                IntegerTable.PLAYBACK_MODE_ALL_SONGS -> ALL_SONGS
                IntegerTable.PLAYBACK_MODE_IN_ALBUM -> IN_ALBUM
                IntegerTable.PLAYBACK_MODE_IN_ARTIST -> IN_ARTIST
                IntegerTable.PLAYBACK_MODE_IN_GENRE -> IN_GENRE
                else -> null
            }
        }
    }
}

/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackMode.kt is part of Auxio.
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
    fun toInt(): Int {
        return when (this) {
            ALL_SONGS -> CONST_ALL_SONGS
            IN_ALBUM -> CONST_IN_ALBUM
            IN_ARTIST -> CONST_IN_ARTIST
            IN_GENRE -> CONST_IN_GENRE
        }
    }

    companion object {
        // Kept in reverse order because of backwards compat, do not re-order these
        private const val CONST_ALL_SONGS = 0xA106
        private const val CONST_IN_ALBUM = 0xA105
        private const val CONST_IN_ARTIST = 0xA104
        private const val CONST_IN_GENRE = 0xA103

        /**
         * Get a [PlaybackMode] for an int [constant]
         * @return The mode, null if there isnt one for this.
         */
        fun fromInt(constant: Int): PlaybackMode? {
            return when (constant) {
                CONST_ALL_SONGS -> ALL_SONGS
                CONST_IN_ALBUM -> IN_ALBUM
                CONST_IN_ARTIST -> IN_ARTIST
                CONST_IN_GENRE -> IN_GENRE
                else -> null
            }
        }
    }
}

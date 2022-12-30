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
 
package org.oxycblt.auxio.music

import org.oxycblt.auxio.IntegerTable

/**
 * Represents a data configuration corresponding to a specific type of [Music],
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class MusicMode {
    /** Configure with respect to [Song] instances. */
    SONGS,
    /** Configure with respect to [Album] instances. */
    ALBUMS,
    /** Configure with respect to [Artist] instances. */
    ARTISTS,
    /** Configure with respect to [Genre] instances. */
    GENRES;

    /**
     * The integer representation of this instance.
     * @see fromIntCode
     */
    val intCode: Int
        get() =
            when (this) {
                SONGS -> IntegerTable.MUSIC_MODE_SONGS
                ALBUMS -> IntegerTable.MUSIC_MODE_ALBUMS
                ARTISTS -> IntegerTable.MUSIC_MODE_ARTISTS
                GENRES -> IntegerTable.MUSIC_MODE_GENRES
            }

    companion object {
        /**
         * Convert a [MusicMode] integer representation into an instance.
         * @param intCode An integer representation of a [MusicMode]
         * @return The corresponding [MusicMode], or null if the [MusicMode] is invalid.
         * @see MusicMode.intCode
         */
        fun fromIntCode(intCode: Int) =
            when (intCode) {
                IntegerTable.MUSIC_MODE_SONGS -> SONGS
                IntegerTable.MUSIC_MODE_ALBUMS -> ALBUMS
                IntegerTable.MUSIC_MODE_ARTISTS -> ARTISTS
                IntegerTable.MUSIC_MODE_GENRES -> GENRES
                else -> null
            }
    }
}

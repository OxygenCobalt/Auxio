/*
 * Copyright (c) 2022 Auxio Project
 * MusicType.kt is part of Auxio.
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
 * General configuration enum to control what kind of music is being worked with.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class MusicType {
    /** @see Song */
    SONGS,
    /** @see Album */
    ALBUMS,
    /** @see Artist */
    ARTISTS,
    /** @see Genre */
    GENRES,
    /** @see Playlist */
    PLAYLISTS;

    /**
     * The integer representation of this instance.
     *
     * @see fromIntCode
     */
    val intCode: Int
        get() =
            when (this) {
                SONGS -> IntegerTable.MUSIC_MODE_SONGS
                ALBUMS -> IntegerTable.MUSIC_MODE_ALBUMS
                ARTISTS -> IntegerTable.MUSIC_MODE_ARTISTS
                GENRES -> IntegerTable.MUSIC_MODE_GENRES
                PLAYLISTS -> IntegerTable.MUSIC_MODE_PLAYLISTS
            }

    companion object {
        /**
         * Convert a [MusicType] integer representation into an instance.
         *
         * @param intCode An integer representation of a [MusicType]
         * @return The corresponding [MusicType], or null if the [MusicType] is invalid.
         * @see MusicType.intCode
         */
        fun fromIntCode(intCode: Int) =
            when (intCode) {
                IntegerTable.MUSIC_MODE_SONGS -> SONGS
                IntegerTable.MUSIC_MODE_ALBUMS -> ALBUMS
                IntegerTable.MUSIC_MODE_ARTISTS -> ARTISTS
                IntegerTable.MUSIC_MODE_GENRES -> GENRES
                IntegerTable.MUSIC_MODE_PLAYLISTS -> PLAYLISTS
                else -> null
            }
    }
}

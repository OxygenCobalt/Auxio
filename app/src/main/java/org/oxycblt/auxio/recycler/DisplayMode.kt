/*
 * Copyright (c) 2021 Auxio Project
 * DisplayMode.kt is part of Auxio.
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

package org.oxycblt.auxio.recycler

/**
 * An enum for determining what items to show in a given list.
 * @author OxygenCobalt
 */
enum class DisplayMode {
    SHOW_GENRES,
    SHOW_ARTISTS,
    SHOW_ALBUMS,
    SHOW_SONGS;

    companion object {
        private const val CONST_SHOW_ALL = 0xA107
        private const val CONST_SHOW_GENRES = 0xA108
        private const val CONST_SHOW_ARTISTS = 0xA109
        private const val CONST_SHOW_ALBUMS = 0xA10A
        private const val CONST_SHOW_SONGS = 0xA10B

        fun toSearchInt(value: DisplayMode?): Int {
            return when (value) {
                SHOW_SONGS -> CONST_SHOW_SONGS
                SHOW_ALBUMS -> CONST_SHOW_ALBUMS
                SHOW_ARTISTS -> CONST_SHOW_ARTISTS
                SHOW_GENRES -> CONST_SHOW_GENRES
                null -> CONST_SHOW_ALL
            }
        }

        fun fromSearchInt(value: Int): DisplayMode? {
            return when (value) {
                CONST_SHOW_SONGS -> SHOW_SONGS
                CONST_SHOW_ALBUMS -> SHOW_ALBUMS
                CONST_SHOW_ARTISTS -> SHOW_ARTISTS
                CONST_SHOW_GENRES -> SHOW_GENRES
                else -> null
            }
        }
    }
}

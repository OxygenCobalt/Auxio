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

package org.oxycblt.auxio.ui

import org.oxycblt.auxio.R

/**
 * An enum for determining what items to show in a given list.
 * Note: **DO NOT RE-ARRANGE THE ENUM**. The ordinals are used to store library tabs, so doing
 *  changing them would also change the meaning of tab instances.
 * @author OxygenCobalt
 */
enum class DisplayMode {
    SHOW_SONGS,
    SHOW_ALBUMS,
    SHOW_ARTISTS,
    SHOW_GENRES;

    val string: Int get() = when (this) {
        SHOW_SONGS -> R.string.lbl_songs
        SHOW_ALBUMS -> R.string.lbl_albums
        SHOW_ARTISTS -> R.string.lbl_artists
        SHOW_GENRES -> R.string.lbl_genres
    }

    val icon: Int get() = when (this) {
        SHOW_SONGS -> R.drawable.ic_song
        SHOW_ALBUMS -> R.drawable.ic_album
        SHOW_ARTISTS -> R.drawable.ic_artist
        SHOW_GENRES -> R.drawable.ic_genre
    }

    companion object {
        private const val CONST_NULL = 0xA107
        private const val CONST_SHOW_GENRES = 0xA108
        private const val CONST_SHOW_ARTISTS = 0xA109
        private const val CONST_SHOW_ALBUMS = 0xA10A
        private const val CONST_SHOW_SONGS = 0xA10B

        /**
         * Convert this enum into an integer for filtering.
         * In this context, a null value means to filter nothing.
         * @return An integer constant for that display mode, or a constant for a null [DisplayMode]
         */
        fun toFilterInt(value: DisplayMode?): Int {
            return when (value) {
                SHOW_SONGS -> CONST_SHOW_SONGS
                SHOW_ALBUMS -> CONST_SHOW_ALBUMS
                SHOW_ARTISTS -> CONST_SHOW_ARTISTS
                SHOW_GENRES -> CONST_SHOW_GENRES
                null -> CONST_NULL
            }
        }

        /**
         * Convert a filtering integer to a [DisplayMode].
         * In this context, a null value means to filter nothing.
         * @return A [DisplayMode] for this constant (including null)
         */
        fun fromFilterInt(value: Int): DisplayMode? {
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

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

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import org.oxycblt.auxio.R

/**
 * An enum for determining what items to show in a given list.
 * @author OxygenCobalt
 */
enum class DisplayMode(@DrawableRes val iconRes: Int) {
    SHOW_ALL(R.drawable.ic_sort_none),
    SHOW_GENRES(R.drawable.ic_genre),
    SHOW_ARTISTS(R.drawable.ic_artist),
    SHOW_ALBUMS(R.drawable.ic_album),
    SHOW_SONGS(R.drawable.ic_song);

    fun isAllOr(value: DisplayMode) = this == SHOW_ALL || this == value

    @IdRes
    fun toId(): Int {
        return when (this) {
            SHOW_ALL -> R.id.option_filter_all
            SHOW_GENRES -> R.id.option_filter_genres
            SHOW_ARTISTS -> R.id.option_filter_artists
            SHOW_ALBUMS -> R.id.option_filter_albums
            SHOW_SONGS -> R.id.option_filter_songs
        }
    }

    fun toInt(): Int {
        return when (this) {
            SHOW_ALL -> CONST_SHOW_ALL
            SHOW_GENRES -> CONST_SHOW_GENRES
            SHOW_ARTISTS -> CONST_SHOW_ARTISTS
            SHOW_ALBUMS -> CONST_SHOW_ALBUMS
            SHOW_SONGS -> CONST_SHOW_SONGS
        }
    }

    companion object {
        const val CONST_SHOW_ALL = 0xA107
        const val CONST_SHOW_GENRES = 0xA108
        const val CONST_SHOW_ARTISTS = 0xA109
        const val CONST_SHOW_ALBUMS = 0xA10A
        const val CONST_SHOW_SONGS = 0xA10B

        fun fromId(@IdRes id: Int): DisplayMode {
            return when (id) {
                R.id.option_filter_all -> SHOW_ALL
                R.id.option_filter_songs -> SHOW_SONGS
                R.id.option_filter_albums -> SHOW_ALBUMS
                R.id.option_filter_artists -> SHOW_ARTISTS
                R.id.option_filter_genres -> SHOW_GENRES

                else -> SHOW_ALL
            }
        }

        /**
         * Get an enum for an int constant
         * @return The [DisplayMode] if the constant is valid, null otherwise.
         */
        fun fromInt(value: Int): DisplayMode? {
            return when (value) {
                CONST_SHOW_ALL -> SHOW_ALL
                CONST_SHOW_GENRES -> SHOW_GENRES
                CONST_SHOW_ARTISTS -> SHOW_ARTISTS
                CONST_SHOW_ALBUMS -> SHOW_ALBUMS
                CONST_SHOW_SONGS -> SHOW_SONGS

                else -> null
            }
        }
    }
}

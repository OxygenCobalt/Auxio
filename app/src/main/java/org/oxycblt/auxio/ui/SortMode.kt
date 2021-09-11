/*
 * Copyright (c) 2021 Auxio Project
 * SortMode.kt is part of Auxio.
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

import androidx.annotation.DrawableRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song

/**
 * The legacy enum for sorting. This is set to be removed soon.
 * @property iconRes The icon for this [SortMode]
 * @author OxygenCobalt
 */
enum class SortMode(@DrawableRes val iconRes: Int) {
    // Icons for each mode are assigned to the enums themselves
    NONE(R.drawable.ic_sort),
    ALPHA_UP(R.drawable.ic_sort),
    ALPHA_DOWN(R.drawable.ic_sort),
    NUMERIC_UP(R.drawable.ic_sort),
    NUMERIC_DOWN(R.drawable.ic_sort);

    /**
     * Get a sorted list of songs for a SortMode. Supports alpha + numeric sorting.
     * @param songs An unsorted list of songs.
     * @return The sorted list of songs.
     */
    fun getSortedSongList(songs: List<Song>): List<Song> {
        return when (this) {
            ALPHA_UP -> songs.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            ALPHA_DOWN -> songs.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            NUMERIC_UP -> songs.sortedWith(compareByDescending { it.track })
            NUMERIC_DOWN -> songs.sortedWith(compareBy { it.track })

            else -> songs
        }
    }

    /**
     * Get a sorted list of songs with regards to an artist.
     * @param songs An unsorted list of songs
     * @return The sorted list of songs
     */
    fun getSortedArtistSongList(songs: List<Song>): List<Song> {
        return when (this) {
            ALPHA_UP -> songs.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            ALPHA_DOWN -> songs.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            NUMERIC_UP -> {
                val list = mutableListOf<Song>()

                songs.groupBy { it.album }.entries.sortedBy { it.key.year }.forEach { entry ->
                    list.addAll(entry.value.sortedWith(compareBy { it.track }))
                }

                list
            }

            NUMERIC_DOWN -> {
                val list = mutableListOf<Song>()

                songs.groupBy { it.album }.entries.sortedWith(compareByDescending { it.key.year }).forEach { entry ->
                    list.addAll(entry.value.sortedWith(compareBy { it.track }))
                }

                list
            }

            else -> songs
        }
    }

    /**
     * Get the constant for this mode. Used to write a compressed variant to SettingsManager
     * @return The int constant for this mode.
     */
    fun toInt(): Int {
        return when (this) {
            NONE -> CONST_NONE
            ALPHA_UP -> CONST_ALPHA_UP
            ALPHA_DOWN -> CONST_ALPHA_DOWN
            NUMERIC_UP -> CONST_NUMERIC_UP
            NUMERIC_DOWN -> CONST_NUMERIC_DOWN
        }
    }

    companion object {
        const val CONST_NONE = 0xA10C
        const val CONST_ALPHA_UP = 0xA10D
        const val CONST_ALPHA_DOWN = 0xA10E
        const val CONST_NUMERIC_UP = 0xA10F
        const val CONST_NUMERIC_DOWN = 0xA110

        /**
         * Get an enum for an int constant
         * @return The [SortMode] if the constant is valid, null otherwise.
         */
        fun fromInt(value: Int): SortMode? {
            return when (value) {
                CONST_NONE -> NONE
                CONST_ALPHA_UP -> ALPHA_UP
                CONST_ALPHA_DOWN -> ALPHA_DOWN
                CONST_NUMERIC_UP -> NUMERIC_UP
                CONST_NUMERIC_DOWN -> NUMERIC_DOWN

                else -> null
            }
        }
    }
}

/**
 * Slice a string so that any preceding articles like The/A(n) are truncated.
 * This is hilariously anglo-centric, but its mostly for MediaStore compat and hopefully
 * shouldn't run with other languages.
 */
fun String.sliceArticle(): String {
    if (length > 5 && startsWith("the ", true)) {
        return slice(4..lastIndex)
    }

    if (length > 4 && startsWith("an ", true)) {
        return slice(3..lastIndex)
    }

    if (length > 3 && startsWith("a ", true)) {
        return slice(2..lastIndex)
    }

    return this
}

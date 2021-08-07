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

package org.oxycblt.auxio.recycler

import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song

/**
 * An enum for the current sorting mode. Contains helper functions to sort lists based
 * off the given sorting mode.
 * @property iconRes The icon for this [SortMode]
 * @author OxygenCobalt
 */
enum class SortMode(@DrawableRes val iconRes: Int) {
    // Icons for each mode are assigned to the enums themselves
    NONE(R.drawable.ic_sort_none),
    ALPHA_UP(R.drawable.ic_sort_alpha_up),
    ALPHA_DOWN(R.drawable.ic_sort_alpha_down),
    NUMERIC_UP(R.drawable.ic_sort_numeric_up),
    NUMERIC_DOWN(R.drawable.ic_sort_numeric_down);

    /**
     * Get a sorted list of genres for a SortMode. Only supports alphabetic sorting.
     * @param genres An unsorted list of genres.
     * @return The sorted list of genres.
     */
    fun getSortedGenreList(genres: List<Genre>): List<Genre> {
        return when (this) {
            ALPHA_UP -> genres.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) {
                    it.resolvedName.sliceArticle()
                }
            )

            ALPHA_DOWN -> genres.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) {
                    it.resolvedName.sliceArticle()
                }
            )

            else -> genres
        }
    }

    /**
     * Get a sorted list of artists for a SortMode. Only supports alphabetic sorting.
     * @param artists An unsorted list of artists.
     * @return The sorted list of artists.
     */
    fun getSortedArtistList(artists: List<Artist>): List<Artist> {
        return when (this) {
            ALPHA_UP -> artists.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            ALPHA_DOWN -> artists.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            else -> artists
        }
    }

    /**
     * Get a sorted list of albums for a SortMode. Supports alpha + numeric sorting.
     * @param albums An unsorted list of albums.
     * @return The sorted list of albums.
     */
    fun getSortedAlbumList(albums: List<Album>): List<Album> {
        return when (this) {
            ALPHA_UP -> albums.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            ALPHA_DOWN -> albums.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) {
                    it.name.sliceArticle()
                }
            )

            NUMERIC_UP -> albums.sortedBy { it.year }
            NUMERIC_DOWN -> albums.sortedByDescending { it.year }

            else -> albums
        }
    }

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
     * Get a sorting menu ID for this mode. Alphabetic only.
     * @return The action id for this mode.
     */
    @IdRes
    fun toMenuId(): Int {
        return when (this) {
            ALPHA_UP -> R.id.option_sort_alpha_up
            ALPHA_DOWN -> R.id.option_sort_alpha_down

            else -> R.id.option_sort_alpha_up
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
 * Bind the [SortMode] icon for an ImageButton.
 */
@BindingAdapter("sortIcon")
fun ImageButton.bindSortIcon(mode: SortMode) {
    setImageResource(mode.iconRes)
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

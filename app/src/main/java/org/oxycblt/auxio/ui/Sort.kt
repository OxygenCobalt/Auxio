/*
 * Copyright (c) 2021 Auxio Project
 * Sort.kt is part of Auxio.
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

import androidx.annotation.IdRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song

/**
 * A data class representing the sort modes used in Auxio.
 *
 * Sorting can be done by Name, Artist, Album, or Year. Sorting of names is always case-insensitive
 * and article-aware. Certain datatypes may only support a subset of sorts since certain sorts
 * cannot be easily applied to them (For Example, [Artist] and [ByYear] or [ByAlbum]).
 *
 * Internally, sorts are saved as an integer in the following format
 *
 * 0b(SORT INT)A
 *
 * Where SORT INT is the corresponding integer value of this specific sort and A is a bit
 * representing whether this sort is ascending or descending.
 *
 * @author OxygenCobalt
 */
sealed class Sort(open val isAscending: Boolean) {
    /** Sort by the names of an item */
    class ByName(override val isAscending: Boolean) : Sort(isAscending)
    /** Sort by the artist of an item, only supported by [Album] and [Song] */
    class ByArtist(override val isAscending: Boolean) : Sort(isAscending)
    /** Sort by the album of an item, only supported by [Song] */
    class ByAlbum(override val isAscending: Boolean) : Sort(isAscending)
    /** Sort by the year of an item, only supported by [Album] and [Song] */
    class ByYear(override val isAscending: Boolean) : Sort(isAscending)

    /**
     * Get the corresponding item id for this sort.
     */
    val itemId: Int get() = when (this) {
        is ByName -> R.id.option_sort_name
        is ByArtist -> R.id.option_sort_artist
        is ByAlbum -> R.id.option_sort_album
        is ByYear -> R.id.option_sort_year
    }

    /**
     * Apply [ascending] to the status of this sort.
     * @return A new [Sort] with the value of [ascending] applied.
     */
    fun ascending(ascending: Boolean): Sort {
        return when (this) {
            is ByName -> ByName(ascending)
            is ByArtist -> ByArtist(ascending)
            is ByAlbum -> ByAlbum(ascending)
            is ByYear -> ByYear(ascending)
        }
    }

    /**
     * Assign a new [id] to this sort
     * @return A new [Sort] corresponding to the [id] given, null if the ID has no analogue.
     */
    fun assignId(@IdRes id: Int): Sort? {
        return when (id) {
            R.id.option_sort_name -> ByName(isAscending)
            R.id.option_sort_artist -> ByArtist(isAscending)
            R.id.option_sort_album -> ByAlbum(isAscending)
            R.id.option_sort_year -> ByYear(isAscending)
            else -> null
        }
    }

    /**
     * Sort a list of [Song] instances to reflect this specific sort.
     *
     * Albums are sorted by ascending track, artists are sorted with [ByYear] descending.
     *
     * @return A sorted list of songs
     */
    fun sortSongs(songs: Collection<Song>): List<Song> {
        return when (this) {
            is ByName -> songs.stringSort { it.name }

            else -> sortAlbums(songs.groupBy { it.album }.keys).flatMap { album ->
                album.songs.intSort(true) { it.track }
            }
        }
    }

    /**
     * Sort a list of [Album] instances to reflect this specific sort.
     *
     * Artists are sorted with [ByYear] descending.
     *
     * @return A sorted list of albums
     */
    fun sortAlbums(albums: Collection<Album>): List<Album> {
        return when (this) {
            is ByName, is ByAlbum -> albums.stringSort { it.resolvedName }

            is ByArtist -> sortParents(albums.groupBy { it.artist }.keys)
                .flatMap { ByYear(false).sortAlbums(it.albums) }

            is ByYear -> albums.intSort { it.year }
        }
    }

    /**
     * Sort a list of [MusicParent] instances to reflect this specific sort.
     *
     * @return A sorted list of the specific parent
     */
    fun <T : MusicParent> sortParents(parents: Collection<T>): List<T> {
        return parents.stringSort { it.resolvedName }
    }

    /**
     * Sort the songs in an album.
     * @see sortSongs
     */
    fun sortAlbum(album: Album): List<Song> {
        return album.songs.intSort { it.track }
    }

    /**
     * Sort the songs in an artist.
     * @see sortSongs
     */
    fun sortArtist(artist: Artist): List<Song> {
        return sortSongs(artist.songs)
    }

    /**
     * Sort the songs in a genre.
     * @see sortSongs
     */
    fun sortGenre(genre: Genre): List<Song> {
        return sortSongs(genre.songs)
    }

    /**
     * Convert this sort to it's integer representation.
     */
    fun toInt(): Int {
        return when (this) {
            is ByName -> CONST_NAME
            is ByArtist -> CONST_ARTIST
            is ByAlbum -> CONST_ALBUM
            is ByYear -> CONST_YEAR
        }.shl(1) or if (isAscending) 1 else 0
    }

    private fun <T : Music> Collection<T>.stringSort(
        asc: Boolean = isAscending,
        selector: (T) -> String
    ): List<T> {
        // Chain whatever item call with sliceArticle for correctness
        val chained: (T) -> String = {
            selector(it).sliceArticle()
        }

        val comparator = if (asc) {
            compareBy(String.CASE_INSENSITIVE_ORDER, chained)
        } else {
            compareByDescending(String.CASE_INSENSITIVE_ORDER, chained)
        }

        return sortedWith(comparator)
    }

    private fun <T : Music> Collection<T>.intSort(
        asc: Boolean = isAscending,
        selector: (T) -> Int,
    ): List<T> {
        val comparator = if (asc) {
            compareBy(selector)
        } else {
            compareByDescending(selector)
        }

        return sortedWith(comparator)
    }

    companion object {
        private const val CONST_NAME = 0xA10C
        private const val CONST_ARTIST = 0xA10D
        private const val CONST_ALBUM = 0xA10E
        private const val CONST_YEAR = 0xA10F

        /**
         * Convert a sort's integer representation into a [Sort] instance.
         *
         * @return A [Sort] instance, null if the data is malformed.
         */
        fun fromInt(value: Int): Sort? {
            val ascending = (value and 1) == 1

            return when (value.shr(1)) {
                CONST_NAME -> ByName(ascending)
                CONST_ARTIST -> ByArtist(ascending)
                CONST_ALBUM -> ByAlbum(ascending)
                CONST_YEAR -> ByYear(ascending)
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

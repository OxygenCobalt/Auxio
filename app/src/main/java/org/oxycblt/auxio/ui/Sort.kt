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
 
package org.oxycblt.auxio.ui

import androidx.annotation.IdRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logW

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
    open fun songs(songs: Collection<Song>): List<Song> {
        logW("This sort is not supported for songs")
        return songs.toList()
    }

    open fun albums(albums: Collection<Album>): List<Album> {
        logW("This sort is not supported for albums")
        return albums.toList()
    }

    open fun artists(artists: Collection<Artist>): List<Artist> {
        logW("This sort is not supported for artists")
        return artists.toList()
    }

    open fun genres(genres: Collection<Genre>): List<Genre> {
        logW("This sort is not supported for genres")
        return genres.toList()
    }

    /** Sort by the names of an item */
    class ByName(override val isAscending: Boolean) : Sort(isAscending) {
        override fun songs(songs: Collection<Song>): List<Song> {
            return songs.sortedWith(compareByDynamic(NameComparator()) { it })
        }

        override fun albums(albums: Collection<Album>): List<Album> {
            return albums.sortedWith(compareByDynamic(NameComparator()) { it })
        }

        override fun artists(artists: Collection<Artist>): List<Artist> {
            return artists.sortedWith(compareByDynamic(NameComparator()) { it })
        }

        override fun genres(genres: Collection<Genre>): List<Genre> {
            return genres.sortedWith(compareByDynamic(NameComparator()) { it })
        }
    }

    /** Sort by the album of an item, only supported by [Song] */
    class ByAlbum(override val isAscending: Boolean) : Sort(isAscending) {
        override fun songs(songs: Collection<Song>): List<Song> {
            return songs.sortedWith(
                MultiComparator(
                    compareByDynamic(NameComparator()) { it.album },
                    compareBy(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }
    }

    /** Sort by the artist of an item, only supported by [Album] and [Song] */
    class ByArtist(override val isAscending: Boolean) : Sort(isAscending) {
        override fun songs(songs: Collection<Song>): List<Song> {
            return songs.sortedWith(
                MultiComparator(
                    compareByDynamic(NameComparator()) { it.album.artist },
                    compareByDescending(NullableComparator()) { it.album.year },
                    compareByDescending(NameComparator()) { it.album },
                    compareBy(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }

        override fun albums(albums: Collection<Album>): List<Album> {
            return albums.sortedWith(
                MultiComparator(
                    compareByDynamic(NameComparator()) { it.artist },
                    compareByDescending(NullableComparator()) { it.year },
                    compareBy(NameComparator()) { it }))
        }
    }

    /** Sort by the year of an item, only supported by [Album] and [Song] */
    class ByYear(override val isAscending: Boolean) : Sort(isAscending) {
        override fun songs(songs: Collection<Song>): List<Song> {
            return songs.sortedWith(
                MultiComparator(
                    compareByDynamic(NullableComparator()) { it.album.year },
                    compareByDescending(NameComparator()) { it.album },
                    compareBy(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }

        override fun albums(albums: Collection<Album>): List<Album> {
            return albums.sortedWith(
                MultiComparator(
                    compareByDynamic(NullableComparator()) { it.year },
                    compareBy(NameComparator()) { it }))
        }
    }

    /** Get the corresponding item id for this sort. */
    val itemId: Int
        get() =
            when (this) {
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
     * Sort the songs in an album.
     * @see songs
     */
    fun album(album: Album): List<Song> {
        return album.songs.sortedWith(
            MultiComparator(
                compareByDynamic(NullableComparator()) { it.track },
                compareBy(NameComparator()) { it }))
    }

    /**
     * Sort the songs in an artist.
     * @see songs
     */
    fun artist(artist: Artist): List<Song> {
        return songs(artist.songs)
    }

    /**
     * Sort the songs in a genre.
     * @see songs
     */
    fun genre(genre: Genre): List<Song> {
        return songs(genre.songs)
    }

    /** Convert this sort to it's integer representation. */
    fun toInt(): Int {
        return when (this) {
            is ByName -> INT_NAME
            is ByArtist -> INT_ARTIST
            is ByAlbum -> INT_ALBUM
            is ByYear -> INT_YEAR
        }.shl(1) or if (isAscending) 1 else 0
    }

    protected inline fun <T : Music, K> compareByDynamic(
        comparator: Comparator<in K>,
        crossinline selector: (T) -> K
    ): Comparator<T> {
        return if (isAscending) {
            compareBy(comparator, selector)
        } else {
            compareByDescending(comparator, selector)
        }
    }

    class NameComparator<T : Music> : Comparator<T> {
        override fun compare(a: T?, b: T?): Int {
            if (a == null && b != null) return -1 // -1 -> a < b
            if (a == null && b == null) return 0 // 0 -> 0 = b
            if (a != null && b == null) return 1 // 1 -> a > b

            return a!!.resolvedName
                .sliceArticle()
                .compareTo(b!!.resolvedName.sliceArticle(), ignoreCase = true)
        }
    }

    class NullableComparator<T : Comparable<T>> : Comparator<T?> {
        override fun compare(a: T?, b: T?): Int {
            if (a == null && b != null) return -1 // -1 -> a < b
            if (a == null && b == null) return 0 // 0 -> 0 = b
            if (a != null && b == null) return 1 // 1 -> a > b
            return a!!.compareTo(b!!)
        }
    }

    /**
     * Chains the given comparators together to form one comparator.
     *
     * Sorts often need to compare multiple things at once across several hierarchies, with this
     * class doing such in a more efficient manner than resorting at multiple intervals or grouping
     * items up. Comparators are checked from first to last, with the first comparator that returns a
     * non-equal result being propagated upwards.
     */
    class MultiComparator<T>(vararg comparators: Comparator<T>) : Comparator<T> {
        private val mComparators = comparators

        override fun compare(a: T?, b: T?): Int {
            for (comparator in mComparators) {
                val result = comparator.compare(a, b)
                if (result != 0) {
                    return result
                }
            }

            return 0
        }
    }

    companion object {
        private const val INT_NAME = 0xA10C
        private const val INT_ARTIST = 0xA10D
        private const val INT_ALBUM = 0xA10E
        private const val INT_YEAR = 0xA10F

        /**
         * Convert a sort's integer representation into a [Sort] instance.
         *
         * @return A [Sort] instance, null if the data is malformed.
         */
        fun fromInt(value: Int): Sort? {
            val ascending = (value and 1) == 1

            return when (value.shr(1)) {
                INT_NAME -> ByName(ascending)
                INT_ARTIST -> ByArtist(ascending)
                INT_ALBUM -> ByAlbum(ascending)
                INT_YEAR -> ByYear(ascending)
                else -> null
            }
        }
    }
}

/**
 * Slice a string so that any preceding articles like The/A(n) are truncated. This is hilariously
 * anglo-centric, but its mostly for MediaStore compat and hopefully shouldn't run with other
 * languages.
 */
fun String.sliceArticle(): String {
    if (length > 5 && startsWith("the ", ignoreCase = true)) {
        return slice(4..lastIndex)
    }

    if (length > 4 && startsWith("an ", ignoreCase = true)) {
        return slice(3..lastIndex)
    }

    if (length > 3 && startsWith("a ", ignoreCase = true)) {
        return slice(2..lastIndex)
    }

    return this
}

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
import org.oxycblt.auxio.IntegerTable
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
 * Sorting can be done by Name, Artist, Album, and others. Sorting of names is always
 * case-insensitive and article-aware. Certain datatypes may only support a subset of sorts since
 * certain sorts cannot be easily applied to them (For Example, [Artist] and [ByYear] or [ByAlbum]).
 *
 * Internally, sorts are saved as an integer in the following format
 *
 * 0b(SORT INT)A
 *
 * Where SORT INT is the corresponding integer value of this specific sort and A is a bit
 * representing whether this sort is ascending or descending.
 *
 * @author OxygenCobalt
 *
 * TODO: Make comparators static instances
 */
sealed class Sort(open val isAscending: Boolean) {
    protected abstract val sortIntCode: Int
    abstract val itemId: Int

    fun songs(songs: Collection<Song>): List<Song> {
        val mutable = songs.toMutableList()
        songsInPlace(mutable)
        return mutable
    }

    fun albums(albums: Collection<Album>): List<Album> {
        val mutable = albums.toMutableList()
        albumsInPlace(mutable)
        return mutable
    }

    fun artists(artists: Collection<Artist>): List<Artist> {
        val mutable = artists.toMutableList()
        artistsInPlace(mutable)
        return mutable
    }

    fun genres(genres: Collection<Genre>): List<Genre> {
        val mutable = genres.toMutableList()
        genresInPlace(mutable)
        return mutable
    }

    open fun songsInPlace(songs: MutableList<Song>) {
        logW("This sort is not supported for songs")
    }

    open fun albumsInPlace(albums: MutableList<Album>) {
        logW("This sort is not supported for albums")
    }

    open fun artistsInPlace(artists: MutableList<Artist>) {
        logW("This sort is not supported for artists")
    }

    open fun genresInPlace(genres: MutableList<Genre>) {
        logW("This sort is not supported for genres")
    }

    /**
     * Apply [newIsAscending] to the status of this sort.
     * @return A new [Sort] with the value of [newIsAscending] applied.
     */
    abstract fun ascending(newIsAscending: Boolean): Sort

    /** Sort by the names of an item */
    class ByName(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_NAME

        override val itemId: Int
            get() = R.id.option_sort_name

        override fun songsInPlace(songs: MutableList<Song>) {
            songs.sortWith(compareByDynamic(NameComparator()) { it })
        }

        override fun albumsInPlace(albums: MutableList<Album>) {
            albums.sortWith(compareByDynamic(NameComparator()) { it })
        }

        override fun artistsInPlace(artists: MutableList<Artist>) {
            artists.sortWith(compareByDynamic(NameComparator()) { it })
        }

        override fun genresInPlace(genres: MutableList<Genre>) {
            genres.sortWith(compareByDynamic(NameComparator()) { it })
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByName(newIsAscending)
        }
    }

    /** Sort by the album of an item, only supported by [Song] */
    class ByAlbum(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_ALBUM

        override val itemId: Int
            get() = R.id.option_sort_album

        override fun songsInPlace(songs: MutableList<Song>) {
            songs.sortWith(
                MultiComparator(
                    compareByDynamic(NameComparator()) { it.album },
                    compareBy(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByAlbum(newIsAscending)
        }
    }

    /** Sort by the artist of an item, only supported by [Album] and [Song] */
    class ByArtist(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_ARTIST

        override val itemId: Int
            get() = R.id.option_sort_artist

        override fun songsInPlace(songs: MutableList<Song>) {
            songs.sortWith(
                MultiComparator(
                    compareByDynamic(NameComparator()) { it.album.artist },
                    compareByDescending(NullableComparator()) { it.album.year },
                    compareByDescending(NameComparator()) { it.album },
                    compareBy(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }

        override fun albumsInPlace(albums: MutableList<Album>) {
            albums.sortWith(
                MultiComparator(
                    compareByDynamic(NameComparator()) { it.artist },
                    compareByDescending(NullableComparator()) { it.year },
                    compareBy(NameComparator()) { it }))
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByArtist(newIsAscending)
        }
    }

    /** Sort by the year of an item, only supported by [Album] and [Song] */
    class ByYear(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_YEAR

        override val itemId: Int
            get() = R.id.option_sort_year

        override fun songsInPlace(songs: MutableList<Song>) {
            songs.sortWith(
                MultiComparator(
                    compareByDynamic(NullableComparator()) { it.album.year },
                    compareByDescending(NameComparator()) { it.album },
                    compareBy(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }

        override fun albumsInPlace(albums: MutableList<Album>) {
            albums.sortWith(
                MultiComparator(
                    compareByDynamic(NullableComparator()) { it.year },
                    compareBy(NameComparator()) { it }))
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByYear(newIsAscending)
        }
    }

    /** Sort by the duration of the item. Supports all items. */
    class ByDuration(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_DURATION

        override val itemId: Int
            get() = R.id.option_sort_duration

        override fun songsInPlace(songs: MutableList<Song>) {
            songs.sortWith(
                MultiComparator(
                    compareByDynamic { it.durationSecs }, compareBy(NameComparator()) { it }))
        }

        override fun albumsInPlace(albums: MutableList<Album>) {
            albums.sortWith(
                MultiComparator(
                    compareByDynamic { it.durationSecs }, compareBy(NameComparator()) { it }))
        }

        override fun artistsInPlace(artists: MutableList<Artist>) {
            artists.sortWith(
                MultiComparator(
                    compareByDynamic { it.durationSecs }, compareBy(NameComparator()) { it }))
        }

        override fun genresInPlace(genres: MutableList<Genre>) {
            genres.sortWith(
                MultiComparator(
                    compareByDynamic { it.durationSecs }, compareBy(NameComparator()) { it }))
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByDuration(newIsAscending)
        }
    }

    /** Sort by the amount of songs. Only applicable to music parents. */
    class ByCount(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_COUNT

        override val itemId: Int
            get() = R.id.option_sort_count

        override fun albumsInPlace(albums: MutableList<Album>) {
            albums.sortWith(
                MultiComparator(
                    compareByDynamic { it.songs.size }, compareBy(NameComparator()) { it }))
        }

        override fun artistsInPlace(artists: MutableList<Artist>) {
            artists.sortWith(
                MultiComparator(
                    compareByDynamic { it.songs.size }, compareBy(NameComparator()) { it }))
        }

        override fun genresInPlace(genres: MutableList<Genre>) {
            genres.sortWith(
                MultiComparator(
                    compareByDynamic { it.songs.size }, compareBy(NameComparator()) { it }))
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByCount(newIsAscending)
        }
    }

    /**
     * Sort by the disc, and then track number of an item. Only supported by [Song]. Do not use this
     * in a main sorting view, as it is not assigned to a particular item ID
     */
    class ByDisc(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_DISC

        // Not an available option, so no ID is set
        override val itemId: Int
            get() = R.id.option_sort_disc

        override fun songsInPlace(songs: MutableList<Song>) {
            songs.sortWith(
                MultiComparator(
                    compareByDynamic(NullableComparator()) { it.disc },
                    compareBy(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByDisc(newIsAscending)
        }
    }

    /**
     * Sort by the disc, and then track number of an item. Only supported by [Song]. Do not use this
     * in a main sorting view, as it is not assigned to a particular item ID
     */
    class ByTrack(override val isAscending: Boolean) : Sort(isAscending) {
        override val sortIntCode: Int
            get() = IntegerTable.SORT_BY_TRACK

        override val itemId: Int
            get() = R.id.option_sort_track

        override fun songsInPlace(songs: MutableList<Song>) {
            songs.sortWith(
                MultiComparator(
                    compareBy(NullableComparator()) { it.disc },
                    compareByDynamic(NullableComparator()) { it.track },
                    compareBy(NameComparator()) { it }))
        }

        override fun ascending(newIsAscending: Boolean): Sort {
            return ByTrack(newIsAscending)
        }
    }

    val intCode: Int
        get() = sortIntCode.shl(1) or if (isAscending) 1 else 0

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
            R.id.option_sort_duration -> ByDuration(isAscending)
            R.id.option_sort_count -> ByCount(isAscending)
            R.id.option_sort_disc -> ByDisc(isAscending)
            R.id.option_sort_track -> ByTrack(isAscending)
            else -> null
        }
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

    protected inline fun <T : Music, K : Comparable<K>> compareByDynamic(
        crossinline selector: (T) -> K
    ): Comparator<T> {
        return if (isAscending) {
            compareBy(selector)
        } else {
            compareByDescending(selector)
        }
    }

    class NameComparator<T : Music> : Comparator<T> {
        override fun compare(a: T, b: T): Int {
            val aSortName = a.sortName
            val bSortName = b.sortName
            return when {
                aSortName != null && bSortName != null ->
                    aSortName.compareTo(bSortName, ignoreCase = true)
                aSortName == null && bSortName != null -> -1 // a < b
                aSortName == null && bSortName == null -> 0 // a = b
                aSortName != null && bSortName == null -> 1 // a < b
                else -> error("Unreachable")
            }
        }
    }

    class NullableComparator<T : Comparable<T>> : Comparator<T?> {
        override fun compare(a: T?, b: T?): Int {
            return when {
                a != null && b != null -> a.compareTo(b)
                a == null && b != null -> -1 // a < b
                a == null && b == null -> 0 // a = b
                a != null && b == null -> 1 // a < b
                else -> error("Unreachable")
            }
        }
    }

    /**
     * Chains the given comparators together to form one comparator.
     *
     * Sorts often need to compare multiple things at once across several hierarchies, with this
     * class doing such in a more efficient manner than resorting at multiple intervals or grouping
     * items up. Comparators are checked from first to last, with the first comparator that returns
     * a non-equal result being propagated upwards.
     */
    class MultiComparator<T>(vararg comparators: Comparator<T>) : Comparator<T> {
        private val _comparators = comparators

        override fun compare(a: T?, b: T?): Int {
            for (comparator in _comparators) {
                val result = comparator.compare(a, b)
                if (result != 0) {
                    return result
                }
            }

            return 0
        }
    }

    companion object {
        /**
         * Convert a sort's integer representation into a [Sort] instance.
         *
         * @return A [Sort] instance, null if the data is malformed.
         */
        fun fromIntCode(value: Int): Sort? {
            val isAscending = (value and 1) == 1

            return when (value.shr(1)) {
                IntegerTable.SORT_BY_NAME -> ByName(isAscending)
                IntegerTable.SORT_BY_ARTIST -> ByArtist(isAscending)
                IntegerTable.SORT_BY_ALBUM -> ByAlbum(isAscending)
                IntegerTable.SORT_BY_YEAR -> ByYear(isAscending)
                IntegerTable.SORT_BY_DURATION -> ByDuration(isAscending)
                IntegerTable.SORT_BY_COUNT -> ByCount(isAscending)
                IntegerTable.SORT_BY_DISC -> ByDisc(isAscending)
                IntegerTable.SORT_BY_TRACK -> ByTrack(isAscending)
                else -> null
            }
        }
    }
}

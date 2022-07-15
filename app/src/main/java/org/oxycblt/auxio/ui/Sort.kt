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
import kotlin.UnsupportedOperationException
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Date
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song

/**
 * Represents the sort modes used in Auxio.
 *
 * Sorting can be done by Name, Artist, Album, and others. Sorting of names is always
 * case-insensitive and article-aware. Certain datatypes may only support a subset of sorts since
 * certain sorts cannot be easily applied to them (For Example, [Mode.ByArtist] and [Mode.ByYear] or
 * [Mode.ByAlbum]).
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
data class Sort(val mode: Mode, val isAscending: Boolean) {
    fun withAscending(new: Boolean) = Sort(mode, new)
    fun withMode(new: Mode) = Sort(new, isAscending)

    val intCode: Int
        get() = mode.intCode.shl(1) or if (isAscending) 1 else 0

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

    fun songsInPlace(songs: MutableList<Song>) {
        songs.sortWith(mode.getSongComparator(isAscending))
    }

    private fun albumsInPlace(albums: MutableList<Album>) {
        albums.sortWith(mode.getAlbumComparator(isAscending))
    }

    private fun artistsInPlace(artists: MutableList<Artist>) {
        artists.sortWith(mode.getArtistComparator(isAscending))
    }

    private fun genresInPlace(genres: MutableList<Genre>) {
        genres.sortWith(mode.getGenreComparator(isAscending))
    }

    sealed class Mode {
        abstract val intCode: Int
        abstract val itemId: Int

        open fun getSongComparator(ascending: Boolean): Comparator<Song> {
            throw UnsupportedOperationException()
        }

        open fun getAlbumComparator(ascending: Boolean): Comparator<Album> {
            throw UnsupportedOperationException()
        }

        open fun getArtistComparator(ascending: Boolean): Comparator<Artist> {
            throw UnsupportedOperationException()
        }

        open fun getGenreComparator(ascending: Boolean): Comparator<Genre> {
            throw UnsupportedOperationException()
        }

        /** Sort by the names of an item */
        object ByName : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_NAME

            override val itemId: Int
                get() = R.id.option_sort_name

            override fun getSongComparator(ascending: Boolean) =
                compareByDynamic(ascending, BasicComparator.SONG)

            override fun getAlbumComparator(ascending: Boolean) =
                compareByDynamic(ascending, BasicComparator.ALBUM)

            override fun getArtistComparator(ascending: Boolean) =
                compareByDynamic(ascending, BasicComparator.ARTIST)

            override fun getGenreComparator(ascending: Boolean) =
                compareByDynamic(ascending, BasicComparator.GENRE)
        }

        /** Sort by the album of an item, only supported by [Song] */
        object ByAlbum : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_ALBUM

            override val itemId: Int
                get() = R.id.option_sort_album

            override fun getSongComparator(ascending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(ascending, BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /** Sort by the artist of an item, only supported by [Album] and [Song] */
        object ByArtist : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_ARTIST

            override val itemId: Int
                get() = R.id.option_sort_artist

            override fun getSongComparator(ascending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(ascending, BasicComparator.ARTIST) { it.album.artist },
                    compareByDescending(NullableComparator.DATE) { it.album.date },
                    compareByDescending(BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(ascending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(ascending, BasicComparator.ARTIST) { it.artist },
                    compareByDescending(NullableComparator.DATE) { it.date },
                    compareBy(BasicComparator.ALBUM))
        }

        /** Sort by the year of an item, only supported by [Album] and [Song] */
        object ByYear : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_YEAR

            override val itemId: Int
                get() = R.id.option_sort_year

            override fun getSongComparator(ascending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(ascending, NullableComparator.DATE) { it.album.date },
                    compareByDescending(BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(ascending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(ascending, NullableComparator.DATE) { it.date },
                    compareBy(BasicComparator.ALBUM))
        }

        /** Sort by the duration of the item. Supports all items. */
        object ByDuration : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DURATION

            override val itemId: Int
                get() = R.id.option_sort_duration

            override fun getSongComparator(ascending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(ascending) { it.durationSecs },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(ascending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(ascending) { it.durationSecs },
                    compareBy(BasicComparator.ALBUM))

            override fun getArtistComparator(ascending: Boolean): Comparator<Artist> =
                MultiComparator(
                    compareByDynamic(ascending) { it.durationSecs },
                    compareBy(BasicComparator.ARTIST))

            override fun getGenreComparator(ascending: Boolean): Comparator<Genre> =
                MultiComparator(
                    compareByDynamic(ascending) { it.durationSecs },
                    compareBy(BasicComparator.GENRE))
        }

        /** Sort by the amount of songs. Only applicable to music parents. */
        object ByCount : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_COUNT

            override val itemId: Int
                get() = R.id.option_sort_count

            override fun getAlbumComparator(ascending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(ascending) { it.songs.size }, compareBy(BasicComparator.ALBUM))

            override fun getArtistComparator(ascending: Boolean): Comparator<Artist> =
                MultiComparator(
                    compareByDynamic(ascending) { it.songs.size },
                    compareBy(BasicComparator.ARTIST))

            override fun getGenreComparator(ascending: Boolean): Comparator<Genre> =
                MultiComparator(
                    compareByDynamic(ascending) { it.songs.size }, compareBy(BasicComparator.GENRE))
        }

        /** Sort by the disc, and then track number of an item. Only supported by [Song]. */
        object ByDisc : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DISC

            override val itemId: Int
                get() = R.id.option_sort_disc

            override fun getSongComparator(ascending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(ascending, NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /**
         * Sort by the disc, and then track number of an item. Only supported by [Song]. Do not use
         * this in a main sorting view, as it is not assigned to a particular item ID
         */
        object ByTrack : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_TRACK

            override val itemId: Int
                get() = R.id.option_sort_track

            override fun getSongComparator(ascending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareBy(NullableComparator.INT) { it.disc },
                    compareByDynamic(ascending, NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /** Sort by the time the item was added. Only supported by [Song] */
        object ByDateAdded : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DATE_ADDED

            override val itemId: Int
                get() = R.id.option_sort_date_added

            override fun getSongComparator(ascending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(ascending) { it.dateAdded }, compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(ascending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(ascending) { album -> album.songs.minOf { it.dateAdded } },
                    compareBy(BasicComparator.ALBUM))
        }

        protected inline fun <T : Music, K> compareByDynamic(
            ascending: Boolean,
            comparator: Comparator<in K>,
            crossinline selector: (T) -> K
        ) =
            if (ascending) {
                compareBy(comparator, selector)
            } else {
                compareByDescending(comparator, selector)
            }

        protected fun <T : Music> compareByDynamic(
            ascending: Boolean,
            comparator: Comparator<in T>
        ): Comparator<T> = compareByDynamic(ascending, comparator) { it }

        protected inline fun <T : Music, K : Comparable<K>> compareByDynamic(
            ascending: Boolean,
            crossinline selector: (T) -> K
        ) =
            if (ascending) {
                compareBy(selector)
            } else {
                compareByDescending(selector)
            }

        protected fun <T : Music> compareBy(comparator: Comparator<T>): Comparator<T> =
            compareBy(comparator) { it }

        /**
         * Chains the given comparators together to form one comparator.
         *
         * Sorts often need to compare multiple things at once across several hierarchies, with this
         * class doing such in a more efficient manner than resorting at multiple intervals or
         * grouping items up. Comparators are checked from first to last, with the first comparator
         * that returns a non-equal result being propagated upwards.
         */
        private class MultiComparator<T>(vararg comparators: Comparator<T>) : Comparator<T> {
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

        private class BasicComparator<T : Music> private constructor() : Comparator<T> {
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

            companion object {
                val SONG: Comparator<Song> = BasicComparator()
                val ALBUM: Comparator<Album> = BasicComparator()
                val ARTIST: Comparator<Artist> = BasicComparator()
                val GENRE: Comparator<Genre> = BasicComparator()
            }
        }

        class NullableComparator<T : Comparable<T>> private constructor() : Comparator<T?> {
            override fun compare(a: T?, b: T?) =
                when {
                    a != null && b != null -> a.compareTo(b)
                    a == null && b != null -> -1 // a < b
                    a == null && b == null -> 0 // a = b
                    a != null && b == null -> 1 // a < b
                    else -> error("Unreachable")
                }

            companion object {
                val INT = NullableComparator<Int>()
                val DATE = NullableComparator<Date>()
            }
        }

        companion object {
            fun fromItemId(@IdRes itemId: Int) =
                when (itemId) {
                    ByName.itemId -> ByName
                    ByAlbum.itemId -> ByAlbum
                    ByArtist.itemId -> ByArtist
                    ByYear.itemId -> ByYear
                    ByDuration.itemId -> ByDuration
                    ByCount.itemId -> ByCount
                    ByDisc.itemId -> ByDisc
                    ByTrack.itemId -> ByTrack
                    ByDateAdded.itemId -> ByDateAdded
                    else -> null
                }
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
            val mode =
                when (value.shr(1)) {
                    Mode.ByName.intCode -> Mode.ByName
                    Mode.ByArtist.intCode -> Mode.ByArtist
                    Mode.ByAlbum.intCode -> Mode.ByAlbum
                    Mode.ByYear.intCode -> Mode.ByYear
                    Mode.ByDuration.intCode -> Mode.ByDuration
                    Mode.ByCount.intCode -> Mode.ByCount
                    Mode.ByDisc.intCode -> Mode.ByDisc
                    Mode.ByTrack.intCode -> Mode.ByTrack
                    Mode.ByDateAdded.intCode -> Mode.ByDateAdded
                    else -> return null
                }

            return Sort(mode, isAscending)
        }
    }
}

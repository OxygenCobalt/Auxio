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
 
package org.oxycblt.auxio.music.library

import androidx.annotation.IdRes
import kotlin.math.max
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.library.Sort.Mode
import org.oxycblt.auxio.music.tags.Date

/**
 * A sorting method.
 *
 * This can be used not only to sort items, but also represent a sorting mode within the UI.
 *
 * @param mode A [Mode] dictating how to sort the list.
 * @param isAscending Whether to sort in ascending or descending order.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class Sort(val mode: Mode, val isAscending: Boolean) {
    /**
     * Create a new [Sort] with the same [mode], but different [isAscending] value.
     * @param isAscending Whether the new sort should be in ascending order or not.
     * @return A new sort with the same mode, but with the new [isAscending] value applied.
     */
    fun withAscending(isAscending: Boolean) = Sort(mode, isAscending)

    /**
     * Create a new [Sort] with the same [isAscending] value, but different [mode] value.
     * @param mode Tbe new mode to use for the Sort.
     * @return A new sort with the same [isAscending] value, but with the new [mode] applied.
     */
    fun withMode(mode: Mode) = Sort(mode, isAscending)

    /**
     * Sort a list of [Song]s.
     * @param songs The list of [Song]s.
     * @return A new list of [Song]s sorted by this [Sort]'s configuration.
     */
    fun songs(songs: Collection<Song>): List<Song> {
        val mutable = songs.toMutableList()
        songsInPlace(mutable)
        return mutable
    }

    /**
     * Sort a list of [Album]s.
     * @param albums The list of [Album]s.
     * @return A new list of [Album]s sorted by this [Sort]'s configuration.
     */
    fun albums(albums: Collection<Album>): List<Album> {
        val mutable = albums.toMutableList()
        albumsInPlace(mutable)
        return mutable
    }

    /**
     * Sort a list of [Artist]s.
     * @param artists The list of [Artist]s.
     * @return A new list of [Artist]s sorted by this [Sort]'s configuration.
     */
    fun artists(artists: Collection<Artist>): List<Artist> {
        val mutable = artists.toMutableList()
        artistsInPlace(mutable)
        return mutable
    }

    /**
     * Sort a list of [Genre]s.
     * @param genres The list of [Genre]s.
     * @return A new list of [Genre]s sorted by this [Sort]'s configuration.
     */
    fun genres(genres: Collection<Genre>): List<Genre> {
        val mutable = genres.toMutableList()
        genresInPlace(mutable)
        return mutable
    }

    /**
     * Sort a *mutable* list of [Song]s in-place using this [Sort]'s configuration.
     * @param songs The [Song]s to sort.
     */
    private fun songsInPlace(songs: MutableList<Song>) {
        songs.sortWith(mode.getSongComparator(isAscending))
    }

    /**
     * Sort a *mutable* list of [Album]s in-place using this [Sort]'s configuration.
     * @param albums The [Album]s to sort.
     */
    private fun albumsInPlace(albums: MutableList<Album>) {
        albums.sortWith(mode.getAlbumComparator(isAscending))
    }

    /**
     * Sort a *mutable* list of [Artist]s in-place using this [Sort]'s configuration.
     * @param artists The [Album]s to sort.
     */
    private fun artistsInPlace(artists: MutableList<Artist>) {
        artists.sortWith(mode.getArtistComparator(isAscending))
    }

    /**
     * Sort a *mutable* list of [Genre]s in-place using this [Sort]'s configuration.
     * @param genres The [Genre]s to sort.
     */
    private fun genresInPlace(genres: MutableList<Genre>) {
        genres.sortWith(mode.getGenreComparator(isAscending))
    }

    /**
     * The integer representation of this instance.
     * @see fromIntCode
     */
    val intCode: Int
        // Sort's integer representation is formatted as AMMMM, where A is a bitflag
        // representing if the sort is in ascending or descending order, and M is the
        // integer representation of the sort mode.
        get() = mode.intCode.shl(1) or if (isAscending) 1 else 0

    sealed class Mode {
        /** The integer representation of this sort mode. */
        abstract val intCode: Int
        /** The item ID of this sort mode in menu resources. */
        abstract val itemId: Int

        /**
         * Get a [Comparator] that sorts [Song]s according to this [Mode].
         * @param isAscending Whether to sort in ascending or descending order.
         * @return A [Comparator] that can be used to sort a [Song] list according to this [Mode].
         */
        open fun getSongComparator(isAscending: Boolean): Comparator<Song> {
            throw UnsupportedOperationException()
        }

        /**
         * Get a [Comparator] that sorts [Album]s according to this [Mode].
         * @param isAscending Whether to sort in ascending or descending order.
         * @return A [Comparator] that can be used to sort a [Album] list according to this [Mode].
         */
        open fun getAlbumComparator(isAscending: Boolean): Comparator<Album> {
            throw UnsupportedOperationException()
        }

        /**
         * Return a [Comparator] that sorts [Artist]s according to this [Mode].
         * @param isAscending Whether to sort in ascending or descending order.
         * @return A [Comparator] that can be used to sort a [Artist] list according to this [Mode].
         */
        open fun getArtistComparator(isAscending: Boolean): Comparator<Artist> {
            throw UnsupportedOperationException()
        }

        /**
         * Return a [Comparator] that sorts [Genre]s according to this [Mode].
         * @param isAscending Whether to sort in ascending or descending order.
         * @return A [Comparator] that can be used to sort a [Genre] list according to this [Mode].
         */
        open fun getGenreComparator(isAscending: Boolean): Comparator<Genre> {
            throw UnsupportedOperationException()
        }

        /**
         * Sort by the item's name.
         * @see Music.collationKey
         */
        object ByName : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_NAME

            override val itemId: Int
                get() = R.id.option_sort_name

            override fun getSongComparator(isAscending: Boolean) =
                compareByDynamic(isAscending, BasicComparator.SONG)

            override fun getAlbumComparator(isAscending: Boolean) =
                compareByDynamic(isAscending, BasicComparator.ALBUM)

            override fun getArtistComparator(isAscending: Boolean) =
                compareByDynamic(isAscending, BasicComparator.ARTIST)

            override fun getGenreComparator(isAscending: Boolean) =
                compareByDynamic(isAscending, BasicComparator.GENRE)
        }

        /**
         * Sort by the [Album] of an item. Only available for [Song]s.
         * @see Album.collationKey
         */
        object ByAlbum : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_ALBUM

            override val itemId: Int
                get() = R.id.option_sort_album

            override fun getSongComparator(isAscending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(isAscending, BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /**
         * Sort by the [Artist] name of an item. Only available for [Song] and [Album].
         * @see Artist.collationKey
         */
        object ByArtist : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_ARTIST

            override val itemId: Int
                get() = R.id.option_sort_artist

            override fun getSongComparator(isAscending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(isAscending, ListComparator.ARTISTS) { it.artists },
                    compareByDescending(NullableComparator.DATE_RANGE) { it.album.dates },
                    compareByDescending(BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(isAscending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(isAscending, ListComparator.ARTISTS) { it.artists },
                    compareByDescending(NullableComparator.DATE_RANGE) { it.dates },
                    compareBy(BasicComparator.ALBUM))
        }

        /**
         * Sort by the [Date] of an item. Only available for [Song] and [Album].
         * @see Song.date
         * @see Album.dates
         */
        object ByDate : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_YEAR

            override val itemId: Int
                get() = R.id.option_sort_year

            override fun getSongComparator(isAscending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(isAscending, NullableComparator.DATE_RANGE) { it.album.dates },
                    compareByDescending(BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(isAscending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(isAscending, NullableComparator.DATE_RANGE) { it.dates },
                    compareBy(BasicComparator.ALBUM))
        }

        /** Sort by the duration of an item. */
        object ByDuration : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DURATION

            override val itemId: Int
                get() = R.id.option_sort_duration

            override fun getSongComparator(isAscending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(isAscending) { it.durationMs },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(isAscending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(isAscending) { it.durationMs },
                    compareBy(BasicComparator.ALBUM))

            override fun getArtistComparator(isAscending: Boolean): Comparator<Artist> =
                MultiComparator(
                    compareByDynamic(isAscending, NullableComparator.LONG) { it.durationMs },
                    compareBy(BasicComparator.ARTIST))

            override fun getGenreComparator(isAscending: Boolean): Comparator<Genre> =
                MultiComparator(
                    compareByDynamic(isAscending) { it.durationMs },
                    compareBy(BasicComparator.GENRE))
        }

        /**
         * Sort by the amount of songs an item contains. Only available for [MusicParent]s.
         * @see MusicParent.songs
         */
        object ByCount : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_COUNT

            override val itemId: Int
                get() = R.id.option_sort_count

            override fun getAlbumComparator(isAscending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(isAscending) { it.songs.size },
                    compareBy(BasicComparator.ALBUM))

            override fun getArtistComparator(isAscending: Boolean): Comparator<Artist> =
                MultiComparator(
                    compareByDynamic(isAscending, NullableComparator.INT) { it.songs.size },
                    compareBy(BasicComparator.ARTIST))

            override fun getGenreComparator(isAscending: Boolean): Comparator<Genre> =
                MultiComparator(
                    compareByDynamic(isAscending) { it.songs.size },
                    compareBy(BasicComparator.GENRE))
        }

        /**
         * Sort by the disc number of an item. Only available for [Song]s.
         * @see Song.disc
         */
        object ByDisc : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DISC

            override val itemId: Int
                get() = R.id.option_sort_disc

            override fun getSongComparator(isAscending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(isAscending, NullableComparator.INT) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /**
         * Sort by the track number of an item. Only available for [Song]s.
         * @see Song.track
         */
        object ByTrack : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_TRACK

            override val itemId: Int
                get() = R.id.option_sort_track

            override fun getSongComparator(isAscending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareBy(NullableComparator.INT) { it.disc },
                    compareByDynamic(isAscending, NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /**
         * Sort by the date an item was added. Only supported by [Song]s and [Album]s.
         * @see Song.dateAdded
         * @see Album.dates
         */
        object ByDateAdded : Mode() {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DATE_ADDED

            override val itemId: Int
                get() = R.id.option_sort_date_added

            override fun getSongComparator(isAscending: Boolean): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(isAscending) { it.dateAdded }, compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(isAscending: Boolean): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(isAscending) { album -> album.dateAdded },
                    compareBy(BasicComparator.ALBUM))
        }

        /**
         * Utility function to create a [Comparator] in a dynamic way determined by [isAscending].
         * @param isAscending Whether to sort in ascending or descending order.
         * @see compareBy
         * @see compareByDescending
         */
        protected inline fun <T : Music, K : Comparable<K>> compareByDynamic(
            isAscending: Boolean,
            crossinline selector: (T) -> K
        ) =
            if (isAscending) {
                compareBy(selector)
            } else {
                compareByDescending(selector)
            }

        /**
         * Utility function to create a [Comparator] in a dynamic way determined by [isAscending]
         * @param isAscending Whether to sort in ascending or descending order.
         * @param comparator A [Comparator] to wrap.
         * @return A new [Comparator] with the specified configuration.
         * @see compareBy
         * @see compareByDescending
         */
        protected fun <T : Music> compareByDynamic(
            isAscending: Boolean,
            comparator: Comparator<in T>
        ): Comparator<T> = compareByDynamic(isAscending, comparator) { it }

        /**
         * Utility function to create a [Comparator] a dynamic way determined by [isAscending]
         * @param isAscending Whether to sort in ascending or descending order.
         * @param comparator A [Comparator] to wrap.
         * @param selector Called to obtain a specific attribute to sort by.
         * @return A new [Comparator] with the specified configuration.
         * @see compareBy
         * @see compareByDescending
         */
        protected inline fun <T : Music, K> compareByDynamic(
            isAscending: Boolean,
            comparator: Comparator<in K>,
            crossinline selector: (T) -> K
        ) =
            if (isAscending) {
                compareBy(comparator, selector)
            } else {
                compareByDescending(comparator, selector)
            }

        /**
         * Utility function to create a [Comparator] that sorts in ascending order based on the
         * given [Comparator], with a selector based on the item itself.
         * @param comparator The [Comparator] to wrap.
         * @return A new [Comparator] with the specified configuration.
         * @see compareBy
         */
        protected fun <T : Music> compareBy(comparator: Comparator<T>): Comparator<T> =
            compareBy(comparator) { it }

        /**
         * A [Comparator] that chains several other [Comparator]s together to form one comparison.
         * @param comparators The [Comparator]s to chain. These will be iterated through in order
         * during a comparison, with the first non-equal result becoming the result.
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

        /**
         * Wraps a [Comparator], extending it to compare two lists.
         * @param inner The [Comparator] to use.
         */
        private class ListComparator<T>(private val inner: Comparator<T>) : Comparator<List<T>> {
            override fun compare(a: List<T>, b: List<T>): Int {
                for (i in 0 until max(a.size, b.size)) {
                    val ai = a.getOrNull(i)
                    val bi = b.getOrNull(i)
                    when {
                        ai != null && bi != null -> {
                            val result = inner.compare(ai, bi)
                            if (result != 0) {
                                return result
                            }
                        }
                        ai == null && bi != null -> return -1 // a < b
                        ai == null && bi == null -> return 0 // a = b
                        else -> return 1 // a < b
                    }
                }

                return 0
            }

            companion object {
                /** A re-usable configured for [Artist]s.. */
                val ARTISTS: Comparator<List<Artist>> = ListComparator(BasicComparator.ARTIST)
            }
        }

        /**
         * A [Comparator] that compares abstract [Music] values. Internally, this is similar to
         * [NullableComparator], however comparing [Music.collationKey] instead of [Comparable].
         * @see NullableComparator
         * @see Music.collationKey
         */
        private class BasicComparator<T : Music> private constructor() : Comparator<T> {
            override fun compare(a: T, b: T): Int {
                val aKey = a.collationKey
                val bKey = b.collationKey
                return when {
                    aKey != null && bKey != null -> aKey.compareTo(bKey)
                    aKey == null && bKey != null -> -1 // a < b
                    aKey == null && bKey == null -> 0 // a = b
                    else -> 1 // a < b
                }
            }

            companion object {
                /** A re-usable instance configured for [Song]s. */
                val SONG: Comparator<Song> = BasicComparator()
                /** A re-usable instance configured for [Album]s. */
                val ALBUM: Comparator<Album> = BasicComparator()
                /** A re-usable instance configured for [Artist]s. */
                val ARTIST: Comparator<Artist> = BasicComparator()
                /** A re-usable instance configured for [Genre]s. */
                val GENRE: Comparator<Genre> = BasicComparator()
            }
        }

        /**
         * A [Comparator] that compares two possibly null values. Values will be considered lesser
         * if they are null, and greater if they are non-null.
         */
        private class NullableComparator<T : Comparable<T>> private constructor() : Comparator<T?> {
            override fun compare(a: T?, b: T?) =
                when {
                    a != null && b != null -> a.compareTo(b)
                    a == null && b != null -> -1 // a < b
                    a == null && b == null -> 0 // a = b
                    else -> 1 // a < b
                }

            companion object {
                /** A re-usable instance configured for [Int]s. */
                val INT = NullableComparator<Int>()
                /** A re-usable instance configured for [Long]s. */
                val LONG = NullableComparator<Long>()
                /** A re-usable instance configured for [Date.Range]s. */
                val DATE_RANGE = NullableComparator<Date.Range>()
            }
        }

        companion object {
            /**
             * Convert a [Mode] integer representation into an instance.
             * @param intCode An integer representation of a [Mode]
             * @return The corresponding [Mode], or null if the [Mode] is invalid.
             * @see intCode
             */
            fun fromIntCode(intCode: Int) =
                when (intCode) {
                    ByName.intCode -> ByName
                    ByArtist.intCode -> ByArtist
                    ByAlbum.intCode -> ByAlbum
                    ByDate.intCode -> ByDate
                    ByDuration.intCode -> ByDuration
                    ByCount.intCode -> ByCount
                    ByDisc.intCode -> ByDisc
                    ByTrack.intCode -> ByTrack
                    ByDateAdded.intCode -> ByDateAdded
                    else -> null
                }

            /**
             * Convert a menu item ID into a [Mode].
             * @param itemId The menu resource ID to convert
             * @return A [Mode] corresponding to the given ID, or null if the ID is invalid.
             * @see itemId
             */
            fun fromItemId(@IdRes itemId: Int) =
                when (itemId) {
                    ByName.itemId -> ByName
                    ByAlbum.itemId -> ByAlbum
                    ByArtist.itemId -> ByArtist
                    ByDate.itemId -> ByDate
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
         * Convert a [Sort] integer representation into an instance.
         * @param intCode An integer representation of a [Sort]
         * @return The corresponding [Sort], or null if the [Sort] is invalid.
         * @see intCode
         */
        fun fromIntCode(intCode: Int): Sort? {
            // Sort's integer representation is formatted as AMMMM, where A is a bitflag
            // representing on if the mode is ascending or descending, and M is the integer
            // representation of the sort mode.
            val isAscending = (intCode and 1) == 1
            val mode = Mode.fromIntCode(intCode.shr(1)) ?: return null
            return Sort(mode, isAscending)
        }
    }
}

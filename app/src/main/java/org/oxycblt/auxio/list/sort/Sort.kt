/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.list.sort

import kotlin.math.max
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.info.Disc

/**
 * A sorting method.
 *
 * This can be used not only to sort items, but also represent a sorting mode within the UI.
 *
 * @param mode A [Mode] dictating how to sort the list.
 * @param direction The [Direction] to sort in.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class Sort(val mode: Mode, val direction: Direction) {
    /**
     * Sort a list of [Song]s.
     *
     * @param songs The list of [Song]s.
     * @return A new list of [Song]s sorted by this [Sort]'s configuration.
     */
    fun <T : Song> songs(songs: Collection<T>): List<T> {
        val mutable = songs.toMutableList()
        songsInPlace(mutable)
        return mutable
    }

    /**
     * Sort a list of [Album]s.
     *
     * @param albums The list of [Album]s.
     * @return A new list of [Album]s sorted by this [Sort]'s configuration.
     */
    fun <T : Album> albums(albums: Collection<T>): List<T> {
        val mutable = albums.toMutableList()
        albumsInPlace(mutable)
        return mutable
    }

    /**
     * Sort a list of [Artist]s.
     *
     * @param artists The list of [Artist]s.
     * @return A new list of [Artist]s sorted by this [Sort]'s configuration.
     */
    fun <T : Artist> artists(artists: Collection<T>): List<T> {
        val mutable = artists.toMutableList()
        artistsInPlace(mutable)
        return mutable
    }

    /**
     * Sort a list of [Genre]s.
     *
     * @param genres The list of [Genre]s.
     * @return A new list of [Genre]s sorted by this [Sort]'s configuration.
     */
    fun <T : Genre> genres(genres: Collection<T>): List<T> {
        val mutable = genres.toMutableList()
        genresInPlace(mutable)
        return mutable
    }

    /**
     * Sort a list of [Playlist]s.
     *
     * @param playlists The list of [Playlist]s.
     * @return A new list of [Playlist]s sorted by this [Sort]'s configuration
     */
    fun <T : Playlist> playlists(playlists: Collection<T>): List<T> {
        val mutable = playlists.toMutableList()
        playlistsInPlace(mutable)
        return mutable
    }

    private fun songsInPlace(songs: MutableList<out Song>) {
        val comparator = mode.getSongComparator(direction) ?: return
        songs.sortWith(comparator)
    }

    private fun albumsInPlace(albums: MutableList<out Album>) {
        val comparator = mode.getAlbumComparator(direction) ?: return
        albums.sortWith(comparator)
    }

    private fun artistsInPlace(artists: MutableList<out Artist>) {
        val comparator = mode.getArtistComparator(direction) ?: return
        artists.sortWith(comparator)
    }

    private fun genresInPlace(genres: MutableList<out Genre>) {
        val comparator = mode.getGenreComparator(direction) ?: return
        genres.sortWith(comparator)
    }

    private fun playlistsInPlace(playlists: MutableList<out Playlist>) {
        val comparator = mode.getPlaylistComparator(direction) ?: return
        playlists.sortWith(comparator)
    }

    /**
     * The integer representation of this instance.
     *
     * @see fromIntCode
     */
    val intCode: Int
        // Sort's integer representation is formatted as AMMMM, where A is a bitflag
        // representing if the sort is in ascending or descending order, and M is the
        // integer representation of the sort mode.
        get() =
            mode.intCode.shl(1) or
                when (direction) {
                    Direction.ASCENDING -> 1
                    Direction.DESCENDING -> 0
                }

    /** Describes the type of data to sort with. */
    sealed interface Mode {
        /** The integer representation of this sort mode. */
        val intCode: Int
        /** The string resource of the human-readable name of this sort mode. */
        val stringRes: Int

        /**
         * Get a [Comparator] that sorts [Song]s according to this [Mode].
         *
         * @param direction The direction to sort in.
         * @return A [Comparator] that can be used to sort a [Song] list according to this [Mode],
         *   or null to not sort at all.
         */
        fun getSongComparator(direction: Direction): Comparator<Song>? = null

        /**
         * Get a [Comparator] that sorts [Album]s according to this [Mode].
         *
         * @param direction The direction to sort in.
         * @return A [Comparator] that can be used to sort a [Album] list according to this [Mode],
         *   or null to not sort at all.
         */
        fun getAlbumComparator(direction: Direction): Comparator<Album>? = null

        /**
         * Return a [Comparator] that sorts [Artist]s according to this [Mode].
         *
         * @param direction The direction to sort in.
         * @return A [Comparator] that can be used to sort a [Artist] list according to this [Mode].
         *   or null to not sort at all.
         */
        fun getArtistComparator(direction: Direction): Comparator<Artist>? = null

        /**
         * Return a [Comparator] that sorts [Genre]s according to this [Mode].
         *
         * @param direction The direction to sort in.
         * @return A [Comparator] that can be used to sort a [Genre] list according to this [Mode].
         *   or null to not sort at all.
         */
        fun getGenreComparator(direction: Direction): Comparator<Genre>? = null

        /**
         * Return a [Comparator] that sorts [Playlist]s according to this [Mode].
         *
         * @param direction The direction to sort in.
         * @return A [Comparator] that can be used to sort a [Genre] list according to this [Mode].
         *   or null to not sort at all.
         */
        fun getPlaylistComparator(direction: Direction): Comparator<Playlist>? = null

        /**
         * Sort by the item's name.
         *
         * @see Music.name
         */
        data object ByName : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_NAME

            override val stringRes: Int
                get() = R.string.lbl_name

            override fun getSongComparator(direction: Direction) =
                compareByDynamic(direction, BasicComparator.SONG)

            override fun getAlbumComparator(direction: Direction) =
                compareByDynamic(direction, BasicComparator.ALBUM)

            override fun getArtistComparator(direction: Direction) =
                compareByDynamic(direction, BasicComparator.ARTIST)

            override fun getGenreComparator(direction: Direction) =
                compareByDynamic(direction, BasicComparator.GENRE)

            override fun getPlaylistComparator(direction: Direction) =
                compareByDynamic(direction, BasicComparator.PLAYLIST)
        }

        /**
         * Sort by the [Album] of an item. Only available for [Song]s.
         *
         * @see Album.name
         */
        data object ByAlbum : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_ALBUM

            override val stringRes: Int
                get() = R.string.lbl_album

            override fun getSongComparator(direction: Direction): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(direction, BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.DISC) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /**
         * Sort by the [Artist] name of an item. Only available for [Song] and [Album].
         *
         * @see Artist.name
         */
        data object ByArtist : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_ARTIST

            override val stringRes: Int
                get() = R.string.lbl_artist

            override fun getSongComparator(direction: Direction): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(direction, ListComparator.ARTISTS) { it.artists },
                    compareByDescending(NullableComparator.DATE_RANGE) { it.album.dates },
                    compareByDescending(BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.DISC) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(direction: Direction): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(direction, ListComparator.ARTISTS) { it.artists },
                    compareByDescending(NullableComparator.DATE_RANGE) { it.dates },
                    compareBy(BasicComparator.ALBUM))
        }

        /**
         * Sort by the [Date] of an item. Only available for [Song] and [Album].
         *
         * @see Song.date
         * @see Album.dates
         */
        data object ByDate : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_YEAR

            override val stringRes: Int
                get() = R.string.lbl_date

            override fun getSongComparator(direction: Direction): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(direction, NullableComparator.DATE_RANGE) { it.album.dates },
                    compareByDescending(BasicComparator.ALBUM) { it.album },
                    compareBy(NullableComparator.DISC) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(direction: Direction): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(direction, NullableComparator.DATE_RANGE) { it.dates },
                    compareBy(BasicComparator.ALBUM))
        }

        /** Sort by the duration of an item. */
        data object ByDuration : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DURATION

            override val stringRes: Int
                get() = R.string.lbl_duration

            override fun getSongComparator(direction: Direction): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(direction) { it.durationMs }, compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(direction: Direction): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(direction) { it.durationMs }, compareBy(BasicComparator.ALBUM))

            override fun getArtistComparator(direction: Direction): Comparator<Artist> =
                MultiComparator(
                    compareByDynamic(direction, NullableComparator.LONG) { it.durationMs },
                    compareBy(BasicComparator.ARTIST))

            override fun getGenreComparator(direction: Direction): Comparator<Genre> =
                MultiComparator(
                    compareByDynamic(direction) { it.durationMs }, compareBy(BasicComparator.GENRE))

            override fun getPlaylistComparator(direction: Direction): Comparator<Playlist> =
                MultiComparator(
                    compareByDynamic(direction) { it.durationMs },
                    compareBy(BasicComparator.PLAYLIST))
        }

        /** Sort by the amount of songs an item contains. Only available for MusicParents. */
        data object ByCount : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_COUNT

            override val stringRes: Int
                get() = R.string.lbl_song_count

            override fun getAlbumComparator(direction: Direction): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(direction) { it.songs.size }, compareBy(BasicComparator.ALBUM))

            override fun getArtistComparator(direction: Direction): Comparator<Artist> =
                MultiComparator(
                    compareByDynamic(direction, NullableComparator.INT) { it.songs.size },
                    compareBy(BasicComparator.ARTIST))

            override fun getGenreComparator(direction: Direction): Comparator<Genre> =
                MultiComparator(
                    compareByDynamic(direction) { it.songs.size }, compareBy(BasicComparator.GENRE))

            override fun getPlaylistComparator(direction: Direction): Comparator<Playlist> =
                MultiComparator(
                    compareByDynamic(direction) { it.songs.size },
                    compareBy(BasicComparator.PLAYLIST))
        }

        /**
         * Sort by the disc number of an item. Only available for [Song]s.
         *
         * @see Song.disc
         */
        data object ByDisc : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DISC

            override val stringRes: Int
                get() = R.string.lbl_disc

            override fun getSongComparator(direction: Direction): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(direction, NullableComparator.DISC) { it.disc },
                    compareBy(NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /**
         * Sort by the track number of an item. Only available for [Song]s.
         *
         * @see Song.track
         */
        data object ByTrack : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_TRACK

            override val stringRes: Int
                get() = R.string.lbl_track

            override fun getSongComparator(direction: Direction): Comparator<Song> =
                MultiComparator(
                    compareBy(NullableComparator.DISC) { it.disc },
                    compareByDynamic(direction, NullableComparator.INT) { it.track },
                    compareBy(BasicComparator.SONG))
        }

        /**
         * Sort by the date an item was added. Only supported by [Song]s and [Album]s.
         *
         * @see Song.dateAdded
         * @see Album.dates
         */
        data object ByDateAdded : Mode {
            override val intCode: Int
                get() = IntegerTable.SORT_BY_DATE_ADDED

            override val stringRes: Int
                get() = R.string.lbl_date_added

            override fun getSongComparator(direction: Direction): Comparator<Song> =
                MultiComparator(
                    compareByDynamic(direction) { it.dateAdded }, compareBy(BasicComparator.SONG))

            override fun getAlbumComparator(direction: Direction): Comparator<Album> =
                MultiComparator(
                    compareByDynamic(direction) { album -> album.dateAdded },
                    compareBy(BasicComparator.ALBUM))
        }

        companion object {
            /**
             * Convert a [Mode] integer representation into an instance.
             *
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
        }
    }

    /** The direction to sort items in. */
    enum class Direction {
        ASCENDING,
        DESCENDING
    }

    companion object {
        /**
         * Convert a [Sort] integer representation into an instance.
         *
         * @param intCode An integer representation of a [Sort]
         * @return The corresponding [Sort], or null if the [Sort] is invalid.
         * @see intCode
         */
        fun fromIntCode(intCode: Int): Sort? {
            // Sort's integer representation is formatted as AMMMM, where A is a bitflag
            // representing on if the mode is ascending or descending, and M is the integer
            // representation of the sort mode.
            val direction = if ((intCode and 1) == 1) Direction.ASCENDING else Direction.DESCENDING
            val mode = Mode.fromIntCode(intCode.shr(1)) ?: return null
            return Sort(mode, direction)
        }
    }
}

/**
 * Utility function to create a [Comparator] in a dynamic way determined by [direction].
 *
 * @param direction The [Sort.Direction] to sort in.
 * @see compareBy
 * @see compareByDescending
 */
private inline fun <T : Music, K : Comparable<K>> compareByDynamic(
    direction: Sort.Direction,
    crossinline selector: (T) -> K
) =
    when (direction) {
        Sort.Direction.ASCENDING -> compareBy(selector)
        Sort.Direction.DESCENDING -> compareByDescending(selector)
    }

/**
 * Utility function to create a [Comparator] in a dynamic way determined by [direction]
 *
 * @param direction The [Sort.Direction] to sort in.
 * @param comparator A [Comparator] to wrap.
 * @return A new [Comparator] with the specified configuration.
 * @see compareBy
 * @see compareByDescending
 */
private fun <T : Music> compareByDynamic(
    direction: Sort.Direction,
    comparator: Comparator<in T>
): Comparator<T> = compareByDynamic(direction, comparator) { it }

/**
 * Utility function to create a [Comparator] a dynamic way determined by [direction]
 *
 * @param direction The [Sort.Direction] to sort in.
 * @param comparator A [Comparator] to wrap.
 * @param selector Called to obtain a specific attribute to sort by.
 * @return A new [Comparator] with the specified configuration.
 * @see compareBy
 * @see compareByDescending
 */
private inline fun <T : Music, K> compareByDynamic(
    direction: Sort.Direction,
    comparator: Comparator<in K>,
    crossinline selector: (T) -> K
) =
    when (direction) {
        Sort.Direction.ASCENDING -> compareBy(comparator, selector)
        Sort.Direction.DESCENDING -> compareByDescending(comparator, selector)
    }

/**
 * Utility function to create a [Comparator] that sorts in ascending order based on the given
 * [Comparator], with a selector based on the item itself.
 *
 * @param comparator The [Comparator] to wrap.
 * @return A new [Comparator] with the specified configuration.
 * @see compareBy
 */
private fun <T : Music> compareBy(comparator: Comparator<T>): Comparator<T> =
    compareBy(comparator) { it }

/**
 * A [Comparator] that chains several other [Comparator]s together to form one comparison.
 *
 * @param comparators The [Comparator]s to chain. These will be iterated through in order during a
 *   comparison, with the first non-equal result becoming the result.
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
 *
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
 * [NullableComparator], however comparing [Music.name] instead of [Comparable].
 *
 * @see NullableComparator
 * @see Music.name
 */
private class BasicComparator<T : Music> private constructor() : Comparator<T> {
    override fun compare(a: T, b: T) = a.name.compareTo(b.name)

    companion object {
        /** A re-usable instance configured for [Song]s. */
        val SONG: Comparator<Song> = BasicComparator()
        /** A re-usable instance configured for [Album]s. */
        val ALBUM: Comparator<Album> = BasicComparator()
        /** A re-usable instance configured for [Artist]s. */
        val ARTIST: Comparator<Artist> = BasicComparator()
        /** A re-usable instance configured for [Genre]s. */
        val GENRE: Comparator<Genre> = BasicComparator()
        /** A re-usable instance configured for [Playlist]s. */
        val PLAYLIST: Comparator<Playlist> = BasicComparator()
    }
}

/**
 * A [Comparator] that compares two possibly null values. Values will be considered lesser if they
 * are null, and greater if they are non-null.
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
        /** A re-usable instance configured for [Disc]s */
        val DISC = NullableComparator<Disc>()
        /** A re-usable instance configured for [Date.Range]s. */
        val DATE_RANGE = NullableComparator<Date.Range>()
    }
}

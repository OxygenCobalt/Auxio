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

import androidx.annotation.IdRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song

/**
 * The enum for the current sort state.
 * This enum is semantic depending on the context it is used. Documentation describing each
 * sorting functions behavior can be found in the function definition.
 * @param itemId Menu ID associated with this enum
 * @author OxygenCobalt
 */
enum class SortMode(@IdRes val itemId: Int) {
    ASCENDING(R.id.option_sort_asc),
    DESCENDING(R.id.option_sort_dsc),
    ARTIST(R.id.option_sort_artist),
    ALBUM(R.id.option_sort_album),
    YEAR(R.id.option_sort_year);

    /**
     * Sort a list of songs.
     *
     * **Behavior:**
     * - [ASCENDING]: By name after article, ascending
     * - [DESCENDING]: By name after article, descending
     * - [ARTIST]: Grouped by album and then sorted [ASCENDING] based off the artist name.
     * - [ALBUM]: Grouped by album and sorted [ASCENDING]
     * - [YEAR]: Grouped by album and sorted by year
     *
     * The grouping mode for songs in an album will be by track, [ASCENDING].
     * @see sortAlbums
     */
    fun sortSongs(songs: Collection<Song>): List<Song> {
        return when (this) {
            ASCENDING -> songs.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { song ->
                    song.name.sliceArticle()
                }
            )

            DESCENDING -> songs.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { song ->
                    song.name.sliceArticle()
                }
            )

            else -> sortAlbums(songs.groupBy { it.album }.keys).flatMap { album ->
                ASCENDING.sortAlbum(album)
            }
        }
    }

    /**
     * Sort a list of albums.
     *
     * **Behavior:**
     * - [ASCENDING]: By name after article, ascending
     * - [DESCENDING]: By name after article, descending
     * - [ARTIST]: Grouped by artist and sorted [ASCENDING]
     * - [ALBUM]: [ASCENDING]
     * - [YEAR]: Sorted by year
     *
     * The grouping mode for albums in an artist will be [YEAR].
     */
    fun sortAlbums(albums: Collection<Album>): List<Album> {
        return when (this) {
            ASCENDING, DESCENDING -> sortParents(albums)

            ARTIST -> ASCENDING.sortParents(albums.groupBy { it.artist }.keys)
                .flatMap { YEAR.sortAlbums(it.albums) }

            ALBUM -> ASCENDING.sortParents(albums)

            YEAR -> albums.sortedByDescending { it.year }
        }
    }

    /**
     * Sort a generic list of [MusicParent] instances.
     *
     * **Behavior:**
     * - [ASCENDING]: By name after article, ascending
     * - [DESCENDING]: By name after article, descending
     * - Same parent list is returned otherwise.
     */
    fun <T : MusicParent> sortParents(parents: Collection<T>): List<T> {
        return when (this) {
            ASCENDING -> parents.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { model ->
                    model.resolvedName.sliceArticle()
                }
            )

            DESCENDING -> parents.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { model ->
                    model.resolvedName.sliceArticle()
                }
            )

            else -> parents.toList()
        }
    }

    /**
     * Sort the songs in an album.
     *
     * **Behavior:**
     * - [ASCENDING]: By track, ascending
     * - [DESCENDING]: By track, descending
     * - Same song list is returned otherwise.
     */
    fun sortAlbum(album: Album): List<Song> {
        return when (this) {
            ASCENDING -> album.songs.sortedBy { it.track }
            DESCENDING -> album.songs.sortedByDescending { it.track }
            else -> album.songs
        }
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
     * Converts this mode into an integer constant. Use this when writing a [SortMode]
     * to storage, as it will be more efficent.
     */
    fun toInt(): Int {
        return when (this) {
            ASCENDING -> CONST_ASCENDING
            DESCENDING -> CONST_DESCENDING
            ARTIST -> CONST_ARTIST
            ALBUM -> CONST_ALBUM
            YEAR -> CONST_YEAR
        }
    }

    companion object {
        private const val CONST_ASCENDING = 0xA10C
        private const val CONST_DESCENDING = 0xA10D
        private const val CONST_ARTIST = 0xA10E
        private const val CONST_ALBUM = 0xA10F
        private const val CONST_YEAR = 0xA110

        /**
         * Returns a [SortMode] depending on the integer constant, use this when restoring
         * a [SortMode] from storage.
         */
        fun fromInt(value: Int): SortMode? {
            return when (value) {
                CONST_ASCENDING -> ASCENDING
                CONST_DESCENDING -> DESCENDING
                CONST_ARTIST -> ARTIST
                CONST_ALBUM -> ALBUM
                CONST_YEAR -> YEAR
                else -> null
            }
        }

        /**
         * Convert a menu [id] to an instance of [SortMode].
         */
        fun fromId(@IdRes id: Int): SortMode? {
            return when (id) {
                ASCENDING.itemId -> ASCENDING
                DESCENDING.itemId -> DESCENDING
                ARTIST.itemId -> ARTIST
                ALBUM.itemId -> ALBUM
                YEAR.itemId -> YEAR
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

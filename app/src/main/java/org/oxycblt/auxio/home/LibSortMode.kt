/*
 * Copyright (c) 2021 Auxio Project
 * LibSortMode.kt is part of Auxio.
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

package org.oxycblt.auxio.home

import androidx.annotation.IdRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.sliceArticle

/**
 * The enum for the current sort state.
 * This enum is semantic depending on the context it is used. Documentation describing each
 * sorting functions behavior can be found in the function definition.
 * @param itemId Menu ID associated with this enum
 * @author OxygenCobalt
 */
enum class LibSortMode(@IdRes val itemId: Int) {
    ASCENDING(R.id.option_sort_asc),
    DESCENDING(R.id.option_sort_dsc),
    ARTIST(R.id.option_sort_artist),
    ALBUM(R.id.option_sort_album),
    YEAR(R.id.option_sort_year);

    /**
     * Sort a list of songs.
     *
     * **Behavior:**
     * - [ASCENDING] & [DESCENDING]: See [sortModels]
     * - [ARTIST]: Grouped by album and then sorted [ASCENDING] based off the artist name.
     * - [ALBUM]: Grouped by album and sorted [ASCENDING]
     * - [YEAR]: Grouped by album and sorted by year
     *
     * The grouping mode for songs in an album will be by track, [ASCENDING].
     * @see sortAlbums
     */
    fun sortSongs(songs: Collection<Song>): List<Song> {
        return when (this) {
            ASCENDING, DESCENDING -> sortModels(songs)

            else -> sortAlbums(songs.groupBy { it.album }.keys).flatMap { album ->
                ASCENDING.sortAlbum(album)
            }
        }
    }

    /**
     * Sort a list of albums.
     *
     * **Behavior:**
     * - [ASCENDING] & [DESCENDING]: See [sortModels]
     * - [ARTIST]: Grouped by artist and sorted [ASCENDING]
     * - [ALBUM]: [ASCENDING]
     * - [YEAR]: Sorted by year
     *
     * The grouping mode for albums in an artist will be [YEAR].
     */
    fun sortAlbums(albums: Collection<Album>): List<Album> {
        return when (this) {
            ASCENDING, DESCENDING -> sortModels(albums)

            ARTIST -> ASCENDING.sortModels(albums.groupBy { it.artist }.keys)
                .flatMap { YEAR.sortAlbums(it.albums) }

            ALBUM -> ASCENDING.sortModels(albums)

            YEAR -> albums.sortedByDescending { it.year }
        }
    }

    /**
     * Sort a list of generic [BaseModel] instances.
     *
     * **Behavior:**
     * - [ASCENDING]: Sorted by name, ascending
     * - [DESCENDING]: Sorted by name, descending
     * - Same list is returned otherwise.
     *
     * Names will be treated as case-insensitive. Articles like "the" and "a" will be skipped
     * to line up with MediaStore behavior.
     */
    fun <T : BaseModel> sortModels(models: Collection<T>): List<T> {
        return when (this) {
            ASCENDING -> models.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { model ->
                    model.name.sliceArticle()
                }
            )

            DESCENDING -> models.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { model ->
                    model.name.sliceArticle()
                }
            )

            else -> models.toList()
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

    companion object {
        /**
         * Convert a menu [id] to an instance of [LibSortMode].
         */
        fun fromId(@IdRes id: Int): LibSortMode? {
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

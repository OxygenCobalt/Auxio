/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music

import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R

enum class MusicMode {
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES;

    val string: Int
        get() =
            when (this) {
                SONGS -> R.string.lbl_songs
                ALBUMS -> R.string.lbl_albums
                ARTISTS -> R.string.lbl_artists
                GENRES -> R.string.lbl_genres
            }

    val icon: Int
        get() =
            when (this) {
                SONGS -> R.drawable.ic_song_24
                ALBUMS -> R.drawable.ic_album_24
                ARTISTS -> R.drawable.ic_artist_24
                GENRES -> R.drawable.ic_genre_24
            }

    val intCode: Int
        get() =
            when (this) {
                SONGS -> IntegerTable.MUSIC_MODE_SONGS
                ALBUMS -> IntegerTable.MUSIC_MODE_ALBUMS
                ARTISTS -> IntegerTable.MUSIC_MODE_ARTISTS
                GENRES -> IntegerTable.MUSIC_MODE_GENRES
            }

    companion object {
        fun fromInt(value: Int) =
            when (value) {
                IntegerTable.MUSIC_MODE_SONGS -> SONGS
                IntegerTable.MUSIC_MODE_ALBUMS -> ALBUMS
                IntegerTable.MUSIC_MODE_ARTISTS -> ARTISTS
                IntegerTable.MUSIC_MODE_GENRES -> GENRES
                else -> null
            }
    }
}

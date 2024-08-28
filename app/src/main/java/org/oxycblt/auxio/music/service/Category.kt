/*
 * Copyright (c) 2024 Auxio Project
 * Category.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.service

import org.oxycblt.auxio.R

sealed interface Category {
    val id: String
    val nameRes: Int
    val bitmapRes: Int?

    data class Root(val amount: Int) : Category {
        override val id = "root/$amount"
        override val nameRes = R.string.info_app_name
        override val bitmapRes = null

        companion object {
            const val ID_PREFIX = "root"

            fun fromString(str: String): Root? {
                val split = str.split("/", limit = 2)
                if (split.size != 2) {
                    return null
                }
                val limit = split[1].toIntOrNull() ?: return null
                return Root(limit)
            }
        }
    }

    data class More(val remainder: Int) : Category {
        override val id = "more/$remainder"
        override val nameRes = R.string.lbl_more
        override val bitmapRes = null

        companion object {
            const val ID_PREFIX = "more"

            fun fromString(str: String): More? {
                val split = str.split("/", limit = 2)
                if (split.size != 2) {
                    return null
                }
                val remainder = split[1].toIntOrNull() ?: return null
                return More(remainder)
            }
        }
    }

    data object Songs : Category {
        override val id = "songs"
        override val nameRes = R.string.lbl_songs
        override val bitmapRes = R.drawable.ic_song_bitmap_24
    }

    data object Albums : Category {
        override val id = "albums"
        override val nameRes = R.string.lbl_albums
        override val bitmapRes = R.drawable.ic_album_bitmap_24
    }

    data object Artists : Category {
        override val id = "artists"
        override val nameRes = R.string.lbl_artists
        override val bitmapRes = R.drawable.ic_artist_bitmap_24
    }

    data object Genres : Category {
        override val id = "genres"
        override val nameRes = R.string.lbl_genres
        override val bitmapRes = R.drawable.ic_genre_bitmap_24
    }

    data object Playlists : Category {
        override val id = "playlists"
        override val nameRes = R.string.lbl_playlists
        override val bitmapRes = R.drawable.ic_playlist_bitmap_24
    }

    companion object {
        val MUSIC = arrayOf(Songs, Albums, Artists, Genres, Playlists)
        val DEVICE_MUSIC = arrayOf(Songs, Albums, Artists, Genres)
        val USER_MUSIC = arrayOf(Playlists)

        fun fromString(str: String): Category? =
            when {
                str.startsWith(Root.ID_PREFIX) -> Root.fromString(str)
                str.startsWith(More.ID_PREFIX) -> More.fromString(str)
                str == Songs.id -> Songs
                str == Albums.id -> Albums
                str == Artists.id -> Artists
                str == Genres.id -> Genres
                str == Playlists.id -> Playlists
                else -> null
            }
    }
}

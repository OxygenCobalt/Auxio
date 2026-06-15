/*
 * Copyright (c) 2024 Auxio Project
 * TabNode.kt is part of Auxio.
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
import org.oxycblt.auxio.music.MusicType

sealed class TabNode {
    abstract val id: String
    abstract val microId: String
    abstract val nameRes: Int
    abstract val bitmapRes: Int?

    override fun toString() = id

    data object Root : TabNode() {
        override val id = "root"
        override val microId: String = "tt"
        override val nameRes = R.string.info_app_name
        override val bitmapRes = null

        override fun toString() = id
    }

    data object More : TabNode() {
        override val id = "more"
        override val microId: String = "tm"
        override val nameRes = R.string.lbl_more
        override val bitmapRes = R.drawable.ic_more_bitmap_24
    }

    data class Home(val type: MusicType) : TabNode() {
        override val id = "$ID/${type.intCode}"
        override val microId: String =
            "t${
            when (type) {
                MusicType.SONGS -> 's'
                MusicType.ALBUMS -> 'a'
                MusicType.ARTISTS -> 'r'
                MusicType.GENRES -> 'g'
                MusicType.PLAYLISTS -> 'p'
            }}"
        override val bitmapRes: Int
            get() =
                when (type) {
                    MusicType.SONGS -> R.drawable.ic_song_bitmap_24
                    MusicType.ALBUMS -> R.drawable.ic_album_bitmap_24
                    MusicType.ARTISTS -> R.drawable.ic_artist_bitmap_24
                    MusicType.GENRES -> R.drawable.ic_genre_bitmap_24
                    MusicType.PLAYLISTS -> R.drawable.ic_playlist_bitmap_24
                }

        override val nameRes = type.nameRes

        companion object {
            const val ID = "home"
        }
    }

    companion object {
        fun fromString(str: String): TabNode? {
            if (str.startsWith("t")) {
                // new compressed id format
                return when (str.getOrNull(1)) {
                    't' -> Root
                    'm' -> More
                    's' -> Home(MusicType.SONGS)
                    'a' -> Home(MusicType.ALBUMS)
                    'r' -> Home(MusicType.ARTISTS)
                    'g' -> Home(MusicType.GENRES)
                    'p' -> Home(MusicType.PLAYLISTS)
                    else -> null
                }
            }
            return when {
                str == Root.id -> Root
                str == More.id -> More
                str.startsWith(Home.ID) -> {
                    val split = str.split("/")
                    if (split.size != 2) return null
                    val intCode = split[1].toIntOrNull() ?: return null
                    Home(MusicType.fromIntCode(intCode) ?: return null)
                }
                else -> null
            }
        }
    }
}

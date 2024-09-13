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
    abstract val data: Int
    abstract val nameRes: Int
    abstract val bitmapRes: Int?

    override fun toString() = "${id}/${data}"

    data class Root(val amount: Int) : TabNode() {
        override val id = ID
        override val data = amount
        override val nameRes = R.string.info_app_name
        override val bitmapRes = null

        companion object {
            const val ID = "root"
        }
    }

    data class More(val remainder: Int) : TabNode() {
        override val id = ID
        override val data = remainder
        override val nameRes = R.string.lbl_more
        override val bitmapRes = null

        companion object {
            const val ID = "more"
        }
    }

    data class Home(val type: MusicType) : TabNode() {
        override val id = ID
        override val data = type.intCode
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
            val split = str.split("/", limit = 2)
            if (split.size != 2) {
                return null
            }
            val data = split[1].toIntOrNull() ?: return null
            return when (split[0]) {
                Root.ID -> Root(data)
                More.ID -> More(data)
                Home.ID -> Home(MusicType.fromIntCode(data) ?: return null)
                else -> null
            }
        }
    }
}

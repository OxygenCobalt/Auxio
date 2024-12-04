/*
 * Copyright (c) 2023 Auxio Project
 * Cover.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.cover

import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Song

sealed interface Cover {
    val key: String

    data class Single(val song: Song) : Cover {
        override val key: String
            get() = "${song.uid}@${song.lastModified}"

        val uid = song.uid
        val uri = song.uri
        val lastModified = song.lastModified
    }

    class Multi(val all: List<Single>) : Cover {
        override val key = "multi@${all.hashCode()}"
    }

    companion object {
        private val FALLBACK_SORT = Sort(Sort.Mode.ByAlbum, Sort.Direction.ASCENDING)

        fun nil() = Multi(listOf())

        fun single(song: Song) = Single(song)

        fun multi(songs: Collection<Song>) = order(songs).run { Multi(this) }

        private fun order(songs: Collection<Song>) =
            FALLBACK_SORT.songs(songs)
                .map { it.cover }
                .groupBy { it.key }
                .entries
                .sortedByDescending { it.value.size }
                .map { it.value.first() }
    }
}

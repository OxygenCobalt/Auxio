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
 
package org.oxycblt.auxio.image.extractor

import android.net.Uri
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Song

sealed interface Cover {
    val key: String
    val mediaStoreCoverUri: Uri

    /**
     * The song has an embedded cover art we support, so we can operate with it on a per-song basis.
     */
    data class Embedded(val songCoverUri: Uri, val songUri: Uri, val perceptualHash: String) :
        Cover {
        override val mediaStoreCoverUri = songCoverUri
        override val key = perceptualHash
    }

    /**
     * We couldn't find any embedded cover art ourselves, but the android system might have some
     * through a cover.jpg file or something similar.
     */
    data class External(val albumCoverUri: Uri) : Cover {
        override val mediaStoreCoverUri = albumCoverUri
        override val key = albumCoverUri.toString()
    }

    companion object {
        private val FALLBACK_SORT = Sort(Sort.Mode.ByAlbum, Sort.Direction.ASCENDING)

        fun order(songs: Collection<Song>) =
            FALLBACK_SORT.songs(songs)
                .map { it.cover }
                .groupBy { it.key }
                .entries
                .sortedByDescending { it.value.size }
                .map { it.value.first() }
    }
}

data class ParentCover(val single: Cover, val all: List<Cover>) {
    companion object {
        fun from(song: Song, songs: Collection<Song>) = from(song.cover, songs)

        fun from(src: Cover, songs: Collection<Song>) = ParentCover(src, Cover.order(songs))
    }
}

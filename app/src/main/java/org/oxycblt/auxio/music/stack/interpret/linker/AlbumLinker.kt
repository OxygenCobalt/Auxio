/*
 * Copyright (c) 2024 Auxio Project
 * AlbumLinker.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.stack.interpret.linker

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.oxycblt.auxio.music.stack.interpret.model.AlbumImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl

class AlbumLinker {
    private val tree = mutableMapOf<String?, MutableMap<UUID?, AlbumLink>>()

    fun register(linkedSongs: Flow<ArtistLinker.LinkedSong>) =
        linkedSongs.map {
            val nameKey = it.linkedAlbum.preAlbum.rawName.lowercase()
            val musicBrainzIdKey = it.linkedAlbum.preAlbum.musicBrainzId
            val albumLink =
                tree
                    .getOrPut(nameKey) { mutableMapOf() }
                    .getOrPut(musicBrainzIdKey) { AlbumLink(AlbumNode(Contribution())) }
            albumLink.node.contributors.contribute(it.linkedAlbum)
            LinkedSong(it, albumLink)
        }

    fun resolve(): Collection<AlbumImpl> =
        tree.values.flatMap { musicBrainzIdBundle ->
            val only = musicBrainzIdBundle.values.singleOrNull()
            if (only != null) {
                return@flatMap listOf(only.node.resolve())
            }
            val nullBundle =
                musicBrainzIdBundle[null]
                    ?: return@flatMap musicBrainzIdBundle.values.map { it.node.resolve() }
            // Only partially tagged with MBIDs, must go through and
            musicBrainzIdBundle
                .filter { it.key != null }
                .forEach {
                    val candidates = it.value.node.contributors.candidates
                    nullBundle.node.contributors.contribute(candidates)
                    it.value.node = nullBundle.node
                }
            listOf(nullBundle.node.resolve())
        }

    data class LinkedSong(
        val linkedArtistSong: ArtistLinker.LinkedSong,
        val album: Linked<AlbumImpl, SongImpl>
    )

    private data class AlbumLink(var node: AlbumNode) : Linked<AlbumImpl, SongImpl> {
        override fun resolve(child: SongImpl): AlbumImpl {
            return requireNotNull(node.albumImpl) { "Album not resolved yet" }
                .also { it.link(child) }
        }
    }

    private class AlbumNode(val contributors: Contribution<ArtistLinker.LinkedAlbum>) {
        var albumImpl: AlbumImpl? = null
            private set

        fun resolve(): AlbumImpl {
            val impl = AlbumImpl(LinkedAlbumImpl(contributors.resolve()))
            albumImpl = impl
            return impl
        }
    }

    private class LinkedAlbumImpl(private val artistLinkedAlbum: ArtistLinker.LinkedAlbum) :
        LinkedAlbum {
        override val preAlbum = artistLinkedAlbum.preAlbum

        override val artists = artistLinkedAlbum.artists
    }
}

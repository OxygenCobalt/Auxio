/*
 * Copyright (c) 2024 Auxio Project
 * ArtistLinker.kt is part of Auxio.
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
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.stack.interpret.model.AlbumImpl
import org.oxycblt.auxio.music.stack.interpret.model.ArtistImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreAlbum
import org.oxycblt.auxio.music.stack.interpret.prepare.PreArtist

class ArtistLinker {
    private val tree = mutableMapOf<String?, MutableMap<UUID?, ArtistLink>>()

    fun register(linkedSongs: Flow<GenreLinker.LinkedSong>) =
        linkedSongs.map {
            val linkedSongArtists =
                it.preSong.preArtists.map { artist ->
                    val nameKey = artist.rawName?.lowercase()
                    val musicBrainzIdKey = artist.musicBrainzId
                    val artistLink =
                        tree
                            .getOrPut(nameKey) { mutableMapOf() }
                            .getOrPut(musicBrainzIdKey) { ArtistLink(ArtistNode(Contribution())) }
                    artistLink.node.contributors.contribute(artist)
                    artistLink
                }
            val linkedAlbumArtists =
                it.preSong.preAlbum.preArtists.map { artist ->
                    val nameKey = artist.rawName?.lowercase()
                    val musicBrainzIdKey = artist.musicBrainzId
                    val artistLink =
                        tree
                            .getOrPut(nameKey) { mutableMapOf() }
                            .getOrPut(musicBrainzIdKey) { ArtistLink(ArtistNode(Contribution())) }
                    artistLink.node.contributors.contribute(artist)
                    artistLink
                }
            val linkedAlbum = LinkedAlbum(it.preSong.preAlbum, MultiArtistLink(linkedAlbumArtists))
            LinkedSong(it, linkedAlbum, MultiArtistLink(linkedSongArtists))
        }

    fun resolve(): Collection<ArtistImpl> =
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
        val linkedGenreSong: GenreLinker.LinkedSong,
        val linkedAlbum: LinkedAlbum,
        val artists: Linked<List<ArtistImpl>, SongImpl>
    )

    data class LinkedAlbum(
        val preAlbum: PreAlbum,
        val artists: Linked<List<ArtistImpl>, AlbumImpl>
    )

    private class MultiArtistLink<T : Music>(val links: List<Linked<ArtistImpl, Music>>) :
        Linked<List<ArtistImpl>, T> {
        override fun resolve(child: T): List<ArtistImpl> {
            return links.map { it.resolve(child) }.distinct()
        }
    }

    private data class ArtistLink(var node: ArtistNode) : Linked<ArtistImpl, Music> {
        override fun resolve(child: Music): ArtistImpl {
            return requireNotNull(node.artistImpl) { "Artist not resolved yet" }
                .also {
                    when (child) {
                        is SongImpl -> it.link(child)
                        is AlbumImpl -> it.link(child)
                        else -> error("Cannot link to child $child")
                    }
                }
        }
    }

    private class ArtistNode(val contributors: Contribution<PreArtist>) {
        var artistImpl: ArtistImpl? = null
            private set

        fun resolve(): ArtistImpl {
            val impl = ArtistImpl(contributors.resolve())
            artistImpl = impl
            return impl
        }
    }
}

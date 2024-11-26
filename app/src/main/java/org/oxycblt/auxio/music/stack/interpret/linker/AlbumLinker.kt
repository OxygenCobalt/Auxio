package org.oxycblt.auxio.music.stack.interpret.linker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.stack.interpret.model.AlbumImpl
import org.oxycblt.auxio.music.stack.interpret.model.ArtistImpl
import org.oxycblt.auxio.music.stack.interpret.model.GenreImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreAlbum
import org.oxycblt.auxio.music.stack.interpret.prepare.PreGenre
import org.oxycblt.auxio.music.stack.interpret.prepare.PreSong
import java.util.UUID


class AlbumLinker {
    private val tree = mutableMapOf<String?, MutableMap<UUID?, AlbumLink>>()

    fun register(linkedSongs: Flow<ArtistLinker.LinkedSong>) = linkedSongs.map {
        val nameKey = it.linkedAlbum.preAlbum.rawName.lowercase()
        val musicBrainzIdKey = it.linkedAlbum.preAlbum.musicBrainzId
        val albumLink = tree.getOrPut(nameKey) { mutableMapOf() }
            .getOrPut(musicBrainzIdKey) { AlbumLink(AlbumNode(Contribution())) }
        albumLink.node.contributors.contribute(it.linkedAlbum)
        LinkedSong(it, albumLink)
    }

    fun resolve(): Collection<AlbumImpl> =
        tree.values.flatMap { musicBrainzIdBundle ->
            val only =
                musicBrainzIdBundle.values.singleOrNull()
            if (only != null) {
                return@flatMap listOf(only.node.resolve())
            }
            val nullBundle = musicBrainzIdBundle[null]
                ?: return@flatMap musicBrainzIdBundle.values.map { it.node.resolve() }
            // Only partially tagged with MBIDs, must go through and
            musicBrainzIdBundle.filter { it.key != null }.forEach {
                val candidates = it.value.node.contributors.candidates
                nullBundle.node.contributors.contribute(candidates)
                it.value.node = nullBundle.node
            }
            listOf(nullBundle.node.resolve())
        }

    data class LinkedSong(
        val linkedSong: ArtistLinker.LinkedSong,
        val album: Linked<AlbumImpl, SongImpl>
    )

    private data class AlbumLink(
        var node: AlbumNode
    ) : Linked<AlbumImpl, SongImpl> {
        override fun resolve(child: SongImpl): AlbumImpl {
            return requireNotNull(node.albumImpl) { "Album not resolved yet" }.also {
                it.link(child)
            }
        }
    }

    private class AlbumNode(
        val contributors: Contribution<ArtistLinker.LinkedAlbum>
    ) {
        var albumImpl: AlbumImpl? = null
            private set

        fun resolve(): AlbumImpl {
            val impl = AlbumImpl(LinkedAlbumImpl(contributors.resolve()))
            albumImpl = impl
            return impl
        }
    }

    private class LinkedAlbumImpl(
        private val artistLinkedAlbum: ArtistLinker.LinkedAlbum
    ) : LinkedAlbum {
        override val preAlbum = artistLinkedAlbum.preAlbum

        override val artists = artistLinkedAlbum.artists
    }
}

package org.oxycblt.auxio.music.stack.interpret.linker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.oxycblt.auxio.music.stack.interpret.model.GenreImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreGenre
import org.oxycblt.auxio.music.stack.interpret.prepare.PreSong

class GenreLinker {
    private val tree = mutableMapOf<String?, GenreLink>()

    fun register(preSong: Flow<PreSong>): Flow<LinkedSong> = preSong.map {
        val genreLinks = it.preGenres.map { genre ->
            val nameKey = genre.rawName?.lowercase()
            val link = tree.getOrPut(nameKey) { GenreLink(GenreNode(Contribution())) }
            link.node.contributors.contribute(genre)
            link
        }
        LinkedSong(it, MultiGenreLink(genreLinks))
    }

    fun resolve() =
        tree.values.map { it.node.resolve() }

    data class LinkedSong(
        val preSong: PreSong,
        val genres: Linked<List<GenreImpl>, SongImpl>
    )

    private class MultiGenreLink(
        val links: List<Linked<GenreImpl, SongImpl>>
    ) : Linked<List<GenreImpl>, SongImpl> {
        override fun resolve(child: SongImpl): List<GenreImpl> {
            return links.map { it.resolve(child) }.distinct()
        }
    }

    private data class GenreLink(
        var node: GenreNode
    ) : Linked<GenreImpl, SongImpl> {
        override fun resolve(child: SongImpl): GenreImpl {
            return requireNotNull(node.genreImpl) { "Genre not resolved yet" }.also {
                it.link(child)
            }
        }
    }

    private class GenreNode(
        val contributors: Contribution<PreGenre>
    ) {
        var genreImpl: GenreImpl? = null
            private set

        fun resolve(): GenreImpl {
            val impl = GenreImpl(contributors.resolve())
            genreImpl = impl
            return impl
        }
    }
}

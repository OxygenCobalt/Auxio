/*
 * Copyright (c) 2024 Auxio Project
 * GenreLinker.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.interpret.link

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.oxycblt.auxio.musikr.model.GenreImpl
import org.oxycblt.auxio.musikr.model.SongImpl
import org.oxycblt.auxio.musikr.interpret.prepare.PreGenre
import org.oxycblt.auxio.musikr.interpret.prepare.PreSong

class GenreLinker {
    private val tree = mutableMapOf<String?, GenreLink>()

    fun register(preSong: Flow<PreSong>): Flow<LinkedSong> =
        preSong.map {
            val genreLinks =
                it.preGenres.map { genre ->
                    val nameKey = genre.rawName?.lowercase()
                    val link = tree.getOrPut(nameKey) { GenreLink(GenreNode(Contribution())) }
                    link.node.contributors.contribute(genre)
                    link
                }
            LinkedSong(it, MultiGenreLink(genreLinks))
        }

    fun resolve() = tree.values.map { it.node.resolve() }

    data class LinkedSong(val preSong: PreSong, val genres: Linked<List<GenreImpl>, SongImpl>)

    private class MultiGenreLink(val links: List<Linked<GenreImpl, SongImpl>>) :
        Linked<List<GenreImpl>, SongImpl> {
        override fun resolve(child: SongImpl): List<GenreImpl> {
            return links.map { it.resolve(child) }.distinct()
        }
    }

    private data class GenreLink(var node: GenreNode) : Linked<GenreImpl, SongImpl> {
        override fun resolve(child: SongImpl): GenreImpl {
            return requireNotNull(node.genreImpl) { "Genre not resolved yet" }
                .also { it.link(child) }
        }
    }

    private class GenreNode(val contributors: Contribution<PreGenre>) {
        var genreImpl: GenreImpl? = null
            private set

        fun resolve(): GenreImpl {
            val impl = GenreImpl(contributors.resolve())
            genreImpl = impl
            return impl
        }
    }
}

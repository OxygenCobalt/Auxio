/*
 * Copyright (c) 2025 Auxio Project
 * SongGraph.kt is part of Auxio.
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
 
package org.oxycblt.musikr.graph

import org.oxycblt.musikr.Music
import org.oxycblt.musikr.tag.interpret.PreSong

internal class SongGraph(
    private val albumGraph: AlbumGraph,
    private val artistGraph: ArtistGraph,
    private val genreGraph: GenreGraph,
    private val playlistGraph: PlaylistGraph
) {
    private val vertices = mutableMapOf<Music.UID, SongVertex>()

    fun link(vertex: SongVertex): Boolean {
        val preSong = vertex.preSong
        val uid = preSong.uid
        if (vertices.containsKey(uid)) {
            return false
        }
        artistGraph.link(vertex)
        genreGraph.link(vertex)
        albumGraph.link(vertex)
        playlistGraph.link(vertex)
        vertices[uid] = vertex
        return true
    }

    fun solve(): Collection<SongVertex> {
        vertices.entries.forEach { entry ->
            val vertex = entry.value
            vertex.artistVertices = vertex.artistVertices.distinct().toMutableList()
            vertex.genreVertices = vertex.genreVertices.distinct().toMutableList()
        }
        return vertices.values
    }
}

internal class SongVertex(
    val preSong: PreSong,
) : Vertex {
    var albumVertex: AlbumVertex? = null
    var artistVertices = mutableListOf<ArtistVertex>()
    var genreVertices = mutableListOf<GenreVertex>()
    override var tag: Any? = null

    override fun toString() = "SongVertex(preSong=$preSong)"
}

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
import org.oxycblt.musikr.playlist.SongPointer
import org.oxycblt.musikr.tag.interpret.PreSong

internal class SongGraph(
    private val albumGraph: AlbumGraph,
    private val artistGraph: ArtistGraph,
    private val genreGraph: GenreGraph,
    private val playlistGraph: PlaylistGraph
) {
    val vertices = mutableMapOf<Music.UID, SongVertex>()

    fun add(preSong: PreSong): SongVertex? {
        val uid = preSong.uid
        if (vertices.containsKey(uid)) {
            return null
        }

        val songGenreVertices = preSong.preGenres.map { preGenre -> genreGraph.add(preGenre) }

        val songArtistVertices = preSong.preArtists.map { preArtist -> artistGraph.add(preArtist) }

        val albumVertex = albumGraph.add(preSong.preAlbum)

        val songVertex =
            SongVertex(
                preSong,
                albumVertex,
                songArtistVertices.toMutableList(),
                songGenreVertices.toMutableList())

        songVertex.artistGraph.forEach { artistVertex ->
            artistVertex.songVertices.add(songVertex)
            songGenreVertices.forEach { genreVertex ->
                // Mutually link any new genres to the artist
                artistVertex.genreGraph.add(genreVertex)
                genreVertex.artistGraph.add(artistVertex)
            }
        }

        songVertex.genreGraph.forEach { genreVertex -> genreVertex.songVertices.add(songVertex) }

        vertices[uid] = songVertex

        return songVertex
    }

    fun simplify() {
        vertices.entries.forEach { entry ->
            val vertex = entry.value
            vertex.artistGraph = vertex.artistGraph.distinct().toMutableList()
            vertex.genreGraph = vertex.genreGraph.distinct().toMutableList()

            playlistGraph.vertices.forEach {
                val pointer = SongPointer.UID(entry.key)
                val index = it.pointerMap[pointer]
                if (index != null) {
                    it.songVertices[index] = vertex
                }
            }
        }
    }
}

internal class SongVertex(
    val preSong: PreSong,
    var albumVertex: AlbumVertex,
    var artistGraph: MutableList<ArtistVertex>,
    var genreGraph: MutableList<GenreVertex>
) : Vertex {
    override var tag: Any? = null

    override fun toString() = "SongVertex(preSong=$preSong)"
}

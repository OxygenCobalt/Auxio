/*
 * Copyright (c) 2024 Auxio Project
 * MusicGraph.kt is part of Auxio.
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

import org.oxycblt.musikr.playlist.interpret.PrePlaylist
import org.oxycblt.musikr.tag.interpret.PreSong

internal data class MusicGraph(
    val songVertex: List<SongVertex>,
    val albumVertex: List<AlbumVertex>,
    val artistVertex: List<ArtistVertex>,
    val genreVertex: List<GenreVertex>,
    val playlistVertex: Set<PlaylistVertex>
) {
    interface Builder {
        fun add(preSong: PreSong)

        fun add(prePlaylist: PrePlaylist)

        fun build(): MusicGraph
    }

    companion object {
        fun builder(): Builder = MusicGraphBuilderImpl()
    }
}

private class MusicGraphBuilderImpl : MusicGraph.Builder {
    private val genreGraph = GenreGraph()
    private val artistGraph = ArtistGraph()
    private val albumGraph = AlbumGraph(artistGraph)
    private val playlistGraph = PlaylistGraph()
    private val songVertices = SongGraph(albumGraph, artistGraph, genreGraph, playlistGraph)

    override fun add(preSong: PreSong) {
        songVertices.add(preSong)
    }

    override fun add(prePlaylist: PrePlaylist) {
        playlistGraph.add(prePlaylist)
    }

    override fun build(): MusicGraph {
        genreGraph.simplify()
        artistGraph.simplify()
        albumGraph.simplify()
        songVertices.simplify()

        val graph =
            MusicGraph(
                songVertices.vertices.values.toList(),
                albumGraph.vertices.values.toList(),
                artistGraph.vertices.values.toList(),
                genreGraph.vertices.values.toList(),
                playlistGraph.vertices)

        return graph
    }
}

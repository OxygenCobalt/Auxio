/*
 * Copyright (c) 2025 Auxio Project
 * PlaylistGraph.kt is part of Auxio.
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

import org.oxycblt.musikr.playlist.SongPointer
import org.oxycblt.musikr.playlist.interpret.PrePlaylist

internal class PlaylistGraph {
    private val pointerMap = mutableMapOf<SongPointer, SongVertex>()
    private val vertices = mutableSetOf<PlaylistVertex>()

    fun link(vertex: PlaylistVertex) {
        for ((pointer, songVertex) in pointerMap) {
            // Link the vertices we are already aware of to this vertex.
            vertex.pointerMap[pointer]?.forEach { index -> vertex.songVertices[index] = songVertex }
        }
        vertices.add(vertex)
    }

    fun link(vertex: SongVertex) {
        val pointer = SongPointer.UID(vertex.preSong.uid)
        pointerMap[pointer] = vertex
        for (playlistVertex in vertices) {
            // Retroactively update previously known playlists to add the new vertex.
            playlistVertex.pointerMap[pointer]?.forEach { index ->
                playlistVertex.songVertices[index] = vertex
            }
        }
    }

    fun solve(): Collection<PlaylistVertex> = vertices
}

internal class PlaylistVertex(val prePlaylist: PrePlaylist) {
    val songVertices = Array<SongVertex?>(prePlaylist.songPointers.size) { null }
    val pointerMap =
        prePlaylist.songPointers
            .withIndex()
            .groupBy { it.value }
            .mapValuesTo(mutableMapOf()) { indexed -> indexed.value.map { it.index } }
    val tag: Any? = null
}

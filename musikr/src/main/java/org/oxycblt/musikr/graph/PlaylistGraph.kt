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

import org.oxycblt.musikr.playlist.interpret.PrePlaylist

internal class PlaylistGraph {
    val vertices = mutableSetOf<PlaylistVertex>()

    fun add(prePlaylist: PrePlaylist) {
        vertices.add(PlaylistVertex(prePlaylist))
    }
}

internal class PlaylistVertex(val prePlaylist: PrePlaylist) {
    val songVertices = Array<SongVertex?>(prePlaylist.songPointers.size) { null }
    val pointerMap =
        prePlaylist.songPointers
            .withIndex()
            .associateBy { it.value }
            .mapValuesTo(mutableMapOf()) { it.value.index }
    val tag: Any? = null
}

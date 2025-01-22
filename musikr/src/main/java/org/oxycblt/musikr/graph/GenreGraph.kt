/*
 * Copyright (c) 2025 Auxio Project
 * GenreGraph.kt is part of Auxio.
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

import org.oxycblt.musikr.tag.interpret.PreGenre

internal class GenreGraph {
    val vertices = mutableMapOf<PreGenre, GenreVertex>()

    fun add(preGenre: PreGenre): GenreVertex = vertices.getOrPut(preGenre) { GenreVertex(preGenre) }

    fun simplify() {
        val genreClusters = vertices.values.groupBy { it.preGenre.rawName?.lowercase() }
        for (cluster in genreClusters.values) {
            simplifyGenreCluster(cluster)
        }
    }

    private fun simplifyGenreCluster(cluster: Collection<GenreVertex>) {
        if (cluster.size == 1) {
            // Nothing to do.
            return
        }
        // All of these genres are semantically equivalent. Pick the most popular variation
        // and merge all the others into it.
        val clusterSet = cluster.toMutableSet()
        val dst = clusterSet.maxBy { it.songVertices.size }
        clusterSet.remove(dst)
        for (src in clusterSet) {
            meldGenreGraph(src, dst)
        }
    }

    private fun meldGenreGraph(src: GenreVertex, dst: GenreVertex) {
        if (src == dst) {
            // Same vertex, do nothing
            return
        }
        // Link all songs and artists from the irrelevant genre to the relevant genre.
        dst.songVertices.addAll(src.songVertices)
        dst.artistGraph.addAll(src.artistGraph)
        // Update all songs and artists to point to the relevant genre.
        src.songVertices.forEach {
            val index = it.genreGraph.indexOf(src)
            check(index >= 0) { "Illegal state: directed edge between genre and song" }
            it.genreGraph[index] = dst
        }
        src.artistGraph.forEach {
            it.genreGraph.remove(src)
            it.genreGraph.add(dst)
        }
        // Remove the irrelevant genre from the graph.
        vertices.remove(src.preGenre)
    }
}

internal class GenreVertex(val preGenre: PreGenre) : Vertex {
    val songVertices = mutableSetOf<SongVertex>()
    val artistGraph = mutableSetOf<ArtistVertex>()
    override var tag: Any? = null

    override fun toString() = "GenreVertex(preGenre=$preGenre)"
}

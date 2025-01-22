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
    private val vertices = mutableMapOf<PreGenre, GenreVertex>()

    fun link(vertex: SongVertex) {
        val preGenres = vertex.preSong.preGenres
        val artistVertices = vertex.artistVertices

        for (preGenre in preGenres) {
            val genreVertex = vertices.getOrPut(preGenre) { GenreVertex(preGenre) }
            vertex.genreVertices.add(genreVertex)
            genreVertex.songVertices.add(vertex)

            for (artistVertex in artistVertices) {
                genreVertex.artistVertices.add(artistVertex)
                artistVertex.genreVertices.add(genreVertex)
            }
        }
    }

    fun solve(): Collection<GenreVertex> {
        val genreClusters = vertices.values.groupBy { it.preGenre.rawName?.lowercase() }
        for (cluster in genreClusters.values) {
            simplifyGenreCluster(cluster)
        }
        return vertices.values
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
            meldGenreVertices(src, dst)
        }
    }

    private fun meldGenreVertices(src: GenreVertex, dst: GenreVertex) {
        if (src == dst) {
            // Same vertex, do nothing
            return
        }
        // Link all songs and artists from the irrelevant genre to the relevant genre.
        dst.songVertices.addAll(src.songVertices)
        dst.artistVertices.addAll(src.artistVertices)
        // Update all songs and artists to point to the relevant genre.
        src.songVertices.forEach {
            val index = it.genreVertices.indexOf(src)
            check(index >= 0) { "Illegal state: directed edge between genre and song" }
            it.genreVertices[index] = dst
        }
        src.artistVertices.forEach {
            it.genreVertices.remove(src)
            it.genreVertices.add(dst)
        }
        // Remove the irrelevant genre from the graph.
        vertices.remove(src.preGenre)
    }
}

internal class GenreVertex(val preGenre: PreGenre) : Vertex {
    val songVertices = mutableSetOf<SongVertex>()
    val artistVertices = mutableSetOf<ArtistVertex>()
    override var tag: Any? = null

    override fun toString() = "GenreVertex(preGenre=$preGenre)"
}

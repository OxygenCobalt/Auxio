/*
 * Copyright (c) 2025 Auxio Project
 * AlbumGraph.kt is part of Auxio.
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

import org.oxycblt.musikr.tag.interpret.PreAlbum
import org.oxycblt.musikr.util.unlikelyToBeNull

internal class AlbumGraph(private val artistGraph: ArtistGraph) {
    val vertices = mutableMapOf<PreAlbum, AlbumVertex>()

    fun add(preAlbum: PreAlbum): AlbumVertex {
        // Albums themselves have their own parent artists that also need to be
        // linked up.
        val albumartistGraph = preAlbum.preArtists.map { preArtist -> artistGraph.add(preArtist) }
        val albumVertex = AlbumVertex(preAlbum, albumartistGraph.toMutableList())
        // Album vertex is linked, now link artists back to album.
        albumartistGraph.forEach { artistVertex -> artistVertex.albumGraph.add(albumVertex) }
        return albumVertex
    }

    fun simplify() {
        val albumClusters = vertices.values.groupBy { it.preAlbum.rawName?.lowercase() }
        for (cluster in albumClusters.values) {
            simplifyAlbumCluster(cluster)
        }

        // Remove any edges that wound up connecting to the same artist or genre
        // in the end after simplification.
        vertices.values.forEach { it.artistGraph = it.artistGraph.distinct().toMutableList() }
    }

    private fun simplifyAlbumCluster(cluster: Collection<AlbumVertex>) {
        if (cluster.size == 1) {
            // Nothing to do.
            return
        }
        val fullMusicBrainzIdCoverage = cluster.all { it.preAlbum.musicBrainzId != null }
        if (fullMusicBrainzIdCoverage) {
            // All albums have MBIDs, nothing needs to be merged.
            val mbidClusters = cluster.groupBy { unlikelyToBeNull(it.preAlbum.musicBrainzId) }
            for (mbidCluster in mbidClusters.values) {
                simplifyAlbumClusterImpl(mbidCluster)
            }
            return
        }
        // No full MBID coverage, discard the MBIDs from the graph.
        val strippedCluster =
            cluster.map {
                val noMbidPreAlbum = it.preAlbum.copy(musicBrainzId = null)
                val simpleMbidVertex =
                    vertices.getOrPut(noMbidPreAlbum) {
                        AlbumVertex(noMbidPreAlbum, it.artistGraph.toMutableList())
                    }
                meldAlbumGraph(it, simpleMbidVertex)
                simpleMbidVertex
            }
        simplifyAlbumClusterImpl(strippedCluster)
    }

    private fun simplifyAlbumClusterImpl(cluster: Collection<AlbumVertex>) {
        // All of these albums are semantically equivalent. Pick the most popular variation
        // and merge all the others into it.
        if (cluster.size == 1) {
            // Nothing to do.
            return
        }
        val clusterSet = cluster.toMutableSet()
        val dst = clusterSet.maxBy { it.songVertices.size }
        clusterSet.remove(dst)
        for (src in clusterSet) {
            meldAlbumGraph(src, dst)
        }
    }

    private fun meldAlbumGraph(src: AlbumVertex, dst: AlbumVertex) {
        if (src == dst) {
            // Same vertex, do nothing
            return
        }
        // Link all songs and artists from the irrelevant album to the relevant album.
        dst.songVertices.addAll(src.songVertices)
        dst.artistGraph.addAll(src.artistGraph)
        // Update all songs and artists to point to the relevant album.
        src.songVertices.forEach { it.albumVertex = dst }
        src.artistGraph.forEach {
            it.albumGraph.remove(src)
            it.albumGraph.add(dst)
        }
        // Remove the irrelevant album from the graph.
        vertices.remove(src.preAlbum)
    }
}

internal class AlbumVertex(val preAlbum: PreAlbum, var artistGraph: MutableList<ArtistVertex>) :
    Vertex {
    val songVertices = mutableSetOf<SongVertex>()
    override var tag: Any? = null

    override fun toString() = "AlbumVertex(preAlbum=$preAlbum)"
}

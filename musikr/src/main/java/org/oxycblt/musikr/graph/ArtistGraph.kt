/*
 * Copyright (c) 2025 Auxio Project
 * ArtistGraph.kt is part of Auxio.
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

import org.oxycblt.musikr.tag.interpret.PreArtist
import org.oxycblt.musikr.util.unlikelyToBeNull

internal class ArtistGraph {
    private val vertices = mutableMapOf<PreArtist, ArtistVertex>()

    fun link(songVertex: SongVertex) {
        val preArtists = songVertex.preSong.preArtists
        val artistVertices = preArtists.map { vertices.getOrPut(it) { ArtistVertex(it) } }
        songVertex.artistVertices.addAll(artistVertices)
        artistVertices.forEach { it.songVertices.add(songVertex) }
    }

    fun link(albumVertex: AlbumVertex) {
        val preArtists = albumVertex.preAlbum.preArtists
        val artistVertices = preArtists.map { vertices.getOrPut(it) { ArtistVertex(it) } }
        albumVertex.artistVertices = artistVertices.toMutableList()
        artistVertices.forEach { it.albumVertices.add(albumVertex) }
    }

    fun solve(): Collection<ArtistVertex> {
        val artistClusters = vertices.values.groupBy { it.preArtist.rawName?.lowercase() }
        for (cluster in artistClusters.values) {
            simplifyArtistCluster(cluster)
        }
        return vertices.values
    }

    private fun simplifyArtistCluster(cluster: Collection<ArtistVertex>) {
        if (cluster.size == 1) {
            // Nothing to do.
            return
        }
        val fullMusicBrainzIdCoverage = cluster.all { it.preArtist.musicBrainzId != null }
        if (fullMusicBrainzIdCoverage) {
            // All artists have MBIDs, nothing needs to be merged.
            val mbidClusters = cluster.groupBy { unlikelyToBeNull(it.preArtist.musicBrainzId) }
            for (mbidCluster in mbidClusters.values) {
                simplifyArtistClusterImpl(mbidCluster)
            }
            return
        }
        // No full MBID coverage, discard the MBIDs from the graph.
        val strippedCluster =
            cluster.map {
                val noMbidPreArtist = it.preArtist.copy(musicBrainzId = null)
                val simpleMbidVertex =
                    vertices.getOrPut(noMbidPreArtist) { ArtistVertex(noMbidPreArtist) }
                meldArtistVertices(it, simpleMbidVertex)
                simpleMbidVertex
            }
        simplifyArtistClusterImpl(strippedCluster)
    }

    private fun simplifyArtistClusterImpl(cluster: Collection<ArtistVertex>) {
        if (cluster.size == 1) {
            // One canonical artist, nothing to collapse
            return
        }
        val clusterSet = cluster.toMutableSet()
        val relevantArtistVertex = clusterSet.maxBy { it.songVertices.size }
        clusterSet.remove(relevantArtistVertex)
        for (irrelevantArtistVertex in clusterSet) {
            meldArtistVertices(irrelevantArtistVertex, relevantArtistVertex)
        }
    }

    private fun meldArtistVertices(src: ArtistVertex, dst: ArtistVertex) {
        if (src == dst) {
            // Same vertex, do nothing
            return
        }
        // Link all songs and albums from the irrelevant artist to the relevant artist.
        dst.songVertices.addAll(src.songVertices)
        dst.albumVertices.addAll(src.albumVertices)
        dst.genreVertices.addAll(src.genreVertices)
        // Update all songs, albums, and genres to point to the relevant artist.
        src.songVertices.forEach {
            val index = it.artistVertices.indexOf(src)
            check(index >= 0) { "Illegal state: directed edge between artist and song" }
            it.artistVertices[index] = dst
        }
        src.albumVertices.forEach {
            val index = it.artistVertices.indexOf(src)
            check(index >= 0) { "Illegal state: directed edge between artist and album" }
            it.artistVertices[index] = dst
        }
        src.genreVertices.forEach {
            it.artistVertices.remove(src)
            it.artistVertices.add(dst)
        }

        // Remove the irrelevant artist from the graph.
        vertices.remove(src.preArtist)
    }
}

internal class ArtistVertex(
    val preArtist: PreArtist,
) : Vertex {
    val songVertices = mutableSetOf<SongVertex>()
    val albumVertices = mutableSetOf<AlbumVertex>()
    val genreVertices = mutableSetOf<GenreVertex>()
    override var tag: Any? = null

    override fun toString() = "ArtistVertex(preArtist=$preArtist)"
}

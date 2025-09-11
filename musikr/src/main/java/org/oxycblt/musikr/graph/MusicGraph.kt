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

import android.content.Context
import java.io.File
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.playlist.SongPointer
import org.oxycblt.musikr.playlist.interpret.PrePlaylist
import org.oxycblt.musikr.tag.interpret.PreAlbum
import org.oxycblt.musikr.tag.interpret.PreArtist
import org.oxycblt.musikr.tag.interpret.PreArtistsFrom
import org.oxycblt.musikr.tag.interpret.PreGenre
import org.oxycblt.musikr.tag.interpret.PreSong
import org.oxycblt.musikr.util.unlikelyToBeNull

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

    fun renderToGraphviz(context: Context, fileName: String = "music_graph.dot") {
        val dot = buildString {
            appendLine("digraph MusicGraph {")
            appendLine("  rankdir=LR;")
            appendLine("  node [shape=rectangle];")
            appendLine()

            // Define node styles for different vertex types
            appendLine("  // Songs")
            appendLine("  node [style=filled,fillcolor=lightblue];")
            songVertex.forEachIndexed { index, song ->
                val songId = "song_$index"
                val name = song.preSong.rawName ?: "Unknown Song"
                val uid = song.preSong.v401Uid
                val label = "${escape(name)}\\nUID: $uid"
                appendLine("  $songId [label=\"$label\"];")
            }
            appendLine()

            appendLine("  // Albums")
            appendLine("  node [style=filled,fillcolor=lightgreen];")
            albumVertex.forEachIndexed { index, album ->
                val albumId = "album_$index"
                val name = album.preAlbum.rawName ?: "Unknown Album"
                val mbid = album.preAlbum.musicBrainzId?.let { "\\nMBID: $it" } ?: ""
                val label = "${escape(name)}$mbid"
                appendLine("  $albumId [label=\"$label\"];")
            }
            appendLine()

            appendLine("  // Artists")
            appendLine("  node [style=filled,fillcolor=lightyellow];")
            artistVertex.forEachIndexed { index, artist ->
                val artistId = "artist_$index"
                val name = artist.preArtist.rawName ?: "Unknown Artist"
                val mbid = artist.preArtist.musicBrainzId?.let { "\\nMBID: $it" } ?: ""
                val label = "${escape(name)}$mbid"
                appendLine("  $artistId [label=\"$label\"];")
            }
            appendLine()

            appendLine("  // Genres")
            appendLine("  node [style=filled,fillcolor=lightcoral];")
            genreVertex.forEachIndexed { index, genre ->
                val genreId = "genre_$index"
                val label = genre.preGenre.rawName ?: "Unknown Genre"
                appendLine("  $genreId [label=\"${escape(label)}\"];")
            }
            appendLine()

            appendLine("  // Playlists")
            appendLine("  node [style=filled,fillcolor=lavender];")
            playlistVertex.forEachIndexed { index, playlist ->
                val playlistId = "playlist_$index"
                val label = playlist.prePlaylist.rawName ?: "Unknown Playlist"
                appendLine("  $playlistId [label=\"${escape(label)}\"];")
            }
            appendLine()

            // Create edges
            appendLine("  // Song -> Album edges")
            songVertex.forEachIndexed { songIndex, song ->
                val albumIndex = albumVertex.indexOf(song.albumVertex)
                if (albumIndex >= 0) {
                    appendLine("  song_$songIndex -> album_$albumIndex [color=blue];")
                }
            }
            appendLine()

            appendLine("  // Song -> Artist edges")
            songVertex.forEachIndexed { songIndex, song ->
                song.artistVertices.forEach { artistVertex ->
                    val artistIndex = this@MusicGraph.artistVertex.indexOf(artistVertex)
                    if (artistIndex >= 0) {
                        appendLine("  song_$songIndex -> artist_$artistIndex [color=green];")
                    }
                }
            }
            appendLine()

            appendLine("  // Song -> Genre edges")
            songVertex.forEachIndexed { songIndex, song ->
                song.genreVertices.forEach { genreVertex ->
                    val genreIndex = this@MusicGraph.genreVertex.indexOf(genreVertex)
                    if (genreIndex >= 0) {
                        appendLine("  song_$songIndex -> genre_$genreIndex [color=red];")
                    }
                }
            }
            appendLine()

            appendLine("  // Album -> Artist edges")
            albumVertex.forEachIndexed { albumIndex, album ->
                album.artistVertices.forEach { artistVertex ->
                    val artistIndex = this@MusicGraph.artistVertex.indexOf(artistVertex)
                    if (artistIndex >= 0) {
                        appendLine("  album_$albumIndex -> artist_$artistIndex [color=purple];")
                    }
                }
            }
            appendLine()

            appendLine("  // Playlist -> Song edges")
            playlistVertex.forEachIndexed { playlistIndex, playlist ->
                playlist.songVertices.forEachIndexed { _, songVertex ->
                    songVertex?.let {
                        val songIndex = this@MusicGraph.songVertex.indexOf(it)
                        if (songIndex >= 0) {
                            appendLine(
                                "  playlist_$playlistIndex -> song_$songIndex [color=orange];")
                        }
                    }
                }
            }

            appendLine("}")
        }

        // Write to internal storage
        val file = File(context.filesDir, fileName)
        file.writeText(dot)
    }

    private fun escape(text: String, maxLength: Int = 50): String {
        return text.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").take(maxLength)
    }
}

private class MusicGraphBuilderImpl : MusicGraph.Builder {
    private val songVertices = mutableMapOf<Music.UID, SongVertex>()
    private val albumVertices = mutableMapOf<PreAlbum, AlbumVertex>()
    private val artistVertices = mutableMapOf<PreArtist, ArtistVertex>()
    private val genreVertices = mutableMapOf<PreGenre, GenreVertex>()
    private val playlistVertices = mutableSetOf<PlaylistVertex>()

    override fun add(preSong: PreSong) {
        val uid = preSong.v363Uid
        if (songVertices.containsKey(uid)) {
            return
        }

        val songGenreVertices =
            preSong.preGenres.map { preGenre ->
                genreVertices.getOrPut(preGenre) { GenreVertex(preGenre) }
            }

        val songArtistVertices =
            preSong.preArtists.map { preArtist ->
                artistVertices.getOrPut(preArtist) { ArtistVertex(preArtist) }
            }

        val albumVertex =
            albumVertices.getOrPut(preSong.preAlbum) {
                // Albums themselves have their own parent artists that also need to be
                // linked up.
                val albumArtistVertices =
                    preSong.preAlbum.preArtists.preArtists.map { preArtist ->
                        artistVertices.getOrPut(preArtist) { ArtistVertex(preArtist) }
                    }
                val albumVertex = AlbumVertex(preSong.preAlbum, albumArtistVertices.toMutableList())
                // Album vertex is linked, now link artists back to album.
                albumArtistVertices.forEach { artistVertex ->
                    artistVertex.albumVertices.add(albumVertex)
                }
                albumVertex
            }

        val songVertex =
            SongVertex(
                preSong,
                albumVertex,
                songArtistVertices.toMutableList(),
                songGenreVertices.toMutableList())
        albumVertex.songVertices.add(songVertex)

        songArtistVertices.forEach { artistVertex ->
            artistVertex.songVertices.add(songVertex)
            songGenreVertices.forEach { genreVertex ->
                // Mutually link any new genres to the artist
                artistVertex.genreVertices.add(genreVertex)
                genreVertex.artistVertices.add(artistVertex)
            }
        }

        songGenreVertices.forEach { genreVertex -> genreVertex.songVertices.add(songVertex) }

        songVertices[uid] = songVertex
    }

    override fun add(prePlaylist: PrePlaylist) {
        playlistVertices.add(PlaylistVertex(prePlaylist))
    }

    override fun build(): MusicGraph {
        val genreClusters = genreVertices.values.groupBy { it.preGenre.rawName?.lowercase() }
        for (cluster in genreClusters.values) {
            simplifyGenreCluster(cluster)
        }

        // first pass: cluster artists by name and process MBIDs where valid
        val artistClusters = artistVertices.values.groupBy { it.preArtist.rawName?.lowercase() }
        for (cluster in artistClusters.values) {
            simplifyArtistCluster(cluster)
        }
        // second pass: cluster artists by mbid and identify invalid ones
        // (i.e artists with the same mbid but different metadata)
        // this only applies to artists w/mbids, if we dont do this well meld all non-mbid
        // artists into a single vertex which is incorrect
        val mbidClusters =
            artistVertices.values.groupBy { it.preArtist.musicBrainzId }.filter { it.key != null }
        for (cluster in mbidClusters.values) {
            // everything in the cluster must have the same pre-artist
            val canon = cluster.maxBy { it.songVertices.size }.preArtist
            val same = cluster.all { it.preArtist == canon }
            if (!same) {
                // invalid mbid setup, collapse verts to one canon vert
                val simpleMbidVertex =
                    artistVertices.getOrPut(canon) { ArtistVertex(canon) }
                for (artist in cluster) {
                    meldArtistVertices(artist, simpleMbidVertex)
                }
            }
        }

        // first pass: cluster albums by name and process MBIDs where valid
        val albumClusters = albumVertices.values.groupBy { it.preAlbum.rawName?.lowercase() }
        for (cluster in albumClusters.values) {
            simplifyAlbumCluster(cluster)
        }
        // second pass: cluster albums by mbid and identify invalid ones
        // (i.e albums with the same mbid but different metadata)
        // this only applies to albums w/mbids, if we dont do this well meld all non-mbid
        // albums into a single vertex which is incorrect
        val mbidAlbumClusters =
            albumVertices.values.groupBy { it.preAlbum.musicBrainzId }.filter { it.key != null }
        for (cluster in mbidAlbumClusters.values) {
            // everything in the cluster must have the same pre-album
            val canon = cluster.maxBy { it.songVertices.size }.preAlbum
            val same = cluster.all { it.preAlbum == canon }
            if (!same) {
                // invalid mbid setup, collapse verts to one canon vert
                val simpleMbidVertex =
                    albumVertices.getOrPut(canon) {
                        AlbumVertex(canon, mutableListOf())
                    }
                for (album in cluster) {
                    meldAlbumVertices(album, simpleMbidVertex)
                }
            }
        }

        // Remove any edges that wound up connecting to the same artist or genre
        // in the end after simplification.
        albumVertices.values.forEach {
            it.artistVertices = it.artistVertices.distinct().toMutableList()
        }

        songVertices.entries.forEach { entry ->
            val vertex = entry.value
            vertex.artistVertices = vertex.artistVertices.distinct().toMutableList()
            vertex.genreVertices = vertex.genreVertices.distinct().toMutableList()

            playlistVertices.forEach {
                val v363Pointer = SongPointer.UID(entry.key)
                it.pointerMap[v363Pointer]?.forEach { index -> it.songVertices[index] = vertex }
                val v400Pointer = SongPointer.UID(entry.value.preSong.v400Uid)
                it.pointerMap[v400Pointer]?.forEach { index -> it.songVertices[index] = vertex }
                val v401Pointer = SongPointer.UID(entry.value.preSong.v401Uid)
                it.pointerMap[v401Pointer]?.forEach { index -> it.songVertices[index] = vertex }
            }
        }

        val graph =
            MusicGraph(
                songVertices.values.toList(),
                albumVertices.values.toList(),
                artistVertices.values.toList(),
                genreVertices.values.toList(),
                playlistVertices)

        return graph
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
        genreVertices.remove(src.preGenre)
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
                    artistVertices.getOrPut(noMbidPreArtist) { ArtistVertex(noMbidPreArtist) }
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
            // There can be duplicate artist vertices that we need to
            // all replace when melding.
            for (idx in it.artistVertices.indices) {
                if (it.artistVertices[idx] == src) {
                    it.artistVertices[idx] = dst
                }
            }
        }
        src.albumVertices.forEach {
            // There can be duplicate artist vertices that we need to
            // all replace when melding.
            for (idx in it.artistVertices.indices) {
                if (it.artistVertices[idx] == src) {
                    it.artistVertices[idx] = dst
                }
            }
        }
        src.genreVertices.forEach {
            it.artistVertices.remove(src)
            it.artistVertices.add(dst)
        }

        // Remove the irrelevant artist from the graph.
        artistVertices.remove(src.preArtist)
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
        val strippedMbidCluster =
            cluster.map {
                val noMbidPreAlbum = it.preAlbum.copy(musicBrainzId = null)
                val simpleMbidVertex =
                    albumVertices.getOrPut(noMbidPreAlbum) {
                        AlbumVertex(noMbidPreAlbum, it.artistVertices.toMutableList())
                    }
                meldAlbumVertices(it, simpleMbidVertex)
                simpleMbidVertex
            }
        val fullAlbumArtistCoverage =
            strippedMbidCluster.all { it.preAlbum.preArtists is PreArtistsFrom.Album }
        if (fullAlbumArtistCoverage) {
            // All albums have album artists, we can reasonably cluster around artists
            // rather than just name.
            val albumArtistClusters =
                strippedMbidCluster.groupBy { it.preAlbum.preArtists.preArtists }
            for (albumArtistCluster in albumArtistClusters.values) {
                simplifyAlbumClusterImpl(albumArtistCluster)
            }
            return
        }
        val strippedAlbumArtistCluster =
            strippedMbidCluster.map {
                val noAlbumArtistPreAlbum =
                    it.preAlbum.copy(
                        preArtists = PreArtistsFrom.Individual(it.preAlbum.preArtists.preArtists))
                val simpleAlbumArtistVertex =
                    albumVertices.getOrPut(noAlbumArtistPreAlbum) {
                        AlbumVertex(noAlbumArtistPreAlbum, it.artistVertices.toMutableList())
                    }
                meldAlbumVertices(it, simpleAlbumArtistVertex)
                simpleAlbumArtistVertex
            }
        simplifyAlbumClusterImpl(strippedAlbumArtistCluster)
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
            meldAlbumVertices(src, dst)
        }
    }

    private fun meldAlbumVertices(src: AlbumVertex, dst: AlbumVertex) {
        if (src == dst) {
            // Same vertex, do nothing
            return
        }
        // Link all songs and artists from the irrelevant album to the relevant album.
        dst.songVertices.addAll(src.songVertices)
        dst.artistVertices.addAll(src.artistVertices)
        // Update all songs and artists to point to the relevant album.
        src.songVertices.forEach { it.albumVertex = dst }
        src.artistVertices.forEach {
            it.albumVertices.remove(src)
            it.albumVertices.add(dst)
        }
        // Remove the irrelevant album from the graph.
        albumVertices.remove(src.preAlbum)
    }
}

internal interface Vertex {
    val tag: Any?
}

internal class SongVertex(
    val preSong: PreSong,
    var albumVertex: AlbumVertex,
    var artistVertices: MutableList<ArtistVertex>,
    var genreVertices: MutableList<GenreVertex>
) : Vertex {
    override var tag: Any? = null

    override fun toString() = "SongVertex(preSong=$preSong)"
}

internal class AlbumVertex(val preAlbum: PreAlbum, var artistVertices: MutableList<ArtistVertex>) :
    Vertex {
    val songVertices = mutableSetOf<SongVertex>()
    override var tag: Any? = null

    override fun toString() = "AlbumVertex(preAlbum=$preAlbum)"
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

internal class GenreVertex(val preGenre: PreGenre) : Vertex {
    val songVertices = mutableSetOf<SongVertex>()
    val artistVertices = mutableSetOf<ArtistVertex>()
    override var tag: Any? = null

    override fun toString() = "GenreVertex(preGenre=$preGenre)"
}

internal class PlaylistVertex(val prePlaylist: PrePlaylist) {
    val songVertices = Array<SongVertex?>(prePlaylist.songPointers.size) { null }
    val pointerMap =
        prePlaylist.songPointers
            .withIndex()
            .groupBy { it.value }
            .mapValuesTo(mutableMapOf()) { entry -> entry.value.map { it.index } }
    val tag: Any? = null
}

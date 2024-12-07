/*
 * Copyright (c) 2024 Auxio Project
 * Library.kt is part of Auxio.
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
 
package org.oxycblt.musikr.model

import javax.inject.Inject
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Library
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.graph.AlbumVertex
import org.oxycblt.musikr.graph.ArtistVertex
import org.oxycblt.musikr.graph.GenreVertex
import org.oxycblt.musikr.graph.MusicGraph
import org.oxycblt.musikr.graph.SongVertex

interface MutableLibrary : Library {
    suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary

    suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary

    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun deletePlaylist(playlist: Playlist): MutableLibrary
}

interface LibraryFactory {
    fun create(graph: MusicGraph): MutableLibrary
}

class LibraryFactoryImpl @Inject constructor() : LibraryFactory {
    override fun create(graph: MusicGraph): MutableLibrary {
        val songs =
            graph.songVertex.mapTo(mutableSetOf()) { vertex ->
                SongImpl(SongVertexHandle(vertex)).also { vertex.tag = it }
            }
        val albums =
            graph.albumVertex.mapTo(mutableSetOf()) { vertex ->
                AlbumImpl(AlbumVertexHandle(vertex)).also { vertex.tag = it }
            }
        val artists =
            graph.artistVertex.mapTo(mutableSetOf()) { vertex ->
                ArtistImpl(ArtistVertexHandle(vertex)).also { vertex.tag = it }
            }
        val genres =
            graph.genreVertex.mapTo(mutableSetOf()) { vertex ->
                GenreImpl(GenreVertexHandle(vertex)).also { vertex.tag = it }
            }
        return LibraryImpl(songs, albums, artists, genres)
    }

    private class SongVertexHandle(private val vertex: SongVertex) : SongHandle {
        override val preSong = vertex.preSong

        override fun resolveAlbum() = vertex.albumVertex.tag as Album

        override fun resolveArtists() = vertex.artistVertices.map { it.tag as Artist }

        override fun resolveGenres() = vertex.genreVertices.map { it.tag as Genre }
    }

    private class AlbumVertexHandle(private val vertex: AlbumVertex) : AlbumHandle {
        override val preAlbum = vertex.preAlbum

        override val songs = vertex.songVertices.map { SongImpl(SongVertexHandle(it)) }

        override fun resolveArtists() = vertex.artistVertices.map { it.tag as Artist }
    }

    private class ArtistVertexHandle(private val vertex: ArtistVertex) : ArtistHandle {
        override val preArtist = vertex.preArtist

        override val songs = vertex.songVertices.mapTo(mutableSetOf()) { it.tag as Song }

        override val albums = vertex.albumVertices.mapTo(mutableSetOf()) { it.tag as Album }

        override fun resolveGenres() =
            vertex.genreVertices.mapTo(mutableSetOf()) { it.tag as Genre }
    }

    private class GenreVertexHandle(vertex: GenreVertex) : GenreHandle {
        override val preGenre = vertex.preGenre

        override val songs = vertex.songVertices.mapTo(mutableSetOf()) { it.tag as Song }

        override val artists = vertex.artistVertices.mapTo(mutableSetOf()) { it.tag as Artist }
    }
}

class LibraryImpl(
    override val songs: Collection<SongImpl>,
    override val albums: Collection<AlbumImpl>,
    override val artists: Collection<ArtistImpl>,
    override val genres: Collection<GenreImpl>
) : MutableLibrary {
    override val playlists = emptySet<Playlist>()

    private val songUidMap = songs.associateBy { it.uid }
    private val albumUidMap = albums.associateBy { it.uid }
    private val artistUidMap = artists.associateBy { it.uid }
    private val genreUidMap = genres.associateBy { it.uid }
    private val playlistUidMap = playlists.associateBy { it.uid }

    override fun findSong(uid: Music.UID) = songUidMap[uid]

    override fun findSongByPath(path: Path) = songs.find { it.path == path }

    override fun findAlbum(uid: Music.UID) = albumUidMap[uid]

    override fun findArtist(uid: Music.UID) = artistUidMap[uid]

    override fun findGenre(uid: Music.UID) = genreUidMap[uid]

    override fun findPlaylist(uid: Music.UID) = playlistUidMap[uid]

    override fun findPlaylistByName(name: String) = playlists.find { it.name.raw == name }

    override suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary {
        return this
    }

    override suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary {
        return this
    }

    override suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary {
        return this
    }

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary {
        return this
    }

    override suspend fun deletePlaylist(playlist: Playlist): MutableLibrary {
        return this
    }
}

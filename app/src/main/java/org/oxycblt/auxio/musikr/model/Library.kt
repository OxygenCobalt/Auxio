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
 
package org.oxycblt.auxio.musikr.model

import org.oxycblt.auxio.music.Library
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.musikr.fs.Path

interface MutableLibrary : Library {
    suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary

    suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary

    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun deletePlaylist(playlist: Playlist): MutableLibrary
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

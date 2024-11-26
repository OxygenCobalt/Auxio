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
 
package org.oxycblt.auxio.music.stack.interpret.model

import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Library
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.stack.explore.fs.Path

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

    override fun findSong(uid: Music.UID): Song? {
        TODO("Not yet implemented")
    }

    override fun findSongByPath(path: Path): Song? {
        TODO("Not yet implemented")
    }

    override fun findAlbum(uid: Music.UID): Album? {
        TODO("Not yet implemented")
    }

    override fun findArtist(uid: Music.UID): Artist? {
        TODO("Not yet implemented")
    }

    override fun findGenre(uid: Music.UID): Genre? {
        TODO("Not yet implemented")
    }

    override fun findPlaylist(uid: Music.UID): Playlist? {
        TODO("Not yet implemented")
    }

    override fun findPlaylistByName(name: String): Playlist? {
        TODO("Not yet implemented")
    }

    override suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(playlist: Playlist): MutableLibrary {
        TODO("Not yet implemented")
    }
}

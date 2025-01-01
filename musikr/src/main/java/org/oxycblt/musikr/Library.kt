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
 
package org.oxycblt.musikr

import org.oxycblt.musikr.fs.Path

interface Library {
    val songs: Collection<Song>
    val albums: Collection<Album>
    val artists: Collection<Artist>
    val genres: Collection<Genre>
    val playlists: Collection<Playlist>

    fun empty(): Boolean

    fun findSong(uid: Music.UID): Song?

    fun findSongByPath(path: Path): Song?

    fun findAlbum(uid: Music.UID): Album?

    fun findArtist(uid: Music.UID): Artist?

    fun findGenre(uid: Music.UID): Genre?

    fun findPlaylist(uid: Music.UID): Playlist?

    fun findPlaylistByName(name: String): Playlist?
}

interface MutableLibrary : Library {
    suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary

    suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary

    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun deletePlaylist(playlist: Playlist): MutableLibrary
}

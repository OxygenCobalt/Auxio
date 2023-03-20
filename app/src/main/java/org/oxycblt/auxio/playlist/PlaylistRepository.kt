/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistRepository.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playlist

import java.util.UUID
import javax.inject.Inject
import org.oxycblt.auxio.music.Song

interface PlaylistRepository {
    val playlists: List<Playlist>
    suspend fun createPlaylist(name: String, songs: List<Song>)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>)
    suspend fun removeFromPlaylist(playlist: Playlist, song: Song)
    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>)
}

class PlaylistRepositoryImpl @Inject constructor() : PlaylistRepository {
    private val playlistMap = mutableMapOf<UUID, PlaylistImpl>()
    override val playlists: List<Playlist>
        get() = playlistMap.values.toList()

    override suspend fun createPlaylist(name: String, songs: List<Song>) {
        val uuid = UUID.randomUUID()
        playlistMap[uuid] = PlaylistImpl(uuid, name, songs)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistMap.remove(playlist.id)
    }

    override suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>) {
        editPlaylist(playlist) {
            addAll(songs)
            this
        }
    }

    override suspend fun removeFromPlaylist(playlist: Playlist, song: Song) {
        editPlaylist(playlist) {
            remove(song)
            this
        }
    }

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>) {
        editPlaylist(playlist) { songs }
    }

    private inline fun editPlaylist(playlist: Playlist, edits: MutableList<Song>.() -> List<Song>) {
        check(playlistMap.containsKey(playlist.id)) { "Invalid playlist argument provided" }
        playlistMap[playlist.id] =
            PlaylistImpl(playlist.id, playlist.name, edits(playlist.songs.toMutableList()))
    }
}

private data class PlaylistImpl(
    override val id: UUID,
    override val name: String,
    override val songs: List<Song>
) : Playlist

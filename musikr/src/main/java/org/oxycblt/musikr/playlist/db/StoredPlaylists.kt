/*
 * Copyright (c) 2024 Auxio Project
 * StoredPlaylists.kt is part of Auxio.
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
 
package org.oxycblt.musikr.playlist.db

import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.playlist.SongPointer

interface StoredPlaylists {
    suspend fun read(): List<PlaylistFile>

    companion object {
        fun from(database: PlaylistDatabase): StoredPlaylists =
            StoredPlaylistsImpl(database.playlistDao())
    }
}

private class StoredPlaylistsImpl(private val playlistDao: PlaylistDao) : StoredPlaylists {
    override suspend fun read() =
        playlistDao.readRawPlaylists().map {
            PlaylistFile(
                it.playlistInfo.name,
                it.songs.map { song -> SongPointer.UID(song.songUid) },
                StoredPlaylistHandle(it.playlistInfo, playlistDao))
        }
}

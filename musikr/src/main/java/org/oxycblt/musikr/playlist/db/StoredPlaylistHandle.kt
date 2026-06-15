/*
 * Copyright (c) 2024 Auxio Project
 * StoredPlaylistHandle.kt is part of Auxio.
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

import org.oxycblt.musikr.Song
import org.oxycblt.musikr.playlist.PlaylistHandle

internal class StoredPlaylistHandle(
    private val playlistInfo: PlaylistInfo,
    private val playlistDao: PlaylistDao
) : PlaylistHandle {
    override val uid = playlistInfo.playlistUid

    override suspend fun rename(name: String) {
        playlistDao.replacePlaylistInfo(playlistInfo.copy(name = name))
    }

    override suspend fun rewrite(songs: List<Song>) {
        playlistDao.replacePlaylistSongs(uid, songs.map { PlaylistSong(it.uid) })
    }

    override suspend fun add(songs: List<Song>) {
        playlistDao.insertPlaylistSongs(uid, songs.map { PlaylistSong(it.uid) })
    }

    override suspend fun delete() {
        playlistDao.deletePlaylist(uid)
    }
}

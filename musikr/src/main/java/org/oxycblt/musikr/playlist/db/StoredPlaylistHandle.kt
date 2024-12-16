package org.oxycblt.musikr.playlist.db

import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.playlist.PlaylistHandle
import java.util.UUID

internal class StoredPlaylistHandle(
    private val playlistInfo: PlaylistInfo,
    private val playlistDao: PlaylistDao
) : PlaylistHandle {
    override val uid = playlistInfo.playlistUid

    override suspend fun rename(name: String) {
        playlistDao.replacePlaylistInfo(playlistInfo.copy(name = name))
    }

    override suspend fun rewrite(songs: List<Song>) {
        playlistDao.replacePlaylistSongs(
            uid,
            songs.map { PlaylistSong(it.uid) }
        )
    }

    override suspend fun add(songs: List<Song>) {
        playlistDao.insertPlaylistSongs(
            uid,
            songs.map { PlaylistSong(it.uid) }
        )
    }

    override suspend fun delete() {
        playlistDao.deletePlaylist(uid)
    }

}
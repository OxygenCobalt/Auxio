package org.oxycblt.auxio.music.stack.explore.playlists

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.oxycblt.auxio.music.stack.explore.PlaylistFile
import org.oxycblt.auxio.music.stack.explore.SongPointer
import javax.inject.Inject

interface StoredPlaylists {
    fun read(): Flow<PlaylistFile>
}

class StoredPlaylistsImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : StoredPlaylists {
    override fun read() = flow {
        emitAll(playlistDao.readRawPlaylists()
            .asFlow()
            .map {
                TODO()
            })
    }
}
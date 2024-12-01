package org.oxycblt.auxio.musikr.playlist

import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song

data class PlaylistFile(
    val name: String,
    val songPointers: List<SongPointer>,
    val editor: PlaylistHandle
)

sealed interface SongPointer {
    data class UID(val uid: Music.UID) : SongPointer
    //    data class Path(val options: List<Path>) : SongPointer
}

interface PlaylistHandle {
    val uid: Music.UID

    suspend fun rename(name: String)

    suspend fun add(songs: List<Song>)

    suspend fun rewrite(songs: List<Song>)

    suspend fun delete()
}

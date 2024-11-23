package org.oxycblt.auxio.music.stack.interpret.linker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.oxycblt.auxio.music.stack.interpret.model.AlbumImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl


class AlbumLinker {
    fun register(linkedSongs: Flow<ArtistLinker.LinkedSong>): Flow<LinkedSong> = emptyFlow()
    fun resolve(): Collection<AlbumImpl> = setOf()

    data class LinkedSong(
        val linkedArtistSong: ArtistLinker.LinkedSong,
        val album: Linked<AlbumImpl, SongImpl>
    )
}
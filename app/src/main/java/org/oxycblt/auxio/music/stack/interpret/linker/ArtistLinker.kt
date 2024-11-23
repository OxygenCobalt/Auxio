package org.oxycblt.auxio.music.stack.interpret.linker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.oxycblt.auxio.music.stack.interpret.model.AlbumImpl
import org.oxycblt.auxio.music.stack.interpret.model.ArtistImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreAlbum


class ArtistLinker {
    fun register(preSong: Flow<GenreLinker.LinkedSong>): Flow<LinkedSong> = emptyFlow()
    fun resolve(): Collection<ArtistImpl> = setOf()

    data class LinkedSong(
        val linkedGenreSong: GenreLinker.LinkedSong,
        val linkedAlbum: LinkedAlbum,
        val artists: Linked<List<ArtistImpl>, SongImpl>
    )

    data class LinkedAlbum(
        val preAlbum: PreAlbum,
        val artists: Linked<List<ArtistImpl>, AlbumImpl>
    )
}

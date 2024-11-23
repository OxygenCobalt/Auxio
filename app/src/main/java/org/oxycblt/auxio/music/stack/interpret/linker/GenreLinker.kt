package org.oxycblt.auxio.music.stack.interpret.linker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.oxycblt.auxio.music.stack.interpret.model.GenreImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreSong

class GenreLinker {
    fun register(preSong: Flow<PreSong>): Flow<LinkedSong> = emptyFlow()
    fun resolve(): Collection<GenreImpl> = setOf()

    data class LinkedSong(
        val preSong: PreSong,
        val genres: Linked<List<GenreImpl>, SongImpl>
    )
}

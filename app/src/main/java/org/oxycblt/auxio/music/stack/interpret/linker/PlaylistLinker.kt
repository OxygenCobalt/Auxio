package org.oxycblt.auxio.music.stack.interpret.linker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.oxycblt.auxio.music.stack.explore.PlaylistFile
import org.oxycblt.auxio.music.stack.interpret.model.GenreImpl
import org.oxycblt.auxio.music.stack.interpret.model.PlaylistImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreSong


class PlaylistLinker {
    fun register(playlists: Flow<PlaylistFile>, linkedSongs: Flow<AlbumLinker.LinkedSong>): Flow<LinkedPlaylist> = emptyFlow()
    fun resolve(): Collection<PlaylistImpl> = setOf()
}

package org.oxycblt.auxio.music.stack.interpret.linker

import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.stack.explore.PlaylistFile
import org.oxycblt.auxio.music.stack.interpret.model.AlbumImpl
import org.oxycblt.auxio.music.stack.interpret.model.ArtistImpl
import org.oxycblt.auxio.music.stack.interpret.model.GenreImpl
import org.oxycblt.auxio.music.stack.interpret.model.PlaylistImpl
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreAlbum
import org.oxycblt.auxio.music.stack.interpret.prepare.PrePlaylist
import org.oxycblt.auxio.music.stack.interpret.prepare.PreSong

interface LinkedSong {
    val preSong: PreSong
    val album: Linked<AlbumImpl, SongImpl>
    val artists: Linked<List<ArtistImpl>, SongImpl>
    val genres: Linked<List<GenreImpl>, SongImpl>
}

interface LinkedAlbum {
    val preAlbum: PreAlbum
    val artists: Linked<List<ArtistImpl>, AlbumImpl>
}

interface LinkedPlaylist {
    val prePlaylist: PrePlaylist
    val songs: Linked<List<SongImpl>, PlaylistImpl>
}

interface Linked<P, C> {
    fun resolve(child: C): P
}

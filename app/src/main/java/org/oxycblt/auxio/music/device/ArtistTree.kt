package org.oxycblt.auxio.music.device


interface AlbumTree {
    fun register(linkedSong: ArtistTree.LinkedSong): LinkedSong
    fun resolve(): Collection<AlbumImpl>

    data class LinkedSong(
        val linkedArtistSong: ArtistTree.LinkedSong,
        val album: Linked<AlbumImpl, SongImpl>
    )
}

interface ArtistTree {
    fun register(preSong: GenreTree.LinkedSong): LinkedSong
    fun resolve(): Collection<ArtistImpl>

    data class LinkedSong(
        val linkedGenreSong: GenreTree.LinkedSong,
        val linkedAlbum: LinkedAlbum,
        val artists: Linked<List<ArtistImpl>,  SongImpl>
    )

    data class LinkedAlbum(
        val preAlbum: PreAlbum,
        val artists: Linked<List<ArtistImpl>, AlbumImpl>
    )
}

interface GenreTree {
    fun register(preSong: PreSong): LinkedSong
    fun resolve(): Collection<GenreImpl>

    data class LinkedSong(
        val preSong: PreSong,
        val genres: Linked<List<GenreImpl>, SongImpl>
    )
}

interface Linked<P, C> {
    fun resolve(child: C): P
}

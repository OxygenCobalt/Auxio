package org.oxycblt.auxio.music.model

import kotlinx.coroutines.flow.Flow


interface AlbumInterpreter {
    suspend fun register(linkedSongs: Flow<ArtistInterpreter.LinkedSong>): Flow<LinkedSong>
    fun resolve(): Collection<AlbumImpl>

    data class LinkedSong(
        val linkedArtistSong: ArtistInterpreter.LinkedSong,
        val album: Linked<AlbumImpl, SongImpl>
    )
}

interface ArtistInterpreter {
    suspend fun register(preSong: Flow<GenreInterpreter.LinkedSong>): Flow<LinkedSong>
    fun resolve(): Collection<ArtistImpl>

    data class LinkedSong(
        val linkedGenreSong: GenreInterpreter.LinkedSong,
        val linkedAlbum: LinkedAlbum,
        val artists: Linked<List<ArtistImpl>, SongImpl>
    )

    data class LinkedAlbum(
        val preAlbum: PreAlbum,
        val artists: Linked<List<ArtistImpl>, AlbumImpl>
    )
}

interface GenreInterpreter {
    suspend fun register(preSong: Flow<PreSong>): Flow<LinkedSong>
    fun resolve(): Collection<GenreImpl>

    data class LinkedSong(
        val preSong: PreSong,
        val genres: Linked<List<GenreImpl>, SongImpl>
    )
}

interface Linked<P, C> {
    fun resolve(child: C): P
}

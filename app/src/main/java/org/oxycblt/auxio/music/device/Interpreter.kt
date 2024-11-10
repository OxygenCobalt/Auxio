package org.oxycblt.auxio.music.device

import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.metadata.Separators
import javax.inject.Inject

interface Interpreter {
    fun consume(rawSong: RawSong)
    fun resolve(): DeviceLibrary

    interface Factory {
        fun create(nameFactory: Name.Known.Factory, separators: Separators): Interpreter
    }
}

class LinkedSong(val albumLinkedSong: AlbumTree.LinkedSong) {
    val preSong: PreSong get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.preSong
    val album: Linked<AlbumImpl, SongImpl> get() = albumLinkedSong.album
    val artists: Linked<List<ArtistImpl>, SongImpl> get() = albumLinkedSong.linkedArtistSong.artists
    val genres: Linked<List<GenreImpl>, SongImpl> get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.genres
}

typealias LinkedAlbum = ArtistTree.LinkedAlbum

class InterpreterFactoryImpl @Inject constructor(
    private val songInterpreterFactory: SongInterpreter.Factory,
    private val albumTree: AlbumTree,
    private val artistTree: ArtistTree,
    private val genreTree: GenreTree
) : Interpreter.Factory {
    override fun create(nameFactory: Name.Known.Factory, separators: Separators): Interpreter =
        InterpreterImpl(
            songInterpreterFactory.create(nameFactory, separators),
            albumTree,
            artistTree,
            genreTree
        )
}

private class InterpreterImpl(
    private val songInterpreter: SongInterpreter,
    private val albumTree: AlbumTree,
    private val artistTree: ArtistTree,
    private val genreTree: GenreTree
) : Interpreter {
    private val songs = mutableListOf<LinkedSong>()

    override fun consume(rawSong: RawSong) {
        val preSong = songInterpreter.consume(rawSong)
        val genreLinkedSong = genreTree.register(preSong)
        val artistLinkedSong = artistTree.register(genreLinkedSong)
        val albumLinkedSong = albumTree.register(artistLinkedSong)
        songs.add(LinkedSong(albumLinkedSong))
    }

    override fun resolve(): DeviceLibrary {
        val genres = genreTree.resolve()
        val artists = artistTree.resolve()
        val albums = albumTree.resolve()
        val songs = songs.map { SongImpl(it) }
        return DeviceLibraryImpl(songs, albums, artists, genres)
    }
}

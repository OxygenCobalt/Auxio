package org.oxycblt.auxio.music.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.oxycblt.auxio.music.stack.AudioFile
import org.oxycblt.auxio.music.stack.PlaylistFile

interface Interpreter {
    suspend fun interpret(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation
    ): MutableLibrary
}

class LinkedSong(private val albumLinkedSong: AlbumInterpreter.LinkedSong) {
    val preSong: PreSong get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.preSong
    val album: Linked<AlbumImpl, SongImpl> get() = albumLinkedSong.album
    val artists: Linked<List<ArtistImpl>, SongImpl> get() = albumLinkedSong.linkedArtistSong.artists
    val genres: Linked<List<GenreImpl>, SongImpl> get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.genres
}

typealias LinkedAlbum = ArtistInterpreter.LinkedAlbum

class InterpreterImpl(
    private val songInterpreter: SongInterpreter
) : Interpreter {
    override suspend fun interpret(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation
    ): MutableLibrary {
        val preSongs =
            songInterpreter.prepare(audioFiles, interpretation).flowOn(Dispatchers.Main)
                .buffer()
        val albumInterpreter = makeAlbumTree()
        val artistInterpreter = makeArtistTree()
        val genreInterpreter = makeGenreTree()

        val genreLinkedSongs = genreInterpreter.register(preSongs).flowOn(Dispatchers.Main).buffer()
        val artistLinkedSongs =
            artistInterpreter.register(genreLinkedSongs).flowOn(Dispatchers.Main).buffer()
        val albumLinkedSongs =
            albumInterpreter.register(artistLinkedSongs).flowOn(Dispatchers.Main)
        val linkedSongs = albumLinkedSongs.map { LinkedSong(it) }.toList()

        val genres = genreInterpreter.resolve()
        val artists = artistInterpreter.resolve()
        val albums = albumInterpreter.resolve()
        val songs = linkedSongs.map { SongImpl(it) }
        return LibraryImpl(songs, albums, artists, genres)
    }

    private fun makeAlbumTree(): AlbumInterpreter {
    }

    private fun makeArtistTree(): ArtistInterpreter {
    }

    private fun makeGenreTree(): GenreInterpreter {
    }

}

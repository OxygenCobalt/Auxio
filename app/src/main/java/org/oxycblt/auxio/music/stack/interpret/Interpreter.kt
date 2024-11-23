package org.oxycblt.auxio.music.stack.interpret

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.oxycblt.auxio.music.stack.explore.AudioFile
import org.oxycblt.auxio.music.stack.explore.PlaylistFile
import org.oxycblt.auxio.music.stack.interpret.linker.AlbumLinker
import org.oxycblt.auxio.music.stack.interpret.linker.ArtistLinker
import org.oxycblt.auxio.music.stack.interpret.linker.GenreLinker
import org.oxycblt.auxio.music.stack.interpret.linker.Linked
import org.oxycblt.auxio.music.stack.interpret.linker.LinkedSong
import org.oxycblt.auxio.music.stack.interpret.model.AlbumImpl
import org.oxycblt.auxio.music.stack.interpret.model.ArtistImpl
import org.oxycblt.auxio.music.stack.interpret.model.GenreImpl
import org.oxycblt.auxio.music.stack.interpret.model.LibraryImpl
import org.oxycblt.auxio.music.stack.interpret.model.MutableLibrary
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.stack.interpret.prepare.PreSong
import org.oxycblt.auxio.music.stack.interpret.prepare.Preparer

interface Interpreter {
    suspend fun interpret(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation
    ): MutableLibrary
}

class InterpreterImpl(
    private val preparer: Preparer
) : Interpreter {
    override suspend fun interpret(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation
    ): MutableLibrary {
        val preSongs =
            preparer.prepare(audioFiles, interpretation).flowOn(Dispatchers.Main)
                .buffer()
        val genreLinker = GenreLinker()
        val genreLinkedSongs = genreLinker.register(preSongs).flowOn(Dispatchers.Main).buffer()
        val artistLinker = ArtistLinker()
        val artistLinkedSongs =
            artistLinker.register(genreLinkedSongs).flowOn(Dispatchers.Main).buffer()
        val albumLinker = AlbumLinker()
        val albumLinkedSongs =
            albumLinker.register(artistLinkedSongs)
                .flowOn(Dispatchers.Main)
                .map { LinkedSongImpl(it) }
                .toList()
        val genres = genreLinker.resolve()
        val artists = artistLinker.resolve()
        val albums = albumLinker.resolve()
        val songs = albumLinkedSongs.map { SongImpl(it) }
        return LibraryImpl(songs, albums, artists, genres)
    }


    private data class LinkedSongImpl(
        private val albumLinkedSong: AlbumLinker.LinkedSong
    ) : LinkedSong {
        override val preSong: PreSong get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.preSong
        override val album: Linked<AlbumImpl, SongImpl> get() = albumLinkedSong.album
        override val artists: Linked<List<ArtistImpl>, SongImpl> get() = albumLinkedSong.linkedArtistSong.artists
        override val genres: Linked<List<GenreImpl>, SongImpl> get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.genres
    }
}

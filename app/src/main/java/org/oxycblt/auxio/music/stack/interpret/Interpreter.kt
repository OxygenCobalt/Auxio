/*
 * Copyright (c) 2024 Auxio Project
 * Interpreter.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.music.stack.interpret

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.stack.Indexer
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
import timber.log.Timber as L

interface Interpreter {
    suspend fun interpret(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation,
        eventHandler: suspend (Indexer.Event) -> Unit
    ): MutableLibrary
}

class InterpreterImpl @Inject constructor(private val preparer: Preparer) : Interpreter {
    override suspend fun interpret(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation,
        eventHandler: suspend (Indexer.Event) -> Unit
    ): MutableLibrary {
        val preSongs =
            preparer.prepare(audioFiles, interpretation).flowOn(Dispatchers.Main).buffer()

        val genreLinker = GenreLinker()
        val genreLinkedSongs = genreLinker.register(preSongs).flowOn(Dispatchers.Main).buffer()

        val artistLinker = ArtistLinker()
        val artistLinkedSongs =
            artistLinker.register(genreLinkedSongs).flowOn(Dispatchers.Main).buffer()
        // This is intentional. Song and album instances are dependent on artist
        // data, so we need to ensure that all of the linked artist data is resolved
        // before we go any further.
        val genres = genreLinker.resolve()
        val artists = artistLinker.resolve()

        var interpreted = 0
        val albumLinker = AlbumLinker()
        val albumLinkedSongs =
            albumLinker
                .register(artistLinkedSongs)
                .flowOn(Dispatchers.Main)
                .onEach {
                    interpreted++
                    eventHandler(Indexer.Event.Interpret(interpreted))
                }
                .map { LinkedSongImpl(it) }
                .toList()
        val albums = albumLinker.resolve()

        val uidMap = mutableMapOf<Music.UID, SongImpl>()
        val songs =
            albumLinkedSongs.mapNotNull {
                val uid = it.preSong.computeUid()
                val other = uidMap[uid]
                if (other == null) {
                    SongImpl(it)
                } else {
                    L.d("Song @ $uid already exists at ${other.path}, ignoring")
                    null
                }
            }
        return LibraryImpl(
            songs,
            albums.onEach { it.finalize() },
            artists.onEach { it.finalize() },
            genres.onEach { it.finalize() })
    }

    private data class LinkedSongImpl(private val albumLinkedSong: AlbumLinker.LinkedSong) :
        LinkedSong {
        override val preSong: PreSong
            get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.preSong

        override val album: Linked<AlbumImpl, SongImpl>
            get() = albumLinkedSong.album

        override val artists: Linked<List<ArtistImpl>, SongImpl>
            get() = albumLinkedSong.linkedArtistSong.artists

        override val genres: Linked<List<GenreImpl>, SongImpl>
            get() = albumLinkedSong.linkedArtistSong.linkedGenreSong.genres
    }
}

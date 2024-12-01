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
 
package org.oxycblt.auxio.musikr.interpret

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.musikr.tag.AudioFile
import org.oxycblt.auxio.musikr.playlist.PlaylistFile
import org.oxycblt.auxio.musikr.interpret.link.AlbumLinker
import org.oxycblt.auxio.musikr.interpret.link.ArtistLinker
import org.oxycblt.auxio.musikr.interpret.link.GenreLinker
import org.oxycblt.auxio.musikr.interpret.link.Linked
import org.oxycblt.auxio.musikr.interpret.link.LinkedSong
import org.oxycblt.auxio.musikr.model.AlbumImpl
import org.oxycblt.auxio.musikr.model.ArtistImpl
import org.oxycblt.auxio.musikr.model.GenreImpl
import org.oxycblt.auxio.musikr.model.LibraryImpl
import org.oxycblt.auxio.musikr.model.MutableLibrary
import org.oxycblt.auxio.musikr.model.SongImpl
import org.oxycblt.auxio.musikr.interpret.prepare.PreSong
import org.oxycblt.auxio.musikr.interpret.prepare.Preparer
import timber.log.Timber as L

interface Modeler {
    suspend fun model(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation
    ): MutableLibrary
}

class ModelerImpl @Inject constructor(private val preparer: Preparer) : Modeler {
    override suspend fun model(
        audioFiles: Flow<AudioFile>,
        playlistFiles: Flow<PlaylistFile>,
        interpretation: Interpretation
    ): MutableLibrary {
        val preSongs =
            preparer
                .interpret(audioFiles, interpretation)
                .flowOn(Dispatchers.Main)
                .buffer(Channel.UNLIMITED)

        val genreLinker = GenreLinker()
        val genreLinkedSongs =
            genreLinker.register(preSongs).flowOn(Dispatchers.Main).buffer(Channel.UNLIMITED)

        val artistLinker = ArtistLinker()
        val artistLinkedSongs =
            artistLinker.register(genreLinkedSongs).flowOn(Dispatchers.Main).toList()
        // This is intentional. Song and album instances are dependent on artist
        // data, so we need to ensure that all of the linked artist data is resolved
        // before we go any further.
        val genres = genreLinker.resolve()
        val artists = artistLinker.resolve()

        val albumLinker = AlbumLinker()
        val albumLinkedSongs =
            albumLinker
                .register(artistLinkedSongs.asFlow())
                .map { LinkedSongImpl(it) }
                .flowOn(Dispatchers.Main)
                .toList()
        val albums = albumLinker.resolve()

        val uidMap = mutableMapOf<Music.UID, SongImpl>()
        val songs =
            albumLinkedSongs.mapNotNull {
                val uid = it.preSong.uid
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

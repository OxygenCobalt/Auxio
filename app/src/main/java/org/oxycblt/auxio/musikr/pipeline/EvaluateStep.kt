/*
 * Copyright (c) 2024 Auxio Project
 * EvaluateStep.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.pipeline

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.musikr.model.graph.AlbumLinker
import org.oxycblt.auxio.musikr.model.graph.ArtistLinker
import org.oxycblt.auxio.musikr.model.graph.GenreLinker
import org.oxycblt.auxio.musikr.model.graph.Linked
import org.oxycblt.auxio.musikr.model.graph.LinkedSong
import org.oxycblt.auxio.musikr.model.impl.AlbumImpl
import org.oxycblt.auxio.musikr.model.impl.ArtistImpl
import org.oxycblt.auxio.musikr.model.impl.GenreImpl
import org.oxycblt.auxio.musikr.model.impl.LibraryImpl
import org.oxycblt.auxio.musikr.model.impl.MutableLibrary
import org.oxycblt.auxio.musikr.model.impl.SongImpl
import org.oxycblt.auxio.musikr.tag.Interpretation
import org.oxycblt.auxio.musikr.tag.interpret.PreSong
import org.oxycblt.auxio.musikr.tag.interpret.TagInterpreter
import timber.log.Timber

interface EvaluateStep {
    suspend fun evaluate(
        interpretation: Interpretation,
        extractedMusic: Flow<ExtractedMusic>
    ): MutableLibrary
}

class EvaluateStepImpl
@Inject
constructor(
    private val tagInterpreter: TagInterpreter,
) : EvaluateStep {
    override suspend fun evaluate(
        interpretation: Interpretation,
        extractedMusic: Flow<ExtractedMusic>
    ): MutableLibrary {
        val preSongs =
            extractedMusic
                .filterIsInstance<ExtractedMusic.Song>()
                .map { tagInterpreter.interpret(it.file, it.tags, interpretation) }
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
                val uid = it.preSong.computeUid()
                val other = uidMap[uid]
                if (other == null) {
                    SongImpl(it)
                } else {
                    Timber.d("Song @ $uid already exists at ${other.path}, ignoring")
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

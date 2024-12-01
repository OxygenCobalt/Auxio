/*
 * Copyright (c) 2024 Auxio Project
 * Preparer.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.model.interpret

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.musikr.tag.AudioFile
import org.oxycblt.auxio.musikr.fs.MimeType
import org.oxycblt.auxio.musikr.model.Interpretation
import org.oxycblt.auxio.playback.replaygain.ReplayGainAdjustment
import org.oxycblt.auxio.util.toUuidOrNull

interface Interpreter {
    fun interpret(audioFiles: Flow<AudioFile>, interpretation: Interpretation): Flow<PreSong>
}

class InterpreterImpl @Inject constructor() : Interpreter {
    override fun interpret(audioFiles: Flow<AudioFile>, interpretation: Interpretation) =
        audioFiles.map { audioFile ->
            val individualPreArtists =
                makePreArtists(
                    audioFile.artistMusicBrainzIds,
                    audioFile.artistNames,
                    audioFile.artistSortNames,
                    interpretation)
            val albumPreArtists =
                makePreArtists(
                    audioFile.albumArtistMusicBrainzIds,
                    audioFile.albumArtistNames,
                    audioFile.albumArtistSortNames,
                    interpretation)
            val preAlbum =
                makePreAlbum(audioFile, individualPreArtists, albumPreArtists, interpretation)
            val rawArtists =
                individualPreArtists
                    .ifEmpty { albumPreArtists }
                    .ifEmpty { listOf(unknownPreArtist()) }
            val rawGenres =
                makePreGenres(audioFile, interpretation).ifEmpty { listOf(unknownPreGenre()) }
            val uri = audioFile.deviceFile.uri
            PreSong(
                musicBrainzId = audioFile.musicBrainzId?.toUuidOrNull(),
                name =
                    interpretation.nameFactory.parse(
                        need(audioFile, "name", audioFile.name), audioFile.sortName),
                rawName = audioFile.name,
                track = audioFile.track,
                disc = audioFile.disc?.let { Disc(it, audioFile.subtitle) },
                date = audioFile.date,
                uri = uri,
                path = need(audioFile, "path", audioFile.deviceFile.path),
                mimeType =
                    MimeType(need(audioFile, "mime type", audioFile.deviceFile.mimeType), null),
                size = audioFile.deviceFile.size,
                durationMs = need(audioFile, "duration", audioFile.durationMs),
                replayGainAdjustment =
                    ReplayGainAdjustment(
                        audioFile.replayGainTrackAdjustment,
                        audioFile.replayGainAlbumAdjustment,
                    ),
                lastModified = audioFile.deviceFile.lastModified,
                // TODO: Figure out what to do with date added
                dateAdded = audioFile.deviceFile.lastModified,
                preAlbum = preAlbum,
                preArtists = rawArtists,
                preGenres = rawGenres)
        }

    private fun <T> need(audioFile: AudioFile, what: String, value: T?) =
        requireNotNull(value) { "Invalid $what for song ${audioFile.deviceFile.path}: No $what" }

    private fun makePreAlbum(
        audioFile: AudioFile,
        individualPreArtists: List<PreArtist>,
        albumPreArtists: List<PreArtist>,
        interpretation: Interpretation
    ): PreAlbum {
        val rawAlbumName = need(audioFile, "album name", audioFile.albumName)
        return PreAlbum(
            musicBrainzId = audioFile.albumMusicBrainzId?.toUuidOrNull(),
            name = interpretation.nameFactory.parse(rawAlbumName, audioFile.albumSortName),
            rawName = rawAlbumName,
            releaseType =
                ReleaseType.parse(interpretation.separators.split(audioFile.releaseTypes))
                    ?: ReleaseType.Album(null),
            preArtists =
                albumPreArtists
                    .ifEmpty { individualPreArtists }
                    .ifEmpty { listOf(unknownPreArtist()) })
    }

    private fun makePreArtists(
        rawMusicBrainzIds: List<String>,
        rawNames: List<String>,
        rawSortNames: List<String>,
        interpretation: Interpretation
    ): List<PreArtist> {
        val musicBrainzIds = interpretation.separators.split(rawMusicBrainzIds)
        val names = interpretation.separators.split(rawNames)
        val sortNames = interpretation.separators.split(rawSortNames)
        return names.mapIndexed { i, name ->
            makePreArtist(musicBrainzIds.getOrNull(i), name, sortNames.getOrNull(i), interpretation)
        }
    }

    private fun makePreArtist(
        musicBrainzId: String?,
        rawName: String?,
        sortName: String?,
        interpretation: Interpretation
    ): PreArtist {
        val name =
            rawName?.let { interpretation.nameFactory.parse(it, sortName) }
                ?: Name.Unknown(R.string.def_artist)
        val musicBrainzId = musicBrainzId?.toUuidOrNull()
        return PreArtist(musicBrainzId, name, rawName)
    }

    private fun unknownPreArtist() = PreArtist(null, Name.Unknown(R.string.def_artist), null)

    private fun makePreGenres(
        audioFile: AudioFile,
        interpretation: Interpretation
    ): List<PreGenre> {
        val genreNames =
            audioFile.genreNames.parseId3GenreNames()
                ?: interpretation.separators.split(audioFile.genreNames)
        return genreNames.map { makePreGenre(it, interpretation) }
    }

    private fun makePreGenre(rawName: String?, interpretation: Interpretation) =
        PreGenre(
            rawName?.let { interpretation.nameFactory.parse(it, null) }
                ?: Name.Unknown(R.string.def_genre),
            rawName)

    private fun unknownPreGenre() = PreGenre(Name.Unknown(R.string.def_genre), null)
}

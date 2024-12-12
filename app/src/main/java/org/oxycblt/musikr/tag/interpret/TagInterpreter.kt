/*
 * Copyright (c) 2024 Auxio Project
 * TagInterpreter.kt is part of Auxio.
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
 
package org.oxycblt.musikr.tag.interpret

import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.replaygain.ReplayGainAdjustment
import org.oxycblt.auxio.util.toUuidOrNull
import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.fs.MimeType
import org.oxycblt.musikr.fs.query.DeviceFile
import org.oxycblt.musikr.tag.Disc
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.ReleaseType
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.util.parseId3GenreNames

interface TagInterpreter {
    fun interpret(
        file: DeviceFile,
        parsedTags: ParsedTags,
        cover: Cover.Single?,
        interpretation: Interpretation
    ): PreSong
}

class TagInterpreterImpl @Inject constructor() : TagInterpreter {
    override fun interpret(
        file: DeviceFile,
        parsedTags: ParsedTags,
        cover: Cover.Single?,
        interpretation: Interpretation
    ): PreSong {
        val individualPreArtists =
            makePreArtists(
                parsedTags.artistMusicBrainzIds,
                parsedTags.artistNames,
                parsedTags.artistSortNames,
                interpretation)
        val albumPreArtists =
            makePreArtists(
                parsedTags.albumArtistMusicBrainzIds,
                parsedTags.albumArtistNames,
                parsedTags.albumArtistSortNames,
                interpretation)
        val preAlbum =
            makePreAlbum(file, parsedTags, individualPreArtists, albumPreArtists, interpretation)
        val rawArtists =
            individualPreArtists.ifEmpty { albumPreArtists }.ifEmpty { listOf(unknownPreArtist()) }
        val rawGenres =
            makePreGenres(parsedTags, interpretation).ifEmpty { listOf(unknownPreGenre()) }
        val uri = file.uri
        return PreSong(
            musicBrainzId = parsedTags.musicBrainzId?.toUuidOrNull(),
            name = interpretation.nameFactory.parse(parsedTags.name, parsedTags.sortName),
            rawName = parsedTags.name,
            track = parsedTags.track,
            disc = parsedTags.disc?.let { Disc(it, parsedTags.subtitle) },
            date = parsedTags.date,
            uri = uri,
            path = file.path,
            mimeType = MimeType(file.mimeType, null),
            size = file.size,
            durationMs = parsedTags.durationMs,
            replayGainAdjustment =
                ReplayGainAdjustment(
                    parsedTags.replayGainTrackAdjustment,
                    parsedTags.replayGainAlbumAdjustment,
                ),
            lastModified = file.lastModified,
            // TODO: Figure out what to do with date added
            dateAdded = file.lastModified,
            preAlbum = preAlbum,
            preArtists = rawArtists,
            preGenres = rawGenres,
            cover = null)
    }

    private fun makePreAlbum(
        file: DeviceFile,
        parsedTags: ParsedTags,
        individualPreArtists: List<PreArtist>,
        albumPreArtists: List<PreArtist>,
        interpretation: Interpretation
    ): PreAlbum {
        // TODO: Make fallbacks for this!
        val rawAlbumName = requireNotNull(parsedTags.albumName)
        return PreAlbum(
            musicBrainzId = parsedTags.albumMusicBrainzId?.toUuidOrNull(),
            name = interpretation.nameFactory.parse(rawAlbumName, parsedTags.albumSortName),
            rawName = rawAlbumName,
            releaseType =
                ReleaseType.parse(interpretation.separators.split(parsedTags.releaseTypes))
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
        parsedTags: ParsedTags,
        interpretation: Interpretation
    ): List<PreGenre> {
        val genreNames =
            parsedTags.genreNames.parseId3GenreNames()
                ?: interpretation.separators.split(parsedTags.genreNames)
        return genreNames.map { makePreGenre(it, interpretation) }
    }

    private fun makePreGenre(rawName: String?, interpretation: Interpretation) =
        PreGenre(
            rawName?.let { interpretation.nameFactory.parse(it, null) }
                ?: Name.Unknown(R.string.def_genre),
            rawName)

    private fun unknownPreGenre() = PreGenre(Name.Unknown(R.string.def_genre), null)
}

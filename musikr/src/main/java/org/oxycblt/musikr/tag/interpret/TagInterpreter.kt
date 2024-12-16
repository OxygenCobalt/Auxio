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

import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.fs.Format
import org.oxycblt.musikr.pipeline.RawSong
import org.oxycblt.musikr.tag.Disc
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.Placeholder
import org.oxycblt.musikr.tag.ReleaseType
import org.oxycblt.musikr.tag.ReplayGainAdjustment
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.format.parseId3GenreNames
import org.oxycblt.musikr.util.toUuidOrNull

interface TagInterpreter {
    fun interpret(song: RawSong, interpretation: Interpretation): PreSong

    companion object {
        fun new(): TagInterpreter = TagInterpreterImpl
    }
}

private data object TagInterpreterImpl : TagInterpreter {
    override fun interpret(song: RawSong, interpretation: Interpretation): PreSong {
        val individualPreArtists =
            makePreArtists(
                song.tags.artistMusicBrainzIds,
                song.tags.artistNames,
                song.tags.artistSortNames,
                interpretation)
        val albumPreArtists =
            makePreArtists(
                song.tags.albumArtistMusicBrainzIds,
                song.tags.albumArtistNames,
                song.tags.albumArtistSortNames,
                interpretation)
        val preAlbum =
            makePreAlbum(song.tags, individualPreArtists, albumPreArtists, interpretation)
        val rawArtists =
            individualPreArtists.ifEmpty { albumPreArtists }.ifEmpty { listOf(unknownPreArtist()) }
        val rawGenres =
            makePreGenres(song.tags, interpretation).ifEmpty { listOf(unknownPreGenre()) }
        val uri = song.file.uri
        return PreSong(
            uri = uri,
            path = song.file.path,
            size = song.file.size,
            format = Format.infer(song.file.mimeType, song.properties.mimeType),
            lastModified = song.file.lastModified,
            // TODO: Figure out what to do with date added
            dateAdded = song.file.lastModified,
            musicBrainzId = song.tags.musicBrainzId?.toUuidOrNull(),
            name = interpretation.naming.name(song.tags.name, song.tags.sortName),
            rawName = song.tags.name,
            track = song.tags.track,
            disc = song.tags.disc?.let { Disc(it, song.tags.subtitle) },
            date = song.tags.date,
            durationMs = song.tags.durationMs,
            replayGainAdjustment =
                ReplayGainAdjustment(
                    song.tags.replayGainTrackAdjustment,
                    song.tags.replayGainAlbumAdjustment,
                ),
            preAlbum = preAlbum,
            preArtists = rawArtists,
            preGenres = rawGenres,
            cover = song.cover)
    }

    private fun makePreAlbum(
        parsedTags: ParsedTags,
        individualPreArtists: List<PreArtist>,
        albumPreArtists: List<PreArtist>,
        interpretation: Interpretation
    ): PreAlbum {
        return PreAlbum(
            musicBrainzId = parsedTags.albumMusicBrainzId?.toUuidOrNull(),
            name =
                interpretation.naming.name(
                    parsedTags.albumName, parsedTags.albumSortName, Placeholder.ALBUM),
            rawName = parsedTags.albumName,
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
        val name = interpretation.naming.name(rawName, null, Placeholder.ARTIST)
        val musicBrainzId = musicBrainzId?.toUuidOrNull()
        return PreArtist(musicBrainzId, name, rawName)
    }

    private fun unknownPreArtist() = PreArtist(null, Name.Unknown(Placeholder.GENRE), null)

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
        PreGenre(interpretation.naming.name(rawName, null, Placeholder.GENRE), rawName)

    private fun unknownPreGenre() = PreGenre(Name.Unknown(Placeholder.GENRE), null)
}

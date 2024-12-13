/*
 * Copyright (c) 2024 Auxio Project
 * TagParser.kt is part of Auxio.
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
 
package org.oxycblt.musikr.tag.parse

import javax.inject.Inject
import org.oxycblt.ktaglib.Metadata
import org.oxycblt.musikr.fs.query.DeviceFile

interface TagParser {
    fun parse(file: DeviceFile, metadata: Metadata): ParsedTags
}

class MissingTagError(what: String) : Error("missing tag: $what")

class TagParserImpl @Inject constructor() : TagParser {
    override fun parse(file: DeviceFile, metadata: Metadata): ParsedTags {
        return ParsedTags(
            durationMs = metadata.properties.durationMs,
            replayGainTrackAdjustment = metadata.replayGainTrackAdjustment(),
            replayGainAlbumAdjustment = metadata.replayGainAlbumAdjustment(),
            musicBrainzId = metadata.musicBrainzId(),
            name = metadata.name() ?: file.path.name ?: throw MissingTagError("name"),
            sortName = metadata.sortName(),
            track = metadata.track(),
            disc = metadata.disc(),
            subtitle = metadata.subtitle(),
            date = metadata.date(),
            albumMusicBrainzId = metadata.albumMusicBrainzId(),
            albumName = metadata.albumName(),
            albumSortName = metadata.albumSortName(),
            releaseTypes = metadata.releaseTypes() ?: listOf(),
            artistMusicBrainzIds = metadata.artistMusicBrainzIds() ?: listOf(),
            artistNames = metadata.artistNames() ?: listOf(),
            artistSortNames = metadata.artistSortNames() ?: listOf(),
            albumArtistMusicBrainzIds = metadata.albumArtistMusicBrainzIds() ?: listOf(),
            albumArtistNames = metadata.albumArtistNames() ?: listOf(),
            albumArtistSortNames = metadata.albumArtistSortNames() ?: listOf(),
            genreNames = metadata.genreNames() ?: listOf())
    }
}

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
 
package org.oxycblt.auxio.musikr.tag.parse

import javax.inject.Inject
import org.oxycblt.auxio.musikr.fs.DeviceFile
import org.oxycblt.auxio.musikr.metadata.AudioMetadata

interface TagParser {
    fun parse(file: DeviceFile, metadata: AudioMetadata): ParsedTags
}

class MissingTagError(what: String) : Error("missing tag: $what")

class TagParserImpl @Inject constructor() : TagParser {
    override fun parse(file: DeviceFile, metadata: AudioMetadata): ParsedTags {
        val exoPlayerMetadata =
            metadata.exoPlayerFormat?.metadata
                ?: return ParsedTags(
                    durationMs =
                        metadata.mediaMetadataRetriever.durationMs()
                            ?: throw MissingTagError("durationMs"),
                    name = file.path.name ?: throw MissingTagError("name"),
                )
        val exoPlayerTags = ExoPlayerTags(exoPlayerMetadata)
        return ParsedTags(
            durationMs =
                metadata.mediaMetadataRetriever.durationMs() ?: throw MissingTagError("durationMs"),
            replayGainTrackAdjustment = exoPlayerTags.replayGainTrackAdjustment(),
            replayGainAlbumAdjustment = exoPlayerTags.replayGainAlbumAdjustment(),
            musicBrainzId = exoPlayerTags.musicBrainzId(),
            name = exoPlayerTags.name() ?: file.path.name ?: throw MissingTagError("name"),
            sortName = exoPlayerTags.sortName(),
            track = exoPlayerTags.track(),
            disc = exoPlayerTags.disc(),
            subtitle = exoPlayerTags.subtitle(),
            date = exoPlayerTags.date(),
            albumMusicBrainzId = exoPlayerTags.albumMusicBrainzId(),
            albumName = exoPlayerTags.albumName(),
            albumSortName = exoPlayerTags.albumSortName(),
            releaseTypes = exoPlayerTags.releaseTypes() ?: listOf(),
            artistMusicBrainzIds = exoPlayerTags.artistMusicBrainzIds() ?: listOf(),
            artistNames = exoPlayerTags.artistNames() ?: listOf(),
            artistSortNames = exoPlayerTags.artistSortNames() ?: listOf(),
            albumArtistMusicBrainzIds = exoPlayerTags.albumArtistMusicBrainzIds() ?: listOf(),
            albumArtistNames = exoPlayerTags.albumArtistNames() ?: listOf(),
            albumArtistSortNames = exoPlayerTags.albumArtistSortNames() ?: listOf(),
            genreNames = exoPlayerTags.genreNames() ?: listOf())
    }
}

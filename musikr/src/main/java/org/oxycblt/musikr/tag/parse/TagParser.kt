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

import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.util.unlikelyToBeNull

internal interface TagParser {
    fun parse(file: DeviceFile, metadata: Metadata): ParsedTags

    companion object {
        fun new(): TagParser = TagParserImpl
    }
}

private data object TagParserImpl : TagParser {
    override fun parse(file: DeviceFile, metadata: Metadata): ParsedTags {
        val compilation = metadata.isCompilation()
        return ParsedTags(
            durationMs = metadata.properties.durationMs,
            replayGainTrackAdjustment = metadata.replayGainTrackAdjustment(),
            replayGainAlbumAdjustment = metadata.replayGainAlbumAdjustment(),
            musicBrainzId = metadata.musicBrainzId(),
            name = metadata.name() ?: unlikelyToBeNull(file.path.name),
            sortName = metadata.sortName(),
            track = metadata.track(),
            disc = metadata.disc(),
            subtitle = metadata.subtitle(),
            date = metadata.date(),
            albumMusicBrainzId = metadata.albumMusicBrainzId(),
            albumName = metadata.albumName(),
            albumSortName = metadata.albumSortName(),
            // Compilation flag implies a compilation release type in the case that
            // we don't have any other release types
            releaseTypes =
                metadata.releaseTypes() ?: listOf("compilation").takeIf { compilation } ?: listOf(),
            artistMusicBrainzIds = metadata.artistMusicBrainzIds() ?: listOf(),
            artistNames = metadata.artistNames() ?: listOf(),
            artistSortNames = metadata.artistSortNames() ?: listOf(),
            albumArtistMusicBrainzIds = metadata.albumArtistMusicBrainzIds() ?: listOf(),
            // Compilation pretty heavily implies various artists in the case that we don't
            // have any other album artists
            albumArtistNames =
                metadata.albumArtistNames()
                    ?: listOf("Various Artists").takeIf { compilation }
                    ?: listOf(),
            albumArtistSortNames = metadata.albumArtistSortNames() ?: listOf(),
            genreNames = metadata.genreNames() ?: listOf())
    }
}

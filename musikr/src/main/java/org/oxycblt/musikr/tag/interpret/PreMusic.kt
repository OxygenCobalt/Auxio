/*
 * Copyright (c) 2024 Auxio Project
 * PreMusic.kt is part of Auxio.
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

import android.net.Uri
import java.util.UUID
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.fs.Format
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.Disc
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.ReleaseType
import org.oxycblt.musikr.tag.ReplayGainAdjustment
import org.oxycblt.musikr.util.update

internal data class PreSong(
    val musicBrainzId: UUID?,
    val name: Name.Known,
    val rawName: String,
    val track: Int?,
    val disc: Disc?,
    val date: Date?,
    val uri: Uri,
    val path: Path,
    val format: Format,
    val size: Long,
    val durationMs: Long,
    val bitrateKbps: Int,
    val sampleRateHz: Int,
    val replayGainAdjustment: ReplayGainAdjustment,
    val lastModified: Long,
    val dateAdded: Long,
    val cover: Cover?,
    val preAlbum: PreAlbum,
    val preArtists: List<PreArtist>,
    val preGenres: List<PreGenre>
) {
    val uid =
        musicBrainzId?.let { Music.UID.musicBrainz(Music.UID.Item.SONG, it) }
            ?: Music.UID.auxio(Music.UID.Item.SONG) {
                // Song UIDs are based on the raw data without parsing so that they remain
                // consistent across music setting changes. Parents are not held up to the
                // same standard since grouping is already inherently linked to settings.
                update(rawName)
                update(preAlbum.rawName)
                update(date)

                update(track)
                update(disc?.number)

                update(preArtists.map { artist -> artist.rawName })
                update(preAlbum.preArtists.map { artist -> artist.rawName })
            }
}

internal data class PreAlbum(
    val musicBrainzId: UUID?,
    val name: Name,
    val rawName: String?,
    val releaseType: ReleaseType,
    val preArtists: List<PreArtist>
)

internal data class PreArtist(val musicBrainzId: UUID?, val name: Name, val rawName: String?)

internal data class PreGenre(
    val name: Name,
    val rawName: String?,
)

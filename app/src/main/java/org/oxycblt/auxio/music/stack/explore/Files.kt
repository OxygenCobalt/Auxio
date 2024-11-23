/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.stack.explore

import android.net.Uri
import org.oxycblt.auxio.music.stack.interpret.model.SongImpl
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.stack.explore.fs.Path

data class DeviceFile(
    val uri: Uri,
    val mimeType: String,
    val path: Path,
    val size: Long,
    val lastModified: Long
)

/**
 * Raw information about a [SongImpl] obtained from the filesystem/Extractor instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class AudioFile(
    val deviceFile: DeviceFile,
    var durationMs: Long? = null,
    var replayGainTrackAdjustment: Float? = null,
    var replayGainAlbumAdjustment: Float? = null,
    var musicBrainzId: String? = null,
    var name: String? = null,
    var sortName: String? = null,
    var track: Int? = null,
    var disc: Int? = null,
    var subtitle: String? = null,
    var date: Date? = null,
    var albumMusicBrainzId: String? = null,
    var albumName: String? = null,
    var albumSortName: String? = null,
    var releaseTypes: List<String> = listOf(),
    var artistMusicBrainzIds: List<String> = listOf(),
    var artistNames: List<String> = listOf(),
    var artistSortNames: List<String> = listOf(),
    var albumArtistMusicBrainzIds: List<String> = listOf(),
    var albumArtistNames: List<String> = listOf(),
    var albumArtistSortNames: List<String> = listOf(),
    var genreNames: List<String> = listOf()
)

interface PlaylistFile {
    val name: String
}
/*
 * Copyright (c) 2023 Auxio Project
 * Files.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.explore

import android.net.Uri
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.musikr.explore.fs.Path

data class DeviceFile(
    val uri: Uri,
    val mimeType: String,
    val path: Path,
    val size: Long,
    val lastModified: Long
)

data class AudioFile(
    val deviceFile: DeviceFile,
    val durationMs: Long,
    val replayGainTrackAdjustment: Float? = null,
    val replayGainAlbumAdjustment: Float? = null,
    val musicBrainzId: String? = null,
    val name: String,
    val sortName: String? = null,
    val track: Int? = null,
    val disc: Int? = null,
    val subtitle: String? = null,
    val date: Date? = null,
    val albumMusicBrainzId: String? = null,
    val albumName: String? = null,
    val albumSortName: String? = null,
    val releaseTypes: List<String> = listOf(),
    val artistMusicBrainzIds: List<String> = listOf(),
    val artistNames: List<String> = listOf(),
    val artistSortNames: List<String> = listOf(),
    val albumArtistMusicBrainzIds: List<String> = listOf(),
    val albumArtistNames: List<String> = listOf(),
    val albumArtistSortNames: List<String> = listOf(),
    val genreNames: List<String> = listOf()
)

data class PlaylistFile(
    val name: String,
    val songPointers: List<SongPointer>,
    val editor: PlaylistHandle
)

interface PlaylistHandle {
    val uid: Music.UID

    suspend fun rename(name: String)

    suspend fun add(songs: List<Song>)

    suspend fun rewrite(songs: List<Song>)

    suspend fun delete()
}

sealed interface SongPointer {
    data class UID(val uid: Music.UID) : SongPointer
    //    data class Path(val options: List<Path>) : SongPointer
}

package org.oxycblt.auxio.musikr.tag

import org.oxycblt.auxio.musikr.model.Date
import org.oxycblt.auxio.musikr.fs.DeviceFile

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
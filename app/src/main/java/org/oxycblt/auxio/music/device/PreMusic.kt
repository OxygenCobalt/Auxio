package org.oxycblt.auxio.music.device

import android.net.Uri
import org.oxycblt.auxio.image.extractor.Cover
import org.oxycblt.auxio.music.fs.MimeType
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.playback.replaygain.ReplayGainAdjustment
import java.util.UUID

data class PreSong(
    val musicBrainzId: UUID?,
    val name: Name,
    val rawName: String?,
    val track: Int?,
    val disc: Disc?,
    val date: Date?,
    val uri: Uri,
    val cover: Cover,
    val path: Path,
    val mimeType: MimeType,
    val size: Long,
    val durationMs: Long,
    val replayGainAdjustment: ReplayGainAdjustment,
    val dateAdded: Long,
    val preAlbum: PreAlbum,
    val preArtists: List<PreArtist>,
    val preGenres: List<PreGenre>
)

data class PreAlbum(
    val musicBrainzId: UUID?,
    val name: Name,
    val rawName: String,
    val releaseType: ReleaseType,
    val preArtists: List<PreArtist>
)

data class PreArtist(
    val musicBrainzId: UUID?,
    val name: Name,
    val rawName: String?,
)

data class PreGenre(
    val name: Name,
    val rawName: String?,
)

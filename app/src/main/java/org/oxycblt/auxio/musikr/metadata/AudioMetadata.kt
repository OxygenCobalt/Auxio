package org.oxycblt.auxio.musikr.metadata

import android.media.MediaMetadataRetriever
import androidx.media3.common.Format
import androidx.media3.common.Metadata
import org.oxycblt.auxio.musikr.fs.DeviceFile

data class AudioMetadata(
    val file: DeviceFile,
    val exoPlayerFormat: Format,
    val mediaMetadataRetriever: MediaMetadataRetriever
)

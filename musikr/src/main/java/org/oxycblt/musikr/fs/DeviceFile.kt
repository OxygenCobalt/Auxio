package org.oxycblt.musikr.fs

import android.net.Uri

data class DeviceFile(
    val uri: Uri,
    val mimeType: String,
    val path: Path,
    val size: Long,
    val lastModified: Long
)
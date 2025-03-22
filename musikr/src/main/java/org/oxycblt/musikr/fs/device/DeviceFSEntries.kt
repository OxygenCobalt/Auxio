package org.oxycblt.musikr.fs.device

import android.net.Uri
import kotlinx.coroutines.Deferred
import org.oxycblt.musikr.fs.Path


sealed interface DeviceFSEntry {
    val uri: Uri
    val path: Path
}

data class DeviceDirectory(
    override val uri: Uri,
    override val path: Path,
    val parent: Deferred<DeviceDirectory>?,
    var children: List<DeviceFSEntry>
) : DeviceFSEntry

data class DeviceFile(
    override val uri: Uri,
    override val path: Path,
    val modifiedMs: Long,
    val mimeType: String,
    val size: Long,
    val parent: Deferred<DeviceDirectory>
) : DeviceFSEntry
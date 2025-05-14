/*
 * Copyright (c) 2025 Auxio Project
 * DeviceFSEntries.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.device

import android.net.Uri
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import org.oxycblt.musikr.fs.Path

sealed interface DeviceFSEntry {
    val uri: Uri
    val path: Path
}

interface DeviceDirectory : DeviceFSEntry {
    val parent: DeviceDirectory?
    val children: FiniteHotFlow<DeviceFSEntry>
}

data class DeviceFile(
    override val uri: Uri,
    override val path: Path,
    val modifiedMs: Long,
    val mimeType: String,
    val size: Long,
    val parent: DeviceDirectory
) : DeviceFSEntry

@OptIn(ExperimentalCoroutinesApi::class)
fun DeviceDirectory.flatten(): Flow<DeviceFile> = children.flow().flatMapMerge {
    when (it) {
        is DeviceDirectory -> it.flatten()
        is DeviceFile -> flowOf(it)
    }
}
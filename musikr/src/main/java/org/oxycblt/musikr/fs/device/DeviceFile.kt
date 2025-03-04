/*
 * Copyright (c) 2024 Auxio Project
 * DeviceFile.kt is part of Auxio.
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
import org.oxycblt.musikr.fs.Path

sealed interface DeviceNode {
    val uri: Uri
    val path: Path
}

data class DeviceDirectory(
    override val uri: Uri,
    override val path: Path,
    val parent: Deferred<DeviceDirectory>?,
    val children: List<DeviceNode>
) : DeviceNode

data class DeviceFile(
    override val uri: Uri,
    override val path: Path,
    val modifiedMs: Long,
    val mimeType: String,
    val size: Long,
    val parent: Deferred<DeviceDirectory>
) : DeviceNode

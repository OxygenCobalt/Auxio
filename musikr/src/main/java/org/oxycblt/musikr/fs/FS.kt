/*
 * Copyright (c) 2025 Auxio Project
 * FS.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs

import android.net.Uri
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface FS {
    fun explore(): Flow<File>

    fun track(): Flow<FSUpdate>
}

sealed interface FSEntry {
    val uri: Uri?
    val path: Path
}

data class Directory(
    override val uri: Uri?,
    override val path: Path,
    val parent: Deferred<Directory>?,
    var children: List<File>
) : FSEntry

data class File(
    override val uri: Uri,
    override val path: Path,
    val addedMs: AddedMs,
    val modifiedMs: Long,
    val mimeType: String,
    val size: Long,
    val parent: Deferred<Directory>?
) : FSEntry

sealed interface FSUpdate {
    data class LocationChanged(val location: Location.Opened?) : FSUpdate
}

interface AddedMs {
    suspend fun resolve(): Long?
}

/*
 * Copyright (c) 2024 Auxio Project
 * FileTreeCache.kt is part of Auxio.
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

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

interface FileTreeCache {
    suspend fun queryRootDirectory(uri: Uri): CachedRoot
    suspend fun updateRootDirectory(uri: Uri, cachedRoot: CachedRoot)

    suspend fun queryDirectory(uri: Uri): CachedDirectory
    suspend fun updateDirectory(uri: Uri, directory: CachedDirectory)

    suspend fun queryFile(uri: Uri): CachedFile?
    suspend fun updateFile(uri: Uri, file: CachedFile)
}

data class CachedRoot(
    val uri: String,
    val path: String,
    val modifiedMs: Long,
    val childUris: List<String>
)

data class CachedDirectory(
    val uri: String,
    val name: String,
    val modifiedMs: Long,
    val childUris: List<String>
)

data class CachedFile(
    val uri: String,
    val name: String,
    val modifiedMs: Long,
    val mimeType: String,
    val size: Long
)

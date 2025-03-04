/*
 * Copyright (c) 2025 Auxio Project
 * FSCovers.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers.fs

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.covers.FDCover
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.fs.device.DeviceDirectory
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

open class FSCovers(private val context: Context) : Covers<FDCover> {
    override suspend fun obtain(id: String): CoverResult<FDCover> {
        // Parse the ID to get the directory URI
        if (!id.startsWith("folder:")) {
            return CoverResult.Miss()
        }

        val directoryUri = id.substring("folder:".length)
        val uri = Uri.parse(directoryUri)

        // Check if the URI is still valid
        val exists =
            withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openFileDescriptor(uri, "r")?.close()
                    true
                } catch (e: Exception) {
                    false
                }
            }

        return if (exists) {
            CoverResult.Hit(FolderCoverImpl(context, uri))
        } else {
            CoverResult.Miss()
        }
    }
}

class MutableFSCovers(private val context: Context) : FSCovers(context), MutableCovers<FDCover> {
    override suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<FDCover> {
        val parent = file.parent
        val coverFile = findCoverInDirectory(parent) ?: return CoverResult.Miss()
        return CoverResult.Hit(FolderCoverImpl(context, coverFile.uri))
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        // No cleanup needed for folder covers as they are external files
        // that should not be managed by the app
    }

    private suspend fun findCoverInDirectory(directory: DeviceDirectory): DeviceFile? {
        return directory.children
            .mapNotNull { node -> if (node is DeviceFile && isCoverArtFile(node)) node else null }
            .firstOrNull()
    }

    private fun isCoverArtFile(file: DeviceFile): Boolean {
        val filename = requireNotNull(file.path.name).lowercase()
        val mimeType = file.mimeType.lowercase()

        if (!mimeType.startsWith("image/")) {
            return false
        }

        val coverNames =
            listOf(
                "cover",
                "folder",
                "album",
                "albumart",
                "front",
                "artwork",
                "art",
                "folder")

        val filenameWithoutExt = filename.substringBeforeLast(".")
        val extension = filename.substringAfterLast(".", "")

        return coverNames.any { coverName ->
            filenameWithoutExt.equals(coverName, ignoreCase = true) &&
                (extension.equals("jpg", ignoreCase = true) ||
                    extension.equals("jpeg", ignoreCase = true) ||
                    extension.equals("png", ignoreCase = true))
        }
    }
}

private data class FolderCoverImpl(
    private val context: Context,
    private val uri: Uri,
) : FDCover {
    override val id = "folder:$uri"

    override suspend fun fd(): ParcelFileDescriptor? =
        withContext(Dispatchers.IO) { context.contentResolver.openFileDescriptor(uri, "r") }

    override suspend fun open(): InputStream? =
        withContext(Dispatchers.IO) { context.contentResolver.openInputStream(uri) }
}

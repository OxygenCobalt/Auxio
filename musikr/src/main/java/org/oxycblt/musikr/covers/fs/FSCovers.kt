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

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.covers.FDCover
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

private const val PREFIX = "mcf:"

open class FSCovers(private val context: Context) : Covers<FDCover> {
    override suspend fun obtain(id: String): CoverResult<FDCover> {
        if (!id.startsWith(PREFIX)) {
            return CoverResult.Miss()
        }

        val uri = id.substring(PREFIX.length).toUri()

        // Check if the cover file still actually exists. Perhaps the file was deleted at some
        // point or superceded by a new one.
        val exists =
            withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openFileDescriptor(uri, "r")?.also { it.close() } !=
                        null
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
        // Since DeviceFiles is a streaming API, we have to wait for the current recursive
        // query to finally finish to be able to have a complete list of siblings to search for.
        val parent = file.parent.await()
        val coverFile =
            parent.children.firstNotNullOfOrNull { node ->
                if (node is DeviceFile && isCoverArtFile(node)) node else null
            } ?: return CoverResult.Miss()
        return CoverResult.Hit(FolderCoverImpl(context, coverFile.uri))
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        // No cleanup needed for folder covers as they are external files
        // that should not be managed by the app
    }

    private fun isCoverArtFile(file: DeviceFile): Boolean {
        if (!file.mimeType.startsWith("image/", ignoreCase = true)) {
            return false
        }

        val filename = requireNotNull(file.path.name).lowercase()
        val filenameWithoutExt = filename.substringBeforeLast(".")
        val extension = filename.substringAfterLast(".", "")

        return coverNames.any { coverName ->
            filenameWithoutExt.equals(coverName, ignoreCase = true) &&
                (extension.equals("jpg", ignoreCase = true) ||
                    extension.equals("jpeg", ignoreCase = true) ||
                    extension.equals("png", ignoreCase = true))
        }
    }

    private companion object {
        private val coverNames =
            listOf(
                "cover",
                "folder",
                "album",
                "albumart",
                "front",
                "artwork",
                "art",
                "folder",
                "coverart")
    }
}

private data class FolderCoverImpl(
    private val context: Context,
    private val uri: Uri,
) : FDCover {
    override val id = PREFIX + uri.toString()

    // Implies that client will manage freeing the resources themselves.

    @SuppressLint("Recycle")
    override suspend fun open(): InputStream? =
        withContext(Dispatchers.IO) { context.contentResolver.openInputStream(uri) }

    @SuppressLint("Recycle")
    override suspend fun fd(): ParcelFileDescriptor? =
        withContext(Dispatchers.IO) { context.contentResolver.openFileDescriptor(uri, "r") }
}

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
import kotlin.math.max
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

/**
 * A [Covers] implementation that obtains cover art from the filesystem, such as cover.jpg.
 *
 * Cover.jpg is pretty widely used in music libraries to save space, so it's good to use this.
 *
 * This implementation does not search the directory tree given that it cannot access it. Rather, it
 * assumes the provided id ius one yielded by [MutableFSCovers].
 *
 * See [MutableFSCovers] for the mutable variant.
 *
 * @param context The [Context] to use to access the filesystem and check for ID validity.
 */
class FSCovers(private val context: Context) : Covers<FDCover> {
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

/**
 * A [MutableCovers] implementation that obtains cover art from the filesystem, such as cover.jpg.
 *
 * Cover.jpg is pretty widely used in music libraries to save space, so it's good to use this.
 *
 * This implementation will search the parent directory for the best cover art. "Best" being defined
 * as having cover-art-ish names and having a good format like png/jpg/webp.
 *
 * See [FSCovers] for the immutable variant.
 *
 * @param context The [Context] to use to access the filesystem and check for ID validity.
 */
class MutableFSCovers(private val context: Context) : MutableCovers<FDCover> {
    private val inner = FSCovers(context)

    override suspend fun obtain(id: String): CoverResult<FDCover> = inner.obtain(id)

    override suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<FDCover> {
        // Since DeviceFiles is a streaming API, we have to wait for the current recursive
        // query to finally finish to be able to have a complete list of siblings to search for.
        val parent = file.parent.await()
        val bestCover =
            parent.children
                .filterIsInstance<DeviceFile>()
                .map { it to coverArtScore(it) }
                .maxBy { it.second }
        if (bestCover.second > 0) {
            return CoverResult.Hit(FolderCoverImpl(context, bestCover.first.uri))
        }
        // No useful cover art was found.
        // Well, technically we might have found a cover image, but it might be some unrelated
        // jpeg from poor file organization.
        return CoverResult.Miss()
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        // No cleanup needed for folder covers as they are external files
        // that should not be managed by the app
    }

    private suspend fun coverArtScore(file: DeviceFile): Int {
        if (!file.mimeType.startsWith("image/", ignoreCase = true)) {
            // Not an image file. You lose!
            return 0
        }

        val filename = requireNotNull(file.path.name)
        val name = filename.substringBeforeLast('.')
        val extension = filename.substringAfterLast('.', "")
        // See if the name contains any of the preferred cover names. This helps weed out
        // images that are not actually cover art and are just there.,
        var score =
            (preferredCoverNames + requireNotNull(file.parent.await().path.name))
                .withIndex()
                .filter { name.contains(it.value, ignoreCase = true) }
                .sumOf { it.index + 1 }
        // Multiply the score for preferred formats & extensions. Weirder formats are harder for
        // android to decode, but not the end of the world.
        score *=
            max(preferredFormats.indexOfFirst { file.mimeType.equals(it, ignoreCase = true) }, 1)
        score *=
            max(preferredExtensions.indexOfFirst { extension.equals(it, ignoreCase = true) }, 1)
        return score
    }

    private companion object {
        private val preferredCoverNames = listOf("front", "art", "album", "folder", "cover")

        private val preferredFormats =
            listOf(
                "image/webp",
                "image/jpg",
                "image/jpeg",
                "image/png",
            )

        private val preferredExtensions = listOf("webp", "jpg", "jpeg", "png")
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

/*
 * Copyright (c) 2024 Auxio Project
 * DeviceFiles.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.query

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.Path

interface DeviceFiles {
    fun explore(locations: Flow<MusicLocation>): Flow<DeviceFile>

    companion object {
        fun from(context: Context): DeviceFiles = DeviceFilesImpl(context.contentResolverSafe)
    }
}

data class DeviceFile(
    val uri: Uri,
    val mimeType: String,
    val path: Path,
    val size: Long,
    val lastModified: Long
)

@OptIn(ExperimentalCoroutinesApi::class)
private class DeviceFilesImpl(private val contentResolver: ContentResolver) : DeviceFiles {
    override fun explore(locations: Flow<MusicLocation>): Flow<DeviceFile> =
        locations.flatMapMerge { location ->
            exploreImpl(
                contentResolver,
                location.uri,
                DocumentsContract.getTreeDocumentId(location.uri),
                location.path)
        }

    private fun exploreImpl(
        contentResolver: ContentResolver,
        rootUri: Uri,
        treeDocumentId: String,
        relativePath: Path
    ): Flow<DeviceFile> = flow {
        contentResolver.useQuery(
            DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, treeDocumentId),
            PROJECTION) { cursor ->
                val childUriIndex =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val displayNameIndex =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeTypeIndex =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
                val lastModifiedIndex =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                val recursive = mutableListOf<Flow<DeviceFile>>()
                while (cursor.moveToNext()) {
                    val childId = cursor.getString(childUriIndex)
                    val displayName = cursor.getString(displayNameIndex)
                    val newPath = relativePath.file(displayName)
                    val mimeType = cursor.getString(mimeTypeIndex)
                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        // This does NOT block the current coroutine. Instead, we will
                        // evaluate this flow in parallel later to maximize throughput.
                        recursive.add(exploreImpl(contentResolver, rootUri, childId, newPath))
                    } else if (mimeType.startsWith("audio/") && mimeType != "audio/x-mpegurl") {
                        // Immediately emit all files given that it's just an O(1) op.
                        // This also just makes sure the outer flow has a reason to exist
                        // rather than just being a glorified async.
                        val lastModified = cursor.getLong(lastModifiedIndex)
                        val size = cursor.getLong(sizeIndex)
                        emit(
                            DeviceFile(
                                DocumentsContract.buildDocumentUriUsingTree(rootUri, childId),
                                mimeType,
                                newPath,
                                size,
                                lastModified))
                    }
                }
                emitAll(recursive.asFlow().flattenMerge())
            }
    }

    private companion object {
        val PROJECTION =
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED)
    }
}

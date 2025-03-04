/*
 * Copyright (c) 2024 Auxio Project
 * DeviceFS.kt is part of Auxio.
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

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.Path

internal interface DeviceFS {
    fun explore(locations: Flow<MusicLocation>): Flow<DeviceNode>

    companion object {
        fun from(context: Context, withHidden: Boolean): DeviceFS =
            DeviceFSImpl(context.contentResolverSafe, withHidden)
    }
}

sealed interface DeviceNode {
    val uri: Uri
    val path: Path
}

data class DeviceDirectory(
    override val uri: Uri,
    override val path: Path,
    val parent: DeviceDirectory?,
    var children: Flow<DeviceNode>
) : DeviceNode

data class DeviceFile(
    override val uri: Uri,
    override val path: Path,
    val modifiedMs: Long,
    val mimeType: String,
    val size: Long,
    val parent: DeviceDirectory
) : DeviceNode

@OptIn(ExperimentalCoroutinesApi::class)
private class DeviceFSImpl(
    private val contentResolver: ContentResolver,
    private val withHidden: Boolean
) : DeviceFS {
    override fun explore(locations: Flow<MusicLocation>): Flow<DeviceNode> =
        locations.flatMapMerge { location ->
            // Create a root directory for each location
            val rootDirectory =
                DeviceDirectory(
                    uri = location.uri, path = location.path, parent = null, children = emptyFlow())

            // Set up the children flow for the root directory
            rootDirectory.children =
                exploreDirectoryImpl(
                    contentResolver,
                    location.uri,
                    DocumentsContract.getTreeDocumentId(location.uri),
                    location.path,
                    rootDirectory,
                    withHidden)

            // Return a flow that emits the root directory
            flow { emit(rootDirectory) }
        }

    private fun exploreDirectoryImpl(
        contentResolver: ContentResolver,
        rootUri: Uri,
        treeDocumentId: String,
        relativePath: Path,
        parent: DeviceDirectory,
        withHidden: Boolean
    ): Flow<DeviceNode> = flow {
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

                while (cursor.moveToNext()) {
                    val childId = cursor.getString(childUriIndex)
                    val displayName = cursor.getString(displayNameIndex)

                    // Skip hidden files/directories if ignoreHidden is true
                    if (!withHidden && displayName.startsWith(".")) {
                        continue
                    }

                    val newPath = relativePath.file(displayName)
                    val mimeType = cursor.getString(mimeTypeIndex)
                    val lastModified = cursor.getLong(lastModifiedIndex)
                    val childUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, childId)

                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        // Create a directory node with empty children flow initially
                        val directory =
                            DeviceDirectory(
                                uri = childUri,
                                path = newPath,
                                parent = parent,
                                children = emptyFlow())

                        // Set up the children flow for this directory
                        directory.children =
                            exploreDirectoryImpl(
                                contentResolver, rootUri, childId, newPath, directory, withHidden)

                        // Emit the directory node
                        emit(directory)
                    } else {
                        val size = cursor.getLong(sizeIndex)
                        emit(
                            DeviceFile(
                                uri = childUri,
                                mimeType = mimeType,
                                path = newPath,
                                size = size,
                                modifiedMs = lastModified,
                                parent = parent))
                    }
                }
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

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
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.Path

internal interface DeviceFS {
    fun explore(locations: Flow<MusicLocation>): Flow<DeviceDirectory>

    companion object {
        fun from(context: Context, withHidden: Boolean): DeviceFS =
            DeviceFSImpl(context.contentResolverSafe, withHidden)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private class DeviceFSImpl(
    private val contentResolver: ContentResolver,
    private val withHidden: Boolean
) : DeviceFS {
    override fun explore(locations: Flow<MusicLocation>): Flow<DeviceDirectory> =
        locations.mapNotNull { location ->
            val treeDocumentId =
                DocumentsContract.getTreeDocumentId(location.uri)
            val uri = DocumentsContract.buildDocumentUriUsingTree(location.uri, treeDocumentId)
            val modifiedMs = contentResolver.useQuery(
                uri,
                arrayOf(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
            ) { cursor ->
                if (!cursor.moveToFirst()) return@useQuery null
                val lastModifiedIndex =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                cursor.getLong(lastModifiedIndex)
            }
            if (modifiedMs == null) {
                return@mapNotNull null
            }
            query(
                location.uri,
                treeDocumentId,
                location.path,
                0,
                null
            )
        }

    private suspend fun query(
        rootUri: Uri,
        treeDocumentId: String,
        path: Path,
        modifiedMs: Long,
        parent: DeviceDirectory?,
    ): DeviceDirectory = coroutineScope {
        val dir = DeviceDirectoryImpl(
            uri = DocumentsContract.buildDocumentUriUsingTree(rootUri, treeDocumentId),
            path = path,
            modifiedMs = modifiedMs,
            parent = parent,
            children = emptyFlow()
        )
        dir.children = flow {
            Log.d("DeviceFS", "Finished querying $path")
            contentResolver.useQuery(
                DocumentsContract.buildChildDocumentsUriUsingTree(
                    rootUri,
                    treeDocumentId
                ), PROJECTION
            ) { cursor ->
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

                    val newPath = path.file(displayName)
                    val mimeType = cursor.getString(mimeTypeIndex)
                    val lastModified = cursor.getLong(lastModifiedIndex)

                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        val subdir =
                            query(
                                rootUri = rootUri,
                                treeDocumentId = childId,
                                path = newPath,
                                modifiedMs = modifiedMs,
                                parent = dir
                            )
                        emit(StreamedFile.More(subdir))
                    } else {
                        val size = cursor.getLong(sizeIndex)
                        val childUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, childId)
                        val file =
                            DeviceFile(
                                uri = childUri,
                                mimeType = mimeType,
                                path = newPath,
                                size = size,
                                modifiedMs = lastModified,
                                parent = dir
                            )
                        emit(StreamedFile.More(file))
                    }
                }
                emit(StreamedFile.Done)
            }
        }.shareIn(this, SharingStarted.Eagerly, replay = Int.MAX_VALUE)
            .takeWhile { it is StreamedFile.More }
            .map { (it as StreamedFile.More).value }
        dir
    }

    private sealed interface StreamedFile {
        data class More(val value: DeviceFSEntry) : StreamedFile
        data object Done : StreamedFile
    }

    private companion object {
        private val PROJECTION =
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )
    }
}

private class DeviceDirectoryImpl(
    override val uri: Uri,
    override val path: Path,
    override val modifiedMs: Long,
    override val parent: DeviceDirectory?,
    override var children: Flow<DeviceFSEntry>,
) : DeviceDirectory




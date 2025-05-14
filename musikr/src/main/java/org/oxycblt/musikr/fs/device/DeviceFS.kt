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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.device.FiniteHotFlow.HotObject

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
        locations.map { location ->
            coroutineScope {
                DeviceDirectoryImpl(
                    location.uri,
                    DocumentsContract.getTreeDocumentId(location.uri),
                    location.path,
                    null,
                    coroutineScope = CoroutineScope(Dispatchers.IO),
                    contentResolver = contentResolver,
                    withHidden = withHidden
                )
            }
        }
}

private class DeviceDirectoryImpl(
    rootUri: Uri,
    treeDocumentId: String,
    override val path: Path,
    override val parent: DeviceDirectoryImpl?,
    coroutineScope: CoroutineScope,
    contentResolver: ContentResolver,
    withHidden: Boolean
) : DeviceDirectory {
    private sealed interface HotObject {
        data class More(val value: DeviceFSEntry) : HotObject
        data object Done : HotObject
    }

    override val uri: Uri = DocumentsContract.buildDocumentUriUsingTree(rootUri, treeDocumentId)
    override val children = flow {
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
                    val dir =
                        DeviceDirectoryImpl(
                            rootUri,
                            childId,
                            path = newPath,
                            parent = this@DeviceDirectoryImpl,
                            coroutineScope,
                            contentResolver,
                            withHidden
                        )
                    emit(HotObject.More(dir))
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
                            parent = this@DeviceDirectoryImpl
                        )
                    emit(HotObject.More(file))
                }
            }
            emit(HotObject.Done)
        }
    }.shareIn(coroutineScope, SharingStarted.Lazily, replay = Int.MAX_VALUE)
        .takeWhile { it is HotObject.More }
        .map { (it as HotObject.More).value }

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



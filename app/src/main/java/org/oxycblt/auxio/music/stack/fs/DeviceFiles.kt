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
 
package org.oxycblt.auxio.music.stack.fs

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import org.oxycblt.auxio.music.fs.Components
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.music.fs.useQuery

interface DeviceFiles {
    fun explore(uris: Flow<Uri>): Flow<DeviceFile>
}

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceFilesImpl @Inject constructor(@ApplicationContext private val context: Context) :
    DeviceFiles {
    private val contentResolver = context.contentResolverSafe

    override fun explore(uris: Flow<Uri>): Flow<DeviceFile> =
        uris.flatMapMerge { rootUri -> exploreImpl(contentResolver, rootUri, Components.nil()) }

    private fun exploreImpl(
        contentResolver: ContentResolver,
        uri: Uri,
        relativePath: Components
    ): Flow<DeviceFile> = flow {
        contentResolver.useQuery(uri, PROJECTION) { cursor ->
            val childUriIndex =
                cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val displayNameIndex =
                cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeTypeIndex =
                cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
            val sizeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
            val lastModifiedIndex =
                cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
            // Recurse into sub-directories as another flow
            val recursions = mutableListOf<Flow<DeviceFile>>()
            while (cursor.moveToNext()) {
                val childId = cursor.getString(childUriIndex)
                val childUri = DocumentsContract.buildDocumentUriUsingTree(uri, childId)
                val displayName = cursor.getString(displayNameIndex)
                val path = relativePath.child(displayName)
                val mimeType = cursor.getString(mimeTypeIndex)
                if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                    // This does NOT block the current coroutine. Instead, we will
                    // evaluate this flow in parallel later to maximize throughput.
                    recursions.add(exploreImpl(contentResolver, childUri, path))
                } else {
                    // Immediately emit all files given that it's just an O(1) op.
                    // This also just makes sure the outer flow has a reason to exist
                    // rather than just being a glorified async.
                    val lastModified = cursor.getLong(lastModifiedIndex)
                    val size = cursor.getLong(sizeIndex)
                    emit(DeviceFile(childUri, mimeType, path, size, lastModified))
                }
            }
            // Hypothetically, we could just emitAll as we recurse into a new directory,
            // but this will block the flow and force the tree search to be sequential.
            // Instead, try to leverage flow parallelism  and do all recursive calls in parallel.
            // Kotlin coroutines can handle doing possibly thousands of parallel calls, it'll
            // be fine. I hope.
            emitAll(recursions.asFlow().flattenMerge())
        }
    }

    private companion object {
        val PROJECTION =
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )
    }
}

data class DeviceFile(val uri: Uri, val mimeType: String, val path: Components, val size: Long, val lastModified: Long)

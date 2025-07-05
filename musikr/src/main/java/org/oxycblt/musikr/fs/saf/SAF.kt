/*
 * Copyright (c) 2025 Auxio Project
 * SAF.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.saf

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import org.oxycblt.musikr.fs.AddedMs
import org.oxycblt.musikr.fs.Directory
import org.oxycblt.musikr.fs.FS
import org.oxycblt.musikr.fs.FSUpdate
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.mediastore.MediaStore
import org.oxycblt.musikr.fs.track.LocationObserver
import android.provider.MediaStore as AOSPMediaStore

@OptIn(ExperimentalCoroutinesApi::class)
class SAF
private constructor(
    private val context: Context,
    private val contentResolver: ContentResolver,
    private val query: Query
) : FS {
    override fun explore(): Flow<File> =
        query.source.asFlow().flatMapMerge { location ->
            exploreDirectoryImpl(
                location.uri,
                DocumentsContract.getTreeDocumentId(location.uri),
                location.path,
                null,
                query.exclude.mapTo(mutableSetOf()) { it.path })
        }

    override fun track(): Flow<FSUpdate> = callbackFlow {
        val observers = mutableListOf<LocationObserver>()

        query.source.forEach { location ->
            val observer =
                LocationObserver(context, location.uri) {
                    trySend(FSUpdate.LocationChanged(location))
                }
            observers.add(observer)
        }

        awaitClose { observers.forEach { observer -> observer.release() } }
    }

    private fun exploreDirectoryImpl(
        rootUri: Uri,
        treeDocumentId: String,
        relativePath: Path,
        parent: Deferred<Directory>?,
        exclude: Set<Path>
    ): Flow<File> = flow {
        // Make a kotlin future
        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, treeDocumentId)
        val directoryDeferred = CompletableDeferred<Directory>()
        val recursive = mutableListOf<Flow<File>>()
        val children = mutableListOf<File>()
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

            while (cursor.moveToNext()) {
                val childId = cursor.getString(childUriIndex)
                val displayName = cursor.getString(displayNameIndex)

                // Skip hidden files/directories if ignoreHidden is true
                if (!query.withHidden && displayName.startsWith(".")) {
                    continue
                }

                val newPath = relativePath.file(displayName)
                val mimeType = cursor.getString(mimeTypeIndex)
                val lastModified = cursor.getLong(lastModifiedIndex)
                val childUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, childId)

                // We can check for direct equality as if we block out an excluded directory we
                // will by proxy block out it's children.
                if (newPath in exclude) {
                    continue
                }

                if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                    recursive.add(
                        exploreDirectoryImpl(rootUri, childId, newPath, directoryDeferred, exclude))
                } else {
                    val size = cursor.getLong(sizeIndex)
                    val file =
                        File(
                            uri = childUri,
                            mimeType = mimeType,
                            path = newPath,
                            size = size,
                            modifiedMs = lastModified,
                            parent = directoryDeferred,
                            addedMs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                JoinAddedMs(context, childUri)
                            } else {
                                NullAddedMs
                            })
                    children.add(file)
                    emit(file)
                }
            }
            directoryDeferred.complete(Directory(uri, relativePath, parent, children))
            emitAll(recursive.asFlow().flattenMerge())
        }
    }

    data class Query(
        val source: List<Location.Opened>,
        val exclude: List<Location.Unopened>,
        val withHidden: Boolean
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    private class JoinAddedMs(private val context: Context, private val uri: Uri) : AddedMs {
        override suspend fun resolve(): Long? {
            val mediaUri = AOSPMediaStore.getMediaUri(context, uri) ?: return null
            return context.contentResolverSafe.useQuery(mediaUri, arrayOf(AOSPMediaStore.Files.FileColumns.DATE_ADDED)) {
                if (it.moveToFirst()) {
                    val dateAddedIndex = it.getColumnIndexOrThrow(AOSPMediaStore.Files.FileColumns.DATE_ADDED)
                    it.getLong(dateAddedIndex) * 1000
                } else {
                    null
                }
            }
        }
    }

    private object NullAddedMs : AddedMs {
        override suspend fun resolve(): Long? = null
    }

    companion object {
        fun from(context: Context, query: Query) = SAF(context, context.contentResolverSafe, query)

        private val PROJECTION =
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED)
    }
}

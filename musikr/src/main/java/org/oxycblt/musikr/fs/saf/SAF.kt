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
import android.provider.MediaStore as AOSPMediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.oxycblt.musikr.fs.AddedMs
import org.oxycblt.musikr.fs.Directory
import org.oxycblt.musikr.fs.FS
import org.oxycblt.musikr.fs.FSUpdate
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.track.LocationObserver
import org.oxycblt.musikr.util.tryAsync
import org.oxycblt.musikr.util.tryAsyncWith
import org.oxycblt.musikr.util.tryAwaitAll

@OptIn(ExperimentalCoroutinesApi::class)
class SAF
private constructor(
    private val context: Context,
    private val contentResolver: ContentResolver,
    private val query: Query
) : FS {
    override suspend fun explore(files: Channel<File>): Deferred<Result<Unit>> = coroutineScope {
        tryAsyncWith(files, Dispatchers.Main) {
            query.source
                .map { location ->
                    exploreDirectoryImpl(
                        location.uri,
                        DocumentsContract.getTreeDocumentId(location.uri),
                        location.path,
                        null,
                        query.exclude.mapTo(mutableSetOf()) { it.path },
                        files)
                }
                .tryAwaitAll()
        }
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

    private fun CoroutineScope.exploreDirectoryImpl(
        rootUri: Uri,
        treeDocumentId: String,
        relativePath: Path,
        parent: Deferred<Directory>?,
        exclude: Set<Path>,
        files: Channel<File>
    ): Deferred<Result<Unit>> =
        tryAsync(Dispatchers.IO) {
            // Make a kotlin future
            val uri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, treeDocumentId)
            val directoryDeferred = CompletableDeferred<Directory>()
            val recursive = mutableListOf<Deferred<Result<Unit>>>()
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
                            exploreDirectoryImpl(
                                rootUri, childId, newPath, directoryDeferred, exclude, files))
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
                                addedMs =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        JoinAddedMs(context, childUri)
                                    } else {
                                        NullAddedMs
                                    })
                        children.add(file)
                        files.send(file)
                    }
                }
                directoryDeferred.complete(Directory(uri, relativePath, parent, children))
            }
            recursive.tryAwaitAll()
        }

    data class Query(
        val source: List<Location.Opened>,
        val exclude: List<Location.Unopened>,
        val withHidden: Boolean
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    private class JoinAddedMs(private val context: Context, private val uri: Uri) : AddedMs {
        override suspend fun resolve(): Long? {
            val mediaUri =
                try {
                    AOSPMediaStore.getMediaUri(context, uri) ?: return null
                } catch (e: Exception) {
                    return null
                }
            return context.contentResolverSafe.useQuery(
                mediaUri, arrayOf(AOSPMediaStore.Files.FileColumns.DATE_ADDED)) {
                    if (it.moveToFirst()) {
                        val dateAddedIndex =
                            it.getColumnIndexOrThrow(AOSPMediaStore.Files.FileColumns.DATE_ADDED)
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

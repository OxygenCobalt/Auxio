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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.Path

private const val TAG = "DeviceFS"

internal interface DeviceFS {
    fun explore(locations: Flow<MusicLocation>): Flow<DeviceFile>

    companion object {
        fun from(context: Context, withHidden: Boolean): DeviceFS =
            DeviceFSImpl(context, withHidden)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private class DeviceFSImpl(
    private val context: Context,
    private val withHidden: Boolean
) : DeviceFS {
    private val contentResolver = context.contentResolverSafe
    private val cache = DeviceFSCache(context)

    override fun explore(locations: Flow<MusicLocation>): Flow<DeviceFile> =
        flow {
            // Initialize the cache
            cache.loadCache()
            // Start a new traversal to track processed URIs for cleanup
            cache.resetTraversalTracking()

            emitAll(
                locations.flatMapMerge { location ->
                    exploreDirectoryImpl(
                        location.uri,
                        DocumentsContract.getTreeDocumentId(location.uri),
                        location.path,
                        null
                    )
                }.flowOn(Dispatchers.IO)
            )
        }.onCompletion {
            // Cleanup and save cache when exploration is complete
            cache.cleanupCache()
            cache.saveCache()
        }

    private fun exploreDirectoryImpl(
        rootUri: Uri,
        treeDocumentId: String,
        relativePath: Path,
        parent: Deferred<DeviceDirectory>?
    ): Flow<DeviceFile> = flow {
        // Create deferred value for this directory
        val directoryUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, treeDocumentId)
        val directoryDeferred = CompletableDeferred<DeviceDirectory>()
        val recursive = mutableListOf<Flow<DeviceFile>>()
        val children = mutableListOf<DeviceFSEntry>()

        // Try to use cached directory contents
        val cacheHit = cache.validateDirectoryCache(directoryUri)

        if (cacheHit) {
            // We got a cache hit, process the cached children
            Log.d(TAG, "Cache hit for $directoryUri")
            val cachedChildren = cache.getCachedChildren(directoryUri)

            if (cachedChildren != null) {
                for (child in cachedChildren) {
                    val childUri = Uri.parse(child.uri)
                    val childName = child.name

                    // Skip hidden files/directories if withHidden is false
                    if (!withHidden && childName.startsWith(".")) {
                        continue
                    }

                    val newPath = relativePath.file(childName)

                    if (child.type == EntryType.DIRECTORY) {
                        // For directories, recursively process them
                        val childId = DocumentsContract.getDocumentId(childUri)
                        recursive.add(
                            exploreDirectoryImpl(rootUri, childId, newPath, directoryDeferred)
                        )
                    } else {
                        // For files, create DeviceFile objects and emit them
                        val file = DeviceFile(
                            uri = childUri,
                            mimeType = "", // We don't cache mime type but it's not used elsewhere
                            path = newPath,
                            size = 0, // We don't cache size but it's not used elsewhere
                            modifiedMs = child.modifiedDate,
                            parent = directoryDeferred
                        )
                        children.add(file)
                        emit(file)
                    }
                }
            }
        } else {
            // Cache miss or stale data, query the directory contents directly
            Log.d(TAG, "Cache miss for $directoryUri, querying directory contents")
            val uri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, treeDocumentId)

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
                    if (!withHidden && displayName.startsWith(".")) {
                        continue
                    }

                    val newPath = relativePath.file(displayName)
                    val mimeType = cursor.getString(mimeTypeIndex)
                    val lastModified = cursor.getLong(lastModifiedIndex)

                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        recursive.add(
                            exploreDirectoryImpl(rootUri, childId, newPath, directoryDeferred))
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
                                parent = directoryDeferred)
                        children.add(file)
                        emit(file)
                    }
                }
            }
        }

        // Complete the directory structure
        directoryDeferred.complete(DeviceDirectory(directoryUri, relativePath, parent, children))

        // Process all subdirectories
        emitAll(recursive.asFlow().flattenMerge())
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

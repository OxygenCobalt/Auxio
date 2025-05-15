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
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.path.DocumentPathFactory

internal interface DeviceFS {
    fun explore(locations: Flow<MusicLocation>, fileTree: FileTree): Flow<DeviceDirectory>

    companion object {
        fun from(context: Context, withHidden: Boolean): DeviceFS =
            DeviceFSImpl(context.contentResolverSafe, withHidden)
    }
}

private class DeviceFSImpl(
    private val contentResolver: ContentResolver,
    private val withHidden: Boolean
) : DeviceFS {
    private val explorationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun explore(locations: Flow<MusicLocation>, fileTree: FileTree) =
        locations.mapNotNull { location ->
            queryRoot(location, fileTree)
        }

    private suspend fun queryRoot(
        location: MusicLocation,
        fileTree: FileTree
    ): DeviceDirectory? {
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
            return null
        }
        return query(
            location.uri,
            treeDocumentId,
            location.path,
            modifiedMs,
            null,
            fileTree
        )
    }

    private suspend fun query(
        rootUri: Uri,
        treeDocumentId: String,
        path: Path,
        modifiedMs: Long,
        parent: DeviceDirectory?,
        fileTree: FileTree
    ): DeviceDirectory = coroutineScope {
        val uri = DocumentsContract.buildDocumentUriUsingTree(rootUri, treeDocumentId)
        val cached = fileTree.queryDirectory(uri)
        if (cached != null && cached.modifiedMs == modifiedMs) {
            return@coroutineScope hydrateCached(
                cached = cached,
                parentDir = parent,
                path = path,
                fileTree = fileTree
            )
        }
        val dir = DeviceDirectoryImpl(
            uri = uri,
            path = path,
            modifiedMs = modifiedMs,
            parent = parent,
            children = emptyFlow()
        )
        dir.children = flow {
            Log.d("DeviceFS", "Querying $uri")
            contentResolver.useQuery(
                DocumentsContract.buildChildDocumentsUriUsingTree(
                    rootUri,
                    treeDocumentId,
                ), projection = PROJECTION, sortOrder = "${DocumentsContract.Document.COLUMN_DISPLAY_NAME} DESC"
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

                val childSubdirUris = mutableListOf<String>()
                val childFileUris = mutableListOf<String>()

                val subqueries = mutableListOf<DeviceDirectory>()

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
                                parent = dir,
                                fileTree = fileTree
                            )
                        childSubdirUris.add(subdir.uri.toString())
                        emit(StreamedFile.More(subdir))
                    } else {
                        Log.d("DeviceFS", "Querying file $childId")
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
                        val write = CachedFile(
                            uri = childUri.toString(),
                            name = displayName,
                            modifiedMs = lastModified,
                            mimeType = mimeType,
                            size = size
                        )
                        fileTree.updateFile(childUri, write)
                        childFileUris.add(childUri.toString())
                        emit(StreamedFile.More(file))
                    }
                }
                val writeDir = CachedDirectory(
                    uri = uri.toString(),
                    name = requireNotNull(path.name),
                    modifiedMs = modifiedMs,
                    subdirUris = childSubdirUris,
                    fileUris = childFileUris
                )
                fileTree.updateDirectory(uri, writeDir)
                emit(StreamedFile.Done)
            }
        }.shareIn(explorationScope, SharingStarted.Lazily, replay = Int.MAX_VALUE)
            .takeWhile { it is StreamedFile.More }
            .map { (it as StreamedFile.More).value }
        dir
    }

    private suspend fun hydrateCached(
        cached: CachedDirectory,
        parentDir: DeviceDirectory?,
        path: Path,
        fileTree: FileTree
    ): DeviceDirectory = coroutineScope {
        val dir = DeviceDirectoryImpl(
            uri = cached.uri.toUri(),
            path = path,
            modifiedMs = cached.modifiedMs,
            parent = parentDir,
            children = emptyFlow()
        )
        dir.children = flow {
            emitAll(
                merge(
                    cached.subdirUris.asFlow().map { subdirUriString ->
                        val subdirUri = subdirUriString.toUri()
                        val cachedSubdir = requireNotNull(fileTree.queryDirectory(subdirUri)) {
                            "No cached subdir for $subdirUri, malformed cache! Rescan needed."
                        }
                        hydrateCached(cachedSubdir, dir, dir.path.file(cachedSubdir.name), fileTree)
                    },
                    cached.fileUris.asFlow().map {
                        val cachedFile = requireNotNull(fileTree.queryFile(it.toUri()))
                        DeviceFile(
                            uri = it.toUri(),
                            path = dir.path.file(cachedFile.name),
                            modifiedMs = cachedFile.modifiedMs,
                            mimeType = cachedFile.mimeType,
                            size = cachedFile.size,
                            parent = dir
                        )
                    }
                ).map { StreamedFile.More(it) }
            )
            emit(StreamedFile.Done)
        }
            .flowOn(Dispatchers.IO)
            .shareIn(explorationScope, SharingStarted.Lazily, replay = Int.MAX_VALUE)
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




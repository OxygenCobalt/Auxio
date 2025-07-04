/*
 * Copyright (c) 2025 Auxio Project
 * MediaStore.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.mediastore

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield
import org.oxycblt.musikr.fs.Directory
import org.oxycblt.musikr.fs.FS
import org.oxycblt.musikr.fs.FSUpdate
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.path.MediaStorePathInterpreter
import org.oxycblt.musikr.fs.path.VolumeManager
import org.oxycblt.musikr.fs.saf.contentResolverSafe
import org.oxycblt.musikr.fs.saf.useQuery
import org.oxycblt.musikr.fs.track.LocationObserver

/**
 * MediaStore implementation of [FS] that queries the Android MediaStore database for audio files
 * and yields them as [File] instances.
 */
class MediaStoreFS
private constructor(
    private val context: Context,
    private val volumeManager: VolumeManager,
    private val query: Query
) : FS {
    private val pathInterpreterFactory = MediaStorePathInterpreter.Factory.from(volumeManager)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun explore(): Flow<File> = flow {
        val projection = BASE_PROJECTION + pathInterpreterFactory.projection
        var selector = BASE_SELECTOR
        val args = mutableListOf<String>()

        // Filter out audio that is not music, if enabled
        if (query.excludeNonMusic) {
            selector += " AND ${MediaStore.Audio.AudioColumns.IS_MUSIC}=1"
        }

        // Handle include/exclude directories
        when (query.mode) {
            FilterMode.INCLUDE -> {
                val pathSelector =
                    pathInterpreterFactory.createSelector(query.filtered.map { it.path })
                if (pathSelector != null) {
                    selector += " AND (${pathSelector.template})"
                    args.addAll(pathSelector.args)
                }
            }
            FilterMode.EXCLUDE -> {
                val pathSelector =
                    pathInterpreterFactory.createSelector(query.filtered.map { it.path })
                if (pathSelector != null) {
                    selector += " AND NOT (${pathSelector.template})"
                    args.addAll(pathSelector.args)
                }
            }
        }

        // Collect all files and track unique directories
        val allFiles = mutableListOf<File>()

        context.contentResolverSafe.useQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selector,
            args.toTypedArray()) { cursor ->
                val pathInterpreter = pathInterpreterFactory.wrap(cursor)
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                val mimeTypeIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)
                val dateModifiedIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val path = pathInterpreter.extract() ?: continue

                    val id = cursor.getLong(idIndex)
                    val uri =
                        Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
                    val mimeType = cursor.getStringOrNull(mimeTypeIndex) ?: "audio/*"
                    val size = cursor.getLong(sizeIndex)
                    val dateModified =
                        cursor.getLong(dateModifiedIndex) * 1000 // Convert to milliseconds

                    // Create file with empty deferred parent
                    val parentDeferred = CompletableDeferred<Directory>()
                    val deviceFile =
                        File(
                            uri = uri,
                            path = path,
                            modifiedMs = dateModified,
                            mimeType = mimeType,
                            size = size,
                            parent = parentDeferred)

                    allFiles.add(deviceFile)
                    emit(deviceFile)
                    yield()
                }
            }

        // Build directory hierarchy
        buildDirectoryHierarchy(allFiles)
    }

    override fun track(): Flow<FSUpdate> = callbackFlow {
        val observer =
            LocationObserver(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                trySend(FSUpdate.LocationChanged(null))
            }
        awaitClose { observer.release() }
    }

    private fun buildDirectoryHierarchy(files: List<File>) {
        // Map to store directories by their path
        val directoryMap = mutableMapOf<Path, Directory>()

        // Group files by their parent directory path
        val filesByParent = files.groupBy { it.path.directory }

        // First pass: Create all directories and complete file parent deferreds
        for ((dirPath, dirFiles) in filesByParent) {
            val directory =
                directoryMap.getOrPut(dirPath) {
                    // Create parent deferred for this directory
                    val parentDeferred =
                        if (dirPath.components.components.isEmpty()) {
                            // Root directory has no parent
                            null
                        } else {
                            CompletableDeferred<Directory>()
                        }

                    Directory(
                        uri = null, // Directories don't have MediaStore URIs
                        path = dirPath,
                        parent = parentDeferred,
                        children = dirFiles)
                }

            // Update children if directory already existed
            if (directory.children.isEmpty()) {
                directory.children = dirFiles
            }

            // Complete parent deferreds for all files in this directory
            dirFiles.forEach { file ->
                (file.parent as CompletableDeferred<Directory>).complete(directory)
            }
        }

        // Second pass: Create any missing parent directories
        val directories = directoryMap.values.toList()
        for (directory in directories) {
            var currentPath = directory.path
            while (currentPath.components.components.isNotEmpty()) {
                val parentPath = currentPath.directory
                if (!directoryMap.containsKey(parentPath)) {
                    val grandParentDeferred =
                        if (parentPath.components.components.isEmpty()) {
                            null
                        } else {
                            CompletableDeferred<Directory>()
                        }

                    directoryMap[parentPath] =
                        Directory(
                            uri = null,
                            path = parentPath,
                            parent = grandParentDeferred,
                            children = emptyList())
                }
                currentPath = parentPath
            }
        }

        // Third pass: Complete all directory parent deferreds
        for (directory in directoryMap.values) {
            val parentDeferred = directory.parent as? CompletableDeferred<Directory>
            if (parentDeferred != null && !parentDeferred.isCompleted) {
                val parentPath = directory.path.directory
                val parentDirectory = directoryMap[parentPath]
                if (parentDirectory != null) {
                    parentDeferred.complete(parentDirectory)
                }
            }
        }
    }

    data class Query(
        val mode: FilterMode,
        val filtered: List<Location.Unopened>,
        val excludeNonMusic: Boolean
    )

    enum class FilterMode {
        INCLUDE,
        EXCLUDE
    }

    companion object {
        fun from(context: Context, query: Query) =
            MediaStoreFS(
                context = context, volumeManager = VolumeManager.from(context), query = query)

        /**
         * The base selector that works across all versions of android. Excludes files with zero
         * size.
         */
        private const val BASE_SELECTOR = "NOT ${MediaStore.Audio.Media.SIZE}=0"

        /** The base projection that works across all versions of android. */
        private val BASE_PROJECTION =
            arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.MIME_TYPE)
    }
}

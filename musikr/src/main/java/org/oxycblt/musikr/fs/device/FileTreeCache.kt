/*
 * Copyright (c) 2024 Auxio Project
 * FileTreeCache.kt is part of Auxio.
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

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File

interface FileTreeCache {
    fun read(): FileTree
}

interface FileTree {
    suspend fun queryDirectory(uri: Uri): CachedDirectory?
    suspend fun updateDirectory(uri: Uri, directory: CachedDirectory)

    suspend fun queryFile(uri: Uri): CachedFile?
    suspend fun updateFile(uri: Uri, file: CachedFile)
    
    suspend fun write()
}

// Define the sealed interface
@Serializable
sealed interface FileSystemEntry {
    val uri: String
    val modifiedMs: Long
}

@Serializable
data class CachedDirectory(
    override val uri: String,
    val name: String,
    override val modifiedMs: Long,
    val subdirUris: List<String>,
    val fileUris: List<String>
) : FileSystemEntry

@Serializable
data class CachedFile(
    override val uri: String,
    val name: String,
    override val modifiedMs: Long,
    val mimeType: String,
    val size: Long
) : FileSystemEntry

@Serializable
data class FileTreeData(
    val directories: Map<String, CachedDirectory> = mapOf(),
    val files: Map<String, CachedFile> = mapOf()
)

class FileTreeCacheImpl(private val context: Context) : FileTreeCache {
    companion object {
        private const val TAG = "FileTreeCache"
        private const val CACHE_FILENAME = "file_tree_cache.json"
        private val json = Json { 
            ignoreUnknownKeys = true 
            prettyPrint = true
        }
    }
    
    override fun read(): FileTree {
        val cacheFile = File(context.cacheDir, CACHE_FILENAME)
        
        return if (cacheFile.exists()) {
            try {
                val jsonContent = cacheFile.readText()
                val fileTreeCache = json.decodeFromString<FileTreeData>(jsonContent)
                FileTreeImpl(context, fileTreeCache.directories.toMutableMap(), fileTreeCache.files.toMutableMap())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read cache file", e)
                FileTreeImpl(context, mutableMapOf(), mutableMapOf())
            }
        } else {
            Log.i(TAG, "Cache file does not exist, creating new FileTree")
            FileTreeImpl(context, mutableMapOf(), mutableMapOf())
        }
    }
}

class FileTreeImpl(
    private val context: Context,
    private val mutableDirectories: MutableMap<String, CachedDirectory> = mutableMapOf(),
    private val mutableFiles: MutableMap<String, CachedFile> = mutableMapOf()
) : FileTree {
    companion object {
        private const val TAG = "FileTree"
        private const val CACHE_FILENAME = "file_tree_cache.json"
        private val json = Json { 
            ignoreUnknownKeys = true 
            prettyPrint = true
        }
    }

    // Directory operations
    override suspend fun queryDirectory(uri: Uri): CachedDirectory? {
        Log.d("FileTreeCache", "Querying directory: $uri ${mutableDirectories[uri.toString()]}")
        val uriString = uri.toString()
        return mutableDirectories[uriString]
    }

    override suspend fun updateDirectory(uri: Uri, directory: CachedDirectory) {
        Log.d("FileTreeCache", "Updating directory: $uri")
        val uriString = uri.toString()
        mutableDirectories[uriString] = directory
    }

    // File operations
    override suspend fun queryFile(uri: Uri): CachedFile? {
        Log.d("FileTreeCache", "Querying file: $uri")
        val uriString = uri.toString()
        return mutableFiles[uriString]
    }

    override suspend fun updateFile(uri: Uri, file: CachedFile) {
        Log.d("FileTreeCache", "Updating file: $uri")
        val uriString = uri.toString()
        mutableFiles[uriString] = file
    }
    
    override suspend fun write() = withContext(Dispatchers.IO) {
        Log.d(TAG, "Writing cache to disk")
        val cacheFile = File(context.cacheDir, CACHE_FILENAME)
        try {
            val fileTreeCache = FileTreeData(
                directories = mutableDirectories.toMap(),
                files = mutableFiles.toMap()
            )
            
            val jsonContent = json.encodeToString(fileTreeCache)
            cacheFile.writeText(jsonContent)
            Log.d(TAG, "Successfully wrote cache to disk")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write cache file", e)
        }
        Unit
    }
}

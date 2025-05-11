/*
 * Copyright (c) 2024 Auxio Project
 * DeviceFSCache.kt is part of Auxio.
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
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

private const val TAG = "DeviceFSCache"
private const val CACHE_FILE_NAME = "device_fs_cache.json"
private const val SCHEMA_VERSION = "1.0"

/**
 * Caches directory structure information to minimize ContentResolver queries.
 * 
 * This cache is based on the concept that a directory's modification date changes
 * when its direct contents change (files added, removed, renamed). We can avoid
 * expensive children queries by caching directory contents and validating them
 * against the directory's modification timestamp.
 */
internal class DeviceFSCache(private val context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver
    private val cache: MutableMap<String, CachedDirectory> = mutableMapOf()
    private val processedUris = mutableSetOf<String>()
    
    /**
     * Gets information about a directory URI.
     * 
     * @param uri The document URI to get information for
     * @return A DirectoryInfo object or null if there was an error
     */
    fun getDirectoryInfo(uri: Uri): DirectoryInfo? {
        return contentResolver.useQuery(
            uri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )
        ) { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val lastModifiedIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                
                val name = cursor.getString(nameIndex)
                val lastModified = cursor.getLong(lastModifiedIndex)
                
                DirectoryInfo(name, lastModified)
            } else {
                null
            }
        }
    }
    
    /**
     * Gets the children of a directory URI.
     * 
     * @param uri The document URI to get children for
     * @return A list of DirectoryChildren or null if there was an error
     */
    fun getDirectoryChildren(uri: Uri): List<DirectoryChild>? {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri,
            DocumentsContract.getDocumentId(uri)
        )
        
        return contentResolver.useQuery(
            childrenUri,
            CHILDREN_PROJECTION
        ) { cursor ->
            val children = mutableListOf<DirectoryChild>()
            
            val documentIdIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val displayNameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeTypeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
            val lastModifiedIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
            
            while (cursor.moveToNext()) {
                val childId = cursor.getString(documentIdIndex)
                val displayName = cursor.getString(displayNameIndex)
                val mimeType = cursor.getString(mimeTypeIndex)
                val lastModified = cursor.getLong(lastModifiedIndex)
                
                val isDirectory = mimeType == DocumentsContract.Document.MIME_TYPE_DIR
                val childUri = DocumentsContract.buildDocumentUriUsingTree(uri, childId)
                
                children.add(
                    DirectoryChild(
                        uri = childUri.toString(),
                        name = displayName,
                        type = if (isDirectory) EntryType.DIRECTORY else EntryType.FILE,
                        modifiedDate = lastModified
                    )
                )
            }
            
            children
        } ?: emptyList()
    }
    
    /**
     * Validates the cached data against directory's current state.
     * If directory's modification time has changed, cache is updated.
     * 
     * @param uri The URI of the directory to validate
     * @return Whether the cache was hit (true) or needed updating (false)
     */
    fun validateDirectoryCache(uri: Uri): Boolean {
        val uriString = uri.toString()
        processedUris.add(uriString)
        
        val dirInfo = getDirectoryInfo(uri) ?: run {
            // Directory no longer exists or accessible, remove from cache
            cache.remove(uriString)
            return false
        }
        
        // Check if we have cache entry and whether it's still valid
        val cachedDir = cache[uriString]
        if (cachedDir != null && cachedDir.modifiedDate == dirInfo.lastModified) {
            // Cache hit - modification timestamp matches, so content hasn't changed
            return true
        }
        
        // Cache miss or stale data - need to update
        val children = getDirectoryChildren(uri) ?: emptyList()
        cache[uriString] = CachedDirectory(
            modifiedDate = dirInfo.lastModified,
            children = children
        )
        
        return false
    }
    
    /**
     * Gets cached directory children without validation.
     * Use validateDirectoryCache() first to ensure cached data is valid.
     * 
     * @param uri The URI of the directory
     * @return The cached children or null if not cached
     */
    fun getCachedChildren(uri: Uri): List<DirectoryChild>? {
        return cache[uri.toString()]?.children
    }
    
    /**
     * Cleans up the cache by removing entries that weren't accessed
     * in the current traversal.
     */
    fun cleanupCache() {
        val toRemove = cache.keys.filter { it !in processedUris }
        toRemove.forEach { cache.remove(it) }
    }
    
    /**
     * Resets the processed URIs tracking set for a new full traversal.
     */
    fun resetTraversalTracking() {
        processedUris.clear()
    }
    
    /**
     * Saves the cache to disk.
     */
    suspend fun saveCache() = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            val cacheData = CacheData(
                schemaVersion = SCHEMA_VERSION,
                entries = cache
            )
            cacheFile.writeText(Json.encodeToString(CacheData.serializer(), cacheData))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save cache", e)
        }
    }
    
    /**
     * Loads the cache from disk.
     */
    suspend fun loadCache() = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            if (cacheFile.exists()) {
                val cacheDataString = cacheFile.readText()
                val cacheData = Json.decodeFromString<CacheData>(cacheDataString)
                
                // Only load if schema version matches
                if (cacheData.schemaVersion == SCHEMA_VERSION) {
                    cache.clear()
                    cache.putAll(cacheData.entries)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load cache", e)
            // Start with empty cache if loading fails
            cache.clear()
        }
    }
    
    companion object {
        val CHILDREN_PROJECTION = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )
    }
}

/**
 * Information about a directory.
 */
data class DirectoryInfo(
    val name: String,
    val lastModified: Long
)

/**
 * Type of a directory entry.
 */
enum class EntryType {
    FILE,
    DIRECTORY
}

/**
 * Child entry in a directory.
 */
@Serializable
data class DirectoryChild(
    val uri: String,
    val name: String,
    val type: EntryType,
    val modifiedDate: Long
)

/**
 * Cached information about a directory.
 */
@Serializable
data class CachedDirectory(
    val modifiedDate: Long,
    val children: List<DirectoryChild>
)

/**
 * Structure for serializing/deserializing the entire cache.
 */
@Serializable
data class CacheData(
    val schemaVersion: String,
    val entries: Map<String, CachedDirectory>
)
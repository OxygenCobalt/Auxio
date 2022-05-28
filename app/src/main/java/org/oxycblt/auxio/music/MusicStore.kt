/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.music

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.ContextCompat
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.music.indexer.Indexer
import org.oxycblt.auxio.util.contentResolverSafe
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

/**
 * The main storage for music items. Getting an instance of this object is more complicated as it
 * loads asynchronously. See the companion object for more.
 *
 * TODO: Add automatic rescanning [major change]
 * @author OxygenCobalt
 */
class MusicStore private constructor() {
    private var response: Response? = null
    val library: Library?
        get() =
            response?.let { currentResponse ->
                if (currentResponse is Response.Ok) {
                    currentResponse.library
                } else {
                    null
                }
            }

    private val callbacks = mutableListOf<Callback>()

    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    /** Load/Sort the entire music library. Should always be ran on a coroutine. */
    suspend fun load(context: Context): Response {
        logD("Starting initial music load")
        val newResponse = withContext(Dispatchers.IO) { loadImpl(context) }.also { response = it }
        for (callback in callbacks) {
            callback.onMusicUpdate(newResponse)
        }
        return newResponse
    }

    private fun loadImpl(context: Context): Response {
        val notGranted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED

        if (notGranted) {
            return Response.NoPerms
        }

        val response =
            try {
                val start = System.currentTimeMillis()
                val library = Indexer.index(context)
                if (library != null) {
                    logD(
                        "Music load completed successfully in ${System.currentTimeMillis() - start}ms")
                    Response.Ok(library)
                } else {
                    logE("No music found")
                    Response.NoMusic
                }
            } catch (e: Exception) {
                logE("Music loading failed.")
                logE(e.stackTraceToString())
                Response.Err(e)
            }

        return response
    }

    data class Library(
        val genres: List<Genre>,
        val artists: List<Artist>,
        val albums: List<Album>,
        val songs: List<Song>
    ) {
        /** Find a song in a faster manner using an ID for its album as well. */
        fun findSongFast(songId: Long, albumId: Long): Song? {
            return albums.find { it.id == albumId }?.songs?.find { it.id == songId }
        }

        /**
         * Find a song for a [uri], this is similar to [findSongFast], but with some kind of content
         * uri.
         * @return The corresponding [Song] for this [uri], null if there isn't one.
         */
        fun findSongForUri(context: Context, uri: Uri): Song? {
            context.contentResolverSafe
                .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    cursor.moveToFirst()
                    val fileName =
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))

                    return songs.find { it.fileName == fileName }
                }

            return null
        }
    }

    /**
     * A response that [MusicStore] returns when loading music. And before you ask, yes, I do like
     * rust.
     */
    sealed class Response {
        class Ok(val library: Library) : Response()
        class Err(throwable: Throwable) : Response()
        object NoMusic : Response()
        object NoPerms : Response()
    }

    interface Callback {
        fun onMusicUpdate(response: Response)
    }

    companion object {
        @Volatile private var INSTANCE: MusicStore? = null

        fun getInstance(): MusicStore {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = MusicStore()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

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
import org.oxycblt.auxio.music.indexer.useQuery
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

    /** Add a callback to this instance. Make sure to remove it when done. */
    fun addCallback(callback: Callback) {
        response?.let(callback::onMusicUpdate)
        callbacks.add(callback)
    }

    /** Remove a callback from this instance. */
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    /** Load/Sort the entire music library. Should always be ran on a coroutine. */
    suspend fun load(context: Context, callback: LoadCallback): Response {
        logD("Starting initial music load")

        callback.onLoadStateChanged(null)
        val newResponse =
            withContext(Dispatchers.IO) { loadImpl(context, callback) }.also { response = it }

        callback.onLoadStateChanged(LoadState.Complete(newResponse))
        for (responseCallbacks in callbacks) {
            responseCallbacks.onMusicUpdate(newResponse)
        }

        return newResponse
    }

    private fun loadImpl(context: Context, callback: LoadCallback): Response {
        val notGranted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED

        if (notGranted) {
            return Response.NoPerms
        }

        val response =
            try {
                val start = System.currentTimeMillis()
                val library = Indexer.index(context, callback)
                if (library != null) {
                    logD(
                        "Music load completed successfully in " +
                            "${System.currentTimeMillis() - start}ms")
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
        /** Find a song in a faster manner by using the album ID as well.. */
        fun findSongFast(songId: Long, albumId: Long) =
            albums.find { it.id == albumId }.run { songs.find { it.id == songId } }

        /**
         * Find a song for a [uri], this is similar to [findSongFast], but with some kind of content
         * uri.
         * @return The corresponding [Song] for this [uri], null if there isn't one.
         */
        fun findSongForUri(context: Context, uri: Uri) =
            context.contentResolverSafe.useQuery(uri, arrayOf(OpenableColumns.DISPLAY_NAME)) {
                cursor ->
                cursor.moveToFirst()

                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))

                songs.find { it.fileName == displayName }
            }
    }

    /** Represents the current state of the loading process. */
    sealed class LoadState {
        data class Indexing(val current: Int, val total: Int) : LoadState()
        data class Complete(val response: Response) : LoadState()
    }

    /**
     * A callback for events that occur during the loading process. This is used by [load] in order
     * to have a separate callback interface that is more efficient for rapid-fire updates.
     */
    interface LoadCallback {
        /**
         * Called when the state of the loading process changes. A value of null represents the
         * beginning of a loading process.
         */
        fun onLoadStateChanged(state: LoadState?)
    }

    /** Represents the possible outcomes of a loading process. */
    sealed class Response {
        data class Ok(val library: Library) : Response()
        data class Err(val throwable: Throwable) : Response()
        object NoMusic : Response()
        object NoPerms : Response()
    }

    /** A callback for awaiting the loading of music. */
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

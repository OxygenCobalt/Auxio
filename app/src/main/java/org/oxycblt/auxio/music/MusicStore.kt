/*
 * Copyright (c) 2021 Auxio Project
 * MusicStore.kt is part of Auxio.
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
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import java.lang.Exception

/**
 * The main storage for music items.
 * Getting an instance of this object is more complicated as it loads asynchronously.
 * See the companion object for more.
 * @author OxygenCobalt
 */
class MusicStore private constructor() {
    private var mGenres = listOf<Genre>()
    val genres: List<Genre> get() = mGenres

    private var mArtists = listOf<Artist>()
    val artists: List<Artist> get() = mArtists

    private var mAlbums = listOf<Album>()
    val albums: List<Album> get() = mAlbums

    private var mSongs = listOf<Song>()
    val songs: List<Song> get() = mSongs

    /**
     * Load/Sort the entire music library. Should always be ran on a coroutine.
     */
    private fun load(context: Context): Response {
        logD("Starting initial music load...")

        val notGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED

        if (notGranted) {
            return Response.Err(ErrorKind.NO_PERMS)
        }

        try {
            val start = System.currentTimeMillis()

            val loader = MusicLoader(context)
            loader.load()

            if (loader.songs.isEmpty()) {
                return Response.Err(ErrorKind.NO_MUSIC)
            }

            mSongs = loader.songs
            mAlbums = loader.albums
            mArtists = loader.artists
            mGenres = loader.genres

            logD("Music load completed successfully in ${System.currentTimeMillis() - start}ms.")
        } catch (e: Exception) {
            logE("Something went horribly wrong.")
            logE(e.stackTraceToString())

            return Response.Err(ErrorKind.FAILED)
        }

        return Response.Ok(this)
    }

    /**
     * Find a song in a faster manner using a hash for its album as well.
     */
    fun findSongFast(songHash: Long, albumHash: Long): Song? {
        return albums.find { it.hash == albumHash }?.songs?.find { it.hash == songHash }
    }

    /**
     * Find a song for a [uri], this is similar to [findSongFast], but with some kind of content uri.
     * @return The corresponding [Song] for this [uri], null if there isnt one.
     */
    fun findSongForUri(uri: Uri, resolver: ContentResolver): Song? {
        val cur = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)

        cur?.use { cursor ->
            cursor.moveToFirst()

            // Make studio shut up about "invalid ranges" that don't exist
            @SuppressLint("Range")
            val fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

            return songs.find { it.fileName == fileName }
        }

        return null
    }

    /**
     * A response that [MusicStore] returns when loading music.
     * And before you ask, yes, I do like rust.
     */
    sealed class Response {
        class Ok(val musicStore: MusicStore) : Response()
        class Err(val kind: ErrorKind) : Response()
    }

    enum class ErrorKind {
        NO_PERMS, NO_MUSIC, FAILED
    }

    companion object {
        @Volatile
        private var RESPONSE: Response? = null

        /**
         * Initialize the loading process for this instance. This must be ran on a background
         * thread. If the instance has already been loaded successfully, then it will be returned
         * immediately.
         */
        suspend fun initInstance(context: Context): Response {
            val currentInstance = RESPONSE

            if (currentInstance is Response.Ok) {
                return currentInstance
            }

            val response = withContext(Dispatchers.IO) {
                val response = MusicStore().load(context)

                synchronized(this) {
                    RESPONSE = response
                }

                response
            }

            return response
        }

        /**
         * Await the successful creation of a [MusicStore] instance. The co-routine calling
         * this will block until the successful creation of a [MusicStore], in which it will
         * then be returned.
         */
        suspend fun awaitInstance() = withContext(Dispatchers.Default) {
            // We have to do a withContext call so we don't block the JVM thread
            val musicStore: MusicStore

            while (true) {
                val response = RESPONSE

                if (response is Response.Ok) {
                    musicStore = response.musicStore
                    break
                }
            }

            musicStore
        }

        /**
         * Maybe get a MusicStore instance. This is useful if you are running code while the
         * loading process may still be going on.
         *
         * @return null if the music store instance is still loading or if the loading process has
         * encountered an error. An instance is returned otherwise.
         */
        fun maybeGetInstance(): MusicStore? {
            val currentInstance = RESPONSE

            return if (currentInstance is Response.Ok) {
                currentInstance.musicStore
            } else {
                null
            }
        }

        /**
         * Require a MusicStore instance. This function is dangerous and should only be used if
         * it's guaranteed that the caller's code will only be called after the initial loading
         * process.
         */
        fun requireInstance(): MusicStore {
            return requireNotNull(maybeGetInstance()) {
                "Required MusicStore instance was not available."
            }
        }

        /**
         * Check if this instance has successfully loaded or not.
         */
        fun loaded(): Boolean {
            return maybeGetInstance() != null
        }
    }
}

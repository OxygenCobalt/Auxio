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

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import org.oxycblt.auxio.util.contentResolverSafe

/**
 * The main storage for music items. The items themselves are located in a [Library], however this
 * might not be available at all times.
 *
 * TODO: Add automatic rescanning [major change]
 * @author OxygenCobalt
 */
class MusicStore private constructor() {
    private val callbacks = mutableListOf<Callback>()

    var library: Library? = null
        set(value) {
            field = value
            for (callback in callbacks) {
                callback.onLibraryChanged(library)
            }
        }

    /** Add a callback to this instance. Make sure to remove it when done. */
    fun addCallback(callback: Callback) {
        callback.onLibraryChanged(library)
        callbacks.add(callback)
    }

    /** Remove a callback from this instance. */
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    /** Represents a library of music owned by [MusicStore]. */
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

        /** "Sanitize" a music object from a previous library iteration. */
        fun sanitize(song: Song) = songs.find { it.id == song.id }
        fun sanitize(album: Album) = albums.find { it.id == album.id }
        fun sanitize(artist: Artist) = artists.find { it.id == artist.id }
        fun sanitize(genre: Genre) = genres.find { it.id == genre.id }
    }

    /** A callback for awaiting the loading of music. */
    interface Callback {
        fun onLibraryChanged(library: Library?)
    }

    companion object {
        @Volatile private var INSTANCE: MusicStore? = null

        /** Get the process-level instance of [MusicStore] */
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

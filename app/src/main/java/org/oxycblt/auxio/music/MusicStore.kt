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
 * The main storage for music items.
 *
 * Whereas other apps load music from MediaStore as it is shown, Auxio does not do that, as it
 * cripples any kind of advanced metadata functionality. Instead, Auxio loads all music into a
 * in-memory relational data-structure called [Library]. This costs more memory-wise, but is also
 * much more sensible.
 *
 * The only other, memory-efficient option is to create our own hybrid database that leverages both
 * a typical DB and a mem-cache, like Vinyl. But why would we do that when I've encountered no real
 * issues with the current system.
 *
 * [Library] may not be available at all times, so leveraging [Callback] is recommended. Consumers
 * should also be aware that [Library] may change while they are running, and design their work
 * accordingly.
 *
 * @author OxygenCobalt
 */
class MusicStore private constructor() {
    private val callbacks = mutableListOf<Callback>()

    var library: Library? = null
        private set

    /** Add a callback to this instance. Make sure to remove it when done. */
    @Synchronized
    fun addCallback(callback: Callback) {
        callback.onLibraryChanged(library)
        callbacks.add(callback)
    }

    /** Remove a callback from this instance. */
    @Synchronized
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    /** Update the library in this instance. This is only meant for use by the internal indexer. */
    @Synchronized
    fun updateLibrary(newLibrary: Library?) {
        library = newLibrary
        for (callback in callbacks) {
            callback.onLibraryChanged(library)
        }
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

                songs.find { it.path.name == displayName }
            }

        /** Sanitize an old item to find the corresponding item in a new library. */
        fun sanitize(song: Song) = songs.find { it.id == song.id }
        /** Sanitize an old item to find the corresponding item in a new library. */
        fun sanitize(songs: List<Song>) = songs.mapNotNull { sanitize(it) }
        /** Sanitize an old item to find the corresponding item in a new library. */
        fun sanitize(album: Album) = albums.find { it.id == album.id }
        /** Sanitize an old item to find the corresponding item in a new library. */
        fun sanitize(artist: Artist) = artists.find { it.id == artist.id }
        /** Sanitize an old item to find the corresponding item in a new library. */
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

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
import org.oxycblt.auxio.music.filesystem.contentResolverSafe
import org.oxycblt.auxio.music.filesystem.useQuery

/**
 * A repository granting access to the music library..
 *
 * This can be used to obtain certain music items, or await changes to the music library. It is
 * generally recommended to use this over Indexer to keep track of the library state, as the
 * interface will be less volatile.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicStore private constructor() {
    private val listeners = mutableListOf<Listener>()

    /**
     * The current [Library]. May be null if a [Library] has not been successfully loaded yet. This
     * can change, so it's highly recommended to not access this directly and instead rely on
     * [Listener].
     */
    @Volatile
    var library: Library? = null
        set(value) {
            field = value
            for (callback in listeners) {
                callback.onLibraryChanged(library)
            }
        }

    /**
     * Add a [Listener] to this instance. This can be used to receive changes in the music library.
     * Will invoke all [Listener] methods to initialize the instance with the current state.
     * @param listener The [Listener] to add.
     * @see Listener
     */
    @Synchronized
    fun addListener(listener: Listener) {
        listener.onLibraryChanged(library)
        listeners.add(listener)
    }

    /**
     * Remove a [Listener] from this instance, preventing it from recieving any further updates.
     * @param listener The [Listener] to remove. Does nothing if the [Listener] was never added in
     * the first place.
     * @see Listener
     */
    @Synchronized
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    /**
     * A library of [Music] instances.
     * @param songs All [Song]s loaded from the device.
     * @param albums All [Album]s that could be created.
     * @param artists All [Artist]s that could be created.
     * @param genres All [Genre]s that could be created.
     */
    data class Library(
        val songs: List<Song>,
        val albums: List<Album>,
        val artists: List<Artist>,
        val genres: List<Genre>,
    ) {
        private val uidMap = HashMap<Music.UID, Music>()

        init {
            // The data passed to Library initially are complete, but are still volitaile.
            // Finalize them to ensure they are well-formed. Also initialize the UID map in
            // the same loop for efficiency.
            for (song in songs) {
                song._finalize()
                uidMap[song.uid] = song
            }

            for (album in albums) {
                album._finalize()
                uidMap[album.uid] = album
            }

            for (artist in artists) {
                artist._finalize()
                uidMap[artist.uid] = artist
            }

            for (genre in genres) {
                genre._finalize()
                uidMap[genre.uid] = genre
            }
        }

        /**
         * Finds a [Music] item [T] in the library by it's [Music.UID].
         * @param uid The [Music.UID] to search for.
         * @return The [T] corresponding to the given [Music.UID], or null if nothing could be found
         * or the [Music.UID] did not correspond to a [T].
         */
        @Suppress("UNCHECKED_CAST") fun <T : Music> find(uid: Music.UID) = uidMap[uid] as? T

        /**
         * Convert a [Song] from an another library into a [Song] in this [Library].
         * @param song The [Song] to convert.
         * @return The analogous [Song] in this [Library], or null if it does not exist.
         */
        fun sanitize(song: Song) = find<Song>(song.uid)

        /**
         * Convert a [Album] from an another library into a [Album] in this [Library].
         * @param album The [Album] to convert.
         * @return The analogous [Album] in this [Library], or null if it does not exist.
         */
        fun sanitize(album: Album) = find<Album>(album.uid)

        /**
         * Convert a [Artist] from an another library into a [Artist] in this [Library].
         * @param artist The [Artist] to convert.
         * @return The analogous [Artist] in this [Library], or null if it does not exist.
         */
        fun sanitize(artist: Artist) = find<Artist>(artist.uid)

        /**
         * Convert a [Genre] from an another library into a [Genre] in this [Library].
         * @param genre The [Genre] to convert.
         * @return The analogous [Genre] in this [Library], or null if it does not exist.
         */
        fun sanitize(genre: Genre) = find<Genre>(genre.uid)

        /**
         * Find a [Song] instance corresponding to the given Intent.ACTION_VIEW [Uri].
         * @param context [Context] required to analyze the [Uri].
         * @param uri [Uri] to search for.
         * @return A [Song] corresponding to the given [Uri], or null if one could not be found.
         */
        fun findSongForUri(context: Context, uri: Uri) =
            context.contentResolverSafe.useQuery(
                uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)) { cursor ->
                cursor.moveToFirst()
                // We are weirdly limited to DISPLAY_NAME and SIZE when trying to locate a
                // song. Do what we can to hopefully find the song the user wanted to open.
                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
                songs.find { it.path.name == displayName && it.size == size }
            }
    }

    /** A listener for changes in the music library. */
    interface Listener {
        /**
         * Called when the current [Library] has changed.
         * @param library The new [Library], or null if no [Library] has been loaded yet.
         */
        fun onLibraryChanged(library: Library?)
    }

    companion object {
        @Volatile private var INSTANCE: MusicStore? = null

        /**
         * Get a singleton instance.
         * @return The (possibly newly-created) singleton instance.
         */
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

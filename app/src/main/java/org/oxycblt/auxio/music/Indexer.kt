/*
 * Copyright (c) 2022 Auxio Project
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
import android.database.Cursor
import android.os.Build
import androidx.core.content.ContextCompat
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.backend.Api21MediaStoreBackend
import org.oxycblt.auxio.music.backend.Api29MediaStoreBackend
import org.oxycblt.auxio.music.backend.Api30MediaStoreBackend
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW

/**
 * Auxio's media indexer.
 *
 * Auxio's media indexer is somewhat complicated, as it has grown to support a variety of use cases
 * (and hacky garbage) in order to produce the best possible experience. It is split into three
 * distinct steps:
 *
 * 1. Finding a [Backend] to use and then querying the media database with it.
 * 2. Using the [Backend] and the media data to create songs
 * 3. Using the songs to build the library, which primarily involves linking up all data objects
 * with their corresponding parents/children.
 *
 * This class in particular handles 3 primarily. For the code that handles 1 and 2, see the
 * [Backend] implementations.
 *
 * This class also fulfills the role of maintaining the current music loading state, which seems
 * like a job for [MusicStore] but in practice is only really leveraged by the components that
 * directly work with music loading, making such redundant.
 *
 * @author OxygenCobalt
 */
class Indexer {
    private var lastResponse: Response? = null
    private var indexingState: Indexing? = null

    private var currentGeneration = 0L
    private var controller: Controller? = null
    private var callback: Callback? = null

    /**
     * Whether this instance is in an indeterminate state or not, where nothing has been previously
     * loaded, yet no loading is going on.
     */
    val isIndeterminate: Boolean
        get() = lastResponse == null && indexingState == null

    /** Whether this instance is actively indexing or not. */
    val isIndexing: Boolean
        get() = indexingState != null

    /** Register a [Controller] with this instance. */
    @Synchronized
    fun registerController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller != null) {
            logW("Controller is already registered")
            return
        }

        synchronized(this) { this.controller = controller }
    }

    /** Unregister a [Controller] with this instance. */
    @Synchronized
    fun unregisterController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller !== controller) {
            logW("Given controller did not match current controller")
            return
        }

        this.controller = null
    }

    @Synchronized
    fun registerCallback(callback: Callback) {
        if (BuildConfig.DEBUG && this.callback != null) {
            logW("Callback is already registered")
            return
        }

        val currentState =
            indexingState?.let { State.Indexing(it) } ?: lastResponse?.let { State.Complete(it) }

        callback.onIndexerStateChanged(currentState)

        this.callback = callback
    }

    @Synchronized
    fun unregisterCallback(callback: Callback) {
        if (BuildConfig.DEBUG && this.callback !== callback) {
            logW("Given controller did not match current controller")
            return
        }

        this.callback = null
    }

    @Synchronized
    fun index(context: Context) {
        val generation = ++currentGeneration

        val notGranted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED

        if (notGranted) {
            emitCompletion(Response.NoPerms, generation)
            return
        }

        val response =
            try {
                val start = System.currentTimeMillis()
                val library = indexImpl(context, generation)
                if (library != null) {
                    logD(
                        "Music indexing completed successfully in " +
                            "${System.currentTimeMillis() - start}ms")
                    Response.Ok(library)
                } else {
                    logE("No music found")
                    Response.NoMusic
                }
            } catch (e: Exception) {
                logE("Music indexing failed.")
                logE(e.stackTraceToString())
                Response.Err(e)
            }

        emitCompletion(response, generation)
    }

    /**
     * Request that re-indexing should be done. This should be used by components that do not manage
     * the indexing process to re-index music.
     */
    @Synchronized
    fun requestReindex() {
        logD("Requesting reindex")
        controller?.onStartIndexing()
    }

    /**
     * "Cancel" the last job by making it unable to send further state updates. This should be
     * called if an object that called [index] is about to be destroyed and thus will have it's task
     * canceled, in which it would be useful for any ongoing loading process to not accidentally
     * corrupt the current state.
     */
    @Synchronized
    fun cancelLast() {
        logD("Cancelling last job")
        currentGeneration++
        emitIndexing(null, currentGeneration)
    }

    @Synchronized
    private fun emitIndexing(indexing: Indexing?, generation: Long) {
        if (currentGeneration != generation) {
            return
        }

        indexingState = indexing

        // If we have canceled the loading process, we want to revert to a previous completion
        // whenever possible to prevent state inconsistency.
        val state =
            indexingState?.let { State.Indexing(it) } ?: lastResponse?.let { State.Complete(it) }

        controller?.onIndexerStateChanged(state)
        callback?.onIndexerStateChanged(state)
    }

    @Synchronized
    private fun emitCompletion(response: Response, generation: Long) {
        if (currentGeneration != generation) {
            return
        }

        lastResponse = response
        indexingState = null

        val state = State.Complete(response)
        controller?.onIndexerStateChanged(state)
        callback?.onIndexerStateChanged(state)
    }

    /**
     * Run the proper music loading process. [generation] must be a truthful value of the generation
     * calling this function.
     */
    private fun indexImpl(context: Context, generation: Long): MusicStore.Library? {
        emitIndexing(Indexing.Indeterminate, generation)

        // Since we have different needs for each version, we determine a "Backend" to use
        // when loading music and then leverage that to create the initial song list.
        // This is technically dependency injection. Except it doesn't increase your compile
        // times by 3x. Isn't that nice.

        val mediaStoreBackend =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Api30MediaStoreBackend()
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Api29MediaStoreBackend()
                else -> Api21MediaStoreBackend()
            }

        val songs = buildSongs(context, mediaStoreBackend, generation)
        if (songs.isEmpty()) {
            return null
        }

        val buildStart = System.currentTimeMillis()

        val albums = buildAlbums(songs)
        val artists = buildArtists(albums)

        val genres = buildGenres(songs)

        // Sanity check: Ensure that all songs are linked up to albums/artists/genres.
        for (song in songs) {
            if (song._isMissingAlbum || song._isMissingArtist || song._isMissingGenre) {
                error(
                    "Found unlinked song: ${song.rawName} [" +
                        "missing album: ${song._isMissingAlbum} " +
                        "missing artist: ${song._isMissingArtist} " +
                        "missing genre: ${song._isMissingGenre}]")
            }
        }

        logD("Successfully built library in ${System.currentTimeMillis() - buildStart}ms")

        return MusicStore.Library(genres, artists, albums, songs)
    }

    /**
     * Does the initial query over the song database using [backend]. The songs returned by this
     * function are **not** well-formed. The companion [buildAlbums], [buildArtists], and
     * [buildGenres] functions must be called with the returned list so that all songs are properly
     * linked up.
     */
    private fun buildSongs(context: Context, backend: Backend, generation: Long): List<Song> {
        val start = System.currentTimeMillis()

        var songs =
            backend.query(context).use { cursor ->
                logD(
                    "Successfully queried media database " +
                        "in ${System.currentTimeMillis() - start}ms")

                backend.buildSongs(context, cursor) { indexing ->
                    emitIndexing(indexing, generation)
                }
            }

        // Deduplicate songs to prevent (most) deformed music clones
        songs =
            songs
                .distinctBy {
                    it.rawName to
                        it._albumName to
                        it._artistName to
                        it._albumArtistName to
                        it._genreName to
                        it.track to
                        it.disc to
                        it.durationMs
                }
                .toMutableList()

        // Ensure that sorting order is consistent so that grouping is also consistent.
        Sort(Sort.Mode.ByName, true).songsInPlace(songs)

        logD("Successfully built ${songs.size} songs in ${System.currentTimeMillis() - start}ms")

        return songs
    }

    /**
     * Group songs up into their respective albums. Instead of using the unreliable album or artist
     * databases, we instead group up songs by their *lowercase* artist and album name to create
     * albums. This serves two purposes:
     * 1. Sometimes artist names can be styled differently, e.g "Rammstein" vs. "RAMMSTEIN". This
     * makes sure both of those are resolved into a single artist called "Rammstein"
     * 2. Sometimes MediaStore will split album IDs up if the songs differ in format. This ensures
     * that all songs are unified under a single album.
     *
     * This does come with some costs, it's far slower than using the album ID itself, and it may
     * result in an unrelated album art being selected depending on the song chosen as the template,
     * but it seems to work pretty well.
     */
    private fun buildAlbums(songs: List<Song>): List<Album> {
        val albums = mutableListOf<Album>()
        val songsByAlbum = songs.groupBy { it._albumGroupingId }

        for (entry in songsByAlbum) {
            val albumSongs = entry.value

            // Use the song with the latest year as our metadata song.
            // This allows us to replicate the LAST_YEAR field, which is useful as it means that
            // weird years like "0" wont show up if there are alternatives.
            val templateSong =
                albumSongs.maxWith(compareBy(Sort.Mode.NULLABLE_INT_COMPARATOR) { it._year })

            albums.add(
                Album(
                    rawName = templateSong._albumName,
                    year = templateSong._year,
                    albumCoverUri = templateSong._albumCoverUri,
                    songs = entry.value,
                    _artistGroupingName = templateSong._artistGroupingName))
        }

        logD("Successfully built ${albums.size} albums")

        return albums
    }

    /**
     * Group up albums into artists. This also requires a de-duplication step due to some edge cases
     * where [buildAlbums] could not detect duplicates.
     */
    private fun buildArtists(albums: List<Album>): List<Artist> {
        val artists = mutableListOf<Artist>()
        val albumsByArtist = albums.groupBy { it._artistGroupingId }

        for (entry in albumsByArtist) {
            // The first album will suffice for template metadata.
            val templateAlbum = entry.value[0]
            artists.add(Artist(rawName = templateAlbum._artistGroupingName, albums = entry.value))
        }

        logD("Successfully built ${artists.size} artists")

        return artists
    }

    /**
     * Group up songs into genres. This is a relatively simple step compared to the other library
     * steps, as there is no demand to deduplicate genres by a lowercase name.
     */
    private fun buildGenres(songs: List<Song>): List<Genre> {
        val genres = mutableListOf<Genre>()
        val songsByGenre = songs.groupBy { it._genreGroupingId }

        for (entry in songsByGenre) {
            // The first song fill suffice for template metadata.
            val templateSong = entry.value[0]
            genres.add(Genre(rawName = templateSong._genreName, songs = entry.value))
        }

        logD("Successfully built ${genres.size} genres")

        return genres
    }

    /** Represents the current indexer state. */
    sealed class State {
        data class Indexing(val indexing: Indexer.Indexing) : State()
        data class Complete(val response: Response) : State()
    }

    sealed class Indexing {
        object Indeterminate : Indexing()
        class Songs(val current: Int, val total: Int) : Indexing()
    }

    /** Represents the possible outcomes of a loading process. */
    sealed class Response {
        data class Ok(val library: MusicStore.Library) : Response()
        data class Err(val throwable: Throwable) : Response()
        object NoMusic : Response()
        object NoPerms : Response()
    }

    /**
     * A callback to use when the indexing state changes.
     *
     * This callback is low-level and not guaranteed to be single-thread. For that,
     * [MusicStore.Callback] is recommended instead.
     */
    interface Callback {
        /**
         * Called when the current state of the Indexer changed.
         *
         * Notes:
         * - Null means that no loading is going on, but no load has completed either.
         * - [State.Complete] may represent a previous load, if the current loading process was
         * canceled for one reason or another.
         */
        fun onIndexerStateChanged(state: State?)
    }

    interface Controller : Callback {
        fun onStartIndexing()
    }

    /** Represents a backend that metadata can be extracted from. */
    interface Backend {
        /** Query the media database for a basic cursor. */
        fun query(context: Context): Cursor

        /** Create a list of songs from the [Cursor] queried in [query]. */
        fun buildSongs(
            context: Context,
            cursor: Cursor,
            emitIndexing: (Indexing) -> Unit
        ): List<Song>
    }

    companion object {
        @Volatile private var INSTANCE: Indexer? = null

        /** Get the process-level instance of [Indexer]. */
        fun getInstance(): Indexer {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = Indexer()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

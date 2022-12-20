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
 
package org.oxycblt.auxio.music.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.music.extractor.Api21MediaStoreExtractor
import org.oxycblt.auxio.music.extractor.Api29MediaStoreExtractor
import org.oxycblt.auxio.music.extractor.Api30MediaStoreExtractor
import org.oxycblt.auxio.music.extractor.CacheExtractor
import org.oxycblt.auxio.music.extractor.MetadataExtractor
import org.oxycblt.auxio.settings.Settings
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
 * 1. Creating the chain of extractors to extract metadata with
 * 2. Running the chain process
 * 3. Using the songs to build the library, which primarily involves linking up all data objects
 * with their corresponding parents/children.
 *
 * This class in particular handles 3 primarily. For the code that handles 1 and 2, see the layer
 * implementations.
 *
 * This class also fulfills the role of maintaining the current music loading state, which seems
 * like a job for [MusicStore] but in practice is only really leveraged by the components that
 * directly work with music loading, making such redundant.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class Indexer {
    private var lastResponse: Response? = null
    private var indexingState: Indexing? = null

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

        this.controller = controller
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

    /**
     * Start the indexing process. This should be done by [Controller] in a background thread. When
     * complete, a new completion state will be pushed to each callback.
     * @param withCache Whether to use the cache when loading.
     */
    suspend fun index(context: Context, withCache: Boolean) {
        val notGranted =
            ContextCompat.checkSelfPermission(context, PERMISSION_READ_AUDIO) ==
                PackageManager.PERMISSION_DENIED

        if (notGranted) {
            emitCompletion(Response.NoPerms)
            return
        }

        val response =
            try {
                val start = System.currentTimeMillis()
                val library = indexImpl(context, withCache)
                if (library != null) {
                    logD(
                        "Music indexing completed successfully in " +
                            "${System.currentTimeMillis() - start}ms")
                    Response.Ok(library)
                } else {
                    logE("No music found")
                    Response.NoMusic
                }
            } catch (e: CancellationException) {
                // Got cancelled, propagate upwards
                logD("Loading routine was cancelled")
                throw e
            } catch (e: Exception) {
                logE("Music indexing failed")
                logE(e.stackTraceToString())
                Response.Err(e)
            }

        emitCompletion(response)
    }

    /**
     * Request that re-indexing should be done. This should be used by components that do not manage
     * the indexing process to re-index music.
     * @param withCache Whether to use the cache when loading music.
     */
    @Synchronized
    fun requestReindex(withCache: Boolean) {
        logD("Requesting reindex")
        controller?.onStartIndexing(withCache)
    }

    /**
     * "Cancel" the last job by making it unable to send further state updates. This will cause the
     * worker operating the job for that specific handle to cancel as soon as it tries to send a
     * state update.
     */
    @Synchronized
    fun cancelLast() {
        logD("Cancelling last job")
        emitIndexing(null)
    }

    /** Run the proper music loading process. */
    private suspend fun indexImpl(context: Context, withCache: Boolean): MusicStore.Library? {
        // Create the chain of extractors. Each extractor builds on the previous and
        // enables version-specific features in order to create the best possible music
        // experience. This is technically dependency injection. Except it doesn't increase
        // your compile times by 3x. Isn't that nice.

        val cacheDatabase = CacheExtractor(context, !withCache)

        val mediaStoreExtractor =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                    Api30MediaStoreExtractor(context, cacheDatabase)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                    Api29MediaStoreExtractor(context, cacheDatabase)
                else -> Api21MediaStoreExtractor(context, cacheDatabase)
            }

        val metadataExtractor = MetadataExtractor(context, mediaStoreExtractor)

        val songs = buildSongs(metadataExtractor, Settings(context))
        if (songs.isEmpty()) {
            return null
        }

        val buildStart = System.currentTimeMillis()

        val albums = buildAlbums(songs)
        val artists = buildArtists(songs, albums)
        val genres = buildGenres(songs)

        // Make sure we finalize all the items now that they are fully built.
        for (song in songs) {
            song._finalize()
        }

        for (album in albums) {
            album._finalize()
        }

        for (artist in artists) {
            artist._finalize()
        }

        for (genre in genres) {
            genre._finalize()
        }

        logD("Successfully built library in ${System.currentTimeMillis() - buildStart}ms")

        return MusicStore.Library(genres, artists, albums, songs)
    }

    /**
     * Does the initial query over the song database using [metadataExtractor]. The songs returned
     * by this function are **not** well-formed. The companion [buildAlbums], [buildArtists], and
     * [buildGenres] functions must be called with the returned list so that all songs are properly
     * linked up.
     */
    private suspend fun buildSongs(
        metadataExtractor: MetadataExtractor,
        settings: Settings
    ): List<Song> {
        logD("Starting indexing process")

        val start = System.currentTimeMillis()
        emitIndexing(Indexing.Indeterminate)

        // Initialize the extractor chain. This also nets us the projected total
        // that we can show when loading.
        val total = metadataExtractor.init()
        yield()

        // Note: We use a set here so we can eliminate effective duplicates of
        // songs (by UID) and sort to achieve consistent orderings
        val songs = mutableSetOf<Song>()
        val rawSongs = mutableListOf<Song.Raw>()

        metadataExtractor.parse { rawSong ->
            songs.add(Song(rawSong, settings))
            rawSongs.add(rawSong)

            // Check if we got cancelled after every song addition.
            yield()
            emitIndexing(Indexing.Songs(songs.size, total))
        }

        emitIndexing(Indexing.Indeterminate)

        metadataExtractor.finalize(rawSongs)

        logD("Successfully built ${songs.size} songs in ${System.currentTimeMillis() - start}ms")

        // Ensure that sorting order is consistent so that grouping is also consistent.
        return Sort(Sort.Mode.ByName, true).songs(songs)
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
     * result in an unrelated album cover being selected depending on the song chosen as the
     * template, but it seems to work pretty well.
     */
    private fun buildAlbums(songs: List<Song>): List<Album> {
        val albums = mutableListOf<Album>()
        val songsByAlbum = songs.groupBy { it._rawAlbum }

        for (entry in songsByAlbum) {
            albums.add(Album(entry.key, entry.value))
        }

        logD("Successfully built ${albums.size} albums")

        return albums
    }

    /**
     * Group up songs AND albums into artists. This process seems weird (because it is), but the
     * purpose is that the actual artist information of albums and songs often differs, and so they
     * are linked in different ways.
     */
    private fun buildArtists(songs: List<Song>, albums: List<Album>): List<Artist> {
        val musicByArtist = mutableMapOf<Artist.Raw, MutableList<Music>>()
        for (song in songs) {
            for (rawArtist in song._rawArtists) {
                musicByArtist.getOrPut(rawArtist) { mutableListOf() }.add(song)
            }
        }

        for (album in albums) {
            for (rawArtist in album._rawArtists) {
                musicByArtist.getOrPut(rawArtist) { mutableListOf() }.add(album)
            }
        }

        val artists = musicByArtist.map { Artist(it.key, it.value) }

        logD("Successfully built ${artists.size} artists")

        return artists
    }

    /**
     * Group up songs into genres. This is a relatively simple step compared to the other library
     * steps, as there is no demand to deduplicate genres by a lowercase name.
     */
    private fun buildGenres(songs: List<Song>): List<Genre> {
        val genres = mutableListOf<Genre>()
        val songsByGenre = mutableMapOf<Genre.Raw, MutableList<Song>>()
        for (song in songs) {
            for (rawGenre in song._rawGenres) {
                songsByGenre.getOrPut(rawGenre) { mutableListOf() }.add(song)
            }
        }

        for (entry in songsByGenre) {
            genres.add(Genre(entry.key, entry.value))
        }

        logD("Successfully built ${genres.size} genres")

        return genres
    }

    @Synchronized
    private fun emitIndexing(indexing: Indexing?) {
        indexingState = indexing

        // If we have canceled the loading process, we want to revert to a previous completion
        // whenever possible to prevent state inconsistency.
        val state =
            indexingState?.let { State.Indexing(it) } ?: lastResponse?.let { State.Complete(it) }

        controller?.onIndexerStateChanged(state)
        callback?.onIndexerStateChanged(state)
    }

    private suspend fun emitCompletion(response: Response) {
        yield()

        // Swap to the Main thread so that downstream callbacks don't crash from being on
        // a background thread. Does not occur in emitIndexing due to efficiency reasons.
        withContext(Dispatchers.Main) {
            synchronized(this) {
                // Do not check for redundancy here, as we actually need to notify a switch
                // from Indexing -> Complete and not Indexing -> Indexing or Complete -> Complete.
                lastResponse = response
                indexingState = null

                val state = State.Complete(response)

                controller?.onIndexerStateChanged(state)
                callback?.onIndexerStateChanged(state)
            }
        }
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
        fun onStartIndexing(withCache: Boolean)
    }

    companion object {
        @Volatile private var INSTANCE: Indexer? = null

        val PERMISSION_READ_AUDIO =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // READ_EXTERNAL_STORAGE was superseded by READ_MEDIA_AUDIO in Android 13
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

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

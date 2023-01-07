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
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.extractor.*
import org.oxycblt.auxio.music.library.Library
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW

/**
 * Core music loading state class.
 *
 * This class provides low-level access into the exact state of the music loading process. **This
 * class should not be used in most cases.** It is highly volatile and provides far more information
 * than is usually needed. Use [MusicStore] instead if you do not need to work with the exact music
 * loading state.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class Indexer private constructor() {
    @Volatile private var lastResponse: Result<Library>? = null
    @Volatile private var indexingState: Indexing? = null
    @Volatile private var controller: Controller? = null
    @Volatile private var listener: Listener? = null

    /** Whether music loading is occurring or not. */
    val isIndexing: Boolean
        get() = indexingState != null

    /**
     * Whether this instance has not completed a loading process and is not currently loading music.
     * This often occurs early in an app's lifecycle, and consumers should try to avoid showing any
     * state when this flag is true.
     */
    val isIndeterminate: Boolean
        get() = lastResponse == null && indexingState == null

    /**
     * Register a [Controller] for this instance. This instance will handle any commands to start
     * the music loading process. There can be only one [Controller] at a time. Will invoke all
     * [Listener] methods to initialize the instance with the current state.
     * @param controller The [Controller] to register. Will do nothing if already registered.
     */
    @Synchronized
    fun registerController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller != null) {
            logW("Controller is already registered")
            return
        }

        // Initialize the controller with the current state.
        val currentState =
            indexingState?.let { State.Indexing(it) } ?: lastResponse?.let { State.Complete(it) }
        controller.onIndexerStateChanged(currentState)
        this.controller = controller
    }

    /**
     * Unregister the [Controller] from this instance, prevent it from recieving any further
     * commands.
     * @param controller The [Controller] to unregister. Must be the current [Controller]. Does
     * nothing if invoked by another [Controller] implementation.
     */
    @Synchronized
    fun unregisterController(controller: Controller) {
        if (BuildConfig.DEBUG && this.controller !== controller) {
            logW("Given controller did not match current controller")
            return
        }

        this.controller = null
    }

    /**
     * Register the [Listener] for this instance. This can be used to receive rapid-fire updates to
     * the current music loading state. There can be only one [Listener] at a time. Will invoke all
     * [Listener] methods to initialize the instance with the current state.
     * @param listener The [Listener] to add.
     */
    @Synchronized
    fun registerListener(listener: Listener) {
        if (BuildConfig.DEBUG && this.listener != null) {
            logW("Listener is already registered")
            return
        }

        // Initialize the listener with the current state.
        val currentState =
            indexingState?.let { State.Indexing(it) } ?: lastResponse?.let { State.Complete(it) }
        listener.onIndexerStateChanged(currentState)
        this.listener = listener
    }

    /**
     * Unregister a [Listener] from this instance, preventing it from recieving any further updates.
     * @param listener The [Listener] to unregister. Must be the current [Listener]. Does nothing if
     * invoked by another [Listener] implementation.
     * @see Listener
     */
    @Synchronized
    fun unregisterListener(listener: Listener) {
        if (BuildConfig.DEBUG && this.listener !== listener) {
            logW("Given controller did not match current controller")
            return
        }

        this.listener = null
    }

    /**
     * Start the indexing process. This should be done from in the background from [Controller]'s
     * context after a command has been received to start the process.
     * @param context [Context] required to load music.
     * @param withCache Whether to use the cache or not when loading. If false, the cache will still
     * be written, but no cache entries will be loaded into the new library.
     */
    suspend fun index(context: Context, withCache: Boolean) {
        val result =
            try {
                val start = System.currentTimeMillis()
                val library = indexImpl(context, withCache)
                logD(
                    "Music indexing completed successfully in " +
                        "${System.currentTimeMillis() - start}ms")
                Result.success(library)
            } catch (e: CancellationException) {
                // Got cancelled, propagate upwards to top-level co-routine.
                logD("Loading routine was cancelled")
                throw e
            } catch (e: Exception) {
                // Music loading process failed due to something we have not handled.
                logE("Music indexing failed")
                logE(e.stackTraceToString())
                Result.failure(e)
            }
        emitCompletion(result)
    }

    /**
     * Request that the music library should be reloaded. This should be used by components that do
     * not manage the indexing process in order to signal that the [Controller] should call [index]
     * eventually.
     * @param withCache Whether to use the cache when loading music. Does nothing if there is no
     * [Controller].
     */
    @Synchronized
    fun requestReindex(withCache: Boolean) {
        logD("Requesting reindex")
        controller?.onStartIndexing(withCache)
    }

    /**
     * Reset the current loading state to signal that the instance is not loading. This should be
     * called by [Controller] after it's indexing co-routine was cancelled.
     */
    @Synchronized
    fun reset() {
        logD("Cancelling last job")
        emitIndexing(null)
    }

    /**
     * Internal implementation of the music loading process.
     * @param context [Context] required to load music.
     * @param withCache Whether to use the cache or not when loading. If false, the cache will still
     * be written, but no cache entries will be loaded into the new library.
     * @return A newly-loaded [Library].
     * @throws NoPermissionException If [PERMISSION_READ_AUDIO] was not granted.
     * @throws NoMusicException If no music was found on the device.
     */
    private suspend fun indexImpl(context: Context, withCache: Boolean): Library {
        if (ContextCompat.checkSelfPermission(context, PERMISSION_READ_AUDIO) ==
            PackageManager.PERMISSION_DENIED) {
            // No permissions, signal that we can't do anything.
            throw NoPermissionException()
        }

        // Create the chain of extractors. Each extractor builds on the previous and
        // enables version-specific features in order to create the best possible music
        // experience.
        val cacheDatabase =
            if (withCache) {
                ReadWriteCacheExtractor(context)
            } else {
                WriteOnlyCacheExtractor(context)
            }
        val mediaStoreExtractor =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                    Api30MediaStoreExtractor(context, cacheDatabase)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                    Api29MediaStoreExtractor(context, cacheDatabase)
                else -> Api21MediaStoreExtractor(context, cacheDatabase)
            }
        val metadataExtractor = MetadataExtractor(context, mediaStoreExtractor)
        val rawSongs = loadRawSongs(metadataExtractor).ifEmpty { throw NoMusicException() }
        // Build the rest of the music library from the song list. This is much more powerful
        // and reliable compared to using MediaStore to obtain grouping information.
        val buildStart = System.currentTimeMillis()
        val library = Library(rawSongs, MusicSettings.from(context))
        logD("Successfully built library in ${System.currentTimeMillis() - buildStart}ms")
        return library
    }

    /**
     * Load a list of [Song]s from the device.
     * @param metadataExtractor The completed [MetadataExtractor] instance to use to load [Song.Raw]
     * instances.
     * @return A possibly empty list of [Song]s. These [Song]s will be incomplete and must be linked
     * with parent [Album], [Artist], and [Genre] items in order to be usable.
     */
    private suspend fun loadRawSongs(metadataExtractor: MetadataExtractor): List<Song.Raw> {
        logD("Starting indexing process")
        val start = System.currentTimeMillis()
        // Start initializing the extractors. Use an indeterminate state, as there is no ETA on
        // how long a media database query will take.
        emitIndexing(Indexing.Indeterminate)
        val total = metadataExtractor.init()
        yield()

        // Note: We use a set here so we can eliminate song duplicates.
        val rawSongs = mutableListOf<Song.Raw>()
        metadataExtractor.extract().collect { rawSong ->
            rawSongs.add(rawSong)
            // Now we can signal a defined progress by showing how many songs we have
            // loaded, and the projected amount of songs we found in the library
            // (obtained by the extractors)
            yield()
            emitIndexing(Indexing.Songs(rawSongs.size, total))
        }

        // Finalize the extractors with the songs we have now loaded. There is no ETA
        // on this process, so go back to an indeterminate state.
        emitIndexing(Indexing.Indeterminate)
        metadataExtractor.finalize(rawSongs)
        logD(
            "Successfully loaded ${rawSongs.size} raw songs in ${System.currentTimeMillis() - start}ms")
        return rawSongs
    }

    /**
     * Emit a new [State.Indexing] state. This can be used to signal the current state of the music
     * loading process to external code. Assumes that the callee has already checked if they have
     * not been canceled and thus have the ability to emit a new state.
     * @param indexing The new [Indexing] state to emit, or null if no loading process is occurring.
     */
    @Synchronized
    private fun emitIndexing(indexing: Indexing?) {
        indexingState = indexing
        // If we have canceled the loading process, we want to revert to a previous completion
        // whenever possible to prevent state inconsistency.
        val state =
            indexingState?.let { State.Indexing(it) } ?: lastResponse?.let { State.Complete(it) }
        controller?.onIndexerStateChanged(state)
        listener?.onIndexerStateChanged(state)
    }

    /**
     * Emit a new [State.Complete] state. This can be used to signal the completion of the music
     * loading process to external code. Will check if the callee has not been canceled and thus has
     * the ability to emit a new state
     * @param result The new [Result] to emit, representing the outcome of the music loading
     * process.
     */
    private suspend fun emitCompletion(result: Result<Library>) {
        yield()
        // Swap to the Main thread so that downstream callbacks don't crash from being on
        // a background thread. Does not occur in emitIndexing due to efficiency reasons.
        withContext(Dispatchers.Main) {
            synchronized(this) {
                // Do not check for redundancy here, as we actually need to notify a switch
                // from Indexing -> Complete and not Indexing -> Indexing or Complete -> Complete.
                lastResponse = result
                indexingState = null
                // Signal that the music loading process has been completed.
                val state = State.Complete(result)
                controller?.onIndexerStateChanged(state)
                listener?.onIndexerStateChanged(state)
            }
        }
    }

    /** Represents the current state of [Indexer]. */
    sealed class State {
        /**
         * Music loading is ongoing.
         * @param indexing The current music loading progress..
         * @see Indexer.Indexing
         */
        data class Indexing(val indexing: Indexer.Indexing) : State()

        /**
         * Music loading has completed.
         * @param result The outcome of the music loading process.
         */
        data class Complete(val result: Result<Library>) : State()
    }

    /**
     * Represents the current progress of the music loader. Usually encapsulated in a [State].
     * @see State.Indexing
     */
    sealed class Indexing {
        /**
         * Music loading is occurring, but no definite estimate can be put on the current progress.
         */
        object Indeterminate : Indexing()

        /**
         * Music loading has a definite progress.
         * @param current The current amount of songs that have been loaded.
         * @param total The projected total amount of songs that will be loaded.
         */
        class Songs(val current: Int, val total: Int) : Indexing()
    }

    /** Thrown when the required permissions to load the music library have not been granted yet. */
    class NoPermissionException : Exception() {
        override val message: String
            get() = "Not granted permissions to load music library"
    }

    /** Thrown when no music was found on the device. */
    class NoMusicException : Exception() {
        override val message: String
            get() = "Unable to find any music"
    }

    /**
     * A listener for rapid-fire changes in the music loading state.
     *
     * This is only useful for code that absolutely must show the current loading process.
     * Otherwise, [MusicStore.Listener] is highly recommended due to it's updates only consisting of
     * the [Library].
     */
    interface Listener {
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

    /**
     * Context that runs the music loading process. Implementations should be capable of running the
     * background for long periods of time without android killing the process.
     */
    interface Controller : Listener {
        /**
         * Called when a new music loading process was requested. Implementations should forward
         * this to [index].
         * @param withCache Whether to use the cache or not when loading. If false, the cache should
         * still be written, but no cache entries will be loaded into the new library.
         * @see index
         */
        fun onStartIndexing(withCache: Boolean)
    }

    companion object {
        @Volatile private var INSTANCE: Indexer? = null

        /**
         * A version-compatible identifier for the read external storage permission required by the
         * system to load audio.
         */
        val PERMISSION_READ_AUDIO =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // READ_EXTERNAL_STORAGE was superseded by READ_MEDIA_AUDIO in Android 13
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        /**
         * Get a singleton instance.
         * @return The (possibly newly-created) singleton instance.
         */
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

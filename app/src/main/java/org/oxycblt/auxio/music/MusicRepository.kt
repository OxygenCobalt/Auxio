/*
 * Copyright (c) 2023 Auxio Project
 * MusicRepository.kt is part of Auxio.
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
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.util.LinkedList
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.cache.CacheRepository
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.fs.MediaStoreExtractor
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.metadata.Separators
import org.oxycblt.auxio.music.metadata.TagExtractor
import org.oxycblt.auxio.music.user.MutableUserLibrary
import org.oxycblt.auxio.music.user.UserLibrary
import org.oxycblt.auxio.util.DEFAULT_TIMEOUT
import org.oxycblt.auxio.util.forEachWithTimeout
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW

/**
 * Primary manager of music information and loading.
 *
 * Music information is loaded in-memory by this repository using an [IndexingWorker]. Changes in
 * music (loading) can be reacted to with [UpdateListener] and [IndexingListener].
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Switch listeners to set when you can confirm there are no order-dependent listener
 *   configurations
 */
interface MusicRepository {
    /** The current music information found on the device. */
    val deviceLibrary: DeviceLibrary?
    /** The current user-defined music information. */
    val userLibrary: UserLibrary?
    /** The current state of music loading. Null if no load has occurred yet. */
    val indexingState: IndexingState?

    /**
     * Add an [UpdateListener] to receive updates from this instance.
     *
     * @param listener The [UpdateListener] to add.
     */
    fun addUpdateListener(listener: UpdateListener)

    /**
     * Remove an [UpdateListener] such that it does not receive any further updates from this
     * instance.
     *
     * @param listener The [UpdateListener] to remove.
     */
    fun removeUpdateListener(listener: UpdateListener)

    /**
     * Add an [IndexingListener] to receive updates from this instance.
     *
     * @param listener The [UpdateListener] to add.
     */
    fun addIndexingListener(listener: IndexingListener)

    /**
     * Remove an [IndexingListener] such that it does not receive any further updates from this
     * instance.
     *
     * @param listener The [IndexingListener] to remove.
     */
    fun removeIndexingListener(listener: IndexingListener)

    /**
     * Register an [IndexingWorker] to handle loading operations. Will do nothing if one is already
     * registered.
     *
     * @param worker The [IndexingWorker] to register.
     */
    fun registerWorker(worker: IndexingWorker)

    /**
     * Unregister an [IndexingWorker] and drop any work currently being done by it. Does nothing if
     * given [IndexingWorker] is not the currently registered instance.
     *
     * @param worker The [IndexingWorker] to unregister.
     */
    fun unregisterWorker(worker: IndexingWorker)

    /**
     * Generically search for the [Music] associated with the given [Music.UID]. Note that this
     * method is much slower that type-specific find implementations, so this should only be used if
     * the type of music being searched for is entirely unknown.
     *
     * @param uid The [Music.UID] to search for.
     * @return The expected [Music] information, or null if it could not be found.
     */
    fun find(uid: Music.UID): Music?

    /**
     * Create a new [Playlist] of the given [Song]s.
     *
     * @param name The name of the new [Playlist].
     * @param songs The songs to populate the new [Playlist] with.
     */
    suspend fun createPlaylist(name: String, songs: List<Song>)

    /**
     * Rename a [Playlist].
     *
     * @param playlist The [Playlist] to rename.
     * @param name The name of the new [Playlist].
     */
    suspend fun renamePlaylist(playlist: Playlist, name: String)

    /**
     * Delete a [Playlist].
     *
     * @param playlist The playlist to delete.
     */
    suspend fun deletePlaylist(playlist: Playlist)

    /**
     * Add the given [Song]s to a [Playlist].
     *
     * @param songs The [Song]s to add to the [Playlist].
     * @param playlist The [Playlist] to add to.
     */
    suspend fun addToPlaylist(songs: List<Song>, playlist: Playlist)

    /**
     * Update the [Song]s of a [Playlist].
     *
     * @param playlist The [Playlist] to update.
     * @param songs The new [Song]s to be contained in the [Playlist].
     */
    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>)

    /**
     * Request that a music loading operation is started by the current [IndexingWorker]. Does
     * nothing if one is not available.
     *
     * @param withCache Whether to load with the music cache or not.
     */
    fun requestIndex(withCache: Boolean)

    /**
     * Load the music library. Any prior loads will be canceled.
     *
     * @param worker The [IndexingWorker] to perform the work with.
     * @param withCache Whether to load with the music cache or not.
     * @return The top-level music loading [Job] started.
     */
    fun index(worker: IndexingWorker, withCache: Boolean): Job

    /** A listener for changes to the stored music information. */
    interface UpdateListener {
        /**
         * Called when a change to the stored music information occurs.
         *
         * @param changes The [Changes] that have occurred.
         */
        fun onMusicChanges(changes: Changes)
    }

    /**
     * Flags indicating which kinds of music information changed.
     *
     * @param deviceLibrary Whether the current [DeviceLibrary] has changed.
     * @param userLibrary Whether the current [Playlist]s have changed.
     */
    data class Changes(val deviceLibrary: Boolean, val userLibrary: Boolean)

    /** A listener for events in the music loading process. */
    interface IndexingListener {
        /** Called when the music loading state changed. */
        fun onIndexingStateChanged()
    }

    /** A persistent worker that can load music in the background. */
    interface IndexingWorker {
        /** A [Context] required to read device storage */
        val workerContext: Context

        /** The [CoroutineScope] to perform coroutine music loading work on. */
        val scope: CoroutineScope

        /**
         * Request that the music loading process ([index]) should be started. Any prior loads
         * should be canceled.
         *
         * @param withCache Whether to use the music cache when loading.
         */
        fun requestIndex(withCache: Boolean)
    }
}

class MusicRepositoryImpl
@Inject
constructor(
    private val cacheRepository: CacheRepository,
    private val mediaStoreExtractor: MediaStoreExtractor,
    private val tagExtractor: TagExtractor,
    private val deviceLibraryFactory: DeviceLibrary.Factory,
    private val userLibraryFactory: UserLibrary.Factory,
    private val musicSettings: MusicSettings
) : MusicRepository {
    private val updateListeners = mutableListOf<MusicRepository.UpdateListener>()
    private val indexingListeners = mutableListOf<MusicRepository.IndexingListener>()
    @Volatile private var indexingWorker: MusicRepository.IndexingWorker? = null

    @Volatile override var deviceLibrary: DeviceLibrary? = null
    @Volatile override var userLibrary: MutableUserLibrary? = null
    @Volatile private var previousCompletedState: IndexingState.Completed? = null
    @Volatile private var currentIndexingState: IndexingState? = null
    override val indexingState: IndexingState?
        get() = currentIndexingState ?: previousCompletedState

    @Synchronized
    override fun addUpdateListener(listener: MusicRepository.UpdateListener) {
        logD("Adding $listener to update listeners")
        updateListeners.add(listener)
        listener.onMusicChanges(MusicRepository.Changes(deviceLibrary = true, userLibrary = true))
    }

    @Synchronized
    override fun removeUpdateListener(listener: MusicRepository.UpdateListener) {
        logD("Removing $listener to update listeners")
        if (!updateListeners.remove(listener)) {
            logW("Update listener $listener was not added prior, cannot remove")
        }
    }

    @Synchronized
    override fun addIndexingListener(listener: MusicRepository.IndexingListener) {
        logD("Adding $listener to indexing listeners")
        indexingListeners.add(listener)
        listener.onIndexingStateChanged()
    }

    @Synchronized
    override fun removeIndexingListener(listener: MusicRepository.IndexingListener) {
        logD("Removing $listener from indexing listeners")
        if (!indexingListeners.remove(listener)) {
            logW("Indexing listener $listener was not added prior, cannot remove")
        }
    }

    @Synchronized
    override fun registerWorker(worker: MusicRepository.IndexingWorker) {
        if (indexingWorker != null) {
            logW("Worker is already registered")
            return
        }
        logD("Registering worker $worker")
        indexingWorker = worker
    }

    @Synchronized
    override fun unregisterWorker(worker: MusicRepository.IndexingWorker) {
        if (indexingWorker !== worker) {
            logW("Given worker did not match current worker")
            return
        }
        logD("Unregistering worker $worker")
        indexingWorker = null
        currentIndexingState = null
    }

    @Synchronized
    override fun find(uid: Music.UID) =
        (deviceLibrary?.run { findSong(uid) ?: findAlbum(uid) ?: findArtist(uid) ?: findGenre(uid) }
            ?: userLibrary?.findPlaylist(uid))

    override suspend fun createPlaylist(name: String, songs: List<Song>) {
        val userLibrary = synchronized(this) { userLibrary ?: return }
        logD("Creating playlist $name with ${songs.size} songs")
        userLibrary.createPlaylist(name, songs)
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun renamePlaylist(playlist: Playlist, name: String) {
        val userLibrary = synchronized(this) { userLibrary ?: return }
        logD("Renaming $playlist to $name")
        userLibrary.renamePlaylist(playlist, name)
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val userLibrary = synchronized(this) { userLibrary ?: return }
        logD("Deleting $playlist")
        userLibrary.deletePlaylist(playlist)
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun addToPlaylist(songs: List<Song>, playlist: Playlist) {
        val userLibrary = synchronized(this) { userLibrary ?: return }
        logD("Adding ${songs.size} songs to $playlist")
        userLibrary.addToPlaylist(playlist, songs)
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>) {
        val userLibrary = synchronized(this) { userLibrary ?: return }
        logD("Rewriting $playlist with ${songs.size} songs")
        userLibrary.rewritePlaylist(playlist, songs)
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    @Synchronized
    override fun requestIndex(withCache: Boolean) {
        logD("Requesting index operation [cache=$withCache]")
        indexingWorker?.requestIndex(withCache)
    }

    override fun index(worker: MusicRepository.IndexingWorker, withCache: Boolean) =
        worker.scope.launch { indexWrapper(worker.workerContext, this, withCache) }

    private suspend fun indexWrapper(context: Context, scope: CoroutineScope, withCache: Boolean) {
        try {
            indexImpl(context, scope, withCache)
        } catch (e: CancellationException) {
            // Got cancelled, propagate upwards to top-level co-routine.
            logD("Loading routine was cancelled")
            throw e
        } catch (e: Exception) {
            // Music loading process failed due to something we have not handled.
            // TODO: Still want to display this error eventually
            logE("Music indexing failed")
            logE(e.stackTraceToString())
            emitIndexingCompletion(e)
        }
    }

    private suspend fun indexImpl(context: Context, scope: CoroutineScope, withCache: Boolean) {
        // TODO: Find a way to break up this monster of a method, preferably as another class.

        val start = System.currentTimeMillis()
        // Make sure we have permissions before going forward. Theoretically this would be better
        // done at the UI level, but that intertwines logic and display too much.
        if (ContextCompat.checkSelfPermission(context, PERMISSION_READ_AUDIO) ==
            PackageManager.PERMISSION_DENIED) {
            logE("Permissions were not granted")
            throw NoAudioPermissionException()
        }

        // Obtain configuration information
        val constraints =
            MediaStoreExtractor.Constraints(musicSettings.excludeNonMusic, musicSettings.musicDirs)
        val separators = Separators.from(musicSettings.separators)
        val nameFactory =
            if (musicSettings.intelligentSorting) {
                Name.Known.IntelligentFactory
            } else {
                Name.Known.SimpleFactory
            }

        // Begin with querying MediaStore and the music cache. The former is needed for Auxio
        // to figure out what songs are (probably) on the device, and the latter will be needed
        // for discovery (described later). These have no shared state, so they are done in
        // parallel.
        logD("Starting MediaStore query")
        emitIndexingProgress(IndexingProgress.Indeterminate)

        val mediaStoreQueryJob =
            scope.async {
                val query =
                    try {
                        mediaStoreExtractor.query(constraints)
                    } catch (e: Exception) {
                        // Normally, errors in an async call immediately bubble up to the Looper
                        // and crash the app. Thus, we have to wrap any error into a Result
                        // and then manually forward it to the try block that indexImpl is
                        // called from.
                        return@async Result.failure(e)
                    }
                Result.success(query)
            }
        // Since this main thread is a co-routine, we can do operations in parallel in a way
        // identical to calling async.
        val cache =
            if (withCache) {
                logD("Reading cache")
                cacheRepository.readCache()
            } else {
                null
            }
        logD("Awaiting MediaStore query")
        val query = mediaStoreQueryJob.await().getOrThrow()

        // We now have all the information required to start the "discovery" process. This
        // is the point at which Auxio starts scanning each file given from MediaStore and
        // transforming it into a music library. MediaStore normally
        logD("Starting discovery")
        val incompleteSongs = Channel<RawSong>(Channel.UNLIMITED) // Not fully populated w/metadata
        val completeSongs = Channel<RawSong>(Channel.UNLIMITED) // Populated with quality metadata
        val processedSongs = Channel<RawSong>(Channel.UNLIMITED) // Transformed into SongImpl

        // MediaStoreExtractor discovers all music on the device, and forwards them to either
        // DeviceLibrary if cached metadata exists for it, or TagExtractor if cached metadata
        // does not exist. In the latter situation, it also applies it's own (inferior) metadata.
        logD("Starting MediaStore discovery")
        val mediaStoreJob =
            scope.async {
                try {
                    mediaStoreExtractor.consume(query, cache, incompleteSongs, completeSongs)
                } catch (e: Exception) {
                    // To prevent a deadlock, we want to close the channel with an exception
                    // to cascade to and cancel all other routines before finally bubbling up
                    // to the main extractor loop.
                    logE("MediaStore extraction failed: $e")
                    incompleteSongs.close(
                        Exception("MediaStore extraction failed: ${e.stackTraceToString()}"))
                    return@async
                }
                incompleteSongs.close()
            }

        // TagExtractor takes the incomplete songs from MediaStoreExtractor, parses up-to-date
        // metadata for them, and then forwards it to DeviceLibrary.
        logD("Starting tag extraction")
        val tagJob =
            scope.async {
                try {
                    tagExtractor.consume(incompleteSongs, completeSongs)
                } catch (e: Exception) {
                    logE("Tag extraction failed: $e")
                    completeSongs.close(
                        Exception("Tag extraction failed: ${e.stackTraceToString()}"))
                    return@async
                }
                completeSongs.close()
            }

        // DeviceLibrary constructs music parent instances as song information is provided,
        // and then forwards them to the primary loading loop.
        logD("Starting DeviceLibrary creation")
        val deviceLibraryJob =
            scope.async(Dispatchers.Default) {
                val deviceLibrary =
                    try {
                        deviceLibraryFactory.create(
                            completeSongs, processedSongs, separators, nameFactory)
                    } catch (e: Exception) {
                        logE("DeviceLibrary creation failed: $e")
                        processedSongs.close(
                            Exception("DeviceLibrary creation failed: ${e.stackTraceToString()}"))
                        return@async Result.failure(e)
                    }
                processedSongs.close()
                Result.success(deviceLibrary)
            }

        // We could keep track of a total here, but we also need to collate this RawSong information
        // for when we write the cache later on in the finalization step.
        val rawSongs = LinkedList<RawSong>()
        // Use a longer timeout so that dependent components can timeout and throw errors that
        // provide more context than if we timed out here.
        processedSongs.forEachWithTimeout(DEFAULT_TIMEOUT * 2) {
            rawSongs.add(it)
            // Since discovery takes up the bulk of the music loading process, we switch to
            // indicating a defined amount of loaded songs in comparison to the projected amount
            // of songs that were queried.
            emitIndexingProgress(IndexingProgress.Songs(rawSongs.size, query.projectedTotal))
        }

        withTimeout(DEFAULT_TIMEOUT) {
            mediaStoreJob.await()
            tagJob.await()
        }

        // Deliberately done after the involved initialization step to make it less likely
        // that the short-circuit occurs so quickly as to break the UI.
        // TODO: Do not error, instead just wipe the entire library.
        if (rawSongs.isEmpty()) {
            logE("Music library was empty")
            throw NoMusicException()
        }

        // Now that the library is effectively loaded, we can start the finalization step, which
        // involves writing new cache information and creating more music data that is derived
        // from the library (e.g playlists)
        logD("Discovered ${rawSongs.size} songs, starting finalization")

        // We have no idea how long the cache will take, and the playlist construction
        // will be too fast to indicate, so switch back to an indeterminate state.
        emitIndexingProgress(IndexingProgress.Indeterminate)

        // The UserLibrary job is split into a query and construction step, a la MediaStore.
        // This way, we can start working on playlists even as DeviceLibrary might still be
        // working on parent information.
        logD("Starting UserLibrary query")
        val userLibraryQueryJob =
            scope.async {
                val rawPlaylists =
                    try {
                        userLibraryFactory.query()
                    } catch (e: Exception) {
                        return@async Result.failure(e)
                    }
                Result.success(rawPlaylists)
            }

        // The cache might not exist, or we might have encountered a song not present in it.
        // Both situations require us to rewrite the cache in bulk. This is also done parallel
        // since the playlist read will probably take some time.
        // TODO: Read/write from the cache incrementally instead of in bulk?
        if (cache == null || cache.invalidated) {
            logD("Writing cache [why=${cache?.invalidated}]")
            cacheRepository.writeCache(rawSongs)
        }

        // Create UserLibrary once we finally get the required components for it.
        logD("Awaiting UserLibrary query")
        val rawPlaylists = userLibraryQueryJob.await().getOrThrow()
        logD("Awaiting DeviceLibrary creation")
        val deviceLibrary = deviceLibraryJob.await().getOrThrow()
        logD("Starting UserLibrary creation")
        val userLibrary = userLibraryFactory.create(rawPlaylists, deviceLibrary, nameFactory)

        // Loading process is functionally done, indicate such
        logD(
            "Successfully indexed music library [device=$deviceLibrary " +
                "user=$userLibrary time=${System.currentTimeMillis() - start}]")
        emitIndexingCompletion(null)

        val deviceLibraryChanged: Boolean
        val userLibraryChanged: Boolean
        // We want to make sure that all reads and writes are synchronized due to the sheer
        // amount of consumers of MusicRepository.
        // TODO: Would Atomics not be a better fit here?
        synchronized(this) {
            // It's possible that this reload might have changed nothing, so make sure that
            // hasn't happened before dispatching a change to all consumers.
            deviceLibraryChanged = this.deviceLibrary != deviceLibrary
            userLibraryChanged = this.userLibrary != userLibrary
            if (!deviceLibraryChanged && !userLibraryChanged) {
                logD("Library has not changed, skipping update")
                return
            }

            this.deviceLibrary = deviceLibrary
            this.userLibrary = userLibrary
        }

        // Consumers expect their updates to be on the main thread (notably PlaybackService),
        // so switch to it.
        withContext(Dispatchers.Main) {
            dispatchLibraryChange(deviceLibraryChanged, userLibraryChanged)
        }
    }

    private suspend fun emitIndexingProgress(progress: IndexingProgress) {
        yield()
        synchronized(this) {
            currentIndexingState = IndexingState.Indexing(progress)
            for (listener in indexingListeners) {
                listener.onIndexingStateChanged()
            }
        }
    }

    private suspend fun emitIndexingCompletion(error: Exception?) {
        yield()
        synchronized(this) {
            previousCompletedState = IndexingState.Completed(error)
            currentIndexingState = null
            logD("Dispatching completion state [error=$error]")
            for (listener in indexingListeners) {
                listener.onIndexingStateChanged()
            }
        }
    }

    @Synchronized
    private fun dispatchLibraryChange(device: Boolean, user: Boolean) {
        val changes = MusicRepository.Changes(device, user)
        logD("Dispatching library change [changes=$changes]")
        for (listener in updateListeners) {
            listener.onMusicChanges(changes)
        }
    }
}

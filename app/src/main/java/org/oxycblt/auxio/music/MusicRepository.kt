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
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.MusicRepository.IndexingWorker
import org.oxycblt.musikr.IndexingProgress
import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Musikr
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheDatabase
import org.oxycblt.musikr.cover.StoredCovers
import org.oxycblt.musikr.fs.Components
import org.oxycblt.musikr.playlist.db.PlaylistDatabase
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.tag.interpret.Naming
import org.oxycblt.musikr.tag.interpret.Separators
import timber.log.Timber as L

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
    /** The current library */
    val library: RevisionedLibrary?

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
    suspend fun index(worker: IndexingWorker, withCache: Boolean)

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
     * @param library Whether the current [Playlist]s have changed.
     */
    data class Changes(val deviceLibrary: Boolean, val userLibrary: Boolean)

    /** A listener for events in the music loading process. */
    interface IndexingListener {
        /** Called when the music loading state changed. */
        fun onIndexingStateChanged()
    }

    /** A persistent worker that can load music in the background. */
    interface IndexingWorker {
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
    @ApplicationContext private val context: Context,
    private val cacheDatabase: CacheDatabase,
    private val playlistDatabase: PlaylistDatabase,
    private val musicSettings: MusicSettings
) : MusicRepository {
    private val updateListeners = mutableListOf<MusicRepository.UpdateListener>()
    private val indexingListeners = mutableListOf<MusicRepository.IndexingListener>()
    @Volatile private var indexingWorker: MusicRepository.IndexingWorker? = null

    @Volatile override var library: MutableRevisionedLibrary? = null
    @Volatile private var previousCompletedState: IndexingState.Completed? = null
    @Volatile private var currentIndexingState: IndexingState? = null
    override val indexingState: IndexingState?
        get() = currentIndexingState ?: previousCompletedState

    @Synchronized
    override fun addUpdateListener(listener: MusicRepository.UpdateListener) {
        L.d("Adding $listener to update listeners")
        updateListeners.add(listener)
        listener.onMusicChanges(MusicRepository.Changes(deviceLibrary = true, userLibrary = true))
    }

    @Synchronized
    override fun removeUpdateListener(listener: MusicRepository.UpdateListener) {
        L.d("Removing $listener to update listeners")
        if (!updateListeners.remove(listener)) {
            L.w("Update listener $listener was not added prior, cannot remove")
        }
    }

    @Synchronized
    override fun addIndexingListener(listener: MusicRepository.IndexingListener) {
        L.d("Adding $listener to indexing listeners")
        indexingListeners.add(listener)
        listener.onIndexingStateChanged()
    }

    @Synchronized
    override fun removeIndexingListener(listener: MusicRepository.IndexingListener) {
        L.d("Removing $listener from indexing listeners")
        if (!indexingListeners.remove(listener)) {
            L.w("Indexing listener $listener was not added prior, cannot remove")
        }
    }

    @Synchronized
    override fun registerWorker(worker: MusicRepository.IndexingWorker) {
        if (indexingWorker != null) {
            L.w("Worker is already registered")
            return
        }
        L.d("Registering worker $worker")
        indexingWorker = worker
    }

    @Synchronized
    override fun unregisterWorker(worker: MusicRepository.IndexingWorker) {
        if (indexingWorker !== worker) {
            L.w("Given worker did not match current worker")
            return
        }
        L.d("Unregistering worker $worker")
        indexingWorker = null
        currentIndexingState = null
    }

    @Synchronized
    override fun find(uid: Music.UID) =
        (library?.run {
            findSong(uid)
                ?: findAlbum(uid)
                ?: findArtist(uid)
                ?: findGenre(uid)
                ?: findPlaylist(uid)
        })

    override suspend fun createPlaylist(name: String, songs: List<Song>) {
        val library = synchronized(this) { library ?: return }
        L.d("Creating playlist $name with ${songs.size} songs")
        val newLibrary = library.createPlaylist(name, songs)
        synchronized(this) { this.library = newLibrary }
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun renamePlaylist(playlist: Playlist, name: String) {
        val library = synchronized(this) { library ?: return }
        L.d("Renaming $playlist to $name")
        val newLibrary = library.renamePlaylist(playlist, name)
        synchronized(this) { this.library = newLibrary }
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val library = synchronized(this) { library ?: return }
        L.d("Deleting $playlist")
        val newLibrary = library.deletePlaylist(playlist)
        synchronized(this) { this.library = newLibrary }
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun addToPlaylist(songs: List<Song>, playlist: Playlist) {
        val library = synchronized(this) { library ?: return }
        L.d("Adding ${songs.size} songs to $playlist")
        val newLibrary = library.addToPlaylist(playlist, songs)
        synchronized(this) { this.library = newLibrary }
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>) {
        val library = synchronized(this) { library ?: return }
        L.d("Rewriting $playlist with ${songs.size} songs")
        library.rewritePlaylist(playlist, songs)
        withContext(Dispatchers.Main) { dispatchLibraryChange(device = false, user = true) }
    }

    @Synchronized
    override fun requestIndex(withCache: Boolean) {
        L.d("Requesting index operation [cache=$withCache]")
        indexingWorker?.requestIndex(withCache)
    }

    override suspend fun index(worker: IndexingWorker, withCache: Boolean) {
        L.d("Begin index [cache=$withCache]")
        try {
            indexImpl(withCache)
        } catch (e: CancellationException) {
            // Got cancelled, propagate upwards to top-level co-routine.
            L.d("Loading routine was cancelled")
            throw e
        } catch (e: Exception) {
            // Music loading process failed due to something we have not handled.
            // TODO: Still want to display this error eventually
            L.e("Music indexing failed")
            L.e(e.stackTraceToString())
            emitIndexingCompletion(e)
        }
    }

    private suspend fun indexImpl(withCache: Boolean) {
        // Obtain configuration information
        val separators = Separators.from(musicSettings.separators)
        val nameFactory =
            if (musicSettings.intelligentSorting) {
                Naming.intelligent()
            } else {
                Naming.simple()
            }
        val locations = musicSettings.musicLocations

        val revision: UUID
        val storage: Storage
        if (withCache) {
            revision = this.library?.revision ?: musicSettings.revision
            storage =
                Storage(
                    Cache.writeOnly(cacheDatabase),
                    StoredCovers.editor(context, Components.parseUnix("covers_${UUID.randomUUID()}")),
                    StoredPlaylists.from(playlistDatabase))
        } else {
            revision = UUID.randomUUID()
            storage =
                Storage(
                    Cache.writeOnly(cacheDatabase),
                    StoredCovers.editor(context, Components.parseUnix("covers_$revision")),
                    StoredPlaylists.from(playlistDatabase))
        }

        val interpretation = Interpretation(nameFactory, separators)

        val newLibrary =
            Musikr.new(context, storage, interpretation).run(locations, ::emitIndexingProgress)

        val revisionedLibrary = MutableRevisionedLibrary(revision, newLibrary)

        emitIndexingCompletion(null)

        // We want to make sure that all reads and writes are synchronized due to the sheer
        // amount of consumers of MusicRepository.
        // TODO: Would Atomics not be a better fit here?
        val deviceLibraryChanged: Boolean
        val userLibraryChanged: Boolean
        synchronized(this) {
            // It's possible that this reload might have changed nothing, so make sure that
            // hasn't happened before dispatching a change to all consumers.
            deviceLibraryChanged =
                this.library?.songs != newLibrary.songs ||
                    this.library?.albums != newLibrary.albums ||
                    this.library?.artists != newLibrary.artists ||
                    this.library?.genres != newLibrary.genres
            userLibraryChanged = this.library?.playlists != newLibrary.playlists
            if (!deviceLibraryChanged && !userLibraryChanged) {
                L.d("Library has not changed, skipping update")
                return
            }

            this.library = revisionedLibrary
        }

        // Consumers expect their updates to be on the main thread (notably PlaybackService),
        // so switch to it.
        withContext(Dispatchers.Main) {
            dispatchLibraryChange(deviceLibraryChanged, userLibraryChanged)
        }

        // Quietly update the revision if needed (this way we don't disrupt any new loads)
        if (!withCache) {
            musicSettings.revision = revisionedLibrary.revision
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
            L.d("Dispatching completion state [error=$error]")
            for (listener in indexingListeners) {
                listener.onIndexingStateChanged()
            }
        }
    }

    @Synchronized
    private fun dispatchLibraryChange(device: Boolean, user: Boolean) {
        val changes = MusicRepository.Changes(device, user)
        L.d("Dispatching library change [changes=$changes]")
        for (listener in updateListeners) {
            listener.onMusicChanges(changes)
        }
    }
}

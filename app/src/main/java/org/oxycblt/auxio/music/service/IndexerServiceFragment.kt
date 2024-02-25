/*
 * Copyright (c) 2022 Auxio Project
 * IndexerServiceFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.service

import android.app.Service
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import coil.ImageLoader
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.IndexingProgress
import org.oxycblt.auxio.music.IndexingState
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.service.ServiceFragment
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD

/**
 * A [Service] that manages the background music loading process.
 *
 * Loading music is a time-consuming process that would likely be killed by the system before it
 * could complete if ran anywhere else. So, this [Service] manages the music loading process as an
 * instance of [MusicRepository.IndexingWorker].
 *
 * This [Service] also handles automatic rescanning, as that is a similarly long-running background
 * operation that would be unsuitable elsewhere in the app.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Unify with PlaybackService as part of the service independence project
 */
class IndexerServiceFragment
@Inject
constructor(
    val imageLoader: ImageLoader,
    val musicRepository: MusicRepository,
    val musicSettings: MusicSettings,
    val playbackManager: PlaybackStateManager
) :
    ServiceFragment(),
    MusicRepository.IndexingWorker,
    MusicRepository.IndexingListener,
    MusicRepository.UpdateListener,
    MusicSettings.Listener {
    private val serviceJob = Job()
    private val indexScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private var currentIndexJob: Job? = null
    private lateinit var indexingNotification: IndexingNotification
    private lateinit var observingNotification: ObservingNotification
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var indexerContentObserver: SystemContentObserver

    override fun onCreate(context: Context) {
        indexingNotification = IndexingNotification(context)
        observingNotification = ObservingNotification(context)
        wakeLock =
            context
                .getSystemServiceCompat(PowerManager::class)
                .newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID + ":IndexerService")
        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        indexerContentObserver = SystemContentObserver()
        musicSettings.registerListener(this)
        musicRepository.addUpdateListener(this)
        musicRepository.addIndexingListener(this)
        musicRepository.registerWorker(this)

        logD("Service created.")
    }

    override fun onDestroy() {
        // De-initialize core service components first.
        wakeLock.releaseSafe()
        // Then cancel the listener-dependent components to ensure that stray reloading
        // events will not occur.
        indexerContentObserver.release()
        musicSettings.unregisterListener(this)
        musicRepository.removeUpdateListener(this)
        musicRepository.removeIndexingListener(this)
        musicRepository.unregisterWorker(this)
        // Then cancel any remaining music loading jobs.
        serviceJob.cancel()
    }

    // --- CONTROLLER CALLBACKS ---

    override fun requestIndex(withCache: Boolean) {
        logD("Starting new indexing job (previous=${currentIndexJob?.hashCode()})")
        // Cancel the previous music loading job.
        currentIndexJob?.cancel()
        // Start a new music loading job on a co-routine.
        currentIndexJob = musicRepository.index(this, withCache)
    }

    override val applicationContext: Context
        get() = context

    override val scope = indexScope

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        logD("Music changed, updating shared objects")
        // Wipe possibly-invalidated outdated covers
        imageLoader.memoryCache?.clear()
        // Clear invalid models from PlaybackStateManager. This is not connected
        // to a listener as it is bad practice for a shared object to attach to
        // the listener system of another.
        playbackManager.toSavedState()?.let { savedState ->
            playbackManager.applySavedState(
                savedState.copy(
                    heap =
                        savedState.heap.map { song ->
                            song?.let { deviceLibrary.findSong(it.uid) }
                        }),
                true)
        }
    }

    override fun onIndexingStateChanged() {
        val state = musicRepository.indexingState
        if (state is IndexingState.Indexing) {
            updateActiveSession(state.progress)
        } else {
            updateIdleSession()
        }
    }

    // --- INTERNAL ---

    private fun updateActiveSession(progress: IndexingProgress) {
        // When loading, we want to enter the foreground state so that android does
        // not shut off the loading process. Note that while we will always post the
        // notification when initially starting, we will not update the notification
        // unless it indicates that it has changed.
        val changed = indexingNotification.updateIndexingState(progress)
        if (changed) {
            logD("Notification changed, re-posting notification")
            startForeground(indexingNotification)
        }
        // Make sure we can keep the CPU on while loading music
        wakeLock.acquireSafe()
    }

    private fun updateIdleSession() {
        if (musicSettings.shouldBeObserving) {
            // There are a few reasons why we stay in the foreground with automatic rescanning:
            // 1. Newer versions of Android have become more and more restrictive regarding
            // how a foreground service starts. Thus, it's best to go foreground now so that
            // we can go foreground later.
            // 2. If a non-foreground service is killed, the app will probably still be alive,
            // and thus the music library will not be updated at all.
            // TODO: Assuming I unify this with PlaybackService, it's possible that I won't need
            //  this anymore, or at least I only have to use it when the app task is not removed.
            logD("Need to observe, staying in foreground")
            startForeground(observingNotification)
        } else {
            // Not observing and done loading, exit foreground.
            logD("Exiting foreground")
            stopForeground()
        }
        // Release our wake lock (if we were using it)
        wakeLock.releaseSafe()
    }

    /** Utility to safely acquire a [PowerManager.WakeLock] without crashes/inefficiency. */
    private fun PowerManager.WakeLock.acquireSafe() {
        // Avoid unnecessary acquire calls.
        if (!wakeLock.isHeld) {
            logD("Acquiring wake lock")
            // Time out after a minute, which is the average music loading time for a medium-sized
            // library. If this runs out, we will re-request the lock, and if music loading is
            // shorter than the timeout, it will be released early.
            acquire(WAKELOCK_TIMEOUT_MS)
        }
    }

    /** Utility to safely release a [PowerManager.WakeLock] without crashes/inefficiency. */
    private fun PowerManager.WakeLock.releaseSafe() {
        // Avoid unnecessary release calls.
        if (wakeLock.isHeld) {
            logD("Releasing wake lock")
            release()
        }
    }

    // --- SETTING CALLBACKS ---

    override fun onIndexingSettingChanged() {
        // Music loading configuration changed, need to reload music.
        requestIndex(true)
    }

    override fun onObservingChanged() {
        // Make sure we don't override the service state with the observing
        // notification if we were actively loading when the automatic rescanning
        // setting changed. In such a case, the state will still be updated when
        // the music loading process ends.
        if (currentIndexJob == null) {
            logD("Not loading, updating idle session")
            updateIdleSession()
        }
    }

    /**
     * A [ContentObserver] that observes the [MediaStore] music database for changes, a behavior
     * known to the user as automatic rescanning. The active (and not passive) nature of observing
     * the database is what requires [IndexerService] to stay foreground when this is enabled.
     */
    private inner class SystemContentObserver :
        ContentObserver(Handler(Looper.getMainLooper())), Runnable {
        private val handler = Handler(Looper.getMainLooper())

        init {
            context.contentResolverSafe.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, this)
        }

        /**
         * Release this instance, preventing it from further observing the database and cancelling
         * any pending update events.
         */
        fun release() {
            handler.removeCallbacks(this)
            context.contentResolverSafe.unregisterContentObserver(this)
        }

        override fun onChange(selfChange: Boolean) {
            // Batch rapid-fire updates to the library into a single call to run after 500ms
            handler.removeCallbacks(this)
            handler.postDelayed(this, REINDEX_DELAY_MS)
        }

        override fun run() {
            // Check here if we should even start a reindex. This is much less bug-prone than
            // registering and de-registering this component as this setting changes.
            if (musicSettings.shouldBeObserving) {
                logD("MediaStore changed, starting re-index")
                requestIndex(true)
            }
        }
    }

    private companion object {
        const val WAKELOCK_TIMEOUT_MS = 60 * 1000L
        const val REINDEX_DELAY_MS = 500L
    }
}

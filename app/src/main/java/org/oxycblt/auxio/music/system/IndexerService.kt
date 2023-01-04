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

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.database.ContentObserver
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import coil.imageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.filesystem.contentResolverSafe
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.service.ForegroundManager
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD

/**
 * A [Service] that manages the background music loading process.
 *
 * Loading music is a time-consuming process that would likely be killed by the system before it
 * could complete if ran anywhere else. So, this [Service] manages the music loading process as an
 * instance of [Indexer.Controller].
 *
 * This [Service] also handles automatic rescanning, as that is a similarly long-running background
 * operation that would be unsuitable elsewhere in the app.
 *
 * TODO: Unify with PlaybackService as part of the service independence project
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class IndexerService :
    Service(), Indexer.Controller, SharedPreferences.OnSharedPreferenceChangeListener {
    private val indexer = Indexer.getInstance()
    private val musicStore = MusicStore.getInstance()
    private val playbackManager = PlaybackStateManager.getInstance()
    private val serviceJob = Job()
    private val indexScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private var currentIndexJob: Job? = null
    private lateinit var foregroundManager: ForegroundManager
    private lateinit var indexingNotification: IndexingNotification
    private lateinit var observingNotification: ObservingNotification
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var indexerContentObserver: SystemContentObserver
    private lateinit var settings: Settings

    override fun onCreate() {
        super.onCreate()
        // Initialize the core service components first.
        foregroundManager = ForegroundManager(this)
        indexingNotification = IndexingNotification(this)
        observingNotification = ObservingNotification(this)
        wakeLock =
            getSystemServiceCompat(PowerManager::class)
                .newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID + ":IndexerService")
        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        indexerContentObserver = SystemContentObserver()
        settings = Settings(this)
        settings.addListener(this)
        indexer.registerController(this)
        // An indeterminate indexer and a missing library implies we are extremely early
        // in app initialization so start loading music.
        if (musicStore.library == null && indexer.isIndeterminate) {
            logD("No library present and no previous response, indexing music now")
            onStartIndexing(true)
        }

        logD("Service created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_NOT_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // De-initialize core service components first.
        foregroundManager.release()
        wakeLock.releaseSafe()
        // Then cancel the listener-dependent components to ensure that stray reloading
        // events will not occur.
        indexerContentObserver.release()
        settings.removeListener(this)
        indexer.unregisterController(this)
        // Then cancel any remaining music loading jobs.
        serviceJob.cancel()
        indexer.reset()
    }

    // --- CONTROLLER CALLBACKS ---

    override fun onStartIndexing(withCache: Boolean) {
        if (indexer.isIndexing) {
            // Cancel the previous music loading job.
            currentIndexJob?.cancel()
            indexer.reset()
        }
        // Start a new music loading job on a co-routine.
        currentIndexJob = indexScope.launch { indexer.index(this@IndexerService, withCache) }
    }

    override fun onIndexerStateChanged(state: Indexer.State?) {
        when (state) {
            is Indexer.State.Indexing -> updateActiveSession(state.indexing)
            is Indexer.State.Complete -> {
                val newLibrary = state.result.getOrNull()
                if (newLibrary != null && newLibrary != musicStore.library) {
                    logD("Applying new library")
                    // We only care if the newly-loaded library is going to replace a previously
                    // loaded library.
                    if (musicStore.library != null) {
                        // Wipe possibly-invalidated outdated covers
                        imageLoader.memoryCache?.clear()
                        // Clear invalid models from PlaybackStateManager. This is not connected
                        // to a listener as it is bad practice for a shared object to attach to
                        // the listener system of another.
                        playbackManager.sanitize(newLibrary)
                    }
                    // Forward the new library to MusicStore to continue the update process.
                    musicStore.library = newLibrary
                }
                // On errors, while we would want to show a notification that displays the
                // error, that requires the Android 13 notification permission, which is not
                // handled right now.
                updateIdleSession()
            }
            null -> {
                // Null is the indeterminate state that occurs on app startup or after
                // the cancellation of a load, so in that case we want to stop foreground
                // since (technically) nothing is loading.
                updateIdleSession()
            }
        }
    }

    // --- INTERNAL ---

    /**
     * Update the current state to "Active", in which the service signals that music loading is
     * on-going.
     * @param state The current music loading state.
     */
    private fun updateActiveSession(state: Indexer.Indexing) {
        // When loading, we want to enter the foreground state so that android does
        // not shut off the loading process. Note that while we will always post the
        // notification when initially starting, we will not update the notification
        // unless it indicates that it has changed.
        val changed = indexingNotification.updateIndexingState(state)
        if (!foregroundManager.tryStartForeground(indexingNotification) && changed) {
            logD("Notification changed, re-posting notification")
            indexingNotification.post()
        }
        // Make sure we can keep the CPU on while loading music
        wakeLock.acquireSafe()
    }

    /**
     * Update the current state to "Idle", in which it either does nothing or signals that it's
     * currently monitoring the music library for changes.
     */
    private fun updateIdleSession() {
        if (settings.shouldBeObserving) {
            // There are a few reasons why we stay in the foreground with automatic rescanning:
            // 1. Newer versions of Android have become more and more restrictive regarding
            // how a foreground service starts. Thus, it's best to go foreground now so that
            // we can go foreground later.
            // 2. If a non-foreground service is killed, the app will probably still be alive,
            // and thus the music library will not be updated at all.
            // TODO: Assuming I unify this with PlaybackService, it's possible that I won't need
            //  this anymore, or at least I only have to use it when the app task is not removed.
            if (!foregroundManager.tryStartForeground(observingNotification)) {
                observingNotification.post()
            }
        } else {
            // Not observing and done loading, exit foreground.
            foregroundManager.tryStopForeground()
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            // Hook changes in music settings to a new music loading event.
            getString(R.string.set_key_exclude_non_music),
            getString(R.string.set_key_music_dirs),
            getString(R.string.set_key_music_dirs_include),
            getString(R.string.set_key_separators) -> onStartIndexing(true)
            getString(R.string.set_key_observing) -> {
                // Make sure we don't override the service state with the observing
                // notification if we were actively loading when the automatic rescanning
                // setting changed. In such a case, the state will still be updated when
                // the music loading process ends.
                if (!indexer.isIndexing) {
                    updateIdleSession()
                }
            }
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
            contentResolverSafe.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, this)
        }

        /**
         * Release this instance, preventing it from further observing the database and cancelling
         * any pending update events.
         */
        fun release() {
            handler.removeCallbacks(this)
            contentResolverSafe.unregisterContentObserver(this)
        }

        override fun onChange(selfChange: Boolean) {
            // Batch rapid-fire updates to the library into a single call to run after 500ms
            handler.removeCallbacks(this)
            handler.postDelayed(this, REINDEX_DELAY_MS)
        }

        override fun run() {
            // Check here if we should even start a reindex. This is much less bug-prone than
            // registering and de-registering this component as this setting changes.
            if (settings.shouldBeObserving) {
                onStartIndexing(true)
            }
        }
    }

    private companion object {
        const val WAKELOCK_TIMEOUT_MS = 60 * 1000L
        const val REINDEX_DELAY_MS = 500L
    }
}

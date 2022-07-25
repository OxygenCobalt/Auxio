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
import android.database.ContentObserver
import android.os.*
import android.provider.MediaStore
import coil.imageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.system.ForegroundManager
import org.oxycblt.auxio.util.contentResolverSafe
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD

/**
 * A [Service] that handles the music loading process.
 *
 * Loading music is actually somewhat time-consuming, to the point where it's likely better suited
 * to a service that is less likely to be killed by the OS.
 *
 * You could probably do the same using WorkManager and the GooberQueue library or whatever, but the
 * boilerplate you skip is not worth the insanity of androidx.
 *
 * @author OxygenCobalt
 */
class IndexerService : Service(), Indexer.Controller, Settings.Callback {
    private val indexer = Indexer.getInstance()
    private val musicStore = MusicStore.getInstance()

    private val serviceJob = Job()
    private val indexScope = CoroutineScope(serviceJob + Dispatchers.IO)

    private val playbackManager = PlaybackStateManager.getInstance()

    private lateinit var foregroundManager: ForegroundManager
    private lateinit var indexingNotification: IndexingNotification
    private lateinit var observingNotification: ObservingNotification

    private lateinit var settings: Settings
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var indexerContentObserver: SystemContentObserver

    override fun onCreate() {
        super.onCreate()

        foregroundManager = ForegroundManager(this)
        indexingNotification = IndexingNotification(this)
        observingNotification = ObservingNotification(this)

        wakeLock =
            getSystemServiceSafe(PowerManager::class)
                .newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID + ".IndexerService")

        settings = Settings(this, this)
        indexerContentObserver = SystemContentObserver()

        indexer.registerController(this)
        if (musicStore.library == null && indexer.isIndeterminate) {
            logD("No library present and no previous response, indexing music now")
            onStartIndexing()
        }

        logD("Service created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_NOT_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        foregroundManager.release()
        wakeLock.releaseSafe()

        // De-initialize the components first to prevent stray reloading events
        settings.release()
        indexerContentObserver.release()
        indexer.unregisterController(this)

        // Then cancel the other components.
        indexer.cancelLast()
        serviceJob.cancel()
    }

    // --- CONTROLLER CALLBACKS ---

    override fun onStartIndexing() {
        if (indexer.isIndexing) {
            indexer.cancelLast()
        }

        indexScope.launch { indexer.index(this@IndexerService) }
    }

    override fun onIndexerStateChanged(state: Indexer.State?) {
        when (state) {
            is Indexer.State.Complete -> {
                if (state.response is Indexer.Response.Ok &&
                    state.response.library != musicStore.library) {
                    logD("Applying new library")

                    val newLibrary = state.response.library

                    if (musicStore.library != null) {
                        // This is a new library to replace an existing one.

                        // Wipe possibly-invalidated album covers
                        imageLoader.memoryCache?.clear()

                        // Clear invalid models from PlaybackStateManager. This is not connected
                        // to a callback as it is bad practice for a shared object to attach to
                        // the callback system of another.
                        playbackManager.sanitize(newLibrary)
                    }

                    musicStore.updateLibrary(newLibrary)
                }

                // On errors, while we would want to show a notification that displays the
                // error, in practice that comes into conflict with the upcoming Android 13
                // notification permission, and there is no point implementing permission
                // on-boarding for such when it will only be used for this.
                updateIdleSession()
            }
            is Indexer.State.Indexing -> {
                updateActiveSession(state.indexing)
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

    private fun updateActiveSession(state: Indexer.Indexing) {
        // When loading, we want to enter the foreground state so that android does
        // not shut off the loading process. Note that while we will always post the
        // notification when initially starting, we will not update the notification
        // unless it indicates that we have changed it.
        val changed = indexingNotification.updateIndexingState(state)
        if (!foregroundManager.tryStartForeground(indexingNotification) && changed) {
            logD("Notification changed, re-posting notification")
            indexingNotification.post()
        }

        // Make sure we can keep the CPU on while loading music
        wakeLock.acquireSafe()
    }

    private fun updateIdleSession() {
        if (settings.shouldBeObserving) {
            // There are a few reasons why we stay in the foreground with automatic rescanning:
            // 1. Newer versions of Android have become more and more restrictive regarding
            // how a foreground service starts. Thus, it's best to go foreground now so that
            // we can go foreground later.
            // 2. If a non-foreground service is killed, the app will probably still be alive,
            // and thus the music library will not be updated at all.
            if (!foregroundManager.tryStartForeground(observingNotification)) {
                observingNotification.post()
            }
        } else {
            foregroundManager.tryStopForeground()
        }

        // Release our wake lock (if we were using it)
        wakeLock.releaseSafe()
    }

    private fun PowerManager.WakeLock.acquireSafe() {
        if (!wakeLock.isHeld) {
            logD("Acquiring wake lock")
            acquire()
        }
    }

    private fun PowerManager.WakeLock.releaseSafe() {
        if (wakeLock.isHeld) {
            logD("Releasing wake lock")
            release()
        }
    }

    // --- SETTING CALLBACKS ---

    override fun onSettingChanged(key: String) {
        when (key) {
            getString(R.string.set_key_music_dirs),
            getString(R.string.set_key_music_dirs_include),
            getString(R.string.set_key_quality_tags) -> onStartIndexing()
            getString(R.string.set_key_observing) -> {
                if (!indexer.isIndexing) {
                    updateIdleSession()
                }
            }
        }
    }

    /** Internal content observer intended to work with the automatic reloading system. */
    private inner class SystemContentObserver(
        private val handler: Handler = Handler(Looper.getMainLooper())
    ) : ContentObserver(handler), Runnable {
        init {
            contentResolverSafe.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, this)
        }

        fun release() {
            contentResolverSafe.unregisterContentObserver(this)
        }

        override fun onChange(selfChange: Boolean) {
            // Batch rapid-fire updates to the library into a single call to run after 500ms
            handler.removeCallbacks(this)
            handler.postDelayed(this, REINDEX_DELAY)
        }

        override fun run() {
            // Check here if we should even start a reindex. This is much less bug-prone than
            // registering and de-registering this component as this setting changes.
            if (settings.shouldBeObserving) {
                onStartIndexing()
            }
        }
    }

    companion object {
        const val REINDEX_DELAY = 500L
    }
}

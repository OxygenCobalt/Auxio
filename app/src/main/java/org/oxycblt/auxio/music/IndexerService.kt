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

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.ServiceCompat
import coil.imageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.state.PlaybackStateDatabase
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.Settings
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
 *
 * TODO: Add file observing
 */
class IndexerService : Service(), Indexer.Controller, Settings.Callback {
    private val indexer = Indexer.getInstance()
    private val musicStore = MusicStore.getInstance()

    private val serviceJob = Job()
    private val indexScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private val updateScope = CoroutineScope(serviceJob + Dispatchers.Main)

    private val playbackManager = PlaybackStateManager.getInstance()
    private lateinit var settings: Settings

    private var isForeground = false
    private lateinit var notification: IndexerNotification

    override fun onCreate() {
        super.onCreate()

        notification = IndexerNotification(this)
        settings = Settings(this, this)

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

        // cancelLast actually stops foreground for us as it updates the loading state to
        // null or completed.
        indexer.cancelLast()
        indexer.unregisterController(this)
        serviceJob.cancel()
        settings.release()
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

                    // Load was completed successfully. However, we still need to do some
                    // extra work to update the app's state.
                    updateScope.launch {
                        if (musicStore.library != null) {
                            // This is a new library to replace a pre-existing one.

                            // Wipe possibly-invalidated album covers
                            imageLoader.memoryCache?.clear()

                            // PlaybackStateManager needs to be updated. We would do this in the
                            // playback module, but this service could is the only component
                            // capable of doing long-running background work as it stands.
                            playbackManager.sanitize(
                                PlaybackStateDatabase.getInstance(this@IndexerService), newLibrary)
                        }

                        musicStore.updateLibrary(newLibrary)

                        stopForegroundSession()
                    }
                } else {
                    // On errors, while we would want to show a notification that displays the
                    // error, in practice that comes into conflict with the upcoming Android 13
                    // notification permission, and there is no point implementing permission
                    // on-boarding for such when it will only be used for this.
                    stopForegroundSession()
                }
            }
            is Indexer.State.Indexing -> {
                // When loading, we want to enter the foreground state so that android does
                // not shut off the loading process. Note that while we will always post the
                // notification when initially starting, we will not update the notification
                // unless it indicates that we have changed it.
                val changed = notification.updateIndexingState(state.indexing)
                if (!isForeground) {
                    logD("Starting foreground session")
                    startForeground(IntegerTable.INDEXER_NOTIFICATION_CODE, notification.build())
                    isForeground = true
                } else if (changed) {
                    logD("Notification changed, re-posting notification")
                    notification.renotify()
                }
            }
            null -> {
                // Null is the indeterminate state that occurs on app startup or after
                // the cancellation of a load, so in that case we want to stop foreground
                // since (technically) nothing is loading.
                stopForegroundSession()
            }
        }
    }

    // --- SETTING CALLBACKS ---

    override fun onSettingChanged(key: String) {
        if (key == getString(R.string.set_key_music_dirs) ||
            key == getString(R.string.set_key_music_dirs_include) ||
            key == getString(R.string.set_key_quality_tags)) {
            onStartIndexing()
        }
    }

    private fun stopForegroundSession() {
        if (isForeground) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            isForeground = false
        }
    }
}

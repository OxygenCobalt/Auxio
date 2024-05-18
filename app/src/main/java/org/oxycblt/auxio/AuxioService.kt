/*
 * Copyright (c) 2024 Auxio Project
 * AuxioService.kt is part of Auxio.
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
 
package org.oxycblt.auxio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.music.service.IndexerServiceFragment
import org.oxycblt.auxio.playback.service.MediaSessionServiceFragment
import org.oxycblt.auxio.util.logD

@AndroidEntryPoint
class AuxioService : MediaLibraryService(), ForegroundListener {
    @Inject lateinit var mediaSessionFragment: MediaSessionServiceFragment

    @Inject lateinit var indexingFragment: IndexerServiceFragment

    private var nativeStart = false

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        mediaSessionFragment.attach(this, this)
        indexingFragment.attach(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        // handleIntent(intent)
        return super.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Start command occurring from a foreign service basically implies a detached
        //  service, we might need more handling here.
        handleIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleIntent(intent: Intent?) {
        nativeStart = intent?.getBooleanExtra(INTENT_KEY_INTERNAL_START, false) ?: false
        logD("${intent} $nativeStart")
        if (!nativeStart) {
            // Some foreign code started us, no guarantees about foreground stability. Figure
            // out what to do.
            mediaSessionFragment.handleNonNativeStart()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSessionFragment.handleTaskRemoved()
    }

    override fun onDestroy() {
        super.onDestroy()
        indexingFragment.release()
        mediaSessionFragment.release()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession =
        mediaSessionFragment.mediaSession

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        updateForeground(ForegroundListener.Change.MEDIA_SESSION)
    }

    override fun updateForeground(change: ForegroundListener.Change) {
        val state = mediaSessionFragment.hasNotification()

        if (state == MediaSessionServiceFragment.NotificationState.RUNNING) {
            if (change == ForegroundListener.Change.MEDIA_SESSION) {
                mediaSessionFragment.createNotification {
                    startForeground(it.notificationId, it.notification)
                }
            }
            // Nothing changed, but don't show anything music related since we can always
            // index during playback.
        } else {
            indexingFragment.createNotification {
                if (it != null) {
                    startForeground(it.code, it.build())
                } else if (state == MediaSessionServiceFragment.NotificationState.NOT_RUNNING) {
                    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                }
            }
        }
    }

    companion object {
        // This is only meant for Auxio to internally ensure that it's state management will work.
        const val INTENT_KEY_INTERNAL_START = BuildConfig.APPLICATION_ID + ".service.INTERNAL_START"
    }
}

interface ForegroundListener {
    fun updateForeground(change: Change)

    enum class Change {
        MEDIA_SESSION,
        INDEXER
    }
}

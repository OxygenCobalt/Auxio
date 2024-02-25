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
 
package org.oxycblt.auxio.service

import android.app.Service
import android.content.Intent
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.music.service.IndexerServiceFragment
import org.oxycblt.auxio.playback.service.PlaybackServiceFragment

@AndroidEntryPoint
class AuxioService : Service() {
    @Inject lateinit var playbackFragment: PlaybackServiceFragment
    @Inject lateinit var indexerFragment: IndexerServiceFragment

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        playbackFragment.attach(this)
        indexerFragment.attach(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        playbackFragment.handleIntent(intent)
        indexerFragment.handleIntent(intent)
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        playbackFragment.handleTaskRemoved()
        indexerFragment.handleTaskRemoved()
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackFragment.release()
        indexerFragment.release()
    }

    fun refreshForeground() {
        val currentNotification = playbackFragment.notification ?: indexerFragment.notification
        if (currentNotification != null) {
            startForeground(currentNotification.code, currentNotification.build())
        } else {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
        }
    }
}

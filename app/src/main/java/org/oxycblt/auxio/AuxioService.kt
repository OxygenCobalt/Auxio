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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import androidx.annotation.StringRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.media.MediaBrowserServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.music.service.MusicServiceFragment
import org.oxycblt.auxio.playback.service.PlaybackServiceFragment

@AndroidEntryPoint
class AuxioService : MediaBrowserServiceCompat(), ForegroundListener {
    @Inject lateinit var mediaSessionFragment: PlaybackServiceFragment

    @Inject lateinit var indexingFragment: MusicServiceFragment

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        setSessionToken(mediaSessionFragment.attach(this))
        indexingFragment.attach(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Start command occurring from a foreign service basically implies a detached
        //  service, we might need more handling here.
        onHandleForeground(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        onHandleForeground(intent)
        return super.onBind(intent)
    }

    private fun onHandleForeground(intent: Intent?) {
        val startId = intent?.getIntExtra(INTENT_KEY_START_ID, -1) ?: -1
        indexingFragment.start()
        mediaSessionFragment.start(startId)
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

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) = throw NotImplementedError()

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>,
        options: Bundle
    ) {
        super.onLoadChildren(parentId, result, options)
    }

    override fun onLoadItem(itemId: String, result: Result<MediaBrowserCompat.MediaItem>) {
        super.onLoadItem(itemId, result)
    }

    override fun onSearch(
        query: String,
        extras: Bundle?,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        super.onSearch(query, extras, result)
    }

    @SuppressLint("RestrictedApi")
    override fun onSubscribe(id: String?, option: Bundle?) {
        super.onSubscribe(id, option)
    }

    @SuppressLint("RestrictedApi")
    override fun onUnsubscribe(id: String?) {
        super.onUnsubscribe(id)
    }

    override fun updateForeground(change: ForegroundListener.Change) {
        val mediaNotification = mediaSessionFragment.notification
        if (mediaNotification != null) {
            if (change == ForegroundListener.Change.MEDIA_SESSION) {
                startForeground(mediaNotification.code, mediaNotification.build())
            }
            // Nothing changed, but don't show anything music related since we can always
            // index during playback.
        } else {
            indexingFragment.createNotification {
                if (it != null) {
                    startForeground(it.code, it.build())
                    isForeground = true
                } else {
                    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                    isForeground = false
                }
            }
        }
    }

    companion object {
        var isForeground = false
            private set

        // This is only meant for Auxio to internally ensure that it's state management will work.
        const val INTENT_KEY_START_ID = BuildConfig.APPLICATION_ID + ".service.START_ID"
    }
}

interface ForegroundListener {
    fun updateForeground(change: Change)

    enum class Change {
        MEDIA_SESSION,
        INDEXER
    }
}

/**
 * Wrapper around [NotificationCompat.Builder] intended for use for [NotificationCompat]s that
 * signal a Service's ongoing foreground state.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class ForegroundServiceNotification(context: Context, info: ChannelInfo) :
    NotificationCompat.Builder(context, info.id) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        // Set up the notification channel. Foreground notifications are non-substantial, and
        // thus make no sense to have lights, vibration, or lead to a notification badge.
        val channel =
            NotificationChannelCompat.Builder(info.id, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(context.getString(info.nameRes))
                .setLightsEnabled(false)
                .setVibrationEnabled(false)
                .setShowBadge(false)
                .build()
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * The code used to identify this notification.
     *
     * @see NotificationManagerCompat.notify
     */
    abstract val code: Int

    /**
     * Reduced representation of a [NotificationChannelCompat].
     *
     * @param id The ID of the channel.
     * @param nameRes A string resource ID corresponding to the human-readable name of this channel.
     */
    data class ChannelInfo(val id: String, @StringRes val nameRes: Int)
}

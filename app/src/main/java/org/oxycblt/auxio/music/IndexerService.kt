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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.newMainPendingIntent

/**
 * A [Service] that handles the music loading process.
 *
 * Loading music is actually somewhat time-consuming, to the point where it's likely better suited
 * to a service that is less likely to be
 *
 * TODO: Rename all instances of loading in-app with indexing
 *
 * @author OxygenCobalt
 */
class IndexerService : Service(), Indexer.Callback {
    private val indexer = Indexer.getInstance()
    private val musicStore = MusicStore.getInstance()

    private val serviceJob = Job()
    private val indexScope = CoroutineScope(serviceJob + Dispatchers.Default)

    private var isForeground = false
    private lateinit var notification: IndexerNotification

    override fun onCreate() {
        super.onCreate()

        notification = IndexerNotification(this)

        indexer.addCallback(this)
        if (musicStore.library == null) {
            logD("No library present, loading music now")
            onRequestReindex()
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
        indexer.removeCallback(this)
        serviceJob.cancel()
    }

    override fun onIndexerStateChanged(state: Indexer.State?) {
        when (state) {
            is Indexer.State.Complete -> {
                if (state.response is Indexer.Response.Ok && musicStore.library == null) {
                    // Load was completed successfully, so apply the new library if we
                    // have not already.
                    // TODO: Change null check for equality check [automatic rescanning]
                    musicStore.library = state.response.library
                }

                // On errors, while we would want to show a notification that displays the
                // error, in practice that comes into conflict with the upcoming Android 13
                // notification permission, and there is no point implementing permission
                // on-boarding for such when it will only be used for this.

                // Note that we don't stop the service here, as (in the future)
                // this service will be used to reload music and observe the music
                // database.
                stopForegroundSession()
            }
            is Indexer.State.Loading -> {
                // When loading, we want to enter the foreground state so that android does
                // not shut off the loading process. Note that while we will always post the
                // notification when initially starting, we will not update the notification
                // unless it indicates that we have changed it.
                val changed = notification.updateLoadingState(state.loading)
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

    override fun onRequestReindex() {
        indexScope.launch { indexer.index(this@IndexerService) }
    }

    private fun stopForegroundSession() {
        if (isForeground) {
            stopForeground(true)
            isForeground = false
        }
    }
}

private class IndexerNotification(private val context: Context) :
    NotificationCompat.Builder(context, CHANNEL_ID) {
    private val notificationManager = context.getSystemServiceSafe(NotificationManager::class)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.info_indexer_channel_name),
                    NotificationManager.IMPORTANCE_LOW)

            notificationManager.createNotificationChannel(channel)
        }

        setSmallIcon(R.drawable.ic_indexer)
        setCategory(NotificationCompat.CATEGORY_SERVICE)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainPendingIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setContentTitle(context.getString(R.string.info_indexer_channel_name))
        setContentText(context.getString(R.string.lbl_loading))
        setProgress(0, 0, true)
    }

    fun renotify() {
        notificationManager.notify(IntegerTable.INDEXER_NOTIFICATION_CODE, build())
    }

    fun updateLoadingState(loading: Indexer.Loading): Boolean {
        when (loading) {
            is Indexer.Loading.Indeterminate -> {
                setContentText(context.getString(R.string.lbl_loading))
                setProgress(0, 0, true)
                return true
            }
            is Indexer.Loading.Songs -> {
                // Only update the notification every 50 songs to prevent excessive updates.
                if (loading.current % 50 == 0) {
                    setContentText(
                        context.getString(R.string.fmt_indexing, loading.current, loading.total))
                    setProgress(loading.total, loading.current, false)
                    return true
                }
            }
        }

        return false
    }

    companion object {
        const val CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel.INDEXER"
    }
}

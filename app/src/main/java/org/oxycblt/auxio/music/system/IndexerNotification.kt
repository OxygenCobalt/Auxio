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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.newMainPendingIntent

/** The notification responsible for showing the indexer state. */
class IndexerNotification(private val context: Context) :
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

        setSmallIcon(R.drawable.ic_indexer_24)
        setCategory(NotificationCompat.CATEGORY_PROGRESS)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainPendingIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setContentTitle(context.getString(R.string.info_indexer_channel_name))
        setContentText(context.getString(R.string.lbl_indexing))
        setProgress(0, 0, true)
    }

    fun renotify() {
        notificationManager.notify(IntegerTable.INDEXER_NOTIFICATION_CODE, build())
    }

    fun updateIndexingState(indexing: Indexer.Indexing): Boolean {
        when (indexing) {
            is Indexer.Indexing.Indeterminate -> {
                logD("Updating state to $indexing")
                setContentText(context.getString(R.string.lbl_indexing))
                setProgress(0, 0, true)
                return true
            }
            is Indexer.Indexing.Songs -> {
                // Only update the notification every 50 songs to prevent excessive updates.
                if (indexing.current % 50 == 0) {
                    logD("Updating state to $indexing")
                    setContentText(
                        context.getString(R.string.fmt_indexing, indexing.current, indexing.total))
                    setProgress(indexing.total, indexing.current, false)
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

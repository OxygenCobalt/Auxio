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

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.system.ServiceNotification
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.newMainPendingIntent

/** The notification responsible for showing the indexer state. */
class IndexingNotification(private val context: Context) :
    ServiceNotification(context, INDEXER_CHANNEL) {
    init {
        setSmallIcon(R.drawable.ic_indexer_24)
        setCategory(NotificationCompat.CATEGORY_PROGRESS)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainPendingIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setContentTitle(context.getString(R.string.lbl_indexing))
        setContentText(context.getString(R.string.lbl_indexing_desc))
        setProgress(0, 0, true)
    }

    override val code: Int
        get() = IntegerTable.INDEXER_NOTIFICATION_CODE

    fun updateIndexingState(indexing: Indexer.Indexing): Boolean {
        when (indexing) {
            is Indexer.Indexing.Indeterminate -> {
                logD("Updating state to $indexing")
                setContentText(context.getString(R.string.lbl_indexing_desc))
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
}

/** The notification responsible for showing the indexer state. */
class ObservingNotification(context: Context) : ServiceNotification(context, INDEXER_CHANNEL) {
    init {
        setSmallIcon(R.drawable.ic_indexer_24)
        setCategory(NotificationCompat.CATEGORY_SERVICE)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainPendingIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setContentTitle(context.getString(R.string.lbl_observing))
        setContentText(context.getString(R.string.lbl_observing_desc))
    }

    override val code: Int
        get() = IntegerTable.INDEXER_NOTIFICATION_CODE
}

private val INDEXER_CHANNEL =
    ServiceNotification.ChannelInfo(
        id = BuildConfig.APPLICATION_ID + ".channel.INDEXER",
        R.string.info_indexer_channel_name,
        NotificationManager.IMPORTANCE_LOW)

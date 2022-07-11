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
 
package org.oxycblt.auxio.ui.system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import org.oxycblt.auxio.util.getSystemServiceSafe

/**
 * Wrapper around [NotificationCompat.Builder] that automates parts of the notification setup, under
 * the assumption that the notification will be used in a service.
 * @author OxygenCobalt
 */
abstract class ServiceNotification(context: Context, info: ChannelInfo) :
    NotificationCompat.Builder(context, info.id) {
    private val notificationManager = context.getSystemServiceSafe(NotificationManager::class)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(info.id, context.getString(info.nameRes), info.importance)

            notificationManager.createNotificationChannel(channel)
        }
    }

    abstract val code: Int

    fun post() {
        notificationManager.notify(code, build())
    }

    data class ChannelInfo(val id: String, @StringRes val nameRes: Int, val importance: Int)
}

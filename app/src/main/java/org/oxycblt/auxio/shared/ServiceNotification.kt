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
 
package org.oxycblt.auxio.shared

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Wrapper around [NotificationCompat.Builder] that automates parts of the notification setup, under
 * the assumption that the notification will be used in a service.
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class ServiceNotification(context: Context, info: ChannelInfo) :
    NotificationCompat.Builder(context, info.id) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        val channel =
            NotificationChannelCompat.Builder(info.id, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(context.getString(info.nameRes))
                .setLightsEnabled(false)
                .setVibrationEnabled(false)
                .setShowBadge(false)
                .build()

        notificationManager.createNotificationChannel(channel)
    }

    abstract val code: Int

    @Suppress("MissingPermission")
    fun post() {
        // This is safe to call without the POST_NOTIFICATIONS permission, as it's a foreground
        // notification.
        notificationManager.notify(code, build())
    }

    data class ChannelInfo(val id: String, @StringRes val nameRes: Int)
}

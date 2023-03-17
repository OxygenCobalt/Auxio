/*
 * Copyright (c) 2022 Auxio Project
 * ForegroundManager.kt is part of Auxio.
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
import androidx.core.app.ServiceCompat
import org.oxycblt.auxio.util.logD

/**
 * A utility to create consistent foreground behavior for a given [Service].
 *
 * @param service [Service] to wrap in this instance.
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Merge with unified service when done.
 */
class ForegroundManager(private val service: Service) {
    private var isForeground = false

    /** Release this instance. */
    fun release() {
        tryStopForeground()
    }

    /**
     * Try to enter a foreground state.
     *
     * @param notification The [ForegroundServiceNotification] to show in order to signal the
     *   foreground state.
     * @return true if the state was changed, false otherwise
     * @see Service.startForeground
     */
    fun tryStartForeground(notification: ForegroundServiceNotification): Boolean {
        if (isForeground) {
            // Nothing to do.
            return false
        }

        logD("Starting foreground state")
        service.startForeground(notification.code, notification.build())
        isForeground = true
        return true
    }

    /**
     * Try to exit a foreground state. Will remove the foreground notification.
     *
     * @return true if the state was changed, false otherwise
     * @see Service.stopForeground
     */
    fun tryStopForeground(): Boolean {
        if (!isForeground) {
            // Nothing to do.
            return false
        }

        logD("Stopping foreground state")
        ServiceCompat.stopForeground(service, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isForeground = false
        return true
    }
}

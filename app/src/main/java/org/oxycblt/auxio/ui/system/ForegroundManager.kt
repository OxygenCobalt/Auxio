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

import android.app.Service
import androidx.core.app.ServiceCompat
import org.oxycblt.auxio.util.logD

/**
 * Wrapper to create consistent behavior regarding a service's foreground state.
 * @author OxygenCobalt
 */
class ForegroundManager(private val service: Service) {
    private var isForeground = false

    fun release() {
        tryStopForeground()
    }

    /**
     * Try to enter a foreground state. Returns false if already in foreground, returns true if
     * state was entered.
     */
    fun tryStartForeground(notification: ServiceNotification): Boolean {
        if (isForeground) {
            return false
        }

        logD("Starting foreground state")

        service.startForeground(notification.code, notification.build())
        isForeground = true

        return true
    }

    /**
     * Try to stop a foreground state. Returns false if already in backend, returns true if state
     * was stopped.
     */
    fun tryStopForeground(): Boolean {
        if (!isForeground) {
            return false
        }

        logD("Stopping foreground state")

        ServiceCompat.stopForeground(service, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isForeground = false

        return true
    }
}

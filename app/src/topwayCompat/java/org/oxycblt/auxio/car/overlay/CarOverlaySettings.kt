/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlaySettings.kt is part of Auxio.
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

package org.oxycblt.auxio.car.overlay

import android.content.Context
import android.provider.Settings

/**
 * Settings facade for the car floating controls feature. Manages enable/disable with permission
 * checks and delegates to [CarOverlayPrefs] for persistence and [CarFloatingControlsService] for
 * runtime control.
 */
object CarOverlaySettings {

    fun isEnabled(context: Context): Boolean {
        return CarOverlayPrefs.from(context).enabled
    }

    /**
     * Enables or disables the car floating controls. When enabling, checks overlay permission and
     * launches the permission activity if not granted. When disabling, stops the overlay service.
     *
     * @return true if the operation completed immediately (enable succeeded or disable completed),
     *   false if permission is needed (activity launched, switch should revert to unchecked)
     */
    fun setEnabled(context: Context, enabled: Boolean): Boolean {
        val prefs = CarOverlayPrefs.from(context)

        if (enabled) {
            if (!Settings.canDrawOverlays(context)) {
                // Mark pending-enable so PermissionActivity knows to enable after grant.
                prefs.pendingEnable = true
                context.startActivity(CarOverlayPermissionActivity.intent(context))
                // Return false: switch should NOT remain enabled until permission is granted.
                return false
            }
            prefs.enabled = true
            prefs.pendingEnable = false
            CarFloatingControlsService.start(context)
        } else {
            prefs.enabled = false
            prefs.pendingEnable = false
            CarFloatingControlsService.stop(context)
        }
        return true
    }

    /**
     * Resets overlay position to default. Updates prefs unconditionally. Only sends a position
     * update to the service if it is enabled and running (does not cold-start the service).
     */
    fun resetPosition(context: Context) {
        val prefs = CarOverlayPrefs.from(context)
        prefs.resetPosition()
        // Only notify a running service — do not cold-start via startService.
        if (prefs.enabled && Settings.canDrawOverlays(context)) {
            CarFloatingControlsService.resetPositionIfRunning(context)
        }
    }

    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}

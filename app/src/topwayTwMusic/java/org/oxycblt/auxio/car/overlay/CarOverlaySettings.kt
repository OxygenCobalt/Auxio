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
     * @return true if the operation completed, false if permission is needed (activity launched)
     */
    fun setEnabled(context: Context, enabled: Boolean): Boolean {
        val prefs = CarOverlayPrefs.from(context)

        if (enabled) {
            if (!Settings.canDrawOverlays(context)) {
                // Launch permission activity - don't enable until permission is granted
                context.startActivity(CarOverlayPermissionActivity.intent(context))
                return false
            }
            prefs.enabled = true
            CarFloatingControlsService.start(context)
        } else {
            prefs.enabled = false
            CarFloatingControlsService.stop(context)
        }
        return true
    }

    fun resetPosition(context: Context) {
        val prefs = CarOverlayPrefs.from(context)
        prefs.resetPosition()
        if (prefs.enabled && Settings.canDrawOverlays(context)) {
            CarFloatingControlsService.resetPosition(context)
        }
    }

    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}

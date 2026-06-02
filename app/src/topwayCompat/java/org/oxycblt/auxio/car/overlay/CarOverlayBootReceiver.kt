/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayBootReceiver.kt is part of Auxio.
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import timber.log.Timber as L

/**
 * Restores the car floating controls overlay after system boot (`ACTION_BOOT_COMPLETED`) if the
 * feature is enabled and overlay permission is still granted.
 *
 * Note: This receiver only handles standard Android boot. ACC sleep/wake events on TS18/Topway
 * devices are handled by Android's standard lifecycle (the service uses `START_NOT_STICKY` and the
 * boot receiver restores state on full system boot).
 */
class CarOverlayBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = CarOverlayPrefs.from(context)
        if (!prefs.enabled) {
            L.d("Car overlay disabled, skipping boot restore")
            return
        }
        if (!Settings.canDrawOverlays(context)) {
            L.w("Overlay permission not granted, skipping boot restore")
            return
        }

        L.d("Restoring car overlay after boot")
        CarFloatingControlsService.start(context)
    }
}

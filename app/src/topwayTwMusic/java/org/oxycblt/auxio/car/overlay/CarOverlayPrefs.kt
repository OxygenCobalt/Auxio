/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayPrefs.kt is part of Auxio.
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
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

/**
 * SharedPreferences wrapper for car floating controls overlay configuration. Manages enablement,
 * position, opacity, and visibility settings.
 *
 * Uses [PreferenceManager.getDefaultSharedPreferences] so that the `car_overlay_enabled` key is
 * shared with the AndroidX `SwitchPreferenceCompat` declared in `preferences_ui.xml`. This avoids
 * split-brain between the preference UI and runtime state.
 */
class CarOverlayPrefs private constructor(private val prefs: SharedPreferences) {

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_ENABLED, value) }

    /** Pending-enable flag: permission activity sets this when permission is being requested. */
    var pendingEnable: Boolean
        get() = prefs.getBoolean(KEY_PENDING_ENABLE, false)
        set(value) = prefs.edit { putBoolean(KEY_PENDING_ENABLE, value) }

    var hideWhileAuxioForeground: Boolean
        get() = prefs.getBoolean(KEY_HIDE_WHILE_AUXIO_FG, true)
        set(value) = prefs.edit { putBoolean(KEY_HIDE_WHILE_AUXIO_FG, value) }

    var positionX: Int
        get() = prefs.getInt(KEY_POSITION_X, DEFAULT_X)
        set(value) = prefs.edit { putInt(KEY_POSITION_X, value) }

    var positionY: Int
        get() = prefs.getInt(KEY_POSITION_Y, DEFAULT_Y)
        set(value) = prefs.edit { putInt(KEY_POSITION_Y, value) }

    var opacityPercent: Int
        get() = prefs.getInt(KEY_OPACITY, DEFAULT_OPACITY).coerceIn(MIN_OPACITY, MAX_OPACITY)
        set(value) = prefs.edit { putInt(KEY_OPACITY, value.coerceIn(MIN_OPACITY, MAX_OPACITY)) }

    fun resetPosition() {
        prefs.edit {
            putInt(KEY_POSITION_X, DEFAULT_X)
            putInt(KEY_POSITION_Y, DEFAULT_Y)
        }
    }

    companion object {
        const val KEY_ENABLED = "car_overlay_enabled"
        private const val KEY_PENDING_ENABLE = "car_overlay_pending_enable"
        private const val KEY_HIDE_WHILE_AUXIO_FG = "car_overlay_hide_auxio_fg"
        private const val KEY_POSITION_X = "car_overlay_x"
        private const val KEY_POSITION_Y = "car_overlay_y"
        private const val KEY_OPACITY = "car_overlay_opacity"

        // Top-center anchor for TS18 (1280x720, 55px status bar, 55px right nav).
        // Usable width = 1280 - 55 = 1225; center = (1225 - ~350 overlay) / 2 ≈ 437.
        const val DEFAULT_X = 437
        const val DEFAULT_Y = 55
        const val DEFAULT_OPACITY = 90
        const val MIN_OPACITY = 30
        const val MAX_OPACITY = 100

        fun from(context: Context): CarOverlayPrefs {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            return CarOverlayPrefs(prefs)
        }
    }
}

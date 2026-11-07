/*
 * Copyright (c) 2026 Auxio Project
 * UsbSettings.kt is part of Auxio.
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
 
package org.oxycblt.auxio.usb

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.ostrichbat2k.usbdac.UsbDacPreferences
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings

/**
 * Auxio's own [Settings]-backed implementation of [UsbDacPreferences] -- the libusbdac library
 * defines the contract, this is the glue that persists it via Auxio's existing SharedPreferences
 * architecture and string resources.
 *
 * @author Auxio Project
 */
class UsbSettingsImpl @Inject constructor(@ApplicationContext context: Context) :
    Settings.Impl<UsbDacPreferences.Listener>(context), UsbDacPreferences {
    override val enabled: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_usb_enabled), false)

    override val bitPerfectMode: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_usb_bit_perfect), false)

    override val abxTestMode: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_usb_abx_test), false)

    override val manualReleaseControl: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_usb_manual_release_control), false)

    override val volumeStepPercent: Int
        get() =
            sharedPreferences
                .getInt(
                    getString(R.string.set_key_usb_volume_step_percent),
                    DEFAULT_VOLUME_STEP_PERCENT,
                )
                .coerceIn(1, UsbDacPreferences.MAX_VOLUME_STEP_PERCENT)

    override var volumeDb: Int?
        get() =
            getString(R.string.set_key_usb_volume).let { key ->
                if (sharedPreferences.contains(key)) sharedPreferences.getInt(key, 0) else null
            }
        set(value) {
            sharedPreferences.edit {
                val key = getString(R.string.set_key_usb_volume)
                if (value != null) putInt(key, value) else remove(key)
            }
        }

    override fun onSettingChanged(key: String, listener: UsbDacPreferences.Listener) {
        when (key) {
            getString(R.string.set_key_usb_enabled) -> listener.onUsbEnabledChanged()
            getString(R.string.set_key_usb_bit_perfect) -> listener.onBitPerfectModeChanged()
            getString(R.string.set_key_usb_abx_test) -> listener.onAbxTestModeChanged()
            getString(R.string.set_key_usb_volume_step_percent) ->
                listener.onVolumeStepPercentChanged()
            getString(R.string.set_key_usb_manual_release_control) -> listener.onManualReleaseControlChanged()
        }
    }

    private companion object {
        /** Matches the system volume UI's old fixed 20-step granularity (1/20 = 5%). */
        const val DEFAULT_VOLUME_STEP_PERCENT = 5
    }
}

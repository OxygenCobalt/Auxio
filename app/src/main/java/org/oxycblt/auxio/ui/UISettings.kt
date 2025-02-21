/*
 * Copyright (c) 2023 Auxio Project
 * UISettings.kt is part of Auxio.
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
 
package org.oxycblt.auxio.ui

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.accent.Accent
import timber.log.Timber as L

/**
 * User configuration for the general app UI.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface UISettings : Settings<UISettings.Listener> {
    /** The current theme. Represented by the AppCompatDelegate constants. */
    val theme: Int
    /** Whether to use a black background when a dark theme is currently used. */
    val useBlackTheme: Boolean
    /** The current [Accent] (Color Scheme). */
    var accent: Accent
    /** Whether to round additional UI elements that require album covers to be rounded. */
    val roundMode: Boolean

    interface Listener {
        /** Called when [roundMode] changes. */
        fun onRoundModeChanged()
    }
}

class UISettingsImpl @Inject constructor(@ApplicationContext context: Context) :
    Settings.Impl<UISettings.Listener>(context), UISettings {
    override val theme: Int
        get() =
            sharedPreferences.getInt(
                getString(R.string.set_key_theme), AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    override val useBlackTheme: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_black_theme), false)

    override var accent: Accent
        get() =
            Accent.from(
                sharedPreferences.getInt(getString(R.string.set_key_accent), Accent.DEFAULT))
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_accent), value.index)
                apply()
            }
        }

    override val roundMode: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_round_mode), true)

    override fun migrate() {
        if (sharedPreferences.contains(OLD_KEY_ACCENT3)) {
            L.d("Migrating $OLD_KEY_ACCENT3")

            var accent = sharedPreferences.getInt(OLD_KEY_ACCENT3, 5)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Accents were previously frozen as soon as the OS was updated to android
                // twelve, as dynamic colors were enabled by default. This is no longer the
                // case, so we need to re-update the setting to dynamic colors here.
                accent = 16
            }

            sharedPreferences.edit {
                putInt(getString(R.string.set_key_accent), accent)
                remove(OLD_KEY_ACCENT3)
                apply()
            }
        }
    }

    override fun onSettingChanged(key: String, listener: UISettings.Listener) {
        if (key == getString(R.string.set_key_round_mode)) {
            L.d("Dispatching round mode setting change")
            listener.onRoundModeChanged()
        }
    }

    private companion object {
        const val OLD_KEY_ACCENT3 = "auxio_accent"
    }
}

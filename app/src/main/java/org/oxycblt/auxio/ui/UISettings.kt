/*
 * Copyright (c) 2023 Auxio Project
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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.accent.Accent
import org.oxycblt.auxio.util.logD

interface UISettings : Settings {
    /** The current theme. Represented by the AppCompatDelegate constants. */
    val theme: Int
    /** Whether to use a black background when a dark theme is currently used. */
    val useBlackTheme: Boolean
    /** The current [Accent] (Color Scheme). */
    var accent: Accent
    /** Whether to round additional UI elements that require album covers to be rounded. */
    val roundMode: Boolean

    private class Real(context: Context) : Settings.Real(context), UISettings {
        override val theme: Int
            get() =
                sharedPreferences.getInt(
                    context.getString(R.string.set_key_theme),
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        override val useBlackTheme: Boolean
            get() =
                sharedPreferences.getBoolean(context.getString(R.string.set_key_black_theme), false)

        override var accent: Accent
            get() =
                Accent.from(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_accent), Accent.DEFAULT))
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_accent), value.index)
                    apply()
                }
            }

        override val roundMode: Boolean
            get() =
                sharedPreferences.getBoolean(context.getString(R.string.set_key_round_mode), false)

        override fun migrate() {
            if (sharedPreferences.contains(OLD_KEY_ACCENT3)) {
                logD("Migrating $OLD_KEY_ACCENT3")

                var accent = sharedPreferences.getInt(OLD_KEY_ACCENT3, 5)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Accents were previously frozen as soon as the OS was updated to android
                    // twelve,
                    // as dynamic colors were enabled by default. This is no longer the case, so we
                    // need
                    // to re-update the setting to dynamic colors here.
                    accent = 16
                }

                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_accent), accent)
                    remove(OLD_KEY_ACCENT3)
                    apply()
                }
            }
        }

        companion object {
            const val OLD_KEY_ACCENT3 = "auxio_accent"
        }
    }

    companion object {
        /**
         * Get a framework-backed implementation.
         * @param context [Context] required.
         */
        fun from(context: Context): UISettings = Real(context)
    }
}

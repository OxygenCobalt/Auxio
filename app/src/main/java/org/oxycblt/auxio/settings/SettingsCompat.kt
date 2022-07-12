/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.content.edit
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.accent.Accent

// A couple of utils for migrating from old settings values to the new formats.
// Usually, these will last for 6 months before being removed.

fun handleAccentCompat(context: Context, prefs: SharedPreferences): Accent {
    val currentKey = context.getString(R.string.set_key_accent)

    if (prefs.contains(OldKeys.KEY_ACCENT3)) {
        Log.d("Auxio.SettingsCompat", "Migrating ${OldKeys.KEY_ACCENT3}")

        var accent = prefs.getInt(OldKeys.KEY_ACCENT3, 5)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Accents were previously frozen as soon as the OS was updated to android twelve,
            // as dynamic colors were enabled by default. This is no longer the case, so we need
            // to re-update the setting to dynamic colors here.
            accent = 16
        }

        prefs.edit {
            putInt(currentKey, accent)
            remove(OldKeys.KEY_ACCENT3)
            apply()
        }
    }

    return Accent.from(prefs.getInt(currentKey, Accent.DEFAULT))
}

/** Cache of the old keys used in Auxio. */
private object OldKeys {
    const val KEY_ACCENT3 = "auxio_accent"
}

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

import android.content.SharedPreferences
import androidx.core.content.edit
import org.oxycblt.auxio.ui.accent.Accent

// A couple of utils for migrating from old settings values to the new formats

fun handleAccentCompat(prefs: SharedPreferences): Accent {
    if (prefs.contains(OldKeys.KEY_ACCENT2)) {
        var accent = prefs.getInt(OldKeys.KEY_ACCENT2, 5)

        // Blue grey was merged with Light Blue in 2.0.0
        if (accent >= 17) {
            accent = 6
        }

        // Deep Orange was merged with red in 2.0.0
        if (accent == 14) {
            accent = 0
        }

        // Correct accents beyond deep orange (Brown/Grey)
        if (accent > 14) {
            accent--
        }

        prefs.edit {
            putInt(SettingsManager.KEY_ACCENT, accent)
            remove(OldKeys.KEY_ACCENT2)
            apply()
        }
    }

    return Accent(prefs.getInt(SettingsManager.KEY_ACCENT, 5))
}

/** Cache of the old keys used in Auxio. */
private object OldKeys {
    const val KEY_ACCENT2 = "KEY_ACCENT2"
}

/*
 * Copyright (c) 2021 Auxio Project
 * SettingUtils.kt is part of Auxio.
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
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import org.oxycblt.auxio.R

/**
 * Convert an theme integer into an icon that can be used.
 */
@DrawableRes
fun Int.toThemeIcon(): Int {
    return when (this) {
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.drawable.ic_auto
        AppCompatDelegate.MODE_NIGHT_NO -> R.drawable.ic_day
        AppCompatDelegate.MODE_NIGHT_YES -> R.drawable.ic_night

        else -> R.drawable.ic_auto
    }
}

/**
 * Converts an int preference under [key] to [T] through a [convert] function.
 * This is only intended for use for the enums with fromInt functions.
 *
 * NOTE: If one of your constant values uses Int.MIN_VALUE, this function may return an
 * unexpected result.
 */
fun <T> SharedPreferences.getData(key: String, convert: (Int) -> T?): T? {
    return convert(getInt(key, Int.MIN_VALUE))
}

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
 * A verbose shortcut for getString(key, null). Used during string pref migrations
 */
fun SharedPreferences.getStringOrNull(key: String): String? = getString(key, null)

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

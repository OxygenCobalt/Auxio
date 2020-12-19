package org.oxycblt.auxio.settings

import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import org.oxycblt.auxio.R

/**
 * Convert a string representing a theme entry name to an actual theme int that can be used.
 * This is only done because PreferenceFragment does not like int arrays for some...reason.
 * @return The proper theme int for this value.
 */
fun String.toThemeInt(): Int {
    return when (this) {
        SettingsManager.EntryValues.THEME_AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        SettingsManager.EntryValues.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        SettingsManager.EntryValues.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES

        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
}

/**
 * Convert an theme integer into an icon that can be used.
 * @return An icon for this theme.
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

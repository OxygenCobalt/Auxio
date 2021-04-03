package org.oxycblt.auxio.settings

import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import org.oxycblt.auxio.R

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

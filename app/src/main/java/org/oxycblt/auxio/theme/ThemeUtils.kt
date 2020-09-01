package org.oxycblt.auxio.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import org.oxycblt.auxio.R

fun getDayNightTransparency(): Int {
    val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    // Depending on the theme use a different opacity for the divider
    return if (isDark) 45 else 85
}

fun getDeselectedTransparency(color: Int): Int {
    return if (color == R.color.yellow) 100 else 150
}

fun getAccentTransparency(context: Context, color: Int, alpha: Int): Int {
    return ColorUtils.setAlphaComponent(
        ContextCompat.getColor(context, color),
        alpha
    )
}

fun Int.toColor(context: Context): Int {
    return try {
        ContextCompat.getColor(context, this)
    } catch (e: Exception) {
        ContextCompat.getColor(context, android.R.color.white)
    }
}

package org.oxycblt.auxio.ui

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import org.oxycblt.auxio.R

// Functions for managing colors/accents/whatever.

// Pairs of the base accent and its theme
private val ACCENTS = listOf(
    Pair(R.color.red, R.style.Theme_Red), // 0
    Pair(R.color.pink, R.style.Theme_Pink), // 1
    Pair(R.color.purple, R.style.Theme_Purple), // 2
    Pair(R.color.deep_purple, R.style.Theme_DeepPurple), // 3
    Pair(R.color.indigo, R.style.Theme_Indigo), // 4
    Pair(R.color.blue, R.style.Theme_Blue), // 5
    Pair(R.color.light_blue, R.style.Theme_LightBlue), // 6
    Pair(R.color.cyan, R.style.Theme_Cyan), // 7
    Pair(R.color.teal, R.style.Theme_Teal), // 8
    Pair(R.color.green, R.style.Theme_Green), // 9
    Pair(R.color.light_green, R.style.Theme_LightGreen), // 10
    Pair(R.color.lime, R.style.Theme_Lime), // 11
    Pair(R.color.yellow, R.style.Theme_Yellow), // 12
    Pair(R.color.amber, R.style.Theme_Amber), // 13
    Pair(R.color.orange, R.style.Theme_Orange), // 14
    Pair(R.color.deep_orange, R.style.Theme_DeepOrange), // 15
    Pair(R.color.brown, R.style.Theme_Brown), // 16
    Pair(R.color.grey, R.style.Theme_Gray), // 17
    Pair(R.color.blue_grey, R.style.Theme_BlueGrey) // 18
)

val accent = ACCENTS[5]

/**
 * Gets the transparent form of a color.
 * @param context [Context] required to create the color
 * @param color The RESOURCE ID for the color
 * @param alpha The new alpha that wants to be applied
 * @return The new, resolved transparent color
 */
@ColorInt
fun getTransparentAccent(context: Context, @ColorRes color: Int, alpha: Int): Int {
    return ColorUtils.setAlphaComponent(
        color.toColor(context),
        alpha
    )
}

/**
 * Get the inactive alpha of an accent.
 */
@ColorInt
fun getInactiveAlpha(@ColorRes color: Int): Int {
    return if (color == R.color.yellow) 100 else 150
}

/**
 * Resolve a color.
 * @param context [Context] required
 * @return The resolved color, black if the resolving process failed.
 */
@ColorInt
fun Int.toColor(context: Context): Int {
    return try {
        ContextCompat.getColor(context, this)
    } catch (e: Exception) {
        // Default to the emergency color [Black] if the loading fails.
        ContextCompat.getColor(context, android.R.color.black)
    }
}

/**
 * Resolve an attribute into a color.
 * @param context [Context] required
 * @param attr The Resource ID for the attribute
 * @return The resolved color for that attribute. Black if the process failed.
 */
@ColorInt
fun resolveAttr(context: Context, @AttrRes attr: Int): Int {
    // Convert the attribute into its color
    val resolvedAttr = TypedValue().apply {
        context.theme.resolveAttribute(attr, this, true)
    }

    // Then convert it to a proper color
    val color = if (resolvedAttr.resourceId != 0) {
        resolvedAttr.resourceId
    } else {
        resolvedAttr.data
    }

    return color.toColor(context)
}

package org.oxycblt.auxio.ui

import android.content.Context
import android.content.res.Resources
import android.text.Spanned
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.text.toSpanned
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logE
import java.util.Locale

// Functions for managing colors/accents.

/**
 * An array of the base accents and their respective themes.
 */
val ACCENTS = arrayOf(
    Pair(R.color.red, R.style.Theme_Red), // 0
    Pair(R.color.pink, R.style.Theme_Pink), // 1
    Pair(R.color.purple, R.style.Theme_Purple), // 2
    Pair(R.color.deep_purple, R.style.Theme_DeepPurple), // 3
    Pair(R.color.indigo, R.style.Theme_Indigo), // 4
    Pair(R.color.blue, R.style.Theme_Blue), // 5 - Default!
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
    Pair(R.color.blue_grey, R.style.Theme_BlueGrey), // 18
    Pair(R.color.control_color, R.style.Theme_Neutral)
)

/**
 * An array of strings for each accent, use these instead of [Resources.getResourceName] so that
 * the accent names are translated.
 */
private val ACCENT_NAMES = arrayOf(
    R.string.color_label_red, R.string.color_label_pink,
    R.string.color_label_purple, R.string.color_label_deep_purple,
    R.string.color_label_indigo, R.string.color_label_blue,
    R.string.color_label_light_blue, R.string.color_label_cyan,
    R.string.color_label_teal, R.string.color_label_green,
    R.string.color_label_light_green, R.string.color_label_lime,
    R.string.color_label_yellow, R.string.color_label_amber,
    R.string.color_label_orange, R.string.color_label_deep_orange,
    R.string.color_label_brown, R.string.color_label_grey,
    R.string.color_label_blue_grey, R.string.color_label_neutral
)

/**
 * The programmatically accessible accent, reflects the currently set accent.
 */
lateinit var accent: Pair<Int, Int>

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
 * Resolve a color.
 * @param context [Context] required
 * @return The resolved color, black if the resolving process failed.
 */
@ColorInt
fun Int.toColor(context: Context): Int {
    return try {
        ContextCompat.getColor(context, this)
    } catch (e: Resources.NotFoundException) {
        logE("Attempted color load failed.")

        // Default to the emergency color [Black] if the loading fails.
        ContextCompat.getColor(context, android.R.color.black)
    }
}

/**
 * Get the name of an accent.
 * @param context [Context] required
 * @param newAccent The accent the name should be given for.
 * @return The accent name according to the strings for this specific locale.
 */
fun getAccentItemSummary(context: Context, newAccent: Pair<Int, Int>): String {
    val accentIndex = ACCENTS.indexOf(newAccent)

    check(accentIndex != -1) { "Invalid accent given" }

    return context.getString(ACCENT_NAMES[accentIndex])
}

/**
 * Get the name (in bold) and the hex value of a accent.
 * @param context [Context] required
 * @param newAccent Accent to get the information for
 * @return A rendered span with the name in bold + the hex value of the accent.
 */
fun getDetailedAccentSummary(context: Context, newAccent: Pair<Int, Int>): Spanned {
    val name = getAccentItemSummary(context, newAccent)
    val hex = context.getString(accent.first).toUpperCase(Locale.getDefault())

    return context.getString(
        R.string.format_accent_summary,
        name, hex
    ).toSpanned().render()
}

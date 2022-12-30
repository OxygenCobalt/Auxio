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
 
package org.oxycblt.auxio.ui.accent

import android.os.Build
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.util.logW

private val ACCENT_NAMES =
    intArrayOf(
        R.string.clr_red,
        R.string.clr_pink,
        R.string.clr_purple,
        R.string.clr_deep_purple,
        R.string.clr_indigo,
        R.string.clr_blue,
        R.string.clr_deep_blue,
        R.string.clr_cyan,
        R.string.clr_teal,
        R.string.clr_green,
        R.string.clr_deep_green,
        R.string.clr_lime,
        R.string.clr_yellow,
        R.string.clr_orange,
        R.string.clr_brown,
        R.string.clr_grey,
        R.string.clr_dynamic)

private val ACCENT_THEMES =
    intArrayOf(
        R.style.Theme_Auxio_Red,
        R.style.Theme_Auxio_Pink,
        R.style.Theme_Auxio_Purple,
        R.style.Theme_Auxio_DeepPurple,
        R.style.Theme_Auxio_Indigo,
        R.style.Theme_Auxio_Blue,
        R.style.Theme_Auxio_DeepBlue,
        R.style.Theme_Auxio_Cyan,
        R.style.Theme_Auxio_Teal,
        R.style.Theme_Auxio_Green,
        R.style.Theme_Auxio_DeepGreen,
        R.style.Theme_Auxio_Lime,
        R.style.Theme_Auxio_Yellow,
        R.style.Theme_Auxio_Orange,
        R.style.Theme_Auxio_Brown,
        R.style.Theme_Auxio_Grey,
        R.style.Theme_Auxio_App // Dynamic colors are on the base theme
        )

private val ACCENT_BLACK_THEMES =
    intArrayOf(
        R.style.Theme_Auxio_Black_Red,
        R.style.Theme_Auxio_Black_Pink,
        R.style.Theme_Auxio_Black_Purple,
        R.style.Theme_Auxio_Black_DeepPurple,
        R.style.Theme_Auxio_Black_Indigo,
        R.style.Theme_Auxio_Black_Blue,
        R.style.Theme_Auxio_Black_DeepBlue,
        R.style.Theme_Auxio_Black_Cyan,
        R.style.Theme_Auxio_Black_Teal,
        R.style.Theme_Auxio_Black_Green,
        R.style.Theme_Auxio_Black_DeepGreen,
        R.style.Theme_Auxio_Black_Lime,
        R.style.Theme_Auxio_Black_Yellow,
        R.style.Theme_Auxio_Black_Orange,
        R.style.Theme_Auxio_Black_Brown,
        R.style.Theme_Auxio_Black_Grey,
        R.style.Theme_Auxio_Black // Dynamic colors are on the base theme
        )

private val ACCENT_PRIMARY_COLORS =
    intArrayOf(
        R.color.red_primary,
        R.color.pink_primary,
        R.color.purple_primary,
        R.color.deep_purple_primary,
        R.color.indigo_primary,
        R.color.blue_primary,
        R.color.deep_blue_primary,
        R.color.cyan_primary,
        R.color.teal_primary,
        R.color.green_primary,
        R.color.deep_green_primary,
        R.color.lime_primary,
        R.color.yellow_primary,
        R.color.orange_primary,
        R.color.brown_primary,
        R.color.grey_primary,
        R.color.dynamic_primary)

/**
 * The data object for a colored theme to use in the UI. This can be nominally used to gleam some
 * attributes about a given color scheme, but this is not recommended. Attributes are the better
 * option in nearly all cases.
 *
 * @param index The unique number for this particular accent.
 * @author Alexander Capehart (OxygenCobalt)
 */
class Accent private constructor(val index: Int) : Item {
    /** The name of this [Accent]. */
    val name: Int
        get() = ACCENT_NAMES[index]
    /** The theme resource for this accent. */
    val theme: Int
        get() = ACCENT_THEMES[index]
    /**
     * The black theme resource for this accent. Identical to [theme], but with a black background.
     */
    val blackTheme: Int
        get() = ACCENT_BLACK_THEMES[index]
    /** The accent's primary color. */
    val primary: Int
        get() = ACCENT_PRIMARY_COLORS[index]

    override fun equals(other: Any?) = other is Accent && index == other.index

    override fun hashCode() = index.hashCode()

    companion object {
        /**
         * Create a new instance.
         * @param index The unique number for this particular accent.
         * @return A new [Accent] with the specified [index]. If [index] is not within the range of
         * valid accents, [index] will be [DEFAULT] instead.
         */
        fun from(index: Int): Accent {
            if (index !in 0 until MAX) {
                logW("Accent is out of bounds [idx: $index]")
                return Accent(DEFAULT)
            }
            return Accent(index)
        }

        /** The default accent. */
        val DEFAULT =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Use dynamic coloring on devices that support it.
                ACCENT_THEMES.lastIndex
            } else {
                // Use blue everywhere else.
                5
            }

        /** The amount of valid accents. */
        val MAX =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ACCENT_THEMES.size
            } else {
                // Disable the option for a dynamic accent on unsupported devices.
                ACCENT_THEMES.size - 1
            }
    }
}

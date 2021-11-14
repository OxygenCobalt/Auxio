/*
 * Copyright (c) 2021 Auxio Project
 * Accent.kt is part of Auxio.
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

package org.oxycblt.auxio.accent

import org.oxycblt.auxio.R

val ACCENT_COUNT: Int get() = ACCENT_NAMES.size

private val ACCENT_NAMES = arrayOf(
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
)

private val ACCENT_THEMES = arrayOf(
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
)

private val ACCENT_BLACK_THEMES = arrayOf(
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
)

private val ACCENT_PRIMARY_COLORS = arrayOf(
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
)

/**
 * The data object for an accent. In the UI this is known as a "Color Scheme."
 * @property name  The name of this accent
 * @property theme The theme resource for this accent
 * @property blackTheme The black theme resource for this accent
 * @property primary The primary color resource for this accent
 * @author OxygenCobalt
 */
data class Accent(val index: Int) {
    val name: Int get() = ACCENT_NAMES[index]
    val theme: Int get() = ACCENT_THEMES[index]
    val blackTheme: Int get() = ACCENT_BLACK_THEMES[index]
    val primary: Int get() = ACCENT_PRIMARY_COLORS[index]

    companion object {
        @Volatile
        private var CURRENT: Accent? = null

        /**
         * Get the current accent.
         * @return The current accent
         * @throws IllegalStateException When the accent has not been set.
         */
        fun get(): Accent {
            val cur = CURRENT

            if (cur != null) {
                return cur
            }

            error("Accent must be set before retrieving it.")
        }

        /**
         * Set the current accent.
         * @return The new accent
         */
        fun set(accent: Accent): Accent {
            synchronized(this) {
                CURRENT = accent
            }

            return accent
        }
    }
}

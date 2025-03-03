/*
 * Copyright (c) 2022 Auxio Project
 * CoverMode.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image

import org.oxycblt.auxio.IntegerTable

/**
 * Represents the options available for album cover loading.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class CoverMode {
    OFF,
    SAVE_SPACE,
    BALANCED,
    HIGH_QUALITY,
    AS_IS;

    /**
     * The integer representation of this instance.
     *
     * @see fromIntCode
     */
    val intCode: Int
        get() =
            when (this) {
                OFF -> IntegerTable.COVER_MODE_OFF
                SAVE_SPACE -> IntegerTable.COVER_MODE_SAVE_SPACE
                BALANCED -> IntegerTable.COVER_MODE_BALANCED
                HIGH_QUALITY -> IntegerTable.COVER_MODE_HIGH_QUALITY
                AS_IS -> IntegerTable.COVER_MODE_AS_IS
            }

    companion object {
        /**
         * Convert a [CoverMode] integer representation into an instance.
         *
         * @param intCode An integer representation of a [CoverMode]
         * @return The corresponding [CoverMode], or null if the [CoverMode] is invalid.
         * @see CoverMode.intCode
         */
        fun fromIntCode(intCode: Int) =
            when (intCode) {
                IntegerTable.COVER_MODE_OFF -> OFF
                IntegerTable.COVER_MODE_SAVE_SPACE -> SAVE_SPACE
                IntegerTable.COVER_MODE_BALANCED -> BALANCED
                IntegerTable.COVER_MODE_HIGH_QUALITY -> HIGH_QUALITY
                IntegerTable.COVER_MODE_AS_IS -> AS_IS
                else -> null
            }
    }
}

/*
 * Copyright (c) 2022 Auxio Project
 * ActionMode.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback

import org.oxycblt.auxio.IntegerTable

/**
 * Represents a configuration option for what kind of "secondary" action to show in a particular UI
 * context.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class ActionMode {
    /** Use a "Skip next" button for the secondary action. */
    NEXT,
    /** Use a repeat mode button for the secondary action. */
    REPEAT,
    /** Use a shuffle mode button for the secondary action. */
    SHUFFLE;

    /**
     * The integer representation of this instance.
     *
     * @see fromIntCode
     */
    val intCode: Int
        get() =
            when (this) {
                NEXT -> IntegerTable.ACTION_MODE_NEXT
                REPEAT -> IntegerTable.ACTION_MODE_REPEAT
                SHUFFLE -> IntegerTable.ACTION_MODE_SHUFFLE
            }

    companion object {
        /**
         * Convert a [ActionMode] integer representation into an instance.
         *
         * @param intCode An integer representation of a [ActionMode]
         * @return The corresponding [ActionMode], or null if the [ActionMode] is invalid.
         * @see ActionMode.intCode
         */
        fun fromIntCode(intCode: Int) =
            when (intCode) {
                IntegerTable.ACTION_MODE_NEXT -> NEXT
                IntegerTable.ACTION_MODE_REPEAT -> REPEAT
                IntegerTable.ACTION_MODE_SHUFFLE -> SHUFFLE
                else -> null
            }
    }
}

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
 
package org.oxycblt.auxio.playback.state

import org.oxycblt.auxio.IntegerTable

/**
 * Enum that determines the playback repeat mode.
 * @author OxygenCobalt
 */
enum class RepeatMode {
    NONE,
    ALL,
    TRACK;

    /** Increment the mode, e.g from [NONE] to [ALL] */
    fun increment() =
        when (this) {
            NONE -> ALL
            ALL -> TRACK
            TRACK -> NONE
        }

    /** The integer code representing this particular mode. */
    val intCode: Int
        get() =
            when (this) {
                NONE -> IntegerTable.REPEAT_MODE_NONE
                ALL -> IntegerTable.REPEAT_MODE_ALL
                TRACK -> IntegerTable.REPEAT_MODE_TRACK
            }

    companion object {
        /** Convert an int [code] into an instance, or null if it isn't valid. */
        fun fromIntCode(code: Int) =
            when (code) {
                IntegerTable.REPEAT_MODE_NONE -> NONE
                IntegerTable.REPEAT_MODE_ALL -> ALL
                IntegerTable.REPEAT_MODE_TRACK -> TRACK
                else -> null
            }
    }
}

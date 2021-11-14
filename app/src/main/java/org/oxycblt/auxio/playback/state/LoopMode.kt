/*
 * Copyright (c) 2021 Auxio Project
 * LoopMode.kt is part of Auxio.
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

/**
 * Enum that determines the playback repeat mode.
 * @author OxygenCobalt
 */
enum class LoopMode {
    NONE, ALL, TRACK;

    /**
     * Increment the LoopMode, e.g from [NONE] to [ALL]
     */
    fun increment(): LoopMode {
        return when (this) {
            NONE -> ALL
            ALL -> TRACK
            TRACK -> NONE
        }
    }

    /**
     * Convert the LoopMode to an int constant that is saved in PlaybackStateDatabase
     * @return The int constant for this mode
     */
    fun toInt(): Int {
        return when (this) {
            NONE -> CONST_NONE
            ALL -> CONST_ALL
            TRACK -> CONST_TRACK
        }
    }

    companion object {
        private const val CONST_NONE = 0xA100
        private const val CONST_ALL = 0xA101
        private const val CONST_TRACK = 0xA102

        /**
         * Convert an int [constant] into a LoopMode, or null if it isnt valid.
         */
        fun fromInt(constant: Int): LoopMode? {
            return when (constant) {
                CONST_NONE -> NONE
                CONST_ALL -> ALL
                CONST_TRACK -> TRACK

                else -> null
            }
        }
    }
}

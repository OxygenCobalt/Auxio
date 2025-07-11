/*
 * Copyright (c) 2025 Auxio Project
 * LocationMode.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.locations

import org.oxycblt.auxio.IntegerTable

/** Represents the mode for loading music locations. */
enum class LocationMode {
    /** Use Storage Access Framework (file picker) to select specific folders */
    SAF,
    /** Use system MediaStore database to load all music */
    MEDIA_STORE;

    val intCode: Int
        get() =
            when (this) {
                SAF -> IntegerTable.LOCATION_MODE_SAF
                MEDIA_STORE -> IntegerTable.LOCATION_MODE_MEDIA_STORE
            }

    companion object {
        fun fromInt(int: Int): LocationMode? {
            return when (int) {
                IntegerTable.LOCATION_MODE_SAF -> SAF
                IntegerTable.LOCATION_MODE_MEDIA_STORE -> MEDIA_STORE
                else -> null
            }
        }
    }
}

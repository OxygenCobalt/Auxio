/*
 * Copyright (c) 2022 Auxio Project
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
 * @author OxygenCobalt
 */
enum class CoverMode {
    OFF,
    MEDIA_STORE,
    QUALITY;

    val intCode: Int get() = when (this) {
        OFF -> IntegerTable.COVER_MODE_OFF
        MEDIA_STORE -> IntegerTable.COVER_MODE_MEDIA_STORE
        QUALITY -> IntegerTable.COVER_MODE_QUALITY
    }

    companion object {
        fun fromIntCode(intCode: Int) = when (intCode) {
            IntegerTable.COVER_MODE_OFF -> OFF
            IntegerTable.COVER_MODE_MEDIA_STORE -> MEDIA_STORE
            IntegerTable.COVER_MODE_QUALITY -> QUALITY
            else -> null
        }
    }
}

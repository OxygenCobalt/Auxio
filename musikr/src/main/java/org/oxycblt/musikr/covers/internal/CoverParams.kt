/*
 * Copyright (c) 2024 Auxio Project
 * CoverParams.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers.internal

class CoverParams private constructor(val resolution: Int, val quality: Int) {
    override fun hashCode() = 31 * resolution + quality

    override fun equals(other: Any?) =
        other is CoverParams && other.resolution == resolution && other.quality == quality

    companion object {
        fun of(resolution: Int, quality: Int): CoverParams {
            check(resolution > 0) { "Resolution must be positive" }
            check(quality in 0..100) { "Quality must be between 0 and 100" }
            return CoverParams(resolution, quality)
        }
    }
}

/*
 * Copyright (c) 2024 Auxio Project
 * ComposeCoverUtils.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.coil

internal object ComposeCoverDefaults {
    const val COVER_SIZE_PERCENT = 0.60f
    const val GAP_RATIO = 0.04f
    const val MIN_GAP_CORNER_RATIO = 0.35f
    const val MAX_CORNER_RATIO = 0.15f
}

internal fun List<Int>.validatedZOrder(): List<Int> {
    val expected = listOf(0, 1, 2, 3)
    require(size == expected.size && distinct().size == expected.size && containsAll(expected)) {
        "zOrder must be a permutation of [0, 1, 2, 3], got $this"
    }
    return this
}

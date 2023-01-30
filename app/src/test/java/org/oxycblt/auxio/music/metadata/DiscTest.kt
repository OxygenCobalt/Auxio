/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.metadata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscTest {
    @Test
    fun disc_compare() {
        val a = Disc(1, "Part I")
        val b = Disc(2, "Part II")
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun disc_equals_correct() {
        val a = Disc(1, "Part I")
        val b = Disc(1, "Part I")
        assertTrue(a == b)
        assertTrue(a.hashCode() == b.hashCode())
    }

    @Test
    fun disc_equals_inconsistentNames() {
        val a = Disc(1, "Part I")
        val b = Disc(1, null)
        assertTrue(a == b)
        assertTrue(a.hashCode() == b.hashCode())
    }
}

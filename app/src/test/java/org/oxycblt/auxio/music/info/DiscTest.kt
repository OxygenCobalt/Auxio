/*
 * Copyright (c) 2023 Auxio Project
 * DiscTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.info

import org.junit.Assert.assertEquals
import org.junit.Test

class DiscTest {
    @Test
    fun disc_equals_byNum() {
        val a = Disc(0, null)
        val b = Disc(0, null)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun disc_equals_bySubtitle() {
        val a = Disc(0, "z subtitle")
        val b = Disc(0, "a subtitle")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun disc_compareTo_byNum() {
        val a = Disc(0, null)
        val b = Disc(1, null)
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun disc_compareTo_bySubtitle() {
        val a = Disc(0, "z subtitle")
        val b = Disc(1, "a subtitle")
        assertEquals(-1, a.compareTo(b))
    }
}

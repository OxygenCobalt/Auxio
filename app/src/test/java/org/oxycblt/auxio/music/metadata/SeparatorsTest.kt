/*
 * Copyright (c) 2023 Auxio Project
 * SeparatorsTest.kt is part of Auxio.
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
import org.junit.Test

class SeparatorsTest {
    @Test
    fun separators_split_withString_withSingleChar() {
        assertEquals(listOf("a", "b", "c"), Separators.from(",").split(listOf("a,b,c")))
    }

    @Test
    fun separators_split_withMultiple_withSingleChar() {
        assertEquals(listOf("a,b", "c", "d"), Separators.from(",").split(listOf("a,b", "c", "d")))
    }

    @Test
    fun separators_split_withString_withMultipleChar() {
        assertEquals(
            listOf("a", "b", "c", "d", "e", "f"),
            Separators.from(",;/+&").split(listOf("a,b;c/d+e&f")))
    }

    @Test
    fun separators_split_withList_withMultipleChar() {
        assertEquals(
            listOf("a,b;c/d", "e&f"), Separators.from(",;/+&").split(listOf("a,b;c/d", "e&f")))
    }
}

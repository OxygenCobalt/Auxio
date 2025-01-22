/*
 * Copyright (c) 2023 Auxio Project
 * TagFieldTest.kt is part of Auxio.
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
 
package org.oxycblt.musikr.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TagFieldTest {
    @Test
    fun splitEscaped_correct() {
        assertEquals(listOf("a", "b", "c"), "a,b,c".splitEscaped { it == ',' })
    }

    @Test
    fun splitEscaped_escaped() {
        assertEquals(listOf("a,b", "c"), "a\\,b,c".splitEscaped { it == ',' })
    }

    @Test
    fun splitEscaped_whitespace() {
        assertEquals(listOf("a ", " b", " c ", "  "), "a , b, c ,  ".splitEscaped { it == ',' })
    }

    @Test
    fun splitEscaped_escapedWhitespace() {
        assertEquals(listOf("a , b", " c ", "  "), ("a \\, b, c ,  ".splitEscaped { it == ',' }))
    }

    @Test
    fun correctWhitespace_stringCorrect() {
        assertEquals(
            "asymptotic self-improvement",
            " asymptotic self-improvement  ".correctWhitespace(),
        )
    }

    @Test
    fun correctWhitespace_stringOopsAllWhitespace() {
        assertEquals(null, "".correctWhitespace())
        assertEquals(null, "      ".correctWhitespace())
    }

    @Test
    fun correctWhitespace_listCorrect() {
        assertEquals(
            listOf("asymptotic self-improvement", "daemons never stop", "tcp phagocyte"),
            listOf(" asymptotic self-improvement  ", "  daemons never stop", "tcp phagocyte")
                .correctWhitespace(),
        )
    }

    @Test
    fun correctWhitespace_listOopsAllWhitespace() {
        assertEquals(
            listOf("tcp phagocyte"), listOf("      ", "", "  tcp phagocyte").correctWhitespace())
    }
}

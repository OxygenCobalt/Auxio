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
 
package org.oxycblt.auxio.music.parsing

import org.junit.Assert.assertEquals
import org.junit.Test

class ParsingUtilTest {
    @Test
    fun splitEscaped_correct() {
        assertEquals("a,b,c".splitEscaped { it == ',' }, listOf("a", "b", "c"))
    }

    @Test
    fun splitEscaped_escaped() {
        assertEquals("a\\,b,c".splitEscaped { it == ',' }, listOf("a,b", "c"))
    }

    @Test
    fun splitEscaped_whitespace() {
        assertEquals("a , b, c ,  ".splitEscaped { it == ',' }, listOf("a ", " b", " c ", "  "))
    }

    @Test
    fun splitEscaped_escapedWhitespace() {
        assertEquals("a \\, b, c ,  ".splitEscaped { it == ',' }, listOf("a , b", " c ", "  "))
    }

    @Test
    fun correctWhitespace_stringCorrect() {
        assertEquals(
            " asymptotic self-improvement  ".correctWhitespace(), "asymptotic self-improvement")
    }

    @Test
    fun correctWhitespace_stringOopsAllWhitespace() {
        assertEquals("      ".correctWhitespace(), null)
    }

    @Test
    fun correctWhitespace_listCorrect() {
        assertEquals(
            listOf(" asymptotic self-improvement  ", "  daemons never stop", "tcp phagocyte")
                .correctWhitespace(),
            listOf("asymptotic self-improvement", "daemons never stop", "tcp phagocyte"))
    }

    @Test
    fun correctWhitespace_listOopsAllWhitespacE() {
        assertEquals(
            listOf("      ", "", "  tcp phagocyte").correctWhitespace(), listOf("tcp phagocyte"))
    }

    @Test
    fun parseId3v2Position_correct() {
        assertEquals("16/32".parseId3v2Position(), 16)
    }

    @Test
    fun parseId3v2Position_noTotal() {
        assertEquals("16".parseId3v2Position(), 16)
    }

    @Test
    fun parseId3v2Position_wack() {
        assertEquals("16/".parseId3v2Position(), 16)
    }
}

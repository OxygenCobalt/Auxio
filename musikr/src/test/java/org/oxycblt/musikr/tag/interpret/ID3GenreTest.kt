/*
 * Copyright (c) 2023 Auxio Project
 * ID3GenreTest.kt is part of Auxio.
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
 
package org.oxycblt.musikr.tag.interpret

import org.junit.Assert.assertEquals
import org.junit.Test

class ID3GenreTest {
    @Test
    fun parseId3v2Genre_multi() {
        assertEquals(
            listOf("Post-Rock", "Shoegaze", "Glitch"),
            listOf("Post-Rock", "Shoegaze", "Glitch").parseId3GenreNames())
    }

    @Test
    fun parseId3v2Genre_multiId3v1() {
        assertEquals(
            listOf("Post-Rock", "Shoegaze", "Glitch"),
            listOf("176", "178", "Glitch").parseId3GenreNames())
    }

    @Test
    fun parseId3v2Genre_wackId3() {
        assertEquals(null, listOf("2941").parseId3GenreNames())
    }

    @Test
    fun parseId3v2Genre_singleId3v23() {
        assertEquals(
            listOf("Post-Rock", "Shoegaze", "Remix", "Cover", "Glitch"),
            listOf("(176)(178)(RX)(CR)Glitch").parseId3GenreNames())
    }

    @Test
    fun parseId3v2Genre_singleId3v1() {
        assertEquals(listOf("Post-Rock"), listOf("176").parseId3GenreNames())
    }
}

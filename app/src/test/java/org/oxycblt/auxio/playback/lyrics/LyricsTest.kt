/*
 * Copyright (c) 2026 Auxio Project
 * LyricsTest.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.lyrics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LyricsTest {
    @Test
    fun `parse drops a real LRC metadata tag`() {
        val lyrics = LrcParser.parse("[ar:Some Artist]\n[00:01.00]First line")
        assertEquals(listOf(LyricsLine(1_000, "First line")), lyrics?.lines)
    }

    @Test
    fun `parse keeps a plain bracketed section header, not mistaking it for metadata`() {
        val lyrics = LrcParser.parse("[Chorus]\n[Verse 1]\nSome lyrics")
        assertEquals(
            listOf(
                LyricsLine(null, "[Chorus]"),
                LyricsLine(null, "[Verse 1]"),
                LyricsLine(null, "Some lyrics"),
            ),
            lyrics?.lines)
    }

    @Test
    fun `parse returns null for blank input`() {
        assertNull(LrcParser.parse(null))
        assertNull(LrcParser.parse("   \n  "))
    }
}

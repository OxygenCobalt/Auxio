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
import org.junit.Test
import org.oxycblt.auxio.music.FakeMusicSettings

class TagUtilTest {
    @Test
    fun parseMultiValue_single() {
        assertEquals(listOf("a", "b", "c"), listOf("a,b,c").parseMultiValue(TestMusicSettings(",")))
    }

    @Test
    fun parseMultiValue_many() {
        assertEquals(
            listOf("a", "b", "c"), listOf("a", "b", "c").parseMultiValue(TestMusicSettings(",")))
    }

    @Test
    fun parseMultiValue_several() {
        assertEquals(
            listOf("a", "b", "c", "d", "e", "f"),
            listOf("a,b;c/d+e&f").parseMultiValue(TestMusicSettings(",;/+&")))
    }

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

    @Test
    fun parseId3v2PositionField_correct() {
        assertEquals(16, "16/32".parseId3v2PositionField())
        assertEquals(16, "16".parseId3v2PositionField())
    }

    @Test
    fun parseId3v2PositionField_zeroed() {
        assertEquals(null, "0".parseId3v2PositionField())
        assertEquals(0, "0/32".parseId3v2PositionField())
    }

    @Test
    fun parseId3v2PositionField_wack() {
        assertEquals(16, "16/".parseId3v2PositionField())
        assertEquals(null, "a".parseId3v2PositionField())
        assertEquals(null, "a/b".parseId3v2PositionField())
    }

    @Test
    fun parseVorbisPositionField_correct() {
        assertEquals(16, parseVorbisPositionField("16", "32"))
        assertEquals(16, parseVorbisPositionField("16", null))
    }

    @Test
    fun parseVorbisPositionField_zeroed() {
        assertEquals(null, parseVorbisPositionField("0", null))
        assertEquals(0, parseVorbisPositionField("0", "32"))
    }

    @Test
    fun parseVorbisPositionField_wack() {
        assertEquals(null, parseVorbisPositionField("a", null))
        assertEquals(null, parseVorbisPositionField("a", "b"))
    }

    @Test
    fun parseId3v2Genre_multi() {
        assertEquals(
            listOf("Post-Rock", "Shoegaze", "Glitch"),
            listOf("Post-Rock", "Shoegaze", "Glitch").parseId3GenreNames(TestMusicSettings(",")))
    }

    @Test
    fun parseId3v2Genre_multiId3v1() {
        assertEquals(
            listOf("Post-Rock", "Shoegaze", "Glitch"),
            listOf("176", "178", "Glitch").parseId3GenreNames(TestMusicSettings(",")))
    }

    @Test
    fun parseId3v2Genre_wackId3() {
        assertEquals(listOf("2941"), listOf("2941").parseId3GenreNames(TestMusicSettings(",")))
    }

    @Test
    fun parseId3v2Genre_singleId3v23() {
        assertEquals(
            listOf("Post-Rock", "Shoegaze", "Remix", "Cover", "Glitch"),
            listOf("(176)(178)(RX)(CR)Glitch").parseId3GenreNames(TestMusicSettings(",")))
    }

    @Test
    fun parseId3v2Genre_singleSeparated() {
        assertEquals(
            listOf("Post-Rock", "Shoegaze", "Glitch"),
            listOf("Post-Rock, Shoegaze, Glitch").parseId3GenreNames(TestMusicSettings(",")))
    }

    @Test
    fun parsId3v2Genre_singleId3v1() {
        assertEquals(listOf("Post-Rock"), listOf("176").parseId3GenreNames(TestMusicSettings(",")))
    }

    class TestMusicSettings(private val separators: String) : FakeMusicSettings() {
        override var multiValueSeparators: String
            get() = separators
            set(_) = throw NotImplementedError()
    }
}

/*
 * Copyright (c) 2026 Auxio Project
 * GenreParserTest.kt is part of Auxio.
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

class GenreParserTest {
    @Test
    fun parseGenreNames_splitsApprovedGenreDelimiters() {
        assertGenres(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            "Pop, Rock, Pop-punk, Emo",
        )
        assertGenres(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            "Pop; Rock; Pop-punk; Emo",
        )
        assertGenres(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            "Pop/Rock/Pop-punk/Emo",
        )
        assertGenres(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            "Pop | Rock | Pop-punk | Emo",
        )
        assertGenres(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            "Pop\\Rock\\Pop-punk\\Emo",
        )
        assertGenres(
            listOf("Pop", "Rock", "Pop-punk", "Emo", "Indie"),
            "Pop, Rock/Pop-punk; Emo | Indie",
        )
        assertGenres(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            " Pop,  Rock / Pop-punk ; Emo ",
        )
    }

    @Test
    fun parseGenreNames_removesEmptyValuesAndDeduplicatesCaseInsensitively() {
        assertGenres(listOf("Pop", "Rock"), "Pop,,Rock")
        assertGenres(listOf("Pop", "Rock"), "Pop//Rock")
        assertGenres(listOf("Pop", "Rock"), "Pop||Rock")
        assertGenres(listOf("Pop"), "Pop; pop; POP")
        assertGenres(listOf("Pop"), "Pop / pop / POP")
    }

    @Test
    fun parseGenreNames_splitsEscapedApprovedDelimiters() {
        assertGenres(listOf("Pop", "Rock"), "Pop\\, Rock")
        assertGenres(listOf("Pop", "Rock"), "Pop\\; Rock")
        assertGenres(listOf("Pop", "Rock"), "Pop\\/Rock")
        assertGenres(listOf("Pop", "Rock"), "Pop\\|Rock")
        assertGenres(listOf("Pop", "Rock"), "Pop\\\\Rock")
    }

    @Test
    fun parseGenreNames_keepsExcludedDelimitersLiteral() {
        assertGenres(listOf("R&B"), "R&B")
        assertGenres(listOf("Rock & Roll"), "Rock & Roll")
        assertGenres(listOf("Drum & Bass"), "Drum & Bass")
        assertGenres(listOf("Drum and Bass"), "Drum and Bass")
        assertGenres(listOf("Pop-punk"), "Pop-punk")
        assertGenres(listOf("Alt-rock"), "Alt-rock")
        assertGenres(listOf("Post-hardcore"), "Post-hardcore")
        assertGenres(listOf("Indie + Alternative"), "Indie + Alternative")
    }

    @Test
    fun parseGenreNames_normalizesStructuredAndId3GenreValues() {
        assertGenres(
            listOf("Rock & Roll", "R&B", "Soul", "Pop-punk"),
            "Rock & Roll",
            "R&B/Soul",
            "Pop-punk",
        )
        assertGenres(
            listOf("Post-Rock", "Shoegaze", "R&B", "Soul"),
            "176",
            "178",
            "R&B/Soul",
        )
    }

    @Test
    fun parseGenreNames_handlesSingleGenresAndBlankInputs() {
        assertGenres(listOf("Rock"), "Rock")
        assertGenres(emptyList(), "", "   ")
    }

    private fun assertGenres(expected: List<String>, vararg rawGenreNames: String) {
        assertEquals(expected, parseGenreNames(rawGenreNames.toList()))
    }
}

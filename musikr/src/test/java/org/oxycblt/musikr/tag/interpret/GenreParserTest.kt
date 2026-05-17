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

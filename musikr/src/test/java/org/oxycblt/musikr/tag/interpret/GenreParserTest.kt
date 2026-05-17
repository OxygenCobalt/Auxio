package org.oxycblt.musikr.tag.interpret

import org.junit.Assert.assertEquals
import org.junit.Test

class GenreParserTest {
    @Test
    fun parseGenreNames_delimitedValues_areSplitIntoDistinctGenres() {
        assertEquals(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            parseGenreNames(listOf("Pop, Rock, Pop-punk, Emo"), Separators.from("")),
        )
        assertEquals(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            parseGenreNames(listOf("Pop; Rock; Pop-punk; Emo"), Separators.from("")),
        )
        assertEquals(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            parseGenreNames(listOf("Pop; Rock, Pop-punk; Emo"), Separators.from("")),
        )
    }

    @Test
    fun parseGenreNames_normalizesWhitespaceAndRemovesEmptyValues() {
        assertEquals(
            listOf("Pop", "Rock", "Pop-punk", "Emo"),
            parseGenreNames(listOf(" Pop,  Rock , Pop-punk , Emo "), Separators.from("")),
        )
        assertEquals(listOf("Pop", "Rock"), parseGenreNames(listOf("Pop,,Rock"), Separators.from("")))
    }

    @Test
    fun parseGenreNames_deduplicatesCaseInsensitivelyAndKeepsFirstCasing() {
        assertEquals(listOf("Pop"), parseGenreNames(listOf("Pop; pop; POP"), Separators.from("")))
    }

    @Test
    fun parseGenreNames_preservesNonDelimiterGenreNames() {
        assertEquals(
            listOf("Hip Hop", "R&B", "Drum and Bass"),
            parseGenreNames(listOf("Hip Hop, R&B, Drum and Bass"), Separators.from("")),
        )
    }

    @Test
    fun parseGenreNames_handlesBlankInputsAndUnknownLikeData() {
        assertEquals(emptyList(), parseGenreNames(listOf("", "   "), Separators.from("")))
    }
}

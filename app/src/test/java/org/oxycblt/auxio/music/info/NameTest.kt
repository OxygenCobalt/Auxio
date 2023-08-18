/*
 * Copyright (c) 2023 Auxio Project
 * NameTest.kt is part of Auxio.
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

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.oxycblt.auxio.music.MusicSettings

class NameTest {
    private fun mockIntelligentSorting(enabled: Boolean) =
        mockk<MusicSettings>() { every { intelligentSorting } returns enabled }

    @Test
    fun name_from_simple_withoutPunct() {
        val name = Name.Known.from("Loveless", null, mockIntelligentSorting(false))
        assertEquals("Loveless", name.raw)
        assertEquals(null, name.sort)
        assertEquals("L", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Loveless", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_from_simple_withPunct() {
        val name = Name.Known.from("alt-J", null, mockIntelligentSorting(false))
        assertEquals("alt-J", name.raw)
        assertEquals(null, name.sort)
        assertEquals("A", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("altJ", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_from_simple_oopsAllPunct() {
        val name = Name.Known.from("!!!", null, mockIntelligentSorting(false))
        assertEquals("!!!", name.raw)
        assertEquals(null, name.sort)
        assertEquals("!", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("!!!", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_from_simple_spacedPunct() {
        val name = Name.Known.from("& Yet & Yet", null, mockIntelligentSorting(false))
        assertEquals("& Yet & Yet", name.raw)
        assertEquals(null, name.sort)
        assertEquals("Y", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Yet  Yet", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_from_simple_withSort() {
        val name = Name.Known.from("The Smile", "Smile", mockIntelligentSorting(false))
        assertEquals("The Smile", name.raw)
        assertEquals("Smile", name.sort)
        assertEquals("S", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Smile", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withoutArticle_withoutNumerics() {
        val name = Name.Known.from("Loveless", null, mockIntelligentSorting(true))
        assertEquals("Loveless", name.raw)
        assertEquals(null, name.sort)
        assertEquals("L", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Loveless", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withoutArticle_withSpacedStartNumerics() {
        val name = Name.Known.from("15 Step", null, mockIntelligentSorting(true))
        assertEquals("15 Step", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("15", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("Step", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, second.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withoutArticle_withPackedStartNumerics() {
        val name = Name.Known.from("23Kid", null, mockIntelligentSorting(true))
        assertEquals("23Kid", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("23", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("Kid", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, second.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withoutArticle_withSpacedMiddleNumerics() {
        val name = Name.Known.from("Foo 1 2 Bar", null, mockIntelligentSorting(true))
        assertEquals("Foo 1 2 Bar", name.raw)
        assertEquals(null, name.sort)
        assertEquals("F", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("1", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, second.type)
        val third = name.sortTokens[2]
        assertEquals(" ", third.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, third.type)
        val fourth = name.sortTokens[3]
        assertEquals("2", fourth.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, fourth.type)
        val fifth = name.sortTokens[4]
        assertEquals("Bar", fifth.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, fifth.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withoutArticle_withPackedMiddleNumerics() {
        val name = Name.Known.from("Foo12Bar", null, mockIntelligentSorting(true))
        assertEquals("Foo12Bar", name.raw)
        assertEquals(null, name.sort)
        assertEquals("F", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("12", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, second.type)
        val third = name.sortTokens[2]
        assertEquals("Bar", third.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, third.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withoutArticle_withSpacedEndNumerics() {
        val name = Name.Known.from("Foo 1", null, mockIntelligentSorting(true))
        assertEquals("Foo 1", name.raw)
        assertEquals(null, name.sort)
        assertEquals("F", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("1", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, second.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withoutArticle_withPackedEndNumerics() {
        val name = Name.Known.from("Error404", null, mockIntelligentSorting(true))
        assertEquals("Error404", name.raw)
        assertEquals(null, name.sort)
        assertEquals("E", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Error", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("404", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, second.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withThe_withoutNumerics() {
        val name = Name.Known.from("The National Anthem", null, mockIntelligentSorting(true))
        assertEquals("The National Anthem", name.raw)
        assertEquals(null, name.sort)
        assertEquals("N", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("National Anthem", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withAn_withoutNumerics() {
        val name = Name.Known.from("An Eagle in Your Mind", null, mockIntelligentSorting(true))
        assertEquals("An Eagle in Your Mind", name.raw)
        assertEquals(null, name.sort)
        assertEquals("E", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Eagle in Your Mind", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_withA_withoutNumerics() {
        val name = Name.Known.from("A Song For Our Fathers", null, mockIntelligentSorting(true))
        assertEquals("A Song For Our Fathers", name.raw)
        assertEquals(null, name.sort)
        assertEquals("S", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Song For Our Fathers", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_from_intelligent_withPunct_withoutArticle_withoutNumerics() {
        val name = Name.Known.from("alt-J", null, mockIntelligentSorting(true))
        assertEquals("alt-J", name.raw)
        assertEquals(null, name.sort)
        assertEquals("A", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("altJ", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_from_intelligent_oopsAllPunct_withoutArticle_withoutNumerics() {
        val name = Name.Known.from("!!!", null, mockIntelligentSorting(true))
        assertEquals("!!!", name.raw)
        assertEquals(null, name.sort)
        assertEquals("!", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("!!!", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_from_intelligent_withoutPunct_shortArticle_withNumerics() {
        val name = Name.Known.from("the 1", null, mockIntelligentSorting(true))
        assertEquals("the 1", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("1", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, first.type)
    }

    @Test
    fun name_from_intelligent_spacedPunct_withoutArticle_withoutNumerics() {
        val name = Name.Known.from("& Yet & Yet", null, mockIntelligentSorting(true))
        assertEquals("& Yet & Yet", name.raw)
        assertEquals(null, name.sort)
        assertEquals("Y", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Yet  Yet", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_from_intelligent_withPunct_withoutArticle_withNumerics() {
        val name = Name.Known.from("Design : 2 : 3", null, mockIntelligentSorting(true))
        assertEquals("Design : 2 : 3", name.raw)
        assertEquals(null, name.sort)
        assertEquals("D", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Design", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("2", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, second.type)
        val third = name.sortTokens[2]
        assertEquals("  ", third.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, third.type)
        val fourth = name.sortTokens[3]
        assertEquals("3", fourth.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, fourth.type)
    }

    @Test
    fun name_from_intelligent_oopsAllPunct_withoutArticle_oopsAllNumerics() {
        val name = Name.Known.from("2 + 2 = 5", null, mockIntelligentSorting(true))
        assertEquals("2 + 2 = 5", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("2", first.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("  ", second.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, second.type)
        val third = name.sortTokens[2]
        assertEquals("2", third.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, third.type)
        val fourth = name.sortTokens[3]
        assertEquals("  ", fourth.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, fourth.type)
        val fifth = name.sortTokens[4]
        assertEquals("5", fifth.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.NUMERIC, fifth.type)
    }

    @Test
    fun name_from_intelligent_withSort() {
        val name = Name.Known.from("The Smile", "Smile", mockIntelligentSorting(true))
        assertEquals("The Smile", name.raw)
        assertEquals("Smile", name.sort)
        assertEquals("S", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Smile", only.collationKey.sourceString)
        assertEquals(Name.Known.SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_equals_simple() {
        val a = Name.Known.from("The Same", "Same", mockIntelligentSorting(false))
        val b = Name.Known.from("The Same", "Same", mockIntelligentSorting(false))
        assertEquals(a, b)
    }

    @Test
    fun name_equals_differentSort() {
        val a = Name.Known.from("The Same", "Same", mockIntelligentSorting(false))
        val b = Name.Known.from("The Same", null, mockIntelligentSorting(false))
        assertNotEquals(a, b)
        assertNotEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun name_equals_intelligent_differentTokens() {
        val a = Name.Known.from("The Same", "Same", mockIntelligentSorting(true))
        val b = Name.Known.from("Same", "Same", mockIntelligentSorting(true))
        assertNotEquals(a, b)
        assertNotEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun name_compareTo_simple_withoutSort_withoutArticle_withoutNumeric() {
        val a = Name.Known.from("A", null, mockIntelligentSorting(false))
        val b = Name.Known.from("B", null, mockIntelligentSorting(false))
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_simple_withoutSort_withArticle_withoutNumeric() {
        val a = Name.Known.from("A Brain in a Bottle", null, mockIntelligentSorting(false))
        val b = Name.Known.from("Acid Rain", null, mockIntelligentSorting(false))
        val c = Name.Known.from("Boralis / Contrastellar", null, mockIntelligentSorting(false))
        val d = Name.Known.from("Breathe In", null, mockIntelligentSorting(false))
        assertEquals(-1, a.compareTo(b))
        assertEquals(-1, a.compareTo(c))
        assertEquals(-1, a.compareTo(d))
    }

    @Test
    fun name_compareTo_simple_withSort_withoutArticle_withNumeric() {
        val a = Name.Known.from("15 Step", null, mockIntelligentSorting(false))
        val b = Name.Known.from("128 Harps", null, mockIntelligentSorting(false))
        val c = Name.Known.from("1969", null, mockIntelligentSorting(false))
        assertEquals(1, a.compareTo(b))
        assertEquals(-1, a.compareTo(c))
    }

    @Test
    fun name_compareTo_simple_withPartialSort() {
        val a = Name.Known.from("A", "C", mockIntelligentSorting(false))
        val b = Name.Known.from("B", null, mockIntelligentSorting(false))
        assertEquals(1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_simple_withSort() {
        val a = Name.Known.from("D", "A", mockIntelligentSorting(false))
        val b = Name.Known.from("C", "B", mockIntelligentSorting(false))
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withoutArticle_withoutNumeric() {
        val a = Name.Known.from("A", null, mockIntelligentSorting(true))
        val b = Name.Known.from("B", null, mockIntelligentSorting(true))
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withArticle_withoutNumeric() {
        val a = Name.Known.from("A Brain in a Bottle", null, mockIntelligentSorting(true))
        val b = Name.Known.from("Acid Rain", null, mockIntelligentSorting(true))
        val c = Name.Known.from("Boralis / Contrastellar", null, mockIntelligentSorting(true))
        val d = Name.Known.from("Breathe In", null, mockIntelligentSorting(true))
        assertEquals(1, a.compareTo(b))
        assertEquals(1, a.compareTo(c))
        assertEquals(-1, a.compareTo(d))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withoutArticle_withNumeric() {
        val a = Name.Known.from("15 Step", null, mockIntelligentSorting(true))
        val b = Name.Known.from("128 Harps", null, mockIntelligentSorting(true))
        val c = Name.Known.from("1969", null, mockIntelligentSorting(true))
        assertEquals(-1, a.compareTo(b))
        assertEquals(-1, b.compareTo(c))
        assertEquals(-2, a.compareTo(c))
    }

    @Test
    fun name_compareTo_intelligent_withPartialSort_withoutArticle_withoutNumeric() {
        val a = Name.Known.from("A", "C", mockIntelligentSorting(false))
        val b = Name.Known.from("B", null, mockIntelligentSorting(false))
        assertEquals(1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withSort_withoutArticle_withoutNumeric() {
        val a = Name.Known.from("D", "A", mockIntelligentSorting(true))
        val b = Name.Known.from("C", "B", mockIntelligentSorting(true))
        assertEquals(-1, a.compareTo(b))
    }
}

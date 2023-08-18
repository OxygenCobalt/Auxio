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
import org.junit.Assert.assertTrue
import org.junit.Test
import org.oxycblt.auxio.music.MusicSettings

class NameTest {
    @Test
    fun name_simple_from_settings() {
        val musicSettings = mockk<MusicSettings> { every { intelligentSorting } returns false }
        assertTrue(Name.Known.Factory.from(musicSettings) is SimpleKnownName.Factory)
    }

    @Test
    fun name_intelligent_from_settings() {
        val musicSettings = mockk<MusicSettings> { every { intelligentSorting } returns true }
        assertTrue(Name.Known.Factory.from(musicSettings) is IntelligentKnownName.Factory)
    }

    @Test
    fun name_simple_withoutPunct() {
        val name = SimpleKnownName("Loveless", null)
        assertEquals("Loveless", name.raw)
        assertEquals(null, name.sort)
        assertEquals("L", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Loveless", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_simple_withPunct() {
        val name = SimpleKnownName("alt-J", null)
        assertEquals("alt-J", name.raw)
        assertEquals(null, name.sort)
        assertEquals("A", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("altJ", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_simple_oopsAllPunct() {
        val name = SimpleKnownName("!!!", null)
        assertEquals("!!!", name.raw)
        assertEquals(null, name.sort)
        assertEquals("!", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("!!!", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_simple_spacedPunct() {
        val name = SimpleKnownName("& Yet & Yet", null)
        assertEquals("& Yet & Yet", name.raw)
        assertEquals(null, name.sort)
        assertEquals("Y", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Yet  Yet", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_simple_withSort() {
        val name = SimpleKnownName("The Smile", "Smile")
        assertEquals("The Smile", name.raw)
        assertEquals("Smile", name.sort)
        assertEquals("S", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Smile", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withoutNumerics() {
        val name = IntelligentKnownName("Loveless", null)
        assertEquals("Loveless", name.raw)
        assertEquals(null, name.sort)
        assertEquals("L", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Loveless", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withSpacedStartNumerics() {
        val name = IntelligentKnownName("15 Step", null)
        assertEquals("15 Step", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("15", first.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("Step", second.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withPackedStartNumerics() {
        val name = IntelligentKnownName("23Kid", null)
        assertEquals("23Kid", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("23", first.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("Kid", second.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withSpacedMiddleNumerics() {
        val name = IntelligentKnownName("Foo 1 2 Bar", null)
        assertEquals("Foo 1 2 Bar", name.raw)
        assertEquals(null, name.sort)
        assertEquals("F", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("1", second.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, second.type)
        val third = name.sortTokens[2]
        assertEquals(" ", third.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, third.type)
        val fourth = name.sortTokens[3]
        assertEquals("2", fourth.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, fourth.type)
        val fifth = name.sortTokens[4]
        assertEquals("Bar", fifth.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, fifth.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withPackedMiddleNumerics() {
        val name = IntelligentKnownName("Foo12Bar", null)
        assertEquals("Foo12Bar", name.raw)
        assertEquals(null, name.sort)
        assertEquals("F", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("12", second.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, second.type)
        val third = name.sortTokens[2]
        assertEquals("Bar", third.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, third.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withSpacedEndNumerics() {
        val name = IntelligentKnownName("Foo 1", null)
        assertEquals("Foo 1", name.raw)
        assertEquals(null, name.sort)
        assertEquals("F", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("1", second.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withPackedEndNumerics() {
        val name = IntelligentKnownName("Error404", null)
        assertEquals("Error404", name.raw)
        assertEquals(null, name.sort)
        assertEquals("E", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Error", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("404", second.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withThe_withoutNumerics() {
        val name = IntelligentKnownName("The National Anthem", null)
        assertEquals("The National Anthem", name.raw)
        assertEquals(null, name.sort)
        assertEquals("N", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("National Anthem", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withAn_withoutNumerics() {
        val name = IntelligentKnownName("An Eagle in Your Mind", null)
        assertEquals("An Eagle in Your Mind", name.raw)
        assertEquals(null, name.sort)
        assertEquals("E", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Eagle in Your Mind", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withA_withoutNumerics() {
        val name = IntelligentKnownName("A Song For Our Fathers", null)
        assertEquals("A Song For Our Fathers", name.raw)
        assertEquals(null, name.sort)
        assertEquals("S", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Song For Our Fathers", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withPunct_withoutArticle_withoutNumerics() {
        val name = IntelligentKnownName("alt-J", null)
        assertEquals("alt-J", name.raw)
        assertEquals(null, name.sort)
        assertEquals("A", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("altJ", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_oopsAllPunct_withoutArticle_withoutNumerics() {
        val name = IntelligentKnownName("!!!", null)
        assertEquals("!!!", name.raw)
        assertEquals(null, name.sort)
        assertEquals("!", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("!!!", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_withoutPunct_shortArticle_withNumerics() {
        val name = IntelligentKnownName("the 1", null)
        assertEquals("the 1", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("1", first.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, first.type)
    }

    @Test
    fun name_intelligent_spacedPunct_withoutArticle_withoutNumerics() {
        val name = IntelligentKnownName("& Yet & Yet", null)
        assertEquals("& Yet & Yet", name.raw)
        assertEquals(null, name.sort)
        assertEquals("Y", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Yet  Yet", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withPunct_withoutArticle_withNumerics() {
        val name = IntelligentKnownName("Design : 2 : 3", null)
        assertEquals("Design : 2 : 3", name.raw)
        assertEquals(null, name.sort)
        assertEquals("D", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("Design", first.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("2", second.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, second.type)
        val third = name.sortTokens[2]
        assertEquals("  ", third.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, third.type)
        val fourth = name.sortTokens[3]
        assertEquals("3", fourth.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, fourth.type)
    }

    @Test
    fun name_intelligent_oopsAllPunct_withoutArticle_oopsAllNumerics() {
        val name = IntelligentKnownName("2 + 2 = 5", null)
        assertEquals("2 + 2 = 5", name.raw)
        assertEquals(null, name.sort)
        assertEquals("#", name.thumb)
        val first = name.sortTokens[0]
        assertEquals("2", first.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, first.type)
        val second = name.sortTokens[1]
        assertEquals("  ", second.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, second.type)
        val third = name.sortTokens[2]
        assertEquals("2", third.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, third.type)
        val fourth = name.sortTokens[3]
        assertEquals("  ", fourth.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, fourth.type)
        val fifth = name.sortTokens[4]
        assertEquals("5", fifth.collationKey.sourceString)
        assertEquals(SortToken.Type.NUMERIC, fifth.type)
    }

    @Test
    fun name_intelligent_withSort() {
        val name = IntelligentKnownName("The Smile", "Smile")
        assertEquals("The Smile", name.raw)
        assertEquals("Smile", name.sort)
        assertEquals("S", name.thumb)
        val only = name.sortTokens.single()
        assertEquals("Smile", only.collationKey.sourceString)
        assertEquals(SortToken.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_equals_simple() {
        val a = SimpleKnownName("The Same", "Same")
        val b = SimpleKnownName("The Same", "Same")
        assertEquals(a, b)
    }

    @Test
    fun name_equals_differentSort() {
        val a = SimpleKnownName("The Same", "Same")
        val b = SimpleKnownName("The Same", null)
        assertNotEquals(a, b)
        assertNotEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun name_equals_intelligent_differentTokens() {
        val a = IntelligentKnownName("The Same", "Same")
        val b = IntelligentKnownName("Same", "Same")
        assertNotEquals(a, b)
        assertNotEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun name_compareTo_simple_withoutSort_withoutArticle_withoutNumeric() {
        val a = SimpleKnownName("A", null)
        val b = SimpleKnownName("B", null)
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_simple_withoutSort_withArticle_withoutNumeric() {
        val a = SimpleKnownName("A Brain in a Bottle", null)
        val b = SimpleKnownName("Acid Rain", null)
        val c = SimpleKnownName("Boralis / Contrastellar", null)
        val d = SimpleKnownName("Breathe In", null)
        assertEquals(-1, a.compareTo(b))
        assertEquals(-1, a.compareTo(c))
        assertEquals(-1, a.compareTo(d))
    }

    @Test
    fun name_compareTo_simple_withSort_withoutArticle_withNumeric() {
        val a = SimpleKnownName("15 Step", null)
        val b = SimpleKnownName("128 Harps", null)
        val c = SimpleKnownName("1969", null)
        assertEquals(1, a.compareTo(b))
        assertEquals(-1, a.compareTo(c))
    }

    @Test
    fun name_compareTo_simple_withPartialSort() {
        val a = SimpleKnownName("A", "C")
        val b = SimpleKnownName("B", null)
        assertEquals(1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_simple_withSort() {
        val a = SimpleKnownName("D", "A")
        val b = SimpleKnownName("C", "B")
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withoutArticle_withoutNumeric() {
        val a = IntelligentKnownName("A", null)
        val b = IntelligentKnownName("B", null)
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withArticle_withoutNumeric() {
        val a = IntelligentKnownName("A Brain in a Bottle", null)
        val b = IntelligentKnownName("Acid Rain", null)
        val c = IntelligentKnownName("Boralis / Contrastellar", null)
        val d = IntelligentKnownName("Breathe In", null)
        assertEquals(1, a.compareTo(b))
        assertEquals(1, a.compareTo(c))
        assertEquals(-1, a.compareTo(d))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withoutArticle_withNumeric() {
        val a = IntelligentKnownName("15 Step", null)
        val b = IntelligentKnownName("128 Harps", null)
        val c = IntelligentKnownName("1969", null)
        assertEquals(-1, a.compareTo(b))
        assertEquals(-1, b.compareTo(c))
        assertEquals(-2, a.compareTo(c))
    }

    @Test
    fun name_compareTo_intelligent_withPartialSort_withoutArticle_withoutNumeric() {
        val a = SimpleKnownName("A", "C")
        val b = SimpleKnownName("B", null)
        assertEquals(1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withSort_withoutArticle_withoutNumeric() {
        val a = IntelligentKnownName("D", "A")
        val b = IntelligentKnownName("C", "B")
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_unknown() {
        val a = Name.Unknown(0)
        assertEquals("?", a.thumb)
    }

    @Test
    fun name_compareTo_mixed() {
        val a = Name.Unknown(0)
        val b = IntelligentKnownName("A", null)
        assertEquals(-1, a.compareTo(b))
    }
}

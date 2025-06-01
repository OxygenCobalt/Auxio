/*
 * Copyright (c) 2023 Auxio Project
 * NamingTest.kt is part of Auxio.
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
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.Placeholder
import org.oxycblt.musikr.tag.Token

class NamingTest {
    @Test
    fun name_simple_withoutPunct() {
        val name = Naming.simple().name("Loveless", null)
        assertEquals("Loveless", name.raw)
        assertEquals(null, name.sort)
        val only = name.tokens.single()
        assertEquals("Loveless", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_simple_withPunct() {
        val name = Naming.simple().name("alt-J", null)
        assertEquals("alt-J", name.raw)
        assertEquals(null, name.sort)
        val only = name.tokens.single()
        assertEquals("altJ", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_simple_oopsAllPunct() {
        val name = Naming.simple().name("!!!", null)
        assertEquals("!!!", name.raw)
        assertEquals(null, name.sort)
        val only = name.tokens.single()
        assertEquals("!!!", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_simple_spacedPunct() {
        val name = Naming.simple().name("& Yet & Yet", null)
        assertEquals("& Yet & Yet", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Yet  Yet", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_simple_withSort() {
        val name = Naming.simple().name("The Smile", "Smile")
        assertEquals("The Smile", name.raw)
        assertEquals("Smile", name.sort)
        val only = name.tokens.single()
        assertEquals("Smile", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withoutNumerics() {
        val name = Naming.intelligent().name("Loveless", null)
        assertEquals("Loveless", name.raw)
        assertEquals(null, name.sort)
        val only = name.tokens.single()
        assertEquals("Loveless", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withSpacedStartNumerics() {
        val name = Naming.intelligent().name("15 Step", null)
        assertEquals("15 Step", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("15", first.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, first.type)
        val second = name.tokens[1]
        assertEquals("Step", second.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withPackedStartNumerics() {
        val name = Naming.intelligent().name("23Kid", null)
        assertEquals("23Kid", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("23", first.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, first.type)
        val second = name.tokens[1]
        assertEquals("Kid", second.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withSpacedMiddleNumerics() {
        val name = Naming.intelligent().name("Foo 1 2 Bar", null)
        assertEquals("Foo 1 2 Bar", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
        val second = name.tokens[1]
        assertEquals("1", second.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, second.type)
        val third = name.tokens[2]
        assertEquals(" ", third.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, third.type)
        val fourth = name.tokens[3]
        assertEquals("2", fourth.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, fourth.type)
        val fifth = name.tokens[4]
        assertEquals("Bar", fifth.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, fifth.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withPackedMiddleNumerics() {
        val name = Naming.intelligent().name("Foo12Bar", null)
        assertEquals("Foo12Bar", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
        val second = name.tokens[1]
        assertEquals("12", second.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, second.type)
        val third = name.tokens[2]
        assertEquals("Bar", third.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, third.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withSpacedEndNumerics() {
        val name = Naming.intelligent().name("Foo 1", null)
        assertEquals("Foo 1", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Foo", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
        val second = name.tokens[1]
        assertEquals("1", second.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withoutArticle_withPackedEndNumerics() {
        val name = Naming.intelligent().name("Error404", null)
        assertEquals("Error404", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Error", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
        val second = name.tokens[1]
        assertEquals("404", second.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, second.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withThe_withoutNumerics() {
        val name = Naming.intelligent().name("The National Anthem", null)
        assertEquals("The National Anthem", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("National Anthem", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withAn_withoutNumerics() {
        val name = Naming.intelligent().name("An Eagle in Your Mind", null)
        assertEquals("An Eagle in Your Mind", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Eagle in Your Mind", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withoutPunct_withA_withoutNumerics() {
        val name = Naming.intelligent().name("A Song For Our Fathers", null)
        assertEquals("A Song For Our Fathers", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Song For Our Fathers", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withPunct_withoutArticle_withoutNumerics() {
        val name = Naming.intelligent().name("alt-J", null)
        assertEquals("alt-J", name.raw)
        assertEquals(null, name.sort)
        val only = name.tokens.single()
        assertEquals("altJ", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_oopsAllPunct_withoutArticle_withoutNumerics() {
        val name = Naming.intelligent().name("!!!", null)
        assertEquals("!!!", name.raw)
        assertEquals(null, name.sort)
        val only = name.tokens.single()
        assertEquals("!!!", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_intelligent_withoutPunct_shortArticle_withNumerics() {
        val name = Naming.intelligent().name("the 1", null)
        assertEquals("the 1", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("1", first.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, first.type)
    }

    @Test
    fun name_intelligent_spacedPunct_withoutArticle_withoutNumerics() {
        val name = Naming.intelligent().name("& Yet & Yet", null)
        assertEquals("& Yet & Yet", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Yet  Yet", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
    }

    @Test
    fun name_intelligent_withPunct_withoutArticle_withNumerics() {
        val name = Naming.intelligent().name("Design : 2 : 3", null)
        assertEquals("Design : 2 : 3", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("Design", first.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, first.type)
        val second = name.tokens[1]
        assertEquals("2", second.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, second.type)
        val third = name.tokens[2]
        assertEquals("  ", third.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, third.type)
        val fourth = name.tokens[3]
        assertEquals("3", fourth.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, fourth.type)
    }

    @Test
    fun name_intelligent_oopsAllPunct_withoutArticle_oopsAllNumerics() {
        val name = Naming.intelligent().name("2 + 2 = 5", null)
        assertEquals("2 + 2 = 5", name.raw)
        assertEquals(null, name.sort)
        val first = name.tokens[0]
        assertEquals("2", first.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, first.type)
        val second = name.tokens[1]
        assertEquals("  ", second.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, second.type)
        val third = name.tokens[2]
        assertEquals("2", third.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, third.type)
        val fourth = name.tokens[3]
        assertEquals("  ", fourth.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, fourth.type)
        val fifth = name.tokens[4]
        assertEquals("5", fifth.collationKey.sourceString)
        assertEquals(Token.Type.NUMERIC, fifth.type)
    }

    @Test
    fun name_intelligent_withSort() {
        val name = Naming.intelligent().name("The Smile", "Smile")
        assertEquals("The Smile", name.raw)
        assertEquals("Smile", name.sort)
        val only = name.tokens.single()
        assertEquals("Smile", only.collationKey.sourceString)
        assertEquals(Token.Type.LEXICOGRAPHIC, only.type)
    }

    @Test
    fun name_equals_simple() {
        val a = Naming.simple().name("The Same", "Same")
        val b = Naming.simple().name("The Same", "Same")
        assertEquals(a, b)
    }

    @Test
    fun name_equals_differentSort() {
        val a = Naming.simple().name("The Same", "Same")
        val b = Naming.simple().name("The Same", null)
        assertNotEquals(a, b)
        assertNotEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun name_equals_intelligent_differentTokens() {
        val a = Naming.intelligent().name("The Same", "Same")
        val b = Naming.intelligent().name("Same", "Same")
        assertNotEquals(a, b)
        assertNotEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun name_compareTo_simple_withoutSort_withoutArticle_withoutNumeric() {
        val a = Naming.simple().name("A", null)
        val b = Naming.simple().name("B", null)
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_simple_withoutSort_withArticle_withoutNumeric() {
        val a = Naming.simple().name("A Brain in a Bottle", null)
        val b = Naming.simple().name("Acid Rain", null)
        val c = Naming.simple().name("Boralis / Contrastellar", null)
        val d = Naming.simple().name("Breathe In", null)
        assertEquals(-1, a.compareTo(b))
        assertEquals(-1, a.compareTo(c))
        assertEquals(-1, a.compareTo(d))
    }

    @Test
    fun name_compareTo_simple_withSort_withoutArticle_withNumeric() {
        val a = Naming.simple().name("15 Step", null)
        val b = Naming.simple().name("128 Harps", null)
        val c = Naming.simple().name("1969", null)
        assertEquals(1, a.compareTo(b))
        assertEquals(-1, a.compareTo(c))
    }

    @Test
    fun name_compareTo_simple_withPartialSort() {
        val a = Naming.simple().name("A", "C")
        val b = Naming.simple().name("B", null)
        assertEquals(1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_simple_withSort() {
        val a = Naming.simple().name("D", "A")
        val b = Naming.simple().name("C", "B")
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withoutArticle_withoutNumeric() {
        val a = Naming.intelligent().name("A", null)
        val b = Naming.intelligent().name("B", null)
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withArticle_withoutNumeric() {
        val a = Naming.intelligent().name("A Brain in a Bottle", null)
        val b = Naming.intelligent().name("Acid Rain", null)
        val c = Naming.intelligent().name("Boralis / Contrastellar", null)
        val d = Naming.intelligent().name("Breathe In", null)
        assertEquals(1, a.compareTo(b))
        assertEquals(1, a.compareTo(c))
        assertEquals(-1, a.compareTo(d))
    }

    @Test
    fun name_compareTo_intelligent_withoutSort_withoutArticle_withNumeric() {
        val a = Naming.intelligent().name("15 Step", null)
        val b = Naming.intelligent().name("128 Harps", null)
        val c = Naming.intelligent().name("1969", null)
        assertEquals(-1, a.compareTo(b))
        assertEquals(-1, b.compareTo(c))
        assertEquals(-2, a.compareTo(c))
    }

    @Test
    fun name_compareTo_intelligent_withPartialSort_withoutArticle_withoutNumeric() {
        val a = Naming.simple().name("A", "C")
        val b = Naming.simple().name("B", null)
        assertEquals(1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_intelligent_withSort_withoutArticle_withoutNumeric() {
        val a = Naming.intelligent().name("D", "A")
        val b = Naming.intelligent().name("C", "B")
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun name_compareTo_mixed() {
        val a = Name.Unknown(Placeholder.ALBUM)
        val b = Naming.intelligent().name("A", null)
        assertEquals(-1, a.compareTo(b))
    }
}

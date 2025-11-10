/*
 * Copyright (c) 2024 Auxio Project
 * Naming.kt is part of Auxio.
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

import android.icu.text.Transliterator
import android.os.Build
import java.text.CollationKey
import java.text.Collator
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.Placeholder
import org.oxycblt.musikr.tag.Token

abstract class Naming {
    fun name(raw: String?, sort: String?, placeholder: Placeholder): Name =
        if (raw != null) {
            name(raw, sort)
        } else {
            Name.Unknown(placeholder)
        }

    abstract fun name(raw: String, sort: String?): Name.Known

    companion object {
        fun intelligent(): Naming = IntelligentNaming

        fun simple(): Naming = SimpleNaming
    }
}

data object IntelligentNaming : Naming() {
    override fun name(raw: String, sort: String?): Name.Known = IntelligentKnownName(raw, sort)
}

data object SimpleNaming : Naming() {
    override fun name(raw: String, sort: String?): Name.Known = SimpleKnownName(raw, sort)
}

private val collator: Collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
private val punctRegex by lazy { Regex("[\\p{Punct}+]") }

// TODO: Consider how you want to handle whitespace and "gaps" in names.

/**
 * Plain [Name.Known] implementation that is internationalization-safe.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private data class SimpleKnownName(override val raw: String, override val sort: String?) :
    Name.Known() {
    override val tokens = listOf(parseToken(sort ?: raw))

    private fun parseToken(name: String): Token {
        // Remove excess punctuation from the string, as those usually aren't considered in sorting.
        val stripped = name.replace(punctRegex, "").trim().ifEmpty { name }
        val collationKey = collator.getCollationKey(stripped)
        // Always use lexicographic mode since we aren't parsing any numeric components
        return Token(collationKey, Token.Type.LEXICOGRAPHIC)
    }
}

/**
 * [Name.Known] implementation that adds advanced sorting behavior at the cost of localization.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private data class IntelligentKnownName(override val raw: String, override val sort: String?) :
    Name.Known() {
    override val tokens = parseTokens(sort ?: raw)

    private fun parseTokens(name: String): List<Token> {
        // TODO: This routine is consuming much of the song building runtime, find a way to
        //  optimize it
        var stripped =
            name
                // Remove excess punctuation from the string, as those usually aren't
                // considered in sorting.
                .replace(punctRegex, "")
                .ifEmpty { name }
                .run {
                    // Strip any english articles like "the" or "an" from the start, as music
                    // sorting should ignore such when possible.
                    when {
                        length > 4 && startsWith("the ", ignoreCase = true) -> substring(4)
                        length > 3 && startsWith("an ", ignoreCase = true) -> substring(3)
                        length > 2 && startsWith("a ", ignoreCase = true) -> substring(2)
                        else -> this
                    }
                }

        // Transliterate to latin if available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Transliterator.getAvailableIDs()
                .toList().contains("Any-Latin")
        ) {
            stripped = Transliterator.getInstance("Any-Latin;").transliterate(stripped)
        }

        // To properly compare numeric components in names, we have to split them up into
        // individual lexicographic and numeric tokens and then individually compare them
        // with special logic.
        return TOKEN_REGEX.findAll(stripped).mapTo(mutableListOf()) { match ->
            // Remove excess whitespace where possible
            val token = match.value.trim().ifEmpty { match.value }
            val collationKey: CollationKey
            val type: Token.Type
            // Separate each token into their numeric and lexicographic counterparts.
            if (token.first().isDigit()) {
                // The digit string comparison breaks with preceding zero digits, remove those
                val digits =
                    token.trimStart { Character.getNumericValue(it) == 0 }.ifEmpty { token }
                // Other languages have other types of digit strings, still use collation keys
                collationKey = collator.getCollationKey(digits)
                type = Token.Type.NUMERIC
            } else {
                collationKey = collator.getCollationKey(token)
                type = Token.Type.LEXICOGRAPHIC
            }
            Token(collationKey, type)
        }
    }

    companion object {
        private val TOKEN_REGEX by lazy { Regex("(\\d+)|(\\D+)") }
    }
}

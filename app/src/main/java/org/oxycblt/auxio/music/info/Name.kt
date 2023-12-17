/*
 * Copyright (c) 2023 Auxio Project
 * Name.kt is part of Auxio.
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

import android.content.Context
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import java.text.CollationKey
import java.text.Collator

/**
 * The name of a music item.
 *
 * This class automatically implements advanced sorting heuristics for music naming,
 *
 * @author Alexander Capehart
 */
sealed interface Name : Comparable<Name> {
    /**
     * A logical first character that can be used to collate a sorted list of music.
     *
     * TODO: Move this to the home package
     */
    val thumb: String

    /**
     * Get a human-readable string representation of this instance.
     *
     * @param context [Context] required.
     */
    fun resolve(context: Context): String

    /** A name that could be obtained for the music item. */
    sealed class Known : Name {
        /** The raw name string obtained. Should be ignored in favor of [resolve]. */
        abstract val raw: String
        /** The raw sort name string obtained. */
        abstract val sort: String?

        /** A tokenized version of the name that will be compared. */
        @VisibleForTesting(VisibleForTesting.PROTECTED) abstract val sortTokens: List<SortToken>

        final override val thumb: String
            get() =
                // TODO: Remove these safety checks once you have real unit testing
                sortTokens
                    .firstOrNull()
                    ?.run { collationKey.sourceString.firstOrNull() }
                    ?.let { if (it.isDigit()) "#" else it.uppercase() }
                    ?: "?"

        final override fun resolve(context: Context) = raw

        final override fun compareTo(other: Name) =
            when (other) {
                is Known -> {
                    // Progressively compare the sort tokens between each known name.
                    sortTokens.zip(other.sortTokens).fold(0) { acc, (token, otherToken) ->
                        acc.takeIf { it != 0 } ?: token.compareTo(otherToken)
                    }
                }
                // Unknown names always come before known names.
                is Unknown -> 1
            }

        sealed interface Factory {
            /**
             * Create a new instance of [Name.Known]
             *
             * @param raw The raw name obtained from the music item
             * @param sort The raw sort name obtained from the music item
             */
            fun parse(raw: String, sort: String?): Known
        }

        /** Produces a simple [Known] with basic sorting heuristics that are locale-independent. */
        data object SimpleFactory : Factory {
            override fun parse(raw: String, sort: String?) = SimpleKnownName(raw, sort)
        }

        /** Produces an intelligent [Known] with advanced, but more fragile heuristics. */
        data object IntelligentFactory : Factory {
            override fun parse(raw: String, sort: String?) = IntelligentKnownName(raw, sort)
        }
    }

    /**
     * A placeholder name that is used when a [Known] name could not be obtained for the item.
     *
     * @author Alexander Capehart
     */
    data class Unknown(@StringRes val stringRes: Int) : Name {
        override val thumb = "?"

        override fun resolve(context: Context) = context.getString(stringRes)

        override fun compareTo(other: Name) =
            when (other) {
                // Unknown names do not need any direct comparison right now.
                is Unknown -> 0
                // Unknown names always come before known names.
                is Known -> -1
            }
    }
}

private val collator: Collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
private val punctRegex by lazy { Regex("[\\p{Punct}+]") }

// TODO: Consider how you want to handle whitespace and "gaps" in names.

/**
 * Plain [Name.Known] implementation that is internationalization-safe.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class SimpleKnownName(override val raw: String, override val sort: String?) : Name.Known() {
    override val sortTokens = listOf(parseToken(sort ?: raw))

    private fun parseToken(name: String): SortToken {
        // Remove excess punctuation from the string, as those usually aren't considered in sorting.
        val stripped = name.replace(punctRegex, "").trim().ifEmpty { name }
        val collationKey = collator.getCollationKey(stripped)
        // Always use lexicographic mode since we aren't parsing any numeric components
        return SortToken(collationKey, SortToken.Type.LEXICOGRAPHIC)
    }
}

/**
 * [Name.Known] implementation that adds advanced sorting behavior at the cost of localization.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class IntelligentKnownName(override val raw: String, override val sort: String?) :
    Name.Known() {
    override val sortTokens = parseTokens(sort ?: raw)

    private fun parseTokens(name: String): List<SortToken> {
        // TODO: This routine is consuming much of the song building runtime, find a way to
        //  optimize it
        val stripped =
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

        // To properly compare numeric components in names, we have to split them up into
        // individual lexicographic and numeric tokens and then individually compare them
        // with special logic.
        return TOKEN_REGEX.findAll(stripped).mapTo(mutableListOf()) { match ->
            // Remove excess whitespace where possible
            val token = match.value.trim().ifEmpty { match.value }
            val collationKey: CollationKey
            val type: SortToken.Type
            // Separate each token into their numeric and lexicographic counterparts.
            if (token.first().isDigit()) {
                // The digit string comparison breaks with preceding zero digits, remove those
                val digits =
                    token.trimStart { Character.getNumericValue(it) == 0 }.ifEmpty { token }
                // Other languages have other types of digit strings, still use collation keys
                collationKey = collator.getCollationKey(digits)
                type = SortToken.Type.NUMERIC
            } else {
                collationKey = collator.getCollationKey(token)
                type = SortToken.Type.LEXICOGRAPHIC
            }
            SortToken(collationKey, type)
        }
    }

    companion object {
        private val TOKEN_REGEX by lazy { Regex("(\\d+)|(\\D+)") }
    }
}

/** An individual part of a name string that can be compared intelligently. */
@VisibleForTesting(VisibleForTesting.PROTECTED)
data class SortToken(val collationKey: CollationKey, val type: Type) : Comparable<SortToken> {
    override fun compareTo(other: SortToken): Int {
        // Numeric tokens should always be lower than lexicographic tokens.
        val modeComp = type.compareTo(other.type)
        if (modeComp != 0) {
            return modeComp
        }

        // Numeric strings must be ordered by magnitude, thus immediately short-circuit
        // the comparison if the lengths do not match.
        if (type == Type.NUMERIC &&
            collationKey.sourceString.length != other.collationKey.sourceString.length) {
            return collationKey.sourceString.length - other.collationKey.sourceString.length
        }

        return collationKey.compareTo(other.collationKey)
    }

    /** Denotes the type of comparison to be performed with this token. */
    enum class Type {
        /** Compare as a digit string, like "65". */
        NUMERIC,
        /** Compare as a standard alphanumeric string, like "65daysofstatic" */
        LEXICOGRAPHIC
    }
}

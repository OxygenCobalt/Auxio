/*
 * Copyright (c) 2023 Auxio Project
 * Separators.kt is part of Auxio.
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

/**
 * Defines the user-specified parsing of multi-value tags. This should be used to parse any tags
 * that may be delimited with a separator character.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Separators {
    /**
     * Parse a separated value from one or more strings. If the value is already composed of more
     * than one value, nothing is done. Otherwise, it will attempt to split it based on the user's
     * separator preferences.
     *
     * @return A new list of one or more [String]s parsed by the separator configuration
     */
    fun split(strings: List<String>): List<String>

    companion object {
        const val COMMA = ','
        const val SEMICOLON = ';'
        const val SLASH = '/'
        const val PLUS = '+'
        const val AND = '&'

        /**
         * Creates a new instance from a string of separator characters to use.
         *
         * @param chars The separator characters to use. Each character in the string will be
         *   checked for when splitting a string list.
         * @return A new [Separators] instance reflecting the separators.
         */
        fun from(chars: String) =
            if (chars.isNotEmpty()) {
                CharSeparators(chars.toSet())
            } else {
                NoSeparators
            }
    }
}

private data class CharSeparators(private val chars: Set<Char>) : Separators {
    override fun split(strings: List<String>) =
        if (strings.size == 1) splitImpl(strings.first()) else strings

    private fun splitImpl(string: String) =
        string.splitEscaped { chars.contains(it) }.correctWhitespace()
}

private object NoSeparators : Separators {
    override fun split(strings: List<String>) = strings
}

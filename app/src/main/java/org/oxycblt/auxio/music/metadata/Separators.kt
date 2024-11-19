package org.oxycblt.auxio.music.metadata

import org.oxycblt.auxio.music.stack.extractor.correctWhitespace
import org.oxycblt.auxio.music.stack.extractor.splitEscaped

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

package org.oxycblt.musikr.tag.interpret

import java.util.Locale
import org.oxycblt.musikr.tag.format.parseId3GenreNames
import org.oxycblt.musikr.util.correctWhitespace

/**
 * Converts raw and already-structured metadata genre values into distinct normalized genre names.
 *
 * Some tag formats expose repeated genre fields, while others collapse multiple genres into one
 * text field. Genre parsing is therefore centralized here with a genre-specific delimiter policy:
 * commas, semicolons, slashes, pipes, and backslash-style separators are treated as delimiters,
 * including when an approved delimiter is escaped. Ambiguous separators such as `&`, `+`,
 * hyphens/dashes, and `and` are kept literal. Parsed names are trimmed, internal whitespace is
 * collapsed, empty values are discarded, and duplicates are removed case-insensitively while
 * preserving the first-seen casing.
 */
internal fun parseGenreNames(rawGenreNames: List<String>): List<String> {
    val withId3Fallback = rawGenreNames.parseId3GenreNames() ?: rawGenreNames
    val tokens = mutableListOf<String>()

    for (raw in withId3Fallback) {
        tokens += splitRawGenreField(raw)
    }

    val deduped = LinkedHashMap<String, String>()
    for (token in tokens) {
        val cleaned = token.replace(WHITESPACE_RE, " ").correctWhitespace() ?: continue
        val key = cleaned.lowercase(Locale.ROOT)
        deduped.putIfAbsent(key, cleaned)
    }

    return deduped.values.toList()
}

private fun splitRawGenreField(raw: String): List<String> {
    val split = mutableListOf<String>()
    val current = StringBuilder()
    var index = 0

    while (index < raw.length) {
        val char = raw[index]
        val next = raw.getOrNull(index + 1)

        when {
            char == '\\' && next != null && isGenreDelimiterOrBackslash(next) -> {
                // Genre metadata in the wild often escapes approved joiners (or uses a double
                // backslash display separator), but Auxio-TS treats those joiners as delimiters
                // on purpose so multi-genre values still fan out into distinct memberships.
                split.addCurrentGenre(current)
                index += 2
            }
            isGenreDelimiter(char) -> {
                split.addCurrentGenre(current)
                index++
            }
            char == '\\' &&
                next != null &&
                current.isNotEmpty() &&
                next.isLetterOrDigit() -> {
                // A stray single backslash between genre names is treated as a practical
                // separator, while the following character starts the next genre token.
                split.addCurrentGenre(current)
                index++
            }
            else -> {
                current.append(char)
                index++
            }
        }
    }

    split.addCurrentGenre(current)
    return split
}

private fun MutableList<String>.addCurrentGenre(current: StringBuilder) {
    if (current.isNotEmpty()) {
        add(current.toString())
        current.clear()
    }
}

private fun isGenreDelimiter(char: Char) = char in GENRE_DELIMITERS

private fun isGenreDelimiterOrBackslash(char: Char) = char == '\\' || isGenreDelimiter(char)

private val GENRE_DELIMITERS = setOf(',', ';', '/', '|')
private val WHITESPACE_RE = Regex("\\s+")

package org.oxycblt.musikr.tag.interpret

import java.util.Locale
import org.oxycblt.musikr.tag.format.parseId3GenreNames
import org.oxycblt.musikr.util.correctWhitespace
import org.oxycblt.musikr.util.splitEscaped

internal fun parseGenreNames(rawGenreNames: List<String>, separators: Separators): List<String> {
    val withId3Fallback = rawGenreNames.parseId3GenreNames() ?: rawGenreNames
    val tokens = mutableListOf<String>()

    for (raw in withId3Fallback) {
        tokens += splitGenreTokens(raw, separators)
    }

    val deduped = LinkedHashMap<String, String>()
    for (token in tokens) {
        val cleaned = token.replace(WHITESPACE_RE, " ").correctWhitespace() ?: continue
        val key = cleaned.lowercase(Locale.ROOT)
        deduped.putIfAbsent(key, cleaned)
    }

    return deduped.values.toList()
}

private fun splitGenreTokens(raw: String, separators: Separators): List<String> {
    return separators.split(listOf(raw)).flatMap { it.splitEscaped { char -> char == ',' || char == ';' } }
}

private val WHITESPACE_RE = Regex("\\s+")

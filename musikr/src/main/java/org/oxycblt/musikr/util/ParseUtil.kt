package org.oxycblt.musikr.util

/**
 * Split a [String] by the given selector, automatically handling escaped characters that satisfy
 * the selector.
 *
 * @param selector A block that determines if the string should be split at a given character.
 * @return One or more [String]s split by the selector.
 */
inline fun String.splitEscaped(selector: (Char) -> Boolean): List<String> {
    val split = mutableListOf<String>()
    var currentString = ""
    var i = 0

    while (i < length) {
        val a = get(i)
        val b = getOrNull(i + 1)

        if (selector(a)) {
            // Non-escaped separator, split the string here, making sure any stray whitespace
            // is removed.
            split.add(currentString)
            currentString = ""
            i++
            continue
        }

        if (b != null && a == '\\' && selector(b)) {
            // Is an escaped character, add the non-escaped variant and skip two
            // characters to move on to the next one.
            currentString += b
            i += 2
        } else {
            // Non-escaped, increment normally.
            currentString += a
            i++
        }
    }

    if (currentString.isNotEmpty()) {
        // Had an in-progress split string that is now terminated, add it.
        split.add(currentString)
    }

    return split
}

// TODO: Remove the escaping checks, it's too expensive to do this for every single tag.

/**
 * Fix trailing whitespace or blank contents in a [String].
 *
 * @return A string with trailing whitespace remove,d or null if the [String] was all whitespace or
 *   empty.
 */
fun String.correctWhitespace() = trim().ifBlank { null }

/**
 * Fix trailing whitespace or blank contents within a list of [String]s.
 *
 * @return A list of non-blank strings with trailing whitespace removed.
 */
fun List<String>.correctWhitespace() = mapNotNull { it.correctWhitespace() }

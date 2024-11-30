/*
 * Copyright (c) 2022 Auxio Project
 * TagUtil.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.explore.extractor

import org.oxycblt.auxio.util.positiveOrNull

/// --- GENERIC PARSING ---

// TODO: Remove the escaping checks, it's too expensive to do this for every single tag.

// TODO: I want to eventually be able to move a lot of this into TagWorker once I no longer have
//  to deal with the cross-module dependencies of MediaStoreExtractor.

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

/**
 * Parse an ID3v2-style position + total [String] field. These fields consist of a number and an
 * (optional) total value delimited by a /.
 *
 * @return The position value extracted from the string field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 *
 * @see transformPositionField
 */
fun String.parseId3v2PositionField() =
    split('/', limit = 2).let {
        transformPositionField(it[0].toIntOrNull(), it.getOrNull(1)?.toIntOrNull())
    }

/**
 * Parse a vorbis-style position + total field. These fields consist of two fields for the position
 * and total numbers.
 *
 * @param pos The position value, or null if not present.
 * @param total The total value, if not present.
 * @return The position value extracted from the field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 *
 * @see transformPositionField
 */
fun parseVorbisPositionField(pos: String?, total: String?) =
    transformPositionField(pos?.toIntOrNull(), total?.toIntOrNull())

/**
 * Transform a raw position + total field into a position a way that tolerates placeholder values.
 *
 * @param pos The position value, or null if not present.
 * @param total The total value, if not present.
 * @return The position value extracted from the field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 */
fun transformPositionField(pos: Int?, total: Int?) =
    if (pos != null && (pos > 0 || (total?.positiveOrNull() != null))) {
        pos
    } else {
        null
    }

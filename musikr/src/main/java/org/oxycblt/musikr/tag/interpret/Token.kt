/*
 * Copyright (c) 2024 Auxio Project
 * Token.kt is part of Auxio.
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

import java.text.CollationKey

/** An individual part of a name string that can be compared intelligently. */
data class Token(internal val collationKey: CollationKey, internal val type: Type) : Comparable<Token> {
    val value: String get() = collationKey.sourceString

    override fun compareTo(other: Token): Int {
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

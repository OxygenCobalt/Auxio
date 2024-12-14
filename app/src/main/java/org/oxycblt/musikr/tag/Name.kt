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
 
package org.oxycblt.musikr.tag

import org.oxycblt.musikr.tag.interpret.Token

/**
 * The name of a music item.
 *
 * This class automatically implements advanced sorting heuristics for music naming,
 *
 * @author Alexander Capehart
 */
sealed interface Name : Comparable<Name> {
    /** A name that could be obtained for the music item. */
    abstract class Known : Name {
        /** The raw name string obtained. Should be ignored in favor of [resolve]. */
        abstract val raw: String
        /** The raw sort name string obtained. */
        abstract val sort: String?
        /** A tokenized version of the name that will be compared. */
        abstract val tokens: List<Token>

        final override fun compareTo(other: Name) =
            when (other) {
                is Known -> {
                    val result =
                        tokens.zip(other.tokens).fold(0) { acc, (token, otherToken) ->
                            acc.takeIf { it != 0 } ?: token.compareTo(otherToken)
                        }
                    if (result != 0) result else tokens.size.compareTo(other.tokens.size)
                }
                is Unknown -> 1
            }
    }

    /**
     * A placeholder name that is used when a [Known] name could not be obtained for the item.
     *
     * @author Alexander Capehart
     */
    data class Unknown(val placeholder: Placeholder) : Name {
        override fun compareTo(other: Name) =
            when (other) {
                // Unknown names do not need any direct comparison right now.
                is Unknown -> 0
                // Unknown names always come before known names.
                is Known -> -1
            }
    }
}

enum class Placeholder {
    ALBUM,
    ARTIST,
    GENRE
}

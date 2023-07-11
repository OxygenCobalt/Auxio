/*
 * Copyright (c) 2021 Auxio Project
 * Tab.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home.tabs

import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW

/**
 * A representation of a library tab suitable for configuration.
 *
 * @param type The type of list in the home view this instance corresponds to.
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed class Tab(open val type: MusicType) {
    /**
     * A visible tab. This will be visible in the home and tab configuration views.
     *
     * @param type The type of list in the home view this instance corresponds to.
     */
    data class Visible(override val type: MusicType) : Tab(type)

    /**
     * A visible tab. This will be visible in the tab configuration view, but not in the home view.
     *
     * @param type The type of list in the home view this instance corresponds to.
     */
    data class Invisible(override val type: MusicType) : Tab(type)

    companion object {
        // Like other IO-bound datatypes in Auxio, tabs are stored in a binary format. However, tabs
        // cannot be serialized on their own. Instead, they are saved as a sequence of tabs as shown
        // below:
        //
        // 0bTAB1_TAB2_TAB3_TAB4_TAB5
        //
        // Where TABN is a chunk representing a tab at position N.
        // Each chunk in a sequence is represented as:
        //
        // VTTT
        //
        // Where V is a bit representing the visibility and T is a 3-bit integer representing the
        // MusicMode for this tab.

        /** The maximum index that a well-formed tab sequence should be. */
        const val MAX_SEQUENCE_IDX = 4

        /**
         * The default tab sequence, in integer form. This represents a set of four visible tabs
         * ordered as "Song", "Album", "Artist", "Genre", and "Playlists
         */
        const val SEQUENCE_DEFAULT = 0b1000_1001_1010_1011_1100

        /** Maps between the integer code in the tab sequence and it's [MusicType]. */
        private val MODE_TABLE =
            arrayOf(
                MusicType.SONGS,
                MusicType.ALBUMS,
                MusicType.ARTISTS,
                MusicType.GENRES,
                MusicType.PLAYLISTS)

        /**
         * Convert an array of [Tab]s into it's integer representation.
         *
         * @param tabs The array of [Tab]s to convert
         * @return An integer representation of the [Tab] array
         */
        fun toIntCode(tabs: Array<Tab>): Int {
            // Like when deserializing, make sure there are no duplicate tabs for whatever reason.
            val distinct = tabs.distinctBy { it.type }
            if (tabs.size != distinct.size) {
                logW(
                    "Tab sequences should not have duplicates [old: ${tabs.size} new: ${distinct.size}]")
            }

            var sequence = 0
            var shift = MAX_SEQUENCE_IDX * 4
            for (tab in distinct) {
                val bin =
                    when (tab) {
                        is Visible -> 1.shl(3) or MODE_TABLE.indexOf(tab.type)
                        is Invisible -> MODE_TABLE.indexOf(tab.type)
                    }

                sequence = sequence or bin.shl(shift)
                shift -= 4
            }

            return sequence
        }

        /**
         * Convert a [Tab] integer representation into it's corresponding array of [Tab]s.
         *
         * @param intCode The integer representation of the [Tab]s.
         * @return An array of [Tab]s corresponding to the sequence.
         */
        fun fromIntCode(intCode: Int): Array<Tab>? {
            val tabs = mutableListOf<Tab>()

            // Try to parse a mode for each chunk in the sequence.
            // If we can't parse one, just skip it.
            for (shift in (0..MAX_SEQUENCE_IDX * 4).reversed() step 4) {
                val chunk = intCode.shr(shift) and 0b1111
                val mode = MODE_TABLE.getOrNull(chunk and 7) ?: continue

                // Figure out the visibility
                tabs +=
                    if (chunk and 1.shl(3) != 0) {
                        Visible(mode)
                    } else {
                        Invisible(mode)
                    }
            }

            // Make sure there are no duplicate tabs
            val distinct = tabs.distinctBy { it.type }
            if (tabs.size != distinct.size) {
                logW(
                    "Tab sequences should not have duplicates [old: ${tabs.size} new: ${distinct.size}]")
            }

            // For safety, return null if we have an empty or larger-than-expected tab array.
            if (distinct.isEmpty() || distinct.size < MAX_SEQUENCE_IDX) {
                logE("Sequence size was ${distinct.size}, which is invalid")
                return null
            }

            return distinct.toTypedArray()
        }
    }
}

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

import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.util.logE

/**
 * A data representation of a library tab.
 * A tab can come in two moves, [Visible] or [Invisible]. Invisibility means that the tab
 * will still be present in the customization menu, but will not be shown on the home UI.
 *
 * Like other IO-bound datatypes in Auxio, tabs are stored in a binary format. However, tabs cannot
 * be serialized on their own. Instead, they are saved as a sequence of tabs as shown below:
 *
 * 0bTAB1_TAB2_TAB3_TAB4_TAB5
 *
 * Where TABN is a chunk representing a tab at position N. TAB5 is reserved for playlists.
 * Each chunk in a sequence is represented as:
 *
 * VTTT
 *
 * Where V is a bit representing the visibility and T is a 3-bit integer representing the
 * [DisplayMode] ordinal for this tab.
 *
 * To serialize and deserialize a tab sequence, [toSequence] and [fromSequence] can be used
 * respectively.
 *
 * By default, the tab order will be SONGS, ALBUMS, ARTISTS, GENRES, PLAYLISTS
 */
sealed class Tab(open val mode: DisplayMode) {
    data class Visible(override val mode: DisplayMode) : Tab(mode)
    data class Invisible(override val mode: DisplayMode) : Tab(mode)

    companion object {
        /** The length a well-formed tab sequence should be **/
        const val SEQUENCE_LEN = 4
        /** The default tab sequence, represented in integer form **/
        const val SEQUENCE_DEFAULT = 0b1000_1001_1010_1011_0100

        /**
         * Convert an array [tabs] into a sequence of tabs.
         */
        fun toSequence(tabs: Array<Tab>): Int {
            // Like when deserializing, make sure there are no duplicate tabs for whatever reason.
            val distinct = tabs.distinctBy { it.mode }

            var sequence = 0b0100
            var shift = SEQUENCE_LEN * 4

            distinct.forEach { tab ->
                val bin = when (tab) {
                    is Visible -> 1.shl(3) or tab.mode.ordinal
                    is Invisible -> tab.mode.ordinal
                }

                sequence = sequence or bin.shl(shift)
                shift -= 4
            }

            return sequence
        }

        /**
         * Convert a [sequence] into an array of tabs.
         */
        fun fromSequence(sequence: Int): Array<Tab>? {
            val tabs = mutableListOf<Tab>()

            // Try to parse a mode for each chunk in the sequence.
            // If we can't parse one, just skip it.
            for (shift in (0..4 * SEQUENCE_LEN).reversed() step 4) {
                val chunk = sequence.shr(shift) and 0b1111

                val mode = when (chunk and 7) {
                    0 -> DisplayMode.SHOW_SONGS
                    1 -> DisplayMode.SHOW_ALBUMS
                    2 -> DisplayMode.SHOW_ARTISTS
                    3 -> DisplayMode.SHOW_GENRES
                    else -> continue
                }

                // Figure out the visibility
                tabs += if (chunk and 1.shl(3) != 0) {
                    Visible(mode)
                } else {
                    Invisible(mode)
                }
            }

            // Make sure there are no duplicate tabs
            val distinct = tabs.distinctBy { it.mode }

            // For safety, use the default configuration if something went wrong
            // and we have an empty or larger-than-expected tab array.
            if (distinct.isEmpty() || distinct.size < SEQUENCE_LEN) {
                logE("Sequence size was ${distinct.size}, which is invalid. Using defaults instead")
                return null
            }

            return distinct.toTypedArray()
        }
    }
}

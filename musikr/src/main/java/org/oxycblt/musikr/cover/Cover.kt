/*
 * Copyright (c) 2023 Auxio Project
 * Cover.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cover

import org.oxycblt.musikr.Song

sealed interface Cover {
    val key: String

    data class Single(override val key: String) : Cover

    class Multi(val all: List<Single>) : Cover {
        override val key = "multi@${all.hashCode()}"
    }

    companion object {
        fun nil() = Multi(listOf())

        fun single(key: String) = Single(key)

        fun multi(songs: Collection<Song>) = order(songs).run { Multi(this) }

        private fun order(songs: Collection<Song>) =
            songs
                .mapNotNull { it.cover }
                .groupBy { it.key }
                .entries
                .sortedByDescending { it.key }
                .sortedByDescending { it.value.size }
                .map { it.value.first() }
    }
}

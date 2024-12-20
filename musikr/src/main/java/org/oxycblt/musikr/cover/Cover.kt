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
import java.io.InputStream

sealed interface Cover {
    val id: String

    interface Single : Cover {
        suspend fun resolve(): InputStream?
    }

    class Multi private constructor(val all: List<Single>) : Cover {
        override val id = "multi@${all.hashCode()}"

        companion object {
            fun from(covers: Collection<Single>) =
                Multi(
                    covers
                    .groupBy { it.id }
                    .entries
                    .sortedByDescending { it.key }
                    .sortedByDescending { it.value.size }
                    .map { it.value.first() })
        }
    }
}

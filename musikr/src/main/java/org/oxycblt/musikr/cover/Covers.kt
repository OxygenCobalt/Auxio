/*
 * Copyright (c) 2024 Auxio Project
 * Covers.kt is part of Auxio.
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

import java.io.InputStream

interface Covers {
    suspend fun obtain(id: String): ObtainResult<out Cover>
}

interface MutableCovers : Covers {
    suspend fun write(data: ByteArray): Cover

    suspend fun cleanup(excluding: Collection<Cover>)
}

interface Cover {
    val id: String

    suspend fun open(): InputStream?
}

class CoverCollection private constructor(val covers: List<Cover>) {
    companion object {
        fun from(covers: Collection<Cover>) =
            CoverCollection(
                covers
                    .groupBy { it.id }
                    .entries
                    .sortedByDescending { it.key }
                    .sortedByDescending { it.value.size }
                    .map { it.value.first() })
    }
}

sealed interface ObtainResult<T : Cover> {
    data class Hit<T : Cover>(val cover: T) : ObtainResult<T>

    class Miss<T : Cover> : ObtainResult<T>
}

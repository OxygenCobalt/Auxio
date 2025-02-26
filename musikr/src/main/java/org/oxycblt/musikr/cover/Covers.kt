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
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

interface Covers<T : Cover> {
    suspend fun obtain(id: String): CoverResult<T>
}

interface MutableCovers<T : Cover> : Covers<T> {
    suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<T>

    suspend fun cleanup(excluding: Collection<Cover>)
}

sealed interface CoverResult<T : Cover> {
    data class Hit<T : Cover>(val cover: T) : CoverResult<T>

    class Miss<T : Cover> : CoverResult<T>
}

interface Cover {
    val id: String

    suspend fun open(): InputStream?
}

class CoverCollection private constructor(val covers: List<Cover>) {
    override fun hashCode() = covers.hashCode()

    override fun equals(other: Any?) = other is CoverCollection && covers == other.covers

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

/*
 * Copyright (c) 2024 Auxio Project
 * Cache.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cache

import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.FileCover
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.pipeline.RawSong

abstract class Cache {
    internal abstract suspend fun read(file: DeviceFile, covers: Covers<out Cover>): CacheResult

    internal abstract suspend fun write(song: RawSong)

    internal abstract suspend fun finalize()

    abstract class Factory {
        internal abstract fun open(): Cache
    }
}

internal sealed interface CacheResult {
    data class Hit(val song: RawSong) : CacheResult

    data class Miss(val file: DeviceFile, val addedMs: Long?) : CacheResult
}

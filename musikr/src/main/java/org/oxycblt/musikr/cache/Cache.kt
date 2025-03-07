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

import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.tag.parse.ParsedTags

interface Cache {
    suspend fun read(file: DeviceFile): CacheResult
}

interface MutableCache : Cache {
    suspend fun write(cachedSong: CachedSong)

    suspend fun cleanup(excluding: List<CachedSong>)
}

data class CachedSong(
    val file: DeviceFile,
    val properties: Properties,
    val tags: ParsedTags,
    val coverId: String?,
    val addedMs: Long
)

sealed interface CacheResult {
    data class Hit(val song: CachedSong) : CacheResult

    data class Miss(val file: DeviceFile) : CacheResult

    data class Stale(val file: DeviceFile, val addedMs: Long) : CacheResult
}

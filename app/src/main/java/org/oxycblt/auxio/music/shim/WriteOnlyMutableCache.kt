/*
 * Copyright (c) 2025 Auxio Project
 * WriteOnlyMutableCache.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.shim

import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cache.CachedFile
import org.oxycblt.musikr.cache.MutableCache
import org.oxycblt.musikr.fs.File

class WriteOnlyMutableCache(private val inner: MutableCache) : MutableCache {
    override suspend fun read(file: File): CacheResult {
        return when (val result = inner.read(file)) {
            is CacheResult.Hit -> CacheResult.Stale(file, result.file.addedMs)
            else -> result
        }
    }

    override suspend fun write(cachedFile: CachedFile) {
        inner.write(cachedFile)
    }

    override suspend fun cleanup(excluding: List<CachedFile>) {
        inner.cleanup(excluding)
    }
}

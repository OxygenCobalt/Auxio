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

import android.content.Context
import org.oxycblt.musikr.cover.StoredCovers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.pipeline.RawSong

abstract class Cache {
    internal abstract suspend fun read(file: DeviceFile, storedCovers: StoredCovers): CacheResult

    internal abstract suspend fun write(song: RawSong)
}

interface StoredCache {
    fun full(): Cache

    fun writeOnly(): Cache

    companion object {
        fun from(context: Context): StoredCache = StoredCacheImpl(CacheDatabase.from(context))
    }
}

internal sealed interface CacheResult {
    data class Hit(val song: RawSong) : CacheResult

    data class Miss(val file: DeviceFile, val dateAdded: Long?) : CacheResult
}

private class StoredCacheImpl(private val database: CacheDatabase) : StoredCache {
    override fun full() = CacheImpl(database.cachedSongsDao())

    override fun writeOnly() = WriteOnlyCacheImpl(database.cachedSongsDao())
}

private class CacheImpl(private val cacheInfoDao: CacheInfoDao) : Cache() {
    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers): CacheResult {
        val song = cacheInfoDao.selectSong(file.uri.toString()) ?:
            return CacheResult.Miss(file, null)
        if (song.dateModified != file.lastModified) {
            return CacheResult.Miss(file, song.dateAdded)
        }
        return CacheResult.Hit(song.intoRawSong(file, storedCovers))
    }

    override suspend fun write(song: RawSong) =
        cacheInfoDao.updateSong(CachedSong.fromRawSong(song))
}

private class WriteOnlyCacheImpl(private val cacheInfoDao: CacheInfoDao) : Cache() {
    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers) =
        CacheResult.Miss(file, cacheInfoDao.selectDateAdded(file.uri.toString()))

    override suspend fun write(song: RawSong) =
        cacheInfoDao.updateSong(CachedSong.fromRawSong(song))
}

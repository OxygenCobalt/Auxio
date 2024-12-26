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
    internal abstract fun lap(): Long

    internal abstract suspend fun read(file: DeviceFile, storedCovers: StoredCovers): CacheResult

    internal abstract suspend fun write(song: RawSong)

    internal abstract suspend fun prune(timestamp: Long)
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

    data class Miss(val file: DeviceFile, val addedMs: Long?) : CacheResult
}

private class StoredCacheImpl(private val database: CacheDatabase) : StoredCache {
    override fun full() = CacheImpl(database.cachedSongsDao())

    override fun writeOnly() = WriteOnlyCacheImpl(database.cachedSongsDao())
}

private class CacheImpl(private val cacheInfoDao: CacheInfoDao) : Cache() {
    override fun lap() = System.nanoTime()

    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers): CacheResult {
        val song = cacheInfoDao.selectSong(file.uri.toString()) ?:
            return CacheResult.Miss(file, null)
        if (song.modifiedMs != file.lastModified) {
            // We *found* this file earlier, but it's out of date.
            // Send back it with the timestamp so it will be re-used.
            // The touch timestamp will be updated on write.
            return CacheResult.Miss(file, song.addedMs)
        }
        // Valid file, update the touch time.
        cacheInfoDao.touch(file.uri.toString())
        return CacheResult.Hit(song.intoRawSong(file, storedCovers))
    }

    override suspend fun write(song: RawSong) =
        cacheInfoDao.updateSong(CachedSong.fromRawSong(song))

    override suspend fun prune(timestamp: Long) {
        cacheInfoDao.pruneOlderThan(timestamp)
    }
}

private class WriteOnlyCacheImpl(private val cacheInfoDao: CacheInfoDao) : Cache() {
    override fun lap() = System.nanoTime()

    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers) =
        CacheResult.Miss(file, cacheInfoDao.selectAddedMs(file.uri.toString()))

    override suspend fun write(song: RawSong) =
        cacheInfoDao.updateSong(CachedSong.fromRawSong(song))

    override suspend fun prune(timestamp: Long) {
        cacheInfoDao.pruneOlderThan(timestamp)
    }
}

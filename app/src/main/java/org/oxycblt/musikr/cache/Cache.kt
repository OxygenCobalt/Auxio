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

import org.oxycblt.musikr.fs.query.DeviceFile
import org.oxycblt.musikr.pipeline.RawSong

interface Cache {
    suspend fun read(file: DeviceFile): CacheResult

    suspend fun write(song: RawSong)

    companion object {
        fun full(db: CacheDatabase): Cache = FullCache(db.cachedSongsDao())

        fun writeOnly(db: CacheDatabase): Cache = WriteOnlyCache(db.cachedSongsDao())
    }
}

sealed interface CacheResult {
    data class Hit(val song: RawSong) : CacheResult

    data class Miss(val file: DeviceFile) : CacheResult
}

private class FullCache(private val cacheInfoDao: CacheInfoDao) : Cache {
    override suspend fun read(file: DeviceFile) =
        cacheInfoDao.selectSong(file.uri.toString(), file.lastModified)?.let {
            CacheResult.Hit(it.intoRawSong(file))
        } ?: CacheResult.Miss(file)

    override suspend fun write(song: RawSong) =
        cacheInfoDao.updateSong(CachedSong.fromRawSong(song))
}

private class WriteOnlyCache(private val cacheInfoDao: CacheInfoDao) : Cache {
    override suspend fun read(file: DeviceFile) = CacheResult.Miss(file)

    override suspend fun write(song: RawSong) =
        cacheInfoDao.updateSong(CachedSong.fromRawSong(song))
}

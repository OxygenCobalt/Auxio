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
import org.oxycblt.musikr.fs.query.DeviceFile
import org.oxycblt.musikr.tag.parse.ParsedTags

interface Cache {
    suspend fun read(file: DeviceFile): CachedSong?

    suspend fun write(file: DeviceFile, song: CachedSong)

    companion object {
        fun full(db: CacheDatabase): Cache = FullCache(db.cachedSongsDao())

        fun writeOnly(db: CacheDatabase): Cache = WriteOnlyCache(db.cachedSongsDao())
    }
}

data class CachedSong(val parsedTags: ParsedTags, val cover: Cover?)

private class FullCache(private val cacheInfoDao: CacheInfoDao) : Cache {
    override suspend fun read(file: DeviceFile) =
        cacheInfoDao.selectInfo(file.uri.toString(), file.lastModified)?.intoCachedSong()

    override suspend fun write(file: DeviceFile, song: CachedSong) =
        cacheInfoDao.updateInfo(CachedInfo.fromCachedSong(file, song))
}

private class WriteOnlyCache(private val cacheInfoDao: CacheInfoDao) : Cache {
    override suspend fun read(file: DeviceFile) = null

    override suspend fun write(file: DeviceFile, song: CachedSong) =
        cacheInfoDao.updateInfo(CachedInfo.fromCachedSong(file, song))
}

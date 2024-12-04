/*
 * Copyright (c) 2024 Auxio Project
 * CoverCache.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.cover

import java.io.InputStream
import javax.inject.Inject

interface CoverCache {
    suspend fun read(cover: Cover.Single): InputStream?

    suspend fun write(cover: Cover.Single, data: ByteArray): InputStream?
}

class CoverCacheImpl
@Inject
constructor(
    private val coverIdentifier: CoverIdentifier,
    private val storedCoversDao: StoredCoversDao,
    private val coverFiles: CoverFiles
) : CoverCache {

    override suspend fun read(cover: Cover.Single): InputStream? {
        val id = storedCoversDao.getStoredCoverId(cover.uid, cover.lastModified) ?: return null
        return coverFiles.read(id)
    }

    override suspend fun write(cover: Cover.Single, data: ByteArray): InputStream? {
        val id = coverIdentifier.identify(data)
        coverFiles.write(id, data)
        storedCoversDao.setStoredCover(
            StoredCover(uid = cover.uid, lastModified = cover.lastModified, coverId = id))
        return coverFiles.read(id)
    }
}

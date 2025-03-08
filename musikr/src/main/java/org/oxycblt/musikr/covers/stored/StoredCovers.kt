/*
 * Copyright (c) 2025 Auxio Project
 * StoredCovers.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers.stored

import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.covers.FDCover
import org.oxycblt.musikr.covers.MemoryCover
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

class StoredCovers(private val coverStorage: CoverStorage) : Covers<FDCover> {
    override suspend fun obtain(id: String): CoverResult<FDCover> {
        val cover = coverStorage.find(id) ?: return CoverResult.Miss()
        return CoverResult.Hit(cover)
    }
}

class MutableStoredCovers(
    private val src: MutableCovers<MemoryCover>,
    private val coverStorage: CoverStorage,
    private val transcoding: Transcoding
) : MutableCovers<FDCover> {
    private val base = StoredCovers(coverStorage)

    override suspend fun obtain(id: String): CoverResult<FDCover> = base.obtain(id)

    override suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<FDCover> {
        val cover =
            when (val cover = src.create(file, metadata)) {
                is CoverResult.Hit -> cover.cover
                is CoverResult.Miss -> return CoverResult.Miss()
            }
        val coverFile = coverStorage.write(cover.id + transcoding.tag) { it.write(cover.data()) }
        return CoverResult.Hit(coverFile)
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        src.cleanup(excluding)
        val used = excluding.mapTo(mutableSetOf()) { it.id }
        val unused = coverStorage.ls(exclude = used).filter { it !in used }
        for (file in unused) {
            coverStorage.rm(file)
        }
    }
}

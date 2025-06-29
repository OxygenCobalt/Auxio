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
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.metadata.Metadata

private const val PREFIX = "mcs:"

/**
 * A [Covers] implementation for stored covers in the backing [CoverStorage].
 *
 * Note that this instance is [Transcoding]-agnostic, it will yield a cover as long as it exists
 * somewhere in the given storage.
 *
 * See [MutableStoredCovers] for the mutable variant.
 *
 * @param coverStorage The [CoverStorage] to use to obtain the cover data.
 */
class StoredCovers(private val coverStorage: CoverStorage) : Covers<FDCover> {
    override suspend fun obtain(id: String): CoverResult<FDCover> {
        if (!id.startsWith(PREFIX)) {
            return CoverResult.Miss()
        }
        val file = id.substring(PREFIX.length)
        val cover = coverStorage.find(file) ?: return CoverResult.Miss()
        return CoverResult.Hit(StoredCover(cover))
    }
}

/**
 * A [MutableCovers] implementation for stored covers in the backing [CoverStorage].
 *
 * This will open whatever cover data is yielded by [src], and then write it to the [coverStorage]
 * using the whatever [transcoding] is provided.
 *
 * This allows large in-memory covers yielded by [MutableCovers] to be cached in storage rather than
 * kept in memory. However, it can be used for any asynchronously fetched covers as well to save
 * time, such as ones obtained by network.
 *
 * See [StoredCovers] for the immutable variant.
 *
 * @param src The [MutableCovers] to use to obtain the cover data.
 * @param coverStorage The [CoverStorage] to use to write the cover data to.
 * @param transcoding The [Transcoding] to use to write the cover data to the [coverStorage].
 */
class MutableStoredCovers(
    private val src: MutableCovers<MemoryCover>,
    private val coverStorage: CoverStorage,
    private val transcoding: Transcoding
) : MutableCovers<FDCover> {
    private val base = StoredCovers(coverStorage)

    override suspend fun obtain(id: String): CoverResult<FDCover> = base.obtain(id)

    override suspend fun create(file: File, metadata: Metadata): CoverResult<FDCover> {
        val memoryCover =
            when (val cover = src.create(file, metadata)) {
                is CoverResult.Hit -> cover.cover
                is CoverResult.Miss -> return CoverResult.Miss()
            }
        val innerCover =
            try {
                coverStorage.write(memoryCover.id + transcoding.tag) {
                    transcoding.transcodeInto(memoryCover.data(), it)
                }
            } catch (e: Exception) {
                return CoverResult.Miss()
            }
        return CoverResult.Hit(StoredCover(innerCover))
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        src.cleanup(excluding)
        val used =
            excluding.mapNotNullTo(mutableSetOf()) {
                it.id.takeIf { id -> id.startsWith(PREFIX) }?.substring(PREFIX.length)
            }
        val unused = coverStorage.ls(exclude = used).filter { it !in used }
        for (file in unused) {
            coverStorage.rm(file)
        }
    }
}

private class StoredCover(private val inner: FDCover) : FDCover by inner {
    override val id = PREFIX + inner.id
}

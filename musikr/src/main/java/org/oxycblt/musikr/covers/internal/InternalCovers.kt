/*
 * Copyright (c) 2025 Auxio Project
 * InternalCovers.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers.internal

import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.covers.FDCover
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.fs.app.AppFS
import org.oxycblt.musikr.fs.app.AppFile
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

open class InternalCovers(private val appFS: AppFS, private val coverFormat: CoverFormat) :
    Covers<FDCover> {
    override suspend fun obtain(id: String): CoverResult<FDCover> {
        val file = appFS.find(getFileName(id))
        return if (file != null) {
            CoverResult.Hit(InternalCoverImpl(id, file))
        } else {
            CoverResult.Miss()
        }
    }

    protected fun getFileName(id: String) = "$id.${coverFormat.extension}"
}

class MutableInternalCovers(
    private val appFS: AppFS,
    private val coverFormat: CoverFormat,
    private val coverIdentifier: CoverIdentifier
) : InternalCovers(appFS, coverFormat), MutableCovers<FDCover> {
    override suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<FDCover> {
        val data = metadata.cover ?: return CoverResult.Miss()
        val id = coverIdentifier.identify(data)
        val coverFile = appFS.write(getFileName(id)) { coverFormat.transcodeInto(data, it) }
        return CoverResult.Hit(InternalCoverImpl(id, coverFile))
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        val used = excluding.mapTo(mutableSetOf()) { getFileName(it.id) }
        appFS.deleteWhere { it !in used }
    }
}

private data class InternalCoverImpl(override val id: String, private val appFile: AppFile) :
    FDCover {
    override suspend fun fd() = appFile.fd()

    override suspend fun open() = appFile.open()
}

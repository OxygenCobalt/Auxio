/*
 * Copyright (c) 2025 Auxio Project
 * FSCovers.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cover.fs

import android.os.ParcelFileDescriptor
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.cover.ObtainResult
import org.oxycblt.musikr.fs.app.AppFS
import org.oxycblt.musikr.fs.app.AppFile

open class FSCovers(private val appFS: AppFS, private val coverFormat: CoverFormat) : Covers {
    override suspend fun obtain(id: String): ObtainResult<FileCover> {
        val file = appFS.find(getFileName(id))
        return if (file != null) {
            ObtainResult.Hit(FileCoverImpl(id, file))
        } else {
            ObtainResult.Miss()
        }
    }

    protected fun getFileName(id: String) = "$id.${coverFormat.extension}"
}

class MutableFSCovers(
    private val appFS: AppFS,
    private val coverFormat: CoverFormat,
    private val coverIdentifier: CoverIdentifier
) : FSCovers(appFS, coverFormat), MutableCovers {
    override suspend fun write(data: ByteArray): FileCover {
        val id = coverIdentifier.identify(data)
        val file = appFS.write(getFileName(id)) { coverFormat.transcodeInto(data, it) }
        return FileCoverImpl(id, file)
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        val used = excluding.mapTo(mutableSetOf()) { getFileName(it.id) }
        appFS.deleteWhere { it !in used }
    }
}

interface FileCover : Cover {
    suspend fun fd(): ParcelFileDescriptor?
}

private data class FileCoverImpl(override val id: String, private val appFile: AppFile) :
    FileCover {
    override suspend fun fd() = appFile.fd()

    override suspend fun open() = appFile.open()
}

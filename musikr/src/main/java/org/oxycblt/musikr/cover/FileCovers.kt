/*
 * Copyright (c) 2025 Auxio Project
 * FileCovers.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cover

import android.os.ParcelFileDescriptor
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.fs.app.AppFile
import org.oxycblt.musikr.fs.app.AppFiles
import org.oxycblt.musikr.metadata.Metadata

open class FileCovers(private val appFiles: AppFiles, private val coverFormat: CoverFormat) :
    Covers<FileCover> {
    override suspend fun obtain(id: String): CoverResult<FileCover> {
        val file = appFiles.find(getFileName(id))
        return if (file != null) {
            CoverResult.Hit(FileCoverImpl(id, file))
        } else {
            CoverResult.Miss()
        }
    }

    protected fun getFileName(id: String) = "$id.${coverFormat.extension}"
}

class MutableFileCovers(
    private val appFiles: AppFiles,
    private val coverFormat: CoverFormat,
    private val coverIdentifier: CoverIdentifier
) : FileCovers(appFiles, coverFormat), MutableCovers<FileCover> {
    override suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<FileCover> {
        val data = metadata.cover ?: return CoverResult.Miss()
        val id = coverIdentifier.identify(data)
        val coverFile = appFiles.write(getFileName(id)) { coverFormat.transcodeInto(data, it) }
        return CoverResult.Hit(FileCoverImpl(id, coverFile))
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        val used = excluding.mapTo(mutableSetOf()) { getFileName(it.id) }
        appFiles.deleteWhere { it !in used }
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

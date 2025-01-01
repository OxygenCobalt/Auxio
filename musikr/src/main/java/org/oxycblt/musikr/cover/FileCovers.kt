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
import org.oxycblt.musikr.fs.app.AppFile
import org.oxycblt.musikr.fs.app.AppFiles

class FileCovers(
    private val appFiles: AppFiles,
    private val coverFormat: CoverFormat,
    private val coverIdentifier: CoverIdentifier,
) : Covers, MutableCovers {
    override suspend fun obtain(id: String): ObtainResult<FileCover> {
        val file = appFiles.find(getFileName(id))
        return if (file != null) {
            ObtainResult.Hit(FileCoverImpl(id, file))
        } else {
            ObtainResult.Miss()
        }
    }

    override suspend fun write(data: ByteArray): FileCover {
        val id = coverIdentifier.identify(data)
        val file = appFiles.write(getFileName(id)) { coverFormat.transcodeInto(data, it) }
        return FileCoverImpl(id, file)
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {
        val used = excluding.mapTo(mutableSetOf()) { getFileName(it.id) }
        appFiles.deleteWhere { it !in used }
    }

    private fun getFileName(id: String) = "$id.${coverFormat.extension}"
}

interface FileCover : Cover {
    suspend fun fd(): ParcelFileDescriptor?
}

private class FileCoverImpl(override val id: String, private val appFile: AppFile) : FileCover {
    override suspend fun fd() = appFile.fd()

    override suspend fun open() = appFile.open()
}

/*
 * Copyright (c) 2024 Auxio Project
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

package org.oxycblt.musikr.cover

import android.content.Context
import android.util.Log
import org.oxycblt.musikr.fs.Components
import java.io.File
import java.io.InputStream

interface StoredCovers {
    suspend fun find(id: String): Cover.Single?

    interface Editor : StoredCovers {
        suspend fun add(data: ByteArray): Cover.Single?
    }

    companion object {
        fun from(context: Context): StoredCovers =
            FileStoredCovers(AppFiles.from(context))

        fun editor(context: Context, path: Components): Editor =
            FileStoredCoversEditor(
                path,
                AppFiles.from(context),
                CoverIdentifier.md5(),
                CoverFormat.webp()
            )
    }
}

private open class FileStoredCovers(
    private val appFiles: AppFiles
) : StoredCovers {
    override suspend fun find(id: String) =
        appFiles.read(Components.parseUnix(id))?.let { FileCover(it) }
}

private class FileStoredCoversEditor(
    val root: Components,
    val appFiles: AppFiles,
    val coverIdentifier: CoverIdentifier,
    val coverFormat: CoverFormat
) : FileStoredCovers(appFiles), StoredCovers.Editor {
    override suspend fun add(data: ByteArray): Cover.Single? {
        val id = coverIdentifier.identify(data)
        val path = getTargetPath(id)
        val file = appFiles.write(path) {
            coverFormat.transcodeInto(data, it)
        }
        return file?.let { FileCover(it) }
    }

    private fun getTargetPath(id: String) = root.child("$id.${coverFormat.extension}")
}

private class FileCover(private val file: AppFile) : Cover.Single {
    override val id: String = file.path.unixString

    override suspend fun resolve() = file.open()
}
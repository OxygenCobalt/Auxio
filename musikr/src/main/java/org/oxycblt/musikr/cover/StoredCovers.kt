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

interface StoredCovers {
    suspend fun obtain(id: String): Cover.Single?

    companion object {
        fun at(context: Context, path: String): MutableStoredCovers =
            FileStoredCovers(CoverIdentifier.md5(), CoverFiles.at(context, path))
    }
}

interface MutableStoredCovers : StoredCovers {
    suspend fun write(data: ByteArray): Cover.Single?
}

private class FileStoredCovers(
    private val coverIdentifier: CoverIdentifier,
    private val coverFiles: CoverFiles
) : StoredCovers, MutableStoredCovers {
    override suspend fun obtain(id: String) = coverFiles.find(id)?.let { FileCover(id, it) }

    override suspend fun write(data: ByteArray) =
        coverIdentifier.identify(data).let { id ->
            coverFiles.write(id, data)?.let { FileCover(id, it) }
        }
}

private class FileCover(override val id: String, private val coverFile: CoverFile) : Cover.Single {
    override suspend fun open() = coverFile.open()
}

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

import org.oxycblt.musikr.Library

interface StoredCovers {
    suspend fun obtain(id: String): Cover?

    companion object {
        fun from(
            coverFiles: CoverFiles,
            coverFormat: CoverFormat,
            identifier: CoverIdentifier = CoverIdentifier.md5()
        ): MutableStoredCovers = FileStoredCovers(coverFiles, coverFormat, identifier)
    }
}

interface MutableStoredCovers : StoredCovers {
    suspend fun write(data: ByteArray): Cover?

    suspend fun cleanup(assuming: Library)
}

private class FileStoredCovers(
    private val coverFiles: CoverFiles,
    private val coverFormat: CoverFormat,
    private val coverIdentifier: CoverIdentifier,
) : StoredCovers, MutableStoredCovers {
    override suspend fun obtain(id: String) =
        coverFiles.find(getFileName(id))?.let { FileCover(id, it) }

    override suspend fun write(data: ByteArray): Cover? {
        val id = coverIdentifier.identify(data)
        return coverFiles
            .write(getFileName(id)) { coverFormat.transcodeInto(data, it) }
            ?.let { FileCover(id, it) }
    }

    override suspend fun cleanup(assuming: Library) {
        val used = assuming.songs.mapNotNullTo(mutableSetOf()) { it.cover?.id?.let(::getFileName) }
        coverFiles.deleteWhere { it !in used }
    }

    private fun getFileName(id: String) = "$id.${coverFormat.extension}"
}

private class FileCover(override val id: String, private val coverFile: CoverFile) : Cover {
    override suspend fun open() = coverFile.open()
}

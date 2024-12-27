/*
 * Copyright (c) 2024 Auxio Project
 * Covers.kt is part of Auxio.
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

interface Covers {
    suspend fun obtain(id: String): ObtainResult

    companion object {
        fun from(
            coverFiles: CoverFiles,
            coverFormat: CoverFormat,
            identifier: CoverIdentifier = CoverIdentifier.md5()
        ): MutableCovers = FileCovers(coverFiles, coverFormat, identifier)
    }
}

interface MutableCovers : Covers {
    suspend fun write(data: ByteArray): Cover

    suspend fun cleanup(assuming: Library)
}

sealed interface ObtainResult {
    data class Hit(val cover: Cover) : ObtainResult

    data object Miss : ObtainResult
}

private class FileCovers(
    private val coverFiles: CoverFiles,
    private val coverFormat: CoverFormat,
    private val coverIdentifier: CoverIdentifier,
) : Covers, MutableCovers {
    override suspend fun obtain(id: String): ObtainResult {
        val file = coverFiles.find(getFileName(id))
        return if (file != null) {
            ObtainResult.Hit(FileCover(id, file))
        } else {
            ObtainResult.Miss
        }
    }

    override suspend fun write(data: ByteArray): Cover {
        val id = coverIdentifier.identify(data)
        val file = coverFiles.write(getFileName(id)) { coverFormat.transcodeInto(data, it) }
        return FileCover(id, file)
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

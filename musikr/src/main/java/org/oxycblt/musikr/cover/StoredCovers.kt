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
import java.io.InputStream

interface StoredCovers {
    suspend fun read(cover: Cover.Single): InputStream?

    suspend fun write(data: ByteArray): Cover.Single?

    companion object {
        fun from(context: Context, path: String): StoredCovers =
            FileStoredCovers(
                CoverIdentifier.md5(), CoverFiles.from(context, path, CoverFormat.webp()))
    }
}

private class FileStoredCovers(
    private val coverIdentifier: CoverIdentifier,
    private val coverFiles: CoverFiles
) : StoredCovers {
    override suspend fun read(cover: Cover.Single) = coverFiles.read(cover.key)

    override suspend fun write(data: ByteArray) =
        coverIdentifier.identify(data).let { key ->
            coverFiles.write(key, data)
            Cover.Single(key)
        }
}

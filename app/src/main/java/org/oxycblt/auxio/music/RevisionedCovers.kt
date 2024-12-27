/*
 * Copyright (c) 2024 Auxio Project
 * RevisionedCovers.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music

import android.content.Context
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverFiles
import org.oxycblt.musikr.cover.CoverFormat
import org.oxycblt.musikr.cover.CoverParams
import org.oxycblt.musikr.cover.MutableStoredCovers
import org.oxycblt.musikr.cover.StoredCovers

class RevisionedCovers(private val revision: UUID, private val inner: MutableStoredCovers) :
    MutableStoredCovers {
    override suspend fun obtain(id: String): RevisionedCover? {
        val (coverId, coverRevision) = parse(id) ?: return null
        if (coverRevision != revision) return null
        return inner.obtain(coverId)?.let { RevisionedCover(revision, it) }
    }

    override suspend fun write(data: ByteArray): RevisionedCover? {
        return inner.write(data)?.let { RevisionedCover(revision, it) }
    }

    suspend fun cleanup(context: Context) =
        withContext(Dispatchers.IO) {
            val exclude = revision.toString()
            context.filesDir
                .resolve("covers")
                .listFiles { file -> file.name != exclude }
                ?.forEach { it.deleteRecursively() }
        }

    companion object {
        suspend fun at(context: Context, revision: UUID): RevisionedCovers {
            val dir =
                withContext(Dispatchers.IO) {
                    context.filesDir.resolve("covers/${revision}").apply { mkdirs() }
                }
            return RevisionedCovers(
                revision,
                StoredCovers.from(CoverFiles.at(dir), CoverFormat.jpeg(CoverParams.of(750, 80))))
        }

        private fun parse(id: String): Pair<String, UUID>? {
            val split = id.split('@', limit = 2)
            if (split.size != 2) return null
            val (coverId, coverRevisionStr) = split
            val coverRevision = coverRevisionStr.toUuidOrNull() ?: return null
            return coverId to coverRevision
        }
    }
}

class RevisionedCover(private val revision: UUID, val inner: Cover) : Cover by inner {
    override val id: String
        get() = "${inner.id}@${revision}"
}

private fun String.toUuidOrNull(): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }

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
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.Library
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverFiles
import org.oxycblt.musikr.cover.CoverFormat
import org.oxycblt.musikr.cover.CoverParams
import org.oxycblt.musikr.cover.MutableStoredCovers
import org.oxycblt.musikr.cover.StoredCovers

class RevisionedCovers(
    private val rootDir: File,
    private val revision: UUID,
    private val inner: MutableStoredCovers
) : MutableStoredCovers {
    override suspend fun obtain(id: String): RevisionedCover? {
        val (coverId, coverRevision) = parse(id) ?: return null
        if (coverRevision != revision) return null
        return inner.obtain(coverId)?.let { RevisionedCover(revision, it) }
    }

    override suspend fun write(data: ByteArray): RevisionedCover? {
        return inner.write(data)?.let { RevisionedCover(revision, it) }
    }

    override suspend fun cleanup(assuming: Library) {
        inner.cleanup(assuming)

        // Destroy old revisions no longer being used.
        withContext(Dispatchers.IO) {
            val exclude = revision.toString()
            rootDir.listFiles { file -> file.name != exclude }?.forEach { it.deleteRecursively() }
        }
    }

    companion object {
        suspend fun at(context: Context, revision: UUID): RevisionedCovers {
            val rootDir: File
            val revisionDir: File
            withContext(Dispatchers.IO) {
                rootDir = context.filesDir.resolve("covers").apply { mkdirs() }
                revisionDir = rootDir.resolve(revision.toString()).apply { mkdirs() }
            }
            val files = CoverFiles.at(revisionDir)
            val format = CoverFormat.jpeg(CoverParams.of(750, 80))
            return RevisionedCovers(rootDir, revision, StoredCovers.from(files, format))
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

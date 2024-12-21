/*
 * Copyright (c) 2024 Auxio Project
 * RevisionedLibrary.kt is part of Auxio.
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
import org.oxycblt.auxio.util.unlikelyToBeNull
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverFormat
import org.oxycblt.musikr.cover.MutableStoredCovers
import org.oxycblt.musikr.cover.StoredCovers

open class RevisionedStoredCovers(private val context: Context, private val revision: UUID?) :
    StoredCovers {
    protected val inner =
        revision?.let { StoredCovers.from(context, "covers_$it", CoverFormat.jpeg()) }

    override suspend fun obtain(id: String): RevisionedCover? {
        val split = id.split('@', limit = 2)
        if (split.size != 2) return null
        val (coverId, coverRevisionStr) = split
        val coverRevision = coverRevisionStr.toUuidOrNull() ?: return null
        if (revision != null) {
            if (coverRevision != revision) {
                return null
            }
            val storedCovers = unlikelyToBeNull(inner)
            return storedCovers.obtain(coverId)?.let { RevisionedCover(revision, it) }
        } else {
            val storedCovers =
                StoredCovers.from(context, "covers_$coverRevision", CoverFormat.jpeg())
            return storedCovers.obtain(coverId)?.let { RevisionedCover(coverRevision, it) }
        }
    }
}

class MutableRevisionedStoredCovers(context: Context, private val revision: UUID) :
    RevisionedStoredCovers(context, revision), MutableStoredCovers {
    override suspend fun write(data: ByteArray): RevisionedCover? {
        return unlikelyToBeNull(inner).write(data)?.let { RevisionedCover(revision, it) }
    }
}

class RevisionedCover(private val revision: UUID, val inner: Cover.Single) : Cover.Single by inner {
    override val id: String
        get() = "${inner.id}@${revision}"
}

internal fun String.toUuidOrNull(): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }

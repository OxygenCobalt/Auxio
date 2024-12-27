/*
 * Copyright (c) 2024 Auxio Project
 * SiloedCovers.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.covers

import android.content.Context
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverFiles
import org.oxycblt.musikr.cover.CoverFormat
import org.oxycblt.musikr.cover.CoverParams
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.cover.ObtainResult

class SiloedCovers(
    private val rootDir: File,
    private val silo: CoverSilo,
    private val inner: MutableCovers
) : MutableCovers {
    override suspend fun obtain(id: String): ObtainResult {
        val coverId = SiloedCoverId.parse(id) ?: return ObtainResult.Miss
        if (coverId.silo != silo) return ObtainResult.Miss
        return when (val result = inner.obtain(coverId.id)) {
            is ObtainResult.Hit -> ObtainResult.Hit(SiloedCover(silo, result.cover))
            is ObtainResult.Miss -> ObtainResult.Miss
        }
    }

    override suspend fun write(data: ByteArray) = SiloedCover(silo, inner.write(data))

    override suspend fun cleanup(excluding: Collection<Cover>) {
        inner.cleanup(excluding.filterIsInstance<SiloedCover>().map { it.innerCover })

        // Destroy old revisions no longer being used.
        withContext(Dispatchers.IO) {
            val exclude = silo.toString()
            rootDir.listFiles { file -> file.name != exclude }?.forEach { it.deleteRecursively() }
        }
    }

    companion object {
        suspend fun at(context: Context, silo: CoverSilo): SiloedCovers {
            val rootDir: File
            val revisionDir: File
            withContext(Dispatchers.IO) {
                rootDir = context.filesDir.resolve("covers").apply { mkdirs() }
                revisionDir = rootDir.resolve(silo.toString()).apply { mkdirs() }
            }
            val files = CoverFiles.at(revisionDir)
            val format = CoverFormat.jpeg(silo.params)
            return SiloedCovers(rootDir, silo, Covers.from(files, format))
        }
    }
}

data class CoverSilo(val revision: UUID, val params: CoverParams) {
    override fun toString() = "${revision}.${params.resolution}.${params.quality}"

    companion object {
        fun parse(silo: String): CoverSilo? {
            val parts = silo.split('.')
            if (parts.size != 3) return null
            val revision = parts[0].toUuidOrNull() ?: return null
            val resolution = parts[1].toIntOrNull() ?: return null
            val quality = parts[2].toIntOrNull() ?: return null
            return CoverSilo(revision, CoverParams.of(resolution, quality))
        }
    }
}

class SiloedCover(silo: CoverSilo, val innerCover: Cover) : Cover by innerCover {
    private val innerId = SiloedCoverId(silo, innerCover.id)
    override val id = innerId.toString()
}

data class SiloedCoverId(val silo: CoverSilo, val id: String) {
    override fun toString() = "$id@$silo"

    companion object {
        fun parse(id: String): SiloedCoverId? {
            val parts = id.split('@')
            if (parts.size != 2) return null
            val silo = CoverSilo.parse(parts[1]) ?: return null
            return SiloedCoverId(silo, parts[0])
        }
    }
}

private fun String.toUuidOrNull(): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }

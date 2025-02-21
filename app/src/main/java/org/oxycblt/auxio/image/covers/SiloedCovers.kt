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
 
package org.oxycblt.auxio.image.covers

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverFormat
import org.oxycblt.musikr.cover.CoverIdentifier
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.FileCover
import org.oxycblt.musikr.cover.FileCovers
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.cover.MutableFileCovers
import org.oxycblt.musikr.cover.ObtainResult
import org.oxycblt.musikr.fs.app.AppFiles

open class SiloedCovers(private val silo: CoverSilo, private val fileCovers: FileCovers) : Covers {
    override suspend fun obtain(id: String): ObtainResult<SiloedCover> {
        val coverId = SiloedCoverId.parse(id) ?: return ObtainResult.Miss()
        if (coverId.silo != silo) return ObtainResult.Miss()
        return when (val result = fileCovers.obtain(coverId.id)) {
            is ObtainResult.Hit -> ObtainResult.Hit(SiloedCover(silo, result.cover))
            is ObtainResult.Miss -> ObtainResult.Miss()
        }
    }

    companion object {
        suspend fun from(context: Context, silo: CoverSilo): SiloedCovers {
            val core = SiloCore.from(context, silo)
            return SiloedCovers(silo, FileCovers(core.files, core.format))
        }
    }
}

class MutableSiloedCovers
private constructor(
    private val rootDir: File,
    private val silo: CoverSilo,
    private val fileCovers: MutableFileCovers
) : SiloedCovers(silo, fileCovers), MutableCovers {
    override suspend fun write(data: ByteArray) = SiloedCover(silo, fileCovers.write(data))

    override suspend fun cleanup(excluding: Collection<Cover>) {
        fileCovers.cleanup(excluding.filterIsInstance<SiloedCover>().map { it.innerCover })

        // Destroy old revisions no longer being used.
        withContext(Dispatchers.IO) {
            val exclude = silo.toString()
            rootDir.listFiles { file -> file.name != exclude }?.forEach { it.deleteRecursively() }
        }
    }

    companion object {
        suspend fun from(
            context: Context,
            silo: CoverSilo,
            coverIdentifier: CoverIdentifier
        ): MutableSiloedCovers {
            val core = SiloCore.from(context, silo)
            return MutableSiloedCovers(
                core.rootDir, silo, MutableFileCovers(core.files, core.format, coverIdentifier))
        }
    }
}

data class SiloedCover(private val silo: CoverSilo, val innerCover: FileCover) :
    FileCover by innerCover {
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

private data class SiloCore(val rootDir: File, val files: AppFiles, val format: CoverFormat) {
    companion object {
        suspend fun from(context: Context, silo: CoverSilo): SiloCore {
            val rootDir: File
            val revisionDir: File
            withContext(Dispatchers.IO) {
                rootDir = context.coversDir()
                revisionDir = rootDir.resolve(silo.toString()).apply { mkdirs() }
            }
            val files = AppFiles.at(revisionDir)
            val format = CoverFormat.jpeg(silo.params)
            return SiloCore(rootDir, files, format)
        }
    }
}

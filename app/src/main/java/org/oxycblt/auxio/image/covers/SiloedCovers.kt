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
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.covers.FDCover
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.covers.embedded.CoverFormat
import org.oxycblt.musikr.covers.embedded.CoverIdentifier
import org.oxycblt.musikr.covers.embedded.EmbeddedCovers
import org.oxycblt.musikr.covers.embedded.MutableEmbeddedCovers
import org.oxycblt.musikr.fs.app.AppFS
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

class BaseSiloedCovers(private val context: Context) : Covers<FDCover> {
    override suspend fun obtain(id: String): CoverResult<FDCover> {
        val siloedId = SiloedCoverId.parse(id) ?: return CoverResult.Miss()
        val core = SiloCore.from(context, siloedId.silo)
        val embeddedCovers = EmbeddedCovers(core.files, core.format)
        return when (val result = embeddedCovers.obtain(siloedId.id)) {
            is CoverResult.Hit -> CoverResult.Hit(SiloedCover(siloedId.silo, result.cover))
            is CoverResult.Miss -> CoverResult.Miss()
        }
    }
}

open class SiloedCovers(private val silo: CoverSilo, private val embeddedCovers: EmbeddedCovers) :
    Covers<FDCover> {
    override suspend fun obtain(id: String): CoverResult<FDCover> {
        val coverId = SiloedCoverId.parse(id) ?: return CoverResult.Miss()
        if (silo != coverId.silo) return CoverResult.Miss()
        return when (val result = embeddedCovers.obtain(coverId.id)) {
            is CoverResult.Hit -> CoverResult.Hit(SiloedCover(silo, result.cover))
            is CoverResult.Miss -> CoverResult.Miss()
        }
    }

    companion object {
        suspend fun from(context: Context, silo: CoverSilo): SiloedCovers {
            val core = SiloCore.from(context, silo)
            return SiloedCovers(silo, EmbeddedCovers(core.files, core.format))
        }
    }
}

class MutableSiloedCovers
private constructor(
    private val rootDir: File,
    private val silo: CoverSilo,
    private val fileCovers: MutableEmbeddedCovers
) : SiloedCovers(silo, fileCovers), MutableCovers<FDCover> {
    override suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<FDCover> =
        when (val result = fileCovers.create(file, metadata)) {
            is CoverResult.Hit -> CoverResult.Hit(SiloedCover(silo, result.cover))
            is CoverResult.Miss -> CoverResult.Miss()
        }

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
                core.rootDir, silo, MutableEmbeddedCovers(core.files, core.format, coverIdentifier))
        }
    }
}

data class SiloedCover(private val silo: CoverSilo, val innerCover: FDCover) :
    FDCover by innerCover {
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

private data class SiloCore(val rootDir: File, val files: AppFS, val format: CoverFormat) {
    companion object {
        suspend fun from(context: Context, silo: CoverSilo): SiloCore {
            val rootDir: File
            val revisionDir: File
            withContext(Dispatchers.IO) {
                rootDir = context.coversDir()
                revisionDir = rootDir.resolve(silo.toString()).apply { mkdirs() }
            }
            val files = AppFS.at(revisionDir)
            val format = silo.params?.let(CoverFormat::jpeg) ?: CoverFormat.asIs()
            return SiloCore(rootDir, files, format)
        }
    }
}

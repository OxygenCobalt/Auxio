/*
 * Copyright (c) 2024 Auxio Project
 * NullCovers.kt is part of Auxio.
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
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverIdentifier
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.cover.ObtainResult

class NullCovers(private val context: Context, private val identifier: CoverIdentifier) :
    MutableCovers {
    override suspend fun obtain(id: String) = ObtainResult.Hit(NullCover(id))

    override suspend fun write(data: ByteArray): Cover = NullCover(identifier.identify(data))

    override suspend fun cleanup(excluding: Collection<Cover>) {
        context.coversDir().listFiles()?.forEach { it.deleteRecursively() }
    }
}

class NullCover(override val id: String) : Cover {
    override suspend fun open() = null
}

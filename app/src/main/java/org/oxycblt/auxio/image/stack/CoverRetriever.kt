/*
 * Copyright (c) 2024 Auxio Project
 * CoverRetriever.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.stack

import java.io.InputStream
import javax.inject.Inject
import org.oxycblt.auxio.image.Cover
import org.oxycblt.auxio.image.stack.cache.CoverCache
import org.oxycblt.auxio.image.stack.extractor.CoverExtractor

interface CoverRetriever {
    suspend fun retrieve(cover: Cover.Single): InputStream?
}

class CoverRetrieverImpl
@Inject
constructor(private val coverCache: CoverCache, private val coverExtractor: CoverExtractor) :
    CoverRetriever {
    override suspend fun retrieve(cover: Cover.Single) =
        coverCache.read(cover) ?: coverExtractor.extract(cover)?.let { coverCache.write(cover, it) }
}

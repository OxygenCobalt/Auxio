/*
 * Copyright (c) 2021 Auxio Project
 * Components.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.extractor

import coil.ImageLoader
import coil.fetch.Fetcher
import coil.key.Keyer
import coil.request.Options
import coil.size.Size
import javax.inject.Inject

class CoverKeyer @Inject constructor() : Keyer<Collection<Cover>> {
    override fun key(data: Collection<Cover>, options: Options) =
        "${data.map { it.key }.hashCode()}"
}

class CoverFetcher
private constructor(
    private val covers: Collection<Cover>,
    private val size: Size,
    private val coverExtractor: CoverExtractor,
) : Fetcher {
    override suspend fun fetch() = coverExtractor.extract(covers, size)

    class Factory @Inject constructor(private val coverExtractor: CoverExtractor) :
        Fetcher.Factory<Collection<Cover>> {
        override fun create(data: Collection<Cover>, options: Options, imageLoader: ImageLoader) =
            CoverFetcher(data, options.size, coverExtractor)
    }
}

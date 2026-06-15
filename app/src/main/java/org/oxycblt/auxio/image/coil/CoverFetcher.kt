/*
 * Copyright (c) 2024 Auxio Project
 * CoverFetcher.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.coil

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import javax.inject.Inject
import okio.FileSystem
import okio.buffer
import okio.source
import org.oxycblt.musikr.covers.Cover

class CoverFetcher private constructor(private val cover: Cover) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val stream = cover.open() ?: return null
        return SourceFetchResult(
            source = ImageSource(stream.source().buffer(), FileSystem.SYSTEM, null),
            mimeType = null,
            dataSource = DataSource.DISK)
    }

    class Factory @Inject constructor() : Fetcher.Factory<Cover> {
        override fun create(data: Cover, options: Options, imageLoader: ImageLoader) =
            CoverFetcher(data)
    }
}

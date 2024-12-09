/*
 * Copyright (c) 2024 Auxio Project
 * TagCache.kt is part of Auxio.
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
 
package org.oxycblt.musikr.tag.cache

import javax.inject.Inject
import org.oxycblt.musikr.fs.query.DeviceFile
import org.oxycblt.musikr.tag.parse.ParsedTags

interface TagCache {
    suspend fun read(file: DeviceFile): ParsedTags?

    suspend fun write(file: DeviceFile, tags: ParsedTags)
}

class TagCacheImpl @Inject constructor(private val tagDao: TagDao) : TagCache {
    override suspend fun read(file: DeviceFile) =
        tagDao.selectTags(file.uri.toString(), file.lastModified)?.intoParsedTags()

    override suspend fun write(file: DeviceFile, tags: ParsedTags) =
        tagDao.updateTags(CachedTags.fromParsedTags(file, tags))
}

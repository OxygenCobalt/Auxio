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
 
package org.oxycblt.auxio.musikr.tag.cache

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.oxycblt.auxio.musikr.tag.AudioFile
import org.oxycblt.auxio.musikr.fs.DeviceFile

sealed interface CacheResult {
    data class Hit(val audioFile: AudioFile) : CacheResult

    data class Miss(val deviceFile: DeviceFile) : CacheResult
}

interface TagCache {
    fun read(files: Flow<DeviceFile>): Flow<CacheResult>

    fun write(rawSongs: Flow<AudioFile>): Flow<AudioFile>
}

class TagCacheImpl @Inject constructor(private val tagDao: TagDao) : TagCache {
    override fun read(files: Flow<DeviceFile>) =
        files.map { file ->
            val tags = tagDao.selectTags(file.uri.toString(), file.lastModified)
            if (tags != null) {
                CacheResult.Hit(tags.toAudioFile(file))
            } else {
                CacheResult.Miss(file)
            }
        }

    override fun write(rawSongs: Flow<AudioFile>) =
        rawSongs.onEach { file -> tagDao.updateTags(Tags.fromAudioFile(file)) }
}

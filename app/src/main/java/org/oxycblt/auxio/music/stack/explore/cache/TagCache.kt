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
 
package org.oxycblt.auxio.music.stack.explore.cache

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import org.oxycblt.auxio.music.stack.explore.AudioFile
import org.oxycblt.auxio.music.stack.explore.extractor.TagResult
import org.oxycblt.auxio.music.stack.explore.DeviceFile

interface TagCache {
    fun read(files: Flow<DeviceFile>): Flow<TagResult>

    fun write(rawSongs: Flow<AudioFile>): Flow<AudioFile>
}

class TagCacheImpl @Inject constructor(private val tagDao: TagDao) : TagCache {
    override fun read(files: Flow<DeviceFile>) =
        files.transform<DeviceFile, TagResult> { file ->
            val tags = tagDao.selectTags(file.uri.toString(), file.lastModified)
            if (tags != null) {
                val audioFile = AudioFile(deviceFile = file)
                tags.copyToRaw(audioFile)
                TagResult.Hit(audioFile)
            } else {
                TagResult.Miss(file)
            }
        }

    override fun write(rawSongs: Flow<AudioFile>) =
        rawSongs.onEach { rawSong -> tagDao.updateTags(Tags.fromRaw(rawSong)) }
}

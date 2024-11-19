package org.oxycblt.auxio.music.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.fs.DeviceFile
import org.oxycblt.auxio.music.metadata.TagResult
import javax.inject.Inject

interface TagCache {
    fun read(files: Flow<DeviceFile>): Flow<TagResult>
    suspend fun write(rawSongs: Flow<RawSong>)
}

class TagCacheImpl @Inject constructor(
    private val tagDao: TagDao
)  : TagCache {
    override fun read(files: Flow<DeviceFile>) =
        files.transform<DeviceFile, TagResult> { file ->
            val tags = tagDao.selectTags(file.uri.toString(), file.lastModified)
            if (tags != null) {
                val rawSong = RawSong(file = file)
                tags.copyToRaw(rawSong)
                TagResult.Hit(rawSong)
            } else {
                TagResult.Miss(file)
            }
        }

    override suspend fun write(rawSongs: Flow<RawSong>) {
        rawSongs.collect { rawSong ->
            tagDao.updateTags(Tags.fromRaw(rawSong))
        }
    }
}
package org.oxycblt.auxio.music.stack.explore

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import org.oxycblt.auxio.music.stack.explore.cache.TagCache
import org.oxycblt.auxio.music.stack.explore.extractor.ExoPlayerTagExtractor
import org.oxycblt.auxio.music.stack.explore.extractor.TagResult
import org.oxycblt.auxio.music.stack.explore.fs.DeviceFiles
import javax.inject.Inject

interface Explorer {
    fun explore(uris: List<Uri>): Flow<AudioFile>
}

class ExplorerImpl @Inject constructor(
    private val deviceFiles: DeviceFiles,
    private val tagCache: TagCache,
    private val tagExtractor: ExoPlayerTagExtractor
) : Explorer {
    override fun explore(uris: List<Uri>): Flow<AudioFile> {
        val deviceFiles = deviceFiles.explore(uris.asFlow()).flowOn(Dispatchers.IO).buffer()
        val tagRead = tagCache.read(deviceFiles).flowOn(Dispatchers.IO).buffer()
        val (cacheFiles, cacheSongs) = tagRead.results()
        val tagExtractor = tagExtractor.process(cacheFiles).flowOn(Dispatchers.IO).buffer()
        val (_, extractorSongs) = tagExtractor.results()
        val writtenExtractorSongs = tagCache.write(extractorSongs).flowOn(Dispatchers.IO).buffer()
        return merge(cacheSongs, writtenExtractorSongs)
    }

    private fun Flow<TagResult>.results(): Pair<Flow<DeviceFile>, Flow<AudioFile>> {
        val files = filterIsInstance<TagResult.Miss>().map { it.file }
        val songs = filterIsInstance<TagResult.Hit>().map { it.audioFile }
        return files to songs
    }
}

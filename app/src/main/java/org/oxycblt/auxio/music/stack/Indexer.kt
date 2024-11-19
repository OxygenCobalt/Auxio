package org.oxycblt.auxio.music.stack

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.stack.cache.TagCache
import org.oxycblt.auxio.music.stack.extractor.ExoPlayerTagExtractor
import org.oxycblt.auxio.music.stack.extractor.TagResult
import org.oxycblt.auxio.music.stack.fs.DeviceFile
import org.oxycblt.auxio.music.stack.fs.DeviceFiles
import org.oxycblt.auxio.music.stack.interpreter.Interpreter
import javax.inject.Inject

interface Indexer {
    suspend fun run(uris: List<Uri>): DeviceLibrary
}

class IndexerImpl @Inject constructor(
    private val deviceFiles: DeviceFiles,
    private val tagCache: TagCache,
    private val tagExtractor: ExoPlayerTagExtractor,
    private val interpreter: Interpreter
) : Indexer {
    override suspend fun run(uris: List<Uri>) = coroutineScope {
        val deviceFiles = deviceFiles.explore(uris.asFlow())
            .flowOn(Dispatchers.IO)
            .buffer()
        val tagRead = tagCache.read(deviceFiles)
            .flowOn(Dispatchers.IO)
            .buffer()
        val (cacheFiles, cacheSongs) = tagRead.split()
        val tagExtractor =
            tagExtractor.process(cacheFiles)
                .flowOn(Dispatchers.IO)
                .buffer()
        val (_, extractorSongs) = tagExtractor.split()
        val sharedExtractorSongs = extractorSongs.shareIn(
            CoroutineScope(Dispatchers.Main),
            started = SharingStarted.WhileSubscribed(),
            replay = Int.MAX_VALUE
        )
        val tagWrite = async { tagCache.write(merge(cacheSongs, sharedExtractorSongs)) }
        val deviceLibrary = async { interpreter.interpret(sharedExtractorSongs) }.await()
        tagWrite.await()
        deviceLibrary
    }

    private fun Flow<TagResult>.split(): Pair<Flow<DeviceFile>, Flow<RawSong>> {
        val files = filterIsInstance<TagResult.Miss>().map { it.file }
        val songs = filterIsInstance<TagResult.Hit>().map { it.rawSong }
        return files to songs
    }
}
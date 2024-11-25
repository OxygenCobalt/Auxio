package org.oxycblt.auxio.music.stack.explore

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.withIndex
import org.oxycblt.auxio.music.stack.explore.cache.TagCache
import org.oxycblt.auxio.music.stack.explore.extractor.TagExtractor
import org.oxycblt.auxio.music.stack.explore.cache.CacheResult
import org.oxycblt.auxio.music.stack.explore.fs.DeviceFiles
import org.oxycblt.auxio.music.stack.explore.playlists.StoredPlaylists
import javax.inject.Inject

interface Explorer {
    fun explore(uris: List<Uri>): Files
}

data class Files(
    val audios: Flow<AudioFile>,
    val playlists: Flow<PlaylistFile>
)

class ExplorerImpl @Inject constructor(
    private val deviceFiles: DeviceFiles,
    private val tagCache: TagCache,
    private val tagExtractor: TagExtractor,
    private val storedPlaylists: StoredPlaylists
) : Explorer {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun explore(uris: List<Uri>): Files {
        val deviceFiles = deviceFiles.explore(uris.asFlow()).flowOn(Dispatchers.IO).buffer()
        val tagRead = tagCache.read(deviceFiles).flowOn(Dispatchers.IO).buffer()
        val (uncachedDeviceFiles, cachedAudioFiles) = tagRead.results()
        val extractedAudioFiles = uncachedDeviceFiles.split(8).map {
            tagExtractor.extract(it).flowOn(Dispatchers.IO).buffer()
        }.asFlow().flattenMerge()
        val writtenAudioFiles = tagCache.write(extractedAudioFiles).flowOn(Dispatchers.IO).buffer()
        val playlistFiles = storedPlaylists.read()
        return Files(merge(cachedAudioFiles, writtenAudioFiles), playlistFiles)
    }

    private fun Flow<CacheResult>.results(): Pair<Flow<DeviceFile>, Flow<AudioFile>> {
        val shared = shareIn(CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(), replay = 0)
        val files = shared.filterIsInstance<CacheResult.Miss>().map { it.deviceFile }
        val songs = shared.filterIsInstance<CacheResult.Hit>().map { it.audioFile }
        return files to songs
    }

    private fun <T> Flow<T>.split(n: Int): Array<Flow<T>> {
        val indexed = withIndex()
        val shared = indexed.shareIn(CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(), replay = 0)
        return Array(n) {
            shared.filter { it.index % n == 0 }
                .map { it.value }
        }
    }
}

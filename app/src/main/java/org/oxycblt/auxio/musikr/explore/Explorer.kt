/*
 * Copyright (c) 2024 Auxio Project
 * Explorer.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.explore

import android.net.Uri
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.withIndex
import org.oxycblt.auxio.musikr.IndexingProgress
import org.oxycblt.auxio.musikr.fs.DeviceFile
import org.oxycblt.auxio.musikr.tag.cache.CacheResult
import org.oxycblt.auxio.musikr.tag.cache.TagCache
import org.oxycblt.auxio.musikr.tag.extractor.TagExtractor
import org.oxycblt.auxio.musikr.fs.DeviceFiles
import org.oxycblt.auxio.musikr.playlist.db.StoredPlaylists
import org.oxycblt.auxio.musikr.playlist.PlaylistFile
import org.oxycblt.auxio.musikr.tag.AudioFile
import timber.log.Timber

interface Explorer {
    fun explore(uris: List<Uri>, onProgress: suspend (IndexingProgress.Songs) -> Unit): Files
}

data class Files(val audios: Flow<AudioFile>, val playlists: Flow<PlaylistFile>)

class ExplorerImpl
@Inject
constructor(
    private val deviceFiles: DeviceFiles,
    private val tagCache: TagCache,
    private val tagExtractor: TagExtractor,
    private val storedPlaylists: StoredPlaylists
) : Explorer {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun explore(
        uris: List<Uri>,
        onProgress: suspend (IndexingProgress.Songs) -> Unit
    ): Files {
        var loaded = 0
        var explored = 0
        val deviceFiles =
            deviceFiles
                .explore(uris.asFlow())
                .onEach {
                    Timber.d("File explored: $it")
                    explored++
                    onProgress(IndexingProgress.Songs(loaded, explored))
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
        val cacheResults =
            tagCache.read(deviceFiles).flowOn(Dispatchers.IO).buffer(Channel.UNLIMITED)
        val audioFiles =
            cacheResults
                .handleMisses { misses ->
                    val extracted =
                        misses
                            .stretch(8) { tagExtractor.extract(it).flowOn(Dispatchers.IO) }
                            .buffer(Channel.UNLIMITED)
                    val written =
                        tagCache.write(extracted).flowOn(Dispatchers.IO).buffer(Channel.UNLIMITED)
                    written
                }
                .onEach {
                    loaded++
                    onProgress(IndexingProgress.Songs(loaded, explored))
                    Timber.d("File extracted: $it")
                }
        val playlistFiles = storedPlaylists.read()
        return Files(audioFiles, playlistFiles)
    }

    /** Temporarily split a flow into 8 parallel threads and then */
    private fun <T, R> Flow<T>.stretch(n: Int, creator: (Flow<T>) -> Flow<R>): Flow<R> {
        val posChannels = Array(n) { Channel<T>(Channel.UNLIMITED) }
        val divert: Flow<R> = flow {
            withIndex().collect {
                val index = it.index % n
                posChannels[index].send(it.value)
            }
            for (channel in posChannels) {
                channel.close()
            }
        }
        val handle =
            posChannels.map { creator(it.receiveAsFlow()).buffer(Channel.UNLIMITED) }.asFlow()
        return merge(divert, handle.flattenMerge())
    }

    private fun Flow<CacheResult>.handleMisses(
        uncached: (Flow<DeviceFile>) -> Flow<AudioFile>
    ): Flow<AudioFile> {
        val uncachedChannel = Channel<DeviceFile>()
        val cachedChannel = Channel<AudioFile>()
        val divert: Flow<AudioFile> = flow {
            collect {
                when (it) {
                    is CacheResult.Hit -> cachedChannel.send(it.audioFile)
                    is CacheResult.Miss -> uncachedChannel.send(it.deviceFile)
                }
            }
            cachedChannel.close()
            uncachedChannel.close()
        }
        val uncached = uncached(uncachedChannel.receiveAsFlow())
        val cached = cachedChannel.receiveAsFlow()
        return merge(divert, uncached, cached)
    }
}

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
 
package org.oxycblt.auxio.music.stack.explore

import android.net.Uri
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.withIndex
import org.oxycblt.auxio.music.stack.explore.cache.CacheResult
import org.oxycblt.auxio.music.stack.explore.cache.TagCache
import org.oxycblt.auxio.music.stack.explore.extractor.TagExtractor
import org.oxycblt.auxio.music.stack.explore.fs.DeviceFiles
import org.oxycblt.auxio.music.stack.explore.playlists.StoredPlaylists

interface Explorer {
    fun explore(uris: List<Uri>, onExplored: suspend () -> Unit): Files
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
    override fun explore(uris: List<Uri>, onExplored: suspend () -> Unit): Files {
        var discovered = 0
        val deviceFiles =
            deviceFiles
                .explore(uris.asFlow())
                .onEach {
                    discovered++
                    onExplored()
                }
                .flowOn(Dispatchers.IO)
                .buffer()
        val tagRead = tagCache.read(deviceFiles).flowOn(Dispatchers.IO).buffer()
        val (uncachedDeviceFiles, cachedAudioFiles) = tagRead.results()
        val extractedAudioFiles =
            uncachedDeviceFiles
                .split(8)
                .map { tagExtractor.extract(it).flowOn(Dispatchers.IO).buffer() }
                .flattenMerge()
        val writtenAudioFiles = tagCache.write(extractedAudioFiles).flowOn(Dispatchers.IO).buffer()
        val audioFiles = merge(cachedAudioFiles, writtenAudioFiles)
        val playlistFiles = storedPlaylists.read()
        return Files(audioFiles, playlistFiles)
    }

    private fun Flow<CacheResult>.results(): Pair<Flow<DeviceFile>, Flow<AudioFile>> {
        val shared =
            shareIn(CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(), replay = 0)
        val files = shared.filterIsInstance<CacheResult.Miss>().map { it.deviceFile }
        val songs = shared.filterIsInstance<CacheResult.Hit>().map { it.audioFile }
        return files to songs
    }

    private fun <T> Flow<T>.split(n: Int): Flow<Flow<T>> {
        val indexed = withIndex()
        val shared =
            indexed.shareIn(
                CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(), replay = 0)
        return Array(n) { shared.filter { it.index % n == 0 }.map { it.value } }.asFlow()
    }
}

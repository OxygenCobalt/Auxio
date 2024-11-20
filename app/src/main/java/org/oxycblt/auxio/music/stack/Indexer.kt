/*
 * Copyright (c) 2024 Auxio Project
 * Indexer.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.stack

import android.net.Uri
import javax.inject.Inject
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
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.metadata.Separators
import org.oxycblt.auxio.music.stack.cache.TagCache
import org.oxycblt.auxio.music.stack.extractor.ExoPlayerTagExtractor
import org.oxycblt.auxio.music.stack.extractor.TagResult
import org.oxycblt.auxio.music.stack.fs.DeviceFile
import org.oxycblt.auxio.music.stack.fs.DeviceFiles
import org.oxycblt.auxio.music.user.MutableUserLibrary
import org.oxycblt.auxio.music.user.UserLibrary

interface Indexer {
    suspend fun run(
        uris: List<Uri>,
        separators: Separators,
        nameFactory: Name.Known.Factory
    ): LibraryResult
}

data class LibraryResult(val deviceLibrary: DeviceLibrary, val userLibrary: MutableUserLibrary)

class IndexerImpl
@Inject
constructor(
    private val deviceFiles: DeviceFiles,
    private val tagCache: TagCache,
    private val tagExtractor: ExoPlayerTagExtractor,
    private val deviceLibraryFactory: DeviceLibrary.Factory,
    private val userLibraryFactory: UserLibrary.Factory
) : Indexer {
    override suspend fun run(
        uris: List<Uri>,
        separators: Separators,
        nameFactory: Name.Known.Factory
    ) = coroutineScope {
        val deviceFiles = deviceFiles.explore(uris.asFlow()).flowOn(Dispatchers.IO).buffer()
        val tagRead = tagCache.read(deviceFiles).flowOn(Dispatchers.IO).buffer()
        val (cacheFiles, cacheSongs) = tagRead.split()
        val tagExtractor = tagExtractor.process(cacheFiles).flowOn(Dispatchers.IO).buffer()
        val (_, extractorSongs) = tagExtractor.split()
        val sharedExtractorSongs =
            extractorSongs.shareIn(
                CoroutineScope(Dispatchers.Main),
                started = SharingStarted.WhileSubscribed(),
                replay = Int.MAX_VALUE)
        val tagWrite =
            async(Dispatchers.IO) { tagCache.write(merge(cacheSongs, sharedExtractorSongs)) }
        val rawPlaylists = async(Dispatchers.IO) { userLibraryFactory.query() }
        val deviceLibrary =
            deviceLibraryFactory.create(
                merge(cacheSongs, sharedExtractorSongs), {}, separators, nameFactory)
        val userLibrary =
            userLibraryFactory.create(rawPlaylists.await(), deviceLibrary, nameFactory)
        tagWrite.await()
        LibraryResult(deviceLibrary, userLibrary)
    }

    private fun Flow<TagResult>.split(): Pair<Flow<DeviceFile>, Flow<RawSong>> {
        val files = filterIsInstance<TagResult.Miss>().map { it.file }
        val songs = filterIsInstance<TagResult.Hit>().map { it.rawSong }
        return files to songs
    }
}

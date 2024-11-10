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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.metadata.Separators
import org.oxycblt.auxio.music.model.Interpretation
import org.oxycblt.auxio.music.model.Interpreter
import org.oxycblt.auxio.music.model.MutableLibrary
import org.oxycblt.auxio.music.stack.cache.TagCache
import org.oxycblt.auxio.music.stack.extractor.ExoPlayerTagExtractor
import org.oxycblt.auxio.music.stack.extractor.TagResult
import org.oxycblt.auxio.music.stack.fs.DeviceFiles

interface Indexer {
    suspend fun run(
        uris: List<Uri>,
        interpretation: Interpretation
    ): MutableLibrary
}


class IndexerImpl
@Inject
constructor(
    private val deviceFiles: DeviceFiles,
    private val tagCache: TagCache,
    private val tagExtractor: ExoPlayerTagExtractor,
    private val interpreter: Interpreter
) : Indexer {
    override suspend fun run(
        uris: List<Uri>,
        interpretation: Interpretation
    ) = coroutineScope {
        val deviceFiles = deviceFiles.explore(uris.asFlow()).flowOn(Dispatchers.IO).buffer()
        val tagRead = tagCache.read(deviceFiles).flowOn(Dispatchers.IO).buffer()
        val (cacheFiles, cacheSongs) = tagRead.results()
        val tagExtractor = tagExtractor.process(cacheFiles).flowOn(Dispatchers.IO).buffer()
        val (_, extractorSongs) = tagExtractor.results()
        val sharedExtractorSongs =
            extractorSongs.shareIn(
                CoroutineScope(Dispatchers.Main),
                started = SharingStarted.WhileSubscribed(),
                replay = Int.MAX_VALUE)
        val tagWrite =
            async(Dispatchers.IO) { tagCache.write(sharedExtractorSongs) }
        val library = async(Dispatchers.Main) { interpreter.interpret(
            merge(cacheSongs, sharedExtractorSongs), emptyFlow(), interpretation
        )}
        tagWrite.await()
        library.await()
    }

    private fun Flow<TagResult>.results(): Pair<Flow<DeviceFile>, Flow<AudioFile>> {
        val files = filterIsInstance<TagResult.Miss>().map { it.file }
        val songs = filterIsInstance<TagResult.Hit>().map { it.audioFile }
        return files to songs
    }
}

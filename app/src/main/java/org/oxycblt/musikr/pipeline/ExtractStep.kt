/*
 * Copyright (c) 2024 Auxio Project
 * ExtractStep.kt is part of Auxio.
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
 
package org.oxycblt.musikr.pipeline

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.oxycblt.ktaglib.KTagLib
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.CachedSong
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverParser
import org.oxycblt.musikr.fs.query.DeviceFile
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.parse.TagParser
import timber.log.Timber

interface ExtractStep {
    fun extract(storage: Storage, nodes: Flow<ExploreNode>): Flow<ExtractedMusic>
}

class ExtractStepImpl
@Inject
constructor(
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val coverParser: CoverParser
) : ExtractStep {
    override fun extract(storage: Storage, nodes: Flow<ExploreNode>): Flow<ExtractedMusic> {
        val cacheResults =
            nodes
                .filterIsInstance<ExploreNode.Audio>()
                .map {
                    val tags = storage.cache.read(it.file)
                    MaybeCachedSong(it.file, tags)
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
        val (cachedSongs, uncachedSongs) =
            cacheResults.mapPartition {
                it.cachedSong?.let { song ->
                    ExtractedMusic.Song(it.file, song.parsedTags, song.cover)
                }
            }
        val split = uncachedSongs.distribute(8)
        val extractedSongs =
            Array(split.hot.size) { i ->
                split.hot[i]
                    .map { node ->
                        val metadata = metadataExtractor.extract(node.file)
                        val tags = tagParser.parse(node.file, metadata)
                        val coverData = coverParser.extract(metadata)
                        val cover = coverData?.let { storage.storedCovers.write(it) }
                        ExtractedMusic.Song(node.file, tags, cover)
                    }
                    .flowOn(Dispatchers.IO)
                    .buffer(Channel.UNLIMITED)
            }
        val writtenSongs =
            merge(*extractedSongs)
                .map {
                    storage.cache.write(it.file, CachedSong(it.tags, it.cover))
                    it
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
        return merge<ExtractedMusic>(
            cachedSongs,
            split.cold,
            writtenSongs,
        )
    }

    data class MaybeCachedSong(val file: DeviceFile, val cachedSong: CachedSong?)
}

sealed interface ExtractedMusic {
    data class Song(val file: DeviceFile, val tags: ParsedTags, val cover: Cover.Single?) :
        ExtractedMusic
}

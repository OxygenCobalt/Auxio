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

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.parse.TagParser

internal interface ExtractStep {
    fun extract(storage: Storage, nodes: Flow<ExploreNode>): Flow<ExtractedMusic>

    companion object {
        fun from(context: Context): ExtractStep =
            ExtractStepImpl(MetadataExtractor.from(context), TagParser.new())
    }
}

private class ExtractStepImpl(
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser
) : ExtractStep {
    override fun extract(storage: Storage, nodes: Flow<ExploreNode>): Flow<ExtractedMusic> {
        val cacheResults =
            nodes
                .filterIsInstance<ExploreNode.Audio>()
                .map { storage.cache.read(it.file) }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
        val divertedFlow =
            cacheResults.divert {
                when (it) {
                    is CacheResult.Hit -> Divert.Left(it.song)
                    is CacheResult.Miss -> Divert.Right(it.file)
                }
            }
        val cachedSongs = divertedFlow.left.map { ExtractedMusic.Song(it) }
        val uncachedSongs = divertedFlow.right
        val distributedFlow = uncachedSongs.distribute(16)
        val extractedSongs =
            Array(distributedFlow.flows.size) { i ->
                distributedFlow.flows[i]
                    .mapNotNull { file ->
                        val metadata = metadataExtractor.extract(file) ?: return@mapNotNull null
                        val tags = tagParser.parse(file, metadata)
                        val cover = metadata.cover?.let { storage.storedCovers.write(it) }
                        RawSong(file, metadata.properties, tags, cover)
                    }
                    .flowOn(Dispatchers.IO)
                    .buffer(Channel.UNLIMITED)
            }
        val writtenSongs =
            merge(*extractedSongs)
                .map {
                    storage.cache.write(it)
                    ExtractedMusic.Song(it)
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
        return merge<ExtractedMusic>(
            divertedFlow.manager, cachedSongs, distributedFlow.manager, writtenSongs)
    }
}

data class RawSong(
    val file: DeviceFile,
    val properties: Properties,
    val tags: ParsedTags,
    val cover: Cover.Single?
)

internal sealed interface ExtractedMusic {
    data class Song(val song: RawSong) : ExtractedMusic
}

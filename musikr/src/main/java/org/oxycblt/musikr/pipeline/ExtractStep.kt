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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.StoredCovers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.parse.TagParser

internal interface ExtractStep {
    fun extract(nodes: Flow<ExploreNode>): Flow<ExtractedMusic>

    companion object {
        fun from(context: Context, storage: Storage): ExtractStep =
            ExtractStepImpl(
                MetadataExtractor.from(context),
                TagParser.new(),
                storage.cache,
                storage.storedCovers
            )
    }
}

private class ExtractStepImpl(
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cache: Cache,
    private val storedCovers: StoredCovers
) : ExtractStep {
    override fun extract(nodes: Flow<ExploreNode>): Flow<ExtractedMusic> {
        val filterFlow =
            nodes.divert {
                when (it) {
                    is ExploreNode.Audio -> Divert.Right(it.file)
                    is ExploreNode.Playlist -> Divert.Left(it.file)
                }
            }
        val audioNodes = filterFlow.right
        val playlistNodes = filterFlow.left.map { ExtractedMusic.Playlist(it) }

        val cacheResults =
            audioNodes.map { wrap(it, cache::read) }.flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
        val cacheFlow =
            cacheResults.divert {
                when (it) {
                    is CacheResult.Hit -> Divert.Left(it.song)
                    is CacheResult.Miss -> Divert.Right(it.file)
                }
            }
        val cachedSongs = cacheFlow.left.map { ExtractedMusic.Song(it) }
        val uncachedSongs = cacheFlow.right
        val distributedFlow = uncachedSongs.distribute(16)
        val extractedSongs =
            Array(distributedFlow.flows.size) { i ->
                distributedFlow.flows[i]
                    .mapNotNull { it ->
                        wrap(it) { file ->
                            val metadata = metadataExtractor.extract(file) ?: return@wrap null
                            val tags = tagParser.parse(file, metadata)
                            val cover = metadata.cover?.let { storedCovers.write(it) }
                            RawSong(file, metadata.properties, tags, cover)
                        }
                    }
                    .flowOn(Dispatchers.IO)
                    .buffer(Channel.UNLIMITED)
            }
        val writtenSongs =
            merge(*extractedSongs)
                .map {
                    wrap(it, cache::write)
                    ExtractedMusic.Song(it)
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
        return merge(
            filterFlow.manager,
            cacheFlow.manager,
            cachedSongs,
            distributedFlow.manager,
            writtenSongs,
            playlistNodes
        )
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

    data class Playlist(val file: PlaylistFile) : ExtractedMusic
}

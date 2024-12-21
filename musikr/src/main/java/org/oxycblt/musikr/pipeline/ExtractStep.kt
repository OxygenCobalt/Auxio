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
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.MutableStoredCovers
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
                context,
                MetadataExtractor.from(context),
                TagParser.new(),
                storage.cache,
                storage.storedCovers)
    }
}

private class ExtractStepImpl(
    private val context: Context,
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cache: Cache,
    private val storedCovers: MutableStoredCovers
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

        val distributedAudioNodes = audioNodes.distribute(8)
        val cacheResults =
            distributedAudioNodes.flows
                .map { flow ->
                    flow
                        .map { wrap(it) { file -> cache.read(file, storedCovers) } }
                        .flowOn(Dispatchers.IO)
                        .buffer(Channel.UNLIMITED)
                }
                .flattenMerge()
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

        val fds =
            uncachedSongs
                .mapNotNull {
                    wrap(it) { file ->
                        withContext(Dispatchers.IO) {
                            context.contentResolver.openFileDescriptor(file.uri, "r")?.let { fd ->
                                FileWith(file, fd)
                            }
                        }
                    }
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)

        val metadata =
            fds.mapNotNull { fileWith ->
                    wrap(fileWith.file) { _ ->
                        metadataExtractor
                            .extract(fileWith.with)
                            ?.let { FileWith(fileWith.file, it) }
                            .also { withContext(Dispatchers.IO) { fileWith.with.close() } }
                    }
                }
                .flowOn(Dispatchers.IO)
                // Covers are pretty big, so cap the amount of parsed metadata in-memory to at most
                // 8 to minimize GCs.
                .buffer(8)

        val extractedSongs =
            metadata
                .mapNotNull { fileWith ->
                    val tags = tagParser.parse(fileWith.file, fileWith.with)
                    val cover = fileWith.with.cover?.let { storedCovers.write(it) }
                    RawSong(fileWith.file, fileWith.with.properties, tags, cover)
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)

        val writtenSongs =
            extractedSongs
                .map {
                    wrap(it, cache::write)
                    ExtractedMusic.Song(it)
                }
                .flowOn(Dispatchers.IO)

        return merge(
            filterFlow.manager,
            distributedAudioNodes.manager,
            cacheFlow.manager,
            cachedSongs,
            writtenSongs,
            playlistNodes)
    }

    private data class FileWith<T>(val file: DeviceFile, val with: T)
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

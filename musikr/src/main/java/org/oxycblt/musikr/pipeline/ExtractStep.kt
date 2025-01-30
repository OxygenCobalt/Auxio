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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.MutableCovers
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
                MetadataExtractor.new(),
                TagParser.new(),
                storage.cache,
                storage.storedCovers)
    }
}

private class ExtractStepImpl(
    private val context: Context,
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cacheFactory: Cache.Factory,
    private val storedCovers: MutableCovers
) : ExtractStep {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun extract(nodes: Flow<ExploreNode>): Flow<ExtractedMusic> {
        val cache = cacheFactory.open()
        val addingMs = System.currentTimeMillis()
        val filterFlow =
            nodes.divert {
                when (it) {
                    is ExploreNode.Audio -> Divert.Right(it.file)
                    is ExploreNode.Playlist -> Divert.Left(it.file)
                }
            }
        val audioNodes = filterFlow.right
        val playlistNodes = filterFlow.left.map { ExtractedMusic.Valid.Playlist(it) }

        val readDistributedFlow = audioNodes.distribute(8)
        val cacheResults =
            readDistributedFlow.flows
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
        val cachedSongs = cacheFlow.left.map { ExtractedMusic.Valid.Song(it) }
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
                            .extract(fileWith.file, fileWith.with)
                            .let { FileWith(fileWith.file, it) }
                            .also { withContext(Dispatchers.IO) { fileWith.with.close() } }
                    }
                }
                .flowOn(Dispatchers.IO)
                // Covers are pretty big, so cap the amount of parsed metadata in-memory to at most
                // 8 to minimize GCs.
                .buffer(8)

        val extractedSongs =
            metadata
                .map { fileWith ->
                    if (fileWith.with != null) {
                        val tags = tagParser.parse(fileWith.file, fileWith.with)
                        val cover = fileWith.with.cover?.let { storedCovers.write(it) }
                        RawSong(fileWith.file, fileWith.with.properties, tags, cover, addingMs)
                    } else {
                        null
                    }
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)

        val extractedFilter =
            extractedSongs.divert {
                if (it != null) Divert.Left(it) else Divert.Right(ExtractedMusic.Invalid)
            }

        val write = extractedFilter.left
        val invalid = extractedFilter.right

        val writeDistributedFlow = write.distribute(8)
        val writtenSongs =
            writeDistributedFlow.flows
                .map { flow ->
                    flow
                        .map {
                            wrap(it, cache::write)
                            ExtractedMusic.Valid.Song(it)
                        }
                        .flowOn(Dispatchers.IO)
                        .buffer(Channel.UNLIMITED)
                }
                .flattenMerge()

        val merged =
            merge(
                filterFlow.manager,
                readDistributedFlow.manager,
                cacheFlow.manager,
                cachedSongs,
                extractedFilter.manager,
                writeDistributedFlow.manager,
                writtenSongs,
                invalid,
                playlistNodes)

        return merged.onCompletion { cache.finalize() }
    }

    private data class FileWith<T>(val file: DeviceFile, val with: T)
}

internal data class RawSong(
    val file: DeviceFile,
    val properties: Properties,
    val tags: ParsedTags,
    val cover: Cover?,
    val addedMs: Long
)

internal sealed interface ExtractedMusic {
    sealed interface Valid : ExtractedMusic {
        data class Song(val song: RawSong) : Valid

        data class Playlist(val file: PlaylistFile) : Valid
    }

    data object Invalid : ExtractedMusic
}

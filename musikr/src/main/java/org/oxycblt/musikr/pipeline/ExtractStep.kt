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

        // Distribute audio nodes for parallel processing
        val processingDistributedFlow = audioNodes.distribute(8)
        
        // Process each audio file in parallel flows
        val processedSongs =
            processingDistributedFlow.flows
                .map { flow ->
                    flow
                        .mapNotNull { file ->
                            // First try to read from cache
                            wrap(file) { f -> 
                                when (val result = cache.read(f, storedCovers)) {
                                    is CacheResult.Hit -> ExtractedMusic.Valid.Song(result.song)
                                    is CacheResult.Miss -> {
                                        // If not in cache, process the file
                                        val fd = withContext(Dispatchers.IO) {
                                            context.contentResolver.openFileDescriptor(f.uri, "r")
                                        } ?: return@wrap null
                                        
                                        try {
                                            // Extract metadata
                                            val extractedMetadata = metadataExtractor.extract(f, fd)
                                            
                                            if (extractedMetadata != null) {
                                                // Parse tags
                                                val tags = tagParser.parse(extractedMetadata)
                                                
                                                // Store cover if present
                                                val cover = extractedMetadata.cover?.let { 
                                                    storedCovers.write(it) 
                                                }
                                                
                                                // Create and write the raw song to cache
                                                val rawSong = RawSong(f, extractedMetadata.properties, tags, cover, addingMs)
                                                wrap(rawSong, cache::write)
                                                
                                                ExtractedMusic.Valid.Song(rawSong)
                                            } else {
                                                ExtractedMusic.Invalid
                                            }
                                        } finally {
                                            withContext(Dispatchers.IO) { fd.close() }
                                        }
                                    }
                                }
                            }
                        }
                        .flowOn(Dispatchers.IO)
                        .buffer(Channel.UNLIMITED)
                }
                .flattenMerge()
                .buffer(Channel.UNLIMITED)
        
        // Separate valid songs from invalid ones
        val processedFlow = processedSongs.divert {
            when (it) {
                is ExtractedMusic.Valid.Song -> Divert.Left(it)
                is ExtractedMusic.Invalid -> Divert.Right(it)
                else -> Divert.Right(ExtractedMusic.Invalid) // Should never happen
            }
        }
        
        val validSongs = processedFlow.left
        val invalidSongs = processedFlow.right

        val merged =
            merge(
                filterFlow.manager,
                processingDistributedFlow.manager,
                processedFlow.manager,
                validSongs,
                invalidSongs,
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

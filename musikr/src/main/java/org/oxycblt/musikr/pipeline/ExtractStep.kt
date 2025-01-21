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
import kotlinx.coroutines.flow.merge
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.metadata.MetadataHandle
import org.oxycblt.musikr.tag.parse.TagParser

internal interface ExtractStep {
    fun extract(nodes: Flow<Explored.New>): Flow<Extracted>

    companion object {
        fun from(context: Context, storage: Storage): ExtractStep =
            ExtractStepImpl(
                MetadataExtractor.new(context), TagParser.new(), storage.cache, storage.covers)
    }
}

private class ExtractStepImpl(
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cache: Cache,
    private val storedCovers: MutableCovers
) : ExtractStep {
    override fun extract(nodes: Flow<Explored.New>): Flow<Extracted> {
        val newSongs = nodes.filterIsInstance<NewSong>()

        val handles: Flow<ExtractedInternal.Pre> =
            newSongs
                .tryMap {
                    val handle = metadataExtractor.open(it.file)
                    if (handle != null) NewSongHandle(it, handle) else ExtractFailed
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)

        val extracted: Flow<ExtractedInternal.Post> =
            handles
                .tryMap { item ->
                    when (item) {
                        is NewSongHandle -> {
                            val metadata = item.handle.extract()
                            if (metadata != null) NewSongMetadata(item.song, metadata)
                            else ExtractFailed
                        }
                        is ExtractFailed -> ExtractFailed
                    }
                }
                .flowOn(Dispatchers.IO)
                // Covers are pretty big, so cap the amount of parsed metadata in-memory to at most
                // 8 to minimize GCs.
                .buffer(8)

        val validDiversion =
            extracted.divert {
                when (it) {
                    is NewSongMetadata -> Divert.Right(it)
                    is ExtractFailed -> Divert.Left(it)
                }
            }

        val validSongs = validDiversion.right
        val invalidSongs = validDiversion.left

        val parsed =
            validSongs
                .tryMap { item ->
                    val tags = tagParser.parse(item.song.file, item.metadata)
                    val cover = item.metadata.cover?.let { storedCovers.write(it) }
                    RawSong(
                        item.song.file, item.metadata.properties, tags, cover, item.song.addedMs)
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)

        val writeDistribution = parsed.distribute(8)
        val writtenSongs =
            writeDistribution.flows.mapx { flow ->
                flow
                    .tryMap {
                        cache.write(it)
                        it
                    }
                    .flowOn(Dispatchers.IO)
                    .buffer(Channel.UNLIMITED)
            }

        val invalid = invalidSongs.map { InvalidSong }

        return merge(validDiversion.manager, writeDistribution.manager, *writtenSongs, invalid)
    }

    private sealed interface ExtractedInternal {
        sealed interface Pre : ExtractedInternal

        sealed interface Post : ExtractedInternal
    }

    private data class NewSongHandle(val song: NewSong, val handle: MetadataHandle) :
        ExtractedInternal.Pre

    private data class NewSongMetadata(val song: NewSong, val metadata: Metadata) :
        ExtractedInternal.Post

    private data object ExtractFailed : ExtractedInternal.Pre, ExtractedInternal.Post
}

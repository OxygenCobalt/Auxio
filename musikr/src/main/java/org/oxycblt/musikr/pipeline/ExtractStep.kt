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
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.CachedSong
import org.oxycblt.musikr.cache.MutableSongCache
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.log.Logger
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.metadata.MetadataHandle
import org.oxycblt.musikr.tag.parse.TagParser

internal interface ExtractStep {
    suspend fun extract(nodes: Werk<Explored.New>): Werk<Extracted>

    companion object {
        fun from(context: Context, storage: Storage, logger: Logger): ExtractStep =
            ExtractStepImpl(
                MetadataExtractor.new(context),
                TagParser.new(),
                storage.cache,
                storage.covers,
                logger.primary("exct"),
            )
    }
}

private class ExtractStepImpl(
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cache: MutableSongCache,
    private val storedCovers: MutableCovers,
    private val logger: Logger
) : ExtractStep {
    override suspend fun extract(nodes: Werk<Explored.New>): Werk<Extracted> {
        logger.d("extract start.")
        return nodes
            .filterIsInstance<NewSong>()
            .then {
                val handle = metadataExtractor.open(it.file)
                if (handle != null) NewSongHandle(it, handle) else ExtractFailed
            }
            .then(8) { item ->
                when (item) {
                    is NewSongHandle -> {
                        val metadata = item.handle.extract()
                        if (metadata != null) NewSongMetadata(item.song, metadata)
                        else ExtractFailed
                    }
                    is ExtractFailed -> ExtractFailed
                }
            }
            .then { item ->
                when (item) {
                    is NewSongMetadata -> {
                        val tags = tagParser.parse(item.song.file, item.metadata)
                        val cover = item.metadata.cover?.let { storedCovers.write(it) }
                        RawSong(
                            item.song.file,
                            item.metadata.properties,
                            tags,
                            cover,
                            item.song.addedMs)
                    }
                    is ExtractFailed -> InvalidSong
                }
            }
            .distribute(8)
            .transform { item ->
                when (item) {
                    is RawSong -> {
                        cache.write(
                            CachedSong(
                                item.file,
                                item.properties,
                                item.tags,
                                item.cover?.id,
                                item.addedMs))
                        item
                    }
                    else -> item
                }
            }
            .merge()
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

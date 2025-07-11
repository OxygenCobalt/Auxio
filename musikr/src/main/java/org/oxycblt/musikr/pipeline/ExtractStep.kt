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
import kotlinx.coroutines.flow.onCompletion
import org.oxycblt.musikr.Config
import org.oxycblt.musikr.cache.CachedSong
import org.oxycblt.musikr.cache.MutableCache
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.tag.parse.TagParser

internal interface ExtractStep {
    fun extract(nodes: Flow<Explored>): Flow<Extracted>

    companion object {
        fun from(context: Context, config: Config): ExtractStep =
            ExtractStepImpl(
                MetadataExtractor.from(context),
                TagParser.new(),
                config.storage.cache,
                config.storage.covers)
    }
}

private class ExtractStepImpl(
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cache: MutableCache,
    private val covers: MutableCovers<out Cover>
) : ExtractStep {
    override fun extract(nodes: Flow<Explored>): Flow<Extracted> {
        val addingMs = System.currentTimeMillis()
        val exclude = mutableListOf<CachedSong>()
        return nodes
            // Cover art is huge, so we have to kneecap the concurrency here to avoid excessive
            // GCs. We still reap the concurrency benefits here, just not as much as we could.
            .distributedMap(on = Dispatchers.IO, n = 8, buffer = Channel.RENDEZVOUS) {
                when (it) {
                    is RawSong -> Finalized(it)
                    is RawPlaylist -> Finalized(it)
                    is NewSong -> {
                        val metadata = metadataExtractor.extract(it.file)
                        if (metadata != null) NeedsParsing(it, metadata) else Finalized(InvalidSong)
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .buffer(Channel.RENDEZVOUS)
            .distributedMap(on = Dispatchers.IO, n = 8, buffer = Channel.UNLIMITED) {
                when (it) {
                    is Finalized -> it
                    is NeedsParsing -> {
                        val tags = tagParser.parse(it.metadata)
                        val cover =
                            when (val result = covers.create(it.newSong.file, it.metadata)) {
                                is CoverResult.Hit -> result.cover
                                else -> null
                            }
                        NeedsCaching(
                            RawSong(
                                it.newSong.file,
                                it.metadata.properties,
                                tags,
                                cover,
                                // The thing about date added is that it's resolution can actually
                                // be expensive in some modes (ex. saf backend), so we resolve this
                                // by
                                // moving date added extraction as an extraction operation rather
                                // than doing the redundant work during exploration (well, kind of,
                                // MediaStore's date added query is basically free, it's only saf
                                // that has it's slow hacky workaround that we must accommodate
                                // here.)
                                it.newSong.file.addedMs.resolve() ?: addingMs))
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .buffer(Channel.UNLIMITED)
            .distributedMap(on = Dispatchers.IO, n = 8, buffer = Channel.UNLIMITED) {
                when (it) {
                    is Finalized -> it
                    is NeedsCaching -> {
                        cache.write(it.rawSong.toCachedSong())
                        Finalized(it.rawSong)
                    }
                }
            }
            .map {
                if (it.extracted is RawSong) {
                    exclude.add(it.extracted.toCachedSong())
                }
                it.extracted
            }
            .flowOn(Dispatchers.IO)
            .buffer(Channel.UNLIMITED)
            .onCompletion { cache.cleanup(exclude) }
    }

    private sealed interface ParsedExtractItem

    private data class NeedsParsing(val newSong: NewSong, val metadata: Metadata) :
        ParsedExtractItem

    private sealed interface ParsedCachingItem

    private data class NeedsCaching(val rawSong: RawSong) : ParsedCachingItem

    private data class Finalized(val extracted: Extracted) : ParsedExtractItem, ParsedCachingItem

    private fun RawSong.toCachedSong() = CachedSong(file, properties, tags, cover?.id, addedMs)
}

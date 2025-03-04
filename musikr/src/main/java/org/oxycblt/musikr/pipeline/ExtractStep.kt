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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.onCompletion
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.CachedSong
import org.oxycblt.musikr.cache.MutableCache
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverResult
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.tag.parse.TagParser

internal interface ExtractStep {
    fun extract(nodes: Flow<Explored>): Flow<Extracted>

    companion object {
        fun from(context: Context, storage: Storage): ExtractStep =
            ExtractStepImpl(
                MetadataExtractor.from(context), TagParser.new(), storage.cache, storage.covers)
    }
}

private class ExtractStepImpl(
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cache: MutableCache,
    private val covers: MutableCovers<out Cover>
) : ExtractStep {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun extract(nodes: Flow<Explored>): Flow<Extracted> {
        val exclude = mutableListOf<CachedSong>()
        return nodes
            .distribute(8)
            .distributedMap {
                when (it) {
                    is RawSong -> it
                    is RawPlaylist -> it
                    is NewSong -> {
                        val metadata =
                            metadataExtractor.extract(it.file) ?: return@distributedMap InvalidSong
                        val tags = tagParser.parse(metadata)
                        val cover =
                            when (val result = covers.create(it.file, metadata)) {
                                is CoverResult.Hit -> result.cover
                                else -> null
                            }
                        val cachedSong =
                            CachedSong(it.file, metadata.properties, tags, cover?.id, it.addedMs)
                        cache.write(cachedSong)
                        exclude.add(cachedSong)
                        val rawSong = RawSong(it.file, metadata.properties, tags, cover, it.addedMs)
                        rawSong
                    }
                }
            }
            .flattenMerge()
            .onCompletion { cache.cleanup(exclude) }
    }
}

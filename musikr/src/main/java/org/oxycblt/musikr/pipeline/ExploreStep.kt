/*
 * Copyright (c) 2024 Auxio Project
 * ExploreStep.kt is part of Auxio.
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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cache.CachedSong
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.device.DeviceFS
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.m3u.M3U

internal interface ExploreStep {
    fun explore(locations: List<MusicLocation>): Flow<Explored>

    companion object {
        fun from(context: Context, storage: Storage, interpretation: Interpretation): ExploreStep =
            ExploreStepImpl(
                DeviceFS.from(context, interpretation.withHidden),
                storage.cache,
                storage.covers,
                storage.storedPlaylists)
    }
}

private class ExploreStepImpl(
    private val deviceFS: DeviceFS,
    private val cache: Cache,
    private val covers: Covers<out Cover>,
    private val storedPlaylists: StoredPlaylists
) : ExploreStep {
    override fun explore(locations: List<MusicLocation>): Flow<Explored> {
        val addingMs = System.currentTimeMillis()
        return merge(
            deviceFS
                .explore(
                    locations.asFlow(),
                )
                .filter { it.mimeType.startsWith("audio/") || it.mimeType == M3U.MIME_TYPE }
                .distributedMap(n = 8, on = Dispatchers.IO, buffer = Channel.UNLIMITED) { file ->
                    when (val cacheResult = cache.read(file)) {
                        is CacheResult.Hit -> NeedsCover(cacheResult.song)
                        is CacheResult.Stale ->
                            Finalized(NewSong(cacheResult.file, cacheResult.addedMs))
                        is CacheResult.Miss -> Finalized(NewSong(cacheResult.file, addingMs))
                    }
                }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED)
                .distributedMap(n = 8, on = Dispatchers.IO, buffer = Channel.UNLIMITED) {
                    when (it) {
                        is Finalized -> it
                        is NeedsCover -> {
                            when (val coverResult = it.song.coverId?.let { covers.obtain(it) }) {
                                is CoverResult.Hit ->
                                    Finalized(
                                        RawSong(
                                            it.song.file,
                                            it.song.properties,
                                            it.song.tags,
                                            coverResult.cover,
                                            it.song.addedMs))
                                null ->
                                    Finalized(
                                        RawSong(
                                            it.song.file,
                                            it.song.properties,
                                            it.song.tags,
                                            null,
                                            it.song.addedMs))
                                else -> Finalized(NewSong(it.song.file, it.song.addedMs))
                            }
                        }
                    }
                }
                .map { it.explored }
                .flowOn(Dispatchers.IO)
                .buffer(Channel.UNLIMITED),
            flow { emitAll(storedPlaylists.read().asFlow()) }
                .map { RawPlaylist(it) }
                .flowOn(Dispatchers.IO)
                .buffer())
    }

    private sealed interface InternalExploreItem

    private data class NeedsCover(val song: CachedSong) : InternalExploreItem

    private data class Finalized(val explored: Explored) : InternalExploreItem
}

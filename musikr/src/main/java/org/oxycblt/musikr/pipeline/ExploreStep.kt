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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.ObtainResult
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.device.DeviceFiles
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.m3u.M3U

internal interface ExploreStep {
    fun explore(locations: List<MusicLocation>): Flow<Explored>

    companion object {
        fun from(context: Context, storage: Storage): ExploreStep =
            ExploreStepImpl(
                DeviceFiles.from(context), storage.storedPlaylists, storage.cache, storage.covers)
    }
}

private class ExploreStepImpl(
    private val deviceFiles: DeviceFiles,
    private val storedPlaylists: StoredPlaylists,
    private val cache: Cache,
    private val covers: Covers
) : ExploreStep {
    override fun explore(locations: List<MusicLocation>): Flow<Explored> {
        val audios =
            deviceFiles
                .explore(locations.asFlow())
                .filter { it.mimeType.startsWith("audio/") || it.mimeType == M3U.MIME_TYPE }
                .map { evaluateAudio(it) }
                .flowOn(Dispatchers.IO)
                .buffer()
        val playlists =
            flow { emitAll(storedPlaylists.read().asFlow()) }
                .map { RawPlaylist(it) }
                .flowOn(Dispatchers.IO)
                .buffer()
        return merge(audios, playlists)
    }

    private suspend fun evaluateAudio(file: DeviceFile): Explored {
        return when (val cacheResult = cache.read(file)) {
            is CacheResult.Hit -> {
                val coverResult = cacheResult.coverId?.let { covers.obtain(it) }
                when (coverResult) {
                    is ObtainResult.Hit ->
                        RawSong(
                            file,
                            cacheResult.properties,
                            cacheResult.tags,
                            coverResult.cover,
                            cacheResult.addedMs)
                    else -> NewSong(file, cacheResult.addedMs)
                }
            }
            is CacheResult.Outdated -> NewSong(file, cacheResult.addedMs)
            is CacheResult.Miss -> NewSong(file, System.currentTimeMillis())
        }
    }
}

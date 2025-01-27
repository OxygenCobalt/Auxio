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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cache.SongCache
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.ObtainResult
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.device.DeviceFS
import org.oxycblt.musikr.log.Logger
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.m3u.M3U

internal interface ExploreStep {
    suspend fun explore(locations: List<MusicLocation>): Werk<Explored>

    companion object {
        fun from(context: Context, storage: Storage, logger: Logger): ExploreStep =
            ExploreStepImpl(
                DeviceFS.from(context),
                storage.storedPlaylists,
                storage.cache,
                storage.covers,
                logger.primary("expl"),
            )
    }
}

private class ExploreStepImpl(
    private val deviceFS: DeviceFS,
    private val storedPlaylists: StoredPlaylists,
    private val songCache: SongCache,
    private val covers: Covers,
    private val logger: Logger,
) : ExploreStep {
    override suspend fun explore(locations: List<MusicLocation>): Werk<Explored> {
        logger.d("explore start.")
        val songs: Werk<Explored> =
            deviceFS
                .explore(locations.asFlow())
                .filter { it.mimeType.startsWith("audio/") || it.mimeType == M3U.MIME_TYPE }
                .werk()
                .distribute(8)
                .transform { file ->
                    when (val cacheResult = songCache.read(file)) {
                        is CacheResult.Hit -> {
                            val cachedSong = cacheResult.song
                            val coverResult = cachedSong.coverId?.let { covers.obtain(it) }
                            if (coverResult !is ObtainResult.Hit) {
                                return@transform NewSong(file, cachedSong.addedMs)
                            }
                            RawSong(
                                cachedSong.file,
                                cachedSong.properties,
                                cachedSong.tags,
                                coverResult.cover,
                                cachedSong.addedMs)
                        }
                        is CacheResult.Outdated -> NewSong(file, cacheResult.addedMs)
                        is CacheResult.Miss -> NewSong(file, System.currentTimeMillis())
                    }
                }
                .merge()
        val playlists: Werk<Explored> = werk {
            storedPlaylists.read().forEach { send(RawPlaylist(it)) }
        }
        return listOf(songs, playlists).merge()
    }
}

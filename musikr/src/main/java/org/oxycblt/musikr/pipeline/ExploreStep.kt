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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import org.oxycblt.musikr.Config
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cache.CachedSong
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.fs.FS
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.playlist.m3u.M3U
import org.oxycblt.musikr.util.merge
import org.oxycblt.musikr.util.mapParallel
import org.oxycblt.musikr.util.tryAsyncWith

internal interface ExploreStep {
    suspend fun explore(scope: CoroutineScope, explored: Channel<Explored>): Deferred<Result<Unit>>

    companion object {
        fun from(context: Context, config: Config): ExploreStep =
            ExploreStepImpl(config.fs, config.storage)
    }
}

private class ExploreStepImpl(private val fs: FS, private val storage: Storage) : ExploreStep {
    override suspend fun explore(
        scope: CoroutineScope,
        explored: Channel<Explored>
    ): Deferred<Result<Unit>> {
        val files = Channel<File>(Channel.UNLIMITED)
        val filesTask = fs.explore(files)

        val classified = Channel<Classified>(Channel.UNLIMITED)
        val classifiedTask =
            scope.mapParallel(PARALLELISM, files, classified, Dispatchers.IO) { file ->
                if (!file.mimeType.startsWith("audio/") && file.mimeType != M3U.MIME_TYPE) {
                    return@mapParallel null
                }
                val cacheResult = storage.cache.read(file)
                when (cacheResult) {
                    is CacheResult.Hit -> NeedsCover(cacheResult.song)
                    is CacheResult.Stale -> Finalized(NewSong(cacheResult.file))
                    is CacheResult.Miss -> Finalized(NewSong(cacheResult.file))
                }
            }

        val finalized = Channel<Finalized>(Channel.UNLIMITED)
        val exploredTask =
            scope.mapParallel(PARALLELISM, classified, finalized, Dispatchers.IO) { item ->
                when (item) {
                    is Finalized -> item
                    is NeedsCover -> {
                        when (val coverResult =
                            item.cachedSong.coverId?.let { id -> storage.covers.obtain(id) }) {
                            is CoverResult.Hit ->
                                Finalized(item.cachedSong.toRawSong(coverResult.cover))
                            null -> Finalized(item.cachedSong.toRawSong(null))
                            else -> Finalized(NewSong(item.cachedSong.file))
                        }
                    }
                }
            }
        val playlists = Channel<Explored>(Channel.UNLIMITED)
        val playlistsTask =
            scope.tryAsyncWith(playlists, Dispatchers.IO) {
                for (playlist in storage.storedPlaylists.read()) {
                    val rawPlaylist = RawPlaylist(playlist)
                    it.send(rawPlaylist)
                }
            }

        val mergeTask =
            scope.tryAsyncWith(explored, Dispatchers.Main) {
                for (item in finalized) {
                    it.send(item.explored)
                }
                for (playlist in playlists) {
                    it.send(playlist)
                }
            }

        return scope.merge(filesTask, classifiedTask, exploredTask, playlistsTask, mergeTask)
    }

    private sealed interface Classified

    private data class NeedsCover(val cachedSong: CachedSong) : Classified

    private data class Finalized(val explored: Explored) : Classified

    private fun CachedSong.toRawSong(cover: Cover?) =
        RawSong(file, properties, tags, cover, addedMs)

    private companion object {
        const val PARALLELISM = 8
    }
}

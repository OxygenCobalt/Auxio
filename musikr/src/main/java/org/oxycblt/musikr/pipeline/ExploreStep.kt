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
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cache.CachedFile
import org.oxycblt.musikr.covers.CoverResult
import org.oxycblt.musikr.fs.FS
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.fs.saf.contentResolverSafe
import org.oxycblt.musikr.playlist.PendingImportHandle
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.playlist.SongPointer
import org.oxycblt.musikr.playlist.deriveNameFromFileName
import org.oxycblt.musikr.playlist.m3u.M3U
import org.oxycblt.musikr.util.mapParallel
import org.oxycblt.musikr.util.merge
import org.oxycblt.musikr.util.tryAsyncWith

internal interface ExploreStep {
    suspend fun explore(scope: CoroutineScope, explored: Channel<Explored>): Deferred<Result<Unit>>

    companion object {
        fun from(context: Context, config: Config): ExploreStep =
            ExploreStepImpl(context, config.fs, config.storage, config.withExternalPlaylists)
    }
}

private class ExploreStepImpl(
    private val context: Context,
    private val fs: FS,
    private val storage: Storage,
    private val withExternalPlaylists: Boolean,
) : ExploreStep {
    private val m3u = M3U.from(context)

    override suspend fun explore(
        scope: CoroutineScope,
        explored: Channel<Explored>,
    ): Deferred<Result<Unit>> {
        val files = Channel<File>(Channel.UNLIMITED)
        val filesTask = fs.explore(files)

        val storedPlaylists = storage.storedPlaylists.read()

        val classified = Channel<Classified>(Channel.UNLIMITED)
        val classifiedTask =
            scope.mapParallel(PARALLELISM, files, classified, Dispatchers.IO) { file ->
                if (file.isM3U()) {
                    val playlist = if (withExternalPlaylists) readM3UPlaylist(file) else null
                    return@mapParallel Finalized(playlist?.let { RawPlaylist(it) } ?: NotAudio)
                }
                if (
                    !file.mimeType.startsWith("audio/") &&
                        file.mimeType != "application/ogg" &&
                        file.mimeType != "application/x-ogg" &&
                        file.mimeType != "application/octet-stream"
                ) {
                    return@mapParallel Finalized(NotAudio)
                }
                when (val cacheResult = storage.cache.read(file)) {
                    is CacheResult.Hit -> NeedsHydration(cacheResult.file)
                    is CacheResult.Stale -> Finalized(NewSong(cacheResult.file))
                    is CacheResult.Miss -> Finalized(NewSong(cacheResult.file))
                }
            }

        val finalized = Channel<Finalized>(Channel.UNLIMITED)
        val exploredTask =
            scope.mapParallel(PARALLELISM, classified, finalized, Dispatchers.IO) { item ->
                when (item) {
                    is Finalized -> item
                    is NeedsHydration -> {
                        val audio = item.cachedFile.audio ?: return@mapParallel Finalized(NotAudio)
                        val coverId =
                            when (
                                val result = audio.coverId?.let { id -> storage.covers.obtain(id) }
                            ) {
                                is CoverResult.Hit -> result.cover
                                is CoverResult.Miss ->
                                    return@mapParallel Finalized(NewSong(item.cachedFile.file))
                                null -> null
                            }

                        Finalized(
                            RawSong(
                                item.cachedFile.file,
                                audio.properties,
                                audio.tags,
                                coverId,
                                item.cachedFile.addedMs,
                            )
                        )
                    }
                }
            }
        val playlists = Channel<Explored>(Channel.UNLIMITED)
        val playlistsTask =
            scope.tryAsyncWith(playlists, Dispatchers.IO) {
                for (playlist in storedPlaylists) {
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

    private fun File.isM3U(): Boolean {
        if (mimeType == M3U.MIME_TYPE) return true
        val name = path.name?.lowercase() ?: return false
        return name.endsWith(".m3u") || name.endsWith(".m3u8")
    }

    private fun readM3UPlaylist(file: File): PlaylistFile? {
        val uid =
            Music.UID.auxio(Music.UID.Item.PLAYLIST) { update(file.path.toString().toByteArray()) }
        val imported =
            context.contentResolverSafe.openInputStream(file.uri)?.use { stream ->
                m3u.read(stream, file.path.directory)
            } ?: return null
        val name = deriveNameFromFileName(file.path.name) ?: return null
        return PlaylistFile(
            name,
            imported.paths.map { SongPointer.Path(it) },
            PendingImportHandle(uid, file.modifiedMs),
        )
    }

    private sealed interface Classified

    private data class NeedsHydration(val cachedFile: CachedFile) : Classified

    private data class Finalized(val explored: Explored) : Classified

    private companion object {
        const val PARALLELISM = 8
    }
}

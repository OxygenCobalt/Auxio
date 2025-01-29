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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.device.DeviceFiles
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.m3u.M3U

internal interface ExploreStep {
    suspend fun explore(
        locations: List<MusicLocation>,
        explored: SendChannel<ExploreNode>
    ): Deferred<Result<Unit>>

    companion object {
        fun from(context: Context, storage: Storage): ExploreStep =
            ExploreStepImpl(DeviceFiles.from(context), storage.storedPlaylists)
    }
}

private class ExploreStepImpl(
    private val deviceFiles: DeviceFiles,
    private val storedPlaylists: StoredPlaylists
) : ExploreStep {
    override suspend fun explore(
        locations: List<MusicLocation>,
        explored: SendChannel<ExploreNode>
    ) = coroutineScope {
        async {
            try {
                val audioTask = async {
                    try {
                        deviceFiles
                            .explore(locations.asFlow())
                            .mapNotNull {
                                when {
                                    it.mimeType == M3U.MIME_TYPE -> null
                                    it.mimeType.startsWith("audio/") -> ExploreNode.Audio(it)
                                    else -> null
                                }
                            }
                            .collect { explored.send(it) }
                        Result.success(Unit)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }

                val playlistTask = async {
                    try {
                        storedPlaylists.read().forEach { explored.send(ExploreNode.Playlist(it)) }
                        Result.success(Unit)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }

                audioTask.await().getOrThrow()
                playlistTask.await().getOrThrow()

                explored.close()
                Result.success(Unit)
            } catch (e: Exception) {
                explored.close(e)
                Result.failure(e)
            }
        }
    }
}

internal sealed interface ExploreNode {
    data class Audio(val file: DeviceFile) : ExploreNode

    data class Playlist(val file: PlaylistFile) : ExploreNode
}

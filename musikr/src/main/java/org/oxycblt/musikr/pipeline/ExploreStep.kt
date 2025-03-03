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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.fs.device.DeviceDirectory
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.fs.device.DeviceNode
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.device.DeviceFiles
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.m3u.M3U

internal interface ExploreStep {
    fun explore(locations: List<MusicLocation>): Flow<ExploreNode>

    companion object {
        fun from(context: Context, storage: Storage): ExploreStep =
            ExploreStepImpl(DeviceFiles.from(context), storage.storedPlaylists)
    }
}

private class ExploreStepImpl(
    private val deviceFiles: DeviceFiles,
    private val storedPlaylists: StoredPlaylists
) : ExploreStep {
    override fun explore(locations: List<MusicLocation>): Flow<ExploreNode> {
        val audios =
            deviceFiles
                .explore(locations.asFlow())
                .flattenFilter {
                    it.mimeType.startsWith("audio/") || it.mimeType == M3U.MIME_TYPE
                }
                .flowOn(Dispatchers.IO)
                .buffer()
        val playlists =
            flow { emitAll(storedPlaylists.read().asFlow()) }
                .map { ExploreNode.Playlist(it) }
                .flowOn(Dispatchers.IO)
                .buffer()
        return merge(audios, playlists)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<DeviceNode>.flattenFilter(block: (DeviceFile) -> Boolean): Flow<ExploreNode> = flow {
        collect {
            val recurse = mutableListOf<Flow<ExploreNode>>()
            when {
                it is DeviceFile && block(it) -> emit(ExploreNode.Audio(it))
                it is DeviceDirectory -> recurse.add(it.children.flattenFilter(block))
                else -> {}
            }
            emitAll(recurse.asFlow().flattenMerge())
        }
    }
}

internal sealed interface ExploreNode {
    data class Audio(val file: DeviceFile) : ExploreNode

    data class Playlist(val file: PlaylistFile) : ExploreNode
}

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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.fs.query.DeviceFiles
import org.oxycblt.musikr.playlist.m3u.M3U

internal interface ExploreStep {
    fun explore(locations: List<MusicLocation>): Flow<ExploreNode>

    companion object {
        fun from(context: Context): ExploreStep = ExploreStepImpl(DeviceFiles.from(context))
    }
}

private class ExploreStepImpl(private val deviceFiles: DeviceFiles) : ExploreStep {
    override fun explore(locations: List<MusicLocation>) =
        deviceFiles
            .explore(locations.asFlow())
            .mapNotNull {
                when {
                    it.mimeType == M3U.MIME_TYPE -> null
                    it.mimeType.startsWith("audio/") -> ExploreNode.Audio(it)
                    else -> null
                }
            }
            .flowOn(Dispatchers.IO)
}

internal sealed interface ExploreNode {
    data class Audio(val file: DeviceFile) : ExploreNode
}

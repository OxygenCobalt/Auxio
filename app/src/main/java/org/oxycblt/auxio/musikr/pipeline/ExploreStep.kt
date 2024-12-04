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
 
package org.oxycblt.auxio.musikr.pipeline

import android.net.Uri
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import org.oxycblt.auxio.musikr.fs.DeviceFile
import org.oxycblt.auxio.musikr.fs.DeviceFiles
import org.oxycblt.auxio.musikr.playlist.m3u.M3U

interface ExploreStep {
    fun explore(uris: List<Uri>): Flow<ExploreNode>
}

class ExploreStepImpl @Inject constructor(private val deviceFiles: DeviceFiles) : ExploreStep {
    override fun explore(uris: List<Uri>) =
        deviceFiles
            .explore(uris.asFlow())
            .mapNotNull {
                when {
                    it.mimeType == M3U.MIME_TYPE -> null
                    it.mimeType.startsWith("audio/") -> ExploreNode.Audio(it)
                    else -> null
                }
            }
            .flowOn(Dispatchers.IO)
}

sealed interface ExploreNode {
    data class Audio(val file: DeviceFile) : ExploreNode
}

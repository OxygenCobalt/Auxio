/*
 * Copyright (c) 2024 Auxio Project
 * Indexer.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.stack

import android.net.Uri
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import org.oxycblt.auxio.music.stack.explore.Explorer
import org.oxycblt.auxio.music.stack.interpret.Interpretation
import org.oxycblt.auxio.music.stack.interpret.Interpreter
import org.oxycblt.auxio.music.stack.interpret.model.MutableLibrary

interface Indexer {
    suspend fun run(
        uris: List<Uri>,
        interpretation: Interpretation,
        onProgress: suspend (IndexingProgress) -> Unit = {}
    ): MutableLibrary
}

/**
 * Represents the current progress of music loading.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class IndexingProgress(val interpreted: Int, val explored: Int)

class IndexerImpl
@Inject
constructor(private val explorer: Explorer, private val interpreter: Interpreter) : Indexer {
    override suspend fun run(
        uris: List<Uri>,
        interpretation: Interpretation,
        onProgress: suspend (IndexingProgress) -> Unit
    ) = coroutineScope {
        var interpreted = 0
        var explored = 0
        val files =
            explorer.explore(uris) {
                explored++
                onProgress(IndexingProgress(interpreted, explored))
            }
        val audioFiles = files.audios.flowOn(Dispatchers.IO).buffer()
        val playlistFiles = files.playlists.flowOn(Dispatchers.IO).buffer()

        interpreter.interpret(audioFiles, playlistFiles, interpretation) {
            interpreted++
            onProgress(IndexingProgress(interpreted, explored))
        }
    }
}

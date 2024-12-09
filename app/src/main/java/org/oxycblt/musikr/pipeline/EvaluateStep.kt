/*
 * Copyright (c) 2024 Auxio Project
 * EvaluateStep.kt is part of Auxio.
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

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.oxycblt.musikr.MutableLibrary
import org.oxycblt.musikr.graph.MusicGraph
import org.oxycblt.musikr.model.LibraryFactory
import org.oxycblt.musikr.tag.Interpretation
import org.oxycblt.musikr.tag.interpret.TagInterpreter

interface EvaluateStep {
    suspend fun evaluate(
        interpretation: Interpretation,
        extractedMusic: Flow<ExtractedMusic>
    ): MutableLibrary
}

class EvaluateStepImpl
@Inject
constructor(
    private val tagInterpreter: TagInterpreter,
    private val musicGraphFactory: MusicGraph.Factory,
    private val libraryFactory: LibraryFactory
) : EvaluateStep {
    override suspend fun evaluate(
        interpretation: Interpretation,
        extractedMusic: Flow<ExtractedMusic>
    ): MutableLibrary {
        val preSongs =
            extractedMusic
                .filterIsInstance<ExtractedMusic.Song>()
                .map { tagInterpreter.interpret(it.file, it.tags, interpretation) }
                .flowOn(Dispatchers.Main)
                .buffer(Channel.UNLIMITED)
        val graphBuilder = musicGraphFactory.builder()
        preSongs.collect { graphBuilder.add(it) }
        val graph = graphBuilder.build()
        return libraryFactory.create(graph)
    }
}

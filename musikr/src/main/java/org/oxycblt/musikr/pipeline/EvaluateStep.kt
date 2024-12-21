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

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.MutableLibrary
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.graph.MusicGraph
import org.oxycblt.musikr.model.LibraryFactory
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.interpret.PlaylistInterpreter
import org.oxycblt.musikr.tag.interpret.TagInterpreter

internal interface EvaluateStep {
    suspend fun evaluate(extractedMusic: Flow<ExtractedMusic>): MutableLibrary

    companion object {
        fun new(storage: Storage, interpretation: Interpretation): EvaluateStep =
            EvaluateStepImpl(
                TagInterpreter.new(interpretation),
                PlaylistInterpreter.new(interpretation),
                storage.storedPlaylists,
                LibraryFactory.new())
    }
}

private class EvaluateStepImpl(
    private val tagInterpreter: TagInterpreter,
    private val playlistInterpreter: PlaylistInterpreter,
    private val storedPlaylists: StoredPlaylists,
    private val libraryFactory: LibraryFactory
) : EvaluateStep {
    override suspend fun evaluate(extractedMusic: Flow<ExtractedMusic>): MutableLibrary {
        val filterFlow =
            extractedMusic.divert {
                when (it) {
                    is ExtractedMusic.Song -> Divert.Right(it.song)
                    is ExtractedMusic.Playlist -> Divert.Left(it.file)
                }
            }
        val rawSongs = filterFlow.right
        val preSongs =
            rawSongs
                .map { wrap(it, tagInterpreter::interpret) }
                .flowOn(Dispatchers.Default)
                .buffer(Channel.UNLIMITED)
        val prePlaylists =
            filterFlow.left
                .map { wrap(it, playlistInterpreter::interpret) }
                .flowOn(Dispatchers.Default)
                .buffer(Channel.UNLIMITED)
        val graphBuilder = MusicGraph.builder()
        val graphBuild =
            merge(
                filterFlow.manager,
                preSongs.onEach {
                    wrap(it, graphBuilder::add)
                },
                prePlaylists.onEach { wrap(it, graphBuilder::add) })
        graphBuild.collect()
        val graph = graphBuilder.build()
        return libraryFactory.create(graph, storedPlaylists, playlistInterpreter)
    }
}

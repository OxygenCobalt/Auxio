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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.MutableLibrary
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.graph.MusicGraph
import org.oxycblt.musikr.log.Logger
import org.oxycblt.musikr.model.LibraryFactory
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.interpret.PlaylistInterpreter
import org.oxycblt.musikr.tag.interpret.Interpreter

internal interface EvaluateStep {
    suspend fun evaluate(complete: Flow<Complete>): MutableLibrary

    companion object {
        fun new(storage: Storage, interpretation: Interpretation, logger: Logger): EvaluateStep =
            EvaluateStepImpl(
                Interpreter.new(interpretation),
                PlaylistInterpreter.new(interpretation),
                storage.storedPlaylists,
                LibraryFactory.new(),
                logger.primary("eval"))
    }
}

private class EvaluateStepImpl(
    private val interpreter: Interpreter,
    private val playlistInterpreter: PlaylistInterpreter,
    private val storedPlaylists: StoredPlaylists,
    private val libraryFactory: LibraryFactory,
    private val logger: Logger
) : EvaluateStep {
    override suspend fun evaluate(complete: Flow<Complete>): MutableLibrary {
        logger.d("evaluate start.")
        val filterFlow =
            complete.divert {
                when (it) {
                    is RawSong -> Divert.Right(it)
                    is RawPlaylist -> Divert.Left(it.file)
                }
            }
        val rawSongs = filterFlow.right
        val preSongs =
            rawSongs
                .tryMap { interpreter.interpret(it) }
                .flowOn(Dispatchers.Default)
                .buffer(Channel.UNLIMITED)
        val prePlaylists =
            filterFlow.left
                .tryMap { playlistInterpreter.interpret(it) }
                .flowOn(Dispatchers.Default)
                .buffer(Channel.UNLIMITED)
        val graphBuilder = MusicGraph.builder()
        // Concurrent addition of playlists and songs could easily
        // break the grapher (remember, individual pipeline elements
        // are generally unaware of the highly concurrent nature of
        // the pipeline), prevent that with a mutex.
        val graphLock = Mutex()
        val graphBuild =
            merge(
                filterFlow.manager,
                preSongs.onEach { graphLock.withLock { graphBuilder.add(it) } },
                prePlaylists.onEach { graphLock.withLock { graphBuilder.add(it) } })
        graphBuild.collect()
        logger.d("starting graph build")
        val graph = graphBuilder.build()
        logger.d("graph build done, creating library")
        return libraryFactory.create(graph, storedPlaylists, playlistInterpreter)
    }
}

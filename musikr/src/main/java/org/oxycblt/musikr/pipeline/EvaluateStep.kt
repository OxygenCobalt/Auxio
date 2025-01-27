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
    suspend fun evaluate(complete: Werk<Complete>): MutableLibrary

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
    override suspend fun evaluate(complete: Werk<Complete>): MutableLibrary {
        logger.d("evaluate start.")
        val graphBuilder = MusicGraph.builder()
        for (item in complete.chan) {
            when (item) {
                is RawSong -> graphBuilder.add(interpreter.interpret(item))
                is RawPlaylist -> graphBuilder.add(playlistInterpreter.interpret(item.file))
            }
        }
        complete.def.await()
        logger.d("starting graph build")
        val graph = graphBuilder.build()
        logger.d("graph build done, creating library")
        return libraryFactory.create(graph, storedPlaylists, playlistInterpreter)
    }
}

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

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.MutableLibrary
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.graph.MusicGraph
import org.oxycblt.musikr.model.LibraryFactory
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.interpret.PlaylistInterpreter
import org.oxycblt.musikr.tag.interpret.TagInterpreter

internal interface EvaluateStep {
    suspend fun evaluate(extractedMusic: Channel<ExtractedMusic>): Deferred<Result<MutableLibrary>>

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
    override suspend fun evaluate(
        extractedMusic: Channel<ExtractedMusic>
    ): Deferred<Result<MutableLibrary>> = coroutineScope {
        async {
            try {
                val graphBuilder = MusicGraph.builder()
                for (music in extractedMusic) {
                    when (music) {
                        is ExtractedMusic.Valid.Song ->
                            graphBuilder.add(tagInterpreter.interpret(music.song))
                        is ExtractedMusic.Valid.Playlist ->
                            graphBuilder.add(playlistInterpreter.interpret(music.file))
                        is ExtractedMusic.Invalid -> {}
                    }
                }
                val graph = graphBuilder.build()
                val library = libraryFactory.create(graph, storedPlaylists, playlistInterpreter)

                extractedMusic.close()
                Result.success(library)
            } catch (e: Exception) {
                extractedMusic.close(e)
                Result.failure(e)
            }
        }
    }
}

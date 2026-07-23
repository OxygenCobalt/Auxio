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

import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.Channel
import org.oxycblt.musikr.BuildConfig
import org.oxycblt.musikr.Config
import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.MutableLibrary
import org.oxycblt.musikr.graph.MusicGraph
import org.oxycblt.musikr.model.LibraryFactory
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.playlist.interpret.PlaylistInterpreter
import org.oxycblt.musikr.tag.interpret.TagInterpreter

internal interface EvaluateStep {
    suspend fun evaluate(extractedMusic: Channel<Extracted>): MutableLibrary

    companion object {
        fun new(context: Context, config: Config, interpretation: Interpretation): EvaluateStep =
            EvaluateStepImpl(
                context,
                TagInterpreter.new(interpretation),
                PlaylistInterpreter.new(interpretation),
                config.storage.storedPlaylists,
                LibraryFactory.new(),
            )
    }
}

private class EvaluateStepImpl(
    private val context: Context,
    private val tagInterpreter: TagInterpreter,
    private val playlistInterpreter: PlaylistInterpreter,
    private val storedPlaylists: StoredPlaylists,
    private val libraryFactory: LibraryFactory,
) : EvaluateStep {
    override suspend fun evaluate(extractedMusic: Channel<Extracted>): MutableLibrary {
        val builder = MusicGraph.builder()
        for (extracted in extractedMusic) {
            when (extracted) {
                is RawSong -> builder.add(tagInterpreter.interpret(extracted))
                is RawPlaylist -> builder.add(playlistInterpreter.interpret(extracted.file))
                is NotAudio -> {}
                is InvalidSong -> {}
            }
        }
        val graph = builder.build()

        // Build the uri → uid map for every song in this index run.
        val currentUriToUid =
            graph.songVertex.associate { it.preSong.uri.toString() to it.preSong.v363Uid }

        // Collect UID migrations from two sources and apply them together:
        //  1. legacyUids probe: hash UIDs the song carried before gaining a MusicBrainz ID
        //     (handles the "only MB ID added, other tags unchanged" case without a prior URI
        // record).
        //  2. URI index diff: any song whose file URI was previously mapped to a different UID
        //     (handles tag changes, MB ID + tag changes together, or any other UID drift).
        val uriMigrations = storedPlaylists.computeUriMigrations(currentUriToUid)
        val allMigrations = graph.uidMigrations + uriMigrations
        storedPlaylists.migrate(allMigrations)

        // Persist the current uri → uid mapping so the next run can detect further UID changes.
        storedPlaylists.updateUriIndex(currentUriToUid)

        // Apply URI migrations to the in-memory graph so the current rescan shows correct playlist
        // contents immediately. The graph was built before migrations were computed (the DB still
        // had old UIDs when PlaylistVertex.pointerMap was populated), so without this songs whose
        // UID changed this run would appear missing until a second rescan.
        graph.applyMigrations(uriMigrations)

        // Render graph to Graphviz in debug mode
        if (BuildConfig.DEBUG) {
            try {
                val fileName = "music_graph_debug.dot"
                graph.renderToGraphviz(context, fileName)
                val filePath = context.filesDir.resolve(fileName).absolutePath
                Log.d("EvaluateStep", "Music graph rendered to: $filePath")
                Log.d("EvaluateStep", "To pull the file, run: adb pull $filePath")
            } catch (e: Exception) {
                Log.e("EvaluateStep", "Failed to render music graph", e)
            }
        }

        return libraryFactory.create(graph, storedPlaylists, playlistInterpreter)
    }
}

/*
 * Copyright (c) 2024 Auxio Project
 * Musikr.kt is part of Auxio.
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
 
package org.oxycblt.musikr

import android.content.Context
import android.os.Build
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.log.Logger
import org.oxycblt.musikr.pipeline.Divert
import org.oxycblt.musikr.pipeline.EvaluateStep
import org.oxycblt.musikr.pipeline.ExploreStep
import org.oxycblt.musikr.pipeline.Explored
import org.oxycblt.musikr.pipeline.ExtractStep
import org.oxycblt.musikr.pipeline.Extracted
import org.oxycblt.musikr.pipeline.divert

/**
 * A highly opinionated, multi-threaded device music library.
 *
 * Use this to load music with [run].
 *
 * Note the following:
 * 1. Musikr's API surface is intended to be primarily "stateless", with side-effects mostly
 *    contained within [Storage]. It's your job to manage long-term state.
 * 2. There are no "defaults" in Musikr. You should think carefully about the parameters you are
 *    specifying and know consider they are desirable or not.
 * 3. Musikr is currently not extendable, so if you're embedding this elsewhere you should be ready
 *    to fork and modify the source code.
 */
interface Musikr {
    /**
     * Start loading music from the given [locations] and the configuration provided earlier.
     *
     * @param locations The [MusicLocation]s to search for music in.
     * @param onProgress Optional callback to receive progress on the current status of the music
     *   pipeline. Warning: These events will be rapid-fire.
     * @return A handle to the newly created library alongside further cleanup.
     */
    suspend fun run(
        locations: List<MusicLocation>,
        onProgress: suspend (IndexingProgress) -> Unit = {}
    ): LibraryResult

    companion object {
        /**
         * Create a new instance from the given configuration.
         *
         * @param context The context to use for loading resources.
         * @param logger The logger to use for logging events.
         * @param storage Side-effect laden storage for use within the music loader **and** when
         *   mutating [MutableLibrary]. You should take responsibility for managing their long-term
         *   state.
         * @param interpretation The configuration to use for interpreting certain vague tags. This
         *   should be configured by the user, if possible.
         */
        fun new(
            context: Context,
            storage: Storage,
            interpretation: Interpretation,
            logger: Logger
        ): Musikr =
            MusikrImpl(
                logger,
                storage,
                ExploreStep.from(context, storage, logger),
                ExtractStep.from(context, storage, logger),
                EvaluateStep.new(storage, interpretation, logger))
    }
}

/** Simple library handle returned by [Musikr.run]. */
interface LibraryResult {
    val library: MutableLibrary

    /**
     * Clean up expired resources. This should be done as soon as possible after music loading to
     * reduce storage use.
     *
     * This may have unexpected results if previous [Library]s are in circulation across your app,
     * so use it once you've fully updated your state.
     */
    suspend fun cleanup()
}

/** Music loading progress as reported by the music pipeline. */
sealed interface IndexingProgress {
    /**
     * Currently indexing and extracting tags from device music.
     *
     * @param explored The amount of music currently found from the given [MusicLocation]s.
     * @param loaded The amount of music that has had metadata extracted and parsed.
     */
    data class Songs(val loaded: Int, val explored: Int) : IndexingProgress

    /**
     * Currently creating the music graph alongside I/O finalization.
     *
     * There is no way to measure progress on these events.
     */
    data object Indeterminate : IndexingProgress
}

private class MusikrImpl(
    private val logger: Logger,
    private val storage: Storage,
    private val exploreStep: ExploreStep,
    private val extractStep: ExtractStep,
    private val evaluateStep: EvaluateStep
) : Musikr {
    override suspend fun run(
        locations: List<MusicLocation>,
        onProgress: suspend (IndexingProgress) -> Unit
    ) = coroutineScope {
        logger.d(
            "musikr start.",
            "hw:",
            Build.MANUFACTURER,
            Build.MODEL,
            Build.SUPPORTED_ABIS.joinToString(" "),
            "sw:",
            Build.VERSION.SDK_INT,
            Build.DISPLAY)

        var exploredCount = 0
        var extractedCount = 0
        val explored =
            exploreStep
                .explore(locations)
                .buffer(Channel.UNLIMITED)
                .onStart { onProgress(IndexingProgress.Songs(0, 0)) }
                .onEach { onProgress(IndexingProgress.Songs(extractedCount, ++exploredCount)) }
        val typeDiversion =
            explored.divert {
                when (it) {
                    is Explored.Known -> Divert.Right(it)
                    is Explored.New -> Divert.Left(it)
                }
            }
        val known = typeDiversion.right
        val new = typeDiversion.left
        val extracted =
            extractStep
                .extract(new)
                .buffer(Channel.UNLIMITED)
                .onEach { onProgress(IndexingProgress.Songs(++extractedCount, exploredCount)) }
                .onCompletion { onProgress(IndexingProgress.Indeterminate) }
        val complete =
            merge(typeDiversion.manager, known, extracted.filterIsInstance<Extracted.Valid>())
        val library = evaluateStep.evaluate(complete)
        LibraryResultImpl(storage, library)
    }
}

private class LibraryResultImpl(
    private val storage: Storage,
    override val library: MutableLibrary
) : LibraryResult {
    override suspend fun cleanup() {
        storage.cache.cleanup(library.songs)
        storage.covers.cleanup(library.songs.mapNotNull { it.cover })
    }
}

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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.pipeline.EvaluateStep
import org.oxycblt.musikr.pipeline.ExploreStep
import org.oxycblt.musikr.pipeline.ExtractStep

interface Musikr {
    suspend fun run(
        locations: List<MusicLocation>,
        storage: Storage,
        interpretation: Interpretation,
        onProgress: suspend (IndexingProgress) -> Unit = {}
    ): MutableLibrary

    companion object {
        fun new(context: Context): Musikr =
            MusikrImpl(ExploreStep.from(context), ExtractStep.from(context), EvaluateStep.new())
    }
}

/**
 * Represents the current progress of music loading.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface IndexingProgress {
    data class Songs(val loaded: Int, val explored: Int) : IndexingProgress

    data object Indeterminate : IndexingProgress
}

private class MusikrImpl(
    private val exploreStep: ExploreStep,
    private val extractStep: ExtractStep,
    private val evaluateStep: EvaluateStep
) : Musikr {
    override suspend fun run(
        locations: List<MusicLocation>,
        storage: Storage,
        interpretation: Interpretation,
        onProgress: suspend (IndexingProgress) -> Unit
    ) = coroutineScope {
        var exploredCount = 0
        var extractedCount = 0
        val explored =
            exploreStep
                .explore(locations)
                .buffer(Channel.UNLIMITED)
                .onStart { onProgress(IndexingProgress.Songs(0, 0)) }
                .onEach { onProgress(IndexingProgress.Songs(extractedCount, ++exploredCount)) }
        val extracted =
            extractStep
                .extract(storage, explored)
                .buffer(Channel.UNLIMITED)
                .onEach { onProgress(IndexingProgress.Songs(++extractedCount, exploredCount)) }
                .onCompletion { onProgress(IndexingProgress.Indeterminate) }
        evaluateStep.evaluate(interpretation, extracted)
    }
}

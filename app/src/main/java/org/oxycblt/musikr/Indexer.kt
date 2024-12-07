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
 
package org.oxycblt.musikr

import android.net.Uri
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.model.MutableLibrary
import org.oxycblt.musikr.pipeline.EvaluateStep
import org.oxycblt.musikr.pipeline.ExploreStep
import org.oxycblt.musikr.pipeline.ExtractStep
import org.oxycblt.musikr.tag.Interpretation

interface Indexer {
    suspend fun run(
        locations: MusicLocation,
        interpretation: Interpretation,
        onProgress: suspend (IndexingProgress) -> Unit = {}
    ): MutableLibrary
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

class IndexerImpl
@Inject
constructor(
    private val exploreStep: ExploreStep,
    private val extractStep: ExtractStep,
    private val evaluateStep: EvaluateStep
) : Indexer {
    override suspend fun run(
        locations: MusicLocation,
        interpretation: Interpretation,
        onProgress: suspend (IndexingProgress) -> Unit
    ) = coroutineScope {
        var exploredCount = 0
        val explored =
            exploreStep
                .explore(uris)
                .buffer(Channel.UNLIMITED)
                .onStart { onProgress(IndexingProgress.Songs(0, 0)) }
                .onEach { onProgress(IndexingProgress.Songs(0, ++exploredCount)) }
        var extractedCount = 0
        val extracted =
            extractStep
                .extract(explored)
                .buffer(Channel.UNLIMITED)
                .onEach { onProgress(IndexingProgress.Songs(++extractedCount, exploredCount)) }
                .onCompletion { onProgress(IndexingProgress.Indeterminate) }
        evaluateStep.evaluate(interpretation, extracted)
    }
}

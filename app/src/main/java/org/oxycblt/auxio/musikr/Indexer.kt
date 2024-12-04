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
 
package org.oxycblt.auxio.musikr

import android.net.Uri
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import org.oxycblt.auxio.musikr.model.impl.MutableLibrary
import org.oxycblt.auxio.musikr.pipeline.EvaluateStep
import org.oxycblt.auxio.musikr.pipeline.ExploreStep
import org.oxycblt.auxio.musikr.pipeline.ExtractStep
import org.oxycblt.auxio.musikr.tag.Interpretation

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
        uris: List<Uri>,
        interpretation: Interpretation,
        onProgress: suspend (IndexingProgress) -> Unit
    ) = coroutineScope {
        val explored = exploreStep.explore(uris).buffer(Channel.UNLIMITED)
        val extracted = extractStep.extract(explored).buffer(Channel.UNLIMITED)
        evaluateStep.evaluate(interpretation, extracted)
    }

    private fun <T> Flow<T>.cap(start: suspend () -> Unit, end: suspend () -> Unit): Flow<T> =
        flow {
            start()
            try {
                collect { emit(it) }
            } finally {
                end()
            }
        }
}

/*
 * Copyright (c) 2024 Auxio Project
 * FlowUtil.kt is part of Auxio.
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

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.withIndex

/**
 * Equally "distributes" the values of some flow across n new flows.
 *
 * Note that this function requires the "manager" flow to be consumed alongside the split flows in
 * order to function. Without this, all of the newly split flows will simply block.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T, R> Flow<T>.distributedMap(
    n: Int,
    on: CoroutineContext = Dispatchers.Main,
    buffer: Int = Channel.UNLIMITED,
    block: suspend (T) -> R,
): Flow<R> {
    val posChannels = List(n) { Channel<T>(Channel.UNLIMITED) }
    val managerFlow =
        flow<Nothing> {
            withIndex().collect {
                val index = it.index % n
                posChannels[index].send(it.value)
            }
            for (channel in posChannels) {
                channel.close()
            }
        }
    return (posChannels.map { it.receiveAsFlow() } + managerFlow)
        .asFlow()
        .map { it.tryMap(block).flowOn(on).buffer(buffer) }
        .flattenMerge()
}

internal fun <T, R> Flow<T>.tryMap(transform: suspend (T) -> R): Flow<R> = flow {
    collect { value ->
        try {
            emit(transform(value))
        } catch (e: Exception) {
            throw PipelineException(value, e)
        }
    }
}

internal suspend fun <T, A> Flow<T>.tryFold(initial: A, operation: suspend (A, T) -> A): A {
    var accumulator = initial
    collect { value ->
        try {
            accumulator = operation(accumulator, value)
        } catch (e: Exception) {
            throw PipelineException(value, e)
        }
    }
    return accumulator
}

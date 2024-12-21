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

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.withIndex

internal sealed interface Divert<L, R> {
    data class Left<L, R>(val value: L) : Divert<L, R>

    data class Right<L, R>(val value: R) : Divert<L, R>
}

internal class DivertedFlow<L, R>(
    val manager: Flow<Nothing>,
    val left: Flow<L>,
    val right: Flow<R>
)

internal inline fun <T, L, R> Flow<T>.divert(
    crossinline predicate: (T) -> Divert<L, R>
): DivertedFlow<L, R> {
    val leftChannel = Channel<L>(Channel.UNLIMITED)
    val rightChannel = Channel<R>(Channel.UNLIMITED)
    val managedFlow =
        flow<Nothing> {
            collect {
                when (val result = predicate(it)) {
                    is Divert.Left -> leftChannel.send(result.value)
                    is Divert.Right -> rightChannel.send(result.value)
                }
            }
            leftChannel.close()
            rightChannel.close()
        }
    return DivertedFlow(managedFlow, leftChannel.receiveAsFlow(), rightChannel.receiveAsFlow())
}

internal class DistributedFlow<T>(val manager: Flow<Nothing>, val flows: Flow<Flow<T>>)

/**
 * Equally "distributes" the values of some flow across n new flows.
 *
 * Note that this function requires the "manager" flow to be consumed alongside the split flows in
 * order to function. Without this, all of the newly split flows will simply block.
 */
internal fun <T> Flow<T>.distribute(n: Int): DistributedFlow<T> {
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
    val hotFlows = posChannels.asFlow().map { it.receiveAsFlow() }
    return DistributedFlow(managerFlow, hotFlows)
}

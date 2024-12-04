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
 
package org.oxycblt.auxio.musikr.pipeline

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.withIndex

data class HotCold<H, C>(val hot: H, val cold: Flow<C>)

inline fun <T, R> Flow<T>.mapPartition(crossinline predicate: (T) -> R?): HotCold<Flow<R>, T> {
    val passChannel = Channel<R>(Channel.UNLIMITED)
    val passFlow = passChannel.consumeAsFlow()
    val failFlow = flow {
        collect {
            val result = predicate(it)
            if (result != null) {
                passChannel.send(result)
            } else {
                emit(it)
            }
        }
    }
    return HotCold(passFlow, failFlow)
}

/**
 * Equally "distributes" the values of some flow across n new flows.
 *
 * Note that this function requires the "manager" flow to be consumed alongside the split flows in
 * order to function. Without this, all of the newly split flows will simply block.
 */
fun <T> Flow<T>.distribute(n: Int): HotCold<Array<Flow<T>>, Nothing> {
    val posChannels = Array(n) { Channel<T>(Channel.UNLIMITED) }
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
    val hotFlows = posChannels.map { it.receiveAsFlow() }.toTypedArray()
    return HotCold(hotFlows, managerFlow)
}

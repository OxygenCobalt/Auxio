package org.oxycblt.musikr.fs.device

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile

class FiniteHotFlow<T>(scope: CoroutineScope, emitter: suspend (emit: suspend (T) -> Unit) -> Unit) {
    private sealed interface HotObject<T> {
        data class More<T>(val value: T) : HotObject<T>
        data object Done : HotObject<Nothing>
    }

    private val sharedFlow = flow {
        emitter {
            emit(HotObject.More(it))
        }
        emit(HotObject.Done)
    }.shareIn(scope, SharingStarted.Lazily, replay = Int.MAX_VALUE)

    @Suppress("UNCHECKED_CAST")
    fun flow() = flow {
        sharedFlow.takeWhile { it is HotObject.More<*> }.collect {
            emit((it as HotObject.More<*>).value as T)
        }
    }
}
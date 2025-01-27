/*
 * Copyright (c) 2025 Auxio Project
 * Werk.kt is part of Auxio.
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
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * A kind of pseudo-[Flow] created from [Channel].
 *
 * We need the ability to parallelize and distribute work across multiple threads, and [Flow] cannot
 * do this without abusing it (and creating a pile of memory leaks in the process). However, the
 * [Channel] API is absolutely awful, requiring piles of boilerplate to do things that require
 * one-liners with [Flow]. This more or less makes a similar surface of one-liners as [Flow], except
 * composed entirely of [Channel] and [Deferred] composition.
 */
internal class Werk<E>(val chan: ReceiveChannel<E>, val def: Close) {
    internal suspend fun <O> then(lim: Int = Channel.UNLIMITED, block: suspend (E) -> O): Werk<O> {
        val nextChan = Channel<O>(lim)
        val nextDef = coroutineScope {
            async {
                for (e in chan) {
                    nextChan.send(block(e))
                }
                nextChan.close()
            }
        }
        return Werk(nextChan, CloseAll(def, DeferredClose(nextDef)))
    }

    internal suspend fun distribute(n: Int): List<Werk<E>> {
        val distChans = List(n) { Channel<E>(Channel.UNLIMITED) }
        val distDef = coroutineScope {
            async {
                var i = 0
                for (e in chan) {
                    distChans[i].send(e)
                    i = (i + 1) % n
                }
                distChans.forEach { it.close() }
            }
        }
        return distChans.map { Werk(it, CloseAll(def, DeferredClose(distDef))) }
    }

    internal suspend fun <L, R> divert(block: suspend (E) -> Divert<L, R>): Pair<Werk<L>, Werk<R>> {
        val leftChan = Channel<L>(Channel.UNLIMITED)
        val rightChan = Channel<R>(Channel.UNLIMITED)
        val lrDef = coroutineScope {
            async {
                for (e in chan) {
                    when (val result = block(e)) {
                        is Divert.Left -> leftChan.send(result.value)
                        is Divert.Right -> rightChan.send(result.value)
                    }
                }
                leftChan.close()
                rightChan.close()
            }
        }
        return Pair(
            Werk(leftChan, CloseAll(def, DeferredClose(lrDef))),
            Werk(rightChan, CloseAll(def, DeferredClose(lrDef))))
    }

    internal suspend inline fun <reified T> filterIsInstance(): Werk<T> {
        val nextChan = Channel<T>(Channel.UNLIMITED)
        val nextDef = coroutineScope {
            async {
                for (e in chan) {
                    if (e is T) {
                        nextChan.send(e)
                    }
                }
                nextChan.close()
            }
        }
        return Werk(nextChan, CloseAll(def, DeferredClose(nextDef)))
    }

    internal suspend fun on(
        start: suspend () -> Unit = {},
        each: suspend (E) -> Unit = {},
        end: suspend () -> Unit = {}
    ): Werk<E> {
        val nextChan = Channel<E>(Channel.UNLIMITED)
        val nextDef = coroutineScope {
            async {
                start()
                for (e in chan) {
                    nextChan.send(e)
                    each(e)
                }
                nextChan.close()
                end()
            }
        }
        return Werk(nextChan, CloseAll(def, DeferredClose(nextDef)))
    }
}

internal interface Close {
    suspend fun await()
}

internal suspend fun <E> werk(prod: suspend Channel<E>.() -> Unit): Werk<E> {
    val chan = Channel<E>(Channel.UNLIMITED)
    val def = coroutineScope {
        async {
            prod(chan)
            chan.close()
        }
    }
    return Werk(chan, DeferredClose(def))
}

internal suspend fun <E> Flow<E>.werk(): Werk<E> = werk { collect { send(it) } }

internal suspend fun <T, E> List<Werk<T>>.then(block: suspend (T) -> E): List<Werk<E>> {
    return map { it.then(block = block) }
}

internal suspend fun <S> Collection<Werk<out S>>.merge(): Werk<S> {
    val chan = Channel<S>(Channel.UNLIMITED)
    val defs = map { it.def }
    val def = coroutineScope {
        async {
            for (w in this@merge) {
                for (e in w.chan) {
                    chan.send(e)
                }
            }
            chan.close()
        }
    }
    return Werk(chan, CloseAll(DeferredClose(def), *defs.toTypedArray()))
}

internal sealed interface Divert<L, R> {
    data class Left<L, R>(val value: L) : Divert<L, R>

    data class Right<L, R>(val value: R) : Divert<L, R>
}

internal class DeferredClose(private val def: Deferred<*>) : Close {
    override suspend fun await() {
        def.await()
    }
}

internal class CloseAll(private val closes: List<Close>) : Close {
    constructor(vararg closes: Close) : this(closes.toList())

    override suspend fun await() {
        for (c in closes) {
            c.await()
        }
    }
}

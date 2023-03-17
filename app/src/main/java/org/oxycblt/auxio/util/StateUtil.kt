/*
 * Copyright (c) 2023 Auxio Project
 * StateUtil.kt is part of Auxio.
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
 
package org.oxycblt.auxio.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * A wrapper around [StateFlow] exposing a one-time consumable event.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Event<T> {
    /** The inner [StateFlow] contained by the [Event]. */
    val flow: StateFlow<T?>
    /**
     * Consume whatever value is currently contained by this instance.
     *
     * @return A value placed into this instance prior, or null if there isn't any.
     */
    fun consume(): T?
}

/**
 * A wrapper around [StateFlow] exposing a one-time consumable event that can be modified by it's
 * owner.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MutableEvent<T> : Event<T> {
    override val flow = MutableStateFlow<T?>(null)
    override fun consume() = flow.value?.also { flow.value = null }

    /**
     * Place a new value into this instance, replacing any prior value.
     *
     * @param v The value to update with.
     */
    fun put(v: T) {
        flow.value = v
    }
}

/**
 * Collect a [StateFlow] into [block] in a lifecycle-aware manner *eventually.* Due to co-routine
 * launching, the initializing call will occur ~100ms after draw time. If this is not desirable, use
 * [collectImmediately].
 *
 * @param stateFlow The [StateFlow] to collect.
 * @param block The code to run when the [StateFlow] updates.
 */
fun <T> Fragment.collect(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    launch { stateFlow.collect(block) }
}

/**
 * Collect a [StateFlow] into a [block] in a lifecycle-aware manner *immediately.* This will
 * immediately run an initializing call to ensure the UI is set up before draw-time. Note that this
 * will result in two initializing calls.
 *
 * @param stateFlow The [StateFlow] to collect.
 * @param block The code to run when the [StateFlow] updates.
 */
fun <T> Fragment.collectImmediately(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    block(stateFlow.value)
    launch { stateFlow.collect(block) }
}

/**
 * Like [collectImmediately], but with two [StateFlow] instances that are collected with the same
 * block.
 *
 * @param a The first [StateFlow] to collect.
 * @param b The second [StateFlow] to collect.
 * @param block The code to run when either [StateFlow] updates.
 */
fun <T1, T2> Fragment.collectImmediately(
    a: StateFlow<T1>,
    b: StateFlow<T2>,
    block: (T1, T2) -> Unit
) {
    block(a.value, b.value)
    // We can combine flows, but only if we transform them into one flow output.
    // Thus, we have to first combine the two flow values into a Pair, and then
    // decompose it when we collect the values.
    val combine = a.combine(b) { first, second -> Pair(first, second) }
    launch { combine.collect { block(it.first, it.second) } }
}

/**
 * Like [collectImmediately], but with three [StateFlow] instances that are collected with the same
 * block.
 *
 * @param a The first [StateFlow] to collect.
 * @param b The second [StateFlow] to collect.
 * @param c The third [StateFlow] to collect.
 * @param block The code to run when any of the [StateFlow]s update.
 */
fun <T1, T2, T3> Fragment.collectImmediately(
    a: StateFlow<T1>,
    b: StateFlow<T2>,
    c: StateFlow<T3>,
    block: (T1, T2, T3) -> Unit
) {
    block(a.value, b.value, c.value)
    val combine = combine(a, b, c) { a1, b2, c3 -> Triple(a1, b2, c3) }
    launch { combine.collect { block(it.first, it.second, it.third) } }
}

/**
 * Launch a [Fragment] co-routine whenever the [Lifecycle] hits the given [Lifecycle.State]. This
 * should always been used when launching [Fragment] co-routines was it will not result in
 * unexpected behavior.
 *
 * @param state The [Lifecycle.State] to launch the co-routine in.
 * @param block The block to run in the co-routine.
 * @see repeatOnLifecycle
 */
private fun Fragment.launch(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch { viewLifecycleOwner.repeatOnLifecycle(state, block) }
}

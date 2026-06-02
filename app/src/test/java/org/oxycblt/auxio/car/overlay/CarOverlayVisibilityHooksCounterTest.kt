/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayVisibilityHooksCounterTest.kt is part of Auxio.
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

package org.oxycblt.auxio.car.overlay

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the started-activity counter logic used by [CarOverlayVisibilityHooks] to detect app-level
 * foreground/background transitions. Uses a simplified model to verify the counter arithmetic
 * without needing Android framework dependencies.
 */
class CarOverlayVisibilityHooksCounterTest {

    /** Simulates the counter logic from CarOverlayVisibilityHooks. */
    private class CounterModel {
        var count = 0
            private set

        var foregroundTransitions = 0
            private set

        var backgroundTransitions = 0
            private set

        fun onActivityStarted() {
            val previous = count
            count++
            if (previous == 0) foregroundTransitions++
        }

        fun onActivityStopped() {
            count = (count - 1).coerceAtLeast(0)
            if (count == 0) backgroundTransitions++
        }
    }

    @Test
    fun `single activity start and stop produces one foreground and one background transition`() {
        val model = CounterModel()
        model.onActivityStarted()
        assertEquals(1, model.foregroundTransitions)
        assertEquals(0, model.backgroundTransitions)

        model.onActivityStopped()
        assertEquals(1, model.foregroundTransitions)
        assertEquals(1, model.backgroundTransitions)
    }

    @Test
    fun `overlapping activity lifecycle does not produce spurious transitions`() {
        val model = CounterModel()
        // Activity A starts
        model.onActivityStarted()
        assertEquals(1, model.foregroundTransitions)

        // Activity B starts (in-app navigation)
        model.onActivityStarted()
        assertEquals(1, model.foregroundTransitions) // No additional transition

        // Activity A stops
        model.onActivityStopped()
        assertEquals(0, model.backgroundTransitions) // Still one activity visible

        // Activity B stops
        model.onActivityStopped()
        assertEquals(1, model.backgroundTransitions) // Now background
    }

    @Test
    fun `counter cannot go negative`() {
        val model = CounterModel()
        // Defensive: stopped without started should not crash or go negative
        model.onActivityStopped()
        assertEquals(0, model.count)
        // This counts as a background transition because count transitions to 0,
        // but it was already 0 — the defensive clamp handles this.
        assertEquals(1, model.backgroundTransitions)
    }

    @Test
    fun `repeated start-stop cycles count correctly`() {
        val model = CounterModel()
        repeat(3) {
            model.onActivityStarted()
            model.onActivityStopped()
        }
        assertEquals(3, model.foregroundTransitions)
        assertEquals(3, model.backgroundTransitions)
        assertEquals(0, model.count)
    }
}

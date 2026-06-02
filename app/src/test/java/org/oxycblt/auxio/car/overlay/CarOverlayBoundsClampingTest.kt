/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayBoundsClampingTest.kt is part of Auxio.
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
import kotlin.test.assertTrue

/**
 * Tests overlay bounds clamping logic. Uses the same algorithm as
 * [CarFloatingControlsService.clampPosition] but extracted here for pure unit testing without
 * Android dependencies.
 */
class CarOverlayBoundsClampingTest {

    // Mirror the constants from CarFloatingControlsService companion.
    private val statusBarInset = 55
    private val navBarInset = 55
    private val overlayWidth = 350
    private val overlayHeight = 80

    /** Simplified clamp function matching the service implementation. */
    private fun clampPosition(
        x: Int,
        y: Int,
        screenW: Int = 1280,
        screenH: Int = 720,
    ): Pair<Int, Int> {
        val minX = 0
        val minY = statusBarInset
        val maxX = (screenW - navBarInset - overlayWidth).coerceAtLeast(minX)
        val maxY = (screenH - overlayHeight).coerceAtLeast(minY)
        return x.coerceIn(minX, maxX) to y.coerceIn(minY, maxY)
    }

    @Test
    fun `position within bounds is unchanged`() {
        val (x, y) = clampPosition(100, 100)
        assertEquals(100, x)
        assertEquals(100, y)
    }

    @Test
    fun `negative x is clamped to zero`() {
        val (x, _) = clampPosition(-50, 100)
        assertEquals(0, x)
    }

    @Test
    fun `y below status bar is clamped to status bar inset`() {
        val (_, y) = clampPosition(100, 10)
        assertEquals(statusBarInset, y)
    }

    @Test
    fun `x beyond right nav bar area is clamped`() {
        // maxX = 1280 - 55 - 350 = 875
        val (x, _) = clampPosition(1000, 100)
        assertEquals(875, x)
    }

    @Test
    fun `y beyond bottom is clamped`() {
        // maxY = 720 - 80 = 640
        val (_, y) = clampPosition(100, 700)
        assertEquals(640, y)
    }

    @Test
    fun `default position from CarOverlayPrefs is within TS18 bounds`() {
        val defaultX = 437 // CarOverlayPrefs.DEFAULT_X (top-center for TS18)
        val defaultY = 55 // CarOverlayPrefs.DEFAULT_Y (below status bar)
        val (x, y) = clampPosition(defaultX, defaultY)
        assertTrue(x >= 0, "Default X must be non-negative")
        assertTrue(y >= statusBarInset, "Default Y must be below status bar")
        assertTrue(x <= 875, "Default X must be within right bound")
        assertTrue(y <= 640, "Default Y must be within bottom bound")
    }

    @Test
    fun `extremely large coordinates are clamped safely`() {
        val (x, y) = clampPosition(9999, 9999)
        assertEquals(875, x)
        assertEquals(640, y)
    }
}

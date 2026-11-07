/*
 * Copyright (c) 2026 Auxio Project
 * UsbVolumeStepsTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.service

import org.junit.Assert.assertEquals
import org.junit.Test

class UsbVolumeStepsTest {
    @Test
    fun `divisions is 100 divided by the step percent`() {
        assertEquals(100, UsbVolumeSteps.divisions(1))
        assertEquals(50, UsbVolumeSteps.divisions(2))
        assertEquals(20, UsbVolumeSteps.divisions(5))
        assertEquals(10, UsbVolumeSteps.divisions(10))
    }

    @Test
    fun `divisions never divides by zero for a non-positive percent`() {
        assertEquals(100, UsbVolumeSteps.divisions(0))
        assertEquals(100, UsbVolumeSteps.divisions(-5))
    }

    @Test
    fun `stepToDb moves by the expected fraction of the range`() {
        val range = -15360 to 0
        // 1% steps (divisions = 100): one step should move 1% of the 15360-wide range.
        assertEquals(-15360, UsbVolumeSteps.stepToDb(0, range, 100))
        assertEquals(-15207, UsbVolumeSteps.stepToDb(1, range, 100))
        assertEquals(0, UsbVolumeSteps.stepToDb(100, range, 100))
        // 10% steps (divisions = 10): one step should move 10% of the range.
        assertEquals(-13824, UsbVolumeSteps.stepToDb(1, range, 10))
        assertEquals(0, UsbVolumeSteps.stepToDb(10, range, 10))
    }

    @Test
    fun `regression -- the exact values observed drifting on real hardware now round-trip`() {
        // Captured from a real FiiO-class DAC's logcat: volume key presses at 1% step size kept
        // silently dropping from step 31 (-10599) to step 30 (-10752) with no corresponding key
        // press, because dbToStep used to floor-divide and lose a step when round-tripping a
        // value stepToDb itself had just floor-divided to produce. See MediaSessionHolder's
        // UsbVolumeProvider.applyRange() doc comment for the full story.
        val range = -15360 to 0
        val divisions = 100 // 1% step size

        assertEquals(-10599, UsbVolumeSteps.stepToDb(31, range, divisions))
        assertEquals(31, UsbVolumeSteps.dbToStep(-10599, range, divisions))

        assertEquals(-10752, UsbVolumeSteps.stepToDb(30, range, divisions))
        assertEquals(30, UsbVolumeSteps.dbToStep(-10752, range, divisions))
    }

    @Test
    fun `every step round-trips exactly through stepToDb then dbToStep`() {
        val ranges = listOf(-15360 to 0, -2000 to 500, -256 to 256, -100 to 0)
        val percents = 1..10

        for (range in ranges) {
            for (percent in percents) {
                val divisions = UsbVolumeSteps.divisions(percent)
                for (step in 0..divisions) {
                    val db = UsbVolumeSteps.stepToDb(step, range, divisions)
                    val recoveredStep = UsbVolumeSteps.dbToStep(db, range, divisions)
                    assertEquals(
                        "range=$range percent=$percent step=$step db=$db did not round-trip",
                        step,
                        recoveredStep,
                    )
                }
            }
        }
    }

    @Test
    fun `dbToStep clamps out-of-range dB values instead of over- or under-shooting`() {
        val range = -15360 to 0
        assertEquals(0, UsbVolumeSteps.dbToStep(-99999, range, 20))
        assertEquals(20, UsbVolumeSteps.dbToStep(99999, range, 20))
    }

    @Test
    fun `dbToStep returns zero for a degenerate zero-width range`() {
        assertEquals(0, UsbVolumeSteps.dbToStep(-500, -500 to -500, 20))
    }
}

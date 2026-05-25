/*
 * Copyright (c) 2024 Auxio Project
 * WidgetTimelineTest.kt is part of Auxio.
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

package org.oxycblt.auxio.widgets

import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetTimelineTest {
    @Test
    fun `clock formatting supports minute and hour forms`() {
        assertEquals("0:59", WidgetTimeline.formatClock(59_000L))
        assertEquals("1:02", WidgetTimeline.formatClock(62_000L))
        assertEquals("1:00:00", WidgetTimeline.formatClock(3_600_000L))
    }

    @Test
    fun `progress is clamped and converted to seconds`() {
        assertEquals(5 to 0, WidgetTimeline.clampProgressSeconds(-1_000L, 5_000L))
        assertEquals(5 to 5, WidgetTimeline.clampProgressSeconds(8_000L, 5_000L))
        assertEquals(0 to 0, WidgetTimeline.clampProgressSeconds(100L, -1L))
    }

    @Test
    fun `state exposes safe render fields`() {
        val state = WidgetTimeline.state(1_500L, 5_000L)
        assertEquals("0:01", state.currentText)
        assertEquals("0:05", state.durationText)
        assertEquals(5, state.maxSeconds)
        assertEquals(1, state.progressSeconds)
        assertEquals(WidgetTimelineState("0:00", "0:00", 1, 0), WidgetTimeline.NO_SESSION)
    }

    @Test
    fun `state clamps current time label to the same progress used by the bar`() {
        val state = WidgetTimeline.state(positionMs = 8_000L, durationMs = 5_000L)
        assertEquals("0:05", state.currentText)
        assertEquals(5, state.progressSeconds)
    }
}

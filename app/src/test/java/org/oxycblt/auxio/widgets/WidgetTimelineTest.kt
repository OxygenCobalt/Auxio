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

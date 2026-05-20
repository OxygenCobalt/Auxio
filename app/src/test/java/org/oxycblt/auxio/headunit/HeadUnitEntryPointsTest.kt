package org.oxycblt.auxio.headunit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeadUnitEntryPointsTest {
    @Test
    fun `destinationForAction maps known actions`() {
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.NOW_PLAYING,
            HeadUnitEntryPoints.destinationForAction(HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING),
        )
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.QUEUE,
            HeadUnitEntryPoints.destinationForAction(HeadUnitEntryPoints.ACTION_OPEN_QUEUE),
        )
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.HEAD_UNIT_SETTINGS,
            HeadUnitEntryPoints.destinationForAction(HeadUnitEntryPoints.ACTION_OPEN_HEAD_UNIT_SETTINGS),
        )
    }

    @Test
    fun `destinationForAction returns null for unknown action`() {
        assertNull(HeadUnitEntryPoints.destinationForAction("org.oxycblt.auxio.action.UNKNOWN"))
    }
}

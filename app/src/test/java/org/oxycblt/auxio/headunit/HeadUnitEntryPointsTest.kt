package org.oxycblt.auxio.headunit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeadUnitEntryPointsTest {
    @Test
    fun destinationForAction_mapsKnownActions() {
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.NOW_PLAYING,
            HeadUnitEntryPoints.destinationForAction(HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING),
        )
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.QUEUE,
            HeadUnitEntryPoints.destinationForAction(HeadUnitEntryPoints.ACTION_OPEN_QUEUE),
        )
    }

    @Test
    fun destinationForAction_ignoresUnknownAction() {
        assertNull(HeadUnitEntryPoints.destinationForAction("nope"))
        assertNull(HeadUnitEntryPoints.destinationForAction(null))
    }
}

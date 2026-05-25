/*
 * Copyright (c) 2026 Auxio Project
 * HeadUnitEntryPointsTest.kt is part of Auxio.
 */
package org.oxycblt.auxio.headunit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    }

    @Test
    fun `destinationForAction returns null for unknown action`() {
        assertNull(HeadUnitEntryPoints.destinationForAction("org.oxycblt.auxio.action.UNKNOWN"))
        assertNull(HeadUnitEntryPoints.destinationForAction(null))
    }

    @Test
    fun `safeDestinationForAction falls back to now playing for unknown`() {
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.NOW_PLAYING,
            HeadUnitEntryPoints.safeDestinationForAction("unknown"),
        )
    }

    @Test
    fun `publishedDynamicShortcutIds uses priority order and cap`() {
        assertEquals(
            listOf(
                "shortcut_now_playing",
                "shortcut_shuffle",
                "shortcut_queue",
                "shortcut_recently_added",
                "shortcut_playlists"),
            HeadUnitEntryPoints.publishedDynamicShortcutIds(5),
        )
    }

    @Test
    fun `parity coverage for public actions is complete`() {
        assertTrue(HeadUnitEntryPoints.isParityActionCoverageComplete())
    }
}

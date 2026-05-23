/*
 * Copyright (c) 2026 Auxio Project
 * HeadUnitEntryPointsTest.kt is part of Auxio.
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
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.HEAD_UNIT_SETTINGS,
            HeadUnitEntryPoints.destinationForAction(HeadUnitEntryPoints.ACTION_OPEN_HEAD_UNIT_SETTINGS),
        )
        assertEquals(
            HeadUnitEntryPoints.EntryDestination.PLAYLISTS,
            HeadUnitEntryPoints.destinationForAction(HeadUnitEntryPoints.ACTION_OPEN_PLAYLISTS),
        )
    }

    @Test
    fun `destinationForAction returns null for unknown action`() {
        assertNull(HeadUnitEntryPoints.destinationForAction("org.oxycblt.auxio.action.UNKNOWN"))
        assertNull(HeadUnitEntryPoints.destinationForAction(null))
    }

    @Test
    fun `publishedDynamicShortcutIds uses priority order and cap`() {
        assertEquals(
            listOf(
                "shortcut_now_playing",
                "shortcut_shuffle",
                "shortcut_queue",
                "shortcut_recently_added",
                "shortcut_playlists",
            ),
            HeadUnitEntryPoints.publishedDynamicShortcutIds(5),
        )
        assertEquals(
            listOf("shortcut_now_playing", "shortcut_shuffle"),
            HeadUnitEntryPoints.publishedDynamicShortcutIds(2),
        )
    }

    @Test
    fun `publishedDynamicShortcutIds returns empty for non-positive limits`() {
        assertTrue(HeadUnitEntryPoints.publishedDynamicShortcutIds(0).isEmpty())
        assertTrue(HeadUnitEntryPoints.publishedDynamicShortcutIds(-1).isEmpty())
    }
}

/*
 * Copyright (c) 2024 Auxio Project
 * HeadUnitDashboardPolicyTest.kt is part of Auxio.
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HeadUnitDashboardPolicyTest {
    @Test
    fun `dashboard parity baseline remains aligned`() {
        assertTrue(HeadUnitDashboardPolicy.isParityAligned())
    }

    @Test
    fun `dashboard parity fails when a required action is not public`() {
        assertFalse(
            HeadUnitDashboardPolicy.isParityAligned(
                publicActions =
                    HeadUnitEntryPoints.ALL_PUBLIC_ACTIONS - HeadUnitEntryPoints.ACTION_OPEN_QUEUE))
    }

    @Test
    fun `dashboard parity fails when a required action is not routable`() {
        assertFalse(
            HeadUnitDashboardPolicy.isParityAligned(
                routeForAction = { action ->
                    if (action == HeadUnitEntryPoints.ACTION_OPEN_QUEUE) {
                        null
                    } else {
                        HeadUnitRoutePolicy.routeForAction(action)
                    }
                }))
    }

    @Test
    fun `dashboard parity fails when a required route has no destination`() {
        assertFalse(
            HeadUnitDashboardPolicy.isParityAligned(
                entryDestinationForRoute = { route ->
                    if (route == HeadUnitRoute.QUEUE) {
                        null
                    } else {
                        HeadUnitRoutePolicy.entryDestinationForRoute(route)
                    }
                }))
    }

    @Test
    fun `entries include stable priority order`() {
        val entries =
            HeadUnitDashboardPolicy.entries(
                HeadUnitDashboardState(
                    hasLibraryContent = true,
                    hasFavourites = true,
                    isIndexing = false,
                ))
        assertEquals(QuickPickAction.NOW_PLAYING, entries.first().action)
        assertEquals(QuickPickAction.QUEUE, entries[1].action)
        assertEquals(QuickPickAction.SHUFFLE_ALL, entries[2].action)
    }

    @Test
    fun `entries disable browse actions when library empty`() {
        val entries =
            HeadUnitDashboardPolicy.entries(
                    HeadUnitDashboardState(
                        hasLibraryContent = false,
                        hasFavourites = false,
                        isIndexing = false,
                    ))
                .associateBy { it.action }
        assertFalse(entries.getValue(QuickPickAction.ALBUMS).enabled)
        assertFalse(entries.getValue(QuickPickAction.ARTISTS).enabled)
        assertFalse(entries.getValue(QuickPickAction.PLAYLISTS).enabled)
        assertTrue(entries.getValue(QuickPickAction.NOW_PLAYING).enabled)
    }

    @Test
    fun `settings entry disabled during indexing`() {
        val entries =
            HeadUnitDashboardPolicy.entries(
                    HeadUnitDashboardState(
                        hasLibraryContent = true,
                        hasFavourites = true,
                        isIndexing = true,
                    ))
                .associateBy { it.action }
        assertFalse(entries.getValue(QuickPickAction.HEAD_UNIT_SETTINGS).enabled)
        assertFalse(entries.getValue(QuickPickAction.ALBUMS).enabled)
        assertFalse(entries.getValue(QuickPickAction.ARTISTS).enabled)
        assertFalse(entries.getValue(QuickPickAction.PLAYLISTS).enabled)
        assertEquals(
            HeadUnitRoute.HEAD_UNIT_SETTINGS,
            entries.getValue(QuickPickAction.HEAD_UNIT_SETTINGS).route,
        )
    }

    @Test
    fun `dashboard policy does not expose folders or decades as dashboard routes`() {
        val actions =
            HeadUnitDashboardPolicy.entries(
                    HeadUnitDashboardState(
                        hasLibraryContent = true,
                        hasFavourites = true,
                        isIndexing = false,
                    ))
                .map { it.action }
        assertFalse(actions.contains(QuickPickAction.FOLDERS))
        assertFalse(actions.contains(QuickPickAction.DECADES))
    }

    @Test
    fun `favourites entry hidden when no favourites and present when available`() {
        val noFavs =
            HeadUnitDashboardPolicy.entries(
                    HeadUnitDashboardState(
                        hasLibraryContent = true,
                        hasFavourites = false,
                        isIndexing = false,
                    ))
                .map { it.action }
        assertFalse(noFavs.contains(QuickPickAction.FAVOURITES))
        assertEquals(QuickPickAction.HEAD_UNIT_SETTINGS, noFavs.last())

        val withFavsList =
            HeadUnitDashboardPolicy.entries(
                HeadUnitDashboardState(
                    hasLibraryContent = true,
                    hasFavourites = true,
                    isIndexing = false,
                ))
        val withFavs = withFavsList.associateBy { it.action }
        assertTrue(withFavs.containsKey(QuickPickAction.FAVOURITES))
        assertTrue(withFavs.getValue(QuickPickAction.FAVOURITES).enabled)
        assertEquals(QuickPickAction.FAVOURITES, withFavsList[withFavsList.size - 2].action)
        assertEquals(QuickPickAction.HEAD_UNIT_SETTINGS, withFavsList.last().action)
    }
}

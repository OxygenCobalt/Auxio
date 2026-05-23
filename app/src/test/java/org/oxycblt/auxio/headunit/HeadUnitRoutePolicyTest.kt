package org.oxycblt.auxio.headunit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeadUnitRoutePolicyTest {
    @Test
    fun `routeForAction maps all supported head-unit actions`() {
        val expectedRoutes =
            mapOf(
                HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING to HeadUnitRoute.NOW_PLAYING,
                HeadUnitEntryPoints.ACTION_SHUFFLE_ALL to HeadUnitRoute.SHUFFLE_ALL,
                HeadUnitEntryPoints.ACTION_OPEN_QUEUE to HeadUnitRoute.QUEUE,
                HeadUnitEntryPoints.ACTION_OPEN_RECENTLY_ADDED to HeadUnitRoute.RECENTLY_ADDED,
                HeadUnitEntryPoints.ACTION_OPEN_GENRES to HeadUnitRoute.GENRES,
                HeadUnitEntryPoints.ACTION_OPEN_ARTISTS to HeadUnitRoute.ARTISTS,
                HeadUnitEntryPoints.ACTION_OPEN_ALBUMS to HeadUnitRoute.ALBUMS,
                HeadUnitEntryPoints.ACTION_OPEN_PLAYLISTS to HeadUnitRoute.PLAYLISTS,
                HeadUnitEntryPoints.ACTION_OPEN_FAVOURITES to HeadUnitRoute.FAVOURITES,
                HeadUnitEntryPoints.ACTION_OPEN_HEAD_UNIT_SETTINGS to
                    HeadUnitRoute.HEAD_UNIT_SETTINGS,
            )

        expectedRoutes.forEach { (action, expectedRoute) ->
            assertEquals(
                "Unexpected route for action $action",
                expectedRoute,
                HeadUnitRoutePolicy.routeForAction(action),
            )
        }
    }

    @Test
    fun `unknown action fails safely`() {
        assertNull(HeadUnitRoutePolicy.routeForAction("org.oxycblt.auxio.action.UNKNOWN"))
        assertNull(HeadUnitRoutePolicy.routeForAction(null))
    }

    @Test
    fun `routeForQuickPick only maps canonical head-unit routes`() {
        val expectedRoutes =
            mapOf(
                QuickPickAction.NOW_PLAYING to HeadUnitRoute.NOW_PLAYING,
                QuickPickAction.SHUFFLE_ALL to HeadUnitRoute.SHUFFLE_ALL,
                QuickPickAction.GENRES to HeadUnitRoute.GENRES,
                QuickPickAction.ARTISTS to HeadUnitRoute.ARTISTS,
                QuickPickAction.ALBUMS to HeadUnitRoute.ALBUMS,
                QuickPickAction.PLAYLISTS to HeadUnitRoute.PLAYLISTS,
                QuickPickAction.QUEUE to HeadUnitRoute.QUEUE,
                QuickPickAction.RECENTLY_ADDED to HeadUnitRoute.RECENTLY_ADDED,
                QuickPickAction.HEAD_UNIT_SETTINGS to HeadUnitRoute.HEAD_UNIT_SETTINGS,
                QuickPickAction.DECADES to null,
                QuickPickAction.FOLDERS to null,
                QuickPickAction.FAVOURITES to HeadUnitRoute.FAVOURITES,
            )

        QuickPickAction.entries.forEach { action ->
            assertEquals(
                "Unexpected route for quick pick $action",
                expectedRoutes[action],
                HeadUnitRoutePolicy.routeForQuickPick(action),
            )
        }
    }

    @Test
    fun `entryDestinationForRoute only maps entry destinations`() {
        val expectedDestinations =
            mapOf(
                HeadUnitRoute.NOW_PLAYING to HeadUnitEntryPoints.EntryDestination.NOW_PLAYING,
                HeadUnitRoute.SHUFFLE_ALL to null,
                HeadUnitRoute.QUEUE to HeadUnitEntryPoints.EntryDestination.QUEUE,
                HeadUnitRoute.RECENTLY_ADDED to
                    HeadUnitEntryPoints.EntryDestination.RECENTLY_ADDED,
                HeadUnitRoute.GENRES to HeadUnitEntryPoints.EntryDestination.GENRES,
                HeadUnitRoute.ARTISTS to HeadUnitEntryPoints.EntryDestination.ARTISTS,
                HeadUnitRoute.ALBUMS to HeadUnitEntryPoints.EntryDestination.ALBUMS,
                HeadUnitRoute.PLAYLISTS to HeadUnitEntryPoints.EntryDestination.PLAYLISTS,
                HeadUnitRoute.FAVOURITES to HeadUnitEntryPoints.EntryDestination.FAVOURITES,
                HeadUnitRoute.HEAD_UNIT_SETTINGS to
                    HeadUnitEntryPoints.EntryDestination.HEAD_UNIT_SETTINGS,
            )

        HeadUnitRoute.entries.forEach { route ->
            assertEquals(
                "Unexpected destination for route $route",
                expectedDestinations[route],
                HeadUnitRoutePolicy.entryDestinationForRoute(route),
            )
        }
    }
}

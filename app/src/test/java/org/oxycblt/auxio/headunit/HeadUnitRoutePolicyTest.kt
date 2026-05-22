package org.oxycblt.auxio.headunit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeadUnitRoutePolicyTest {
    @Test
    fun `routeForAction maps all supported head-unit actions`() {
        assertEquals(HeadUnitRoute.NOW_PLAYING, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING))
        assertEquals(HeadUnitRoute.SHUFFLE_ALL, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_SHUFFLE_ALL))
        assertEquals(HeadUnitRoute.QUEUE, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_QUEUE))
        assertEquals(HeadUnitRoute.RECENTLY_ADDED, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_RECENTLY_ADDED))
        assertEquals(HeadUnitRoute.GENRES, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_GENRES))
        assertEquals(HeadUnitRoute.ARTISTS, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_ARTISTS))
        assertEquals(HeadUnitRoute.ALBUMS, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_ALBUMS))
        assertEquals(HeadUnitRoute.FAVOURITES, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_FAVOURITES))
        assertEquals(HeadUnitRoute.HEAD_UNIT_SETTINGS, HeadUnitRoutePolicy.routeForAction(HeadUnitEntryPoints.ACTION_OPEN_HEAD_UNIT_SETTINGS))
    }

    @Test
    fun `unknown action fails safely`() {
        assertNull(HeadUnitRoutePolicy.routeForAction("org.oxycblt.auxio.action.UNKNOWN"))
        assertNull(HeadUnitRoutePolicy.routeForAction(null))
    }

    @Test
    fun `queue quick pick always maps to queue route`() {
        assertEquals(HeadUnitRoute.QUEUE, HeadUnitRoutePolicy.routeForQuickPick(QuickPickAction.QUEUE))
    }
}

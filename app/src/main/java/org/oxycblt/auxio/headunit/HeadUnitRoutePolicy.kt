package org.oxycblt.auxio.headunit

/** Canonical route model for head-unit visible actions/entry points. */
enum class HeadUnitRoute {
    NOW_PLAYING,
    SHUFFLE_ALL,
    QUEUE,
    RECENTLY_ADDED,
    GENRES,
    ARTISTS,
    ALBUMS,
    FAVOURITES,
    HEAD_UNIT_SETTINGS,
}

object HeadUnitRoutePolicy {
    private val entryDestinationByRoute =
        mapOf(
            HeadUnitRoute.NOW_PLAYING to HeadUnitEntryPoints.EntryDestination.NOW_PLAYING,
            HeadUnitRoute.QUEUE to HeadUnitEntryPoints.EntryDestination.QUEUE,
            HeadUnitRoute.RECENTLY_ADDED to HeadUnitEntryPoints.EntryDestination.RECENTLY_ADDED,
            HeadUnitRoute.GENRES to HeadUnitEntryPoints.EntryDestination.GENRES,
            HeadUnitRoute.ARTISTS to HeadUnitEntryPoints.EntryDestination.ARTISTS,
            HeadUnitRoute.ALBUMS to HeadUnitEntryPoints.EntryDestination.ALBUMS,
            HeadUnitRoute.FAVOURITES to HeadUnitEntryPoints.EntryDestination.FAVOURITES,
            HeadUnitRoute.HEAD_UNIT_SETTINGS to HeadUnitEntryPoints.EntryDestination.HEAD_UNIT_SETTINGS,
        )

    fun routeForAction(action: String?): HeadUnitRoute? =
        when (action) {
            HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING -> HeadUnitRoute.NOW_PLAYING
            HeadUnitEntryPoints.ACTION_SHUFFLE_ALL -> HeadUnitRoute.SHUFFLE_ALL
            HeadUnitEntryPoints.ACTION_OPEN_QUEUE -> HeadUnitRoute.QUEUE
            HeadUnitEntryPoints.ACTION_OPEN_RECENTLY_ADDED -> HeadUnitRoute.RECENTLY_ADDED
            HeadUnitEntryPoints.ACTION_OPEN_GENRES -> HeadUnitRoute.GENRES
            HeadUnitEntryPoints.ACTION_OPEN_ARTISTS -> HeadUnitRoute.ARTISTS
            HeadUnitEntryPoints.ACTION_OPEN_ALBUMS -> HeadUnitRoute.ALBUMS
            HeadUnitEntryPoints.ACTION_OPEN_FAVOURITES -> HeadUnitRoute.FAVOURITES
            HeadUnitEntryPoints.ACTION_OPEN_HEAD_UNIT_SETTINGS -> HeadUnitRoute.HEAD_UNIT_SETTINGS
            else -> null
        }

    fun routeForQuickPick(action: QuickPickAction): HeadUnitRoute =
        when (action) {
            QuickPickAction.NOW_PLAYING -> HeadUnitRoute.NOW_PLAYING
            QuickPickAction.SHUFFLE_ALL -> HeadUnitRoute.SHUFFLE_ALL
            QuickPickAction.GENRES -> HeadUnitRoute.GENRES
            QuickPickAction.ARTISTS -> HeadUnitRoute.ARTISTS
            QuickPickAction.ALBUMS -> HeadUnitRoute.ALBUMS
            QuickPickAction.QUEUE -> HeadUnitRoute.QUEUE
            QuickPickAction.RECENTLY_ADDED -> HeadUnitRoute.RECENTLY_ADDED
            QuickPickAction.FAVOURITES -> HeadUnitRoute.FAVOURITES
            QuickPickAction.DECADES,
            QuickPickAction.FOLDERS -> HeadUnitRoute.HEAD_UNIT_SETTINGS
        }

    fun entryDestinationForRoute(route: HeadUnitRoute): HeadUnitEntryPoints.EntryDestination? =
        entryDestinationByRoute[route]
}

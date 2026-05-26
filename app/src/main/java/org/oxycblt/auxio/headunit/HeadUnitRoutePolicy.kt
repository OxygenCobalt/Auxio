/*
 * Copyright (c) 2024 Auxio Project
 * HeadUnitRoutePolicy.kt is part of Auxio.
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

/** Canonical route model for head-unit visible actions/entry points. */
enum class HeadUnitRoute {
    NOW_PLAYING,
    SHUFFLE_ALL,
    QUEUE,
    RECENTLY_ADDED,
    GENRES,
    ARTISTS,
    ALBUMS,
    PLAYLISTS,
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
            HeadUnitRoute.PLAYLISTS to HeadUnitEntryPoints.EntryDestination.PLAYLISTS,
            HeadUnitRoute.FAVOURITES to HeadUnitEntryPoints.EntryDestination.FAVOURITES,
            HeadUnitRoute.HEAD_UNIT_SETTINGS to
                HeadUnitEntryPoints.EntryDestination.HEAD_UNIT_SETTINGS,
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
            HeadUnitEntryPoints.ACTION_OPEN_PLAYLISTS -> HeadUnitRoute.PLAYLISTS
            HeadUnitEntryPoints.ACTION_OPEN_FAVOURITES -> HeadUnitRoute.FAVOURITES
            HeadUnitEntryPoints.ACTION_OPEN_HEAD_UNIT_SETTINGS -> HeadUnitRoute.HEAD_UNIT_SETTINGS
            else -> null
        }

    fun routeForQuickPick(action: QuickPickAction): HeadUnitRoute? =
        when (action) {
            QuickPickAction.NOW_PLAYING -> HeadUnitRoute.NOW_PLAYING
            QuickPickAction.SHUFFLE_ALL -> HeadUnitRoute.SHUFFLE_ALL
            QuickPickAction.GENRES -> HeadUnitRoute.GENRES
            QuickPickAction.ARTISTS -> HeadUnitRoute.ARTISTS
            QuickPickAction.ALBUMS -> HeadUnitRoute.ALBUMS
            QuickPickAction.PLAYLISTS -> HeadUnitRoute.PLAYLISTS
            QuickPickAction.QUEUE -> HeadUnitRoute.QUEUE
            QuickPickAction.RECENTLY_ADDED -> HeadUnitRoute.RECENTLY_ADDED
            QuickPickAction.FAVOURITES -> HeadUnitRoute.FAVOURITES
            QuickPickAction.HEAD_UNIT_SETTINGS -> HeadUnitRoute.HEAD_UNIT_SETTINGS
            QuickPickAction.DECADES,
            QuickPickAction.FOLDERS -> null
        }

    fun entryDestinationForRoute(route: HeadUnitRoute): HeadUnitEntryPoints.EntryDestination? =
        entryDestinationByRoute[route]

    /**
     * Returns true if the given route is an immediate playback action (e.g. shuffle all) that does
     * not navigate to an [HeadUnitEntryPoints.EntryDestination] but is still a valid head-unit
     * entry point handled by [org.oxycblt.auxio.MainActivity.startIntentAction].
     */
    fun isImmediateActionRoute(route: HeadUnitRoute): Boolean =
        route == HeadUnitRoute.SHUFFLE_ALL
}

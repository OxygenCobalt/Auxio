/*
 * Copyright (c) 2026 Auxio Project
 * HeadUnitDashboardPolicy.kt is part of Auxio.
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

import org.oxycblt.auxio.R

data class HeadUnitDashboardState(
    val hasLibraryContent: Boolean,
    val hasFavourites: Boolean,
    val isIndexing: Boolean,
)

data class HeadUnitDashboardEntry(
    val action: QuickPickAction,
    val route: HeadUnitRoute?,
    val labelRes: Int,
    val iconRes: Int,
    val enabled: Boolean,
)

object HeadUnitDashboardPolicy {
    fun entries(state: HeadUnitDashboardState): List<HeadUnitDashboardEntry> =
        buildList {
            add(entry(QuickPickAction.NOW_PLAYING, R.string.lbl_playback, R.drawable.ic_play_24, true))
            add(entry(QuickPickAction.QUEUE, R.string.lbl_queue, R.drawable.ic_queue_add_24, true))
            add(
                entry(
                    QuickPickAction.SHUFFLE_ALL,
                    R.string.lbl_shuffle,
                    R.drawable.ic_shortcut_shuffle_24,
                    state.hasLibraryContent,
                )
            )
            add(
                entry(
                    QuickPickAction.RECENTLY_ADDED,
                    R.string.lbl_recently_added,
                    R.drawable.ic_time_24,
                    state.hasLibraryContent,
                )
            )
            add(
                entry(
                    QuickPickAction.ARTISTS,
                    R.string.lbl_artists,
                    R.drawable.ic_artist_24,
                    state.hasLibraryContent,
                )
            )
            add(
                entry(
                    QuickPickAction.ALBUMS,
                    R.string.lbl_albums,
                    R.drawable.ic_album_24,
                    state.hasLibraryContent,
                )
            )
            add(
                entry(
                    QuickPickAction.GENRES,
                    R.string.lbl_genres,
                    R.drawable.ic_genre_24,
                    state.hasLibraryContent,
                )
            )
            add(
                entry(
                    QuickPickAction.PLAYLISTS,
                    R.string.lbl_playlists,
                    R.drawable.ic_playlist_24,
                    state.hasLibraryContent,
                )
            )
            if (state.hasFavourites) {
                add(
                    entry(
                        QuickPickAction.FAVOURITES,
                        R.string.lbl_favourites,
                        R.drawable.ic_playlist_24,
                        true,
                    )
                )
            }
            add(
                entry(
                    QuickPickAction.HEAD_UNIT_SETTINGS,
                    R.string.set_head_unit,
                    R.drawable.ic_more_24,
                    !state.isIndexing,
                )
            )
        }

    private fun entry(
        action: QuickPickAction,
        labelRes: Int,
        iconRes: Int,
        enabled: Boolean,
    ) =
        HeadUnitDashboardEntry(
            action,
            HeadUnitRoutePolicy.routeForQuickPick(action),
            labelRes,
            iconRes,
            enabled,
        )
}

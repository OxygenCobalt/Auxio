package org.oxycblt.auxio.headunit

import org.oxycblt.auxio.R

data class HeadUnitDashboardState(
    val hasLibraryContent: Boolean,
    val hasQueue: Boolean,
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
        listOf(
            entry(QuickPickAction.NOW_PLAYING, R.string.lbl_playback, R.drawable.ic_play_24, true),
            entry(QuickPickAction.QUEUE, R.string.lbl_queue, R.drawable.ic_queue_add_24, true),
            entry(QuickPickAction.SHUFFLE_ALL, R.string.lbl_shuffle, R.drawable.ic_shortcut_shuffle_24, state.hasLibraryContent),
            entry(QuickPickAction.RECENTLY_ADDED, R.string.lbl_recently_added, R.drawable.ic_time_24, state.hasLibraryContent),
            entry(QuickPickAction.ARTISTS, R.string.lbl_artists, R.drawable.ic_artist_24, state.hasLibraryContent),
            entry(QuickPickAction.ALBUMS, R.string.lbl_albums, R.drawable.ic_album_24, state.hasLibraryContent),
            entry(QuickPickAction.GENRES, R.string.lbl_genres, R.drawable.ic_genre_24, state.hasLibraryContent),
            entry(QuickPickAction.PLAYLISTS, R.string.lbl_playlists, R.drawable.ic_playlist_24, state.hasLibraryContent),
            entry(QuickPickAction.FAVOURITES, R.string.lbl_favourites, R.drawable.ic_playlist_24, state.hasFavourites),
        ) + listOf(
            HeadUnitDashboardEntry(
                action = QuickPickAction.HEAD_UNIT_SETTINGS,
                route = HeadUnitRoute.HEAD_UNIT_SETTINGS,
                labelRes = R.string.set_head_unit,
                iconRes = R.drawable.ic_more_24,
                enabled = !state.isIndexing,
            )
        )

    private fun entry(
        action: QuickPickAction,
        labelRes: Int,
        iconRes: Int,
        enabled: Boolean,
    ) = HeadUnitDashboardEntry(action, HeadUnitRoutePolicy.routeForQuickPick(action), labelRes, iconRes, enabled)
}

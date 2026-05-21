/*
 * Copyright (c) 2026 Auxio Project
 * HeadUnitQuickAccess.kt is part of Auxio.
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

/** Well-known name used to identify the persisted Favourites playlist. */
const val FAVOURITES_PLAYLIST_NAME = "Favourites"

enum class QuickPickAction {
    NOW_PLAYING,
    SHUFFLE_ALL,
    GENRES,
    ARTISTS,
    ALBUMS,
    QUEUE,
    RECENTLY_ADDED,
    DECADES,
    FOLDERS,
    FAVOURITES,
}

data class QuickPickItem(val action: QuickPickAction, val enabled: Boolean)

data class MetadataChipState(
    val genres: Boolean,
    val decades: Boolean,
    val folders: Boolean,
    val recentlyAdded: Boolean,
    val favourites: Boolean,
)

object HeadUnitQuickAccess {
    fun quickPicks(
        hasLibraryContent: Boolean,
        hasFolderSupport: Boolean,
        hasFavouritesSupport: Boolean,
        hasYearMetadata: Boolean,
    ): List<QuickPickItem> =
        listOf(
            QuickPickItem(QuickPickAction.NOW_PLAYING, true),
            QuickPickItem(QuickPickAction.SHUFFLE_ALL, hasLibraryContent),
            QuickPickItem(QuickPickAction.GENRES, hasLibraryContent),
            QuickPickItem(QuickPickAction.ARTISTS, hasLibraryContent),
            QuickPickItem(QuickPickAction.ALBUMS, hasLibraryContent),
            QuickPickItem(QuickPickAction.QUEUE, true),
            QuickPickItem(QuickPickAction.RECENTLY_ADDED, hasLibraryContent),
            QuickPickItem(QuickPickAction.DECADES, hasLibraryContent && hasYearMetadata),
            QuickPickItem(QuickPickAction.FOLDERS, hasFolderSupport),
            QuickPickItem(QuickPickAction.FAVOURITES, hasFavouritesSupport),
        )

    fun metadataChipState(
        genreCount: Int,
        decadeCount: Int,
        hasRecent: Boolean,
        hasFolders: Boolean,
        hasFavourites: Boolean,
    ): MetadataChipState =
        MetadataChipState(
            genres = genreCount > 0,
            decades = decadeCount > 0,
            folders = hasFolders,
            recentlyAdded = hasRecent,
            favourites = hasFavourites,
        )

    fun deriveDecades(years: List<Int>): List<Int> =
        years.filter { it in 1900..2099 }.map { (it / 10) * 10 }.distinct().sorted()
}
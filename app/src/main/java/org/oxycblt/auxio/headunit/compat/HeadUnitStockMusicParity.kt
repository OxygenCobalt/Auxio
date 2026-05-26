/*
 * Copyright (c) 2024 Auxio Project
 * HeadUnitStockMusicParity.kt is part of Auxio.
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

package org.oxycblt.auxio.headunit.compat

import org.oxycblt.auxio.headunit.HeadUnitEntryPoints

enum class StockMusicExpectation {
    PLAY_PAUSE,
    NEXT,
    PREVIOUS,
    QUEUE,
    NOW_PLAYING,
    SHUFFLE_ALL,
    FAVOURITES,
    RECENTLY_ADDED,
    LIBRARY_CATEGORIES,
    METADATA_PUBLICATION,
    ARTWORK_PUBLICATION,
    AUDIO_FOCUS,
}

object HeadUnitStockMusicParity {
    fun requiredEntryActions(): Set<String> =
        setOf(
            HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING,
            HeadUnitEntryPoints.ACTION_OPEN_QUEUE,
            HeadUnitEntryPoints.ACTION_SHUFFLE_ALL,
            HeadUnitEntryPoints.ACTION_OPEN_FAVOURITES,
            HeadUnitEntryPoints.ACTION_OPEN_RECENTLY_ADDED,
        )

    fun hasRequiredActionCoverage(actions: Set<String>): Boolean =
        requiredEntryActions().all { it in actions }

    val parityMap: Map<StockMusicExpectation, String> =
        mapOf(
            StockMusicExpectation.PLAY_PAUSE to "media_session_transport_controls",
            StockMusicExpectation.NEXT to "media_session_next",
            StockMusicExpectation.PREVIOUS to "media_session_previous",
            StockMusicExpectation.QUEUE to HeadUnitEntryPoints.ACTION_OPEN_QUEUE,
            StockMusicExpectation.NOW_PLAYING to HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING,
            StockMusicExpectation.SHUFFLE_ALL to HeadUnitEntryPoints.ACTION_SHUFFLE_ALL,
            StockMusicExpectation.FAVOURITES to HeadUnitEntryPoints.ACTION_OPEN_FAVOURITES,
            StockMusicExpectation.RECENTLY_ADDED to HeadUnitEntryPoints.ACTION_OPEN_RECENTLY_ADDED,
            StockMusicExpectation.LIBRARY_CATEGORIES to "genres/artists/albums/playlists",
            StockMusicExpectation.METADATA_PUBLICATION to "standard_media_metadata",
            StockMusicExpectation.ARTWORK_PUBLICATION to
                "session_notification_widget_artwork_fallback",
            StockMusicExpectation.AUDIO_FOCUS to "audio_focus_policy_safe_resume",
        )

    fun hasUnexpectedActionLeak(): Boolean =
        parityMap.values.any {
            it.contains(".action") && it !in HeadUnitEntryPoints.ALL_PUBLIC_ACTIONS
        }
}

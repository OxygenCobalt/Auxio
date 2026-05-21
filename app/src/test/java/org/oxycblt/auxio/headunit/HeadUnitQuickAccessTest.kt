/*
 * Copyright (c) 2026 Auxio Project
 * HeadUnitQuickAccessTest.kt is part of Auxio.
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

class HeadUnitQuickAccessTest {
    @Test
    fun quickPicks_emptyLibrary_disablesContentDependentShortcuts() {
        val picks =
            HeadUnitQuickAccess.quickPicks(
                    hasLibraryContent = false,
                    hasFolderSupport = true,
                    hasFavouritesSupport = false,
                    hasYearMetadata = false,
                )
                .associateBy { it.action }
        assertTrue(picks.getValue(QuickPickAction.NOW_PLAYING).enabled)
        assertFalse(picks.getValue(QuickPickAction.SHUFFLE_ALL).enabled)
        assertFalse(picks.getValue(QuickPickAction.GENRES).enabled)
        assertFalse(picks.getValue(QuickPickAction.RECENTLY_ADDED).enabled)
        assertFalse(picks.getValue(QuickPickAction.DECADES).enabled)
        assertTrue(picks.getValue(QuickPickAction.FOLDERS).enabled)
        assertFalse(picks.getValue(QuickPickAction.FAVOURITES).enabled)
    }

    @Test
    fun quickPicks_decadesRequiresYearMetadata() {
        val noYears =
            HeadUnitQuickAccess.quickPicks(true, hasFolderSupport = true, hasFavouritesSupport = false, hasYearMetadata = false)
                .associateBy { it.action }
        val withYears =
            HeadUnitQuickAccess.quickPicks(true, hasFolderSupport = true, hasFavouritesSupport = false, hasYearMetadata = true)
                .associateBy { it.action }
        assertFalse(noYears.getValue(QuickPickAction.DECADES).enabled)
        assertTrue(withYears.getValue(QuickPickAction.DECADES).enabled)
    }

    @Test
    fun quickPicks_favourites_enabledWhenSupported() {
        val picks =
            HeadUnitQuickAccess.quickPicks(
                    hasLibraryContent = true,
                    hasFolderSupport = true,
                    hasFavouritesSupport = true,
                    hasYearMetadata = false,
                )
                .associateBy { it.action }
        assertTrue(picks.getValue(QuickPickAction.FAVOURITES).enabled)
    }

    @Test
    fun quickPicks_favourites_markedDisabledWhenNoPlaylist() {
        val picks =
            HeadUnitQuickAccess.quickPicks(
                    hasLibraryContent = true,
                    hasFolderSupport = true,
                    hasFavouritesSupport = false,
                    hasYearMetadata = false,
                )
                .associateBy { it.action }
        // The FAVOURITES item is present in the list but disabled; the UI layer filters it from
        // display so it is never shown as a dead placeholder.
        assertFalse(picks.getValue(QuickPickAction.FAVOURITES).enabled)
    }

    @Test
    fun deriveDecades_returnsDistinctSortedDecades() {
        val decades = HeadUnitQuickAccess.deriveDecades(listOf(1999, 2001, 2009, 2013, 1880, 2101, 2001))
        assertEquals(listOf(1990, 2000, 2010), decades)
    }

    @Test
    fun metadataChipState_excludesForbiddenTechnicalChipsByDesign() {
        val state = HeadUnitQuickAccess.metadataChipState(genreCount = 2, decadeCount = 1, hasRecent = true, hasFolders = false, hasFavourites = false)
        assertTrue(state.genres)
        assertTrue(state.decades)
        assertTrue(state.recentlyAdded)
        assertFalse(state.folders)
        assertFalse(state.favourites)
        // MetadataChipState has exactly 5 allowed fields: genres, decades, folders, recentlyAdded,
        // favourites. Forbidden technical categories (file type, bitrate, codec, sample rate,
        // storage size) do not exist in this data class by design.
        val fields = MetadataChipState::class.java.declaredFields.map { it.name }
        assertTrue("genres field present", fields.contains("genres"))
        assertTrue("decades field present", fields.contains("decades"))
        assertTrue("folders field present", fields.contains("folders"))
        assertTrue("recentlyAdded field present", fields.contains("recentlyAdded"))
        assertTrue("favourites field present", fields.contains("favourites"))
        // Verify no forbidden categories crept in
        assertFalse("no fileType field", fields.contains("fileType"))
        assertFalse("no bitrate field", fields.contains("bitrate"))
        assertFalse("no codec field", fields.contains("codec"))
        assertFalse("no sampleRate field", fields.contains("sampleRate"))
        assertFalse("no fileSize field", fields.contains("fileSize"))
    }

    @Test
    fun metadataChipState_showsFavourites_whenHasFavouritesTrue() {
        val state = HeadUnitQuickAccess.metadataChipState(
            genreCount = 1,
            decadeCount = 0,
            hasRecent = true,
            hasFolders = true,
            hasFavourites = true,
        )
        assertTrue(state.favourites)
    }

    @Test
    fun favouritesPlaylistName_isNonEmpty() {
        assertTrue(FAVOURITES_PLAYLIST_NAME.isNotEmpty())
    }
}
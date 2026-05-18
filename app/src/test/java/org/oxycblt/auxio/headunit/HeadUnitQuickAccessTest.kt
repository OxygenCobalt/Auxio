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
    }
}

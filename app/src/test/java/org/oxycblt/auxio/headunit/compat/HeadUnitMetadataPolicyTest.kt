/*
 * Copyright (c) 2024 Auxio Project
 * HeadUnitMetadataPolicyTest.kt is part of Auxio.
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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HeadUnitMetadataPolicyTest {
    @Test
    fun fromRaw_without_title_returns_null() {
        assertNull(
            HeadUnitMetadataPolicy.fromRaw(null, "a", "b", "c", 1L, "id", "uri", null, false))
    }

    @Test
    fun fromRaw_builds_consistent_snapshot() {
        val s =
            HeadUnitMetadataPolicy.fromRaw(
                "Track", "Artist", "Album Artist", "Album", 1000L, "1", "u", "art", true)!!
        assertEquals("Track", s.displayTitle)
        assertTrue(s.displaySubtitle.contains("Artist"))
        assertEquals("Album", s.displayDescription)
    }

    @Test
    fun fromRaw_deduplicates_artist_subtitle_and_handles_blank_artwork_uri() {
        val s =
            HeadUnitMetadataPolicy.fromRaw(
                "Track", "Artist", "Artist", "", 1L, "id", "uri", "  ", false)!!
        assertEquals("Artist", s.displaySubtitle)
        assertEquals("Artist", s.displayDescription)
        assertTrue(!s.hasArtwork)
    }
}

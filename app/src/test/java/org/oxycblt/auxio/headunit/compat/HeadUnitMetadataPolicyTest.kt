package org.oxycblt.auxio.headunit.compat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HeadUnitMetadataPolicyTest {
    @Test
    fun fromRaw_without_title_returns_null() {
        assertNull(HeadUnitMetadataPolicy.fromRaw(null, "a", "b", "c", 1L, "id", "uri", null, false))
    }

    @Test
    fun fromRaw_builds_consistent_snapshot() {
        val s = HeadUnitMetadataPolicy.fromRaw("Track", "Artist", "Album Artist", "Album", 1000L, "1", "u", "art", true)!!
        assertEquals("Track", s.displayTitle)
        assertTrue(s.displaySubtitle.contains("Artist"))
        assertEquals("Album", s.displayDescription)
    }

    @Test
    fun fromRaw_deduplicates_artist_subtitle_and_handles_blank_artwork_uri() {
        val s = HeadUnitMetadataPolicy.fromRaw("Track", "Artist", "Artist", "", 1L, "id", "uri", "  ", false)!!
        assertEquals("Artist", s.displaySubtitle)
        assertEquals("Artist", s.displayDescription)
        assertTrue(!s.hasArtwork)
    }
}

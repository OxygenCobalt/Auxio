/*
 * Copyright (c) 2024 Auxio Project
 * TagParsingEdgeCasesTest.kt is part of Auxio.
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
 
package org.oxycblt.musikr.tag.parse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.metadata.Properties
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class TagParsingEdgeCasesTest {
    private val tagParser = TagParser.new()

    @Test
    fun tagParser_emptyTags() {
        // Test behavior with completely empty tag maps
        val metadata = createTestMetadata()
        val parsedTags = tagParser.parse(metadata)

        // Check that all fields have their default values
        assertNull(parsedTags.name)
        assertNull(parsedTags.sortName)
        assertNull(parsedTags.track)
        assertNull(parsedTags.disc)
        assertNull(parsedTags.subtitle)
        assertNull(parsedTags.date)
        assertNull(parsedTags.albumName)
        assertNull(parsedTags.albumSortName)
        assertTrue(parsedTags.releaseTypes.isEmpty())
        assertTrue(parsedTags.artistNames.isEmpty())
        assertTrue(parsedTags.artistSortNames.isEmpty())
        assertTrue(parsedTags.albumArtistNames.isEmpty())
        assertTrue(parsedTags.albumArtistSortNames.isEmpty())
        assertTrue(parsedTags.genreNames.isEmpty())
    }

    @Test
    fun tagParser_malformedTags() {
        // Test behavior with malformed tags
        val metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf(
                        "TRCK" to listOf("not-a-number"),
                        "TPOS" to listOf("also-not-a-number"),
                        "TYER" to listOf("invalid-year"),
                        "REPLAYGAIN_TRACK_GAIN" to listOf("not-a-float dB")))
        val parsedTags = tagParser.parse(metadata)

        // Verify all numeric fields are null when given invalid values
        assertNull(parsedTags.track)
        assertNull(parsedTags.disc)
        assertNull(parsedTags.date)
        assertNull(parsedTags.replayGainTrackAdjustment)
    }

    @Test
    fun tagParser_priorityHandling() {
        // Test that field priority is handled correctly when multiple formats have the same field
        val metadata =
            createTestMetadata(
                id3v2Tags = mapOf("TIT2" to listOf("ID3 Title"), "TALB" to listOf("ID3 Album")),
                mp4Tags = mapOf("©nam" to listOf("MP4 Title"), "©alb" to listOf("MP4 Album")),
                xiphTags = mapOf("TITLE" to listOf("Xiph Title"), "ALBUM" to listOf("Xiph Album")))

        // Individual field extraction
        assertEquals("Xiph Title", metadata.name())
        assertEquals("Xiph Album", metadata.albumName())

        // Full tag parsing
        val parsedTags = tagParser.parse(metadata)
        assertEquals("Xiph Title", parsedTags.name)
        assertEquals("Xiph Album", parsedTags.albumName)
    }

    @Test
    fun tagParser_fieldBackoffHandling() {
        // Test that when a format doesn't have a field, it falls back to the next format
        val metadata =
            createTestMetadata(
                id3v2Tags = mapOf("TIT2" to listOf("ID3 Title"), "TALB" to listOf("ID3 Album")),
                mp4Tags =
                    mapOf(
                        "©nam" to listOf("MP4 Title")
                        // No album in MP4
                        ),
                xiphTags =
                    mapOf(
                        // No title in Xiph
                        "ALBUM" to listOf("Xiph Album")))

        // Individual field extraction
        assertEquals("MP4 Title", metadata.name()) // Xiph missing, falls back to MP4
        assertEquals("Xiph Album", metadata.albumName()) // Xiph has it, no fallback needed

        // Full tag parsing
        val parsedTags = tagParser.parse(metadata)
        assertEquals("MP4 Title", parsedTags.name)
        assertEquals("Xiph Album", parsedTags.albumName)
    }

    @Test
    fun tagParser_compilationBehavior() {
        // Test the special behavior for compilation albums

        // Case 1: Compilation flag only
        var metadata = createTestMetadata(id3v2Tags = mapOf("TCMP" to listOf("1")))
        var tags = tagParser.parse(metadata)
        assertEquals(listOf("Various Artists"), tags.albumArtistNames)
        assertEquals(listOf("compilation"), tags.releaseTypes)

        // Case 2: Compilation flag with explicit album artist
        metadata =
            createTestMetadata(
                id3v2Tags = mapOf("TCMP" to listOf("1"), "TPE2" to listOf("Custom Album Artist")))
        tags = tagParser.parse(metadata)
        assertEquals(listOf("Custom Album Artist"), tags.albumArtistNames)
        assertEquals(listOf("compilation"), tags.releaseTypes)

        // Case 3: Compilation flag with explicit release type
        metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf("TCMP" to listOf("1"), "TXXX:RELEASETYPE" to listOf("soundtrack")))
        tags = tagParser.parse(metadata)
        assertEquals(listOf("Various Artists"), tags.albumArtistNames)
        assertEquals(listOf("soundtrack"), tags.releaseTypes)
    }

    @Test
    fun tagFields_replayGainEdgeCases() {
        // Test various ReplayGain edge cases

        // Non-numeric characters filtered out
        var metadata =
            createTestMetadata(
                xiphTags = mapOf("REPLAYGAIN_TRACK_GAIN" to listOf("+2.5 dB some text")))
        assertEquals(2.5f, metadata.replayGainTrackAdjustment())

        // Zero values filtered
        metadata = createTestMetadata(xiphTags = mapOf("REPLAYGAIN_ALBUM_GAIN" to listOf("0.0 dB")))
        assertNull(metadata.replayGainAlbumAdjustment())

        // R128 value with special parsing
        metadata = createTestMetadata(xiphTags = mapOf("R128_TRACK_GAIN" to listOf("-1280")))
        assertEquals(0.0f, metadata.replayGainTrackAdjustment()) // -1280/256 + 5 = 0.0

        // Completely invalid value
        metadata =
            createTestMetadata(xiphTags = mapOf("REPLAYGAIN_TRACK_GAIN" to listOf("not a number")))
        assertNull(metadata.replayGainTrackAdjustment())
    }

    @Test
    fun tagFields_id3v23DateEdgeCases() {
        // Test ID3v2.3 date edge cases

        // Just year
        var metadata = createTestMetadata(id3v2Tags = mapOf("TYER" to listOf("2018")))
        assertEquals("2018", metadata.date().toString())

        // Year and date
        metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf(
                        "TYER" to listOf("2018"), "TDAT" to listOf("0315") // March 15
                        ))
        assertEquals("2018-03-15", metadata.date().toString())

        // Year, date and time
        metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf(
                        "TYER" to listOf("2018"),
                        "TDAT" to listOf("0315"), // March 15
                        "TIME" to listOf("1422") // 14:22
                        ))
        assertEquals("2018-03-15T14:22Z", metadata.date().toString())

        // Original year
        metadata = createTestMetadata(id3v2Tags = mapOf("TORY" to listOf("1995")))
        assertEquals("1995", metadata.date().toString())

        // Invalid date components
        metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf(
                        "TYER" to listOf("2018"),
                        "TDAT" to listOf("invalid"),
                        "TIME" to listOf("invalid")))
        assertEquals("2018", metadata.date().toString())

        // Non-numeric date components
        metadata = createTestMetadata(id3v2Tags = mapOf("TYER" to listOf("year")))
        assertNull(metadata.date())
    }

    private fun createTestMetadata(
        id3v2Tags: Map<String, List<String>> = emptyMap(),
        xiphTags: Map<String, List<String>> = emptyMap(),
        mp4Tags: Map<String, List<String>> = emptyMap(),
        cover: ByteArray? = null,
        durationMs: Long = 1000,
        bitrateKbps: Int = 320,
        sampleRateHz: Int = 44100,
        mimeType: String = "audio/mpeg"
    ): Metadata {
        val properties = Properties(mimeType, durationMs, bitrateKbps, sampleRateHz)
        return Metadata(id3v2Tags, xiphTags, mp4Tags, cover, properties)
    }
}

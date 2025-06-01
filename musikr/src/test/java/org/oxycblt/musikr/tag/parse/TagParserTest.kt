/*
 * Copyright (c) 2024 Auxio Project
 * TagParserTest.kt is part of Auxio.
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
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.metadata.Properties
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class TagParserTest {
    private val tagParser = TagParser.new()

    @Test
    fun tagParser_parse_basic() {
        val metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf(
                        "TIT2" to listOf("Test Song"),
                        "TALB" to listOf("Test Album"),
                        "TPE1" to listOf("Test Artist"),
                        "TPE2" to listOf("Test Album Artist"),
                        "TYER" to listOf("2020"),
                        "TRCK" to listOf("1/10"),
                        "TPOS" to listOf("1/2"),
                        "TCON" to listOf("Rock", "Electronic")))

        val tags = tagParser.parse(metadata)

        assertEquals("Test Song", tags.name)
        assertEquals("Test Album", tags.albumName)
        assertEquals(listOf("Test Artist"), tags.artistNames)
        assertEquals(listOf("Test Album Artist"), tags.albumArtistNames)
        assertEquals(2020, tags.date?.year)
        assertEquals(1, tags.track)
        assertEquals(1, tags.disc)
        assertEquals(listOf("Rock", "Electronic"), tags.genreNames)
    }

    @Test
    fun tagParser_parse_multipleFormats() {
        // Test priority handling between different tag formats
        val metadata =
            createTestMetadata(
                id3v2Tags = mapOf("TIT2" to listOf("ID3 Song"), "TALB" to listOf("ID3 Album")),
                xiphTags = mapOf("TITLE" to listOf("Xiph Song"), "ALBUM" to listOf("Xiph Album")),
                mp4Tags = mapOf("©nam" to listOf("MP4 Song"), "©alb" to listOf("MP4 Album")))

        val tags = tagParser.parse(metadata)

        // Check priority: xiph > mp4 > id3v2
        assertEquals("Xiph Song", tags.name)
        assertEquals("Xiph Album", tags.albumName)
    }

    @Test
    fun tagParser_parse_compilation() {
        // Test compilation album behavior
        val metadata = createTestMetadata(id3v2Tags = mapOf("TCMP" to listOf("1")))

        val tags = tagParser.parse(metadata)

        assertEquals(listOf("Various Artists"), tags.albumArtistNames)
        assertEquals(listOf("compilation"), tags.releaseTypes)
    }

    @Test
    fun tagParser_parse_compilationWithReleaseType() {
        // Test compilation album with explicit release type
        val metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf("TCMP" to listOf("1"), "TXXX:RELEASETYPE" to listOf("soundtrack")))

        val tags = tagParser.parse(metadata)

        assertEquals(listOf("Various Artists"), tags.albumArtistNames)
        assertEquals(listOf("soundtrack"), tags.releaseTypes)
    }

    @Test
    fun tagParser_parse_empty() {
        // Test handling of empty metadata
        val metadata = createTestMetadata()
        val tags = tagParser.parse(metadata)

        assertNull(tags.name)
        assertNull(tags.albumName)
        assertEquals(emptyList<String>(), tags.artistNames)
    }

    @Test
    fun tagParser_musicBrainzIds() {
        val metadata =
            createTestMetadata(
                id3v2Tags =
                    mapOf(
                        "TXXX:MUSICBRAINZ RELEASE TRACK ID" to listOf("track-id-123"),
                        "TXXX:MUSICBRAINZ ALBUM ID" to listOf("album-id-456"),
                        "TXXX:MUSICBRAINZ ARTIST ID" to listOf("artist-id-789"),
                        "TXXX:MUSICBRAINZ ALBUM ARTIST ID" to listOf("album-artist-id-012")))

        val tags = tagParser.parse(metadata)

        assertEquals("track-id-123", tags.musicBrainzId)
        assertEquals("album-id-456", tags.albumMusicBrainzId)
        assertEquals(listOf("artist-id-789"), tags.artistMusicBrainzIds)
        assertEquals(listOf("album-artist-id-012"), tags.albumArtistMusicBrainzIds)
    }

    @Test
    fun tagParser_replayGain() {
        val metadata =
            createTestMetadata(
                xiphTags =
                    mapOf(
                        "REPLAYGAIN_TRACK_GAIN" to listOf("-3.5 dB"),
                        "REPLAYGAIN_ALBUM_GAIN" to listOf("-2.1 dB")))

        val tags = tagParser.parse(metadata)

        assertEquals(-3.5f, tags.replayGainTrackAdjustment)
        assertEquals(-2.1f, tags.replayGainAlbumAdjustment)
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

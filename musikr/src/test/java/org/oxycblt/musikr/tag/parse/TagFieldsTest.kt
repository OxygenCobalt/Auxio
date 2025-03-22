/*
 * Copyright (c) 2024 Auxio Project
 * TagFieldsTest.kt is part of Auxio.
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

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.tag.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class TagFieldsTest {

    @Test
    fun metadata_name_formats() {
        // Test ID3v2 format
        var metadata = createTestMetadata(id3v2Tags = mapOf("TIT2" to listOf("ID3 Title")))
        assertEquals("ID3 Title", metadata.name())

        // Test MP4 format
        metadata = createTestMetadata(mp4Tags = mapOf("©nam" to listOf("MP4 Title")))
        assertEquals("MP4 Title", metadata.name())

        // Test Xiph format
        metadata = createTestMetadata(xiphTags = mapOf("TITLE" to listOf("Xiph Title")))
        assertEquals("Xiph Title", metadata.name())

        // Test priority (xiph > mp4 > id3v2)
        metadata = createTestMetadata(
            id3v2Tags = mapOf("TIT2" to listOf("ID3 Title")),
            mp4Tags = mapOf("©nam" to listOf("MP4 Title")),
            xiphTags = mapOf("TITLE" to listOf("Xiph Title"))
        )
        assertEquals("Xiph Title", metadata.name())
    }

    @Test
    fun metadata_track_formats() {
        // Test ID3v2 format
        var metadata = createTestMetadata(id3v2Tags = mapOf("TRCK" to listOf("5/10")))
        assertEquals(5, metadata.track())

        // Test ID3v2 format without total
        metadata = createTestMetadata(id3v2Tags = mapOf("TRCK" to listOf("5")))
        assertEquals(5, metadata.track())

        // Test MP4 format
        metadata = createTestMetadata(mp4Tags = mapOf("trkn" to listOf("7/12")))
        assertEquals(7, metadata.track())

        // Test Xiph format
        metadata = createTestMetadata(
            xiphTags = mapOf(
                "TRACKNUMBER" to listOf("9"),
                "TOTALTRACKS" to listOf("15")
            )
        )
        assertEquals(9, metadata.track())

        // Test Xiph alternative total tracks
        metadata = createTestMetadata(
            xiphTags = mapOf(
                "TRACKNUMBER" to listOf("8"),
                "TRACKTOTAL" to listOf("16")
            )
        )
        assertEquals(8, metadata.track())
    }

    @Test
    fun metadata_disc_formats() {
        // Test ID3v2 format
        var metadata = createTestMetadata(id3v2Tags = mapOf("TPOS" to listOf("2/3")))
        assertEquals(2, metadata.disc())

        // Test MP4 format
        metadata = createTestMetadata(mp4Tags = mapOf("disk" to listOf("1/2")))
        assertEquals(1, metadata.disc())

        // Test Xiph format
        metadata = createTestMetadata(
            xiphTags = mapOf(
                "DISCNUMBER" to listOf("3"),
                "TOTALDISCS" to listOf("4")
            )
        )
        assertEquals(3, metadata.disc())

        // Test Xiph alternative total discs
        metadata = createTestMetadata(
            xiphTags = mapOf(
                "DISCNUMBER" to listOf("2"),
                "DISCTOTAL" to listOf("5")
            )
        )
        assertEquals(2, metadata.disc())
    }

    @Test
    fun metadata_subtitle() {
        var metadata = createTestMetadata(id3v2Tags = mapOf("TSST" to listOf("Bonus Disc")))
        assertEquals("Bonus Disc", metadata.subtitle())

        metadata = createTestMetadata(xiphTags = mapOf("DISCSUBTITLE" to listOf("Rarities")))
        assertEquals("Rarities", metadata.subtitle())

        // Test priority
        metadata = createTestMetadata(
            id3v2Tags = mapOf("TSST" to listOf("ID3 Subtitle")),
            xiphTags = mapOf("DISCSUBTITLE" to listOf("Xiph Subtitle"))
        )
        assertEquals("Xiph Subtitle", metadata.subtitle())
    }

    @Test
    fun metadata_date_formats() {
        // Test simple year
        var metadata = createTestMetadata(id3v2Tags = mapOf("TDRC" to listOf("2022")))
        assertEquals("2022", metadata.date().toString())

        // Test ISO-8601 date
        metadata = createTestMetadata(id3v2Tags = mapOf("TDRC" to listOf("2022-05-17")))
        assertEquals("2022-05-17", metadata.date().toString())

        // Test ISO-8601 datetime
        metadata = createTestMetadata(id3v2Tags = mapOf("TDRC" to listOf("2022-05-17T14:30:00")))
        assertEquals("2022-05-17T14:30:00Z", metadata.date().toString())

        // Test Xiph date
        metadata = createTestMetadata(xiphTags = mapOf("DATE" to listOf("2021-12-25")))
        assertEquals("2021-12-25", metadata.date().toString())

        // Test date priority (ORIGINALDATE > DATE)
        metadata = createTestMetadata(
            xiphTags = mapOf(
                "DATE" to listOf("2023-01-01"),
                "ORIGINALDATE" to listOf("2021-01-01")
            )
        )
        assertEquals("2021-01-01", metadata.date().toString())

        // Test MP4 date
        metadata = createTestMetadata(mp4Tags = mapOf("©day" to listOf("2020-11-30")))
        assertEquals("2020-11-30", metadata.date().toString())
    }

    @Test
    fun metadata_id3v23Date() {
        // Test ID3v2.3 date components
        val metadata = createTestMetadata(
            id3v2Tags = mapOf(
                "TYER" to listOf("2019"),
                "TDAT" to listOf("0523"), // May 23
                "TIME" to listOf("2145") // 21:45
            )
        )
        val date = metadata.date()
        assertEquals(2019, date?.year)
        assertEquals(5, date?.month)
        assertEquals(23, date?.day)
        assertEquals(21, date?.hour)
        assertEquals(45, date?.minute)
    }

    @Test
    fun metadata_albumInfo() {
        val metadata = createTestMetadata(
            id3v2Tags = mapOf(
                "TALB" to listOf("Album Name"),
                "TSOA" to listOf("Sort Album Name"),
                "TXXX:RELEASETYPE" to listOf("compilation")
            )
        )

        assertEquals("Album Name", metadata.albumName())
        assertEquals("Sort Album Name", metadata.albumSortName())
        assertEquals(listOf("compilation"), metadata.releaseTypes())
    }

    @Test
    fun metadata_artistInfo() {
        val metadata = createTestMetadata(
            id3v2Tags = mapOf(
                "TPE1" to listOf("Artist 1"),
                "TPE2" to listOf("Album Artist 1"),
                "TSOP" to listOf("Artist 1 Sort"),
                "TSO2" to listOf("Album Artist 1 Sort")
            )
        )

        assertEquals(listOf("Artist 1"), metadata.artistNames())
        assertEquals(listOf("Album Artist 1"), metadata.albumArtistNames())
        assertEquals(listOf("Artist 1 Sort"), metadata.artistSortNames())
        assertEquals(listOf("Album Artist 1 Sort"), metadata.albumArtistSortNames())
    }

    @Test
    fun metadata_musicBrainzIds() {
        // Test different formats and variants of MusicBrainz identifiers
        val metadata = createTestMetadata(
            xiphTags = mapOf(
                "MUSICBRAINZ_RELEASETRACKID" to listOf("track-mb-id"),
                "MUSICBRAINZ_ALBUMID" to listOf("album-mb-id")
            )
        )

        assertEquals("track-mb-id", metadata.musicBrainzId())
        assertEquals("album-mb-id", metadata.albumMusicBrainzId())

        // Test Apple iTunes MP4 variants
        val metadata2 = createTestMetadata(
            mp4Tags = mapOf(
                "----:COM.APPLE.ITUNES:MUSICBRAINZ RELEASE TRACK ID" to listOf("track-mb-id-2"),
                "----:COM.APPLE.ITUNES:MUSICBRAINZ ALBUM ID" to listOf("album-mb-id-2")
            )
        )

        assertEquals("track-mb-id-2", metadata2.musicBrainzId())
        assertEquals("album-mb-id-2", metadata2.albumMusicBrainzId())
    }

    @Test
    fun metadata_isCompilation() {
        // Test various compilation flags
        var metadata = createTestMetadata(
            id3v2Tags = mapOf("TCMP" to listOf("1"))
        )
        assertTrue(metadata.isCompilation())

        metadata = createTestMetadata(
            id3v2Tags = mapOf("TCMP" to listOf("0"))
        )
        assertFalse(metadata.isCompilation())

        metadata = createTestMetadata(
            xiphTags = mapOf("COMPILATION" to listOf("1"))
        )
        assertTrue(metadata.isCompilation())

        metadata = createTestMetadata(
            mp4Tags = mapOf("cpil" to listOf("1"))
        )
        assertTrue(metadata.isCompilation())

        // Test invalid value
        metadata = createTestMetadata(
            id3v2Tags = mapOf("TCMP" to listOf("yes"))
        )
        assertFalse(metadata.isCompilation())
    }

    @Test
    fun metadata_replayGain() {
        // Test ReplayGain track adjustment
        var metadata = createTestMetadata(
            xiphTags = mapOf("REPLAYGAIN_TRACK_GAIN" to listOf("-5.5 dB"))
        )
        assertEquals(-5.5f, metadata.replayGainTrackAdjustment())

        // Test ReplayGain album adjustment
        metadata = createTestMetadata(
            xiphTags = mapOf("REPLAYGAIN_ALBUM_GAIN" to listOf("3.2 dB"))
        )
        assertEquals(3.2f, metadata.replayGainAlbumAdjustment())

        // Test R128 track gain
        metadata = createTestMetadata(
            xiphTags = mapOf("R128_TRACK_GAIN" to listOf("-2560"))
        )
        // R128 is converted to match ReplayGain scale (-2560/256 + 5 = -5.0)
        assertEquals(-5.0f, metadata.replayGainTrackAdjustment())

        // Test zero value
        metadata = createTestMetadata(
            xiphTags = mapOf("REPLAYGAIN_TRACK_GAIN" to listOf("0.0 dB"))
        )
        assertNull(metadata.replayGainTrackAdjustment())
    }

    @Test
    fun metadata_genreNames() {
        var metadata = createTestMetadata(
            id3v2Tags = mapOf("TCON" to listOf("Rock", "Electronic"))
        )
        assertEquals(listOf("Rock", "Electronic"), metadata.genreNames())

        metadata = createTestMetadata(
            mp4Tags = mapOf("©gen" to listOf("Hip Hop", "Jazz"))
        )
        assertEquals(listOf("Hip Hop", "Jazz"), metadata.genreNames())

        metadata = createTestMetadata(
            xiphTags = mapOf("GENRE" to listOf("Classical", "Ambient"))
        )
        assertEquals(listOf("Classical", "Ambient"), metadata.genreNames())
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
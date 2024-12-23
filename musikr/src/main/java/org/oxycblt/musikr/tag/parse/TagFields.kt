/*
 * Copyright (c) 2024 Auxio Project
 * TagFields.kt is part of Auxio.
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

import androidx.core.text.isDigitsOnly
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.format.parseSlashPositionField
import org.oxycblt.musikr.tag.format.parseXiphPositionField
import org.oxycblt.musikr.util.nonZeroOrNull

// Note: TagLibJNI deliberately uppercases descriptive tags to avoid casing issues,
// hence why the casing here is matched. Note that MP4 atoms are kept in their
// original casing, as they are case-sensitive.

// Song
internal fun Metadata.musicBrainzId() =
    (xiph["MUSICBRAINZ_RELEASETRACKID"]
        ?: xiph["MUSICBRAINZ RELEASE TRACK ID"]
        ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ RELEASE TRACK ID"]
        ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ_RELEASETRACKID"])
        ?: id3v2["TXXX:MUSICBRAINZ RELEASE TRACK ID"]
        ?: id3v2["TXXX:MUSICBRAINZ_RELEASETRACKID"]?.first()

internal fun Metadata.name() =
    (xiph["TITLE"] ?: mp4["©nam"] ?: mp4["©trk"] ?: id3v2["TIT2"])?.first()

internal fun Metadata.sortName() = (xiph["TITLESORT"] ?: mp4["sonm"] ?: id3v2["TSOT"])?.first()

// Track.
internal fun Metadata.track() =
    (parseXiphPositionField(
        xiph["TRACKNUMBER"]?.first(),
        (xiph["TOTALTRACKS"] ?: xiph["TRACKTOTAL"] ?: xiph["TRACKC"])?.first())
        ?: (mp4["trkn"] ?: id3v2["TRCK"])?.run { first().parseSlashPositionField() })

// Disc and it's subtitle name.
internal fun Metadata.disc() =
    (parseXiphPositionField(
        xiph["DISCNUMBER"]?.first(),
        (xiph["TOTALDISCS"] ?: xiph["DISCTOTAL"] ?: xiph["DISCC"])?.run { first() })
        ?: (mp4["disk"] ?: id3v2["TPOS"])?.run { first().parseSlashPositionField() })

internal fun Metadata.subtitle() = (xiph["DISCSUBTITLE"] ?: id3v2["TSST"])?.first()

internal fun Metadata.date() =
    // For ID3v 2, are somewhat complicated, as not only did their semantics change from a flat
    // year value in ID3v2.3 to a full ISO-8601 date in ID3v2.4, but there are also a variety of
    // date types.
    // Our hierarchy for dates is as such:
    // 1. ID3v2.4 Original Date, as it resolves the "Released in X, Remastered in Y" issue
    // 2. ID3v2.4 Recording Date, as it is the most common date type
    // 3. ID3v2.4 Release Date, as it is the second most common date type
    // 4. ID3v2.3 Original Date, as it is like #1
    // 5. ID3v2.3 Release Year, as it is the most common date type
    // xiph dates aren't complicated, but there are still several types
    // Our hierarchy for dates is as such:
    // 1. Original Date, as it solves the "Released in X, Remastered in Y" issue
    // 2. Date, as it is the most common date type
    // 3. Year, as old xiph tags tended to use this (I know this because it's the only
    // date tag that android supports, so it must be 15 years old or more!)
    // TODO: Show original and normal dates side-by-side
    // TODO: Handle dates that are in "January" because the actual specific release date
    //  isn't known?
    ((xiph["ORIGINALDATE"]
            ?: xiph["DATE"]
            ?: xiph["YEAR"]
            ?: mp4["©day"]
            ?: id3v2["TDOR"]
            ?: id3v2["TDRC"]
            ?: id3v2["TDRL"])
        ?.run { Date.from(first()) } ?: parseId3v23Date())

// Album
internal fun Metadata.albumMusicBrainzId() =
    (xiph["MUSICBRAINZ_ALBUMID"]
            ?: xiph["MUSICBRAINZ ALBUM ID"]
            ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ ALBUM ID"]
            ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ_ALBUMID"]
            ?: id3v2["TXXX:MUSICBRAINZ ALBUM ID"]
            ?: id3v2["TXXX:MUSICBRAINZ_ALBUMID"])
        ?.first()

internal fun Metadata.albumName() = (xiph["ALBUM"] ?: mp4["©alb"] ?: id3v2["TALB"])?.first()

internal fun Metadata.albumSortName() = (xiph["ALBUMSORT"] ?: mp4["soal"] ?: id3v2["TSOA"])?.first()

internal fun Metadata.releaseTypes() =
    // GRP/GRP1 are assumed to also pertain to release types.
    // GRP1 is a non-standard iTunes extension.
    (xiph["RELEASETYPE"]
        ?: xiph["MUSICBRAINZ ALBUM TYPE"]
        ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ ALBUM TYPE"]
        ?: mp4["----:COM.APPLE.ITUNES:RELEASETYPE"]
        ?: mp4["©grp"]
        ?: id3v2["TXXX:MUSICBRAINZ ALBUM TYPE"]
        ?: id3v2["TXXX:RELEASETYPE"]
        ?: id3v2["GRP1"])

// Artist
internal fun Metadata.artistMusicBrainzIds() =
    (xiph["MUSICBRAINZ_ARTISTID"]
        ?: xiph["MUSICBRAINZ ARTIST ID"]
        ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ ARTIST ID"]
        ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ_ARTISTID"]
        ?: id3v2["TXXX:MUSICBRAINZ ARTIST ID"]
        ?: id3v2["TXXX:MUSICBRAINZ_ARTISTID"])

internal fun Metadata.artistNames() =
    (xiph["ARTISTS"]
        ?: xiph["ARTIST"]
        ?: mp4["----:COM.APPLE.ITUNES:ARTISTS"]
        ?: mp4["©ART"]
        ?: mp4["----:COM.APPLE.ITUNES:ARTIST"]
        ?: id3v2["TXXX:ARTISTS"]
        ?: id3v2["TPE1"]
        ?: id3v2["TXXX:ARTIST"])

internal fun Metadata.artistSortNames() =
    (xiph["ARTISTSSORT"]
        ?: xiph["ARTISTS_SORT"]
        ?: xiph["ARTISTS SORT"]
        ?: xiph["ARTISTSORT"]
        ?: xiph["ARTIST SORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ARTISTSSORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ARTISTS_SORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ARTISTS SORT"]
        ?: mp4["soar"]
        ?: mp4["----:COM.APPLE.ITUNES:ARTISTSORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ARTIST SORT"]
        ?: id3v2["TXXX:ARTISTSSORT"]
        ?: id3v2["TXXX:ARTISTS_SORT"]
        ?: id3v2["TXXX:ARTISTS SORT"]
        ?: id3v2["TSOP"]
        ?: id3v2["TXXX:ARTISTSORT"]
        ?: id3v2["TXXX:ARTIST SORT"])

internal fun Metadata.albumArtistMusicBrainzIds() =
    (xiph["MUSICBRAINZ_ALBUMARTISTID"]
        ?: xiph["MUSICBRAINZ ALBUM ARTIST ID"]
        ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ ALBUM ARTIST ID"]
        ?: mp4["----:COM.APPLE.ITUNES:MUSICBRAINZ_ALBUMARTISTID"]
        ?: id3v2["TXXX:MUSICBRAINZ ALBUM ARTIST ID"]
        ?: id3v2["TXXX:MUSICBRAINZ_ALBUMARTISTID"])

internal fun Metadata.albumArtistNames() =
    (xiph["ALBUMARTISTS"]
        ?: xiph["ALBUM_ARTISTS"]
        ?: xiph["ALBUM ARTISTS"]
        ?: xiph["ALBUMARTIST"]
        ?: xiph["ALBUM ARTIST"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUMARTISTS"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUM_ARTISTS"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUM ARTISTS"]
        ?: mp4["aART"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUMARTIST"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUM ARTIST"]
        ?: id3v2["TXXX:ALBUMARTISTS"]
        ?: id3v2["TXXX:ALBUM_ARTISTS"]
        ?: id3v2["TXXX:ALBUM ARTISTS"]
        ?: id3v2["TPE2"]
        ?: id3v2["TXXX:ALBUMARTIST"]
        ?: id3v2["TXXX:ALBUM ARTIST"])

internal fun Metadata.albumArtistSortNames() =
    // TSO2 is a non-standard iTunes extension.
    (xiph["ALBUMARTISTSSORT"]
        ?: xiph["ALBUMARTISTS_SORT"]
        ?: xiph["ALBUMARTISTS SORT"]
        ?: xiph["ALBUMARTISTSORT"]
        ?: xiph["ALBUM ARTIST SORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUMARTISTSSORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUMARTISTS_SORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUMARTISTS SORT"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUMARTISTSORT"]
        ?: mp4["soaa"]
        ?: mp4["----:COM.APPLE.ITUNES:ALBUM ARTIST SORT"]
        ?: id3v2["TXXX:ALBUMARTISTSSORT"]
        ?: id3v2["TXXX:ALBUMARTISTS_SORT"]
        ?: id3v2["TXXX:ALBUMARTISTS SORT"]
        ?: id3v2["TXXX:ALBUMARTISTSORT"]
        ?: id3v2["TSO2"]
        ?: id3v2["TXXX:ALBUM ARTIST SORT"])

// Genre
internal fun Metadata.genreNames() = xiph["GENRE"] ?: mp4["©gen"] ?: mp4["gnre"] ?: id3v2["TCON"]

// Compilation Flag
internal fun Metadata.isCompilation() =
    // TCMP is a non-standard itunes extension
    (xiph["COMPILATION"]
            ?: xiph["ITUNESCOMPILATION"]
            ?: mp4["cpil"]
            ?: mp4["----:COM.APPLE.ITUNES:COMPILATION"]
            ?: mp4["----:COM.APPLE.ITUNES:ITUNESCOMPILATION"]
            ?: id3v2["TCMP"]
            ?: id3v2["TXXX:COMPILATION"]
            ?: id3v2["TXXX:ITUNESCOMPILATION"])
        ?.let {
            // Ignore invalid instances of this tag
            it == listOf("1")
        }

// ReplayGain information
internal fun Metadata.replayGainTrackAdjustment() =
    (xiph["R128_TRACK_GAIN"]?.parseR128Adjustment()
        ?: xiph["REPLAYGAIN_TRACK_GAIN"]?.parseReplayGainAdjustment()
        ?: mp4["----:COM.APPLE.ITUNES:REPLAYGAIN_TRACK_GAIN"]?.parseReplayGainAdjustment()
        ?: id3v2["TXXX:REPLAYGAIN_TRACK_GAIN"]?.parseReplayGainAdjustment())

internal fun Metadata.replayGainAlbumAdjustment() =
    (xiph["R128_ALBUM_GAIN"]?.parseR128Adjustment()
        ?: xiph["REPLAYGAIN_ALBUM_GAIN"]?.parseReplayGainAdjustment()
        ?: mp4["----:COM.APPLE.ITUNES:REPLAYGAIN_ALBUM_GAIN"]?.parseReplayGainAdjustment()
        ?: id3v2["TXXX:REPLAYGAIN_ALBUM_GAIN"]?.parseReplayGainAdjustment())

private fun List<String>.parseR128Adjustment() =
    first().replace(REPLAYGAIN_ADJUSTMENT_FILTER_REGEX, "").toFloatOrNull()?.nonZeroOrNull()?.run {
        // Convert to fixed-point and adjust to LUFS 18 to match the ReplayGain scale
        this / 256f + 5
    }

/**
 * Parse a ReplayGain adjustment into a float value.
 *
 * @return A parsed adjustment float, or null if the adjustment had invalid formatting.
 */
private fun List<String>.parseReplayGainAdjustment() =
    first().replace(REPLAYGAIN_ADJUSTMENT_FILTER_REGEX, "").toFloatOrNull()?.nonZeroOrNull()

/**
 * Matches non-float information from ReplayGain adjustments. Derived from vanilla music:
 * https://github.com/vanilla-music/vanilla
 */
private val REPLAYGAIN_ADJUSTMENT_FILTER_REGEX by lazy { Regex("[^\\d.-]") }

private fun Metadata.parseId3v23Date(): Date? {
    // Assume that TDAT/TIME can refer to TYER or TORY depending on if TORY
    // is present.
    val year =
        id3v2["TORY"]?.run { first().toIntOrNull() }
            ?: id3v2["TYER"]?.run { first().toIntOrNull() }
            ?: return null

    val tdat = id3v2["TDAT"]
    return if (tdat != null && tdat.first().length == 4 && tdat.first().isDigitsOnly()) {
        // TDAT frames consist of a 4-digit string where the first two digits are
        // the month and the last two digits are the day.
        val mm = tdat.first().substring(0..1).toInt()
        val dd = tdat.first().substring(2..3).toInt()

        val time = id3v2["TIME"]
        if (time != null && time.first().length == 4 && time.first().isDigitsOnly()) {
            // TIME frames consist of a 4-digit string where the first two digits are
            // the hour and the last two digits are the minutes. No second value is
            // possible.
            val hh = time.first().substring(0..1).toInt()
            val mi = time.first().substring(2..3).toInt()
            // Able to return a full date.
            Date.from(year, mm, dd, hh, mi)
        } else {
            // Unable to parse time, just return a date
            Date.from(year, mm, dd)
        }
    } else {
        // Unable to parse month/day, just return a year
        return Date.from(year)
    }
}

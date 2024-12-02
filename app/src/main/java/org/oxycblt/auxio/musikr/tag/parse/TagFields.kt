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
 
package org.oxycblt.auxio.musikr.tag.parse

import androidx.core.text.isDigitsOnly
import org.oxycblt.auxio.musikr.tag.Date
import org.oxycblt.auxio.musikr.tag.util.parseId3v2PositionField
import org.oxycblt.auxio.musikr.tag.util.parseVorbisPositionField
import org.oxycblt.auxio.util.nonZeroOrNull

// Song
fun TextTags.musicBrainzId() =
    (vorbis["musicbrainz_releasetrackid"]
            ?: vorbis["musicbrainz release track id"]
            ?: id3v2["TXXX:musicbrainz release track id"]
            ?: id3v2["TXXX:musicbrainz_releasetrackid"])
        ?.first()

fun TextTags.name() = (vorbis["title"] ?: id3v2["TIT2"])?.first()

fun TextTags.sortName() = (vorbis["titlesort"] ?: id3v2["TSOT"])?.first()

// Track.
fun TextTags.track() =
    (parseVorbisPositionField(
        vorbis["tracknumber"]?.first(),
        (vorbis["totaltracks"] ?: vorbis["tracktotal"] ?: vorbis["trackc"])?.first())
        ?: id3v2["TRCK"]?.run { first().parseId3v2PositionField() })

// Disc and it's subtitle name.
fun TextTags.disc() =
    (parseVorbisPositionField(
        vorbis["discnumber"]?.first(),
        (vorbis["totaldiscs"] ?: vorbis["disctotal"] ?: vorbis["discc"])?.run { first() })
        ?: id3v2["TPOS"]?.run { first().parseId3v2PositionField() })

fun TextTags.subtitle() = (vorbis["discsubtitle"] ?: id3v2["TSST"])?.first()

// Dates are somewhat complicated, as not only did their semantics change from a flat year
// value in ID3v2.3 to a full ISO-8601 date in ID3v2.4, but there are also a variety of
// date types.
// Our hierarchy for dates is as such:
// 1. ID3v2.4 Original Date, as it resolves the "Released in X, Remastered in Y" issue
// 2. ID3v2.4 Recording Date, as it is the most common date type
// 3. ID3v2.4 Release Date, as it is the second most common date type
// 4. ID3v2.3 Original Date, as it is like #1
// 5. ID3v2.3 Release Year, as it is the most common date type
// TODO: Show original and normal dates side-by-side
// TODO: Handle dates that are in "January" because the actual specific release date
//  isn't known?
fun TextTags.date() =
    (vorbis["originaldate"]?.run { Date.from(first()) }
        ?: vorbis["date"]?.run { Date.from(first()) }
        ?: vorbis["year"]?.run { Date.from(first()) }
        ?:

        // Vorbis dates are less complicated, but there are still several types
        // Our hierarchy for dates is as such:
        // 1. Original Date, as it solves the "Released in X, Remastered in Y" issue
        // 2. Date, as it is the most common date type
        // 3. Year, as old vorbis tags tended to use this (I know this because it's the only
        // date tag that android supports, so it must be 15 years old or more!)
        id3v2["TDOR"]?.run { Date.from(first()) }
        ?: id3v2["TDRC"]?.run { Date.from(first()) }
        ?: id3v2["TDRL"]?.run { Date.from(first()) }
        ?: parseId3v23Date())

// Album
fun TextTags.albumMusicBrainzId() =
    (vorbis["musicbrainz_albumid"]
            ?: vorbis["musicbrainz album id"]
            ?: id3v2["TXXX:musicbrainz album id"]
            ?: id3v2["TXXX:musicbrainz_albumid"])
        ?.first()

fun TextTags.albumName() = (vorbis["album"] ?: id3v2["TALB"])?.first()

fun TextTags.albumSortName() = (vorbis["albumsort"] ?: id3v2["TSOA"])?.first()

fun TextTags.releaseTypes() =
    (vorbis["releasetype"]
        ?: vorbis["musicbrainz album type"]
        ?: id3v2["TXXX:musicbrainz album type"]
        ?: id3v2["TXXX:releasetype"]
        ?:
        // This is a non-standard iTunes extension
        id3v2["GRP1"])

// Artist
fun TextTags.artistMusicBrainzIds() =
    (vorbis["musicbrainz_artistid"]
        ?: vorbis["musicbrainz artist id"]
        ?: id3v2["TXXX:musicbrainz artist id"]
        ?: id3v2["TXXX:musicbrainz_artistid"])

fun TextTags.artistNames() =
    (vorbis["artists"]
        ?: vorbis["artist"]
        ?: id3v2["TXXX:artists"]
        ?: id3v2["TPE1"]
        ?: id3v2["TXXX:artist"])

fun TextTags.artistSortNames() =
    (vorbis["artistssort"]
        ?: vorbis["artists_sort"]
        ?: vorbis["artists sort"]
        ?: vorbis["artistsort"]
        ?: vorbis["artist sort"]
        ?: id3v2["TXXX:artistssort"]
        ?: id3v2["TXXX:artists_sort"]
        ?: id3v2["TXXX:artists sort"]
        ?: id3v2["TSOP"]
        ?: id3v2["artistsort"]
        ?: id3v2["TXXX:artist sort"])

fun TextTags.albumArtistMusicBrainzIds() =
    (vorbis["musicbrainz_albumartistid"]
        ?: vorbis["musicbrainz album artist id"]
        ?: id3v2["TXXX:musicbrainz album artist id"]
        ?: id3v2["TXXX:musicbrainz_albumartistid"])

fun TextTags.albumArtistNames() =
    (vorbis["albumartists"]
        ?: vorbis["album_artists"]
        ?: vorbis["album artists"]
        ?: vorbis["albumartist"]
        ?: vorbis["album artist"]
        ?: id3v2["TXXX:albumartists"]
        ?: id3v2["TXXX:album_artists"]
        ?: id3v2["TXXX:album artists"]
        ?: id3v2["TPE2"]
        ?: id3v2["TXXX:albumartist"]
        ?: id3v2["TXXX:album artist"])

fun TextTags.albumArtistSortNames() =
    (vorbis["albumartistssort"]
        ?: vorbis["albumartists_sort"]
        ?: vorbis["albumartists sort"]
        ?: vorbis["albumartistsort"]
        ?: vorbis["album artist sort"]
        ?: id3v2["TXXX:albumartistssort"]
        ?: id3v2["TXXX:albumartists_sort"]
        ?: id3v2["TXXX:albumartists sort"]
        ?: id3v2["TXXX:albumartistsort"]
        // This is a non-standard iTunes extension
        ?: id3v2["TSO2"]
        ?: id3v2["TXXX:album artist sort"])

// Genre
fun TextTags.genreNames() = vorbis["genre"] ?: id3v2["TCON"]

// Compilation Flag
fun TextTags.isCompilation() =
    (vorbis["compilation"]
            ?: vorbis["itunescompilation"]
            ?: id3v2["TCMP"] // This is a non-standard itunes extension
            ?: id3v2["TXXX:compilation"]
            ?: id3v2["TXXX:itunescompilation"])
        ?.let {
            // Ignore invalid instances of this tag
            it == listOf("1")
        }

// ReplayGain information
fun TextTags.replayGainTrackAdjustment() =
    (vorbis["r128_track_gain"]?.parseR128Adjustment()
        ?: vorbis["replaygain_track_gain"]?.parseReplayGainAdjustment()
        ?: id3v2["TXXX:replaygain_track_gain"]?.parseReplayGainAdjustment())

fun TextTags.replayGainAlbumAdjustment() =
    (vorbis["r128_album_gain"]?.parseR128Adjustment()
        ?: vorbis["replaygain_album_gain"]?.parseReplayGainAdjustment()
        ?: id3v2["TXXX:replaygain_album_gain"]?.parseReplayGainAdjustment())

private fun TextTags.parseId3v23Date(): Date? {
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
val REPLAYGAIN_ADJUSTMENT_FILTER_REGEX by lazy { Regex("[^\\d.-]") }

/*
 * Copyright (c) 2023 Auxio Project
 * TagInterpreter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.stack.explore.extractor

import androidx.core.text.isDigitsOnly
import androidx.media3.exoplayer.MetadataRetriever
import javax.inject.Inject
import org.oxycblt.auxio.music.stack.explore.AudioFile
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.util.nonZeroOrNull

/**
 * An processing abstraction over the [MetadataRetriever] and [TextTags] workflow that operates on
 * [AudioFile] instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface TagInterpreter {
    /**
     * Poll to see if this worker is done processing.
     *
     * @return A completed [AudioFile] if done, null otherwise.
     */
    fun interpretOn(textTags: TextTags, audioFile: AudioFile)
}

class TagInterpreterImpl @Inject constructor() : TagInterpreter {
    override fun interpretOn(textTags: TextTags, audioFile: AudioFile) {
        populateWithId3v2(audioFile, textTags.id3v2)
        populateWithVorbis(audioFile, textTags.vorbis)
    }

    private fun populateWithId3v2(audioFile: AudioFile, textFrames: Map<String, List<String>>) {
        // Song
        (textFrames["TXXX:musicbrainz release track id"]
                ?: textFrames["TXXX:musicbrainz_releasetrackid"])
            ?.let { audioFile.musicBrainzId = it.first() }
        textFrames["TIT2"]?.let { audioFile.name = it.first() }
        textFrames["TSOT"]?.let { audioFile.sortName = it.first() }

        // Track.
        textFrames["TRCK"]?.run { first().parseId3v2PositionField() }?.let { audioFile.track = it }

        // Disc and it's subtitle name.
        textFrames["TPOS"]?.run { first().parseId3v2PositionField() }?.let { audioFile.disc = it }
        textFrames["TSST"]?.let { audioFile.subtitle = it.first() }

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
        (textFrames["TDOR"]?.run { Date.from(first()) }
                ?: textFrames["TDRC"]?.run { Date.from(first()) }
                ?: textFrames["TDRL"]?.run { Date.from(first()) }
                ?: parseId3v23Date(textFrames))
            ?.let { audioFile.date = it }

        // Album
        (textFrames["TXXX:musicbrainz album id"] ?: textFrames["TXXX:musicbrainz_albumid"])?.let {
            audioFile.albumMusicBrainzId = it.first()
        }
        textFrames["TALB"]?.let { audioFile.albumName = it.first() }
        textFrames["TSOA"]?.let { audioFile.albumSortName = it.first() }
        (textFrames["TXXX:musicbrainz album type"]
                ?: textFrames["TXXX:releasetype"]
                ?:
                // This is a non-standard iTunes extension
                textFrames["GRP1"])
            ?.let { audioFile.releaseTypes = it }

        // Artist
        (textFrames["TXXX:musicbrainz artist id"] ?: textFrames["TXXX:musicbrainz_artistid"])?.let {
            audioFile.artistMusicBrainzIds = it
        }
        (textFrames["TXXX:artists"] ?: textFrames["TPE1"] ?: textFrames["TXXX:artist"])?.let {
            audioFile.artistNames = it
        }
        (textFrames["TXXX:artistssort"]
                ?: textFrames["TXXX:artists_sort"]
                ?: textFrames["TXXX:artists sort"]
                ?: textFrames["TSOP"]
                ?: textFrames["artistsort"]
                ?: textFrames["TXXX:artist sort"])
            ?.let { audioFile.artistSortNames = it }

        // Album artist
        (textFrames["TXXX:musicbrainz album artist id"]
                ?: textFrames["TXXX:musicbrainz_albumartistid"])
            ?.let { audioFile.albumArtistMusicBrainzIds = it }
        (textFrames["TXXX:albumartists"]
                ?: textFrames["TXXX:album_artists"]
                ?: textFrames["TXXX:album artists"]
                ?: textFrames["TPE2"]
                ?: textFrames["TXXX:albumartist"]
                ?: textFrames["TXXX:album artist"])
            ?.let { audioFile.albumArtistNames = it }
        (textFrames["TXXX:albumartistssort"]
                ?: textFrames["TXXX:albumartists_sort"]
                ?: textFrames["TXXX:albumartists sort"]
                ?: textFrames["TXXX:albumartistsort"]
                // This is a non-standard iTunes extension
                ?: textFrames["TSO2"]
                ?: textFrames["TXXX:album artist sort"])
            ?.let { audioFile.albumArtistSortNames = it }

        // Genre
        textFrames["TCON"]?.let { audioFile.genreNames = it }

        // Compilation Flag
        (textFrames["TCMP"] // This is a non-standard itunes extension
            ?: textFrames["TXXX:compilation"] ?: textFrames["TXXX:itunescompilation"])
            ?.let {
                // Ignore invalid instances of this tag
                if (it.size != 1 || it[0] != "1") return@let
                // Change the metadata to be a compilation album made by "Various Artists"
                audioFile.albumArtistNames =
                    audioFile.albumArtistNames.ifEmpty { COMPILATION_ALBUM_ARTISTS }
                audioFile.releaseTypes = audioFile.releaseTypes.ifEmpty { COMPILATION_RELEASE_TYPES }
            }

        // ReplayGain information
        textFrames["TXXX:replaygain_track_gain"]?.parseReplayGainAdjustment()?.let {
            audioFile.replayGainTrackAdjustment = it
        }
        textFrames["TXXX:replaygain_album_gain"]?.parseReplayGainAdjustment()?.let {
            audioFile.replayGainAlbumAdjustment = it
        }
    }

    private fun parseId3v23Date(textFrames: Map<String, List<String>>): Date? {
        // Assume that TDAT/TIME can refer to TYER or TORY depending on if TORY
        // is present.
        val year =
            textFrames["TORY"]?.run { first().toIntOrNull() }
                ?: textFrames["TYER"]?.run { first().toIntOrNull() }
                ?: return null

        val tdat = textFrames["TDAT"]
        return if (tdat != null && tdat.first().length == 4 && tdat.first().isDigitsOnly()) {
            // TDAT frames consist of a 4-digit string where the first two digits are
            // the month and the last two digits are the day.
            val mm = tdat.first().substring(0..1).toInt()
            val dd = tdat.first().substring(2..3).toInt()

            val time = textFrames["TIME"]
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

    private fun populateWithVorbis(audioFile: AudioFile, comments: Map<String, List<String>>) {
        // Song
        (comments["musicbrainz_releasetrackid"] ?: comments["musicbrainz release track id"])?.let {
            audioFile.musicBrainzId = it.first()
        }
        comments["title"]?.let { audioFile.name = it.first() }
        comments["titlesort"]?.let { audioFile.sortName = it.first() }

        // Track.
        parseVorbisPositionField(
                comments["tracknumber"]?.first(),
                (comments["totaltracks"] ?: comments["tracktotal"] ?: comments["trackc"])?.first())
            ?.let { audioFile.track = it }

        // Disc and it's subtitle name.
        parseVorbisPositionField(
                comments["discnumber"]?.first(),
                (comments["totaldiscs"] ?: comments["disctotal"] ?: comments["discc"])?.first())
            ?.let { audioFile.disc = it }
        comments["discsubtitle"]?.let { audioFile.subtitle = it.first() }

        // Vorbis dates are less complicated, but there are still several types
        // Our hierarchy for dates is as such:
        // 1. Original Date, as it solves the "Released in X, Remastered in Y" issue
        // 2. Date, as it is the most common date type
        // 3. Year, as old vorbis tags tended to use this (I know this because it's the only
        // date tag that android supports, so it must be 15 years old or more!)
        (comments["originaldate"]?.run { Date.from(first()) }
                ?: comments["date"]?.run { Date.from(first()) }
                ?: comments["year"]?.run { Date.from(first()) })
            ?.let { audioFile.date = it }

        // Album
        (comments["musicbrainz_albumid"] ?: comments["musicbrainz album id"])?.let {
            audioFile.albumMusicBrainzId = it.first()
        }
        comments["album"]?.let { audioFile.albumName = it.first() }
        comments["albumsort"]?.let { audioFile.albumSortName = it.first() }
        (comments["releasetype"] ?: comments["musicbrainz album type"])?.let {
            audioFile.releaseTypes = it
        }

        // Artist
        (comments["musicbrainz_artistid"] ?: comments["musicbrainz artist id"])?.let {
            audioFile.artistMusicBrainzIds = it
        }
        (comments["artists"] ?: comments["artist"])?.let { audioFile.artistNames = it }
        (comments["artistssort"]
                ?: comments["artists_sort"]
                ?: comments["artists sort"]
                ?: comments["artistsort"]
                ?: comments["artist sort"])
            ?.let { audioFile.artistSortNames = it }

        // Album artist
        (comments["musicbrainz_albumartistid"] ?: comments["musicbrainz album artist id"])?.let {
            audioFile.albumArtistMusicBrainzIds = it
        }
        (comments["albumartists"]
                ?: comments["album_artists"]
                ?: comments["album artists"]
                ?: comments["albumartist"]
                ?: comments["album artist"])
            ?.let { audioFile.albumArtistNames = it }
        (comments["albumartistssort"]
                ?: comments["albumartists_sort"]
                ?: comments["albumartists sort"]
                ?: comments["albumartistsort"]
                ?: comments["album artist sort"])
            ?.let { audioFile.albumArtistSortNames = it }

        // Genre
        comments["genre"]?.let { audioFile.genreNames = it }

        // Compilation Flag
        (comments["compilation"] ?: comments["itunescompilation"])?.let {
            // Ignore invalid instances of this tag
            if (it.size != 1 || it[0] != "1") return@let
            // Change the metadata to be a compilation album made by "Various Artists"
            audioFile.albumArtistNames =
                audioFile.albumArtistNames.ifEmpty { COMPILATION_ALBUM_ARTISTS }
            audioFile.releaseTypes = audioFile.releaseTypes.ifEmpty { COMPILATION_RELEASE_TYPES }
        }

        // ReplayGain information
        // Most ReplayGain tags are formatted as a simple decibel adjustment in a custom
        // replaygain_*_gain tag, but opus has it's own "r128_*_gain" ReplayGain specification,
        // which requires dividing the adjustment by 256 to get the gain. This is used alongside
        // the base adjustment intrinsic to the format to create the normalized adjustment. This is
        // normally the only tag used for opus files, but some software still writes replay gain
        // tags anyway.
        (comments["r128_track_gain"]?.parseR128Adjustment()
                ?: comments["replaygain_track_gain"]?.parseReplayGainAdjustment())
            ?.let { audioFile.replayGainTrackAdjustment = it }
        (comments["r128_album_gain"]?.parseR128Adjustment()
                ?: comments["replaygain_album_gain"]?.parseReplayGainAdjustment())
            ?.let { audioFile.replayGainAlbumAdjustment = it }
    }

    private fun List<String>.parseR128Adjustment() =
        first()
            .replace(REPLAYGAIN_ADJUSTMENT_FILTER_REGEX, "")
            .toFloatOrNull()
            ?.nonZeroOrNull()
            ?.run {
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

    private companion object {
        const val COVER_KEY_SAMPLE = 32

        val COMPILATION_ALBUM_ARTISTS = listOf("Various Artists")
        val COMPILATION_RELEASE_TYPES = listOf("compilation")

        /**
         * Matches non-float information from ReplayGain adjustments. Derived from vanilla music:
         * https://github.com/vanilla-music/vanilla
         */
        val REPLAYGAIN_ADJUSTMENT_FILTER_REGEX by lazy { Regex("[^\\d.-]") }
    }
}

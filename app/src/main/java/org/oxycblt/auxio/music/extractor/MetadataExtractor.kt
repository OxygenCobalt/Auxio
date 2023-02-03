/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music.extractor

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MetadataRetriever
import kotlinx.coroutines.channels.Channel
import org.oxycblt.auxio.music.library.RealSong
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.metadata.TextTags
import org.oxycblt.auxio.music.parsing.parseId3v2PositionField
import org.oxycblt.auxio.music.parsing.parseVorbisPositionField
import org.oxycblt.auxio.music.storage.toAudioUri
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * The extractor that leverages ExoPlayer's [MetadataRetriever] API to parse metadata. This is the
 * last step in the music extraction process and is mostly responsible for papering over the bad
 * metadata that [RealMediaStoreExtractor] produces.
 *
 * @param context [Context] required for reading audio files.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MetadataExtractor(private val context: Context) {
    // We can parallelize MetadataRetriever Futures to work around it's speed issues,
    // producing similar throughput's to other kinds of manual metadata extraction.
    private val taskPool: Array<Task?> = arrayOfNulls(TASK_CAPACITY)

    suspend fun consume(
        incompleteSongs: Channel<RealSong.Raw>,
        completeSongs: Channel<RealSong.Raw>
    ) {
        spin@ while (true) {
            // Spin until there is an open slot we can insert a task in.
            for (i in taskPool.indices) {
                val task = taskPool[i]
                if (task != null) {
                    completeSongs.send(task.get() ?: continue)
                }
                val result = incompleteSongs.tryReceive()
                if (result.isClosed) {
                    taskPool[i] = null
                    break@spin
                }
                taskPool[i] = result.getOrNull()?.let { Task(context, it) }
            }
        }

        do {
            var ongoingTasks = false
            for (i in taskPool.indices) {
                val task = taskPool[i]
                if (task != null) {
                    val finishedRawSong = task.get()
                    if (finishedRawSong != null) {
                        completeSongs.send(finishedRawSong)
                        taskPool[i] = null
                    } else {
                        ongoingTasks = true
                    }
                }
            }
        } while (ongoingTasks)

        completeSongs.close()
    }

    private companion object {
        const val TASK_CAPACITY = 8
    }
}

/**
 * Wraps a [MetadataExtractor] future and processes it into a [RealSong.Raw] when completed.
 * @param context [Context] required to open the audio file.
 * @param raw [RealSong.Raw] to process.
 * @author Alexander Capehart (OxygenCobalt)
 */
private class Task(context: Context, private val raw: RealSong.Raw) {
    // Note that we do not leverage future callbacks. This is because errors in the
    // (highly fallible) extraction process will not bubble up to Indexer when a
    // listener is used, instead crashing the app entirely.
    private val future =
        MetadataRetriever.retrieveMetadata(
            context,
            MediaItem.fromUri(
                requireNotNull(raw.mediaStoreId) { "Invalid raw: No id" }.toAudioUri()))

    init {}

    /**
     * Try to get a completed song from this [Task], if it has finished processing.
     * @return A [RealSong.Raw] instance if processing has completed, null otherwise.
     */
    fun get(): RealSong.Raw? {
        if (!future.isDone) {
            // Not done yet, nothing to do.
            return null
        }

        val format =
            try {
                future.get()[0].getFormat(0)
            } catch (e: Exception) {
                logW("Unable to extract metadata for ${raw.name}")
                logW(e.stackTraceToString())
                null
            }
        if (format == null) {
            logD("Nothing could be extracted for ${raw.name}")
            return raw
        }

        val metadata = format.metadata
        if (metadata != null) {
            val textTags = TextTags(metadata)
            populateWithId3v2(textTags.id3v2)
            populateWithVorbis(textTags.vorbis)
        } else {
            logD("No metadata could be extracted for ${raw.name}")
        }

        return raw
    }

    /**
     * Complete this instance's [RealSong.Raw] with ID3v2 Text Identification Frames.
     * @param textFrames A mapping between ID3v2 Text Identification Frame IDs and one or more
     * values.
     */
    private fun populateWithId3v2(textFrames: Map<String, List<String>>) {
        // Song
        textFrames["TXXX:musicbrainz release track id"]?.let { raw.musicBrainzId = it.first() }
        textFrames["TIT2"]?.let { raw.name = it.first() }
        textFrames["TSOT"]?.let { raw.sortName = it.first() }

        // Track.
        textFrames["TRCK"]?.run { first().parseId3v2PositionField() }?.let { raw.track = it }

        // Disc and it's subtitle name.
        textFrames["TPOS"]?.run { first().parseId3v2PositionField() }?.let { raw.disc = it }
        textFrames["TSST"]?.let { raw.subtitle = it.first() }

        // Dates are somewhat complicated, as not only did their semantics change from a flat year
        // value in ID3v2.3 to a full ISO-8601 date in ID3v2.4, but there are also a variety of
        // date types.
        // Our hierarchy for dates is as such:
        // 1. ID3v2.4 Original Date, as it resolves the "Released in X, Remastered in Y" issue
        // 2. ID3v2.4 Recording Date, as it is the most common date type
        // 3. ID3v2.4 Release Date, as it is the second most common date type
        // 4. ID3v2.3 Original Date, as it is like #1
        // 5. ID3v2.3 Release Year, as it is the most common date type
        (textFrames["TDOR"]?.run { Date.from(first()) }
                ?: textFrames["TDRC"]?.run { Date.from(first()) }
                    ?: textFrames["TDRL"]?.run { Date.from(first()) }
                    ?: parseId3v23Date(textFrames))
            ?.let { raw.date = it }

        // Album
        textFrames["TXXX:musicbrainz album id"]?.let { raw.albumMusicBrainzId = it.first() }
        textFrames["TALB"]?.let { raw.albumName = it.first() }
        textFrames["TSOA"]?.let { raw.albumSortName = it.first() }
        (textFrames["TXXX:musicbrainz album type"] ?: textFrames["GRP1"])?.let {
            raw.releaseTypes = it
        }

        // Artist
        textFrames["TXXX:musicbrainz artist id"]?.let { raw.artistMusicBrainzIds = it }
        (textFrames["TXXX:artists"] ?: textFrames["TPE1"])?.let { raw.artistNames = it }
        (textFrames["TXXX:artists_sort"] ?: textFrames["TSOP"])?.let { raw.artistSortNames = it }

        // Album artist
        textFrames["TXXX:musicbrainz album artist id"]?.let { raw.albumArtistMusicBrainzIds = it }
        (textFrames["TXXX:albumartists"] ?: textFrames["TPE2"])?.let { raw.albumArtistNames = it }
        (textFrames["TXXX:albumartists_sort"] ?: textFrames["TSO2"])?.let {
            raw.albumArtistSortNames = it
        }

        // Genre
        textFrames["TCON"]?.let { raw.genreNames = it }
    }

    /**
     * Parses the ID3v2.3 timestamp specification into a [Date] from the given Text Identification
     * Frames.
     * @param textFrames A mapping between ID3v2 Text Identification Frame IDs and one or more
     * values.
     * @return A [Date] of a year value from TORY/TYER, a month and day value from TDAT, and a
     * hour/minute value from TIME. No second value is included. The latter two fields may not be
     * included in they cannot be parsed. Will be null if a year value could not be parsed.
     */
    private fun parseId3v23Date(textFrames: Map<String, List<String>>): Date? {
        // Assume that TDAT/TIME can refer to TYER or TORY depending on if TORY
        // is present.
        val year =
            textFrames["TORY"]?.run { first().toIntOrNull() }
                ?: textFrames["TYER"]?.run { first().toIntOrNull() } ?: return null

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

    /**
     * Complete this instance's [RealSong.Raw] with Vorbis comments.
     * @param comments A mapping between vorbis comment names and one or more vorbis comment values.
     */
    private fun populateWithVorbis(comments: Map<String, List<String>>) {
        // Song
        comments["musicbrainz_releasetrackid"]?.let { raw.musicBrainzId = it.first() }
        comments["title"]?.let { raw.name = it.first() }
        comments["titlesort"]?.let { raw.sortName = it.first() }

        // Track.
        parseVorbisPositionField(
                comments["tracknumber"]?.first(),
                (comments["totaltracks"] ?: comments["tracktotal"] ?: comments["trackc"])?.first())
            ?.let { raw.track = it }

        // Disc and it's subtitle name.
        parseVorbisPositionField(
                comments["discnumber"]?.first(),
                (comments["totaldiscs"] ?: comments["disctotal"] ?: comments["discc"])?.first())
            ?.let { raw.disc = it }
        comments["discsubtitle"]?.let { raw.subtitle = it.first() }

        // Vorbis dates are less complicated, but there are still several types
        // Our hierarchy for dates is as such:
        // 1. Original Date, as it solves the "Released in X, Remastered in Y" issue
        // 2. Date, as it is the most common date type
        // 3. Year, as old vorbis tags tended to use this (I know this because it's the only
        // date tag that android supports, so it must be 15 years old or more!)
        (comments["originaldate"]?.run { Date.from(first()) }
                ?: comments["date"]?.run { Date.from(first()) }
                    ?: comments["year"]?.run { Date.from(first()) })
            ?.let { raw.date = it }

        // Album
        comments["musicbrainz_albumid"]?.let { raw.albumMusicBrainzId = it.first() }
        comments["album"]?.let { raw.albumName = it.first() }
        comments["albumsort"]?.let { raw.albumSortName = it.first() }
        comments["releasetype"]?.let { raw.releaseTypes = it }

        // Artist
        comments["musicbrainz_artistid"]?.let { raw.artistMusicBrainzIds = it }
        (comments["artists"] ?: comments["artist"])?.let { raw.artistNames = it }
        (comments["artists_sort"] ?: comments["artistsort"])?.let { raw.artistSortNames = it }

        // Album artist
        comments["musicbrainz_albumartistid"]?.let { raw.albumArtistMusicBrainzIds = it }
        (comments["albumartists"] ?: comments["albumartist"])?.let { raw.albumArtistNames = it }
        (comments["albumartists_sort"] ?: comments["albumartistsort"])?.let {
            raw.albumArtistSortNames = it
        }

        // Genre
        comments["genre"]?.let { raw.genreNames = it }
    }
}

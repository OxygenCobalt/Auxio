/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.metadata

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.AudioOnlyExtractors
import org.oxycblt.auxio.music.model.RawSong
import org.oxycblt.auxio.music.storage.toAudioUri
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * The extractor that leverages ExoPlayer's [MetadataRetriever] API to parse metadata. This is the
 * last step in the music extraction process and is mostly responsible for papering over the bad
 * metadata that other extractors produce.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface TagExtractor {
    /**
     * Extract the metadata of songs from [incompleteSongs] and send them to [completeSongs]. Will
     * terminate as soon as [incompleteSongs] is closed.
     * @param incompleteSongs A [Channel] of incomplete songs to process.
     * @param completeSongs A [Channel] to send completed songs to.
     */
    suspend fun consume(incompleteSongs: Channel<RawSong>, completeSongs: Channel<RawSong>)
}

class TagExtractorImpl @Inject constructor(@ApplicationContext private val context: Context) :
    TagExtractor {
    override suspend fun consume(
        incompleteSongs: Channel<RawSong>,
        completeSongs: Channel<RawSong>
    ) {
        // We can parallelize MetadataRetriever Futures to work around it's speed issues,
        // producing similar throughput's to other kinds of manual metadata extraction.
        val taskPool: Array<Task?> = arrayOfNulls(TASK_CAPACITY)

        for (song in incompleteSongs) {
            spin@ while (true) {
                for (i in taskPool.indices) {
                    val task = taskPool[i]
                    if (task != null) {
                        val finishedRawSong = task.get()
                        if (finishedRawSong != null) {
                            completeSongs.send(finishedRawSong)
                            yield()
                        } else {
                            continue
                        }
                    }
                    taskPool[i] = Task(context, song)
                    break@spin
                }
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
                        yield()
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
 * Wraps a [TagExtractor] future and processes it into a [RawSong] when completed.
 * @param context [Context] required to open the audio file.
 * @param rawSong [RawSong] to process.
 * @author Alexander Capehart (OxygenCobalt)
 */
private class Task(context: Context, private val rawSong: RawSong) {
    // Note that we do not leverage future callbacks. This is because errors in the
    // (highly fallible) extraction process will not bubble up to Indexer when a
    // listener is used, instead crashing the app entirely.
    private val future =
        MetadataRetriever.retrieveMetadata(
            DefaultMediaSourceFactory(context, AudioOnlyExtractors),
            MediaItem.fromUri(
                requireNotNull(rawSong.mediaStoreId) { "Invalid raw: No id" }.toAudioUri()))

    /**
     * Try to get a completed song from this [Task], if it has finished processing.
     * @return A [RawSong] instance if processing has completed, null otherwise.
     */
    fun get(): RawSong? {
        if (!future.isDone) {
            // Not done yet, nothing to do.
            return null
        }

        val format =
            try {
                future.get()[0].getFormat(0)
            } catch (e: Exception) {
                logW("Unable to extract metadata for ${rawSong.name}")
                logW(e.stackTraceToString())
                null
            }
        if (format == null) {
            logD("Nothing could be extracted for ${rawSong.name}")
            return rawSong
        }

        val metadata = format.metadata
        if (metadata != null) {
            val textTags = TextTags(metadata)
            populateWithId3v2(textTags.id3v2)
            populateWithVorbis(textTags.vorbis)
        } else {
            logD("No metadata could be extracted for ${rawSong.name}")
        }

        return rawSong
    }

    /**
     * Complete this instance's [RawSong] with ID3v2 Text Identification Frames.
     * @param textFrames A mapping between ID3v2 Text Identification Frame IDs and one or more
     * values.
     */
    private fun populateWithId3v2(textFrames: Map<String, List<String>>) {
        // Song
        textFrames["TXXX:musicbrainz release track id"]?.let { rawSong.musicBrainzId = it.first() }
        textFrames["TIT2"]?.let { rawSong.name = it.first() }
        textFrames["TSOT"]?.let { rawSong.sortName = it.first() }

        // Track.
        textFrames["TRCK"]?.run { first().parseId3v2PositionField() }?.let { rawSong.track = it }

        // Disc and it's subtitle name.
        textFrames["TPOS"]?.run { first().parseId3v2PositionField() }?.let { rawSong.disc = it }
        textFrames["TSST"]?.let { rawSong.subtitle = it.first() }

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
            ?.let { rawSong.date = it }

        // Album
        textFrames["TXXX:musicbrainz album id"]?.let { rawSong.albumMusicBrainzId = it.first() }
        textFrames["TALB"]?.let { rawSong.albumName = it.first() }
        textFrames["TSOA"]?.let { rawSong.albumSortName = it.first() }
        (textFrames["TXXX:musicbrainz album type"]
                ?: textFrames["TXXX:releasetype"] ?: textFrames["GRP1"])
            ?.let { rawSong.releaseTypes = it }

        // Artist
        textFrames["TXXX:musicbrainz artist id"]?.let { rawSong.artistMusicBrainzIds = it }
        (textFrames["TXXX:artists"] ?: textFrames["TPE1"])?.let { rawSong.artistNames = it }
        (textFrames["TXXX:artists_sort"] ?: textFrames["TSOP"])?.let {
            rawSong.artistSortNames = it
        }

        // Album artist
        textFrames["TXXX:musicbrainz album artist id"]?.let {
            rawSong.albumArtistMusicBrainzIds = it
        }
        (textFrames["TXXX:albumartists"] ?: textFrames["TPE2"])?.let {
            rawSong.albumArtistNames = it
        }
        (textFrames["TXXX:albumartists_sort"] ?: textFrames["TSO2"])?.let {
            rawSong.albumArtistSortNames = it
        }

        // Genre
        textFrames["TCON"]?.let { rawSong.genreNames = it }
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
     * Complete this instance's [RawSong] with Vorbis comments.
     * @param comments A mapping between vorbis comment names and one or more vorbis comment values.
     */
    private fun populateWithVorbis(comments: Map<String, List<String>>) {
        // Song
        comments["musicbrainz_releasetrackid"]?.let { rawSong.musicBrainzId = it.first() }
        comments["title"]?.let { rawSong.name = it.first() }
        comments["titlesort"]?.let { rawSong.sortName = it.first() }

        // Track.
        parseVorbisPositionField(
                comments["tracknumber"]?.first(),
                (comments["totaltracks"] ?: comments["tracktotal"] ?: comments["trackc"])?.first())
            ?.let { rawSong.track = it }

        // Disc and it's subtitle name.
        parseVorbisPositionField(
                comments["discnumber"]?.first(),
                (comments["totaldiscs"] ?: comments["disctotal"] ?: comments["discc"])?.first())
            ?.let { rawSong.disc = it }
        comments["discsubtitle"]?.let { rawSong.subtitle = it.first() }

        // Vorbis dates are less complicated, but there are still several types
        // Our hierarchy for dates is as such:
        // 1. Original Date, as it solves the "Released in X, Remastered in Y" issue
        // 2. Date, as it is the most common date type
        // 3. Year, as old vorbis tags tended to use this (I know this because it's the only
        // date tag that android supports, so it must be 15 years old or more!)
        (comments["originaldate"]?.run { Date.from(first()) }
                ?: comments["date"]?.run { Date.from(first()) }
                    ?: comments["year"]?.run { Date.from(first()) })
            ?.let { rawSong.date = it }

        // Album
        comments["musicbrainz_albumid"]?.let { rawSong.albumMusicBrainzId = it.first() }
        comments["album"]?.let { rawSong.albumName = it.first() }
        comments["albumsort"]?.let { rawSong.albumSortName = it.first() }
        comments["releasetype"]?.let { rawSong.releaseTypes = it }

        // Artist
        comments["musicbrainz_artistid"]?.let { rawSong.artistMusicBrainzIds = it }
        (comments["artists"] ?: comments["artist"])?.let { rawSong.artistNames = it }
        (comments["artists_sort"] ?: comments["artistsort"])?.let { rawSong.artistSortNames = it }

        // Album artist
        comments["musicbrainz_albumartistid"]?.let { rawSong.albumArtistMusicBrainzIds = it }
        (comments["albumartists"] ?: comments["albumartist"])?.let { rawSong.albumArtistNames = it }
        (comments["albumartists_sort"] ?: comments["albumartistsort"])?.let {
            rawSong.albumArtistSortNames = it
        }

        // Genre
        comments["genre"]?.let { rawSong.genreNames = it }
    }
}

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
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import org.oxycblt.auxio.music.Date
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.audioUri
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * The layer that leverages ExoPlayer's metadata retrieval system to index metadata.
 *
 * Normally, leveraging ExoPlayer's metadata system would be a terrible idea, as it is horrifically
 * slow. However, if we parallelize it, we can get similar throughput to other metadata extractors,
 * which is nice as it means we don't have to bundle a redundant metadata library like JAudioTagger.
 *
 * Now, ExoPlayer's metadata API is not the best. It's opaque, undocumented, and prone to weird
 * pitfalls given ExoPlayer's cozy relationship with native code. However, this backend should do
 * enough to eliminate such issues.
 *
 * TODO: Fix failing ID3v2 multi-value tests in fork (Implies parsing problem)
 *
 * @author OxygenCobalt
 */
class MetadataExtractor(private val context: Context, private val mediaStoreExtractor: MediaStoreExtractor) {
    private val taskPool: Array<Task?> = arrayOfNulls(TASK_CAPACITY)

    /** Initialize the sub-layers that this layer relies on. */
    fun init() = mediaStoreExtractor.init().count

    /** Finalize the sub-layers that this layer relies on. */
    fun finalize(rawSongs: List<Song.Raw>) = mediaStoreExtractor.finalize(rawSongs)

    suspend fun parse(emit: suspend (Song.Raw) -> Unit) {
        while (true) {
            val raw = Song.Raw()
            if (mediaStoreExtractor.populateRawSong(raw) ?: break) {
                // No need to extract metadata that was successfully restored from the cache
                emit(raw)
                continue
            }

            // Spin until there is an open slot we can insert a task in. Note that we do
            // not add callbacks to our new tasks, as Future callbacks run on a different
            // executor and thus will crash the app if an error occurs instead of bubbling
            // back up to Indexer.
            spin@ while (true) {
                for (i in taskPool.indices) {
                    val task = taskPool[i]

                    if (task != null) {
                        val finishedRaw = task.get()
                        if (finishedRaw != null) {
                            emit(finishedRaw)
                            taskPool[i] = Task(context, raw)
                            break@spin
                        }
                    } else {
                        taskPool[i] = Task(context, raw)
                        break@spin
                    }
                }
            }
        }

        spin@ while (true) {
            // Spin until all of the remaining tasks are complete.
            for (i in taskPool.indices) {
                val task = taskPool[i]

                if (task != null) {
                    val finishedRaw = task.get() ?: continue@spin
                    emit(finishedRaw)
                    taskPool[i] = null
                }
            }

            break
        }
    }

    companion object {
        /** The amount of tasks this backend can run efficiently at once. */
        private const val TASK_CAPACITY = 8
    }
}

/**
 * Wraps an ExoPlayer metadata retrieval task in a safe abstraction. Access is done with [get].
 * @author OxygenCobalt
 */
class Task(context: Context, private val raw: Song.Raw) {
    private val future =
        MetadataRetriever.retrieveMetadata(
            context,
            MediaItem.fromUri(requireNotNull(raw.mediaStoreId) { "Invalid raw: No id" }.audioUri)
        )

    /**
     * Get the song that this task is trying to complete. If the task is still busy, this will
     * return null. Otherwise, it will return a song.
     */
    fun get(): Song.Raw? {
        if (!future.isDone) {
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

        // Populate the format mime type if we have one.
        format.sampleMimeType?.let { raw.formatMimeType = it }

        val metadata = format.metadata
        if (metadata != null) {
            completeRawSong(metadata)
        } else {
            logD("No metadata could be extracted for ${raw.name}")
        }

        return raw
    }

    private fun completeRawSong(metadata: Metadata) {
        val id3v2Tags = mutableMapOf<String, List<String>>()
        val vorbisTags = mutableMapOf<String, MutableList<String>>()

        // ExoPlayer only exposes ID3v2 and Vorbis metadata, which constitutes the vast majority
        // of audio formats. Load both of these types of tags into separate maps, letting the
        // "source of truth" be the last of a particular tag in a file.
        for (i in 0 until metadata.length()) {
            when (val tag = metadata[i]) {
                is TextInformationFrame -> {
                    val id = tag.description?.let { "TXXX:${it.sanitize()}" } ?: tag.id.sanitize()
                    val values = tag.values.map { it.sanitize() }
                    if (values.isNotEmpty() && values.all { it.isNotEmpty() }) {
                        id3v2Tags[id] = values
                    }
                }
                is VorbisComment -> {
                    // Vorbis comment keys can be in any case, make them uppercase for simplicity.
                    val id = tag.key.sanitize().uppercase()
                    val value = tag.value.sanitize()
                    if (value.isNotEmpty()) {
                        vorbisTags.getOrPut(id) { mutableListOf() }.add(value)
                    }
                }
            }
        }

        when {
            vorbisTags.isEmpty() -> populateId3v2(id3v2Tags)
            id3v2Tags.isEmpty() -> populateVorbis(vorbisTags)
            else -> {
                // Some formats (like FLAC) can contain both ID3v2 and Vorbis, so we apply
                // them both with priority given to vorbis.
                populateId3v2(id3v2Tags)
                populateVorbis(vorbisTags)
            }
        }
    }

    private fun populateId3v2(tags: Map<String, List<String>>) {
        // Song
        tags["TXXX:MusicBrainz Release Track Id"]?.let { raw.musicBrainzId = it[0] }
        tags["TIT2"]?.let { raw.name = it[0] }
        tags["TSOT"]?.let { raw.sortName = it[0] }

        // Track, as NN/TT
        tags["TRCK"]?.run { get(0).parsePositionNum() }?.let { raw.track = it }

        // Disc, as NN/TT
        tags["TPOS"]?.run { get(0).parsePositionNum() }?.let { raw.disc = it }

        // Dates are somewhat complicated, as not only did their semantics change from a flat year
        // value in ID3v2.3 to a full ISO-8601 date in ID3v2.4, but there are also a variety of
        // date types.
        // Our hierarchy for dates is as such:
        // 1. ID3v2.4 Original Date, as it resolves the "Released in X, Remastered in Y" issue
        // 2. ID3v2.4 Recording Date, as it is the most common date type
        // 3. ID3v2.4 Release Date, as it is the second most common date type
        // 4. ID3v2.3 Original Date, as it is like #1
        // 5. ID3v2.3 Release Year, as it is the most common date type
        (
            tags["TDOR"]?.run { get(0).parseTimestamp() }
                ?: tags["TDRC"]?.run { get(0).parseTimestamp() }
                ?: tags["TDRL"]?.run { get(0).parseTimestamp() } ?: parseId3v23Date(tags)
            )
            ?.let { raw.date = it }

        // Album
        tags["TXXX:MusicBrainz Album Id"]?.let { raw.albumMusicBrainzId = it[0] }
        tags["TALB"]?.let { raw.albumName = it[0] }
        tags["TSOA"]?.let { raw.albumSortName = it[0] }
        (tags["TXXX:MusicBrainz Album Type"] ?: tags["GRP1"])?.let {
            raw.albumReleaseTypes = it
        }

        // Artist
        tags["TXXX:MusicBrainz Artist Id"]?.let { raw.artistMusicBrainzIds = it }
        tags["TPE1"]?.let { raw.artistNames = it }
        tags["TSOP"]?.let { raw.artistSortNames = it }

        // Album artist
        tags["TXXX:MusicBrainz Album Artist Id"]?.let { raw.albumArtistMusicBrainzIds = it }
        tags["TPE2"]?.let { raw.albumArtistNames = it }
        tags["TSO2"]?.let { raw.albumArtistSortNames = it }

        // Genre
        tags["TCON"]?.let { raw.genreNames = it }
    }

    private fun parseId3v23Date(tags: Map<String, List<String>>): Date? {
        val year =
            tags["TORY"]?.run { get(0).toIntOrNull() }
                ?: tags["TYER"]?.run { get(0).toIntOrNull() } ?: return null

        // Assume that TDAT/TIME can refer to TYER or TORY depending on if TORY
        // is present.

        val tdat = tags["TDAT"]
        return if (tdat != null && tdat[0].length == 4 && tdat[0].isDigitsOnly()) {
            val mm = tdat[0].substring(0..1).toInt()
            val dd = tdat[0].substring(2..3).toInt()

            val time = tags["TIME"]
            if (time != null && time[0].length == 4 && time[0].isDigitsOnly()) {
                val hh = time[0].substring(0..1).toInt()
                val mi = time[0].substring(2..3).toInt()
                Date.from(year, mm, dd, hh, mi)
            } else {
                Date.from(year, mm, dd)
            }
        } else {
            return Date.from(year)
        }
    }

    private fun populateVorbis(tags: Map<String, List<String>>) {
        // Song
        tags["MUSICBRAINZ_RELEASETRACKID"]?.let { raw.musicBrainzId = it[0] }
        tags["TITLE"]?.let { raw.name = it[0] }
        tags["TITLESORT"]?.let { raw.sortName = it[0] }

        // Track
        tags["TRACKNUMBER"]?.run { get(0).parsePositionNum() }?.let { raw.track = it }

        // Disc
        tags["DISCNUMBER"]?.run { get(0).parsePositionNum() }?.let { raw.disc = it }

        // Vorbis dates are less complicated, but there are still several types
        // Our hierarchy for dates is as such:
        // 1. Original Date, as it solves the "Released in X, Remastered in Y" issue
        // 2. Date, as it is the most common date type
        // 3. Year, as old vorbis tags tended to use this (I know this because it's the only
        // tag that android supports, so it must be 15 years old or more!)
        (
            tags["ORIGINALDATE"]?.run { get(0).parseTimestamp() }
                ?: tags["DATE"]?.run { get(0).parseTimestamp() }
                ?: tags["YEAR"]?.run { get(0).parseYear() }
            )
            ?.let { raw.date = it }

        // Album
        tags["MUSICBRAINZ_ALBUMID"]?.let { raw.albumMusicBrainzId = it[0] }
        tags["ALBUM"]?.let { raw.albumName = it[0] }
        tags["ALBUMSORT"]?.let { raw.albumSortName = it[0] }
        tags["RELEASETYPE"]?.let { raw.albumReleaseTypes = it }

        // Artist
        tags["MUSICBRAINZ_ARTISTID"]?.let { raw.artistMusicBrainzIds = it }
        tags["ARTIST"]?.let { raw.artistNames = it }
        tags["ARTISTSORT"]?.let { raw.artistSortNames = it }

        // Album artist
        tags["MUSICBRAINZ_ALBUMARTISTID"]?.let { raw.albumArtistMusicBrainzIds = it }
        tags["ALBUMARTIST"]?.let { raw.albumArtistNames = it }
        tags["ALBUMARTISTSORT"]?.let { raw.albumArtistSortNames = it }

        // Genre
        tags["GENRE"]?.let { raw.genreNames = it }
    }

    /**
     * Copies and sanitizes this string under the assumption that it is UTF-8.
     *
     * Sometimes ExoPlayer emits weird UTF-8. Worse still, sometimes it emits strings backed by data
     * allocated by some native function. This could easily cause a terrible crash if you even look
     * at the malformed string the wrong way.
     *
     * This function mitigates it by first encoding the string as UTF-8 bytes (replacing malformed
     * characters with the replacement in the process), and then re-interpreting it as a new string,
     * which hopefully fixes encoding insanity while also copying the string out of dodgy native
     * memory.
     */
    private fun String.sanitize() = String(encodeToByteArray())
}

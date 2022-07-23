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
 
package org.oxycblt.auxio.music.system

import android.content.Context
import android.database.Cursor
import androidx.core.text.isDigitsOnly
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import org.oxycblt.auxio.music.Date
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.audioUri
import org.oxycblt.auxio.music.parseId3GenreName
import org.oxycblt.auxio.music.parsePositionNum
import org.oxycblt.auxio.music.parseReleaseType
import org.oxycblt.auxio.music.parseTimestamp
import org.oxycblt.auxio.music.parseYear
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * A [Indexer.Backend] that leverages ExoPlayer's metadata retrieval system to index metadata.
 *
 * Normally, leveraging ExoPlayer's metadata system would be a terrible idea, as it is horrifically
 * slow. However, if we parallelize it, we can get similar throughput to other metadata extractors,
 * which is nice as it means we don't have to bundle a redundant metadata library like JAudioTagger.
 *
 * Now, ExoPlayer's metadata API is not the best. It's opaque, undocumented, and prone to weird
 * pitfalls given ExoPlayer's cozy relationship with native code. However, this backend should do
 * enough to eliminate such issues.
 *
 * @author OxygenCobalt
 */
class ExoPlayerBackend(private val inner: MediaStoreBackend) : Indexer.Backend {
    private val runningTasks: Array<Task?> = arrayOfNulls(TASK_CAPACITY)

    // No need to implement our own query logic, as this backend is still reliant on
    // MediaStore.
    override fun query(context: Context) = inner.query(context)

    override fun buildSongs(
        context: Context,
        cursor: Cursor,
        emitIndexing: (Indexer.Indexing) -> Unit
    ): List<Song> {
        // Metadata retrieval with ExoPlayer is asynchronous, so a callback may at any point
        // add a completed song to the list. To prevent a crash in that case, we use the
        // concurrent counterpart to a typical mutable list.
        val songs = mutableListOf<Song>()
        val total = cursor.count

        while (cursor.moveToNext()) {
            // Note: This call to buildAudio does not populate the genre field. This is
            // because indexing genres is quite slow with MediaStore, and so keeping the
            // field blank on unsupported ExoPlayer formats ends up being preferable.
            val audio = inner.buildAudio(context, cursor)

            // Spin until there is an open slot we can insert a task in. Note that we do
            // not add callbacks to our new tasks, as Future callbacks run on a different
            // executor and thus will crash the app if an error occurs instead of bubbling
            // back up to Indexer.
            spin@ while (true) {
                for (i in runningTasks.indices) {
                    val task = runningTasks[i]

                    if (task != null) {
                        val song = task.get()
                        if (song != null) {
                            songs.add(song)
                            emitIndexing(Indexer.Indexing.Songs(songs.size, total))
                            runningTasks[i] = Task(context, audio)
                            break@spin
                        }
                    } else {
                        runningTasks[i] = Task(context, audio)
                        break@spin
                    }
                }
            }
        }

        spin@ while (true) {
            // Spin until all of the remaining tasks are complete.
            for (i in runningTasks.indices) {
                val task = runningTasks[i]

                if (task != null) {
                    val song = task.get() ?: continue@spin
                    songs.add(song)
                    emitIndexing(Indexer.Indexing.Songs(songs.size, total))
                    runningTasks[i] = null
                }
            }

            break
        }

        return songs
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
class Task(context: Context, private val audio: MediaStoreBackend.Audio) {
    private val future =
        MetadataRetriever.retrieveMetadata(
            context,
            MediaItem.fromUri(requireNotNull(audio.id) { "Malformed audio: No id" }.audioUri))

    /**
     * Get the song that this task is trying to complete. If the task is still busy, this will
     * return null. Otherwise, it will return a song.
     */
    fun get(): Song? {
        if (!future.isDone) {
            return null
        }

        val format =
            try {
                future.get()[0].getFormat(0)
            } catch (e: Exception) {
                logW("Unable to extract metadata for ${audio.title}")
                logW(e.stackTraceToString())
                null
            }

        if (format == null) {
            logD("Nothing could be extracted for ${audio.title}")
            return audio.toSong()
        }

        // Populate the format mime type if we have one.
        format.sampleMimeType?.let { audio.formatMimeType = it }

        val metadata = format.metadata
        if (metadata != null) {
            completeAudio(metadata)
        } else {
            logD("No metadata could be extracted for ${audio.title}")
        }

        return audio.toSong()
    }

    private fun completeAudio(metadata: Metadata) {
        val id3v2Tags = mutableMapOf<String, String>()
        val vorbisTags = mutableMapOf<String, MutableList<String>>()

        // ExoPlayer only exposes ID3v2 and Vorbis metadata, which constitutes the vast majority
        // of audio formats. Load both of these types of tags into separate maps, letting the
        // "source of truth" be the last of a particular tag in a file.
        for (i in 0 until metadata.length()) {
            when (val tag = metadata[i]) {
                is TextInformationFrame -> {
                    val id = tag.description?.let { "TXXX:${it.sanitize()}" } ?: tag.id.sanitize()
                    val value = tag.value.sanitize()
                    if (value.isNotEmpty()) {
                        id3v2Tags[id] = value
                    }
                }
                is VorbisComment -> {
                    // Vorbis comment keys can be in any case, make them uppercase for simplicity.
                    val id = tag.key.sanitize().uppercase()
                    val value = tag.value.sanitize()
                    if (value.isNotEmpty()) {
                        if (vorbisTags.containsKey(id)) {
                            vorbisTags[id]!!.add(value)
                        } else {
                            vorbisTags[id] = mutableListOf(value)
                        }
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

    private fun populateId3v2(tags: Map<String, String>) {
        // (Sort) Title
        tags["TIT2"]?.let { audio.title = it }
        tags["TSOT"]?.let { audio.sortTitle = it }

        // Track, as NN/TT
        tags["TRCK"]?.parsePositionNum()?.let { audio.track = it }

        // Disc, as NN/TT
        tags["TPOS"]?.parsePositionNum()?.let { audio.disc = it }

        // Dates are somewhat complicated, as not only did their semantics change from a flat year
        // value in ID3v2.3 to a full ISO-8601 date in ID3v2.4, but there are also a variety of
        // date types.
        // Our hierarchy for dates is as such:
        // 1. ID3v2.4 Original Date, as it resolves the "Released in X, Remastered in Y" issue
        // 2. ID3v2.4 Recording Date, as it is the most common date type
        // 3. ID3v2.4 Release Date, as it is the second most common date type
        // 4. ID3v2.3 Original Date, as it is like #1
        // 5. ID3v2.3 Release Year, as it is the most common date type
        (tags["TDOR"]?.parseTimestamp()
                ?: tags["TDRC"]?.parseTimestamp() ?: tags["TDRL"]?.parseTimestamp()
                    ?: parseId3v23Date(tags))
            ?.let { audio.date = it }

        // (Sort) Album
        tags["TALB"]?.let { audio.album = it }
        tags["TSOA"]?.let { audio.sortAlbum = it }

        // (Sort) Artist
        tags["TPE1"]?.let { audio.artist = it }
        tags["TSOP"]?.let { audio.sortArtist = it }

        // (Sort) Album artist
        tags["TPE2"]?.let { audio.albumArtist = it }
        tags["TSO2"]?.let { audio.sortAlbumArtist = it }

        // Genre, with the weird ID3 rules.
        tags["TCON"]?.let { audio.genre = it.parseId3GenreName() }

        // Release type (GRP1 is sometimes used for this, so fall back to it)
        (tags["TXXX:MusicBrainz Album Type"] ?: tags["GRP1"])?.parseReleaseType()?.let {
            audio.releaseType = it
        }
    }

    private fun parseId3v23Date(tags: Map<String, String>): Date? {
        val year = tags["TORY"]?.toIntOrNull() ?: tags["TYER"]?.toIntOrNull() ?: return null

        val mmdd = tags["TDAT"]
        return if (mmdd != null && mmdd.length == 4 && mmdd.isDigitsOnly()) {
            val mm = mmdd.substring(0..1).toInt()
            val dd = mmdd.substring(2..3).toInt()

            val hhmi = tags["TIME"]
            if (hhmi != null && hhmi.length == 4 && hhmi.isDigitsOnly()) {
                val hh = hhmi.substring(0..1).toInt()
                val mi = hhmi.substring(2..3).toInt()
                Date.from(year, mm, dd, hh, mi)
            } else {
                Date.from(year, mm, dd)
            }
        } else {
            return Date.from(year)
        }
    }

    private fun populateVorbis(tags: Map<String, List<String>>) {
        // (Sort) Title
        tags["TITLE"]?.let { audio.title = it[0] }
        tags["TITLESORT"]?.let { audio.sortTitle = it[0] }

        // Track
        tags["TRACKNUMBER"]?.run { get(0).parsePositionNum() }?.let { audio.track = it }

        // Disc
        tags["DISCNUMBER"]?.run { get(0).parsePositionNum() }?.let { audio.disc = it }

        // Vorbis dates are less complicated, but there are still several types
        // Our hierarchy for dates is as such:
        // 1. Original Date, as it solves the "Released in X, Remastered in Y" issue
        // 2. Date, as it is the most common date type
        // 3. Year, as old vorbis tags tended to use this (I know this because it's the only
        // tag that android supports, so it must be 15 years old or more!)
        (tags["ORIGINALDATE"]?.run { get(0).parseTimestamp() }
                ?: tags["DATE"]?.run { get(0).parseTimestamp() }
                    ?: tags["YEAR"]?.run { get(0).parseYear() })
            ?.let { audio.date = it }

        // (Sort) Album
        tags["ALBUM"]?.let { audio.album = it.joinToString() }
        tags["ALBUMSORT"]?.let { audio.sortAlbum = it.joinToString() }

        // (Sort) Artist
        tags["ARTIST"]?.let { audio.artist = it.joinToString() }
        tags["ARTISTSORT"]?.let { audio.sortArtist = it.joinToString() }

        // (Sort) Album artist
        tags["ALBUMARTIST"]?.let { audio.albumArtist = it.joinToString() }
        tags["ALBUMARTISTSORT"]?.let { audio.sortAlbumArtist = it.joinToString() }

        // Genre, no ID3 rules here
        tags["GENRE"]?.let { audio.genre = it.joinToString() }

        // Release type
        tags["RELEASETYPE"]?.parseReleaseType()?.let { audio.releaseType = it }
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

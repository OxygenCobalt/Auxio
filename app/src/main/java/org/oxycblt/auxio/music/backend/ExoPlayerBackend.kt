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
 
package org.oxycblt.auxio.music.backend

import android.content.Context
import android.database.Cursor
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import org.oxycblt.auxio.music.Indexer
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.audioUri
import org.oxycblt.auxio.music.id3GenreName
import org.oxycblt.auxio.music.iso8601year
import org.oxycblt.auxio.music.no
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
 *
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

        val metadata =
            try {
                future.get()[0].getFormat(0).metadata
            } catch (e: Exception) {
                logW("Unable to extract metadata for ${audio.title}")
                logW(e.stackTraceToString())
                null
            }

        if (metadata != null) {
            completeAudio(metadata)
        } else {
            logD("No metadata could be extracted for ${audio.title}")
        }

        return audio.toSong()
    }

    private fun completeAudio(metadata: Metadata) {
        // ExoPlayer only exposes ID3v2 and Vorbis metadata, which constitutes the vast majority
        // of audio formats. Some formats (like FLAC) can contain both ID3v2 and vorbis tags, but
        // this isn't too big of a deal, as we generally let the "source of truth" for metadata
        // be the last instance of a particular tag in a file.
        for (i in 0 until metadata.length()) {
            when (val tag = metadata[i]) {
                is TextInformationFrame -> populateWithId3v2(tag)
                is VorbisComment -> populateWithVorbis(tag)
            }
        }
    }

    private fun populateWithId3v2(frame: TextInformationFrame) {
        val id = frame.id.sanitize()
        val value = frame.value.sanitize()
        if (value.isEmpty()) {
            return
        }

        when (id) {
            // Title
            "TIT2" -> audio.title = value
            // Track, as NN/TT
            "TRCK" -> value.no?.let { audio.track = it }
            // Disc, as NN/TT
            "TPOS" -> value.no?.let { audio.disc = it }
            // ID3v2.3 year, should be digits
            "TYER" -> value.toIntOrNull()?.let { audio.year = it }
            // ID3v2.4 year, parse as ISO-8601
            "TDRC" -> value.iso8601year?.let { audio.year = it }
            // Album
            "TALB" -> audio.album = value
            // Artist
            "TPE1" -> audio.artist = value
            // Album artist
            "TPE2" -> audio.albumArtist = value
            // Genre, with the weird ID3 rules
            "TCON" -> audio.genre = value.id3GenreName
        }
    }

    private fun populateWithVorbis(comment: VorbisComment) {
        val key = comment.key.sanitize()
        val value = comment.value.sanitize()
        if (value.isEmpty()) {
            return
        }

        when (key) {
            // Title
            "TITLE" -> audio.title = value
            // Track, might be NN/TT
            "TRACKNUMBER" -> value.no?.let { audio.track = it }
            // Disc, might be NN/TT
            "DISCNUMBER" -> value.no?.let { audio.disc = it }
            // Date, presumably as ISO-8601
            "DATE" -> value.iso8601year?.let { audio.year = it }
            // Album
            "ALBUM" -> audio.album = value
            // Artist
            "ARTIST" -> audio.artist = value
            // Album artist
            "ALBUMARTIST" -> audio.albumArtist = value
            // Genre, assumed that ID3 rules do not apply here.
            "GENRE" -> audio.genre = value
        }
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

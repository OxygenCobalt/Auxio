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
 
package org.oxycblt.auxio.music.indexer

import android.content.Context
import android.database.Cursor
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.Future
import org.oxycblt.auxio.music.Song
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
 * TODO: This class is currently not used, as there are a number of technical improvements that must
 * be made first before it can be integrated.
 *
 * @author OxygenCobalt
 */
@Suppress("UNUSED")
class ExoPlayerBackend(private val inner: MediaStoreBackend) : Indexer.Backend {
    private val runningTasks: Array<Future<TrackGroupArray>?> = arrayOfNulls(TASK_CAPACITY)

    override fun query(context: Context) = inner.query(context)

    override fun loadSongs(context: Context, cursor: Cursor): Collection<Song> {
        // Metadata retrieval with ExoPlayer is asynchronous, so a callback may at any point
        // add a completed song to the list. To prevent a crash in that case, we use the
        // concurrent counterpart to a typical mutable list.
        val songs = ConcurrentLinkedQueue<Song>()

        while (cursor.moveToNext()) {
            val audio = inner.buildAudio(context, cursor)

            val audioUri = requireNotNull(audio.id) { "Malformed audio: No id" }.audioUri

            while (true) {
                // Spin until a task slot opens up, then start trying to parse metadata.
                val index = runningTasks.indexOfFirst { it == null }
                if (index != -1) {
                    val task =
                        MetadataRetriever.retrieveMetadata(context, MediaItem.fromUri(audioUri))

                    Futures.addCallback(
                        task,
                        AudioCallback(audio, songs, index),
                        Executors.newSingleThreadExecutor())

                    runningTasks[index] = task

                    break
                }
            }
        }

        while (runningTasks.any { it != null }) {
            // Spin until all tasks are complete
        }

        return songs
    }

    private inner class AudioCallback(
        private val audio: MediaStoreBackend.Audio,
        private val dest: ConcurrentLinkedQueue<Song>,
        private val taskIndex: Int
    ) : FutureCallback<TrackGroupArray> {
        override fun onSuccess(result: TrackGroupArray) {
            val metadata = result[0].getFormat(0).metadata
            if (metadata != null) {
                completeAudio(audio, metadata)
            } else {
                logW("No metadata was found for ${audio.title}")
            }

            dest.add(audio.toSong())
            runningTasks[taskIndex] = null
        }

        override fun onFailure(t: Throwable) {
            logW("Unable to extract metadata for ${audio.title}")
            logW(t.stackTraceToString())
            dest.add(audio.toSong())
            runningTasks[taskIndex] = null
        }
    }

    private fun completeAudio(audio: MediaStoreBackend.Audio, metadata: Metadata) {
        for (i in 0 until metadata.length()) {
            // We only support two formats as it stands:
            // - ID3v2 text frames
            // - Vorbis comments
            // This should be enough to cover the vast, vast majority of audio formats.
            // It is also assumed that a file only has either ID3v2 text frames or vorbis
            // comments.
            when (val tag = metadata.get(i)) {
                is TextInformationFrame ->
                    if (tag.value.isNotEmpty()) {
                        handleId3v2TextFrame(tag.id.sanitize(), tag.value.sanitize(), audio)
                    }
                is VorbisComment ->
                    if (tag.value.isNotEmpty()) {
                        handleVorbisComment(tag.key.sanitize(), tag.value.sanitize(), audio)
                    }
            }
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

    private fun handleId3v2TextFrame(id: String, value: String, audio: MediaStoreBackend.Audio) {
        // It's assumed that duplicate frames are eliminated by ExoPlayer's metadata parser.
        when (id) {
            "TIT2" -> audio.title = value // Title
            "TRCK" -> value.no?.let { audio.track = it } // Track, as NN/TT
            "TPOS" -> value.no?.let { audio.disc = it } // Disc, as NN/TT
            "TYER" -> value.toIntOrNull()?.let { audio.year = it } // ID3v2.3 year, should be digits
            "TDRC" -> value.iso8601year?.let { audio.year = it } // ID3v2.4 date, parse year field
            "TALB" -> audio.album = value // Album
            "TPE1" -> audio.artist = value // Artist
            "TPE2" -> audio.albumArtist = value // Album artist
            "TCON" -> audio.genre = value // Genre, with the weird ID3v2 rules
        }
    }

    private fun handleVorbisComment(key: String, value: String, audio: MediaStoreBackend.Audio) {
        // It's assumed that duplicate tags are eliminated by ExoPlayer's metadata parser.
        when (key) {
            "TITLE" -> audio.title = value // Title, presumably as NN/TT
            "TRACKNUMBER" -> value.no?.let { audio.track = it } // Track, presumably as NN/TT
            "DISCNUMBER" -> value.no?.let { audio.disc = it } // Disc, presumably as NN/TT
            "DATE" -> value.iso8601year?.let { audio.year = it } // Date, presumably as ISO-8601
            "ALBUM" -> audio.album = value // Album
            "ARTIST" -> audio.artist = value // Artist
            "ALBUMARTIST" -> audio.albumArtist = value // Album artist
            "GENRE" -> audio.genre = value // Genre, assumed that ID3v2 rules will apply here too.
        }
    }

    companion object {
        /**
         * The amount of tasks this backend can run efficiently at one time. Eight was chosen here
         * as higher values made little difference in speed, and lower values generally caused
         * bottlenecks.
         */
        private const val TASK_CAPACITY = 8
    }
}

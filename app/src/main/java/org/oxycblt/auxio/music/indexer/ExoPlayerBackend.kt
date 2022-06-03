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
import java.util.concurrent.Future
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.oxycblt.auxio.music.MusicStore
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

    // No need to implement our own query logic, as this backend is still reliant on
    // MediaStore.
    override fun query(context: Context) = inner.query(context)

    override fun loadSongs(
        context: Context,
        cursor: Cursor,
        callback: MusicStore.LoadCallback
    ): Collection<Song> {
        // Metadata retrieval with ExoPlayer is asynchronous, so a callback may at any point
        // add a completed song to the list. To prevent a crash in that case, we use the
        // concurrent counterpart to a typical mutable list.
        val songs = ConcurrentLinkedQueue<Song>()

        while (cursor.moveToNext()) {
            // Note: This call to buildAudio does not populate the genre field. This is
            // because indexing genres is quite slow with MediaStore, and so keeping the
            // field blank on unsupported ExoPlayer formats ends up being preferable.
            val audio = inner.buildAudio(context, cursor)
            val audioUri = requireNotNull(audio.id) { "Malformed audio: No id" }.audioUri

            while (true) {
                // In order to properly parallelize ExoPlayer's parser, we have an array containing
                // a few slots for ongoing futures. As soon as a finished task opens up their slot,
                // we begin loading metadata for this audio.
                val index = runningTasks.indexOfFirst { it == null }
                if (index != -1) {
                    val task =
                        MetadataRetriever.retrieveMetadata(context, MediaItem.fromUri(audioUri))

                    Futures.addCallback(
                        task,
                        AudioCallback(audio) {
                            runningTasks[index] = null
                            songs.add(it)
                            callback.onLoadStateChanged(
                                MusicStore.LoadState.Indexing(songs.size, cursor.count))
                        },
                        // Normal JVM dispatcher will suffice here, as there is no IO work
                        // going on (and there is no cost from switching contexts with executors)
                        Dispatchers.Default.asExecutor())

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
        private val onComplete: (Song) -> Unit,
    ) : FutureCallback<TrackGroupArray> {
        override fun onSuccess(result: TrackGroupArray) {
            val metadata = result[0].getFormat(0).metadata

            if (metadata != null) {
                completeAudio(audio, metadata)
            } else {
                logW("No metadata was found for ${audio.title}")
            }

            onComplete(audio.toSong())
        }

        override fun onFailure(t: Throwable) {
            logW("Unable to extract metadata for ${audio.title}")
            logW(t.stackTraceToString())
            onComplete(audio.toSong())
        }
    }

    private fun completeAudio(audio: MediaStoreBackend.Audio, metadata: Metadata) {
        // ExoPlayer only exposes ID3v2 and Vorbis metadata, which constitutes the vast majority
        // of audio formats. Some formats (like FLAC) can contain both ID3v2 and vorbis tags, but
        // this isn't too big of a deal, as we generally let the "source of truth" for metadata
        // be the last instance of a particular tag in a file.
        for (i in 0 until metadata.length()) {
            when (val tag = metadata[i]) {
                is TextInformationFrame -> populateWithId3v2(audio, tag)
                is VorbisComment -> populateWithVorbis(audio, tag)
            }
        }
    }

    private fun populateWithId3v2(audio: MediaStoreBackend.Audio, frame: TextInformationFrame) {
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
            // Disc
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
            // Genre, with the weird ID3v2 rules
            "TCON" -> audio.genre = value
        }
    }

    private fun populateWithVorbis(audio: MediaStoreBackend.Audio, comment: VorbisComment) {
        val key = comment.key.sanitize()
        val value = comment.value.sanitize()
        if (value.isEmpty()) {
            return
        }

        when (key) {
            // Title
            "TITLE" -> audio.title = value
            // Track, presumably as NN/TT
            "TRACKNUMBER" -> value.no?.let { audio.track = it }
            // Disc, presumably as NN/TT
            "DISCNUMBER" -> value.no?.let { audio.disc = it }
            // Date, presumably as ISO-8601
            "DATE" -> value.iso8601year?.let { audio.year = it }
            // Album
            "ALBUM" -> audio.album = value
            // Artist
            "ARTIST" -> audio.artist = value
            // Album artist
            "ALBUMARTIST" -> audio.albumArtist = value
            // Genre, assumed that ID3v2 rules will apply here too.
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

    companion object {
        /** The amount of tasks this backend can run efficiently at once. */
        private const val TASK_CAPACITY = 8
    }
}

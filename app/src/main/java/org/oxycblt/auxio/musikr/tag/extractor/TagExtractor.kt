/*
 * Copyright (c) 2023 Auxio Project
 * TagExtractor.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.tag.extractor

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Timeline
import androidx.media3.common.util.Clock
import androidx.media3.common.util.HandlerWrapper
import androidx.media3.exoplayer.LoadingInfo
import androidx.media3.exoplayer.analytics.PlayerId
import androidx.media3.exoplayer.source.MediaPeriod
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.upstream.Allocator
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.asDeferred
import org.oxycblt.auxio.musikr.tag.AudioFile
import org.oxycblt.auxio.musikr.fs.DeviceFile
import timber.log.Timber as L

interface TagExtractor {
    fun extract(deviceFiles: Flow<DeviceFile>): Flow<AudioFile>
}

class TagExtractorImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val mediaSourceFactory: MediaSource.Factory,
) : TagExtractor {
    override fun extract(deviceFiles: Flow<DeviceFile>) = flow {
        val retriever = ChunkedMetadataRetriever(mediaSourceFactory)
        deviceFiles.collect { deviceFile ->
            //            val exoPlayerMetadataFuture =
            //                MetadataRetriever.retrieveMetadata(
            //                    mediaSourceFactory, MediaItem.fromUri(deviceFile.uri))
            val exoPlayerMetadataFuture = retriever.retrieve(deviceFile.uri)
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, deviceFile.uri)
            val exoPlayerMetadata = exoPlayerMetadataFuture.asDeferred().await()
            val result = extractTags(deviceFile, exoPlayerMetadata, mediaMetadataRetriever)
            mediaMetadataRetriever.close()
            emit(result)
        }
        retriever.release()
    }

    private fun extractTags(
        input: DeviceFile,
        output: TrackGroupArray,
        retriever: MediaMetadataRetriever
    ): AudioFile {
        if (output.isEmpty) return defaultAudioFile(input, retriever)
        val track = output.get(0)
        if (track.length == 0) return defaultAudioFile(input, retriever)
        val format = track.getFormat(0)
        val metadata = format.metadata ?: return defaultAudioFile(input, retriever)
        val textTags = TextTags(metadata)
        return AudioFile(
            deviceFile = input,
            durationMs =
                need(
                    retriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong(),
                    "duration"),
            replayGainTrackAdjustment = textTags.replayGainTrackAdjustment(),
            replayGainAlbumAdjustment = textTags.replayGainAlbumAdjustment(),
            musicBrainzId = textTags.musicBrainzId(),
            name = need(textTags.name() ?: input.path.name, "name"),
            sortName = textTags.sortName(),
            track = textTags.track(),
            disc = textTags.disc(),
            subtitle = textTags.subtitle(),
            date = textTags.date(),
            albumMusicBrainzId = textTags.albumMusicBrainzId(),
            albumName = textTags.albumName(),
            albumSortName = textTags.albumSortName(),
            releaseTypes = textTags.releaseTypes() ?: listOf(),
            artistMusicBrainzIds = textTags.artistMusicBrainzIds() ?: listOf(),
            artistNames = textTags.artistNames() ?: listOf(),
            artistSortNames = textTags.artistSortNames() ?: listOf(),
            albumArtistMusicBrainzIds = textTags.albumArtistMusicBrainzIds() ?: listOf(),
            albumArtistNames = textTags.albumArtistNames() ?: listOf(),
            albumArtistSortNames = textTags.albumArtistSortNames() ?: listOf(),
            genreNames = textTags.genreNames() ?: listOf())
    }

    private fun defaultAudioFile(
        deviceFile: DeviceFile,
        metadataRetriever: MediaMetadataRetriever
    ) =
        AudioFile(
            deviceFile,
            name = need(deviceFile.path.name, "name"),
            durationMs =
                need(
                    metadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong(),
                    "duration"),
        )

    private fun <T> need(a: T, called: String) =
        requireNotNull(a) { "Invalid tag, missing $called" }
}

private const val MESSAGE_PREPARE = 0
private const val MESSAGE_CONTINUE_LOADING = 1
private const val MESSAGE_CHECK_FAILURE = 2
private const val MESSAGE_RELEASE = 3
private const val CHECK_INTERVAL_MS = 100

/**
 * Patched version of Media3's MetadataRetriever that extracts metadata from several tracks at once
 * on one thread. This is generally more efficient than stacking several threads at once.
 *
 * @author Media3 Team, Alexander Capehart (OxygenCobalt)
 */
private class ChunkedMetadataRetriever(private val mediaSourceFactory: MediaSource.Factory) :
    Handler.Callback {
    private val mediaSourceThread = HandlerThread("Auxio:ChunkedMetadataRetriever:${hashCode()}")
    private val mediaSourceHandler: HandlerWrapper
    private var job: MetadataJob? = null

    private data class JobParams(val uri: Uri, val future: SettableFuture<TrackGroupArray>)

    private class JobData(
        val params: JobParams,
        val mediaSource: MediaSource,
        var mediaPeriod: MediaPeriod?,
    )

    private class MetadataJob(val data: JobData, val mediaSourceCaller: MediaSourceCaller)

    init {
        mediaSourceThread.start()
        mediaSourceHandler = Clock.DEFAULT.createHandler(mediaSourceThread.looper, this)
    }

    fun retrieve(uri: Uri): ListenableFuture<TrackGroupArray> {
        val job = job
        check(job == null || job.data.params.future.isDone) { "Already working on something: $job" }
        val future = SettableFuture.create<TrackGroupArray>()
        mediaSourceHandler.obtainMessage(MESSAGE_PREPARE, JobParams(uri, future)).sendToTarget()
        return future
    }

    fun release() {
        mediaSourceHandler.removeCallbacksAndMessages(null)
        mediaSourceThread.quit()
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MESSAGE_PREPARE -> {
                val params = msg.obj as JobParams

                val mediaSource =
                    mediaSourceFactory.createMediaSource(MediaItem.fromUri(params.uri))
                val data = JobData(params, mediaSource, null)
                val mediaSourceCaller = MediaSourceCaller(data)
                mediaSource.prepareSource(
                    mediaSourceCaller, /* mediaTransferListener= */ null, PlayerId.UNSET)
                job = MetadataJob(data, mediaSourceCaller)

                mediaSourceHandler.sendEmptyMessageDelayed(
                    MESSAGE_CHECK_FAILURE, /* delayMs= */ CHECK_INTERVAL_MS
                )

                return true
            }
            MESSAGE_CONTINUE_LOADING -> {
                val job = job ?: return true
                checkNotNull(job.data.mediaPeriod)
                    .continueLoading(LoadingInfo.Builder().setPlaybackPositionUs(0).build())
                return true
            }
            MESSAGE_CHECK_FAILURE -> {
                val job = job ?: return true
                val mediaPeriod = job.data.mediaPeriod
                val mediaSource = job.data.mediaSource
                val mediaSourceCaller = job.mediaSourceCaller
                try {
                    if (mediaPeriod == null) {
                        mediaSource.maybeThrowSourceInfoRefreshError()
                    } else {
                        mediaPeriod.maybeThrowPrepareError()
                    }
                } catch (e: Exception) {
                    L.e("Failed to extract MediaSource")
                    L.e(e.stackTraceToString())
                    mediaPeriod?.let(mediaSource::releasePeriod)
                    mediaSource.releaseSource(mediaSourceCaller)
                    job.data.params.future.setException(e)
                }
                return true
            }
            MESSAGE_RELEASE -> {
                val job = job ?: return true
                val mediaPeriod = job.data.mediaPeriod
                val mediaSource = job.data.mediaSource
                val mediaSourceCaller = job.mediaSourceCaller
                mediaPeriod?.let { mediaSource.releasePeriod(it) }
                mediaSource.releaseSource(mediaSourceCaller)
                this.job = null
                return true
            }
            else -> return false
        }
    }

    private inner class MediaSourceCaller(private val data: JobData) :
        MediaSource.MediaSourceCaller {

        private val mediaPeriodCallback: MediaPeriodCallback =
            MediaPeriodCallback(data.params.future)
        private val allocator: Allocator =
            DefaultAllocator(
                /* trimOnReset= */ true,
                /* individualAllocationSize= */ C.DEFAULT_BUFFER_SEGMENT_SIZE)

        private var mediaPeriodCreated = false

        override fun onSourceInfoRefreshed(source: MediaSource, timeline: Timeline) {
            if (mediaPeriodCreated) {
                // Ignore dynamic updates.
                return
            }
            mediaPeriodCreated = true
            val mediaPeriod =
                source.createPeriod(
                    MediaSource.MediaPeriodId(timeline.getUidOfPeriod(/* periodIndex= */ 0)),
                    allocator,
                    /* startPositionUs= */ 0)
            data.mediaPeriod = mediaPeriod
            mediaPeriod.prepare(mediaPeriodCallback, /* positionUs= */ 0)
        }

        private inner class MediaPeriodCallback(
            private val future: SettableFuture<TrackGroupArray>
        ) : MediaPeriod.Callback {
            override fun onPrepared(mediaPeriod: MediaPeriod) {
                future.set(mediaPeriod.getTrackGroups())
                mediaSourceHandler.sendEmptyMessage(MESSAGE_RELEASE)
            }

            @Override
            override fun onContinueLoadingRequested(source: MediaPeriod) {
                mediaSourceHandler.sendEmptyMessage(MESSAGE_CONTINUE_LOADING)
            }
        }
    }
}

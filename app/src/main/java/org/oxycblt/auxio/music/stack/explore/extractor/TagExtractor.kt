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
 
package org.oxycblt.auxio.music.stack.explore.extractor

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
import androidx.media3.exoplayer.source.MediaSource.Factory
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
import org.oxycblt.auxio.music.stack.explore.AudioFile
import org.oxycblt.auxio.music.stack.explore.DeviceFile
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
            val exoPlayerMetadataFuture = retriever.push(deviceFile.uri)
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, deviceFile.uri)
            val exoPlayerMetadata = exoPlayerMetadataFuture.asDeferred().await()
            val result = extractTags(deviceFile, exoPlayerMetadata, mediaMetadataRetriever)
            mediaMetadataRetriever.close()
            emit(result)
        }
        retriever.stop()
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

private const val MESSAGE_CHECK_JOBS = 0
private const val MESSAGE_CONTINUE_LOADING = 1
private const val MESSAGE_RELEASE = 2
private const val MESSAGE_RELEASE_ALL = 3
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

    private class MetadataJob(
        val mediaItem: MediaItem,
        val future: SettableFuture<TrackGroupArray>,
        var mediaSource: MediaSource?,
        var mediaPeriod: MediaPeriod?,
        var mediaSourceCaller: MediaSourceCaller?
    )

    init {
        mediaSourceThread.start()
        mediaSourceHandler = Clock.DEFAULT.createHandler(mediaSourceThread.looper, this)
    }

    fun push(uri: Uri): ListenableFuture<TrackGroupArray> {
        val job = job
        check(job == null || job.future.isDone) { "Already working on something: $job" }
        val future = SettableFuture.create<TrackGroupArray>()
        this.job = MetadataJob(MediaItem.fromUri(uri), future, null, null, null)
        mediaSourceHandler.sendEmptyMessage(MESSAGE_CHECK_JOBS)
        return future
    }

    fun stop() {
        mediaSourceHandler.sendEmptyMessage(MESSAGE_RELEASE_ALL)
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MESSAGE_CHECK_JOBS -> {
                //                L.d("checking jobs")
                val job = job
                if (job != null) {
                    val currentMediaSource = job.mediaSource
                    val currentMediaSourceCaller = job.mediaSourceCaller
                    val mediaSource: MediaSource
                    val mediaSourceCaller: MediaSourceCaller
                    if (currentMediaSource != null && currentMediaSourceCaller != null) {
                        mediaSource = currentMediaSource
                        mediaSourceCaller = currentMediaSourceCaller
                    } else {
                        mediaSource = mediaSourceFactory.createMediaSource(job.mediaItem)
                        mediaSourceCaller = MediaSourceCaller(job)
                        mediaSource.prepareSource(
                            mediaSourceCaller, /* mediaTransferListener= */ null, PlayerId.UNSET)
                        job.mediaSource = mediaSource
                        job.mediaSourceCaller = mediaSourceCaller
                    }

                    try {
                        val mediaPeriod = job.mediaPeriod
                        if (mediaPeriod == null) {
                            mediaSource.maybeThrowSourceInfoRefreshError()
                        } else {
                            mediaPeriod.maybeThrowPrepareError()
                        }
                    } catch (e: Exception) {
                        L.e("Failed to extract MediaSource")
                        L.e(e.stackTraceToString())
                        job.mediaPeriod?.let(mediaSource::releasePeriod)
                        mediaSource.releaseSource(mediaSourceCaller)
                        job.future.setException(e)
                    }
                }

                mediaSourceHandler.sendEmptyMessageDelayed(
                    MESSAGE_CHECK_JOBS, /* delayMs= */ CHECK_INTERVAL_MS)

                return true
            }
            MESSAGE_CONTINUE_LOADING -> {
                checkNotNull((msg.obj as MetadataJob).mediaPeriod)
                    .continueLoading(LoadingInfo.Builder().setPlaybackPositionUs(0).build())
                return true
            }
            MESSAGE_RELEASE -> {
                val job = msg.obj as MetadataJob
                job.mediaPeriod?.let { job.mediaSource?.releasePeriod(it) }
                job.mediaSourceCaller?.let { job.mediaSource?.releaseSource(it) }
                this.job = null
                return true
            }
            MESSAGE_RELEASE_ALL -> {
                val job = job
                if (job != null) {
                    job.mediaPeriod?.let { job.mediaSource?.releasePeriod(it) }
                    job.mediaSourceCaller?.let { job.mediaSource?.releaseSource(it) }
                }
                mediaSourceHandler.removeCallbacksAndMessages(/* token= */ null)
                mediaSourceThread.quit()
                this.job = null
                return true
            }
            else -> return false
        }
    }

    private inner class MediaSourceCaller(private val job: MetadataJob) :
        MediaSource.MediaSourceCaller {

        private val mediaPeriodCallback: MediaPeriodCallback = MediaPeriodCallback(job)
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
            job.mediaPeriod = mediaPeriod
            mediaPeriod.prepare(mediaPeriodCallback, /* positionUs= */ 0)
        }

        private inner class MediaPeriodCallback(private val job: MetadataJob) :
            MediaPeriod.Callback {
            override fun onPrepared(mediaPeriod: MediaPeriod) {
                job.future.set(mediaPeriod.getTrackGroups())
                mediaSourceHandler.obtainMessage(MESSAGE_RELEASE, job).sendToTarget()
            }

            @Override
            override fun onContinueLoadingRequested(source: MediaPeriod) {
                mediaSourceHandler.obtainMessage(MESSAGE_CONTINUE_LOADING, job).sendToTarget()
            }
        }
    }
}

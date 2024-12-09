/*
 * Copyright (c) 2024 Auxio Project
 * ReusableMetadataRetriever.kt is part of Auxio.
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
 
package org.oxycblt.musikr.metadata

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
import com.google.common.util.concurrent.SettableFuture
import java.util.concurrent.Future
import javax.inject.Inject
import timber.log.Timber

private const val MESSAGE_PREPARE = 0
private const val MESSAGE_CONTINUE_LOADING = 1
private const val MESSAGE_CHECK_FAILURE = 2
private const val MESSAGE_RELEASE = 3
private const val CHECK_INTERVAL_MS = 100

// TODO: Rewrite and re-integrate

interface MetadataRetrieverExt {
    fun retrieveMetadata(mediaItem: MediaItem): Future<TrackGroupArray>

    fun retrieve()

    interface Factory {
        fun create(): MetadataRetrieverExt
    }
}

class ReusableMetadataRetrieverImpl
@Inject
constructor(private val mediaSourceFactory: MediaSource.Factory) :
    MetadataRetrieverExt, Handler.Callback {
    private val mediaSourceThread = HandlerThread("Auxio:ChunkedMetadataRetriever:${hashCode()}")
    private val mediaSourceHandler: HandlerWrapper
    private var job: MetadataJob? = null

    private data class JobParams(
        val mediaItem: MediaItem,
        val future: SettableFuture<TrackGroupArray>
    )

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

    override fun retrieveMetadata(mediaItem: MediaItem): Future<TrackGroupArray> {
        val job = job
        check(job == null || job.data.params.future.isDone) { "Already working on something: $job" }
        val future = SettableFuture.create<TrackGroupArray>()
        mediaSourceHandler
            .obtainMessage(MESSAGE_PREPARE, JobParams(mediaItem, future))
            .sendToTarget()
        return future
    }

    override fun retrieve() {
        mediaSourceHandler.removeCallbacksAndMessages(null)
        mediaSourceThread.quit()
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MESSAGE_PREPARE -> {
                val params = msg.obj as JobParams

                val mediaSource = mediaSourceFactory.createMediaSource(params.mediaItem)
                val data = JobData(params, mediaSource, null)
                val mediaSourceCaller = MediaSourceCaller(data)
                mediaSource.prepareSource(
                    mediaSourceCaller, /* mediaTransferListener= */ null, PlayerId.UNSET)
                job = MetadataJob(data, mediaSourceCaller)

                mediaSourceHandler.sendEmptyMessageDelayed(
                    MESSAGE_CHECK_FAILURE, /* delayMs= */ CHECK_INTERVAL_MS)

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
                    Timber.e("Failed to extract MediaSource")
                    Timber.e(e.stackTraceToString())
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

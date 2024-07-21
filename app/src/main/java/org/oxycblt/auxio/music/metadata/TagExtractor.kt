/*
 * Copyright (c) 2024 Auxio Project
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
 
package org.oxycblt.auxio.music.metadata

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
import com.google.common.util.concurrent.SettableFuture
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.fs.toAudioUri
import org.oxycblt.auxio.util.forEachWithTimeout
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.sendWithTimeout

class TagExtractor
@Inject
constructor(private val mediaSourceFactory: Factory, private val tagInterpreter: TagInterpreter) {
    suspend fun consume(incompleteSongs: Channel<RawSong>, completeSongs: Channel<RawSong>) {
        val worker = MetadataWorker(mediaSourceFactory, tagInterpreter)
        worker.start()

        var songsIn = 0
        incompleteSongs.forEachWithTimeout { incompleteRawSong ->
            spin@ while (!worker.push(incompleteRawSong)) {
                val completeRawSong = worker.pull()
                if (completeRawSong != null) {
                    completeSongs.sendWithTimeout(completeRawSong)
                    yield()
                    songsIn--
                } else {
                    continue
                }
            }
            songsIn++
        }

        logD("All incomplete songs exhausted, starting cleanup loop")
        while (!worker.idle()) {
            val completeRawSong = worker.pull()
            if (completeRawSong != null) {
                completeSongs.sendWithTimeout(completeRawSong)
                yield()
                songsIn--
            } else {
                continue
            }
        }
        worker.stop()
    }
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
private class MetadataWorker(
    private val mediaSourceFactory: Factory,
    private val tagInterpreter: TagInterpreter
) : Handler.Callback {
    private val mediaSourceThread = HandlerThread("Auxio:ChunkedMetadataRetriever")
    private val mediaSourceHandler: HandlerWrapper
    private val jobs = Array<MetadataJob?>(8) { null }

    private class MetadataJob(
        val rawSong: RawSong,
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

    fun start() {
        mediaSourceHandler.sendEmptyMessage(MESSAGE_CHECK_JOBS)
    }

    fun idle() = jobs.all { it == null }

    fun stop() {
        mediaSourceHandler.sendEmptyMessage(MESSAGE_RELEASE_ALL)
    }

    fun push(rawSong: RawSong): Boolean {
        for (i in jobs.indices) {
            if (jobs[i] == null) {
                val uri =
                    requireNotNull(rawSong.mediaStoreId) { "Invalid raw: No URI" }.toAudioUri()
                val job =
                    MetadataJob(
                        rawSong,
                        MediaItem.fromUri(uri),
                        SettableFuture.create<TrackGroupArray>(),
                        null,
                        null,
                        null)
                jobs[i] = job
                return true
            }
        }
        return false
    }

    fun pull(): RawSong? {
        for (i in jobs.indices) {
            val job = jobs[i]
            if (job != null && job.future.isDone) {
                try {
                    tagInterpreter.interpret(job.rawSong, job.future.get())
                } catch (e: Exception) {
                    logE("Failed to extract metadata")
                    logE(e.stackTraceToString())
                }
                jobs[i] = null
                return job.rawSong
            }
        }
        return null
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MESSAGE_CHECK_JOBS -> {
                for (job in jobs) {
                    if (job == null) continue

                    val currentMediaSource = job.mediaSource
                    val currentMediaSourceCaller = job.mediaSourceCaller
                    val mediaSource: MediaSource
                    val mediaSourceCaller: MediaSourceCaller
                    if (currentMediaSource != null && currentMediaSourceCaller != null) {
                        mediaSource = currentMediaSource
                        mediaSourceCaller = currentMediaSourceCaller
                    } else {
                        logD("new media source yahoo")
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
                        logE("Failed to extract MediaSource")
                        logE(e.stackTraceToString())
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
                return true
            }
            MESSAGE_RELEASE_ALL -> {
                for (job in jobs) {
                    if (job == null) continue
                    job.mediaPeriod?.let { job.mediaSource?.releasePeriod(it) }
                    job.mediaSourceCaller?.let { job.mediaSource?.releaseSource(it) }
                }
                mediaSourceHandler.removeCallbacksAndMessages(/* token= */ null)
                mediaSourceThread.quit()
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
            logD("yay source created")
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

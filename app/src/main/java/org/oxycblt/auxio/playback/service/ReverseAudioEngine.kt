/*
 * Copyright (c) 2026 Auxio Project
 * ReverseAudioEngine.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.max

interface ReverseAudioEngine {
    val currentPositionMs: Long
    val isPlaying: Boolean

    fun start(onPlaybackChanged: () -> Unit, onEnded: () -> Unit, onError: (Throwable) -> Unit)

    fun release()

    class Factory @Inject constructor(@ApplicationContext private val context: Context) {
        fun create(
            uri: Uri,
            startPositionMs: Long,
            speed: Float,
            audioSessionId: Int,
            amplification: Float,
        ): ReverseAudioEngine =
            PlatformReverseAudioEngine(
                context,
                uri,
                startPositionMs.coerceIn(0, Long.MAX_VALUE / 1000) * 1000,
                speed,
                audioSessionId,
                amplification,
            )
    }
}

private class PlatformReverseAudioEngine(
    private val context: Context,
    private val uri: Uri,
    private val startPositionUs: Long,
    private val speed: Float,
    private val audioSessionId: Int,
    private val amplification: Float,
) : ReverseAudioEngine {
    private val started = AtomicBoolean()
    private val released = AtomicBoolean()
    private val stopped = AtomicBoolean()
    private val callbackSent = AtomicBoolean()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val audioFocusListener =
        AudioManager.OnAudioFocusChangeListener { change ->
            when (change) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    focusSuspended = false
                    if (!stopped.get()) {
                        audioTrack?.play()
                        isPlaying = audioTrack != null
                    }
                    postPlaybackChanged()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    finalPositionUs = currentPositionMs * 1000
                    focusSuspended = true
                    audioTrack?.pause()
                    isPlaying = false
                    postPlaybackChanged()
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    finalPositionUs = currentPositionMs * 1000
                    focusSuspended = true
                    audioTrack?.pause()
                    isPlaying = false
                    stopped.set(true)
                    hasAudioFocus = false
                    errorCallback?.let { postError(it, ReverseAudioFocusLostException()) }
                }
            }
        }
    private val windows = ArrayBlockingQueue<QueueItem>(WINDOW_QUEUE_SIZE)
    private val executor =
        Executors.newFixedThreadPool(2) { runnable ->
            Thread(runnable, "Auxio reverse audio").apply { isDaemon = true }
        }

    @Volatile private var audioTrack: AudioTrack? = null
    @Volatile private var playbackChangedCallback: (() -> Unit)? = null
    @Volatile private var errorCallback: ((Throwable) -> Unit)? = null
    @Volatile private var audioFocusRequest: AudioFocusRequest? = null
    @Volatile private var hasAudioFocus = false
    @Volatile private var focusSuspended = false
    @Volatile private var sampleRate = 0
    @Volatile private var finalPositionUs = startPositionUs
    @Volatile
    override var isPlaying = false
        private set

    override val currentPositionMs: Long
        get() {
            val track = audioTrack
            val rate = sampleRate
            if (track != null && rate > 0) {
                val playedFrames = track.playbackHeadPosition.toLong() and UINT_MASK
                finalPositionUs =
                    (startPositionUs - playedFrames * MICROS_PER_SECOND / rate).coerceAtLeast(0)
            }
            return finalPositionUs / 1000
        }

    override fun start(
        onPlaybackChanged: () -> Unit,
        onEnded: () -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        if (!started.compareAndSet(false, true)) {
            return
        }
        if (released.get()) {
            postError(onError, IllegalStateException("Reverse audio engine was released"))
            return
        }
        if (!speed.isFinite() || speed <= 0) {
            stopped.set(true)
            postError(onError, IllegalArgumentException("Playback speed must be positive"))
            return
        }
        if (startPositionUs == 0L) {
            finalPositionUs = 0
            stopped.set(true)
            postEnded(onEnded)
            return
        }
        playbackChangedCallback = onPlaybackChanged
        errorCallback = onError
        if (!requestAudioFocus()) {
            stopped.set(true)
            postError(onError, IllegalStateException("Could not obtain audio focus"))
            return
        }

        executor.execute { decodeWindows() }
        executor.execute { playWindows(onEnded, onError) }
    }

    override fun release() {
        if (!released.compareAndSet(false, true)) {
            return
        }
        stopped.set(true)
        finalPositionUs = currentPositionMs * 1000
        isPlaying = false
        abandonAudioFocus()
        audioTrack?.let { track ->
            runCatching { track.pause() }
            runCatching { track.flush() }
            runCatching { track.stop() }
        }
        windows.clear()
        executor.shutdownNow()
    }

    private fun decodeWindows() {
        try {
            var windowEndUs = startPositionUs
            while (windowEndUs > 0 && !stopped.get()) {
                val windowStartUs = (windowEndUs - WINDOW_DURATION_US).coerceAtLeast(0)
                val window = decodeWindow(windowStartUs, windowEndUs)
                if (window.pcm.isNotEmpty() && !offer(WindowItem(window))) {
                    return
                }
                windowEndUs = windowStartUs
            }
            offer(EndItem)
        } catch (error: Throwable) {
            if (!stopped.get()) {
                offer(ErrorItem(error))
            }
        }
    }

    private fun decodeWindow(startUs: Long, endUs: Long): DecodedWindow {
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null
        try {
            extractor.setDataSource(context, uri, null)
            val trackIndex =
                (0 until extractor.trackCount).firstOrNull { index ->
                    extractor
                        .getTrackFormat(index)
                        .getString(MediaFormat.KEY_MIME)
                        ?.startsWith("audio/") == true
                } ?: throw IllegalArgumentException("Uri has no decodable audio track: $uri")
            extractor.selectTrack(trackIndex)
            extractor.seekTo(startUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)

            val inputFormat = extractor.getTrackFormat(trackIndex)
            val mime =
                inputFormat.getString(MediaFormat.KEY_MIME)
                    ?: throw IllegalArgumentException("Audio track has no MIME type")
            inputFormat.setInteger(MediaFormat.KEY_PCM_ENCODING, AudioFormat.ENCODING_PCM_16BIT)
            codec = MediaCodec.createDecoderByType(mime)
            codec.configure(inputFormat, null, null, 0)
            codec.start()

            var outputSampleRate = inputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            var outputChannelCount = inputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            var outputChannelMask =
                if (inputFormat.containsKey(MediaFormat.KEY_CHANNEL_MASK)) {
                    inputFormat.getInteger(MediaFormat.KEY_CHANNEL_MASK)
                } else {
                    channelMask(outputChannelCount)
                }
            val pcm = ByteArrayOutputStream()
            val info = MediaCodec.BufferInfo()
            var inputEnded = false
            var outputEnded = false
            while (!outputEnded && !stopped.get()) {
                if (!inputEnded) {
                    val inputIndex = codec.dequeueInputBuffer(CODEC_TIMEOUT_US)
                    if (inputIndex >= 0) {
                        val inputBuffer = requireNotNull(codec.getInputBuffer(inputIndex))
                        val sampleTimeUs = extractor.sampleTime
                        if (sampleTimeUs < 0 || sampleTimeUs >= endUs) {
                            codec.queueInputBuffer(
                                inputIndex,
                                0,
                                0,
                                max(sampleTimeUs, endUs),
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM,
                            )
                            inputEnded = true
                        } else {
                            val size = extractor.readSampleData(inputBuffer, 0)
                            if (size < 0) {
                                codec.queueInputBuffer(
                                    inputIndex,
                                    0,
                                    0,
                                    endUs,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM,
                                )
                                inputEnded = true
                            } else {
                                codec.queueInputBuffer(inputIndex, 0, size, sampleTimeUs, 0)
                                extractor.advance()
                            }
                        }
                    }
                }

                when (val outputIndex = codec.dequeueOutputBuffer(info, CODEC_TIMEOUT_US)) {
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        val outputFormat = codec.outputFormat
                        val encoding =
                            if (outputFormat.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
                                outputFormat.getInteger(MediaFormat.KEY_PCM_ENCODING)
                            } else {
                                AudioFormat.ENCODING_PCM_16BIT
                            }
                        check(encoding == AudioFormat.ENCODING_PCM_16BIT) {
                            "Decoder produced unsupported PCM encoding $encoding"
                        }
                        outputSampleRate = outputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                        outputChannelCount = outputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                        outputChannelMask =
                            if (outputFormat.containsKey(MediaFormat.KEY_CHANNEL_MASK)) {
                                outputFormat.getInteger(MediaFormat.KEY_CHANNEL_MASK)
                            } else {
                                channelMask(outputChannelCount)
                            }
                    }
                    in 0..Int.MAX_VALUE -> {
                        val outputBuffer = requireNotNull(codec.getOutputBuffer(outputIndex))
                        if (
                            info.size > 0 && info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0
                        ) {
                            appendClippedPcm(
                                pcm,
                                outputBuffer,
                                info,
                                outputSampleRate,
                                outputChannelCount,
                                startUs,
                                endUs,
                            )
                        }
                        outputEnded = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                        codec.releaseOutputBuffer(outputIndex, false)
                    }
                }
            }

            val frameSize = outputChannelCount * PCM_BYTES_PER_SAMPLE
            val bytes = pcm.toByteArray().copyOf(pcm.size() - pcm.size() % frameSize)
            amplifyPcm(bytes, amplification)
            reversePcmFrames(bytes, frameSize)
            return DecodedWindow(bytes, outputSampleRate, outputChannelCount, outputChannelMask)
        } finally {
            codec?.let {
                runCatching { it.stop() }
                it.release()
            }
            extractor.release()
        }
    }

    private fun playWindows(onEnded: () -> Unit, onError: (Throwable) -> Unit) {
        var track: AudioTrack? = null
        var submittedFrames = 0L
        var outputChannelMask = 0
        try {
            while (!stopped.get()) {
                when (val item = windows.poll(QUEUE_WAIT_MS, TimeUnit.MILLISECONDS)) {
                    null -> Unit
                    is WindowItem -> {
                        val window = item.window
                        if (track == null) {
                            val newTrack = createAudioTrack(window)
                            if (stopped.get()) {
                                newTrack.release()
                                return
                            }
                            track = newTrack
                            audioTrack = track
                            sampleRate = window.sampleRate
                            outputChannelMask = window.channelMask
                            if (!focusSuspended) {
                                track.play()
                                isPlaying = true
                            }
                            postPlaybackChanged()
                        } else {
                            check(
                                sampleRate == window.sampleRate &&
                                    track.channelCount == window.channelCount &&
                                    outputChannelMask == window.channelMask
                            ) {
                                "Audio format changed while decoding reverse audio"
                            }
                        }

                        var offset = 0
                        while (offset < window.pcm.size && !released.get()) {
                            val written =
                                track.write(
                                    window.pcm,
                                    offset,
                                    window.pcm.size - offset,
                                    AudioTrack.WRITE_BLOCKING,
                                )
                            check(written >= 0) { "AudioTrack write failed: $written" }
                            offset += written
                            submittedFrames +=
                                written / (window.channelCount * PCM_BYTES_PER_SAMPLE)
                        }
                    }
                    is ErrorItem -> throw item.error
                    EndItem -> {
                        if (track != null) {
                            while (!released.get()) {
                                val playedFrames = track.playbackHeadPosition.toLong() and UINT_MASK
                                if (playedFrames >= submittedFrames) {
                                    break
                                }
                                Thread.sleep(DRAIN_WAIT_MS)
                            }
                        }
                        if (!released.get()) {
                            finalPositionUs = 0
                            isPlaying = false
                            postEnded(onEnded)
                        }
                        return
                    }
                }
            }
        } catch (error: Throwable) {
            if (!released.get()) {
                finalPositionUs = currentPositionMs * 1000
                isPlaying = false
                postError(onError, error)
            }
        } finally {
            stopped.set(true)
            audioTrack = null
            track?.let {
                runCatching { it.stop() }
                it.release()
            }
            executor.shutdownNow()
        }
    }

    private fun createAudioTrack(window: DecodedWindow): AudioTrack {
        val channelMask = window.channelMask
        val minimumBufferSize =
            AudioTrack.getMinBufferSize(
                window.sampleRate,
                channelMask,
                AudioFormat.ENCODING_PCM_16BIT,
            )
        check(minimumBufferSize > 0) { "Unsupported AudioTrack format" }
        val frameSize = window.channelCount * PCM_BYTES_PER_SAMPLE
        val builder =
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(window.sampleRate)
                        .setChannelMask(channelMask)
                        .build()
                )
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setBufferSizeInBytes(max(minimumBufferSize * 4, window.sampleRate * frameSize))
        if (audioSessionId != AudioManager.AUDIO_SESSION_ID_GENERATE) {
            builder.setSessionId(audioSessionId)
        }
        val track = builder.build()
        try {
            check(track.state == AudioTrack.STATE_INITIALIZED) { "Could not initialize AudioTrack" }
            track.playbackParams =
                track.playbackParams
                    .setSpeed(speed)
                    .setPitch(1f)
                    .setAudioFallbackMode(android.media.PlaybackParams.AUDIO_FALLBACK_MODE_DEFAULT)
            return track
        } catch (error: Throwable) {
            track.release()
            throw error
        }
    }

    @Suppress("DEPRECATION")
    private fun requestAudioFocus(): Boolean {
        val result =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val request =
                    AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setOnAudioFocusChangeListener(audioFocusListener, mainHandler)
                        .setWillPauseWhenDucked(true)
                        .build()
                audioFocusRequest = request
                audioManager.requestAudioFocus(request)
            } else {
                audioManager.requestAudioFocus(
                    audioFocusListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN,
                )
            }
        hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        return hasAudioFocus
    }

    @Suppress("DEPRECATION")
    private fun abandonAudioFocus() {
        if (!hasAudioFocus) {
            return
        }
        hasAudioFocus = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let(audioManager::abandonAudioFocusRequest)
        } else {
            audioManager.abandonAudioFocus(audioFocusListener)
        }
        audioFocusRequest = null
        playbackChangedCallback = null
        errorCallback = null
    }

    private fun postPlaybackChanged() {
        val callback = playbackChangedCallback ?: return
        mainHandler.post {
            if (!released.get()) {
                callback()
            }
        }
    }

    private fun offer(item: QueueItem): Boolean {
        while (!stopped.get()) {
            if (windows.offer(item, QUEUE_WAIT_MS, TimeUnit.MILLISECONDS)) {
                return true
            }
        }
        return false
    }

    private fun postEnded(callback: () -> Unit) {
        if (callbackSent.compareAndSet(false, true)) {
            mainHandler.post {
                if (!released.get()) {
                    callback()
                }
            }
        }
    }

    private fun postError(callback: (Throwable) -> Unit, error: Throwable) {
        if (callbackSent.compareAndSet(false, true)) {
            mainHandler.post {
                if (!released.get()) {
                    callback(error)
                }
            }
        }
    }

    private sealed interface QueueItem

    private class WindowItem(val window: DecodedWindow) : QueueItem

    private class ErrorItem(val error: Throwable) : QueueItem

    private object EndItem : QueueItem

    private class DecodedWindow(
        val pcm: ByteArray,
        val sampleRate: Int,
        val channelCount: Int,
        val channelMask: Int,
    )

    private companion object {
        const val WINDOW_DURATION_US = 2_500_000L
        const val MICROS_PER_SECOND = 1_000_000L
        const val CODEC_TIMEOUT_US = 10_000L
        const val QUEUE_WAIT_MS = 100L
        const val DRAIN_WAIT_MS = 10L
        const val WINDOW_QUEUE_SIZE = 3
        const val PCM_BYTES_PER_SAMPLE = 2
        const val UINT_MASK = 0xffffffffL

        fun appendClippedPcm(
            destination: ByteArrayOutputStream,
            source: ByteBuffer,
            info: MediaCodec.BufferInfo,
            sampleRate: Int,
            channelCount: Int,
            startUs: Long,
            endUs: Long,
        ) {
            val frameSize = channelCount * PCM_BYTES_PER_SAMPLE
            val frameCount = info.size / frameSize
            val firstFrame =
                if (startUs <= info.presentationTimeUs) {
                        0
                    } else {
                        framesAtOrAfter(startUs - info.presentationTimeUs, sampleRate)
                    }
                    .coerceAtMost(frameCount)
            val lastFrame =
                if (endUs <= info.presentationTimeUs) {
                        0
                    } else {
                        framesAtOrAfter(endUs - info.presentationTimeUs, sampleRate)
                    }
                    .coerceAtMost(frameCount)
            if (lastFrame <= firstFrame) {
                return
            }

            val byteCount = (lastFrame - firstFrame) * frameSize
            val bytes = ByteArray(byteCount)
            source.position(info.offset + firstFrame * frameSize)
            source.get(bytes)
            destination.write(bytes)
        }

        private fun framesAtOrAfter(durationUs: Long, sampleRate: Int): Int =
            ((durationUs * sampleRate + MICROS_PER_SECOND - 1) / MICROS_PER_SECOND)
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()

        private fun channelMask(channelCount: Int): Int =
            when (channelCount) {
                1 -> AudioFormat.CHANNEL_OUT_MONO
                2 -> AudioFormat.CHANNEL_OUT_STEREO
                3 -> AudioFormat.CHANNEL_OUT_STEREO or AudioFormat.CHANNEL_OUT_FRONT_CENTER
                4 -> AudioFormat.CHANNEL_OUT_QUAD
                5 -> AudioFormat.CHANNEL_OUT_QUAD or AudioFormat.CHANNEL_OUT_FRONT_CENTER
                6 -> AudioFormat.CHANNEL_OUT_5POINT1
                7 -> AudioFormat.CHANNEL_OUT_6POINT1
                8 -> AudioFormat.CHANNEL_OUT_7POINT1_SURROUND
                else -> throw IllegalArgumentException("Unsupported channel count $channelCount")
            }
    }
}

internal fun reversePcmFrames(pcm: ByteArray, frameSize: Int) {
    require(frameSize > 0 && pcm.size % frameSize == 0)
    var left = 0
    var right = pcm.size - frameSize
    while (left < right) {
        for (offset in 0 until frameSize) {
            val value = pcm[left + offset]
            pcm[left + offset] = pcm[right + offset]
            pcm[right + offset] = value
        }
        left += frameSize
        right -= frameSize
    }
}

internal fun amplifyPcm(pcm: ByteArray, amplification: Float) {
    require(pcm.size % 2 == 0 && amplification.isFinite())
    if (amplification == 1f) {
        return
    }
    for (index in pcm.indices step 2) {
        val sample = pcm[index].toInt().and(0xff).or(pcm[index + 1].toInt().shl(8)).toShort()
        val amplified =
            (sample * amplification)
                .toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
        pcm[index] = amplified.toByte()
        pcm[index + 1] = amplified.shr(8).toByte()
    }
}

internal class ReverseAudioFocusLostException : IllegalStateException("Reverse audio lost focus")

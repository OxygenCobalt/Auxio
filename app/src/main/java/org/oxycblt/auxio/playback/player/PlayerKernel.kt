/*
 * Copyright (c) 2024 Auxio Project
 * PlayerKernel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.MediaSource
import javax.inject.Inject
import org.oxycblt.auxio.playback.replaygain.ReplayGainAudioProcessor

interface PlayerKernel {
    val isPlaying: Boolean
    var playWhenReady: Boolean
    val currentPosition: Long
    val audioSessionId: Int
    val queuer: Queuer

    fun attach()

    fun release()

    fun play()

    fun pause()

    fun seekTo(positionMs: Long)

    fun replaceQueuer(queuerFactory: Queuer.Factory)

    interface Listener {
        fun onPlayWhenReadyChanged()

        fun onIsPlayingChanged()

        fun onPositionDiscontinuity()

        fun onError(error: PlaybackException)
    }

    interface Factory {
        fun create(
            context: Context,
            playerListener: Listener,
            queuerFactory: Queuer.Factory,
            queuerListener: Queuer.Listener
        ): PlayerKernel
    }
}

class PlayerKernelFactoryImpl
@Inject
constructor(
    private val mediaSourceFactory: MediaSource.Factory,
    private val replayGainProcessor: ReplayGainAudioProcessor
) : PlayerKernel.Factory {
    override fun create(
        context: Context,
        playerListener: PlayerKernel.Listener,
        queuerFactory: Queuer.Factory,
        queuerListener: Queuer.Listener
    ): PlayerKernel {
        // Since Auxio is a music player, only specify an audio renderer to save
        // battery/apk size/cache size
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(
                FfmpegAudioRenderer(handler, audioListener, replayGainProcessor),
                MediaCodecAudioRenderer(
                    context,
                    MediaCodecSelector.DEFAULT,
                    handler,
                    audioListener,
                    AudioCapabilities.DEFAULT_AUDIO_CAPABILITIES,
                    replayGainProcessor))
        }

        val exoPlayer =
            ExoPlayer.Builder(context, audioRenderer)
                .setMediaSourceFactory(mediaSourceFactory)
                // Enable automatic WakeLock support
                .setWakeMode(C.WAKE_MODE_LOCAL)
                .setAudioAttributes(
                    // Signal that we are a music player.
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true)
                .build()

        return PlayerKernelImpl(
            exoPlayer, replayGainProcessor, playerListener, queuerListener, queuerFactory)
    }
}

private class PlayerKernelImpl(
    private val exoPlayer: ExoPlayer,
    private val replayGainProcessor: ReplayGainAudioProcessor,
    private val playerListener: PlayerKernel.Listener,
    private val queuerListener: Queuer.Listener,
    queuerFactory: Queuer.Factory
) : PlayerKernel, Player.Listener {
    override var queuer: Queuer = queuerFactory.create(exoPlayer, queuerListener)
    override val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    override var playWhenReady: Boolean
        get() = exoPlayer.playWhenReady
        set(value) {
            exoPlayer.playWhenReady = value
        }

    override val currentPosition: Long
        get() = exoPlayer.currentPosition

    override val audioSessionId: Int
        get() = exoPlayer.audioSessionId

    override fun attach() {
        exoPlayer.addListener(this)
        replayGainProcessor.attach()
        queuer.attach()
    }

    override fun release() {
        queuer.release()
        replayGainProcessor.release()
        exoPlayer.release()
    }

    override fun play() = exoPlayer.play()

    override fun pause() = exoPlayer.pause()

    override fun seekTo(positionMs: Long) = exoPlayer.seekTo(positionMs)

    override fun replaceQueuer(queuerFactory: Queuer.Factory) {
        queuer.release()
        queuer = queuerFactory.create(exoPlayer, queuerListener)
        queuer.attach()
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        playerListener.onPlayWhenReadyChanged()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        playerListener.onIsPlayingChanged()
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        playerListener.onPositionDiscontinuity()
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        playerListener.onError(error)
    }
}

package org.oxycblt.auxio.playback.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.RepeatMode
import androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.MediaSource
import org.oxycblt.auxio.playback.replaygain.ReplayGainAudioProcessor
import javax.inject.Inject

interface PlayerFactory {
    fun create(context: Context): ThinPlayer

}

interface ThinPlayer {
    val isPlaying: Boolean
    var playWhenReady: Boolean
    val currentPosition: Long
    @get:RepeatMode var repeatMode: Int
    val audioSessionId: Int
    var pauseAtEndOfMediaItems: Boolean

    fun attach(listener: Player.Listener)
    fun release()

    fun play()
    fun pause()
    fun seekTo(positionMs: Long)

    fun intoQueuer(queuerFactory: Queuer.Factory): Queuer
}

class PlayerFactoryImpl(@Inject private val mediaSourceFactory: MediaSource.Factory, @Inject private val replayGainProcessor: ReplayGainAudioProcessor) : PlayerFactory {
    override fun create(context: Context): ThinPlayer {
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

        return ThinPlayerImpl(exoPlayer, replayGainProcessor)
    }
}

private class ThinPlayerImpl(
    private val exoPlayer: ExoPlayer,
    private val replayGainProcessor: ReplayGainAudioProcessor
) : ThinPlayer {
    override val isPlaying: Boolean get() = exoPlayer.isPlaying
    override var playWhenReady: Boolean
        get() = exoPlayer.playWhenReady
        set(value) {
            exoPlayer.playWhenReady = value
        }
    override val currentPosition: Long get() = exoPlayer.currentPosition
    override var repeatMode: Int
        get() = exoPlayer.repeatMode
        set(value) {
            exoPlayer.repeatMode = value
        }
    override val audioSessionId: Int get() = exoPlayer.audioSessionId
    override var pauseAtEndOfMediaItems: Boolean
        get() = exoPlayer.pauseAtEndOfMediaItems
        set(value) {
            exoPlayer.pauseAtEndOfMediaItems = value
        }

    override fun attach(listener: Player.Listener) {
        exoPlayer.addListener(listener)
        replayGainProcessor.attach()
    }

    override fun release() {
        replayGainProcessor.release()
        exoPlayer.release()
    }

    override fun play() = exoPlayer.play()

    override fun pause() = exoPlayer.pause()

    override fun seekTo(positionMs: Long) = exoPlayer.seekTo(positionMs)

    override fun intoQueuer(queuerFactory: Queuer.Factory) = queuerFactory.create(exoPlayer)
}
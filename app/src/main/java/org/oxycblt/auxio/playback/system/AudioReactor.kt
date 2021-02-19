package org.oxycblt.auxio.playback.system

import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioManager
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager

/**
 * Object that manages the AudioFocus state.
 * Adapted from NewPipe (https://github.com/TeamNewPipe/NewPipe)
 */
class AudioReactor(
    context: Context,
    private val player: SimpleExoPlayer
) : AudioManager.OnAudioFocusChangeListener {
    private val audioManager = ContextCompat.getSystemService(
        context, AudioManager::class.java
    ) ?: error("Cannot obtain AudioManager.")

    private val settingsManager = SettingsManager.getInstance()
    private val playbackManager = PlaybackStateManager.getInstance()

    private val request = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
        .setWillPauseWhenDucked(true)
        .setOnAudioFocusChangeListener(this)
        .build()

    private var pauseWasFromAudioFocus = false

    /**
     * Request the android system for audio focus
     */
    fun requestFocus() {
        AudioManagerCompat.requestAudioFocus(audioManager, request)
    }

    /**
     * Destroy this object and abandon its audio focus request, should be ran on destruction to
     * prevent memory leaks.
     */
    fun destroy() {
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, request)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> onGain()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> onDuck()
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> onLoss()
        }
    }

    private fun onGain() {
        if (settingsManager.doAudioFocus) {
            if (player.volume == VOLUME_DUCK && playbackManager.isPlaying) {
                unduck()
            } else if (pauseWasFromAudioFocus) {
                playbackManager.setPlaying(true)
            }

            pauseWasFromAudioFocus = false
        }
    }

    private fun onLoss() {
        if (settingsManager.doAudioFocus && playbackManager.isPlaying) {
            pauseWasFromAudioFocus = true
            playbackManager.setPlaying(false)
        }
    }

    private fun onDuck() {
        if (settingsManager.doAudioFocus) {
            player.volume = VOLUME_DUCK
        }
    }

    private fun unduck() {
        player.volume = VOLUME_DUCK

        ValueAnimator().apply {
            setFloatValues(VOLUME_DUCK, VOLUME_FULL)
            duration = DUCK_DURATION
            addListener(
                onStart = { player.volume = VOLUME_DUCK },
                onCancel = { player.volume = VOLUME_FULL },
                onEnd = { player.volume = VOLUME_FULL }
            )
            addUpdateListener {
                player.volume = it.animatedValue as Float
            }
            start()
        }
    }

    companion object {
        private const val VOLUME_DUCK = 0.2f
        private const val DUCK_DURATION = 1500L
        private const val VOLUME_FULL = 1.0f
    }
}

/*
 * Copyright (c) 2021 Auxio Project
 * AudioReactor.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.system

import android.content.Context
import android.media.AudioManager
import androidx.core.math.MathUtils
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.flac.VorbisComment
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD
import kotlin.math.pow

/**
 * Object that manages the AudioFocus state.
 * Adapted from NewPipe (https://github.com/TeamNewPipe/NewPipe)
 * @author OxygenCobalt
 */
class AudioReactor(context: Context) : AudioManager.OnAudioFocusChangeListener {
    private data class Gain(val track: Float, val album: Float)

    private val playbackManager = PlaybackStateManager.maybeGetInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val audioManager = context.getSystemServiceSafe(AudioManager::class)

    private val request = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
        .setWillPauseWhenDucked(false)
        .setAudioAttributes(
            AudioAttributesCompat.Builder()
                .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                .build()
        )
        .setOnAudioFocusChangeListener(this)
        .build()

    private var multiplier = 1f
    private var pauseWasTransient = false

    var volume = 0f
        get() = field * multiplier
        private set

    /**
     * Updates the rough volume adjustment for [Metadata] with ReplayGain tags.
     * This is based off Vanilla Music's implementation.
     */
    fun applyReplayGain(metadata: Metadata?) {
        if (metadata == null) {
            logD("No parsable ReplayGain tags, returning volume to 1.")
            volume = 1f
            return
        }

        val gain = parseReplayGain(metadata)

        // Currently we consider both the album and the track gain. One might want to add
        // configuration to handle more cases.
        var adjust = 0f

        if (gain != null) {
            adjust = if (gain.album != 0f) {
                gain.album
            } else {
                gain.track
            }
        }

        // Final adjustment along the volume curve.
        // Ensure this is clamped to 0 or 1 so that it can be used as a volume.
        volume = MathUtils.clamp((10f.pow((adjust / 20f))), 0f, 1f)
        logD("Applied ReplayGain adjustment: $volume")
    }

    private fun parseReplayGain(metadata: Metadata): Gain? {
        data class GainTag(val key: String, val value: Float)

        var trackGain = 0f
        var albumGain = 0f
        var found = false

        val tags = mutableListOf<GainTag>()

        for (i in 0 until metadata.length()) {
            val entry = metadata.get(i)

            // Sometimes the ReplayGain keys will be lowercase, so make them uppercase.
            if (entry is TextInformationFrame && entry.description?.uppercase() in replayGainTags) {
                tags.add(GainTag(entry.description!!.uppercase(), parseReplayGainFloat(entry.value)))
                continue
            }

            if (entry is VorbisComment && entry.key.uppercase() in replayGainTags) {
                tags.add(GainTag(entry.key.uppercase(), parseReplayGainFloat(entry.value)))
            }
        }

        // Case 1: Normal ReplayGain, most commonly found on MPEG files.
        tags.findLast { tag -> tag.key == RG_TRACK }?.let { tag ->
            trackGain = tag.value
            found = true
        }

        tags.findLast { tag -> tag.key == RG_ALBUM }?.let { tag ->
            albumGain = tag.value
            found = true
        }

        // Case 2: R128 ReplayGain, most commonly found on FLAC files.
        // While technically there is the R128 base gain in Opus files, ExoPlayer doesn't
        // have metadata parsing functionality for those, so we just ignore it.
        tags.findLast { tag -> tag.key == R128_TRACK }?.let { tag ->
            trackGain += tag.value / 256f
            found = true
        }

        tags.findLast { tag -> tag.key == R128_ALBUM }?.let { tag ->
            albumGain += tag.value / 256f
            found = true
        }

        return if (found) {
            Gain(trackGain, albumGain)
        } else {
            null
        }
    }

    private fun parseReplayGainFloat(raw: String): Float {
        return try {
            raw.replace(Regex("[^0-9.-]"), "").toFloat()
        } catch (e: Exception) {
            0f
        }
    }

    /**
     * Request the android system for audio focus
     */
    fun requestFocus() {
        AudioManagerCompat.requestAudioFocus(audioManager, request)
    }

    /**
     * Abandon the current focus request, functionally "Destroying it".
     */
    fun release() {
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, request)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (!settingsManager.doAudioFocus) {
            // Don't do audio focus if its not enabled
            return
        }

        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> onGain()
            AudioManager.AUDIOFOCUS_LOSS -> onLossPermanent()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> onLossTransient()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> onDuck()
        }
    }

    private fun onGain() {
        if (multiplier == MULTIPLIER_DUCK) {
            unduck()
        } else if (pauseWasTransient) {
            logD("Gained focus after transient loss")

            // Play again if the pause was only temporary [AudioManager.AUDIOFOCUS_LOSS_TRANSIENT]
            playbackManager.setPlaying(true)
            pauseWasTransient = false
        }
    }

    private fun onLossTransient() {
        // Since this loss is only temporary, mark it as such if we had to pause playback.
        if (playbackManager.isPlaying) {
            logD("Pausing for transient loss")
            playbackManager.setPlaying(false)
            pauseWasTransient = true
        }
    }

    private fun onLossPermanent() {
        logD("Pausing for permanent loss")
        playbackManager.setPlaying(false)
    }

    private fun onDuck() {
        multiplier = MULTIPLIER_DUCK
        logD("Ducked volume, now $volume")
    }

    private fun unduck() {
        multiplier = 1f
        logD("Unducked volume, now $volume")
    }

    companion object {
        private const val MULTIPLIER_DUCK = 0.2f

        const val RG_TRACK = "REPLAYGAIN_TRACK_GAIN"
        const val RG_ALBUM = "REPLAYGAIN_ALBUM_GAIN"
        const val R128_TRACK = "R128_TRACK_GAIN"
        const val R128_ALBUM = "R128_ALBUM_GAIN"

        val replayGainTags = arrayOf(
            RG_TRACK,
            RG_ALBUM,
            R128_ALBUM,
            R128_TRACK
        )
    }
}

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
import android.os.Build
import androidx.core.math.MathUtils
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD
import kotlin.math.pow

/**
 * Manages the current volume and playback state across ReplayGain and AudioFocus events.
 * @author OxygenCobalt
 */
class AudioReactor(
    context: Context,
    private val callback: (Float) -> Unit
) : AudioManager.OnAudioFocusChangeListener, SettingsManager.Callback {
    private data class Gain(val track: Float, val album: Float)
    private data class GainTag(val key: String, val value: Float)

    private val playbackManager = PlaybackStateManager.getInstance()
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

    private var pauseWasTransient = false

    // It's good to keep the volume and the ducking multiplier separate so that we can
    private var multiplier = 1f
        set(value) {
            field = value
            callback(volume)
        }

    private var volume = 0f
        get() = field * multiplier
        set(value) {
            field = value
            callback(volume)
        }

    init {
        settingsManager.addCallback(this)
    }

    /**
     * Request the android system for audio focus
     */
    fun requestFocus() {
        AudioManagerCompat.requestAudioFocus(audioManager, request)
    }

    /**
     * Updates the rough volume adjustment for [Metadata] with ReplayGain tags.
     * This is based off Vanilla Music's implementation.
     */
    fun applyReplayGain(metadata: Metadata?) {
        if (metadata == null) {
            logD("No metadata.")
            volume = 1f
            return
        }

        // ReplayGain is configurable, so determine what to do based off of the mode.
        val useAlbumGain: (Gain) -> Boolean = when (settingsManager.replayGainMode) {
            ReplayGainMode.OFF -> {
                logD("ReplayGain is off.")
                volume = 1f
                return
            }

            // User wants track gain to be preferred. Default to album gain only if there
            // is no track gain.
            ReplayGainMode.TRACK ->
                { gain ->
                    gain.track == 0f
                }

            // User wants album gain to be preferred. Default to track gain only if there
            // is no album gain.
            ReplayGainMode.ALBUM ->
                { gain ->
                    gain.album != 0f
                }

            // User wants album gain to be used when in an album, track gain otherwise.
            ReplayGainMode.DYNAMIC ->
                { _ ->
                    playbackManager.parent is Album &&
                        playbackManager.song?.album == playbackManager.parent
                }
        }
        val gain = parseReplayGain(metadata)

        val adjust = if (gain != null) {
            if (useAlbumGain(gain)) {
                logD("Using album gain.")
                gain.album
            } else {
                logD("Using track gain.")
                gain.track
            }
        } else {
            // No gain tags were present
            0f
        }

        // Final adjustment along the volume curve.
        // Ensure this is clamped to 0 or 1 so that it can be used as a volume.
        // While positive ReplayGain values *could* be theoretically added, it's such
        // a niche use-case that to be worth the effort required. Maybe if someone requests it.
        volume = MathUtils.clamp((10f.pow((adjust / 20f))), 0f, 1f)
    }

    private fun parseReplayGain(metadata: Metadata): Gain? {
        var trackGain = 0f
        var albumGain = 0f
        var found = false

        val tags = mutableListOf<GainTag>()

        for (i in 0 until metadata.length()) {
            val entry = metadata.get(i)

            val key: String?
            val value: String

            when (entry) {
                is TextInformationFrame -> {
                    key = entry.description?.uppercase()
                    value = entry.value
                }

                is VorbisComment -> {
                    key = entry.key
                    value = entry.value
                }

                else -> continue
            }

            if (key in REPLAY_GAIN_TAGS) {
                tags.add(GainTag(key!!, parseReplayGainFloat(value)))
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
        // While technically there is the R128 base gain in Opus files, that is automatically
        // applied by the media framework [which ExoPlayer relies on]. The only reason we would
        // want to read it is to zero previous ReplayGain values for being invalid, however there
        // is no demand to fix that edge case right now.
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
     * Abandon the current focus request and any callbacks
     */
    fun release() {
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, request)
        settingsManager.removeCallback(this)
    }

    // --- INTERNAL AUDIO FOCUS ---

    override fun onAudioFocusChange(focusChange: Int) {
        if (!settingsManager.doAudioFocus && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
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

    // --- SETTINGS MANAGEMENT ---

    override fun onAudioFocusUpdate(focus: Boolean) {
        if (!focus) {
            onGain()
        }
    }

    companion object {
        private const val MULTIPLIER_DUCK = 0.2f

        const val RG_TRACK = "REPLAYGAIN_TRACK_GAIN"
        const val RG_ALBUM = "REPLAYGAIN_ALBUM_GAIN"
        const val R128_TRACK = "R128_TRACK_GAIN"
        const val R128_ALBUM = "R128_ALBUM_GAIN"

        val REPLAY_GAIN_TAGS = arrayOf(
            RG_TRACK,
            RG_ALBUM,
            R128_ALBUM,
            R128_TRACK
        )
    }
}

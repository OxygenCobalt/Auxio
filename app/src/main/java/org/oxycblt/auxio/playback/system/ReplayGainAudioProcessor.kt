/*
 * Copyright (c) 2022 Auxio Project
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

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.BaseAudioProcessor
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import java.nio.ByteBuffer
import kotlin.math.pow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.clamp
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * An [AudioProcessor] that automatically handles ReplayGain values and their amplification of the
 * audio stream. Instead of leveraging the volume attribute like other implementations, this system
 * manipulates the bitstream itself to modify the volume, which allows the use of positive
 * ReplayGain values.
 *
 * TODO: Pre-amp values
 *
 * @author OxygenCobalt
 */
class ReplayGainAudioProcessor : BaseAudioProcessor() {
    private data class Gain(val track: Float, val album: Float)
    private data class GainTag(val key: String, val value: Float)

    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    private var volume = 1f
        set(value) {
            field = value
            // Processed bytes are no longer valid, flush the stream
            flush()
        }

    // --- REPLAYGAIN PARSING ---

    /**
     * Updates the rough volume adjustment for [Metadata] with ReplayGain tags. This is based off
     * Vanilla Music's implementation.
     */
    fun applyReplayGain(metadata: Metadata?) {
        if (metadata == null) {
            logW("No metadata could be extracted from this track")
            volume = 1f
            return
        }

        // ReplayGain is configurable, so determine what to do based off of the mode.
        val useAlbumGain: (Gain) -> Boolean =
            when (settingsManager.replayGainMode) {
                ReplayGainMode.OFF -> {
                    logD("ReplayGain is off")
                    volume = 1f
                    return
                }

                // User wants track gain to be preferred. Default to album gain only if there
                // is no track gain.
                ReplayGainMode.TRACK -> { gain -> gain.track == 0f }

                // User wants album gain to be preferred. Default to track gain only if there
                // is no album gain.
                ReplayGainMode.ALBUM -> { gain -> gain.album != 0f }

                // User wants album gain to be used when in an album, track gain otherwise.
                ReplayGainMode.DYNAMIC -> { _ ->
                        playbackManager.parent is Album &&
                            playbackManager.song?.album == playbackManager.parent
                    }
            }

        val gain = parseReplayGain(metadata)

        val adjust =
            if (gain != null) {
                if (useAlbumGain(gain)) {
                    logD("Using album gain")
                    gain.album
                } else {
                    logD("Using track gain")
                    gain.track
                }
            } else {
                // No gain tags were present
                0f
            }

        // Final adjustment along the volume curve.
        volume = 10f.pow(adjust / 20f)
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
                tags.add(GainTag(unlikelyToBeNull(key), parseReplayGainFloat(value)))
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

    // --- AUDIO PROCESSOR IMPLEMENTATION ---

    override fun onConfigure(
        inputAudioFormat: AudioProcessor.AudioFormat
    ): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding == C.ENCODING_PCM_16BIT) {
            // AudioProcessor is only provided 16-bit PCM audio data, so that's the only
            // encoding we need to check for.
            return inputAudioFormat
        }

        throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val position = inputBuffer.position()
        val limit = inputBuffer.limit()
        val size = limit - position
        val buffer = replaceOutputBuffer(size)

        if (volume == 1f) {
            // Nothing to do, just copy the bytes into the output buffer.
            for (i in position until limit) {
                buffer.put(inputBuffer[i])
            }
        } else {
            for (i in position until limit step 2) {
                val sample = inputBuffer.getLeShort(i)
                // Clamp the values to the minimum and maximum values possible for the
                // encoding. This prevents issues where samples amplified beyond 1 << 16
                // will end up becoming truncated during the conversion to a short,
                // resulting in popping.
                val targetSample =
                    (sample * volume)
                        .toInt()
                        .clamp(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        .toShort()
                buffer.putLeShort(targetSample)
            }
        }

        inputBuffer.position(limit)
        buffer.flip()
    }

    private fun ByteBuffer.getLeShort(at: Int): Short {
        return get(at + 1).toInt().shl(8).or(get(at).toInt().and(0xFF)).toShort()
    }

    private fun ByteBuffer.putLeShort(short: Short) {
        put(short.toByte())
        put(short.toInt().shr(8).toByte())
    }

    companion object {
        private const val RG_TRACK = "REPLAYGAIN_TRACK_GAIN"
        private const val RG_ALBUM = "REPLAYGAIN_ALBUM_GAIN"
        private const val R128_TRACK = "R128_TRACK_GAIN"
        private const val R128_ALBUM = "R128_ALBUM_GAIN"

        private val REPLAY_GAIN_TAGS = arrayOf(RG_TRACK, RG_ALBUM, R128_ALBUM, R128_TRACK)
    }
}

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
import com.google.android.exoplayer2.Format
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

    /// --- REPLAYGAIN PARSING ---

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
        // TODO: Determine if we really need all of these encodings
        val encoding = inputAudioFormat.encoding
        if (encoding != C.ENCODING_PCM_8BIT &&
            encoding != C.ENCODING_PCM_16BIT &&
            encoding != C.ENCODING_PCM_16BIT_BIG_ENDIAN &&
            encoding != C.ENCODING_PCM_24BIT &&
            encoding != C.ENCODING_PCM_32BIT &&
            encoding != C.ENCODING_PCM_FLOAT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }

        return inputAudioFormat
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
            // AudioProcessor supplies us with the raw bytes and the encoding. It's our job
            // to decode and manipulate it. However, the way we muck the bytes into integer
            // types (and vice versa) introduces the possibility for bits to be dropped along
            // the way. This is very bad and can result in popping, corrupted audio streams.
            // Fix this by clamping the values to the possible range of *signed* values, as
            // the PCM data is unsigned and still uses the bit that the JVM interprets as a sign.
            when (inputAudioFormat.encoding) {
                C.ENCODING_PCM_8BIT -> {
                    // 8-bit PCM, decode a single byte and multiply it
                    for (i in position until limit) {
                        val sample = inputBuffer.get(i).toInt().and(0xFF)
                        val targetSample =
                            (sample * volume)
                                .toInt()
                                .clamp(Byte.MIN_VALUE.toInt(), Byte.MAX_VALUE.toInt())
                                .toByte()
                        buffer.put(targetSample)
                    }
                }
                C.ENCODING_PCM_16BIT -> {
                    // 16-bit PCM (little endian).
                    for (i in position until limit step 2) {
                        val sample = inputBuffer.getLeShort(i)
                        val targetSample =
                            (sample * volume)
                                .toInt()
                                .clamp(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                                .toShort()
                        buffer.putLeShort(targetSample)
                    }
                }
                C.ENCODING_PCM_16BIT_BIG_ENDIAN -> {
                    // 16-bit PCM (big endian)
                    for (i in position until limit step 2) {
                        val sample = inputBuffer.getBeShort(i)
                        val targetSample =
                            (sample * volume)
                                .toInt()
                                .clamp(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                                .toShort()
                        buffer.putBeSort(targetSample)
                    }
                }
                C.ENCODING_PCM_24BIT -> {
                    // 24-bit PCM (little endian), decode the data three bytes at a time.
                    // I don't know if the clamping we do here is valid or not. Since the bit
                    // values should not cross over into the sign, we should be able to do a
                    // simple unsigned clamp, but I'm not sure.
                    for (i in position until limit step 3) {
                        val sample = inputBuffer.getLeInt24(i)
                        val targetSample = (sample * volume).toInt().clamp(0, 0xFF_FF_FF)
                        buffer.putLeInt24(targetSample)
                    }
                }
                C.ENCODING_PCM_32BIT -> {
                    // 32-bit PCM (little endian).
                    for (i in position until limit step 4) {
                        var sample = inputBuffer.getLeInt32(i)
                        sample = (sample * volume).toInt().clamp(Int.MIN_VALUE, Int.MAX_VALUE)
                        buffer.putLeInt32(sample)
                    }
                }
                C.ENCODING_PCM_FLOAT -> {
                    // PCM float. Here we can actually clamp values since the value isn't
                    // bitwise.
                    for (i in position until limit step 4) {
                        val sample = inputBuffer.getFloat(i)
                        val targetSample = (sample * volume).clamp(0f, 1f)
                        buffer.putFloat(targetSample)
                    }
                }
                C.ENCODING_INVALID, Format.NO_VALUE -> {}
            }
        }

        inputBuffer.position(limit)
        buffer.flip()
    }

    private fun ByteBuffer.getLeShort(at: Int): Short {
        return get(at + 1).toInt().shl(8).or(get(at).toInt().and(0xFF)).toShort()
    }

    private fun ByteBuffer.getBeShort(at: Int): Short {
        return get(at).toInt().shl(8).or(get(at + 1).toInt().and(0xFF)).toShort()
    }

    private fun ByteBuffer.putLeShort(short: Short) {
        put(short.toByte())
        put(short.toInt().shr(8).toByte())
    }

    private fun ByteBuffer.putBeSort(short: Short) {
        put(short.toInt().shr(8).toByte())
        put(short.toByte())
    }

    private fun ByteBuffer.getLeInt24(at: Int): Int {
        return get(at + 2)
            .toInt()
            .shl(16)
            .or(get(at + 1).toInt().shl(8))
            .or(get(at).toInt().and(0xFF))
    }

    private fun ByteBuffer.putLeInt24(int: Int) {
        put(int.toByte())
        put(int.shr(8).toByte())
        put(int.shr(16).toByte())
    }

    private fun ByteBuffer.getLeInt32(at: Int): Int {
        return get(at + 3)
            .toInt()
            .shl(24)
            .or(get(at + 2).toInt().shl(16))
            .or(get(at + 1).toInt().shl(8))
            .or(get(at).toInt().and(0xFF))
    }

    private fun ByteBuffer.putLeInt32(int: Int) {
        put(int.toByte())
        put(int.shr(8).toByte())
        put(int.shr(16).toByte())
        put(int.shr(24).toByte())
    }

    companion object {
        private const val RG_TRACK = "REPLAYGAIN_TRACK_GAIN"
        private const val RG_ALBUM = "REPLAYGAIN_ALBUM_GAIN"
        private const val R128_TRACK = "R128_TRACK_GAIN"
        private const val R128_ALBUM = "R128_ALBUM_GAIN"

        private val REPLAY_GAIN_TAGS = arrayOf(RG_TRACK, RG_ALBUM, R128_ALBUM, R128_TRACK)
    }
}

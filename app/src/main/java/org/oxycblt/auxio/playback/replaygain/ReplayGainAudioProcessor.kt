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
 
package org.oxycblt.auxio.playback.replaygain

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.BaseAudioProcessor
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.InternalFrame
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import java.nio.ByteBuffer
import kotlin.math.pow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * An [AudioProcessor] that handles ReplayGain values and their amplification of the audio stream.
 * Instead of leveraging the volume attribute like other implementations, this system manipulates
 * the bitstream itself to modify the volume, which allows the use of positive ReplayGain values.
 *
 * Note: This instance must be updated with a new [Metadata] every time the active track chamges.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ReplayGainAudioProcessor(context: Context) : BaseAudioProcessor() {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settings = Settings(context)

    private var volume = 1f
        set(value) {
            field = value
            // Processed bytes are no longer valid, flush the stream
            flush()
        }

    // --- REPLAYGAIN PARSING ---

    /**
     * Updates the volume adjustment based on the given [Metadata].
     * @param metadata The [Metadata] of the currently playing track, or null if the track has no
     * [Metadata].
     */
    fun applyReplayGain(metadata: Metadata?) {
        // TODO: Allow this to automatically obtain it's own [Metadata].
        val gain = metadata?.let(::parseReplayGain)
        val preAmp = settings.replayGainPreAmp

        val adjust =
            if (gain != null) {
                // ReplayGain is configurable, so determine what to do based off of the mode.
                val useAlbumGain =
                    when (settings.replayGainMode) {
                        // User wants track gain to be preferred. Default to album gain only if
                        // there is no track gain.
                        ReplayGainMode.TRACK -> gain.track == 0f

                        // User wants album gain to be preferred. Default to track gain only if
                        // here is no album gain.
                        ReplayGainMode.ALBUM -> gain.album != 0f

                        // User wants album gain to be used when in an album, track gain otherwise.
                        ReplayGainMode.DYNAMIC ->
                            playbackManager.parent is Album &&
                                playbackManager.song?.album == playbackManager.parent
                    }

                val resolvedGain =
                    if (useAlbumGain) {
                        logD("Using album gain")
                        gain.album
                    } else {
                        logD("Using track gain")
                        gain.track
                    }

                // Apply the adjustment specified when there is ReplayGain tags.
                resolvedGain + preAmp.with
            } else {
                // No ReplayGain tags existed, or no tags were parsable, or there was no metadata
                // in the first place. Return the gain to use when there is no ReplayGain value.
                logD("No ReplayGain tags present")
                preAmp.without
            }

        logD("Applying ReplayGain adjustment ${adjust}db")

        // Final adjustment along the volume curve.
        volume = 10f.pow(adjust / 20f)
    }

    /**
     * Parse ReplayGain information from the given [Metadata].
     * @param metadata The [Metadata] to parse.
     * @return A [Gain] adjustment, or null if there was no adjustments to parse.
     */
    private fun parseReplayGain(metadata: Metadata): Gain? {
        // TODO: Unify this parser with the music parser? They both grok Metadata.

        var trackGain = 0f
        var albumGain = 0f
        var found = false

        val tags = mutableListOf<GainTag>()

        for (i in 0 until metadata.length()) {
            val entry = metadata.get(i)

            val key: String?
            val value: String

            when (entry) {
                // ID3v2 text information frame, usually these are formatted in lowercase
                // (like "replaygain_track_gain"), but can also be uppercase. Make sure that
                // capitalization is consistent before continuing.
                is TextInformationFrame -> {
                    key = entry.description
                    value = entry.values[0]
                }
                // Internal Frame. This is actually MP4's "----" atom, but mapped to an ID3v2
                // frame by ExoPlayer (presumably to reduce duplication).
                is InternalFrame -> {
                    key = entry.description
                    value = entry.text
                }
                // Vorbis comment. These are nearly always uppercase, so a check for such is
                // skipped.
                is VorbisComment -> {
                    key = entry.key
                    value = entry.value
                }
                else -> continue
            }

            if (key in REPLAY_GAIN_TAGS) {
                // Grok a float from a ReplayGain tag by removing everything that is not 0-9, ,
                // or -.
                // Derived from vanilla music: https://github.com/vanilla-music/vanilla
                val gainValue =
                    try {
                        value.replace(Regex("[^\\d.-]"), "").toFloat()
                    } catch (e: Exception) {
                        0f
                    }

                tags.add(GainTag(unlikelyToBeNull(key), gainValue))
            }
        }

        // Case 1: Normal ReplayGain, most commonly found on MPEG files.
        tags
            .findLast { tag -> tag.key.equals(TAG_RG_TRACK, ignoreCase = true) }
            ?.let { tag ->
                trackGain = tag.value
                found = true
            }

        tags
            .findLast { tag -> tag.key.equals(TAG_RG_ALBUM, ignoreCase = true) }
            ?.let { tag ->
                albumGain = tag.value
                found = true
            }

        // Case 2: R128 ReplayGain, most commonly found on FLAC files and other lossless
        // encodings to increase precision in volume adjustments.
        // While technically there is the R128 base gain in Opus files, that is automatically
        // applied by the media framework [which ExoPlayer relies on]. The only reason we would
        // want to read it is to zero previous ReplayGain values for being invalid, however there
        // is no demand to fix that edge case right now.
        tags
            .findLast { tag -> tag.key.equals(R128_TRACK, ignoreCase = true) }
            ?.let { tag ->
                trackGain += tag.value / 256f
                found = true
            }

        tags
            .findLast { tag -> tag.key.equals(R128_ALBUM, ignoreCase = true) }
            ?.let { tag ->
                albumGain += tag.value / 256f
                found = true
            }

        return if (found) {
            Gain(trackGain, albumGain)
        } else {
            null
        }
    }
    // --- AUDIO PROCESSOR IMPLEMENTATION ---

    override fun onConfigure(
        inputAudioFormat: AudioProcessor.AudioFormat
    ): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding == C.ENCODING_PCM_16BIT) {
            // AudioProcessor is only provided 16-bit PCM audio data, so that's the only
            // encoding we need to check for.
            // TODO: Convert to a low-level audio processor capable of handling any kind of
            //  PCM data, once ExoPlayer can support it.
            return inputAudioFormat
        }

        throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
    }

    override fun isActive() = super.isActive() && volume != 1f

    override fun queueInput(inputBuffer: ByteBuffer) {
        val position = inputBuffer.position()
        val limit = inputBuffer.limit()
        val size = limit - position
        val buffer = replaceOutputBuffer(size)

        for (i in position until limit step 2) {
            // Ensure we clamp the values to the minimum and maximum values possible
            // for the encoding. This prevents issues where samples amplified beyond
            // 1 << 16 will end up becoming truncated during the conversion to a short,
            // resulting in popping.
            var sample = inputBuffer.getLeShort(i)
            sample =
                (sample * volume)
                    .toInt()
                    .coerceAtLeast(Short.MIN_VALUE.toInt())
                    .coerceAtMost(Short.MAX_VALUE.toInt())
                    .toShort()
            buffer.putLeShort(sample)
        }

        inputBuffer.position(limit)
        buffer.flip()
    }

    /**
     * Always read a little-endian [Short] from the [ByteBuffer] at the given index.
     * @param at The index to read the [Short] from.
     */
    private fun ByteBuffer.getLeShort(at: Int) =
        get(at + 1).toInt().shl(8).or(get(at).toInt().and(0xFF)).toShort()

    /**
     * Always write a little-endian [Short] at the end of the [ByteBuffer].
     * @param short The [Short] to write.
     */
    private fun ByteBuffer.putLeShort(short: Short) {
        put(short.toByte())
        put(short.toInt().shr(8).toByte())
    }

    /**
     * The resolved ReplayGain adjustment for a file.
     * @param track The track adjustment (in dB), or 0 if it is not present.
     * @param album The album adjustment (in dB), or 0 if it is not present.
     */
    private data class Gain(val track: Float, val album: Float)

    /**
     * A raw ReplayGain adjustment.
     * @param key The tag's key.
     * @param value The tag's adjustment, in dB.
     */
    private data class GainTag(val key: String, val value: Float)
    // TODO: Try to phase this out

    companion object {
        private const val TAG_RG_TRACK = "replaygain_track_gain"
        private const val TAG_RG_ALBUM = "replaygain_album_gain"
        private const val R128_TRACK = "r128_track_gain"
        private const val R128_ALBUM = "r128_album_gain"

        private val REPLAY_GAIN_TAGS = arrayOf(TAG_RG_TRACK, TAG_RG_ALBUM, R128_ALBUM, R128_TRACK)
    }
}

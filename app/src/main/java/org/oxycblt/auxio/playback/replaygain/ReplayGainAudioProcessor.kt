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

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.BaseAudioProcessor
import com.google.android.exoplayer2.util.MimeTypes
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.pow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.metadata.TextTags
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.util.logD

/**
 * An [AudioProcessor] that handles ReplayGain values and their amplification of the audio stream.
 * Instead of leveraging the volume attribute like other implementations, this system manipulates
 * the bitstream itself to modify the volume, which allows the use of positive ReplayGain values.
 *
 * Note: This audio processor must be attached to a respective [Player] instance as a
 * [Player.Listener] to function properly.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ReplayGainAudioProcessor
@Inject
constructor(
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings
) : BaseAudioProcessor(), Player.Listener, PlaybackSettings.Listener {
    private var lastFormat: Format? = null

    private var volume = 1f
        set(value) {
            field = value
            // Processed bytes are no longer valid, flush the stream
            flush()
        }

    /**
     * Add this instance to the components required for it to function correctly.
     * @param player The [Player] to attach to. Should already have this instance as an audio
     * processor.
     */
    fun addToListeners(player: Player) {
        player.addListener(this)
        playbackSettings.registerListener(this)
    }

    /**
     * Remove this instance from the components required for it to function correctly.
     * @param player The [Player] to detach from. Should already have this instance as an audio
     * processor.
     */
    fun releaseFromListeners(player: Player) {
        player.removeListener(this)
        playbackSettings.unregisterListener(this)
    }

    // --- OVERRIDES ---

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
        // Try to find the currently playing track so we can update the ReplayGain adjustment
        // based on it.
        for (group in tracks.groups) {
            if (group.isSelected) {
                for (i in 0 until group.length) {
                    if (group.isTrackSelected(i)) {
                        applyReplayGain(group.getTrackFormat(i))
                        return
                    }
                }
            }
        }
        // Nothing selected, apply nothing
        applyReplayGain(null)
    }

    override fun onReplayGainSettingsChanged() {
        // ReplayGain config changed, we need to set it up again.
        applyReplayGain(lastFormat)
    }

    // --- REPLAYGAIN PARSING ---

    /**
     * Updates the volume adjustment based on the given [Format].
     * @param format The [Format] of the currently playing track, or null if nothing is playing.
     */
    private fun applyReplayGain(format: Format?) {
        lastFormat = format
        val gain = parseReplayGain(format ?: return)
        val preAmp = playbackSettings.replayGainPreAmp

        val adjust =
            if (gain != null) {
                logD("Found ReplayGain adjustment $gain")
                // ReplayGain is configurable, so determine what to do based off of the mode.
                val useAlbumGain =
                    when (playbackSettings.replayGainMode) {
                        // User wants track gain to be preferred. Default to album gain only if
                        // there is no track gain.
                        ReplayGainMode.TRACK -> gain.track == 0f
                        // User wants album gain to be preferred. Default to track gain only if
                        // here is no album gain.
                        ReplayGainMode.ALBUM -> gain.album != 0f
                        // User wants album gain to be used when in an album, track gain otherwise.
                        ReplayGainMode.DYNAMIC ->
                            playbackManager.parent is Album &&
                                playbackManager.queue.currentSong?.album == playbackManager.parent
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
     * Parse ReplayGain information from the given [Format].
     * @param format The [Format] to parse.
     * @return A [Adjustment] adjustment, or null if there were no valid adjustments.
     */
    private fun parseReplayGain(format: Format): Adjustment? {
        val textTags = TextTags(format.metadata ?: return null)
        var trackGain = 0f
        var albumGain = 0f

        // Most ReplayGain tags are formatted as a simple decibel adjustment in a custom
        // replaygain_*_gain tag.
        if (format.sampleMimeType != MimeTypes.AUDIO_OPUS) {
            textTags.id3v2["TXXX:$TAG_RG_TRACK_GAIN"]
                ?.run { first().parseReplayGainAdjustment() }
                ?.let { trackGain = it }
            textTags.id3v2["TXXX:$TAG_RG_ALBUM_GAIN"]
                ?.run { first().parseReplayGainAdjustment() }
                ?.let { albumGain = it }
            textTags.vorbis[TAG_RG_ALBUM_GAIN]
                ?.run { first().parseReplayGainAdjustment() }
                ?.let { trackGain = it }
            textTags.vorbis[TAG_RG_TRACK_GAIN]
                ?.run { first().parseReplayGainAdjustment() }
                ?.let { albumGain = it }
        } else {
            // Opus has it's own "r128_*_gain" ReplayGain specification, which requires dividing the
            // adjustment by 256 to get the gain. This is used alongside the base adjustment
            // intrinsic to the format to create the normalized adjustment. That base adjustment
            // is already handled by the media framework, so we just need to apply the more
            // specific adjustments.
            textTags.vorbis[TAG_R128_TRACK_GAIN]
                ?.run { first().parseReplayGainAdjustment() }
                ?.let { trackGain = it / 256f }
            textTags.vorbis[TAG_R128_ALBUM_GAIN]
                ?.run { first().parseReplayGainAdjustment() }
                ?.let { albumGain = it / 256f }
        }

        return if (trackGain != 0f || albumGain != 0f) {
            Adjustment(trackGain, albumGain)
        } else {
            null
        }
    }

    /**
     * Parse a ReplayGain adjustment into a float value.
     * @return A parsed adjustment float, or null if the adjustment had invalid formatting.
     */
    private fun String.parseReplayGainAdjustment() =
        replace(REPLAYGAIN_ADJUSTMENT_FILTER_REGEX, "").toFloatOrNull()

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

    override fun queueInput(inputBuffer: ByteBuffer) {
        val pos = inputBuffer.position()
        val limit = inputBuffer.limit()
        val buffer = replaceOutputBuffer(limit - pos)

        if (volume == 1f) {
            // Nothing to adjust, just copy the audio data.
            // isActive is technically a much better way of doing a no-op like this, but since
            // the adjustment can change during playback I'm largely forced to do this.
            buffer.put(inputBuffer.slice())
        } else {
            for (i in pos until limit step 2) {
                // 16-bit PCM audio, deserialize a little-endian short.
                var sample = inputBuffer.getLeShort(i)
                // Ensure we clamp the values to the minimum and maximum values possible
                // for the encoding. This prevents issues where samples amplified beyond
                // 1 << 16 will end up becoming truncated during the conversion to a short,
                // resulting in popping.
                sample =
                    (sample * volume)
                        .toInt()
                        .coerceAtLeast(Short.MIN_VALUE.toInt())
                        .coerceAtMost(Short.MAX_VALUE.toInt())
                        .toShort()
                buffer.putLeShort(sample)
            }
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
    private data class Adjustment(val track: Float, val album: Float)

    private companion object {
        const val TAG_RG_TRACK_GAIN = "replaygain_track_gain"
        const val TAG_RG_ALBUM_GAIN = "replaygain_album_gain"
        const val TAG_R128_TRACK_GAIN = "r128_track_gain"
        const val TAG_R128_ALBUM_GAIN = "r128_album_gain"

        /**
         * Matches non-float information from ReplayGain adjustments. Derived from vanilla music:
         * https://github.com/vanilla-music/vanilla
         */
        val REPLAYGAIN_ADJUSTMENT_FILTER_REGEX = Regex("[^\\d.-]")
    }
}

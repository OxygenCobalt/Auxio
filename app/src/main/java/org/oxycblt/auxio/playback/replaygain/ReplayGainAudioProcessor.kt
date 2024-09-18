/*
 * Copyright (c) 2022 Auxio Project
 * ReplayGainAudioProcessor.kt is part of Auxio.
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

import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.pow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.QueueChange
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
) : BaseAudioProcessor(), PlaybackStateManager.Listener, PlaybackSettings.Listener {
    private var volume = 1f
        set(value) {
            field = value
            // Processed bytes are no longer valid, flush the stream
            flush()
        }

    fun attach() {
        playbackManager.addListener(this)
        playbackSettings.registerListener(this)
    }

    /** Remove this instance from the components required for it to function correctly. */
    fun release() {
        playbackManager.removeListener(this)
        playbackSettings.unregisterListener(this)
    }

    // --- OVERRIDES ---

    override fun onIndexMoved(index: Int) {
        logD("Index moved, updating current song")
        applyReplayGain(playbackManager.currentSong)
    }

    override fun onQueueChanged(queue: List<Song>, index: Int, change: QueueChange) {
        // Other types of queue changes preserve the current song.
        if (change.type == QueueChange.Type.SONG) {
            applyReplayGain(playbackManager.currentSong)
        }
    }

    override fun onNewPlayback(
        parent: MusicParent?,
        queue: List<Song>,
        index: Int,
        isShuffled: Boolean
    ) {
        logD("New playback started, updating playback information")
        applyReplayGain(playbackManager.currentSong)
    }

    override fun onReplayGainSettingsChanged() {
        // ReplayGain config changed, we need to set it up again.
        applyReplayGain(playbackManager.currentSong)
    }

    // --- REPLAYGAIN PARSING ---

    /**
     * Updates the volume adjustment based on the given [Format].
     *
     * @param song The [Format] of the currently playing track, or null if nothing is playing.
     */
    private fun applyReplayGain(song: Song?) {
        if (song == null) {
            logD("Nothing playing, disabling adjustment")
            volume = 1f
            return
        }

        logD("Applying ReplayGain adjustment for $song")

        val gain = song.replayGainAdjustment
        val preAmp = playbackSettings.replayGainPreAmp

        // ReplayGain is configurable, so determine what to do based off of the mode.
        val resolvedAdjustment =
            when (playbackSettings.replayGainMode) {
                // User wants no adjustment.
                ReplayGainMode.OFF -> {
                    logD("ReplayGain is off")
                    null
                }
                // User wants track gain to be preferred. Default to album gain only if
                // there is no track gain.
                ReplayGainMode.TRACK -> {
                    logD("Using track strategy")
                    gain.track ?: gain.album
                }
                // User wants album gain to be preferred. Default to track gain only if
                // here is no album gain.
                ReplayGainMode.ALBUM -> {
                    logD("Using album strategy")
                    gain.album ?: gain.track
                }
                // User wants album gain to be used when in an album, track gain otherwise.
                ReplayGainMode.DYNAMIC -> {
                    logD("Using dynamic strategy")
                    gain.album?.takeIf {
                        playbackManager.parent is Album &&
                            playbackManager.currentSong?.album == playbackManager.parent
                    }
                        ?: gain.track
                }
            }

        val amplifiedAdjustment =
            if (resolvedAdjustment != null) {
                // Successfully resolved an adjustment, apply the corresponding pre-amp
                logD("Applying with pre-amp")
                resolvedAdjustment + preAmp.with
            } else {
                // No adjustment found, use the corresponding user-defined pre-amp
                logD("Applying without pre-amp")
                preAmp.without
            }

        logD("Applying ReplayGain adjustment ${amplifiedAdjustment}db")

        // Final adjustment along the volume curve.
        volume = 10f.pow(amplifiedAdjustment / 20f)
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
     *
     * @param at The index to read the [Short] from.
     */
    private fun ByteBuffer.getLeShort(at: Int) =
        get(at + 1).toInt().shl(8).or(get(at).toInt().and(0xFF)).toShort()

    /**
     * Always write a little-endian [Short] at the end of the [ByteBuffer].
     *
     * @param short The [Short] to write.
     */
    private fun ByteBuffer.putLeShort(short: Short) {
        put(short.toByte())
        put(short.toInt().shr(8).toByte())
    }
}

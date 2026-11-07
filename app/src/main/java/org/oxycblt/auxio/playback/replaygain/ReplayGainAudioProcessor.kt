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
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.pow
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.QueueChange
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song
import timber.log.Timber as L

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
@OptIn(markerClass = [UnstableApi::class])
class ReplayGainAudioProcessor
@Inject
constructor(
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
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
        L.d("Index moved, updating current song")
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
        isShuffled: Boolean,
    ) {
        L.d("New playback started, updating playback information")
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
            L.d("Nothing playing, disabling adjustment")
            volume = 1f
            return
        }

        L.d("Applying ReplayGain adjustment for $song")

        val gain = song.replayGainAdjustment
        val preAmp = playbackSettings.replayGainPreAmp

        // ReplayGain is configurable, so determine what to do based off of the mode.
        val resolvedAdjustment =
            when (playbackSettings.replayGainMode) {
                // User wants no adjustment.
                ReplayGainMode.OFF -> {
                    L.d("ReplayGain is off")
                    null
                }
                // User wants track gain to be preferred. Default to album gain only if
                // there is no track gain.
                ReplayGainMode.TRACK -> {
                    L.d("Using track strategy")
                    gain.track ?: gain.album
                }
                // User wants album gain to be preferred. Default to track gain only if
                // here is no album gain.
                ReplayGainMode.ALBUM -> {
                    L.d("Using album strategy")
                    gain.album ?: gain.track
                }
                // User wants album gain to be used when in an album, track gain otherwise.
                ReplayGainMode.DYNAMIC -> {
                    L.d("Using dynamic strategy")
                    gain.album?.takeIf {
                        playbackManager.parent is Album &&
                            playbackManager.currentSong?.album == playbackManager.parent
                    } ?: gain.track
                }
            }

        val amplifiedAdjustment =
            if (resolvedAdjustment != null) {
                // Successfully resolved an adjustment, apply the corresponding pre-amp
                L.d("Applying with pre-amp")
                resolvedAdjustment + preAmp.with
            } else {
                // No adjustment found, use the corresponding user-defined pre-amp
                L.d("Applying without pre-amp")
                preAmp.without
            }

        L.d("Applying ReplayGain adjustment ${amplifiedAdjustment}db")

        // Final adjustment along the volume curve.
        volume = 10f.pow(amplifiedAdjustment / 20f)
    }

    // --- AUDIO PROCESSOR IMPLEMENTATION ---

    // Set by onConfigure, used by queueInput -- was hardcoded to exactly 2 (16-bit), which meant
    // this processor -- sitting in the AudioProcessor chain ahead of *every* output path,
    // DAC or speaker -- rejected anything else outright
    // (AudioProcessor.UnhandledAudioFormatException),
    // forcing the renderer to decode down to 16-bit even for a genuinely 24-bit source so this
    // processor could accept it at all. Confirmed, reproduced: a real 24-bit/96kHz FLAC
    // (ffprobe: bits_per_raw_sample=24) was still seen by UsbAudioOutputProvider as a 16-bit
    // track, meaning no 24-bit content ever actually got bit-perfect treatment through this
    // app, independent of USB vs speaker. Now handles 16/24/32-bit PCM (2/3/4 bytes), all signed
    // little-endian, which covers every encoding UsbAudioOutputProvider's own encodingFor() maps
    // a device format to.
    private var bytesPerSample = 2

    override fun onConfigure(
        inputAudioFormat: AudioProcessor.AudioFormat
    ): AudioProcessor.AudioFormat {
        val bytes =
            when (inputAudioFormat.encoding) {
                C.ENCODING_PCM_16BIT -> 2
                C.ENCODING_PCM_24BIT -> 3
                C.ENCODING_PCM_32BIT -> 4
                else -> null
            }
        if (bytes == null) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
        bytesPerSample = bytes
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val pos = inputBuffer.position()
        val limit = inputBuffer.limit()
        val buffer = replaceOutputBuffer(limit - pos)
        val bytes = bytesPerSample

        if (volume == 1f) {
            // Nothing to adjust, just copy the audio data.
            // isActive is technically a much better way of doing a no-op like this, but since
            // the adjustment can change during playback I'm largely forced to do this.
            buffer.put(inputBuffer.slice())
        } else {
            // Full-scale range for whatever bit depth this is, e.g. +-32768 for 16-bit,
            // +-8388608 for 24-bit -- same clamping purpose as before (prevents a sample
            // amplified past full scale from wrapping around on truncation and popping), just
            // generalized instead of hardcoded to Short's range.
            val maxValue = (1L shl (bytes * 8 - 1)) - 1
            val minValue = -(1L shl (bytes * 8 - 1))
            for (i in pos until limit step bytes) {
                var sample = inputBuffer.getLeSigned(i, bytes)
                sample = (sample * volume).toLong().coerceIn(minValue, maxValue)
                buffer.putLeSigned(sample, bytes)
            }
        }

        inputBuffer.position(limit)
        buffer.flip()
    }

    /** Reads a little-endian signed integer of [bytes] length from the [ByteBuffer] at [at]. */
    private fun ByteBuffer.getLeSigned(at: Int, bytes: Int): Long {
        var value = 0L
        for (b in 0 until bytes) {
            value = value or ((get(at + b).toLong() and 0xFF) shl (8 * b))
        }
        // Sign-extend from `bytes * 8` bits up to a full Long.
        val shift = 64 - bytes * 8
        return (value shl shift) shr shift
    }

    /** Writes a little-endian signed integer of [bytes] length to the end of the [ByteBuffer]. */
    private fun ByteBuffer.putLeSigned(value: Long, bytes: Int) {
        for (b in 0 until bytes) {
            put(((value shr (8 * b)) and 0xFF).toByte())
        }
    }
}

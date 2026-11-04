/*
 * Copyright (c) 2026 Auxio Project
 * UsbPipelineFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.settings.categories

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.control.UsbControlState
import org.ostrichbat2k.usbdac.streaming.UsbPipelineStats
import org.oxycblt.auxio.databinding.FragmentUsbPipelineBinding
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.collectImmediately

/**
 * A live signal-flow view of where audio data actually is right now, from the file's own declared
 * format through to the USB wire -- unlike every other USB diagnostic screen here, which only
 * updates on a real state change (a track switching route, a control being read back), this one
 * re-renders continuously while something is genuinely streaming, driven by [UsbPipelineStats]'s
 * own ~160ms reporting cadence (see where it's sent from in
 * [org.ostrichbat2k.usbdac.UsbAudioOutput]).
 */
@AndroidEntryPoint
class UsbPipelineFragment : ViewBindingFragment<FragmentUsbPipelineBinding>() {
    @Inject lateinit var usbAudioManager: UsbAudioManager

    // Kept across update() calls purely to derive a URBs/sec rate from the delta between two
    // consecutive UsbPipelineStats emissions -- see UsbPipelineStats's own doc comment on why it
    // reports raw cumulative counters instead of an already-computed rate.
    private var previousStats: UsbPipelineStats? = null

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentUsbPipelineBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentUsbPipelineBinding,
        savedInstanceState: Bundle?,
    ) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.usbPipelineToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        collectImmediately(
            usbAudioManager.playbackRoute,
            usbAudioManager.controlState,
            usbAudioManager.pipelineStats,
            ::update,
        )
    }

    private fun update(
        route: UsbAudioManager.PlaybackRoute,
        controlState: UsbControlState?,
        stats: UsbPipelineStats?,
    ) {
        val binding = requireBinding()

        binding.usbPipelineFile.text = fileLabel(route)
        binding.usbPipelineRoute.text = routeLabel(route)

        if (stats != null && stats.bufferCapacityBytes > 0) {
            val fillPercent =
                (100 * stats.bufferedBytes / stats.bufferCapacityBytes).coerceIn(0, 100)
            binding.usbPipelineBufferBar.progress = fillPercent
            val channels = usbAudioManager.capabilities.value?.topology?.channelCount ?: 0
            binding.usbPipelineBufferLabel.text =
                "${msFor(stats.bufferedBytes, controlState, channels)}ms / " +
                    "${msFor(stats.bufferCapacityBytes, controlState, channels)}ms buffered ($fillPercent%)"
        } else {
            binding.usbPipelineBufferBar.progress = 0
            binding.usbPipelineBufferLabel.text = "Idle -- nothing streaming right now"
        }

        // Only a real, same-session pair of emissions (not the first one ever seen, and not one
        // straddling a stream stop-then-restart, where urbsSubmitted resets to 0 on the new
        // instance and would otherwise read as a nonsensical negative rate) computes a rate.
        val previous = previousStats
        binding.usbPipelineUrbs.text =
            if (
                stats != null &&
                    previous != null &&
                    stats.atMs > previous.atMs &&
                    stats.urbsSubmitted >= previous.urbsSubmitted
            ) {
                val urbsPerSec =
                    (stats.urbsSubmitted - previous.urbsSubmitted) * 1000.0 /
                        (stats.atMs - previous.atMs)
                "%.1f URBs/sec".format(urbsPerSec)
            } else {
                "Idle -- nothing streaming right now"
            }
        previousStats = stats
        binding.usbPipelineUnderruns.text = "${stats?.underrunCount ?: 0} underruns this stream"

        val capabilities = usbAudioManager.capabilities.value
        binding.usbPipelineDevice.text =
            if (capabilities != null && controlState != null) {
                "${capabilities.deviceName} -- ${controlState.activeFormat.bitResolution}-bit @ ${controlState.sampleRateHz}Hz"
            } else {
                "No active USB session"
            }
    }

    /**
     * The file's own declared format -- both [UsbAudioManager.PlaybackRoute.Direct] and
     * [UsbAudioManager.PlaybackRoute.Fallback] carry it identically; see the latter's own doc
     * comment for why that's true even though HAL doesn't actually honor it.
     */
    private fun fileLabel(route: UsbAudioManager.PlaybackRoute): String =
        when (route) {
            is UsbAudioManager.PlaybackRoute.Direct ->
                "${route.bitDepth ?: "?"}-bit / ${route.sampleRateHz}Hz / ${route.channels}ch"
            is UsbAudioManager.PlaybackRoute.Fallback ->
                "${route.bitDepth ?: "?"}-bit / ${route.sampleRateHz}Hz / ${route.channels}ch"
            UsbAudioManager.PlaybackRoute.Switching -> "Unknown -- switching output…"
        }

    private fun routeLabel(route: UsbAudioManager.PlaybackRoute): String =
        when (route) {
            is UsbAudioManager.PlaybackRoute.Direct -> "Direct -- bit-perfect straight to the DAC"
            is UsbAudioManager.PlaybackRoute.Fallback ->
                "Fallback -- through Android's shared HAL mixer (format above not honored)"
            UsbAudioManager.PlaybackRoute.Switching -> "Switching output…"
        }

    /**
     * Converts a byte count in the ring buffer to milliseconds of audio, using the device's
     * currently active format/rate/channel count -- returns 0 rather than crashing if nothing's
     * active, since this is purely a live diagnostic label, not a value anything else depends on.
     */
    private fun msFor(bytes: Int, controlState: UsbControlState?, channels: Int): Long {
        val format = controlState?.activeFormat ?: return 0
        val sampleRateHz = controlState.sampleRateHz
        val bytesPerFrame = channels * format.subslotSize
        if (bytesPerFrame <= 0 || sampleRateHz <= 0) return 0
        return bytes.toLong() * 1000L / bytesPerFrame / sampleRateHz
    }
}

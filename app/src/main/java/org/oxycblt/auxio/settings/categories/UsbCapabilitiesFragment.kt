/*
 * Copyright (c) 2026 Auxio Project
 * UsbCapabilitiesFragment.kt is part of Auxio.
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

import android.hardware.usb.UsbConstants
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.control.UsbControlState
import org.ostrichbat2k.usbdac.protocol.UacNonPcmFormat
import org.ostrichbat2k.usbdac.protocol.UacStreamFormat
import org.ostrichbat2k.usbdac.protocol.UsbAudioController
import org.ostrichbat2k.usbdac.protocol.UsbDeviceCapabilities
import org.ostrichbat2k.usbdac.protocol.UsbInterfaceInfo
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.BasePreferenceFragment
import org.oxycblt.auxio.util.collectImmediately

/**
 * A glossary-style breakdown of everything [UsbAudioManager.capabilities] found on the attached USB
 * DAC's descriptors -- unlike the raw field dump this replaced, every row explains *why* that field
 * exists and what it actually controls, since none of this (Clock Source, Feature Unit, Mixer Unit,
 * Power Domain...) is self-explanatory USB Audio Class jargon to someone who isn't already familiar
 * with the spec. Where a field's current, live value can be read back from the device (clock
 * frequency/valid, mute/volume, mixer gains, which Power Domain state or Format is actually active
 * right now) it's shown alongside the static descriptor info, not just what the device merely
 * declares it's capable of.
 */
@AndroidEntryPoint
class UsbCapabilitiesFragment : BasePreferenceFragment(R.xml.preferences_usb_capabilities) {
    @Inject lateinit var usbAudioManager: UsbAudioManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectImmediately(usbAudioManager.capabilities, usbAudioManager.controlState, ::update)
    }

    private fun update(capabilities: UsbDeviceCapabilities?, controlState: UsbControlState?) {
        val deviceCategory =
            findPreference<PreferenceCategory>(
                getString(R.string.set_key_usb_capabilities_device_category)
            )
        val interfacesCategory =
            findPreference<PreferenceCategory>(
                getString(R.string.set_key_usb_capabilities_interfaces_category)
            )
        val topologyCategory =
            findPreference<PreferenceCategory>(
                getString(R.string.set_key_usb_capabilities_topology_category)
            )
        val powerCategory =
            findPreference<PreferenceCategory>(
                getString(R.string.set_key_usb_capabilities_power_category)
            )
        val formatsCategory =
            findPreference<PreferenceCategory>(
                getString(R.string.set_key_usb_capabilities_formats_category)
            )
        val nonPcmCategory =
            findPreference<PreferenceCategory>(
                getString(R.string.set_key_usb_capabilities_nonpcm_category)
            )
        deviceCategory?.removeAll()
        interfacesCategory?.removeAll()
        topologyCategory?.removeAll()
        powerCategory?.removeAll()
        formatsCategory?.removeAll()
        nonPcmCategory?.removeAll()

        if (capabilities == null) {
            deviceCategory?.addPreference(info(getString(R.string.set_usb_capabilities_none)))
            return
        }

        val topology = capabilities.topology

        deviceCategory?.apply {
            addPreference(
                info(
                    label = "Name",
                    value = capabilities.deviceName,
                    summary =
                        "What the device calls itself. May not match the printed brand or model " +
                            "if the manufacturer used a generic USB description.",
                )
            )
            addPreference(
                info(
                    label = "USB ID",
                    value = "%04x:%04x".format(capabilities.vendorId, capabilities.productId),
                    summary =
                        "Vendor ID : product ID, a fixed pair baked into the hardware. Identifies " +
                            "the exact chipset -- useful for looking up known quirks online.",
                )
            )
            addPreference(
                info(
                    label = "USB Audio Class",
                    value = "UAC ${topology.uacVersion}.0",
                    summary = uacVersionExplanation(topology.uacVersion),
                )
            )
        }

        interfacesCategory?.apply {
            if (capabilities.interfaces.isEmpty()) {
                addPreference(
                    info(
                        "Interface list unavailable -- this can happen right after activation; " +
                            "reopen this screen or retry activation."
                    )
                )
            }
            for (iface in capabilities.interfaces) {
                addPreference(
                    info(
                        label = "Interface ${iface.number}",
                        value = usbInterfaceClassName(iface.interfaceClass),
                        summary = usbInterfaceRoleExplanation(iface),
                    )
                )
            }
        }

        topologyCategory?.apply {
            addPreference(
                info(
                    label = "Configuration",
                    value = "${topology.configurationValue}",
                    summary =
                        "Which of the device's USB configurations Auxio selected. Almost every " +
                            "device only offers one.",
                )
            )
            addPreference(
                info(
                    label = "Control interface",
                    value = "${topology.controlInterfaceNumber}",
                    summary =
                        "The USB interface Auxio sends commands over -- volume, mute, sample " +
                            "rate, and so on.",
                )
            )
            addPreference(
                info(
                    label = "Streaming interface",
                    value = "${topology.streamingInterfaceNumber}",
                    summary = "The USB interface Auxio sends the actual audio data over.",
                )
            )
            addPreference(
                info(
                    label = "Channel count",
                    value =
                        if (topology.channelCount > 0) "${topology.channelCount}" else "unknown",
                    summary =
                        if (topology.channelCount > 0) {
                            "How many audio channels the device's input declares (2 = stereo). " +
                                "Read directly from its descriptors, not assumed."
                        } else {
                            "UAC 3.0 pushes this into a separate descriptor fetch Auxio doesn't " +
                                "do -- falls back to assuming stereo (2 channels)."
                        },
                )
            )
            val clockPreference =
                info(
                    label = "Clock source",
                    value =
                        if (topology.clockSourceId >= 0) {
                            "${topology.clockSourceId} (reading…)"
                        } else {
                            "none"
                        },
                    summary =
                        if (topology.clockSourceId >= 0) {
                            "Controls the device's sample rate (how many times a second it plays " +
                                "a new value) -- Auxio sets this to match whatever's playing, and " +
                                "reads it back along with whether the device reports its clock as " +
                                "actually locked, not just set."
                        } else {
                            "This device has no separate clock entity -- UAC 1.0 doesn't have " +
                                "the concept. Instead, each format below declares its own fixed " +
                                "sample rate(s) directly, set via the audio connection itself."
                        },
                )
            addPreference(clockPreference)
            if (topology.clockSourceId >= 0) {
                // getClockInfo() does real, synchronous USB control transfers (up to ~1s each,
                // two of them) -- keep them off the main thread instead of blocking this whole
                // screen's redraw on them every time capabilities/controlState re-emit, which
                // used to happen here on every single call.
                viewLifecycleOwner.lifecycleScope.launch {
                    val liveInfo = withContext(Dispatchers.IO) { usbAudioManager.getClockInfo() }
                    clockPreference.title =
                        "Clock source: ${topology.clockSourceId} (${clockStateLabel(liveInfo)})"
                }
                addPreference(
                    info(
                        label = "Clock supported rates",
                        value = clockSupportedRatesLabel(controlState),
                        summary =
                            "What the clock source itself says it can run at, read via GET_RANGE " +
                                "at activation -- not assumed. Auxio only ever requests a rate " +
                                "that falls in one of these ranges; asking for anything else was " +
                                "confirmed, on a real device, to silently keep the hardware clock " +
                                "at its previous rate while still reporting success, which plays " +
                                "back pitched up and sped up.",
                    )
                )
            }
            addPreference(
                info(
                    label = "Output control",
                    value =
                        if (topology.outputFeatureUnitId >= 0) {
                            "unit ${topology.outputFeatureUnitId} (${outputControlLabel(controlState)})"
                        } else {
                            "none found"
                        },
                    summary =
                        if (topology.outputFeatureUnitId >= 0) {
                            "Controls mute and volume for what the device actually plays out -- " +
                                "this is what Auxio adjusts when you move the volume slider, and " +
                                "the current mute/volume reading, shown in parentheses, comes " +
                                "from the device itself, not from what Auxio last set."
                        } else {
                            "Auxio couldn't find the part of the device that handles mute/volume " +
                                "-- it may not be independently adjustable from this app."
                        },
                )
            )
            addPreference(
                info(
                    label = "Bit-perfect capable",
                    value = if (controlState?.volumeRange != null) "yes" else "no",
                    summary =
                        "Whether Auxio can confirm where this device's unity (0dB, no digital " +
                            "attenuation) gain point is -- the output control above was found " +
                            "*and* its volume range could be read back. Gates whether the " +
                            "\"Bit-perfect mode\" switch on the USB Audio screen can be turned " +
                            "on at all.",
                )
            )
            addPreference(
                info(
                    label = "Internal mixer",
                    value =
                        if (topology.mixerUnitId >= 0) {
                            "unit ${topology.mixerUnitId}, channel ${topology.mixerInputChannelBase}"
                        } else {
                            "none"
                        },
                    summary =
                        if (topology.mixerUnitId >= 0) {
                            "Some devices combine USB audio with another source (e.g. a built-in " +
                                "microphone) before sending it out. Auxio makes sure its own " +
                                "input into this mixer isn't silenced, separately from the " +
                                "output control above -- both have to be right for sound to come " +
                                "out. Its actual per-channel gain, read from the device, is below."
                        } else {
                            "No internal mixing stage was found between Auxio's input and the " +
                                "device's output -- nothing extra to configure here."
                        },
                )
            )
            if (topology.mixerUnitId >= 0 && controlState != null) {
                for ((inCh, outCh) in controlState.mixerChannels) {
                    addPreference(
                        info(
                            label = "Mixer gain in=$inCh out=$outCh",
                            value =
                                gainLabel(
                                    controlState.mixerGains[inCh to outCh],
                                    controlState.mixerGainRange,
                                ),
                        )
                    )
                }
            }
        }

        powerCategory?.apply {
            if (topology.powerDomainId >= 0) {
                val powerPreference =
                    info(
                        label = "Power domain",
                        value = "${topology.powerDomainId} (reading…)",
                        summary =
                            "A UAC 3.0 power-saving feature: this part of the device can sleep " +
                                "when idle and has to be woken before streaming. Its three " +
                                "possible states are D0 (full power, ready to stream), D1 (light " +
                                "sleep, wakes in up to ${topology.powerDomainD1RecoveryUs / 1000}ms), " +
                                "and D2 (deep sleep, wakes in up to " +
                                "${topology.powerDomainD2RecoveryUs / 1000}ms). Auxio always wakes " +
                                "it to D0 and waits out that recovery time before streaming; " +
                                "skipping that step can produce clean, error-free silence with " +
                                "nothing to show why.",
                    )
                addPreference(powerPreference)
                // getPowerDomainState() is a real, synchronous USB control transfer (up to ~1s) --
                // same reasoning as the clock read above: off the main thread, not blocking this
                // screen's redraw on it.
                viewLifecycleOwner.lifecycleScope.launch {
                    val liveState =
                        withContext(Dispatchers.IO) { usbAudioManager.getPowerDomainState() }
                    powerPreference.title =
                        "Power domain: ${topology.powerDomainId} (${powerStateLabel(liveState)})"
                }
            } else {
                addPreference(
                    info(
                        label = "Power domain",
                        value = "none",
                        summary =
                            if (topology.uacVersion == 3) {
                                "No power-saving domain covers Auxio's audio path on this device " +
                                    "-- it's assumed to always be ready."
                            } else {
                                "UAC ${topology.uacVersion}.0 doesn't have this concept at all " +
                                    "-- it's a UAC 3.0 addition. This device's audio circuitry is " +
                                    "assumed to always be ready."
                            },
                    )
                )
            }
        }

        formatsCategory?.apply {
            addPreference(
                info(
                    label = "What's a format?",
                    value = null,
                    summary =
                        "Each one is a selectable audio quality mode -- bit depth, and on UAC " +
                            "1.0, sample rate too -- that this device supports. Auxio picks one " +
                            "automatically based on what's playing and falls back to normal " +
                            "Android audio output for anything none of them can handle.",
                )
            )
            for ((index, format) in topology.formats.withIndex()) {
                val isActive = controlState?.activeFormat == format
                addPreference(
                    info(
                        label = "Format ${index + 1}" + if (isActive) " (active now)" else "",
                        value = formatValue(format),
                        summary = formatSummary(format),
                    )
                )
            }
        }

        nonPcmCategory?.apply {
            if (topology.nonPcmFormats.isEmpty()) {
                addPreference(
                    info(
                        "None found on this device -- every alt setting it declares is ordinary PCM."
                    )
                )
            }
            for ((index, format) in topology.nonPcmFormats.withIndex()) {
                addPreference(
                    info(
                        label = "Alt setting ${format.alternateSetting}",
                        value = nonPcmFormatValue(format),
                        summary = nonPcmFormatSummary(format),
                    )
                )
            }
        }
    }

    /** A short label for one of [UsbAudioController]'s PD_STATE_* constants, or "unknown". */
    private fun powerStateLabel(state: Int?) =
        when (state) {
            UsbAudioController.PD_STATE_D0 -> "currently D0, full power"
            UsbAudioController.PD_STATE_D1 -> "currently D1, light sleep"
            UsbAudioController.PD_STATE_D2 -> "currently D2, deep sleep"
            else -> "current state unknown"
        }

    /**
     * Formats an already-fetched (off the main thread, see [update]) clock frequency/valid pair.
     */
    private fun clockStateLabel(info: Pair<Int?, Boolean?>?): String {
        val (frequency, valid) = info ?: (null to null)
        val freqPart = frequency?.let { "${it}Hz" } ?: "frequency unknown"
        val validPart =
            when (valid) {
                true -> "locked"
                false -> "not locked"
                null -> "lock state unknown"
            }
        return "$freqPart, $validPart"
    }

    /** The Clock Source's declared supported rate sub-ranges, formatted for display. */
    private fun clockSupportedRatesLabel(controlState: UsbControlState?): String {
        val ranges = controlState?.clockSupportedRates
        if (ranges.isNullOrEmpty()) return "unknown -- GET_RANGE query failed or not read yet"
        return ranges.joinToString(", ") { (min, max) ->
            if (min == max) "${min}Hz" else "${min}-${max}Hz"
        }
    }

    /** [controlState]'s live mute/volume reading for the Output control row. */
    private fun outputControlLabel(controlState: UsbControlState?): String {
        if (controlState == null) return "not read yet -- no active session"
        if (controlState.muted == true) return "muted"
        return "unmuted, volume ${gainLabel(controlState.volume, controlState.volumeRange)}"
    }

    /** A (1/256 dB) gain reading plus its device-reported range, e.g. "0 (range -32768..0)". */
    private fun gainLabel(value: Int?, range: Pair<Int, Int>?): String {
        val valuePart = value?.toString() ?: "unknown"
        val rangePart = range?.let { " (range ${it.first}..${it.second})" } ?: ""
        return "$valuePart/256dB$rangePart"
    }

    /** A human name for an `UsbConstants.USB_CLASS_*` value, falling back to the raw number. */
    private fun usbInterfaceClassName(interfaceClass: Int): String =
        when (interfaceClass) {
            UsbConstants.USB_CLASS_AUDIO -> "Audio"
            UsbConstants.USB_CLASS_HID -> "HID (buttons/remote)"
            UsbConstants.USB_CLASS_COMM -> "Communications"
            UsbConstants.USB_CLASS_MASS_STORAGE -> "Mass storage"
            UsbConstants.USB_CLASS_HUB -> "Hub"
            UsbConstants.USB_CLASS_VIDEO -> "Video"
            UsbConstants.USB_CLASS_PRINTER -> "Printer"
            UsbConstants.USB_CLASS_STILL_IMAGE -> "Still image"
            UsbConstants.USB_CLASS_WIRELESS_CONTROLLER -> "Wireless controller"
            UsbConstants.USB_CLASS_MISC -> "Miscellaneous"
            UsbConstants.USB_CLASS_VENDOR_SPEC -> "Vendor-specific"
            else -> "Unknown class 0x%02x".format(interfaceClass)
        }

    /**
     * Explains what Auxio actually does with [iface] -- a composite device (a headset with its own
     * inline-remote HID interface alongside its audio ones, confirmed on a real EarPods) has
     * interfaces here that none of the rest of this screen ever mentions, and it matters to say
     * plainly that Auxio leaves them alone: a direct-USB claim evicting the kernel driver from
     * every interface briefly, confirmed, reproduced, used to leave a non-audio one -- a remote's
     * HID interface -- stuck unclaimed with its physical buttons dead for the rest of the session,
     * once nothing ever gave it back.
     */
    private fun usbInterfaceRoleExplanation(iface: UsbInterfaceInfo): String {
        val altsNote =
            if (iface.alternateSettingCount > 1)
                " (${iface.alternateSettingCount} alternate settings)"
            else ""
        return when (iface.role) {
            UsbInterfaceInfo.Role.CONTROL ->
                "Auxio's control interface$altsNote -- see Audio routing below."
            UsbInterfaceInfo.Role.STREAMING ->
                "Auxio's streaming interface$altsNote -- see Audio routing below."
            UsbInterfaceInfo.Role.OTHER_AUDIO ->
                "Audio class, but not the playback path Auxio uses$altsNote -- likely a capture " +
                    "(microphone) interface. Auxio doesn't claim this."
            UsbInterfaceInfo.Role.NON_AUDIO ->
                "Not audio at all$altsNote -- e.g. a remote control's buttons. Auxio never claims " +
                    "this and leaves it for the rest of the system to use normally."
        }
    }

    private fun uacVersionExplanation(version: Int) =
        when (version) {
            1 ->
                "The oldest, simplest version of the USB Audio Class standard -- common in " +
                    "inexpensive dongles. No per-device clock entity and usually no power " +
                    "management; each format just declares the rate(s) it supports directly."
            2 ->
                "Added higher sample/bit rates and a proper Clock Source entity (letting the " +
                    "device run its own precise timing instead of following the host's clock) " +
                    "compared to UAC 1.0."
            3 ->
                "The newest version -- adds fine-grained power management (Power Domains, see " +
                    "below) and more flexible internal routing on top of what UAC 2.0 offers."
            else -> "Unrecognized UAC version."
        }

    private fun formatValue(format: UacStreamFormat): String {
        val rates = format.sampleRates
        return if (rates.isNotEmpty()) {
            "${format.bitResolution}-bit, ${rates.joinToString("/") { "${it}Hz" }}"
        } else {
            "${format.bitResolution}-bit"
        }
    }

    private fun formatSummary(format: UacStreamFormat): String {
        val rateNote =
            if (format.sampleRates.isEmpty()) {
                " Sample rate is set via the clock source above, not per-format."
            } else {
                ""
            }
        val syncNote =
            if (format.isAsync) {
                "the device supplies its own precise timing"
            } else {
                "timing follows the host's USB clock"
            }
        return "Alternate setting ${format.alternateSetting} · ${format.subslotSize}-byte " +
            "samples · sent over endpoint 0x${"%02x".format(format.endpointAddress)} in packets " +
            "up to ${format.maxPacketSize} bytes · $syncNote.$rateNote"
    }

    private fun nonPcmFormatValue(format: UacNonPcmFormat): String =
        if (format.isRawData) "Raw bitstream (e.g. DSD)" else "Non-PCM (type undetermined)"

    /**
     * Explains why this alt setting shows up here instead of under "Formats" above -- unlike every
     * PCM format there, this one declares the exact same byte layout as an ordinary PCM alt setting
     * (same bit depth/subslot size, same endpoint) but a different meaning for those bytes, which
     * only its bmFormats field reveals. Auxio never selects it for playback.
     */
    private fun nonPcmFormatSummary(format: UacNonPcmFormat): String {
        val kindNote =
            if (format.isRawData) {
                "Its bmFormats field sets the \"raw data\" bit -- the real-world convention a " +
                    "device uses to carry DSD (or another non-PCM bitstream) disguised inside " +
                    "what otherwise looks like an ordinary PCM alt setting."
            } else {
                "Its bmFormats field doesn't declare the PCM bit, but also isn't the specific " +
                    "\"raw data\" bit Auxio recognizes as DSD -- likely a genuinely compressed " +
                    "format (e.g. MPEG, AC-3) this parser doesn't otherwise identify."
            }
        return "Sent over endpoint 0x${"%02x".format(format.endpointAddress)} in packets up to " +
            "${format.maxPacketSize} bytes. $kindNote"
    }

    /** A read-only "[label]: [value]" row with an explanatory [summary] underneath. */
    private fun info(label: String, value: String?, summary: String? = null): Preference =
        Preference(requireContext()).apply {
            isSelectable = false
            title = if (value != null) "$label: $value" else label
            if (summary != null) this.summary = summary
        }

    /** A single-line, label-less informational row (e.g. an empty-state message). */
    private fun info(message: String): Preference =
        Preference(requireContext()).apply {
            isSelectable = false
            title = message
        }
}

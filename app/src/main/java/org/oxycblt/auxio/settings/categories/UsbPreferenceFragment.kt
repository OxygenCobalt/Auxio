/*
 * Copyright (c) 2026 Auxio Project
 * UsbPreferenceFragment.kt is part of Auxio.
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
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.UsbDacPreferences
import org.ostrichbat2k.usbdac.control.UsbControlState
import org.ostrichbat2k.usbdac.discovery.UsbAudioDevice
import org.ostrichbat2k.usbdac.protocol.UsbDeviceCapabilities
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.BasePreferenceFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.navigateSafe

/**
 * USB DAC status and activation log, for diagnosing direct USB audio output -- shows whatever's
 * currently attached/permitted and a running history of what [UsbAudioManager] did the last time it
 * tried to activate a session, so changes across attempts (settling delays, control-transfer fixes,
 * etc.) can be tracked from the device itself instead of pulling logcat each time.
 */
@AndroidEntryPoint
class UsbPreferenceFragment : BasePreferenceFragment(R.xml.preferences_usb) {
    @Inject lateinit var usbAudioManager: UsbAudioManager
    @Inject lateinit var usbSettings: UsbDacPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectImmediately(usbAudioManager.devices, ::updateDevice)
        collectImmediately(usbAudioManager.diagnosticLog, ::updateLog)
        collectImmediately(usbAudioManager.capabilities, ::updateCapabilities)
        collectImmediately(usbAudioManager.controlState, ::updateControls)
    }

    private fun updateDevice(devices: List<UsbAudioDevice>) {
        val summary =
            devices
                .joinToString("\n") { "${it.displayName} (permission: ${it.hasPermission})" }
                .ifEmpty { getString(R.string.set_usb_device_none) }
        findPreference<Preference>(getString(R.string.set_key_usb_device))?.summary = summary
    }

    private fun updateLog(lines: List<String>) {
        val category =
            findPreference<PreferenceCategory>(getString(R.string.set_key_usb_log_category))
                ?: return
        category.removeAll()
        if (lines.isEmpty()) {
            category.addPreference(
                Preference(requireContext()).apply {
                    isSelectable = false
                    title = getString(R.string.set_usb_log_empty)
                }
            )
            return
        }
        for (line in lines) {
            category.addPreference(
                Preference(requireContext()).apply {
                    isSelectable = false
                    title = line
                }
            )
        }
    }

    /**
     * Updates the "Capabilities" entry point's summary with a one-line preview of what's been found
     * so far -- the full, explained breakdown lives on [UsbCapabilitiesFragment], navigated to on
     * tap (see [onPreferenceTreeClick]). Independent of whether Auxio went on to successfully
     * configure the device, so this still updates even if [updateControls] ends up showing no
     * active session.
     */
    private fun updateCapabilities(capabilities: UsbDeviceCapabilities?) {
        val summary =
            capabilities?.let { "${it.deviceName} -- UAC ${it.topology.uacVersion}.0" }
                ?: getString(R.string.set_usb_capabilities_none)
        findPreference<Preference>(getString(R.string.set_key_usb_capabilities))?.summary = summary
    }

    /**
     * Rebuilds the "Controls" category from what [state] says this device actually supports -- a
     * device with a single format offers no format picker, one with no Mixer Unit in its topology
     * offers no mixer-gain sliders, etc. Nothing here is specific to any one device model.
     *
     * [state] updates on every mute/volume/mixer-gain write (this screen's own, or a physical
     * volume key) and every track's format negotiation, which is often -- a full
     * [PreferenceCategory.removeAll] + rebuild on every single one of those used to tear down and
     * recreate every control's widget just because one value changed, visibly flickering the whole
     * category and risking disrupting a slider the user wasn't even touching (e.g. adjusting one
     * mixer-gain channel used to rebuild every other channel's slider mid-gesture too). Now only
     * does that full rebuild when the actual *set* of controls needs to change (see
     * [desiredControlKeys]); an unchanged set just updates each existing widget's value in place
     * via [updateControlValuesInPlace], which only re-renders that one row.
     */
    private fun updateControls(state: UsbControlState?) {
        updateBitPerfectAvailability(state)

        val category =
            findPreference<PreferenceCategory>(getString(R.string.set_key_usb_controls_category))
                ?: return

        if (state == null) {
            if (
                category.preferenceCount != 1 || category.getPreference(0).key != KEY_CONTROLS_NONE
            ) {
                category.removeAll()
                category.addPreference(
                    Preference(requireContext()).apply {
                        key = KEY_CONTROLS_NONE
                        isSelectable = false
                        title = getString(R.string.set_usb_controls_none)
                    }
                )
            }
            return
        }

        val currentKeys = (0 until category.preferenceCount).map { category.getPreference(it).key }
        if (currentKeys == desiredControlKeys(state)) {
            updateControlValuesInPlace(category, state)
            return
        }

        category.removeAll()
        if (state.formats.size > 1) {
            category.addPreference(
                ListPreference(requireContext()).apply {
                    key = getString(R.string.set_key_usb_format)
                    title = getString(R.string.set_usb_format)
                    isPersistent = false
                    entries = state.formats.map { "${it.bitResolution}-bit" }.toTypedArray()
                    entryValues = state.formats.map { it.bitResolution.toString() }.toTypedArray()
                    value = state.activeFormat.bitResolution.toString()
                    summary = "%s"
                    setOnPreferenceChangeListener { _, newValue ->
                        usbAudioManager.setPreferredBitResolution((newValue as String).toInt())
                        true
                    }
                }
            )
        }

        category.addPreference(
            SwitchPreferenceCompat(requireContext()).apply {
                key = getString(R.string.set_key_usb_mute)
                title = getString(R.string.set_usb_mute)
                isPersistent = false
                isChecked = state.muted == true
                setOnPreferenceChangeListener { _, newValue ->
                    usbAudioManager.setMuted(newValue as Boolean)
                    true
                }
            }
        )

        val volumeRange = state.volumeRange
        if (volumeRange != null) {
            category.addPreference(
                SeekBarPreference(requireContext()).apply {
                    key = getString(R.string.set_key_usb_volume)
                    title = getString(R.string.set_usb_volume)
                    isPersistent = false
                    min = volumeRange.first
                    max = volumeRange.second
                    showSeekBarValue = true
                    // Prefer our own tracked target over the live device readback, not the other
                    // way around -- confirmed, on real hardware, that a live readback taken right
                    // after a clock change or route switch can be transiently stale (e.g. still
                    // reporting unity gain) before the device's own state has settled, which this
                    // exact fallback order has already caused real, reproduced bugs from
                    // elsewhere in this codebase (see MediaSessionHolder.UsbVolumeProvider).
                    value =
                        (usbSettings.volumeDb ?: state.volume ?: volumeRange.second).coerceIn(
                            min,
                            max,
                        )
                    setOnPreferenceChangeListener { _, newValue ->
                        usbAudioManager.setVolume(newValue as Int)
                        true
                    }
                }
            )
        }

        val mixerRange = state.mixerGainRange
        if (mixerRange != null) {
            for ((inCh, outCh) in state.mixerChannels) {
                category.addPreference(
                    SeekBarPreference(requireContext()).apply {
                        key = mixerGainKey(inCh, outCh)
                        title = getString(R.string.set_usb_mixer_gain, inCh, outCh)
                        isPersistent = false
                        min = mixerRange.first
                        max = mixerRange.second
                        showSeekBarValue = true
                        value =
                            (state.mixerGains[inCh to outCh] ?: mixerRange.second).coerceIn(
                                min,
                                max,
                            )
                        setOnPreferenceChangeListener { _, newValue ->
                            usbAudioManager.setMixerGain(inCh, outCh, newValue as Int)
                            true
                        }
                    }
                )
            }
        }
    }

    /** The exact keys [updateControls] should have in the category right now, in build order. */
    private fun desiredControlKeys(state: UsbControlState): List<String> {
        val keys = mutableListOf<String>()
        if (state.formats.size > 1) keys += getString(R.string.set_key_usb_format)
        keys += getString(R.string.set_key_usb_mute)
        if (state.volumeRange != null) keys += getString(R.string.set_key_usb_volume)
        if (state.mixerGainRange != null) {
            for ((inCh, outCh) in state.mixerChannels) keys += mixerGainKey(inCh, outCh)
        }
        return keys
    }

    private fun mixerGainKey(inCh: Int, outCh: Int) =
        getString(R.string.set_key_usb_mixer_gain_prefix) + "${inCh}_$outCh"

    /**
     * Updates every existing control's displayed value from [state] without touching the category's
     * children -- see [updateControls]'s doc comment for why this matters. Only called once
     * [desiredControlKeys] has already confirmed the right set of controls exists.
     */
    private fun updateControlValuesInPlace(category: PreferenceCategory, state: UsbControlState) {
        category.findPreference<ListPreference>(getString(R.string.set_key_usb_format))?.value =
            state.activeFormat.bitResolution.toString()

        category
            .findPreference<SwitchPreferenceCompat>(getString(R.string.set_key_usb_mute))
            ?.isChecked = state.muted == true

        val volumeRange = state.volumeRange
        if (volumeRange != null) {
            category
                .findPreference<SeekBarPreference>(getString(R.string.set_key_usb_volume))
                ?.let {
                    it.value =
                        (usbSettings.volumeDb ?: state.volume ?: volumeRange.second).coerceIn(
                            it.min,
                            it.max,
                        )
                }
        }

        val mixerRange = state.mixerGainRange
        if (mixerRange != null) {
            for ((inCh, outCh) in state.mixerChannels) {
                category.findPreference<SeekBarPreference>(mixerGainKey(inCh, outCh))?.let {
                    it.value =
                        (state.mixerGains[inCh to outCh] ?: mixerRange.second).coerceIn(
                            it.min,
                            it.max,
                        )
                }
            }
        }
    }

    /**
     * Grays out the "Bit-perfect mode" switch with an explanation whenever
     * [UsbAudioManager.isBitPerfectCapable] says no -- toggling it would otherwise do nothing
     * observable (resolveActiveSink already refuses to use USB output in that case), which reads as
     * a silent failure rather than the device genuinely not supporting it.
     */
    private fun updateBitPerfectAvailability(state: UsbControlState?) {
        val preference =
            findPreference<SwitchPreferenceCompat>(getString(R.string.set_key_usb_bit_perfect))
                ?: return
        val capable = state?.volumeRange != null
        preference.isEnabled = capable
        preference.summary =
            if (capable) getString(R.string.set_usb_bit_perfect_desc)
            else getString(R.string.set_usb_bit_perfect_not_capable)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == getString(R.string.set_key_usb_retry)) {
            usbAudioManager.retryActivation()
            return true
        }
        if (preference.key == getString(R.string.set_key_usb_capabilities)) {
            findNavController().navigateSafe(UsbPreferenceFragmentDirections.usbCapabilities())
            return true
        }
        if (preference.key == getString(R.string.set_key_usb_pipeline)) {
            findNavController().navigateSafe(UsbPreferenceFragmentDirections.usbPipeline())
            return true
        }
        return super.onPreferenceTreeClick(preference)
    }

    private companion object {
        /**
         * Marker key for the "no active session" placeholder row, not a real `set_key_*` resource.
         */
        const val KEY_CONTROLS_NONE = "usb_controls_none_marker"
    }
}

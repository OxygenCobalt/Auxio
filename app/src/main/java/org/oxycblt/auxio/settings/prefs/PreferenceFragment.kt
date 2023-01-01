/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.settings.prefs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.settings.SettingsFragmentDirections
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * The [PreferenceFragmentCompat] that displays the list of settings.
 * @author Alexander Capehart (OxygenCobalt)
 */
class PreferenceFragment : PreferenceFragmentCompat() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager.onDisplayPreferenceDialogListener = this
        preferenceScreen.children.forEach(::setupPreference)

        // Configure the RecyclerView to support edge-to-edge.
        view.findViewById<RecyclerView>(androidx.preference.R.id.recycler_view).apply {
            clipToPadding = false
            setOnApplyWindowInsetsListener { _, insets ->
                updatePadding(bottom = insets.systemBarInsetsCompat.bottom)
                insets
            }
        }

        logD("Fragment created")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_main, rootKey)
    }

    @Suppress("Deprecation")
    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {
            is IntListPreference -> {
                // Copy the built-in preference dialog launching code into our project so
                // we can automatically use the provided preference class.
                val dialog = IntListPreferenceDialog.from(preference)
                dialog.setTargetFragment(this, 0)
                dialog.show(parentFragmentManager, IntListPreferenceDialog.TAG)
            }
            is WrappedDialogPreference -> {
                // WrappedDialogPreference cannot launch a dialog on it's own, it has to
                // be handled manually.
                val directions =
                    when (preference.key) {
                        getString(R.string.set_key_accent) ->
                            SettingsFragmentDirections.goToAccentDialog()
                        getString(R.string.set_key_lib_tabs) ->
                            SettingsFragmentDirections.goToTabDialog()
                        getString(R.string.set_key_pre_amp) ->
                            SettingsFragmentDirections.goToPreAmpDialog()
                        getString(R.string.set_key_music_dirs) ->
                            SettingsFragmentDirections.goToMusicDirsDialog()
                        getString(R.string.set_key_separators) ->
                            SettingsFragmentDirections.goToSeparatorsDialog()
                        else -> error("Unexpected dialog key ${preference.key}")
                    }
                findNavController().navigate(directions)
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        // Hook generic preferences to their specified preferences
        // TODO: These seem like good things to put into a side navigation view, if I choose to
        //  do one.
        when (preference.key) {
            getString(R.string.set_key_save_state) -> {
                playbackModel.savePlaybackState { saved ->
                    // Use the nullable context, as we could try to show a toast when this
                    // fragment is no longer attached.
                    if (saved) {
                        context?.showToast(R.string.lbl_state_saved)
                    } else {
                        context?.showToast(R.string.err_did_not_save)
                    }
                }
            }
            getString(R.string.set_key_wipe_state) -> {
                playbackModel.wipePlaybackState { wiped ->
                    if (wiped) {
                        // Use the nullable context, as we could try to show a toast when this
                        // fragment is no longer attached.
                        context?.showToast(R.string.lbl_state_wiped)
                    } else {
                        context?.showToast(R.string.err_did_not_wipe)
                    }
                }
            }
            getString(R.string.set_key_restore_state) ->
                playbackModel.tryRestorePlaybackState { restored ->
                    if (restored) {
                        // Use the nullable context, as we could try to show a toast when this
                        // fragment is no longer attached.
                        context?.showToast(R.string.lbl_state_restored)
                    } else {
                        context?.showToast(R.string.err_did_not_restore)
                    }
                }
            getString(R.string.set_key_reindex) -> musicModel.refresh()
            getString(R.string.set_key_rescan) -> musicModel.rescan()
            else -> return super.onPreferenceTreeClick(preference)
        }

        return true
    }

    private fun setupPreference(preference: Preference) {
        val settings = Settings(requireContext())

        if (!preference.isVisible) {
            // Nothing to do.
            return
        }

        if (preference is PreferenceCategory) {
            preference.children.forEach(::setupPreference)
            return
        }

        when (preference.key) {
            getString(R.string.set_key_theme) -> {
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, value ->
                        AppCompatDelegate.setDefaultNightMode(value as Int)
                        true
                    }
            }
            getString(R.string.set_key_accent) -> {
                preference.summary = getString(settings.accent.name)
            }
            getString(R.string.set_key_black_theme) -> {
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, _ ->
                        val activity = requireActivity()
                        if (activity.isNight) {
                            activity.recreate()
                        }

                        true
                    }
            }
            getString(R.string.set_key_cover_mode) -> {
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, _ ->
                        Coil.imageLoader(requireContext()).memoryCache?.clear()
                        true
                    }
            }
        }
    }
}

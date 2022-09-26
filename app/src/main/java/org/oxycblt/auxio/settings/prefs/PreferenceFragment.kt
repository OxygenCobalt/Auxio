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
import androidx.annotation.DrawableRes
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
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * The actual fragment containing the settings menu. Inherits [PreferenceFragmentCompat].
 * @author OxygenCobalt
 */
@Suppress("UNUSED")
class PreferenceFragment : PreferenceFragmentCompat() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager.onDisplayPreferenceDialogListener = this
        preferenceScreen.children.forEach(::setupPreference)

        // Make the RecycleView edge-to-edge capable
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
                // Creating our own preference dialog is hilariously difficult. For one, we need
                // to override this random method within the class in order to launch the dialog in
                // the first (because apparently you can't just implement some interface that
                // automatically provides this behavior), then we also need to use a deprecated
                // method to adequately supply a "target fragment" (otherwise we will crash since
                // the dialog requires one), and then we need to actually show the dialog, making
                // sure we use  the parent FragmentManager as again, it will crash if we don't.
                //
                // Fragments were a mistake.
                val dialog = IntListPreferenceDialog.from(preference)
                dialog.setTargetFragment(this, 0)
                dialog.show(parentFragmentManager, IntListPreferenceDialog.TAG)
            }
            is WrappedDialogPreference -> {
                val context = requireContext()
                val directions = when (preference.key) {
                    context.getString(R.string.set_key_accent) -> SettingsFragmentDirections.goToAccentDialog()
                    context.getString(R.string.set_key_lib_tabs) -> SettingsFragmentDirections.goToTabDialog()
                    context.getString(R.string.set_key_pre_amp) -> SettingsFragmentDirections.goToPreAmpDialog()
                    context.getString(R.string.set_key_music_dirs) -> SettingsFragmentDirections.goToMusicDirsDialog()
                    getString(R.string.set_key_separators) -> SettingsFragmentDirections.goToSeparatorsDialog()
                    else -> error("Unexpected dialog key ${preference.key}")
                }
                findNavController().navigate(directions)
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val context = requireContext()

        when (preference.key) {
            context.getString(R.string.set_key_save_state) -> {
                playbackModel.savePlaybackState {
                    this.context?.showToast(R.string.lbl_state_saved)
                }
            }
            context.getString(R.string.set_key_wipe_state) -> {
                playbackModel.wipePlaybackState {
                    this.context?.showToast(R.string.lbl_state_wiped)
                }
            }
            context.getString(R.string.set_key_restore_state) ->
                playbackModel.tryRestorePlaybackState { restored ->
                    if (restored) {
                        this.context?.showToast(R.string.lbl_state_restored)
                    } else {
                        this.context?.showToast(R.string.err_did_not_restore)
                    }
                }
            context.getString(R.string.set_key_reindex) -> {
                musicModel.reindex()
            }
            else -> return super.onPreferenceTreeClick(preference)
        }

        return true
    }

    private fun setupPreference(preference: Preference) {
        val context = requireActivity()
        val settings = Settings(context)

        if (!preference.isVisible) return

        if (preference is PreferenceCategory) {
            preference.children.forEach(::setupPreference)
            return
        }

        when (preference.key) {
            context.getString(R.string.set_key_theme) -> {
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, value ->
                        AppCompatDelegate.setDefaultNightMode(value as Int)
                        true
                    }
            }
            context.getString(R.string.set_key_accent) -> {
                preference.summary = context.getString(settings.accent.name)
            }
            context.getString(R.string.set_key_black_theme) -> {
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, _ ->
                        if (context.isNight) {
                            context.recreate()
                        }

                        true
                    }
            }
            context.getString(R.string.set_key_cover_mode) -> {
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, _ ->
                        Coil.imageLoader(context).memoryCache?.clear()
                        true
                    }
            }
        }
    }

    /** Convert an theme integer into an icon that can be used. */
    @DrawableRes
    private fun Int.toThemeIcon(): Int {
        return when (this) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.drawable.ic_auto_24
            AppCompatDelegate.MODE_NIGHT_NO -> R.drawable.ic_light_24
            AppCompatDelegate.MODE_NIGHT_YES -> R.drawable.ic_dark_24
            else -> R.drawable.ic_auto_24
        }
    }
}
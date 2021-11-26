/*
 * Copyright (c) 2021 Auxio Project
 * SettingsListFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.settings

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import org.oxycblt.auxio.R
import org.oxycblt.auxio.accent.AccentDialog
import org.oxycblt.auxio.excluded.ExcludedDialog
import org.oxycblt.auxio.home.tabs.TabCustomizeDialog
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.pref.IntListPrefDialog
import org.oxycblt.auxio.settings.pref.IntListPreference
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * The actual fragment containing the settings menu. Inherits [PreferenceFragmentCompat].
 * @author OxygenCobalt
 */
@Suppress("UNUSED")
class SettingsListFragment : PreferenceFragmentCompat() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    val settingsManager = SettingsManager.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceScreen.children.forEach { pref ->
            recursivelyHandleChildren(pref)
        }

        preferenceManager.onDisplayPreferenceDialogListener = this

        view.findViewById<RecyclerView>(androidx.preference.R.id.recycler_view).apply {
            clipToPadding = false

            setOnApplyWindowInsetsListener { _, insets ->
                updatePadding(bottom = insets.systemBarsCompat.bottom)

                insets
            }
        }

        logD("Fragment created.")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_main, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is IntListPreference) {
            IntListPrefDialog.from(preference).show(childFragmentManager, IntListPrefDialog.TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    /**
     * Recursively call [handlePreference] on a preference.
     */
    private fun recursivelyHandleChildren(preference: Preference) {
        if (!preference.isVisible) {
            return
        }

        if (preference is PreferenceCategory) {
            // If this preference is a category of its own, handle its own children
            preference.children.forEach { pref ->
                recursivelyHandleChildren(pref)
            }
        } else {
            handlePreference(preference)
        }
    }

    /**
     * Handle a preference, doing any specific actions on it.
     */
    private fun handlePreference(pref: Preference) {
        pref.apply {
            when (key) {
                SettingsManager.KEY_THEME -> {
                    setIcon(AppCompatDelegate.getDefaultNightMode().toThemeIcon())

                    onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
                        AppCompatDelegate.setDefaultNightMode(value as Int)
                        setIcon(AppCompatDelegate.getDefaultNightMode().toThemeIcon())
                        true
                    }
                }

                SettingsManager.KEY_BLACK_THEME -> {
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        if (requireContext().isNight) {
                            requireActivity().recreate()
                        }

                        true
                    }
                }

                SettingsManager.KEY_ACCENT -> {
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        AccentDialog().show(childFragmentManager, AccentDialog.TAG)
                        true
                    }

                    summary = context.getString(settingsManager.accent.name)
                }

                SettingsManager.KEY_LIB_TABS -> {
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        TabCustomizeDialog().show(childFragmentManager, TabCustomizeDialog.TAG)
                        true
                    }
                }

                SettingsManager.KEY_SHOW_COVERS, SettingsManager.KEY_QUALITY_COVERS -> {
                    onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                        Coil.imageLoader(requireContext()).apply {
                            this.memoryCache?.clear()
                        }

                        true
                    }
                }

                SettingsManager.KEY_SAVE_STATE -> {
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        playbackModel.savePlaybackState(requireContext()) {
                            requireContext().showToast(R.string.lbl_state_saved)
                        }

                        true
                    }
                }

                SettingsManager.KEY_EXCLUDED -> {
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        ExcludedDialog().show(childFragmentManager, ExcludedDialog.TAG)
                        true
                    }
                }
            }
        }
    }

    /**
     * Convert an theme integer into an icon that can be used.
     */
    @DrawableRes
    private fun Int.toThemeIcon(): Int {
        return when (this) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.drawable.ic_auto
            AppCompatDelegate.MODE_NIGHT_NO -> R.drawable.ic_day
            AppCompatDelegate.MODE_NIGHT_YES -> R.drawable.ic_night

            else -> R.drawable.ic_auto
        }
    }
}

/*
 * Copyright (c) 2023 Auxio Project
 * UIPreferenceFragment.kt is part of Auxio.
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

import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.BasePreferenceFragment
import org.oxycblt.auxio.settings.ui.WrappedDialogPreference
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.navigateSafe

/**
 * Display preferences.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class UIPreferenceFragment : BasePreferenceFragment(R.xml.preferences_ui) {
    @Inject lateinit var uiSettings: UISettings

    override fun onOpenDialogPreference(preference: WrappedDialogPreference) {
        if (preference.key == getString(R.string.set_key_accent)) {
            logD("Navigating to accent dialog")
            findNavController().navigateSafe(UIPreferenceFragmentDirections.accentSettings())
        }
    }

    override fun onSetupPreference(preference: Preference) {
        when (preference.key) {
            getString(R.string.set_key_theme) -> {
                logD("Configuring theme setting")
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, value ->
                        logD("Theme changed, recreating")
                        AppCompatDelegate.setDefaultNightMode(value as Int)
                        true
                    }
            }
            getString(R.string.set_key_accent) -> {
                logD("Configuring accent setting")
                preference.summary = getString(uiSettings.accent.name)
            }
            getString(R.string.set_key_black_theme) -> {
                logD("Configuring black theme setting")
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, _ ->
                        val activity = requireActivity()
                        if (activity.isNight) {
                            logD("Black theme changed in night mode, recreating")
                            activity.recreate()
                        }

                        true
                    }
            }
        }
    }
}

/*
 * Copyright (c) 2023 Auxio Project
 * AudioPreferenceFragment.kt is part of Auxio.
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

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.BasePreferenceFragment
import org.oxycblt.auxio.settings.ui.WrappedDialogPreference
import org.oxycblt.auxio.util.navigateSafe
import timber.log.Timber as L

/**
 * Audio settings interface.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AudioPreferenceFragment : BasePreferenceFragment(R.xml.preferences_audio) {
    override fun onSetupPreference(preference: Preference) {
        if (preference.key == getString(R.string.set_key_sleep_timer_minutes)) {
            val minutes =
                PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getInt(preference.key, DEFAULT_SLEEP_TIMER_MINUTES)
                    .coerceIn(MIN_SLEEP_TIMER_MINUTES, MAX_SLEEP_TIMER_MINUTES)
            preference.summary = getString(R.string.set_sleep_timer_minutes_desc, minutes)
        }
    }

    override fun onOpenDialogPreference(preference: WrappedDialogPreference) {
        when (preference.key) {
            getString(R.string.set_key_pre_amp) -> {
                L.d("Navigating to pre-amp dialog")
                findNavController().navigateSafe(AudioPreferenceFragmentDirections.preAmpSettings())
            }
            getString(R.string.set_key_sleep_timer_minutes) -> showSleepTimerDialog(preference)
        }
    }

    private fun showSleepTimerDialog(preference: WrappedDialogPreference) {
        val context = requireContext()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val currentValue =
            sharedPreferences
                .getInt(preference.key, DEFAULT_SLEEP_TIMER_MINUTES)
                .coerceIn(MIN_SLEEP_TIMER_MINUTES, MAX_SLEEP_TIMER_MINUTES)
        val picker =
            NumberPicker(context).apply {
                minValue = MIN_SLEEP_TIMER_MINUTES
                maxValue = MAX_SLEEP_TIMER_MINUTES
                value = currentValue
                wrapSelectorWheel = false
                contentDescription = getString(R.string.set_sleep_timer_minutes)
            }
        val content =
            LinearLayout(context).apply {
                gravity = Gravity.CENTER
                orientation = LinearLayout.HORIZONTAL
                addView(picker)
                addView(
                    TextView(context).apply {
                        text = getString(R.string.set_sleep_timer_minutes_unit)
                        setPadding(0, 0, resources.getDimensionPixelSize(R.dimen.spacing_medium), 0)
                    }
                )
            }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.set_sleep_timer_minutes_dialog)
            .setView(content)
            .setNegativeButton(R.string.lbl_cancel, null)
            .setPositiveButton(R.string.lbl_save) { _, _ ->
                val minutes = picker.value
                sharedPreferences.edit { putInt(preference.key, minutes) }
                preference.summary = getString(R.string.set_sleep_timer_minutes_desc, minutes)
            }
            .show()
    }

    private companion object {
        const val DEFAULT_SLEEP_TIMER_MINUTES = 15
        const val MIN_SLEEP_TIMER_MINUTES = 1
        const val MAX_SLEEP_TIMER_MINUTES = 180
    }
}

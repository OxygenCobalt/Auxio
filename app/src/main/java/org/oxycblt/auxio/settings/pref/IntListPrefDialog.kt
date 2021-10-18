/*
 * Copyright (c) 2021 Auxio Project
 * IntListPrefDialog.kt is part of Auxio.
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

package org.oxycblt.auxio.settings.pref

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.ui.LifecycleDialog

/**
 * The dialog shown whenever an [IntListPreference] is shown.
 */
class IntListPrefDialog : LifecycleDialog() {
    override fun onConfigDialog(builder: AlertDialog.Builder) {
        // Since we have to store the preference key as an argument, we have to find the
        // preference we need to use manually.
        val pref = requireNotNull(
            (parentFragment as PreferenceFragmentCompat).preferenceManager
                .findPreference<IntListPreference>(requireArguments().getString(ARG_KEY, null))
        )

        builder.setTitle(pref.title)

        builder.setSingleChoiceItems(pref.entries, pref.getValueIndex()) { _, index ->
            pref.setValueIndex(index)
            dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel, null)
    }

    companion object {
        const val TAG = BuildConfig.APPLICATION_ID + ".tag.INT_PREF"
        const val ARG_KEY = BuildConfig.APPLICATION_ID + ".arg.PREF_KEY"

        fun from(pref: IntListPreference): IntListPrefDialog {
            return IntListPrefDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, pref.key)
                }
            }
        }
    }
}

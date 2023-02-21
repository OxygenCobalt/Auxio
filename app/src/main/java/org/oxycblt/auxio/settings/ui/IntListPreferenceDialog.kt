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
 
package org.oxycblt.auxio.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.preference.PreferenceDialogFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.fixDoubleRipple

/**
 * The companion dialog to [IntListPreference]. Use [from] to create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
class IntListPreferenceDialog : PreferenceDialogFragmentCompat() {
    private val listPreference: IntListPreference
        get() = (preference as IntListPreference)
    private var pendingValueIndex = -1

    override fun onStart() {
        super.onStart()

        (requireDialog() as AlertDialog).apply {
            (getButton(AlertDialog.BUTTON_NEUTRAL) as AppCompatButton).fixDoubleRipple()
            (getButton(AlertDialog.BUTTON_POSITIVE) as AppCompatButton).fixDoubleRipple()
            (getButton(AlertDialog.BUTTON_NEGATIVE) as AppCompatButton).fixDoubleRipple()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        // PreferenceDialogFragmentCompat does not allow us to customize the actual creation
        // of the alert dialog, so we have to override onCreateDialog and create a new dialog
        // ourselves.
        MaterialAlertDialogBuilder(requireContext(), theme)
            .setTitle(listPreference.title)
            .setPositiveButton(null, null)
            .setNegativeButton(R.string.lbl_cancel, null)
            .setSingleChoiceItems(listPreference.entries, listPreference.getValueIndex()) { _, index
                ->
                pendingValueIndex = index
                dismiss()
            }
            .create()

    override fun onDialogClosed(positiveResult: Boolean) {
        if (pendingValueIndex > -1) {
            listPreference.setValueIndex(pendingValueIndex)
        }
    }

    companion object {
        /** The tag to use when instantiating this dialog. */
        const val TAG = BuildConfig.APPLICATION_ID + ".tag.INT_PREF"

        /**
         * Create a new instance.
         * @param preference The [IntListPreference] to display.
         * @return A new instance.
         */
        fun from(preference: IntListPreference) =
            IntListPreferenceDialog().apply {
                // Populate the key field required by PreferenceDialogFragmentCompat.
                arguments = Bundle().apply { putString(ARG_KEY, preference.key) }
            }
    }
}

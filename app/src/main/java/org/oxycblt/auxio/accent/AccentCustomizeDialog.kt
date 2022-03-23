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
 
package org.oxycblt.auxio.accent

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogAccentBinding
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.logD

/**
 * Dialog responsible for showing the list of accents to select.
 * @author OxygenCobalt
 */
class AccentCustomizeDialog : ViewBindingDialogFragment<DialogAccentBinding>() {
    private val settingsManager = SettingsManager.getInstance()
    private var pendingAccent = settingsManager.accent

    override fun onCreateBinding(inflater: LayoutInflater) = DialogAccentBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.set_accent)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            if (pendingAccent != settingsManager.accent) {
                logD("Applying new accent")
                settingsManager.accent = pendingAccent
                requireActivity().recreate()
            }

            dismiss()
        }

        // Negative button just dismisses, no need for a listener.
        builder.setNegativeButton(android.R.string.cancel, null)
    }

    override fun onBindingCreated(binding: DialogAccentBinding, savedInstanceState: Bundle?) {
        savedInstanceState?.getInt(KEY_PENDING_ACCENT)?.let { index ->
            pendingAccent = Accent(index)
        }

        // --- UI SETUP ---

        binding.accentRecycler.adapter =
            AccentAdapter(pendingAccent) { accent ->
                logD("Switching selected accent to $accent")
                pendingAccent = accent
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PENDING_ACCENT, pendingAccent.index)
    }

    companion object {
        const val TAG = BuildConfig.APPLICATION_ID + ".tag.ACCENT_PICKER"
        const val KEY_PENDING_ACCENT = BuildConfig.APPLICATION_ID + ".key.PENDING_ACCENT"
    }
}

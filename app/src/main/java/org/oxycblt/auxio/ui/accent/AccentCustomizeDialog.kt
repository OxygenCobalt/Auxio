/*
 * Copyright (c) 2021 Auxio Project
 * AccentCustomizeDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.ui.accent

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogAccentBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ViewBindingDialogFragment] that allows the user to configure the current [Accent].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class AccentCustomizeDialog :
    ViewBindingDialogFragment<DialogAccentBinding>(), ClickableListListener<Accent> {
    private var accentAdapter = AccentAdapter(this)
    @Inject lateinit var uiSettings: UISettings

    override fun onCreateBinding(inflater: LayoutInflater) = DialogAccentBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.set_accent)
            .setPositiveButton(R.string.lbl_ok) { _, _ ->
                if (accentAdapter.selectedAccent == uiSettings.accent) {
                    // Nothing to do.
                    return@setPositiveButton
                }

                logD("Applying new accent")
                uiSettings.accent = unlikelyToBeNull(accentAdapter.selectedAccent)
                requireActivity().recreate()
                dismiss()
            }
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogAccentBinding, savedInstanceState: Bundle?) {
        binding.accentRecycler.adapter = accentAdapter
        // Restore a previous pending accent if possible, otherwise select the current setting.
        accentAdapter.setSelectedAccent(
            if (savedInstanceState != null) {
                Accent.from(savedInstanceState.getInt(KEY_PENDING_ACCENT))
            } else {
                uiSettings.accent
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save any pending accent configuration to restore if this dialog is re-created.
        outState.putInt(KEY_PENDING_ACCENT, unlikelyToBeNull(accentAdapter.selectedAccent).index)
    }

    override fun onDestroyBinding(binding: DialogAccentBinding) {
        binding.accentRecycler.adapter = null
    }

    override fun onClick(item: Accent, viewHolder: RecyclerView.ViewHolder) {
        accentAdapter.setSelectedAccent(item)
    }

    private companion object {
        const val KEY_PENDING_ACCENT = BuildConfig.APPLICATION_ID + ".key.PENDING_ACCENT"
    }
}

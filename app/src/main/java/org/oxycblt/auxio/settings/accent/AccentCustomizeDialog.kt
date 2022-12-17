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
 
package org.oxycblt.auxio.settings.accent

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogAccentBinding
import org.oxycblt.auxio.list.BasicListListener
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.shared.ViewBindingDialogFragment
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * Dialog responsible for showing the list of accents to select.
 * @author Alexander Capehart (OxygenCobalt)
 */
class AccentCustomizeDialog : ViewBindingDialogFragment<DialogAccentBinding>(), BasicListListener {
    private var accentAdapter = AccentAdapter(this)
    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }

    override fun onCreateBinding(inflater: LayoutInflater) = DialogAccentBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.set_accent)
            .setPositiveButton(R.string.lbl_ok) { _, _ ->
                if (accentAdapter.selectedAccent != settings.accent) {
                    logD("Applying new accent")
                    settings.accent = unlikelyToBeNull(accentAdapter.selectedAccent)
                    requireActivity().recreate()
                }

                dismiss()
            }
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogAccentBinding, savedInstanceState: Bundle?) {
        binding.accentRecycler.adapter = accentAdapter
        accentAdapter.setSelectedAccent(
            if (savedInstanceState != null) {
                Accent.from(savedInstanceState.getInt(KEY_PENDING_ACCENT))
            } else {
                settings.accent
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PENDING_ACCENT, unlikelyToBeNull(accentAdapter.selectedAccent).index)
    }

    override fun onDestroyBinding(binding: DialogAccentBinding) {
        binding.accentRecycler.adapter = null
    }

    override fun onClick(item: Item) {
        check(item is Accent) { "Unexpected datatype: ${item::class.java}" }
        accentAdapter.setSelectedAccent(item)
    }

    companion object {
        const val KEY_PENDING_ACCENT = BuildConfig.APPLICATION_ID + ".key.PENDING_ACCENT"
    }
}
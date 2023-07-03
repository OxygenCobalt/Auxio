/*
 * Copyright (c) 2023 Auxio Project
 * MenuDialogFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.children
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMenuBinding
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.ui.ViewBindingBottomSheetDialogFragment

/**
 * A [ViewBindingBottomSheetDialogFragment] that displays basic music information and
 * a series of options.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MenuDialogFragment : ViewBindingBottomSheetDialogFragment<DialogMenuBinding>() {
    private val menuAdapter = MenuOptionAdapter()

    override fun onCreateBinding(inflater: LayoutInflater) = DialogMenuBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogMenuBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.menuRecycler.apply {
            adapter = menuAdapter
            itemAnimator = null
        }

        // Avoid having to use a dummy view and rely on what AndroidX Toolbar uses.
        @SuppressLint("RestrictedApi") val builder = MenuBuilder(requireContext())
        MenuInflater(requireContext()).inflate(R.menu.item_song, builder)
        menuAdapter.update(builder.children.toList(), UpdateInstructions.Diff)
    }
}

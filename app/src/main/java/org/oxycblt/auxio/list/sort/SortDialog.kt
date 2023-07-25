/*
 * Copyright (c) 2023 Auxio Project
 * SortDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.sort

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogSortBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.ui.ViewBindingBottomSheetDialogFragment
import org.oxycblt.auxio.util.systemBarInsetsCompat

abstract class SortDialog :
    ViewBindingBottomSheetDialogFragment<DialogSortBinding>(), ClickableListListener<Sort.Mode> {
    private val modeAdapter = SortModeAdapter(this)

    abstract fun getInitialSort(): Sort

    abstract fun applyChosenSort(sort: Sort)

    abstract fun getModeChoices(): List<Sort.Mode>

    override fun onCreateBinding(inflater: LayoutInflater) = DialogSortBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogSortBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(bottom = insets.systemBarInsetsCompat.bottom)
            insets
        }

        binding.sortModeRecycler.adapter = modeAdapter

        binding.sortCancel.setOnClickListener { dismiss() }

        binding.sortSave.setOnClickListener {
            val initial = getInitialSort()
            // FIXME: This won't work for the playlist sort dialog.
            val mode = modeAdapter.currentMode ?: initial.mode
            val direction =
                when (binding.sortDirectionGroup.checkedButtonId) {
                    R.id.sort_direction_asc -> Sort.Direction.ASCENDING
                    R.id.sort_direction_dsc -> Sort.Direction.DESCENDING
                    else -> initial.direction
                }
            applyChosenSort(Sort(mode, direction))
            dismiss()
        }

        // --- STATE SETUP ---
        val initial = getInitialSort()

        modeAdapter.update(getModeChoices(), UpdateInstructions.Diff)
        modeAdapter.setSelected(initial.mode)

        val directionId =
            when (initial.direction) {
                Sort.Direction.ASCENDING -> R.id.sort_direction_asc
                Sort.Direction.DESCENDING -> R.id.sort_direction_dsc
            }
        binding.sortDirectionGroup.check(directionId)
    }

    override fun onClick(item: Sort.Mode, viewHolder: RecyclerView.ViewHolder) {
        modeAdapter.setSelected(item)
    }
}

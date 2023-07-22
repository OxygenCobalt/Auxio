/*
 * Copyright (c) 2023 Auxio Project
 * SortAdapter.kt is part of Auxio.
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

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemSortModeBinding
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.util.inflater

class SortAdapter(var selectedMode: Sort.Mode) :
    FlexibleListAdapter<Sort.Mode, SortModeViewHolder>(SortModeViewHolder.DIFF_CALLBACK) {
    var currentlySelected = selectedMode
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SortModeViewHolder.from(parent)

    override fun onBindViewHolder(holder: SortModeViewHolder, position: Int) {
        throw NotImplementedError()
    }

    override fun onBindViewHolder(holder: SortModeViewHolder, position: Int, payload: List<Any>) {
        val mode = getItem(position)
        if (payload.isEmpty()) {
            holder.bind(mode)
        }
        holder.setSelected(mode == currentlySelected)
    }

    fun setSelected(mode: Sort.Mode) {
        if (mode == currentlySelected) return
        val oldMode = currentList.indexOf(currentlySelected)
        val newMode = currentList.indexOf(mode)
        currentlySelected = selectedMode
        notifyItemChanged(oldMode, PAYLOAD_SELECTION_CHANGED)
        notifyItemChanged(newMode, PAYLOAD_SELECTION_CHANGED)
    }

    private companion object {
        val PAYLOAD_SELECTION_CHANGED = Any()
    }
}

class SortModeViewHolder private constructor(private val binding: ItemSortModeBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(mode: Sort.Mode) {
        // TODO: Add names to sort.mode
        binding.sortRadio.text = mode.toString()
    }

    fun setSelected(selected: Boolean) {
        binding.sortRadio.isChecked = selected
    }

    companion object {
        fun from(parent: View) =
            SortModeViewHolder(ItemSortModeBinding.inflate(parent.context.inflater))

        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Sort.Mode>() {
                override fun areItemsTheSame(oldItem: Sort.Mode, newItem: Sort.Mode) =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: Sort.Mode, newItem: Sort.Mode) =
                    oldItem == newItem
            }
    }
}

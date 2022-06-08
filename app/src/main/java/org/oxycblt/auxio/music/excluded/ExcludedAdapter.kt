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
 
package org.oxycblt.auxio.music.excluded

import android.content.Context
import org.oxycblt.auxio.databinding.ItemExcludedDirBinding
import org.oxycblt.auxio.ui.BackingData
import org.oxycblt.auxio.ui.BindingViewHolder
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

/**
 * Adapter that shows the excluded directories and their "Clear" button.
 * @author OxygenCobalt
 */
class ExcludedAdapter(listener: Listener) :
    MonoAdapter<ExcludedDirectory, ExcludedAdapter.Listener, ExcludedViewHolder>(listener) {
    override val data = ExcludedBackingData(this)
    override val creator = ExcludedViewHolder.CREATOR

    interface Listener {
        fun onRemoveDirectory(dir: ExcludedDirectory)
    }

    class ExcludedBackingData(private val adapter: ExcludedAdapter) :
        BackingData<ExcludedDirectory>() {
        private val _currentList = mutableListOf<ExcludedDirectory>()
        val currentList: List<ExcludedDirectory> = _currentList

        override fun getItemCount(): Int = _currentList.size
        override fun getItem(position: Int): ExcludedDirectory = _currentList[position]

        fun add(dir: ExcludedDirectory) {
            if (_currentList.contains(dir)) {
                return
            }

            _currentList.add(dir)
            adapter.notifyItemInserted(_currentList.lastIndex)
        }

        fun addAll(dirs: List<ExcludedDirectory>) {
            val oldLastIndex = dirs.lastIndex
            _currentList.addAll(dirs)
            adapter.notifyItemRangeInserted(oldLastIndex, dirs.size)
        }

        fun remove(dir: ExcludedDirectory) {
            val idx = _currentList.indexOf(dir)
            _currentList.removeAt(idx)
            adapter.notifyItemRemoved(idx)
        }
    }
}

/** The viewholder for [ExcludedAdapter]. Not intended for use in other adapters. */
class ExcludedViewHolder private constructor(private val binding: ItemExcludedDirBinding) :
    BindingViewHolder<ExcludedDirectory, ExcludedAdapter.Listener>(binding.root) {
    override fun bind(item: ExcludedDirectory, listener: ExcludedAdapter.Listener) {
        binding.excludedPath.textSafe = item.toString()
        binding.excludedClear.setOnClickListener { listener.onRemoveDirectory(item) }
    }

    companion object {
        val CREATOR =
            object : Creator<ExcludedViewHolder> {
                override val viewType: Int
                    get() = throw UnsupportedOperationException()

                override fun create(context: Context) =
                    ExcludedViewHolder(ItemExcludedDirBinding.inflate(context.inflater))
            }
    }
}

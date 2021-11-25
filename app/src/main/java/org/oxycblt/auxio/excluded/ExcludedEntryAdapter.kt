/*
 * Copyright (c) 2021 Auxio Project
 * BlacklistEntryAdapter.kt is part of Auxio.
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

package org.oxycblt.auxio.excluded

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemExcludedDirBinding
import org.oxycblt.auxio.util.inflater

/**
 * Adapter that shows the excluded directories and their "Clear" button.
 * @author OxygenCobalt
 */
class ExcludedEntryAdapter(
    private val onClear: (String) -> Unit
) : RecyclerView.Adapter<ExcludedEntryAdapter.ViewHolder>() {
    private var paths = mutableListOf<String>()

    override fun getItemCount() = paths.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExcludedDirBinding.inflate(parent.context.inflater))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(paths[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newPaths: MutableList<String>) {
        paths = newPaths
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemExcludedDirBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(path: String) {
            binding.excludedPath.text = path
            binding.excludedPath.requestLayout()
            binding.excludedClear.setOnClickListener {
                onClear(path)
            }
        }
    }
}

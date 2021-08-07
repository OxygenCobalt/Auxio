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

package org.oxycblt.auxio.settings.blacklist

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemBlacklistEntryBinding
import org.oxycblt.auxio.ui.inflater

/**
 * Adapter that shows the blacklist entries and their "Clear" button.
 * @author OxygenCobalt
 */
class BlacklistEntryAdapter(
    private val onClear: (String) -> Unit
) : RecyclerView.Adapter<BlacklistEntryAdapter.ViewHolder>() {
    private var paths = mutableListOf<String>()

    override fun getItemCount() = paths.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBlacklistEntryBinding.inflate(parent.context.inflater))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(paths[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newPaths: MutableList<String>) {
        paths = newPaths
        notifyDataSetChanged() // TODO: Consider using remove/addition
    }

    inner class ViewHolder(
        private val binding: ItemBlacklistEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(path: String) {
            binding.blacklistTitle.text = path
            binding.blacklistTitle.requestLayout()
            binding.blacklistClear.setOnClickListener {
                onClear(path)
            }
        }
    }
}

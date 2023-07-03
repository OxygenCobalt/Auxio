/*
 * Copyright (c) 2023 Auxio Project
 * MenuOptionAdapter.kt is part of Auxio.
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

import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import org.oxycblt.auxio.databinding.ItemMenuOptionBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.util.inflater

/**
 * Displays a list of [MenuItem]s as custom list items.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MenuOptionAdapter :
    FlexibleListAdapter<MenuItem, MenuOptionViewHolder>(MenuOptionViewHolder.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MenuOptionViewHolder.from(parent)

    override fun onBindViewHolder(holder: MenuOptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * A [DialogRecyclerView.ViewHolder] that displays a list of menu options based on [MenuItem].
 * @author Alexander Capehart (OxygenCobalt)
 */
class MenuOptionViewHolder private constructor(private val binding: ItemMenuOptionBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    fun bind(item: MenuItem) {
        binding.title.text = item.title
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup) =
            MenuOptionViewHolder(ItemMenuOptionBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<MenuItem>() {
                override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem) =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem) =
                    oldItem.title == newItem.title
            }
    }
}

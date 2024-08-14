/*
 * Copyright (c) 2023 Auxio Project
 * MenuItemAdapter.kt is part of Auxio.
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
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.util.inflater

/**
 * Displays a list of [MenuItem]s as custom list items.
 *
 * @param listener A [ClickableListListener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MenuItemAdapter(private val listener: ClickableListListener<MenuItem>) :
    FlexibleListAdapter<MenuItem, MenuItemViewHolder>(MenuItemViewHolder.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MenuItemViewHolder.from(parent)

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

/**
 * A [DialogRecyclerView.ViewHolder] that displays a [MenuItem].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MenuItemViewHolder private constructor(private val binding: ItemMenuOptionBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param item The new [MenuItem] to bind.
     * @param listener A [ClickableListListener] to bind interactions to.
     */
    fun bind(item: MenuItem, listener: ClickableListListener<MenuItem>) {
        listener.bind(item, this)
        binding.title.apply {
            text = item.title
            setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon, null, null, null)
            isEnabled = item.isEnabled
        }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup) =
            MenuItemViewHolder(ItemMenuOptionBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<MenuItem>() {
                override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem) =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem) =
                    oldItem.title.toString() == newItem.title.toString()
            }
    }
}

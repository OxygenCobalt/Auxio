/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.detail.recycler

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.databinding.ItemSortHeaderBinding
import org.oxycblt.auxio.detail.SortHeader
import org.oxycblt.auxio.ui.recycler.Header
import org.oxycblt.auxio.ui.recycler.HeaderViewHolder
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SimpleItemCallback
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logW

abstract class DetailAdapter<L : DetailAdapter.Listener>(
    private val listener: L,
    diffCallback: DiffUtil.ItemCallback<Item>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @Suppress("LeakingThis") override fun getItemCount() = differ.currentList.size

    override fun getItemViewType(position: Int) =
        when (differ.currentList[position]) {
            is Header -> HeaderViewHolder.VIEW_TYPE
            is SortHeader -> SortHeaderViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            HeaderViewHolder.VIEW_TYPE -> HeaderViewHolder.new(parent)
            SortHeaderViewHolder.VIEW_TYPE -> SortHeaderViewHolder.new(parent)
            else -> error("Invalid item type $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        throw IllegalStateException()

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        val item = differ.currentList[position]

        if (payload.isEmpty()) {
            when (item) {
                is Header -> (holder as HeaderViewHolder).bind(item)
                is SortHeader -> (holder as SortHeaderViewHolder).bind(item, listener)
            }
        }

        holder.itemView.isActivated = shouldHighlightViewHolder(item)
    }

    protected val differ = AsyncListDiffer(this, diffCallback)

    protected abstract fun shouldHighlightViewHolder(item: Item): Boolean

    protected inline fun <reified T : Item> highlightImpl(oldItem: T?, newItem: T?) {
        if (oldItem != null) {
            val pos = differ.currentList.indexOfFirst { item -> item.id == oldItem.id && item is T }

            if (pos > -1) {
                notifyItemChanged(pos, PAYLOAD_HIGHLIGHT_CHANGED)
            } else {
                logW("oldItem was not in adapter data")
            }
        }

        if (newItem != null) {
            val pos = differ.currentList.indexOfFirst { item -> item is T && item.id == newItem.id }

            if (pos > -1) {
                notifyItemChanged(pos, PAYLOAD_HIGHLIGHT_CHANGED)
            } else {
                logW("newItem was not in adapter data")
            }
        }
    }

    fun submitList(list: List<Item>) {
        differ.submitList(list)
    }

    companion object {
        // This payload value serves two purposes:
        // 1. It disables animations from notifyItemChanged, which looks bad when highlighting
        // ViewHolders.
        // 2. It instructs adapters to avoid re-binding information, and instead simply
        // change the highlight state.
        val PAYLOAD_HIGHLIGHT_CHANGED = Any()

        val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Header && newItem is Header ->
                            HeaderViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is SortHeader && newItem is SortHeader ->
                            SortHeaderViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        else -> false
                    }
                }
            }
    }

    interface Listener : MenuItemListener {
        fun onPlayParent()
        fun onShuffleParent()
        fun onShowSortMenu(anchor: View)
    }
}

class SortHeaderViewHolder(private val binding: ItemSortHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: SortHeader, listener: DetailAdapter.Listener) {
        binding.headerTitle.text = binding.context.getString(item.string)
        binding.headerButton.apply {
            TooltipCompat.setTooltipText(this, contentDescription)
            setOnClickListener(listener::onShowSortMenu)
        }
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_SORT_HEADER

        fun new(parent: View) =
            SortHeaderViewHolder(ItemSortHeaderBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<SortHeader>() {
                override fun areItemsTheSame(oldItem: SortHeader, newItem: SortHeader) =
                    oldItem.string == newItem.string
            }
    }
}

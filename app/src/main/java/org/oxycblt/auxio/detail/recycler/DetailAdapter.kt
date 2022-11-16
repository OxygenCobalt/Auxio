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
import org.oxycblt.auxio.ui.recycler.AuxioRecyclerView
import org.oxycblt.auxio.ui.recycler.Header
import org.oxycblt.auxio.ui.recycler.HeaderViewHolder
import org.oxycblt.auxio.ui.recycler.IndicatorAdapter
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SimpleItemCallback
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

abstract class DetailAdapter<L : DetailAdapter.Listener>(
    private val listener: L,
    diffCallback: DiffUtil.ItemCallback<Item>
) : IndicatorAdapter<RecyclerView.ViewHolder>(), AuxioRecyclerView.SpanSizeLookup {
    private var isPlaying = false

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
        payloads: List<Any>
    ) {
        val item = differ.currentList[position]

        if (payloads.isEmpty()) {
            when (item) {
                is Header -> (holder as HeaderViewHolder).bind(item)
                is SortHeader -> (holder as SortHeaderViewHolder).bind(item, listener)
            }
        }

        super.onBindViewHolder(holder, position, payloads)
    }

    override fun isItemFullWidth(position: Int): Boolean {
        val item = differ.currentList[position]
        return item is Header || item is SortHeader
    }

    @Suppress("LeakingThis") protected val differ = AsyncListDiffer(this, diffCallback)

    override val currentList: List<Item>
        get() = differ.currentList

    fun submitList(list: List<Item>) {
        differ.submitList(list)
    }

    companion object {
        val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Header && newItem is Header ->
                            HeaderViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
                        oldItem is SortHeader && newItem is SortHeader ->
                            SortHeaderViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
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
                override fun areContentsTheSame(oldItem: SortHeader, newItem: SortHeader) =
                    oldItem.string == newItem.string
            }
    }
}

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

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.databinding.ItemSortHeaderBinding
import org.oxycblt.auxio.ui.AsyncBackingData
import org.oxycblt.auxio.ui.BindingViewHolder
import org.oxycblt.auxio.ui.Header
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.MultiAdapter
import org.oxycblt.auxio.ui.NewHeaderViewHolder
import org.oxycblt.auxio.ui.SimpleItemCallback
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

abstract class DetailAdapter<L : DetailAdapter.Listener>(
    listener: L,
    diffCallback: DiffUtil.ItemCallback<Item>
) : MultiAdapter<L>(listener) {
    abstract fun shouldHighlightViewHolder(item: Item): Boolean

    protected inline fun <reified T : Item> highlightItem(newItem: T?): Int? {
        if (newItem == null) {
            return null
        }

        // Use existing data instead of having to re-sort it.
        // We also have to account for the album count when searching for the ViewHolder.
        val pos = data.currentList.indexOfFirst { item -> item.id == newItem.id && item is T }
        notifyItemChanged(pos, PAYLOAD_HIGHLIGHT_CHANGED)
        return pos
    }

    @Suppress("LeakingThis") override val data = AsyncBackingData(this, diffCallback)

    override fun getCreatorFromItem(item: Item) =
        when (item) {
            is Header -> NewHeaderViewHolder.CREATOR
            is SortHeader -> SortHeaderViewHolder.CREATOR
            else -> null
        }

    override fun getCreatorFromViewType(viewType: Int) =
        when (viewType) {
            NewHeaderViewHolder.CREATOR.viewType -> NewHeaderViewHolder.CREATOR
            SortHeaderViewHolder.CREATOR.viewType -> SortHeaderViewHolder.CREATOR
            else -> null
        }

    override fun onBind(
        viewHolder: RecyclerView.ViewHolder,
        item: Item,
        listener: L,
        payload: List<Any>
    ) {
        if (payload.isEmpty()) {
            when (item) {
                is Header -> (viewHolder as NewHeaderViewHolder).bind(item, Unit)
                is SortHeader -> (viewHolder as SortHeaderViewHolder).bind(item, listener)
            }
        }

        viewHolder.itemView.isActivated = shouldHighlightViewHolder(item)
    }

    companion object {
        // This payload value serves two purposes:
        // 1. It disables animations from notifyItemChanged, which looks bad when highlighting
        // values.
        // 2. It instructs adapters to avoid re-binding information, and instead simply
        // change the highlight state.
        val PAYLOAD_HIGHLIGHT_CHANGED = Any()

        val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Header && newItem is Header ->
                            NewHeaderViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
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

data class SortHeader(override val id: Long, @StringRes val string: Int) : Item()

class SortHeaderViewHolder(private val binding: ItemSortHeaderBinding) :
    BindingViewHolder<SortHeader, DetailAdapter.Listener>(binding.root) {
    override fun bind(item: SortHeader, listener: DetailAdapter.Listener) {
        binding.headerTitle.textSafe = binding.context.getString(item.string)
        binding.headerButton.apply {
            TooltipCompat.setTooltipText(this, contentDescription)
            setOnClickListener(listener::onShowSortMenu)
        }
    }

    companion object {
        val CREATOR =
            object : Creator<SortHeaderViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_SORT_HEADER

                override fun create(context: Context) =
                    SortHeaderViewHolder(ItemSortHeaderBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<SortHeader>() {
                override fun areItemsTheSame(oldItem: SortHeader, newItem: SortHeader) =
                    oldItem.string == newItem.string
            }
    }
}

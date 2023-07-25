/*
 * Copyright (c) 2022 Auxio Project
 * DetailListAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.list

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.databinding.ItemSortHeaderBinding
import org.oxycblt.auxio.list.BasicHeader
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.BasicHeaderViewHolder
import org.oxycblt.auxio.list.recycler.DividerViewHolder
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * A [RecyclerView.Adapter] that implements shared behavior between lists of child items in the
 * detail views.
 *
 * @param listener A [Listener] to bind interactions to.
 * @param diffCallback A [DiffUtil.ItemCallback] to compare list updates with.
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class DetailListAdapter(
    private val listener: Listener<*>,
    private val diffCallback: DiffUtil.ItemCallback<Item>
) : SelectionIndicatorAdapter<Item, RecyclerView.ViewHolder>(diffCallback) {

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            // Implement support for headers and sort headers
            is Divider -> DividerViewHolder.VIEW_TYPE
            is BasicHeader -> BasicHeaderViewHolder.VIEW_TYPE
            is SortHeader -> SortHeaderViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            DividerViewHolder.VIEW_TYPE -> DividerViewHolder.from(parent)
            BasicHeaderViewHolder.VIEW_TYPE -> BasicHeaderViewHolder.from(parent)
            SortHeaderViewHolder.VIEW_TYPE -> SortHeaderViewHolder.from(parent)
            else -> error("Invalid item type $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is BasicHeader -> (holder as BasicHeaderViewHolder).bind(item)
            is SortHeader -> (holder as SortHeaderViewHolder).bind(item, listener)
        }
    }

    /** An extended [SelectableListListener] for [DetailListAdapter] implementations. */
    interface Listener<in T : Music> : SelectableListListener<T> {
        /**
         * Called when the button in a [SortHeader] item is pressed, requesting that the sort menu
         * should be opened.
         */
        fun onOpenSortMenu()
    }

    protected companion object {
        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Divider && newItem is Divider ->
                            DividerViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is BasicHeader && newItem is BasicHeader ->
                            BasicHeaderViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is SortHeader && newItem is SortHeader ->
                            SortHeaderViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        else -> false
                    }
                }
            }
    }
}

/**
 * A header variation that displays a button to open a sort menu.
 *
 * @param titleRes The string resource to use as the header title
 * @author Alexander Capehart (OxygenCobalt)
 */
data class SortHeader(@StringRes override val titleRes: Int) : Header

/**
 * A [RecyclerView.ViewHolder] that displays a [SortHeader] and it's actions. Use [from] to create
 * an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private class SortHeaderViewHolder(private val binding: ItemSortHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param sortHeader The new [SortHeader] to bind.
     * @param listener An [DetailListAdapter.Listener] to bind interactions to.
     */
    fun bind(sortHeader: SortHeader, listener: DetailListAdapter.Listener<*>) {
        binding.headerTitle.text = binding.context.getString(sortHeader.titleRes)
        binding.headerSort.apply {
            // Add a Tooltip based on the content description so that the purpose of this
            // button can be clear.
            TooltipCompat.setTooltipText(this, contentDescription)
            setOnClickListener { listener.onOpenSortMenu() }
        }
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_SORT_HEADER

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            SortHeaderViewHolder(ItemSortHeaderBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<SortHeader>() {
                override fun areContentsTheSame(oldItem: SortHeader, newItem: SortHeader) =
                    oldItem.titleRes == newItem.titleRes
            }
    }
}

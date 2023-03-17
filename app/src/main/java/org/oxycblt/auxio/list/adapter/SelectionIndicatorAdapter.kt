/*
 * Copyright (c) 2022 Auxio Project
 * SelectionIndicatorAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Music

/**
 * A [PlayingIndicatorAdapter] that also supports indicating the selection status of a group of
 * items.
 *
 * @param diffCallback A [DiffUtil.ItemCallback] to compare list updates with.
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class SelectionIndicatorAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : PlayingIndicatorAdapter<T, VH>(diffCallback) {
    private var selectedItems = setOf<T>()

    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (holder is ViewHolder) {
            holder.updateSelectionIndicator(selectedItems.contains(currentList[position]))
        }
    }

    /**
     * Update the list of selected items.
     *
     * @param items A set of selected [T] items.
     */
    fun setSelected(items: Set<T>) {
        val oldSelectedItems = selectedItems
        val newSelectedItems = items.toSet()
        if (newSelectedItems == oldSelectedItems) {
            // Nothing to do.
            return
        }

        selectedItems = newSelectedItems
        for (i in currentList.indices) {
            // TODO: Perhaps add an optimization that allows me to avoid the O(n) iteration
            //  assuming all list items are unique?
            val item = currentList[i]
            if (item !is Music) {
                // Not applicable.
                continue
            }

            // Only update items that were added or removed from the list.
            if (oldSelectedItems.contains(item) xor newSelectedItems.contains(item)) {
                notifyItemChanged(i, PAYLOAD_SELECTION_INDICATOR_CHANGED)
            }
        }
    }

    /** A [PlayingIndicatorAdapter.ViewHolder] that can display a selection indicator. */
    abstract class ViewHolder(root: View) : PlayingIndicatorAdapter.ViewHolder(root) {
        /**
         * Update the selection indicator within this [PlayingIndicatorAdapter.ViewHolder].
         *
         * @param isSelected Whether this [PlayingIndicatorAdapter.ViewHolder] is selected.
         */
        abstract fun updateSelectionIndicator(isSelected: Boolean)
    }

    private companion object {
        val PAYLOAD_SELECTION_INDICATOR_CHANGED = Any()
    }
}

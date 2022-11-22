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
 
package org.oxycblt.auxio.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Music

/**
 * An adapter that implements selection indicators.
 * @author OxygenCobalt
 */
abstract class SelectionIndicatorAdapter<VH : RecyclerView.ViewHolder> :
    PlayingIndicatorAdapter<VH>() {
    private var selectedItems = setOf<Music>()

    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, payloads)

        if (holder is ViewHolder) {
            holder.updateSelectionIndicator(selectedItems.contains(currentList[position]))
        }
    }

    fun updateSelection(items: List<Music>) {
        val oldSelectedItems = selectedItems
        val newSelectedItems = items.toSet()
        if (newSelectedItems == oldSelectedItems) {
            return
        }

        selectedItems = newSelectedItems
        for (i in currentList.indices) {
            // TODO: Perhaps add an optimization that allows me to avoid the O(n) iteration
            //  assuming all list items are unique?
            val item = currentList[i]
            if (item !is Music) {
                continue
            }

            val added = !oldSelectedItems.contains(item) && newSelectedItems.contains(item)
            val removed = oldSelectedItems.contains(item) && !newSelectedItems.contains(item)
            if (added || removed) {
                notifyItemChanged(i, PAYLOAD_INDICATOR_CHANGED)
            }
        }
    }

    /** A ViewHolder that can respond to selection indicator updates. */
    abstract class ViewHolder(root: View) : PlayingIndicatorAdapter.ViewHolder(root) {
        abstract fun updateSelectionIndicator(isSelected: Boolean)
    }
}

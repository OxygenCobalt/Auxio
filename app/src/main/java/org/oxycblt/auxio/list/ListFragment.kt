/*
 * Copyright (c) 2022 Auxio Project
 * ListFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.music.Music

/**
 * A Fragment containing a selectable list.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class ListFragment<in T : Music, VB : ViewBinding> :
    SelectionFragment<VB>(), SelectableListListener<T> {
    /**
     * Called when [onClick] is called, but does not result in the item being selected. This more or
     * less corresponds to an [onClick] implementation in a non-[ListFragment].
     *
     * @param item The [T] data of the item that was clicked.
     */
    abstract fun onRealClick(item: T)

    final override fun onClick(item: T, viewHolder: RecyclerView.ViewHolder) {
        if (listModel.selected.value.isNotEmpty()) {
            // Map clicking an item to selecting an item when items are already selected.
            listModel.select(item)
        } else {
            // Delegate to the concrete implementation when we don't select the item.
            onRealClick(item)
        }
    }

    final override fun onSelect(item: T) {
        listModel.select(item)
    }
}

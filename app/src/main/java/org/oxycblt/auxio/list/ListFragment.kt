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

import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.util.logD

/**
 * A Fragment containing a selectable list.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class ListFragment<in T : Music, VB : ViewBinding> :
    SelectionFragment<VB>(), SelectableListListener<T> {
    private var currentMenu: PopupMenu? = null

    override fun onDestroyBinding(binding: VB) {
        super.onDestroyBinding(binding)
        currentMenu?.dismiss()
        currentMenu = null
    }

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

    /**
     * Open a menu. This menu will be managed by the Fragment and closed when the view is destroyed.
     * If a menu is already opened, this call is ignored.
     *
     * @param anchor The [View] to anchor the menu to.
     * @param menuRes The resource of the menu to load.
     * @param block A block that is ran within [PopupMenu] that allows further configuration.
     */
    protected fun openMenu(anchor: View, @MenuRes menuRes: Int, block: PopupMenu.() -> Unit) {
        if (currentMenu != null) {
            logD("Menu already present, not launching")
            return
        }

        logD("Opening popup menu menu")

        currentMenu =
            PopupMenu(requireContext(), anchor).apply {
                inflate(menuRes)
                MenuCompat.setGroupDividerEnabled(menu, true)
                block()
                setOnDismissListener { currentMenu = null }
                show()
            }
    }
}

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
 
package org.oxycblt.auxio.list

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * A basic listener for list interactions.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface ClickableListListener {
    /**
     * Called when an [Item] in the list is clicked.
     * @param item The [Item] that was clicked.
     * @param viewHolder The [RecyclerView.ViewHolder] of the item that was clicked.
     */
    fun onClick(item: Item, viewHolder: RecyclerView.ViewHolder)

    /**
     * Binds this instance to a list item.
     * @param item The [Item] that this list entry is bound to.
     * @param viewHolder The [RecyclerView.ViewHolder] of the item that was clicked.
     * @param bodyView The [View] containing the main body of the list item. Any click events on
     * this [View] are routed to the listener. Defaults to the root view.
     */
    fun bind(
        item: Item,
        viewHolder: RecyclerView.ViewHolder,
        bodyView: View = viewHolder.itemView
    ) {
        bodyView.setOnClickListener { onClick(item, viewHolder) }
    }
}

/**
 * An extension of [ClickableListListener] that enables list editing functionality.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface EditableListListener : ClickableListListener {
    /**
     * Called when a [RecyclerView.ViewHolder] requests that it should be dragged.
     * @param viewHolder The [RecyclerView.ViewHolder] that should start being dragged.
     */
    fun onPickUp(viewHolder: RecyclerView.ViewHolder)

    /**
     * Binds this instance to a list item.
     * @param item The [Item] that this list entry is bound to.
     * @param viewHolder The [RecyclerView.ViewHolder] to bind.
     * @param bodyView The [View] containing the main body of the list item. Any click events on
     * this [View] are routed to the listener. Defaults to the root view.
     * @param dragHandle A touchable [View]. Any drag on this view will start a drag event.
     */
    fun bind(
        item: Item,
        viewHolder: RecyclerView.ViewHolder,
        bodyView: View = viewHolder.itemView,
        dragHandle: View
    ) {
        bind(item, viewHolder, bodyView)
        dragHandle.setOnTouchListener { _, motionEvent ->
            dragHandle.performClick()
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                onPickUp(viewHolder)
                true
            } else false
        }
    }
}

/**
 * An extension of [ClickableListListener] that enables menu and selection functionality.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface SelectableListListener : ClickableListListener {
    /**
     * Called when an [Item] in the list requests that a menu related to it should be opened.
     * @param item The [Item] to show a menu for.
     * @param anchor The [View] to anchor the menu to.
     */
    fun onOpenMenu(item: Item, anchor: View)

    /**
     * Called when an [Item] in the list requests that it be selected.
     * @param item The [Item] to select.
     */
    fun onSelect(item: Item)

    /**
     * Binds this instance to a list item.
     * @param item The [Item] that this list entry is bound to.
     * @param viewHolder The [RecyclerView.ViewHolder] to bind.
     * @param bodyView The [View] containing the main body of the list item. Any click events on
     * this [View] are routed to the listener. Defaults to the root view.
     * @param menuButton A clickable [View]. Any click events on this [View] will open a menu.
     */
    fun bind(
        item: Item,
        viewHolder: RecyclerView.ViewHolder,
        bodyView: View = viewHolder.itemView,
        menuButton: View
    ) {
        bind(item, viewHolder, bodyView)
        // Map long clicks to the selection listener.
        bodyView.setOnLongClickListener {
            onSelect(item)
            true
        }
        // Map the menu button to the menu opening listener.
        menuButton.setOnClickListener { onOpenMenu(item, it) }
    }
}

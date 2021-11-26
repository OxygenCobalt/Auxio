/*
 * Copyright (c) 2021 Auxio Project
 * QueueDragCallback.kt is part of Auxio.
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

package org.oxycblt.auxio.home.tabs

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [ItemTouchHelper.Callback] that handles dragging items in the tab customization menu.
 * Unlike QueueAdapter's ItemTouchHelper, this one is bare and simple.
 */
class TabDragCallback(private val getTabs: () -> Array<Tab>) : ItemTouchHelper.Callback() {
    private val tabs: Array<Tab> get() = getTabs()
    private lateinit var tabAdapter: TabAdapter

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeFlag(
        ItemTouchHelper.ACTION_STATE_DRAG,
        ItemTouchHelper.UP or ItemTouchHelper.DOWN
    )

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // No fancy UI magic here. This is a dialog, we don't need to give it as much attention.
        // Just make sure the built-in androidx code doesn't get in our way.
        viewHolder.itemView.translationX = dX
        viewHolder.itemView.translationY = dY
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.translationX = 0f
        viewHolder.itemView.translationY = 0f
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        tabs.swap(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        tabAdapter.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    /**
     * Add the tab adapter to this callback.
     * Done because there's a circular dependency between the two objects
     */
    fun addTabAdapter(adapter: TabAdapter) {
        tabAdapter = adapter
    }

    private fun <T : Any> Array<T>.swap(from: Int, to: Int) {
        val t = get(to)
        val f = get(from)

        set(from, t)
        set(to, f)
    }
}

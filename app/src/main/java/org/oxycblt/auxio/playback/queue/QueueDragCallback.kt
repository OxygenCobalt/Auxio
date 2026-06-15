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
 
package org.oxycblt.auxio.playback.queue

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.list.recycler.MaterialDragCallback

/**
 * A highly customized [ItemTouchHelper.Callback] that enables some extra eye candy in the queue UI,
 * such as an animation when lifting items.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class QueueDragCallback(private val queueModel: QueueViewModel) : MaterialDragCallback() {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) =
        queueModel.moveQueueDataItems(
            viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        queueModel.removeQueueDataItem(viewHolder.bindingAdapterPosition)
    }
}

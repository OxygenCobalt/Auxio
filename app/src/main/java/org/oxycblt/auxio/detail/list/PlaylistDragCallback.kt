/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDragCallback.kt is part of Auxio.
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

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.list.recycler.MaterialDragCallback

/**
 * A [MaterialDragCallback] extension for playlist-specific item editing.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaylistDragCallback(private val detailModel: DetailViewModel) : MaterialDragCallback() {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) =
        detailModel.movePlaylistSongs(
            viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        detailModel.removePlaylistSong(viewHolder.bindingAdapterPosition)
    }
}

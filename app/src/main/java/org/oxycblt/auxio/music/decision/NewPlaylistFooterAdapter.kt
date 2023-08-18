/*
 * Copyright (c) 2023 Auxio Project
 * NewPlaylistFooterAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.decision

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemNewPlaylistChoiceBinding
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.util.inflater

/**
 * A purely-visual [RecyclerView.Adapter] that acts as a footer providing a "New Playlist" choice in
 * [AddToPlaylistDialog].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class NewPlaylistFooterAdapter(private val listener: Listener) :
    RecyclerView.Adapter<NewPlaylistFooterViewHolder>() {
    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NewPlaylistFooterViewHolder.from(parent)

    override fun onBindViewHolder(holder: NewPlaylistFooterViewHolder, position: Int) {
        holder.bind(listener)
    }

    /** A listener for [NewPlaylistFooterAdapter] interactions. */
    interface Listener {
        /**
         * Called when the footer has been pressed, requesting to create a new playlist to add to.
         */
        fun onNewPlaylist()
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a "New Playlist" choice in [NewPlaylistFooterAdapter].
 * Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class NewPlaylistFooterViewHolder
private constructor(private val binding: ItemNewPlaylistChoiceBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param listener A [NewPlaylistFooterAdapter.Listener] to bind interactions to.
     */
    fun bind(listener: NewPlaylistFooterAdapter.Listener) {
        binding.root.setOnClickListener { listener.onNewPlaylist() }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            NewPlaylistFooterViewHolder(
                ItemNewPlaylistChoiceBinding.inflate(parent.context.inflater))
    }
}

/*
 * Copyright (c) 2023 Auxio Project
 * DetailHeaderAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.header

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.MusicParent

/**
 * A [RecyclerView.Adapter] that implements shared behavior between each parent header view.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class DetailHeaderAdapter<T : MusicParent, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {
    private var currentParent: T? = null
    final override fun getItemCount() = 1
    final override fun onBindViewHolder(holder: VH, position: Int) =
        onBindHeader(holder, requireNotNull(currentParent))

    /**
     * Bind the created header [RecyclerView.ViewHolder] with the current [parent].
     *
     * @param holder The [RecyclerView.ViewHolder] to bind.
     * @param parent The current [MusicParent] to bind.
     */
    abstract fun onBindHeader(holder: VH, parent: T)

    /**
     * Update the [MusicParent] shown in the header.
     *
     * @param parent The new [MusicParent] to show.
     */
    fun setParent(parent: T) {
        currentParent = parent
        notifyItemChanged(0, PAYLOAD_UPDATE_HEADER)
    }

    /** An extended listener for [DetailHeaderAdapter] implementations. */
    interface Listener {
        /**
         * Called when the play button in a detail header is pressed, requesting that the current
         * item should be played.
         */
        fun onPlay()

        /**
         * Called when the shuffle button in a detail header is pressed, requesting that the current
         * item should be shuffled
         */
        fun onShuffle()
    }

    private companion object {
        val PAYLOAD_UPDATE_HEADER = Any()
    }
}

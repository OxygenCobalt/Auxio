/*
 * Copyright (c) 2021 Auxio Project
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
import org.oxycblt.auxio.util.logW

/**
 * An adapter capable of highlighting particular viewholders as "Playing". All behavior is handled
 * by the adapter, only the implementation ViewHolders need to add code to handle the indicator UI
 * itself.
 * @author OxygenCobalt
 */
abstract class PlayingIndicatorAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private var isPlaying = false
    private var currentItem: Item? = null

    override fun onBindViewHolder(holder: VH, position: Int) = throw UnsupportedOperationException()

    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        if (holder is ViewHolder) {
            val item = currentList[position]
            val currentItem = currentItem
            holder.updatePlayingIndicator(item == currentItem, isPlaying)
        }
    }

    abstract val currentList: List<Item>

    fun updateIndicator(item: Item?, isPlaying: Boolean) {
        var updatedItem = false

        if (currentItem != item) {
            val oldItem = currentItem
            currentItem = item

            if (oldItem != null) {
                val pos = currentList.indexOfFirst { it == oldItem }

                if (pos > -1) {
                    notifyItemChanged(pos, PAYLOAD_INDICATOR_CHANGED)
                } else {
                    logW("oldItem was not in adapter data")
                }
            }

            if (item != null) {
                val pos = currentList.indexOfFirst { it == item }

                if (pos > -1) {
                    notifyItemChanged(pos, PAYLOAD_INDICATOR_CHANGED)
                } else {
                    logW("newItem was not in adapter data")
                }
            }

            updatedItem = true
        }

        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying

            if (!updatedItem && item != null) {
                val pos = currentList.indexOfFirst { it == item }

                if (pos > -1) {
                    notifyItemChanged(pos, PAYLOAD_INDICATOR_CHANGED)
                } else {
                    logW("newItem was not in adapter data")
                }
            }
        }
    }

    companion object {
        val PAYLOAD_INDICATOR_CHANGED = Any()
    }

    /** A ViewHolder that can respond to playing ]indicator updates. */
    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean)
    }
}

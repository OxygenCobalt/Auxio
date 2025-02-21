/*
 * Copyright (c) 2021 Auxio Project
 * PlayingIndicatorAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber as L

/**
 * A [RecyclerView.Adapter] that supports indicating the playback status of a particular item.
 *
 * @param diffCallback A [DiffUtil.ItemCallback] to compare list updates with.
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class PlayingIndicatorAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : FlexibleListAdapter<T, VH>(diffCallback) {
    // There are actually two states for this adapter:
    // - The currently playing item, which is usually marked as "selected" and becomes accented.
    // - Whether playback is ongoing, which corresponds to whether the item's ImageGroup is
    // marked as "playing" or not.
    private var currentItem: T? = null
    private var isPlaying = false

    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        // Only try to update the playing indicator if the ViewHolder supports it
        if (holder is ViewHolder) {
            holder.updatePlayingIndicator(getItem(position) == currentItem, isPlaying)
        }

        if (payloads.isEmpty()) {
            // Not updating any indicator-specific attributes, so delegate to the concrete
            // adapter (actually bind the item)
            onBindViewHolder(holder, position)
        }
    }
    /**
     * Update the currently playing item in the list.
     *
     * @param item The [T] currently being played, or null if it is not being played.
     * @param isPlaying Whether playback is ongoing or paused.
     */
    fun setPlaying(item: T?, isPlaying: Boolean) {
        L.d("Updating playing item [old: $currentItem new: $item]")

        var updatedItem = false
        if (currentItem != item) {
            val oldItem = currentItem
            currentItem = item

            // Remove the playing indicator from the old item
            if (oldItem != null) {
                val pos = currentList.indexOfFirst { it == oldItem }
                if (pos > -1) {
                    notifyItemChanged(pos, PAYLOAD_PLAYING_INDICATOR_CHANGED)
                } else {
                    L.w("oldItem was not in adapter data")
                }
            }

            // Enable the playing indicator on the new item
            if (item != null) {
                val pos = currentList.indexOfFirst { it == item }
                if (pos > -1) {
                    notifyItemChanged(pos, PAYLOAD_PLAYING_INDICATOR_CHANGED)
                } else {
                    L.w("newItem was not in adapter data")
                }
            }

            updatedItem = true
        }

        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying

            // We may have already called notifyItemChanged before when checking
            // if the item was being played, so in that case we don't need to
            // update again here.
            if (!updatedItem && item != null) {
                val pos = currentList.indexOfFirst { it == item }
                if (pos > -1) {
                    notifyItemChanged(pos, PAYLOAD_PLAYING_INDICATOR_CHANGED)
                } else {
                    L.w("newItem was not in adapter data")
                }
            }
        }
    }

    /** A [RecyclerView.ViewHolder] that can display a playing indicator. */
    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        /**
         * Update the playing indicator within this [RecyclerView.ViewHolder].
         *
         * @param isActive True if this item is playing, false otherwise.
         * @param isPlaying True if playback is ongoing, false if paused. If this is true,
         *   [isActive] will also be true.
         */
        abstract fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean)
    }

    private companion object {
        val PAYLOAD_PLAYING_INDICATOR_CHANGED = Any()
    }
}

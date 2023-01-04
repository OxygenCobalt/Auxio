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
 
package org.oxycblt.auxio.home.tabs

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemTabBinding
import org.oxycblt.auxio.list.EditableListListener
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.util.inflater

/**
 * A [RecyclerView.Adapter] that displays an array of [Tab]s open for configuration.
 * @param listener A [EditableListListener] for tab interactions.
 */
class TabAdapter(private val listener: EditableListListener) :
    RecyclerView.Adapter<TabViewHolder>() {
    /** The current array of [Tab]s. */
    var tabs = arrayOf<Tab>()
        private set

    override fun getItemCount() = tabs.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TabViewHolder.from(parent)
    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(tabs[position], listener)
    }

    /**
     * Immediately update the tab array. This should be used when initializing the list.
     * @param newTabs The new array of tabs to show.
     */
    fun submitTabs(newTabs: Array<Tab>) {
        tabs = newTabs
        @Suppress("NotifyDatasetChanged") notifyDataSetChanged()
    }

    /**
     * Update a specific tab to the given value.
     * @param at The position of the tab to update.
     * @param tab The new tab.
     */
    fun setTab(at: Int, tab: Tab) {
        tabs[at] = tab
        // Use a payload to avoid an item change animation.
        notifyItemChanged(at, PAYLOAD_TAB_CHANGED)
    }

    /**
     * Swap two tabs with each other.
     * @param a The position of the first tab to swap.
     * @param b The position of the second tab to swap.
     */
    fun swapTabs(a: Int, b: Int) {
        val tmp = tabs[b]
        tabs[b] = tabs[a]
        tabs[a] = tmp
        notifyItemMoved(a, b)
    }

    private companion object {
        val PAYLOAD_TAB_CHANGED = Any()
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Tab]. Use [from] to create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
class TabViewHolder private constructor(private val binding: ItemTabBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param tab The new [Tab] to bind.
     * @param listener A [EditableListListener] to bind interactions to.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun bind(tab: Tab, listener: EditableListListener) {
        listener.bind(tab, this, dragHandle = binding.tabDragHandle)
        binding.tabCheckBox.apply {
            // Update the CheckBox name to align with the mode
            setText(
                when (tab.mode) {
                    MusicMode.SONGS -> R.string.lbl_songs
                    MusicMode.ALBUMS -> R.string.lbl_albums
                    MusicMode.ARTISTS -> R.string.lbl_artists
                    MusicMode.GENRES -> R.string.lbl_genres
                })

            // Unlike in other adapters, we update the checked state alongside
            // the tab data since they are in the same data structure (Tab)
            isChecked = tab is Tab.Visible
        }
    }

    companion object {
        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) = TabViewHolder(ItemTabBinding.inflate(parent.context.inflater))
    }
}

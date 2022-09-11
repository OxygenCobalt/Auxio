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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemTabBinding
import org.oxycblt.auxio.music.ui.MusicMode
import org.oxycblt.auxio.ui.recycler.DialogViewHolder
import org.oxycblt.auxio.util.inflater

class TabAdapter(private val listener: Listener) : RecyclerView.Adapter<TabViewHolder>() {
    var tabs = arrayOf<Tab>()
        private set

    override fun getItemCount() = tabs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TabViewHolder.new(parent)

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(tabs[position], listener)
    }

    @Suppress("NotifyDatasetChanged")
    fun submitTabs(newTabs: Array<Tab>) {
        tabs = newTabs
        notifyDataSetChanged()
    }

    fun setTab(at: Int, tab: Tab) {
        tabs[at] = tab
        notifyItemChanged(at, PAYLOAD_TAB_CHANGED)
    }

    fun moveItems(from: Int, to: Int) {
        val t = tabs[to]
        val f = tabs[from]
        tabs[from] = t
        tabs[to] = f
        notifyItemMoved(from, to)
    }

    interface Listener {
        fun onVisibilityToggled(mode: MusicMode)
        fun onPickUpTab(viewHolder: RecyclerView.ViewHolder)
    }

    companion object {
        val PAYLOAD_TAB_CHANGED = Any()
    }
}

class TabViewHolder private constructor(private val binding: ItemTabBinding) :
    DialogViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Tab, listener: TabAdapter.Listener) {
        binding.root.setOnClickListener { listener.onVisibilityToggled(item.mode) }

        binding.tabIcon.apply {
            setText(
                when (item.mode) {
                    MusicMode.SONGS -> R.string.lbl_songs
                    MusicMode.ALBUMS -> R.string.lbl_albums
                    MusicMode.ARTISTS -> R.string.lbl_artists
                    MusicMode.GENRES -> R.string.lbl_genres
                }
            )
            isChecked = item is Tab.Visible
        }

        // Roll our own drag handlers as the default ones suck
        binding.tabDragHandle.setOnTouchListener { _, motionEvent ->
            binding.tabDragHandle.performClick()
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                listener.onPickUpTab(this)
                true
            } else false
        }
    }

    companion object {
        fun new(parent: View) = TabViewHolder(ItemTabBinding.inflate(parent.context.inflater))
    }
}

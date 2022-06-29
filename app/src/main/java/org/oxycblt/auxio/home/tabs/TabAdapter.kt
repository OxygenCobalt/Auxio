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
import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemTabBinding
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.recycler.BackingData
import org.oxycblt.auxio.ui.recycler.BindingViewHolder
import org.oxycblt.auxio.ui.recycler.MonoAdapter
import org.oxycblt.auxio.util.inflater

class TabAdapter(listener: Listener) :
    MonoAdapter<Tab, TabAdapter.Listener, TabViewHolder>(listener) {
    override val data = TabData(this)
    override val creator = TabViewHolder.CREATOR

    interface Listener {
        fun onVisibilityToggled(displayMode: DisplayMode)
        fun onPickUpTab(viewHolder: RecyclerView.ViewHolder)
    }

    class TabData(private val adapter: RecyclerView.Adapter<*>) : BackingData<Tab>() {
        var tabs = arrayOf<Tab>()
            private set

        override fun getItem(position: Int) = tabs[position]
        override fun getItemCount() = tabs.size

        @Suppress("NotifyDatasetChanged")
        fun submitTabs(newTabs: Array<Tab>) {
            tabs = newTabs
            adapter.notifyDataSetChanged()
        }

        fun setTab(at: Int, tab: Tab) {
            tabs[at] = tab
            adapter.notifyItemChanged(at, PAYLOAD_TAB_CHANGED)
        }

        fun moveItems(from: Int, to: Int) {
            val t = tabs[to]
            val f = tabs[from]
            tabs[from] = t
            tabs[to] = f
            adapter.notifyItemMoved(from, to)
        }
    }

    companion object {
        val PAYLOAD_TAB_CHANGED = Any()
    }
}

class TabViewHolder private constructor(private val binding: ItemTabBinding) :
    BindingViewHolder<Tab, TabAdapter.Listener>(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    override fun bind(item: Tab, listener: TabAdapter.Listener) {
        binding.root.apply { setOnClickListener { listener.onVisibilityToggled(item.mode) } }

        binding.tabIcon.apply {
            setText(item.mode.string)
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

        binding.root.setOnLongClickListener {
            listener.onPickUpTab(this)
            true
        }
    }

    companion object {
        val CREATOR =
            object : Creator<TabViewHolder> {
                override val viewType: Int
                    get() = throw UnsupportedOperationException()

                override fun create(context: Context) =
                    TabViewHolder(ItemTabBinding.inflate(context.inflater))
            }
    }
}

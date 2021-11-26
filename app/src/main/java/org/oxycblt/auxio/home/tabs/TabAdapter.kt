/*
 * Copyright (c) 2021 Auxio Project
 * TabAdapter.kt is part of Auxio.
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
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemTabBinding
import org.oxycblt.auxio.util.inflater

class TabAdapter(
    private val touchHelper: ItemTouchHelper,
    private val getTabs: () -> Array<Tab>,
    private val onTabSwitch: (Tab) -> Unit,
) : RecyclerView.Adapter<TabAdapter.TabViewHolder>() {
    private val tabs: Array<Tab> get() = getTabs()

    override fun getItemCount(): Int = Tab.SEQUENCE_LEN

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        return TabViewHolder(ItemTabBinding.inflate(parent.context.inflater))
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(tabs[position])
    }

    inner class TabViewHolder(
        private val binding: ItemTabBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(tab: Tab) {
            binding.root.apply {
                setOnClickListener {
                    // Don't do a typical notifyDataSetChanged call here, because
                    // A. We don't have a real ViewModel state since this is a dialog
                    // B. Doing so would cause a relayout and the ripple effect to disappear
                    // Instead, simply notify a tab change and let TabCustomizeDialog handle it.
                    binding.tabIcon.isChecked = !binding.tabIcon.isChecked
                    onTabSwitch(tab)
                }
            }

            binding.tabIcon.apply {
                setText(tab.mode.string)
                isChecked = tab is Tab.Visible
            }

            binding.tabDragHandle.setOnTouchListener { _, motionEvent ->
                binding.tabDragHandle.performClick()

                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(this)
                    true
                } else false
            }
        }
    }
}

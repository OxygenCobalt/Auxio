/*
 * Copyright (c) 2021 Auxio Project
 * AccentAdapter.kt is part of Auxio.
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

package org.oxycblt.auxio.accent

import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAccentBinding
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.resolveStateList

/**
 * An adapter that displays the list of all possible accents, and highlights the current one.
 * @author OxygenCobalt
 * @param onSelect What to do when an accent is selected.
 */

class AccentAdapter(
    private var curAccent: Accent,
    private val onSelect: (accent: Accent) -> Unit
) : RecyclerView.Adapter<AccentAdapter.ViewHolder>() {
    private var selectedViewHolder: ViewHolder? = null

    override fun getItemCount(): Int = ACCENT_PRIMARY_COLORS.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAccentBinding.inflate(parent.context.inflater))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(Accent(position))
    }

    private fun setAccent(accent: Accent) {
        curAccent = accent
        onSelect(accent)
    }

    inner class ViewHolder(
        private val binding: ItemAccentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(accent: Accent) {
            setSelected(accent == curAccent)

            binding.accent.apply {
                contentDescription = context.getString(accent.name)
                backgroundTintList = accent.getStateList(context)
                TooltipCompat.setTooltipText(this, contentDescription)
            }

            binding.accent.setOnClickListener {
                setAccent(accent)
                setSelected(true)
            }
        }

        private fun setSelected(isSelected: Boolean) {
            val context = binding.accent.context

            binding.accent.isEnabled = !isSelected

            binding.accent.imageTintList = if (isSelected) {
                // Switch out the currently selected viewholder with this one.
                selectedViewHolder?.setSelected(false)
                selectedViewHolder = this

                R.color.surface.resolveStateList(context)
            } else {
                android.R.color.transparent.resolveStateList(context)
            }
        }
    }
}

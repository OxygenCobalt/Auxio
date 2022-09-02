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
 
package org.oxycblt.auxio.ui.accent

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAccentBinding
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getColorCompat
import org.oxycblt.auxio.util.inflater

/**
 * An adapter that displays the accent palette.
 * @author OxygenCobalt
 */
class AccentAdapter(private val listener: Listener) : RecyclerView.Adapter<AccentViewHolder>() {
    var selectedAccent: Accent? = null
        private set

    override fun getItemCount() = Accent.MAX

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AccentViewHolder.new(parent)

    override fun onBindViewHolder(holder: AccentViewHolder, position: Int) =
        throw IllegalStateException()

    override fun onBindViewHolder(
        holder: AccentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = Accent.from(position)

        if (payloads.isEmpty()) {
            holder.bind(item, listener)
        }

        holder.setSelected(item == selectedAccent)
    }

    fun setSelectedAccent(accent: Accent) {
        if (accent == selectedAccent) return
        selectedAccent?.let { old -> notifyItemChanged(old.index, PAYLOAD_SELECTION_CHANGED) }
        selectedAccent = accent
        notifyItemChanged(accent.index, PAYLOAD_SELECTION_CHANGED)
    }

    interface Listener {
        fun onAccentSelected(accent: Accent)
    }

    companion object {
        val PAYLOAD_SELECTION_CHANGED = Any()
    }
}

class AccentViewHolder private constructor(private val binding: ItemAccentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Accent, listener: AccentAdapter.Listener) {
        setSelected(false)

        binding.accent.apply {
            backgroundTintList = context.getColorCompat(item.primary)
            contentDescription = context.getString(item.name)
            TooltipCompat.setTooltipText(this, contentDescription)
            setOnClickListener { listener.onAccentSelected(item) }
        }
    }

    fun setSelected(isSelected: Boolean) {
        binding.accent.apply {
            isEnabled = !isSelected
            iconTint =
                if (isSelected) {
                    context.getAttrColorCompat(R.attr.colorSurface)
                } else {
                    context.getColorCompat(android.R.color.transparent)
                }
        }
    }

    companion object {
        fun new(parent: View) = AccentViewHolder(ItemAccentBinding.inflate(parent.context.inflater))
    }
}

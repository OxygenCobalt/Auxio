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

import android.content.Context
import androidx.appcompat.widget.TooltipCompat
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAccentBinding
import org.oxycblt.auxio.ui.recycler.BackingData
import org.oxycblt.auxio.ui.recycler.BindingViewHolder
import org.oxycblt.auxio.ui.recycler.MonoAdapter
import org.oxycblt.auxio.util.getAttrColorSafe
import org.oxycblt.auxio.util.getColorSafe
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.stateList

/**
 * An adapter that displays the accent palette.
 * @author OxygenCobalt
 */
class AccentAdapter(listener: Listener) :
    MonoAdapter<Accent, AccentAdapter.Listener, AccentViewHolder>(listener) {
    var selectedAccent: Accent? = null
        private set

    override val data = AccentData()
    override val creator = AccentViewHolder.CREATOR

    override fun onBind(
        viewHolder: AccentViewHolder,
        item: Accent,
        listener: Listener,
        payload: List<Any>
    ) {
        if (payload.isEmpty()) {
            super.onBind(viewHolder, item, listener, payload)
        }

        viewHolder.setSelected(item == selectedAccent)
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

    class AccentData : BackingData<Accent>() {
        override fun getItem(position: Int) = Accent.from(position)
        override fun getItemCount() = Accent.MAX
    }

    companion object {
        val PAYLOAD_SELECTION_CHANGED = Any()
    }
}

class AccentViewHolder private constructor(private val binding: ItemAccentBinding) :
    BindingViewHolder<Accent, AccentAdapter.Listener>(binding.root) {

    override fun bind(item: Accent, listener: AccentAdapter.Listener) {
        setSelected(false)

        binding.accent.apply {
            backgroundTintList = context.getColorSafe(item.primary).stateList
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
                    context.getAttrColorSafe(R.attr.colorSurface).stateList
                } else {
                    context.getColorSafe(android.R.color.transparent).stateList
                }
        }
    }

    companion object {
        val CREATOR =
            object : Creator<AccentViewHolder> {
                override val viewType: Int
                    get() = throw UnsupportedOperationException()

                override fun create(context: Context) =
                    AccentViewHolder(ItemAccentBinding.inflate(context.inflater))
            }
    }
}

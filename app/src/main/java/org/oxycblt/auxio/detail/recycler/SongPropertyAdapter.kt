/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.detail.recycler

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemSongPropertyBinding
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.adapter.BasicListInstructions
import org.oxycblt.auxio.list.adapter.DiffAdapter
import org.oxycblt.auxio.list.adapter.ListDiffer
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for [SongProperty] instances.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongPropertyAdapter :
    DiffAdapter<SongProperty, BasicListInstructions, SongPropertyViewHolder>(
        ListDiffer.Blocking(SongPropertyViewHolder.DIFF_CALLBACK)) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SongPropertyViewHolder.from(parent)

    override fun onBindViewHolder(holder: SongPropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * A property entry for use in [SongPropertyAdapter].
 * @param name The contextual title to use for the property.
 * @param value The value of the property.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class SongProperty(@StringRes val name: Int, val value: String) : Item

/**
 * A [RecyclerView.ViewHolder] that displays a [SongProperty]. Use [from] to create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongPropertyViewHolder private constructor(private val binding: ItemSongPropertyBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    fun bind(property: SongProperty) {
        val context = binding.context
        binding.propertyName.hint = context.getString(property.name)
        binding.propertyValue.setText(property.value)
    }

    companion object {
        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            SongPropertyViewHolder(ItemSongPropertyBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<SongProperty>() {
                override fun areContentsTheSame(oldItem: SongProperty, newItem: SongProperty) =
                    oldItem.name == newItem.name && oldItem.value == newItem.value
            }
    }
}

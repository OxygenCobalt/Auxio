/*
 * Copyright (c) 2021 Auxio Project
 * BaseViewHolder.kt is part of Auxio.
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

package org.oxycblt.auxio.recycler.viewholders

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

/**
 * A [RecyclerView.ViewHolder] that streamlines a lot of the common things across all viewholders.
 * @param T The datatype, inheriting [BaseModel] for this ViewHolder.
 * @param binding Basic [ViewDataBinding] required to set up click listeners & sizing.
 * @param doOnClick (Optional) Function that calls on a click.
 * @param doOnLongClick (Optional) Functions that calls on a long-click.
 * @author OxygenCobalt
 */
abstract class BaseViewHolder<T : BaseModel>(
    private val binding: ViewDataBinding,
    private val doOnClick: ((data: T) -> Unit)? = null,
    private val doOnLongClick: ((view: View, data: T) -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {
    init {
        // Force the layout to *actually* be the screen width
        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Bind the viewholder with whatever [BaseModel] instance that has been specified.
     * Will call [onBind] on the inheriting ViewHolder.
     * @param data Data that the viewholder should be bound with
     */
    fun bind(data: T) {
        doOnClick?.let { onClick ->
            binding.root.setOnClickListener {
                onClick(data)
            }
        }

        doOnLongClick?.let { onLongClick ->
            binding.root.setOnLongClickListener { view ->
                onLongClick(view, data)

                true
            }
        }

        onBind(data)

        binding.executePendingBindings()
    }

    /**
     * Function that performs binding operations unique to the inheriting viewholder.
     * Add any specialized code to an override of this instead of [BaseViewHolder] itself.
     */
    protected abstract fun onBind(data: T)
}

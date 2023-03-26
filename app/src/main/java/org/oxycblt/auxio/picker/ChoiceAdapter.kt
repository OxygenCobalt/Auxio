/*
 * Copyright (c) 2023 Auxio Project
 * ChoiceAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.picker

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemPickerChoiceBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/** A [RecyclerView.Adapter] that shows a list */
class ChoiceAdapter<T : Music>(private val listener: ClickableListListener<T>) :
    FlexibleListAdapter<T, ChoiceViewHolder<T>>(ChoiceViewHolder.diffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChoiceViewHolder.from<T>(parent)

    override fun onBindViewHolder(holder: ChoiceViewHolder<T>, position: Int) =
        holder.bind(getItem(position), listener)
}

/**
 * A [DialogRecyclerView.ViewHolder] that displays a smaller variant of a typical [T] item, for use
 * with [ChoiceAdapter]. Use [from] to create an instance.
 */
class ChoiceViewHolder<T : Music>
private constructor(private val binding: ItemPickerChoiceBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param music The new [T] to bind.
     * @param listener A [ClickableListListener] to bind interactions to.
     */
    fun bind(music: T, listener: ClickableListListener<T>) {
        listener.bind(music, this)
        // ImageGroup is not generic, so we must downcast to specific types for now.
        when (music) {
            is Song -> binding.pickerImage.bind(music)
            is Album -> binding.pickerImage.bind(music)
            is Artist -> binding.pickerImage.bind(music)
            is Genre -> binding.pickerImage.bind(music)
            is Playlist -> binding.pickerImage.bind(music)
        }
        binding.pickerName.text = music.resolveName(binding.context)
    }

    companion object {

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun <T : Music> from(parent: View) =
            ChoiceViewHolder<T>(ItemPickerChoiceBinding.inflate(parent.context.inflater))

        /** Get a comparator that can be used with DiffUtil. */
        fun <T : Music> diffCallback() =
            object : SimpleDiffCallback<T>() {
                override fun areContentsTheSame(oldItem: T, newItem: T) =
                    oldItem.rawName == newItem.rawName
            }
    }
}

/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music.picker

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemPickerChoiceBinding
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.ui.recycler.DialogViewHolder
import org.oxycblt.auxio.ui.recycler.ItemClickListener
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * The adapter that displays a list of genre choices in the picker UI.
 */
class GenreChoiceAdapter(private val listener: ItemClickListener) : RecyclerView.Adapter<GenreChoiceViewHolder>() {
    private var genres = listOf<Genre>()

    override fun getItemCount() = genres.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GenreChoiceViewHolder.new(parent)

    override fun onBindViewHolder(holder: GenreChoiceViewHolder, position: Int) =
        holder.bind(genres[position], listener)

    fun submitList(newGenres: List<Genre>) {
        if (newGenres != genres) {
            genres = newGenres

            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged()
        }
    }
}

/**
 * The ViewHolder that displays a genre choice. Smaller than other parent items due to dialog
 * constraints.
 */
class GenreChoiceViewHolder(private val binding: ItemPickerChoiceBinding) : DialogViewHolder(binding.root) {
    fun bind(genre: Genre, listener: ItemClickListener) {
        binding.pickerImage.bind(genre)
        binding.pickerName.text = genre.resolveName(binding.context)
        binding.root.setOnClickListener {
            listener.onItemClick(genre)
        }
    }

    companion object {
        fun new(parent: View) =
            GenreChoiceViewHolder(ItemPickerChoiceBinding.inflate(parent.context.inflater))
    }
}

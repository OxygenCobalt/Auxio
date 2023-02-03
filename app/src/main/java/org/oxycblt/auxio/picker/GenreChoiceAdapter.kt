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
 
package org.oxycblt.auxio.picker

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemPickerChoiceBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * An [RecyclerView.Adapter] that displays a list of [Genre] choices.
 * @param listener A [ClickableListListener] to bind interactions to.
 * @author OxygenCobalt.
 */
class GenreChoiceAdapter(private val listener: ClickableListListener<Genre>) :
    RecyclerView.Adapter<GenreChoiceViewHolder>() {
    private var genres = listOf<Genre>()

    override fun getItemCount() = genres.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GenreChoiceViewHolder.from(parent)

    override fun onBindViewHolder(holder: GenreChoiceViewHolder, position: Int) =
        holder.bind(genres[position], listener)

    /**
     * Immediately update the [Genre] choices.
     * @param newGenres The new [Genre]s to show.
     */
    fun submitList(newGenres: List<Genre>) {
        if (newGenres != genres) {
            genres = newGenres
            @Suppress("NotifyDataSetChanged") notifyDataSetChanged()
        }
    }
}

/**
 * A [DialogRecyclerView.ViewHolder] that displays a smaller variant of a typical [Genre] item, for
 * use with [GenreChoiceAdapter]. Use [from] to create an instance.
 */
class GenreChoiceViewHolder(private val binding: ItemPickerChoiceBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param genre The new [Genre] to bind.
     * @param listener A [ClickableListListener] to bind interactions to.
     */
    fun bind(genre: Genre, listener: ClickableListListener<Genre>) {
        listener.bind(genre, this)
        binding.pickerImage.bind(genre)
        binding.pickerName.text = genre.resolveName(binding.context)
    }

    companion object {
        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            GenreChoiceViewHolder(ItemPickerChoiceBinding.inflate(parent.context.inflater))
    }
}

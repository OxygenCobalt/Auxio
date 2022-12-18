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
import org.oxycblt.auxio.list.ItemClickCallback
import org.oxycblt.auxio.list.recycler.DialogViewHolder
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/** The adapter that displays a list of artist choices in the picker UI. */
class ArtistChoiceAdapter(private val callback: ItemClickCallback) :
    RecyclerView.Adapter<ArtistChoiceViewHolder>() {
    private var artists = listOf<Artist>()

    override fun getItemCount() = artists.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArtistChoiceViewHolder.new(parent)

    override fun onBindViewHolder(holder: ArtistChoiceViewHolder, position: Int) =
        holder.bind(artists[position], callback)

    fun submitList(newArtists: List<Artist>) {
        if (newArtists != artists) {
            artists = newArtists

            @Suppress("NotifyDataSetChanged") notifyDataSetChanged()
        }
    }
}

/**
 * The ViewHolder that displays a artist choice. Smaller than other parent items due to dialog
 * constraints.
 */
class ArtistChoiceViewHolder(private val binding: ItemPickerChoiceBinding) :
    DialogViewHolder(binding.root) {
    fun bind(artist: Artist, callback: ItemClickCallback) {
        binding.pickerImage.bind(artist)
        binding.pickerName.text = artist.resolveName(binding.context)
        binding.root.setOnClickListener { callback.onClick(artist) }
    }

    companion object {
        fun new(parent: View) =
            ArtistChoiceViewHolder(ItemPickerChoiceBinding.inflate(parent.context.inflater))
    }
}

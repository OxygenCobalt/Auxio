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
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * An [RecyclerView.Adapter] that displays a list of [Artist] choices.
 * @param listener A [ClickableListListener] to bind interactions to.
 * @author OxygenCobalt.
 */
class ArtistChoiceAdapter(private val listener: ClickableListListener<Artist>) :
    RecyclerView.Adapter<ArtistChoiceViewHolder>() {
    private var artists = listOf<Artist>()

    override fun getItemCount() = artists.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArtistChoiceViewHolder.from(parent)

    override fun onBindViewHolder(holder: ArtistChoiceViewHolder, position: Int) =
        holder.bind(artists[position], listener)

    /**
     * Immediately update the [Artist] choices.
     * @param newArtists The new [Artist]s to show.
     */
    fun submitList(newArtists: List<Artist>) {
        if (newArtists != artists) {
            artists = newArtists
            @Suppress("NotifyDataSetChanged") notifyDataSetChanged()
        }
    }
}

/**
 * A [DialogRecyclerView.ViewHolder] that displays a smaller variant of a typical [Artist] item, for
 * use with [ArtistChoiceAdapter]. Use [from] to create an instance.
 */
class ArtistChoiceViewHolder(private val binding: ItemPickerChoiceBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param artist The new [Artist] to bind.
     * @param listener A [ClickableListListener] to bind interactions to.
     */
    fun bind(artist: Artist, listener: ClickableListListener<Artist>) {
        listener.bind(artist, this)
        binding.pickerImage.bind(artist)
        binding.pickerName.text = artist.resolveName(binding.context)
    }

    companion object {
        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            ArtistChoiceViewHolder(ItemPickerChoiceBinding.inflate(parent.context.inflater))
    }
}

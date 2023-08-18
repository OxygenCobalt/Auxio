/*
 * Copyright (c) 2023 Auxio Project
 * ArtistShowChoice.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.decision

import android.view.View
import android.view.ViewGroup
import org.oxycblt.auxio.databinding.ItemPickerChoiceBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * A [FlexibleListAdapter] that displays a list of [Artist] navigation choices, for use with
 * [ShowArtistDialog].
 *
 * @param listener A [ClickableListListener] to bind interactions to.
 */
class ArtistShowChoice(private val listener: ClickableListListener<Artist>) :
    FlexibleListAdapter<Artist, ArtistNavigationChoiceViewHolder>(
        ArtistNavigationChoiceViewHolder.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArtistNavigationChoiceViewHolder.from(parent)

    override fun onBindViewHolder(holder: ArtistNavigationChoiceViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

/**
 * A [DialogRecyclerView.ViewHolder] that displays a smaller variant of a typical [Artist] item, for
 * use [ArtistShowChoice]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistNavigationChoiceViewHolder
private constructor(private val binding: ItemPickerChoiceBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param artist The new [Artist] to bind.
     * @param listener A [ClickableListListener] to bind interactions to.
     */
    fun bind(artist: Artist, listener: ClickableListListener<Artist>) {
        listener.bind(artist, this)
        binding.pickerImage.bind(artist)
        binding.pickerName.text = artist.name.resolve(binding.context)
    }

    companion object {

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            ArtistNavigationChoiceViewHolder(
                ItemPickerChoiceBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Artist>() {
                override fun areContentsTheSame(oldItem: Artist, newItem: Artist) =
                    oldItem.name == newItem.name
            }
    }
}

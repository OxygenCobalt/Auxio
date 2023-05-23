/*
 * Copyright (c) 2023 Auxio Project
 * GenreDetailHeaderAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.header

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemDetailHeaderBinding
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * A [DetailHeaderAdapter] that shows [Genre] information.
 *
 * @param listener [DetailHeaderAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreDetailHeaderAdapter(private val listener: Listener) :
    DetailHeaderAdapter<Genre, GenreDetailHeaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GenreDetailHeaderViewHolder.from(parent)

    override fun onBindHeader(holder: GenreDetailHeaderViewHolder, parent: Genre) =
        holder.bind(parent, listener)
}

/**
 * A [RecyclerView.ViewHolder] that displays the [Genre] header in the detail view. Use [from] to
 * create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreDetailHeaderViewHolder
private constructor(private val binding: ItemDetailHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param genre The new [Genre] to bind.
     * @param listener A [DetailHeaderAdapter.Listener] to bind interactions to.
     */
    fun bind(genre: Genre, listener: DetailHeaderAdapter.Listener) {
        binding.detailCover.bind(genre)
        binding.detailType.text = binding.context.getString(R.string.lbl_genre)
        binding.detailName.text = genre.name.resolve(binding.context)
        // Nothing about a genre is applicable to the sub-head text.
        binding.detailSubhead.isVisible = false
        // The song and artist count of the genre maps to the info text.
        binding.detailInfo.text =
            binding.context.getString(
                R.string.fmt_two,
                binding.context.getPlural(R.plurals.fmt_artist_count, genre.artists.size),
                binding.context.getPlural(R.plurals.fmt_song_count, genre.songs.size))
        binding.detailPlayButton.setOnClickListener { listener.onPlay() }
        binding.detailShuffleButton.setOnClickListener { listener.onShuffle() }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            GenreDetailHeaderViewHolder(ItemDetailHeaderBinding.inflate(parent.context.inflater))
    }
}

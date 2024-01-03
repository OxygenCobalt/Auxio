/*
 * Copyright (c) 2023 Auxio Project
 * ArtistDetailHeaderAdapter.kt is part of Auxio.
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
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD

/**
 * A [DetailHeaderAdapter] that shows [Artist] information.
 *
 * @param listener [DetailHeaderAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistDetailHeaderAdapter(private val listener: Listener) :
    DetailHeaderAdapter<Artist, ArtistDetailHeaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArtistDetailHeaderViewHolder.from(parent)

    override fun onBindHeader(holder: ArtistDetailHeaderViewHolder, parent: Artist) =
        holder.bind(parent, listener)
}

/**
 * A [RecyclerView.ViewHolder] that displays the [Artist] header in the detail view. Use [from] to
 * create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistDetailHeaderViewHolder
private constructor(private val binding: ItemDetailHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * Bind new data to this instance.
     *
     * @param artist The new [Artist] to bind.
     * @param listener A [DetailHeaderAdapter.Listener] to bind interactions to.
     */
    fun bind(artist: Artist, listener: DetailHeaderAdapter.Listener) {
        binding.detailCover.bind(artist)
        binding.detailType.text = binding.context.getString(R.string.lbl_artist)
        binding.detailName.text = artist.name.resolve(binding.context)

        // Song and album counts map to the info
        binding.detailInfo.text =
            binding.context.getString(
                R.string.fmt_two,
                if (artist.explicitAlbums.isNotEmpty()) {
                    binding.context.getPlural(R.plurals.fmt_album_count, artist.explicitAlbums.size)
                } else {
                    binding.context.getString(R.string.def_album_count)
                },
                if (artist.songs.isNotEmpty()) {
                    binding.context.getPlural(R.plurals.fmt_song_count, artist.songs.size)
                } else {
                    binding.context.getString(R.string.def_song_count)
                })

        if (artist.songs.isNotEmpty()) {
            // Information about the artist's genre(s) map to the sub-head text
            binding.detailSubhead.apply {
                isVisible = true
                text = artist.genres.resolveNames(context)
            }

            // In the case that this header used to he configured to have no songs,
            // we want to reset the visibility of all information that was hidden.
            binding.detailPlayButton.isVisible = true
            binding.detailShuffleButton.isVisible = true
        } else {
            // The artist does not have any songs, so hide functionality that makes no sense.
            // ex. Play and Shuffle, Song Counts, and Genre Information.
            // Artists are always guaranteed to have albums however, so continue to show those.
            logD("Artist is empty, disabling genres and playback")
            binding.detailSubhead.isVisible = false
            binding.detailPlayButton.isEnabled = false
            binding.detailShuffleButton.isEnabled = false
        }

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
            ArtistDetailHeaderViewHolder(ItemDetailHeaderBinding.inflate(parent.context.inflater))
    }
}

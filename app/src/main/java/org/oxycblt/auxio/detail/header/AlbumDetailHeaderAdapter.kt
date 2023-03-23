/*
 * Copyright (c) 2023 Auxio Project
 * AlbumDetailHeaderAdapter.kt is part of Auxio.
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
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemDetailHeaderBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * A [DetailHeaderAdapter] that shows [Album] information.
 *
 * @param listener [DetailHeaderAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumDetailHeaderAdapter(private val listener: Listener) :
    DetailHeaderAdapter<Album, AlbumDetailHeaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AlbumDetailHeaderViewHolder.from(parent)

    override fun onBindHeader(holder: AlbumDetailHeaderViewHolder, parent: Album) =
        holder.bind(parent, listener)

    /** An extended listener for [DetailHeaderAdapter] implementations. */
    interface Listener : DetailHeaderAdapter.Listener {

        /**
         * Called when the artist name in the [Album] header was clicked, requesting navigation to
         * it's parent artist.
         */
        fun onNavigateToParentArtist()
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays the [Album] header in the detail view. Use [from] to
 * create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumDetailHeaderViewHolder
private constructor(private val binding: ItemDetailHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * Bind new data to this instance.
     *
     * @param album The new [Album] to bind.
     * @param listener A [AlbumDetailHeaderAdapter.Listener] to bind interactions to.
     */
    fun bind(album: Album, listener: AlbumDetailHeaderAdapter.Listener) {
        binding.detailCover.bind(album)

        // The type text depends on the release type (Album, EP, Single, etc.)
        binding.detailType.text = binding.context.getString(album.releaseType.stringRes)

        binding.detailName.text = album.resolveName(binding.context)

        // Artist name maps to the subhead text
        binding.detailSubhead.apply {
            text = album.artists.resolveNames(context)

            // Add a QoL behavior where navigation to the artist will occur if the artist
            // name is pressed.
            setOnClickListener { listener.onNavigateToParentArtist() }
        }

        // Date, song count, and duration map to the info text
        binding.detailInfo.apply {
            // Fall back to a friendlier "No date" text if the album doesn't have date information
            val date = album.dates?.resolveDate(context) ?: context.getString(R.string.def_date)
            val songCount = context.getPlural(R.plurals.fmt_song_count, album.songs.size)
            val duration = album.durationMs.formatDurationMs(true)
            text = context.getString(R.string.fmt_three, date, songCount, duration)
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
            AlbumDetailHeaderViewHolder(ItemDetailHeaderBinding.inflate(parent.context.inflater))
    }
}

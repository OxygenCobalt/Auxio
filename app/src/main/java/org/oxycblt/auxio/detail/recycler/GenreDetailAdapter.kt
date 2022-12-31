/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.detail.recycler

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.recycler.ArtistViewHolder
import org.oxycblt.auxio.list.recycler.SimpleItemCallback
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * An [DetailAdapter] implementing the header and sub-items for the [Genre] detail view.
 * @param listener A [DetailAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreDetailAdapter(private val listener: Listener) : DetailAdapter(listener, DIFF_CALLBACK) {
    override fun getItemViewType(position: Int) =
        when (differ.currentList[position]) {
            // Support the Genre header and generic Artist/Song items. There's nothing about
            // a genre that will make the artists/songs homogeneous, so it doesn't matter what we
            // use for their ViewHolders.
            is Genre -> GenreDetailViewHolder.VIEW_TYPE
            is Artist -> ArtistViewHolder.VIEW_TYPE
            is Song -> SongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            GenreDetailViewHolder.VIEW_TYPE -> GenreDetailViewHolder.from(parent)
            ArtistViewHolder.VIEW_TYPE -> ArtistViewHolder.from(parent)
            SongViewHolder.VIEW_TYPE -> SongViewHolder.from(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (val item = differ.currentList[position]) {
            is Genre -> (holder as GenreDetailViewHolder).bind(item, listener)
            is Artist -> (holder as ArtistViewHolder).bind(item, listener)
            is Song -> (holder as SongViewHolder).bind(item, listener)
        }
    }

    override fun isItemFullWidth(position: Int): Boolean {
        // Genre headers should be full-width in all configurations
        val item = differ.currentList[position]
        return super.isItemFullWidth(position) || item is Genre
    }

    private companion object {
        val DIFF_CALLBACK =
            object : SimpleItemCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Genre && newItem is Genre ->
                            GenreDetailViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Artist && newItem is Artist ->
                            ArtistViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            SongViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        else -> DetailAdapter.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                    }
                }
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays the [Genre] header in the detail view. Use [from] to
 * create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
private class GenreDetailViewHolder private constructor(private val binding: ItemDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param genre The new [Song] to bind.
     * @param listener A [DetailAdapter.Listener] to bind interactions to.
     */
    fun bind(genre: Genre, listener: DetailAdapter.Listener) {
        binding.detailCover.bind(genre)
        binding.detailType.text = binding.context.getString(R.string.lbl_genre)
        binding.detailName.text = genre.resolveName(binding.context)
        // Nothing about a genre is applicable to the sub-head text.
        binding.detailSubhead.isVisible = false
        // The song count of the genre maps to the info text.
        binding.detailInfo.text =
            binding.context.getString(
                R.string.fmt_two,
                binding.context.getPlural(R.plurals.fmt_artist_count, genre.artists.size),
                binding.context.getPlural(R.plurals.fmt_song_count, genre.songs.size))
        binding.detailPlayButton.setOnClickListener { listener.onPlay() }
        binding.detailShuffleButton.setOnClickListener { listener.onShuffle() }
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_GENRE_DETAIL

        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            GenreDetailViewHolder(ItemDetailBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleItemCallback<Genre>() {
                override fun areContentsTheSame(oldItem: Genre, newItem: Genre) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.songs.size == newItem.songs.size &&
                        oldItem.durationMs == newItem.durationMs
            }
    }
}

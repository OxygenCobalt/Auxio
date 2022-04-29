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
 
package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.recycler.DetailAdapter
import org.oxycblt.auxio.detail.recycler.GenreDetailAdapter
import org.oxycblt.auxio.detail.recycler.SortHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.Header
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * The [DetailFragment] for a genre.
 * @author OxygenCobalt
 */
class GenreDetailFragment : DetailFragment(), DetailAdapter.Listener {
    private val args: GenreDetailFragmentArgs by navArgs()
    private val detailAdapter = GenreDetailAdapter(this)

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        detailModel.setGenreId(args.genreId)

        setupToolbar(unlikelyToBeNull(detailModel.currentGenre.value))
        binding.detailRecycler.apply {
            adapter = detailAdapter
            applySpans { pos ->
                val item = detailAdapter.data.currentList[pos]
                item is Header || item is SortHeader || item is Genre
            }
        }

        // --- VIEWMODEL SETUP ---

        detailModel.genreData.observe(viewLifecycleOwner, detailAdapter.data::submitList)
        navModel.exploreNavigationItem.observe(viewLifecycleOwner, ::handleNavigation)
        playbackModel.song.observe(viewLifecycleOwner, ::updateSong)
    }

    override fun onItemClick(item: Item) {
        when (item) {
            is Song -> playbackModel.playSong(item, PlaybackMode.IN_GENRE)
            is Album ->
                findNavController()
                    .navigate(ArtistDetailFragmentDirections.actionShowAlbum(item.id))
        }
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        newMenu(anchor, item)
    }

    override fun onPlayParent() {
        playbackModel.playGenre(unlikelyToBeNull(detailModel.currentGenre.value), false)
    }

    override fun onShuffleParent() {
        playbackModel.playGenre(unlikelyToBeNull(detailModel.currentGenre.value), true)
    }

    override fun onShowSortMenu(anchor: View) {
        showSortMenu(anchor, detailModel.genreSort, onConfirm = { detailModel.genreSort = it })
    }

    private fun handleNavigation(item: Music?) {
        when (item) {
            is Song -> {
                logD("Navigating to another song")
                findNavController()
                    .navigate(GenreDetailFragmentDirections.actionShowAlbum(item.album.id))
            }
            is Album -> {
                logD("Navigating to another album")
                findNavController().navigate(GenreDetailFragmentDirections.actionShowAlbum(item.id))
            }
            // All items will launch new detail fragments.
            is Artist -> {
                logD("Navigating to another artist")
                findNavController()
                    .navigate(GenreDetailFragmentDirections.actionShowArtist(item.id))
            }
            is Genre -> {
                navModel.finishExploreNavigation()
            }
            null -> {}
        }
    }

    private fun updateSong(song: Song?) {
        val binding = requireBinding()
        if (playbackModel.parent.value is Genre &&
            playbackModel.parent.value?.id == unlikelyToBeNull(detailModel.currentGenre.value).id) {
            detailAdapter.highlightSong(song, binding.detailRecycler)
        } else {
            // Clear the ViewHolders if the mode isn't ALL_SONGS
            detailAdapter.highlightSong(null, binding.detailRecycler)
        }
    }
}

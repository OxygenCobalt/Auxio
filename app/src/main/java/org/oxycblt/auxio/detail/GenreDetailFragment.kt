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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.recycler.GenreDetailAdapter
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * The [DetailFragment] for a genre.
 * @author OxygenCobalt
 */
class GenreDetailFragment : DetailFragment() {
    private val args: GenreDetailFragmentArgs by navArgs()

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        detailModel.setGenreId(args.genreId)

        val detailAdapter =
            GenreDetailAdapter(
                playbackModel,
                doOnClick = { song -> playbackModel.playSong(song, PlaybackMode.IN_GENRE) },
                doOnLongClick = { view, data -> newMenu(view, data, ActionMenu.FLAG_IN_GENRE) })

        setupToolbar(detailModel.currentGenre.value!!)
        setupRecycler(detailAdapter) { pos ->
            val item = detailAdapter.currentList[pos]
            item is Header || item is ActionHeader || item is Genre
        }

        // --- VIEWMODEL SETUP ---

        detailModel.genreData.observe(viewLifecycleOwner, detailAdapter::submitList)

        detailModel.navToItem.observe(viewLifecycleOwner, ::handleNavigation)

        playbackModel.song.observe(viewLifecycleOwner) { song -> updateSong(song, detailAdapter) }

        detailModel.showMenu.observe(viewLifecycleOwner) { config ->
            if (config != null) {
                showMenu(config)
            }
        }
    }

    private fun handleNavigation(item: Music?) {
        when (item) {
            // All items will launch new detail fragments.
            is Artist -> {
                logD("Navigating to another artist")
                findNavController()
                    .navigate(GenreDetailFragmentDirections.actionShowArtist(item.id))
            }
            is Album -> {
                logD("Navigating to another album")
                findNavController().navigate(GenreDetailFragmentDirections.actionShowAlbum(item.id))
            }
            is Song -> {
                logD("Navigating to another song")
                findNavController()
                    .navigate(GenreDetailFragmentDirections.actionShowAlbum(item.album.id))
            }
            null -> {}
            else -> logW("Unsupported navigation command ${item::class.java}")
        }
    }

    private fun updateSong(song: Song?, adapter: GenreDetailAdapter) {
        val binding = requireBinding()
        if (playbackModel.playbackMode.value == PlaybackMode.IN_GENRE &&
            playbackModel.parent.value?.id == detailModel.currentGenre.value!!.id) {
            adapter.highlightSong(song, binding.detailRecycler)
        } else {
            // Clear the ViewHolders if the mode isn't ALL_SONGS
            adapter.highlightSong(null, binding.detailRecycler)
        }
    }
}

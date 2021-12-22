/*
 * Copyright (c) 2021 Auxio Project
 * GenreDetailFragment.kt is part of Auxio.
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.detail.recycler.GenreDetailAdapter
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.logD

/**
 * The [DetailFragment] for a genre.
 * @author OxygenCobalt
 */
class GenreDetailFragment : DetailFragment() {
    private val args: GenreDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailModel.setGenre(args.genreId)

        val detailAdapter = GenreDetailAdapter(
            playbackModel,
            doOnClick = { song ->
                playbackModel.playSong(song, PlaybackMode.IN_GENRE)
            },
            doOnLongClick = { view, data ->
                newMenu(view, data, ActionMenu.FLAG_IN_GENRE)
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        setupToolbar(detailModel.curGenre.value!!)
        setupRecycler(detailAdapter) { pos ->
            val item = detailAdapter.currentList[pos]
            item is Header || item is ActionHeader || item is Genre
        }

        // --- DETAILVIEWMODEL SETUP ---

        detailModel.genreData.observe(viewLifecycleOwner) { data ->
            detailAdapter.submitList(data)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            when (item) {
                // All items will launch new detail fragments.
                is Artist -> findNavController().navigate(
                    GenreDetailFragmentDirections.actionShowArtist(item.id)
                )

                is Album -> findNavController().navigate(
                    GenreDetailFragmentDirections.actionShowAlbum(item.id)
                )

                is Song -> findNavController().navigate(
                    GenreDetailFragmentDirections.actionShowAlbum(item.album.id)
                )

                else -> {
                }
            }
        }

        // --- PLAYBACKVIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (playbackModel.playbackMode.value == PlaybackMode.IN_GENRE &&
                playbackModel.parent.value?.id == detailModel.curGenre.value!!.id
            ) {
                detailAdapter.highlightSong(song, binding.detailRecycler)
            } else {
                // Clear the viewholders if the mode isn't ALL_SONGS
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        detailModel.showMenu.observe(viewLifecycleOwner) { config ->
            if (config != null) {
                showMenu(config)
            }
        }

        logD("Fragment created.")

        return binding.root
    }
}

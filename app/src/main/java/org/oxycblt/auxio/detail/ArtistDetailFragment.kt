/*
 * Copyright (c) 2021 Auxio Project
 * ArtistDetailFragment.kt is part of Auxio.
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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.recycler.ArtistDetailAdapter
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.logD

/**
 * The [DetailFragment] for an artist.
 * @author OxygenCobalt
 */
class ArtistDetailFragment : DetailFragment() {
    private val args: ArtistDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailModel.setArtist(args.artistId)

        val detailAdapter = ArtistDetailAdapter(
            playbackModel,
            doOnClick = { data ->
                if (!detailModel.isNavigating) {
                    detailModel.setNavigating(true)

                    findNavController().navigate(
                        ArtistDetailFragmentDirections.actionShowAlbum(data.id)
                    )
                }
            },
            doOnSongClick = { data ->
                playbackModel.playSong(data, PlaybackMode.IN_ARTIST)
            },
            doOnLongClick = { view, data ->
                newMenu(view, data, ActionMenu.FLAG_IN_ARTIST)
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        setupToolbar(detailModel.curArtist.value!!)
        setupRecycler(detailAdapter) { pos ->
            // If the item is an ActionHeader we need to also make the item full-width
            val item = detailAdapter.currentList[pos]
            item is Header || item is ActionHeader || item is Artist
        }

        // --- VIEWMODEL SETUP ---

        detailModel.artistData.observe(viewLifecycleOwner) { data ->
            detailAdapter.submitList(data)
        }

        detailModel.showMenu.observe(viewLifecycleOwner) { config ->
            if (config != null) {
                showMenu(config) { id ->
                    id != R.id.option_sort_artist
                }
            }
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            when (item) {
                is Artist -> {
                    if (item.id == detailModel.curArtist.value?.id) {
                        binding.detailRecycler.scrollToPosition(0)
                        detailModel.finishNavToItem()
                    } else {
                        findNavController().navigate(
                            ArtistDetailFragmentDirections.actionShowArtist(item.id)
                        )
                    }
                }

                is Album -> findNavController().navigate(
                    ArtistDetailFragmentDirections.actionShowAlbum(item.id)
                )

                is Song -> findNavController().navigate(
                    ArtistDetailFragmentDirections.actionShowAlbum(item.album.id)
                )

                else -> {
                }
            }
        }

        // Highlight albums if they are being played
        playbackModel.parent.observe(viewLifecycleOwner) { parent ->
            if (parent is Album?) {
                detailAdapter.highlightAlbum(parent, binding.detailRecycler)
            } else {
                detailAdapter.highlightAlbum(null, binding.detailRecycler)
            }
        }

        // Highlight songs if they are being played
        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (playbackModel.playbackMode.value == PlaybackMode.IN_ARTIST &&
                playbackModel.parent.value?.id == detailModel.curArtist.value?.id
            ) {
                detailAdapter.highlightSong(song, binding.detailRecycler)
            } else {
                // Clear the viewholders if the mode isn't ALL_SONGS
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        logD("Fragment created.")

        return binding.root
    }
}

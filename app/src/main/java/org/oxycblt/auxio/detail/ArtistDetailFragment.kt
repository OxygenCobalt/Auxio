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
import org.oxycblt.auxio.detail.adapters.ArtistDetailAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.newMenu

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
        // If DetailViewModel isn't already storing the artist, get it from MusicStore
        // using the ID given by the navigation arguments
        if (detailModel.currentArtist.value == null ||
            detailModel.currentArtist.value?.id != args.artistId
        ) {
            detailModel.updateArtist(
                MusicStore.getInstance().artists.find {
                    it.id == args.artistId
                }!!
            )
        }

        val detailAdapter = ArtistDetailAdapter(
            playbackModel, detailModel,
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

        setupToolbar()
        setupRecycler(detailAdapter) { pos ->
            // If the item is an ActionHeader we need to also make the item full-width
            pos == 0 || detailAdapter.currentList.getOrNull(pos) is Header
        }

        // --- VIEWMODEL SETUP ---

        detailModel.artistSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            val artist = detailModel.currentArtist.value!!

            val data = mutableListOf<BaseModel>(artist)

            data.addAll(SortMode.NUMERIC_DOWN.getSortedAlbumList(artist.albums))

            data.add(
                Header(
                    id = -2,
                    name = getString(R.string.lbl_songs),
                    isAction = true
                )
            )

            data.addAll(mode.getSortedArtistSongList(artist.songs))

            detailAdapter.submitList(data)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            when (item) {
                is Artist -> {
                    if (item.id == detailModel.currentArtist.value!!.id) {
                        binding.detailRecycler.scrollToPosition(0)
                        detailModel.doneWithNavToItem()
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

                else -> {}
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
            if (playbackModel.mode.value == PlaybackMode.IN_ARTIST &&
                playbackModel.parent.value?.id == detailModel.currentArtist.value!!.id
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

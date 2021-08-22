/*
 * Copyright (c) 2021 Auxio Project
 * HomeListFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.spans
import org.oxycblt.auxio.ui.newMenu

/*
 * Fragment that contains a list of items specified by a [DisplayMode].
 */
class HomeListFragment : Fragment() {
    private val homeModel: HomeViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by viewModels()
    private lateinit var displayMode: DisplayMode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeListBinding.inflate(inflater)

        val homeAdapter = HomeAdapter(
            doOnClick = { item ->
                when (item) {
                    is Song -> playbackModel.playSong(item)

                    is Album -> findNavController().navigate(
                        HomeFragmentDirections.actionShowAlbum(item.id)
                    )

                    is Artist -> findNavController().navigate(
                        HomeFragmentDirections.actionShowArtist(item.id)
                    )

                    is Genre -> findNavController().navigate(
                        HomeFragmentDirections.actionShowGenre(item.id)
                    )

                    else -> {
                    }
                }
            },
            ::newMenu
        )

        // --- UI SETUP ---

        binding.homeRecycler.apply {
            adapter = homeAdapter
            setHasFixedSize(true)

            if (spans != 1) {
                layoutManager = GridLayoutManager(requireContext(), spans)
            }
        }

        // --- VIEWMODEL SETUP ---

        val data = when (displayMode) {
            DisplayMode.SHOW_SONGS -> homeModel.songs
            DisplayMode.SHOW_ALBUMS -> homeModel.albums
            DisplayMode.SHOW_ARTISTS -> homeModel.artists
            DisplayMode.SHOW_GENRES -> homeModel.genres
        }

        data.observe(viewLifecycleOwner) { data ->
            homeAdapter.updateData(data)
        }

        return binding.root
    }

    companion object {
        /*
         * Instantiates this fragment for use in a ViewPager.
         */
        fun new(mode: DisplayMode): HomeListFragment {
            val fragment = HomeListFragment()
            fragment.displayMode = mode
            return fragment
        }
    }
}

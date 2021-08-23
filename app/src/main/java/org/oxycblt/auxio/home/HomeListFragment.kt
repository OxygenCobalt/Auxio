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
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.recycler.sliceArticle
import org.oxycblt.auxio.spans
import org.oxycblt.auxio.ui.newMenu

/**
 * Fragment that contains a list of items specified by a [DisplayMode]. This fragment
 * should be created using the [new] method with it's position in the ViewPager.
 */
class HomeListFragment : Fragment() {
    private val homeModel: HomeViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by viewModels()

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

        val pos = requireNotNull(arguments).getInt(ARG_POS)

        val toObserve = when (requireNotNull(homeModel.tabs.value)[pos]) {
            DisplayMode.SHOW_SONGS -> homeModel.songs
            DisplayMode.SHOW_ALBUMS -> homeModel.albums
            DisplayMode.SHOW_ARTISTS -> homeModel.artists
            DisplayMode.SHOW_GENRES -> homeModel.genres
        }

        // Make sure that this RecyclerView has data before startup
        homeAdapter.updateData(toObserve.value!!)

        toObserve.observe(viewLifecycleOwner) { data ->
            homeAdapter.updateData(
                data.sortedWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) {
                        it.name.sliceArticle()
                    }
                )
            )
        }

        logD("Fragment created")

        return binding.root
    }

    companion object {
        private const val ARG_POS = BuildConfig.APPLICATION_ID + ".key.POS"

        /*
         * Instantiates this fragment for use in a ViewPager.
         */
        fun new(pos: Int): HomeListFragment {
            val fragment = HomeListFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_POS, pos)
            }
            return fragment
        }
    }
}

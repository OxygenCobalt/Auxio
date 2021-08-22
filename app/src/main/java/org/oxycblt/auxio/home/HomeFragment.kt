/*
 * Copyright (c) 2021 Auxio Project
 * MainFragment.kt is part of Auxio.
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeBinding
import org.oxycblt.auxio.home.pager.AlbumListFragment
import org.oxycblt.auxio.home.pager.ArtistListFragment
import org.oxycblt.auxio.home.pager.GenreListFragment
import org.oxycblt.auxio.home.pager.SongListFragment
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.playback.PlaybackViewModel
import java.lang.Exception

/**
 * The main "Launching Point" fragment of Auxio, allowing navigation to the detail
 * views for each respective fragment.
 * TODO: Re-add sorting (but new and improved)
 * TODO: Fix issue where elevation will act wrong when switching tabs
 * @author OxygenCobalt
 */
class HomeFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        binding.homeToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    parentFragment?.parentFragment?.findNavController()?.navigate(
                        MainFragmentDirections.actionShowSettings()
                    )
                }

                R.id.action_search -> {
                    findNavController().navigate(HomeFragmentDirections.actionShowSearch())
                }
            }

            true
        }

        binding.homePager.apply {
            adapter = HomePagerAdapter()

            // By default, ViewPager2's sensitivity is high enough to result in vertical
            // scroll events being registered as horizontal scroll events. Reflect into the
            // internal recyclerview and  change the touch slope so that touch actions will
            // act more as a scroll than as a swipe.
            // Derived from: https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414

            try {
                val recycler = ViewPager2::class.java.getDeclaredField("mRecyclerView").apply {
                    isAccessible = true
                    get(binding.homePager)
                }

                RecyclerView::class.java.getDeclaredField("mTouchSlop").apply {
                    isAccessible = true

                    val slop = get(recycler) as Int
                    set(recycler, slop * 8)
                }
            } catch (e: Exception) {
                logE("Unable to reduce ViewPager sensitivity")
                logE(e.stackTraceToString())
            }
        }

        TabLayoutMediator(binding.homeTabs, binding.homePager) { tab, pos ->
            val labelRes = when (pos) {
                0 -> R.string.lbl_songs
                1 -> R.string.lbl_albums
                2 -> R.string.lbl_artists
                3 -> R.string.lbl_genres
                else -> error("Unreachable")
            }

            tab.setText(labelRes)
        }.attach()

        // --- VIEWMODEL SETUP ---

        playbackModel.setupPlayback(requireContext())

        logD("Fragment Created.")

        return binding.root
    }

    private inner class HomePagerAdapter :
        FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SongListFragment()
                1 -> AlbumListFragment()
                2 -> ArtistListFragment()
                3 -> GenreListFragment()
                else -> error("Unreachable")
            }
        }

        override fun getItemCount(): Int = 4
    }
}

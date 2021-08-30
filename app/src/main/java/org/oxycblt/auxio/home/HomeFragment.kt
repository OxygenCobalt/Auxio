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
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.util.applyEdge
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

/**
 * The main "Launching Point" fragment of Auxio, allowing navigation to the detail
 * views for each respective fragment.
 * TODO: Re-add sorting (but new and improved)
 *  It will require a new SortMode to be made simply for compat. Migrate the old SortMode
 *  eventually.
 * TODO: Add lift-on-scroll eventually [when I can file a bug report or hack it into working]
 * @author OxygenCobalt
 */
class HomeFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val homeModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        binding.applyEdge { bars ->
            binding.homeAppbar.updatePadding(top = bars.top)
        }

        // There is basically no way to prevent the toolbar to draw under the status bar when
        // it collapses, so do the next best thing and fade it out so it doesn't stick out like
        // a sore thumb. As a side effect, this looks really cool.
        binding.homeAppbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.homeToolbar.apply {
                    alpha = (height + verticalOffset) / height.toFloat()
                }
            }
        )

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
            // internal recyclerview and change the touch slope so that touch actions will
            // act more as a scroll than as a swipe.
            // Derived from: https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
            try {
                val recycler = ViewPager2::class.java.getDeclaredField("mRecyclerView").run {
                    isAccessible = true
                    get(binding.homePager)
                }

                RecyclerView::class.java.getDeclaredField("mTouchSlop").apply {
                    isAccessible = true

                    val slop = get(recycler) as Int
                    set(recycler, slop * 3) // 3x seems to be the best fit here
                }
            } catch (e: Exception) {
                logE("Unable to reduce ViewPager sensitivity")
                logE(e.stackTraceToString())
            }
        }

        TabLayoutMediator(binding.homeTabs, binding.homePager) { tab, pos ->
            val labelRes = when (requireNotNull(homeModel.tabs.value)[pos]) {
                DisplayMode.SHOW_SONGS -> R.string.lbl_songs
                DisplayMode.SHOW_ALBUMS -> R.string.lbl_albums
                DisplayMode.SHOW_ARTISTS -> R.string.lbl_artists
                DisplayMode.SHOW_GENRES -> R.string.lbl_genres
            }

            tab.setText(labelRes)
        }.attach()

        // --- VIEWMODEL SETUP ---

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            // The AppBarLayout bugs out and collapses when we navigate too fast, wait for it
            // to draw before we continue.
            binding.homeAppbar.post {
                when (item) {
                    is Song -> findNavController().navigate(
                        HomeFragmentDirections.actionShowAlbum(item.album.id)
                    )

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
            }
        }

        logD("Fragment Created.")

        return binding.root
    }

    private inner class HomePagerAdapter :
        FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = requireNotNull(homeModel.tabs.value).size
        override fun createFragment(position: Int): Fragment = HomeListFragment.new(position)
    }
}

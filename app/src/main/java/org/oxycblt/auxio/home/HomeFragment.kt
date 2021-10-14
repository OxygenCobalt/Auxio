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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.iterator
import androidx.core.view.updatePadding
import androidx.core.view.updatePaddingRelative
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
import org.oxycblt.auxio.home.list.AlbumListFragment
import org.oxycblt.auxio.home.list.ArtistListFragment
import org.oxycblt.auxio.home.list.GenreListFragment
import org.oxycblt.auxio.home.list.SongListFragment
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.SortMode
import org.oxycblt.auxio.ui.memberBinding
import org.oxycblt.auxio.util.applyEdge
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

/**
 * The main "Launching Point" fragment of Auxio, allowing navigation to the detail
 * views for each respective fragment.
 * FIXME: More UI glitches:
 *        - AppBar will just...expand. For no reason. If you navigate away while it's partially
 *          collapsed. No, I don't know why. Guess I have to save the state myself.
 *        - Edge-to-edge is borked still, unsure how to really fix this aside from making some
 *          magic layout like Material Files, but even then it might not work since the scrolling
 *          views are not laid side-by-side to the layout itself.
 * @author OxygenCobalt
 */
class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by memberBinding(FragmentHomeBinding::inflate)
    private val detailModel: DetailViewModel by activityViewModels()
    private val homeModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sortItem: MenuItem

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        binding.applyEdge { bars ->
            binding.homeAppbar.updatePadding(top = bars.top)
        }

        binding.homeAppbar.apply {
            addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                    binding.homeToolbar.alpha = (binding.homeToolbar.height + verticalOffset) /
                        binding.homeToolbar.height.toFloat()
                }
            )

            post {
                // To add our fast scroller, we need to
                val vOffset = (
                    (layoutParams as CoordinatorLayout.LayoutParams)
                        .behavior as AppBarLayout.Behavior
                    ).topAndBottomOffset

                binding.homePager.updatePaddingRelative(
                    bottom = binding.homeAppbar.totalScrollRange + vOffset
                )

                binding.homeAppbar.addOnOffsetChangedListener(
                    AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                        binding.homePager.updatePaddingRelative(
                            bottom = binding.homeAppbar.totalScrollRange + verticalOffset
                        )
                    }
                )
            }
        }

        binding.homeToolbar.apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_search -> {
                        findNavController().navigate(HomeFragmentDirections.actionShowSearch())
                    }

                    R.id.action_settings -> {
                        parentFragment?.parentFragment?.findNavController()?.navigate(
                            MainFragmentDirections.actionShowSettings()
                        )
                    }

                    R.id.action_about -> {
                        parentFragment?.parentFragment?.findNavController()?.navigate(
                            MainFragmentDirections.actionShowAbout()
                        )
                    }

                    R.id.submenu_sorting -> { }

                    // Sorting option was selected, check then and update the mode
                    else -> {
                        item.isChecked = true

                        homeModel.updateCurrentSort(
                            requireNotNull(SortMode.fromId(item.itemId))
                        )
                    }
                }

                true
            }

            sortItem = menu.findItem(R.id.submenu_sorting)
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

            // We know that there will only be a fixed amount of tabs, so we manually set this
            // limit to that. This also prevents the appbar lift state from being confused during
            // page transitions.
            offscreenPageLimit = homeModel.tabs.value!!.size

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) = homeModel.updateCurrentTab(position)
            })
        }

        TabLayoutMediator(binding.homeTabs, binding.homePager) { tab, pos ->
            val labelRes = when (homeModel.tabs.value!![pos]) {
                DisplayMode.SHOW_SONGS -> R.string.lbl_songs
                DisplayMode.SHOW_ALBUMS -> R.string.lbl_albums
                DisplayMode.SHOW_ARTISTS -> R.string.lbl_artists
                DisplayMode.SHOW_GENRES -> R.string.lbl_genres
            }

            tab.setText(labelRes)
        }.attach()

        // --- VIEWMODEL SETUP ---

        homeModel.curTab.observe(viewLifecycleOwner) { tab ->
            binding.homeAppbar.liftOnScrollTargetViewId = when (requireNotNull(tab)) {
                DisplayMode.SHOW_SONGS -> {
                    updateSortMenu(sortItem, tab)
                    R.id.home_song_list
                }

                DisplayMode.SHOW_ALBUMS -> {
                    updateSortMenu(sortItem, tab) { id -> id != R.id.option_sort_album }
                    R.id.home_album_list
                }

                DisplayMode.SHOW_ARTISTS -> {
                    updateSortMenu(sortItem, tab) { id ->
                        id == R.id.option_sort_asc || id == R.id.option_sort_dsc
                    }

                    R.id.home_artist_list
                }

                DisplayMode.SHOW_GENRES -> {
                    updateSortMenu(sortItem, tab) { id ->
                        id == R.id.option_sort_asc || id == R.id.option_sort_dsc
                    }

                    R.id.home_genre_list
                }
            }
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            // The AppBarLayout gets confused and collapses when we navigate too fast, wait for it
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

    private fun updateSortMenu(
        item: MenuItem,
        displayMode: DisplayMode,
        isVisible: (Int) -> Boolean = { true }
    ) {
        val toHighlight = homeModel.getSortForDisplay(displayMode)

        for (option in item.subMenu) {
            if (option.itemId == toHighlight.itemId) {
                option.isChecked = true
            }

            option.isVisible = isVisible(option.itemId)
        }
    }

    private inner class HomePagerAdapter :
        FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = homeModel.tabs.value!!.size

        override fun createFragment(position: Int): Fragment {
            return when (homeModel.tabs.value!![position]) {
                DisplayMode.SHOW_SONGS -> SongListFragment()
                DisplayMode.SHOW_ALBUMS -> AlbumListFragment()
                DisplayMode.SHOW_ARTISTS -> ArtistListFragment()
                DisplayMode.SHOW_GENRES -> GenreListFragment()
            }
        }
    }
}

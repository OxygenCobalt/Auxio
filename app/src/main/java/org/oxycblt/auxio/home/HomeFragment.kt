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
 
package org.oxycblt.auxio.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeBinding
import org.oxycblt.auxio.home.list.AlbumListFragment
import org.oxycblt.auxio.home.list.ArtistListFragment
import org.oxycblt.auxio.home.list.GenreListFragment
import org.oxycblt.auxio.home.list.SongListFragment
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logTraceOrThrow
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * The main "Launching Point" fragment of Auxio, allowing navigation to the detail views for each
 * respective item.
 * @author OxygenCobalt
 *
 * TODO: Make tabs invisible when there is only one
 *
 * TODO: Add duration and song count sorts
 */
class HomeFragment : ViewBindingFragment<FragmentHomeBinding>(), Toolbar.OnMenuItemClickListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()
    private val homeModel: HomeViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentHomeBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        val sortItem: MenuItem

        binding.homeToolbar.apply {
            sortItem = menu.findItem(R.id.submenu_sorting)
            setOnMenuItemClickListener(this@HomeFragment)
        }

        binding.homePager.apply {
            adapter = HomePagerAdapter()
            // We know that there will only be a fixed amount of tabs, so we manually set this
            // limit to that. This also prevents the appbar lift state from being confused during
            // page transitions.
            offscreenPageLimit = homeModel.tabs.size

            reduceSensitivity(3)

            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) =
                        homeModel.updateCurrentTab(position)
                })

            TabLayoutMediator(binding.homeTabs, this, AdaptiveTabStrategy(context, homeModel))
                .attach()
        }

        binding.homeFab.setOnClickListener { playbackModel.shuffleAll() }

        // --- VIEWMODEL SETUP ---

        homeModel.isFastScrolling.observe(viewLifecycleOwner, ::updateFastScrolling)
        homeModel.currentTab.observe(viewLifecycleOwner) { tab -> updateCurrentTab(sortItem, tab) }
        homeModel.recreateTabs.observe(viewLifecycleOwner, ::handleRecreateTabs)

        musicModel.loaderResponse.observe(viewLifecycleOwner, ::handleLoaderResponse)
        navModel.exploreNavigationItem.observe(viewLifecycleOwner, ::handleNavigation)
    }

    override fun onDestroyBinding(binding: FragmentHomeBinding) {
        super.onDestroyBinding(binding)
        binding.homeToolbar.setOnMenuItemClickListener(null)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                logD("Navigating to search")
                findNavController().navigate(HomeFragmentDirections.actionShowSearch())
            }
            R.id.action_settings -> {
                logD("Navigating to settings")
                navModel.mainNavigateTo(MainNavigationAction.SETTINGS)
            }
            R.id.action_about -> {
                logD("Navigating to about")
                navModel.mainNavigateTo(MainNavigationAction.ABOUT)
            }
            R.id.submenu_sorting -> {
                // Junk click event when opening the menu
            }
            R.id.option_sort_asc -> {
                item.isChecked = !item.isChecked
                homeModel.updateCurrentSort(
                    unlikelyToBeNull(
                        homeModel
                            .getSortForDisplay(unlikelyToBeNull(homeModel.currentTab.value))
                            .ascending(item.isChecked)))
            }

            // Sorting option was selected, mark it as selected and update the mode
            else -> {
                item.isChecked = true
                homeModel.updateCurrentSort(
                    unlikelyToBeNull(
                        homeModel
                            .getSortForDisplay(unlikelyToBeNull(homeModel.currentTab.value))
                            .assignId(item.itemId)))
            }
        }

        return true
    }

    private fun updateFastScrolling(isFastScrolling: Boolean) {
        val binding = requireBinding()

        // Make sure an update here doesn't mess up the FAB state when it comes to the
        // loader response.
        if (musicModel.loaderResponse.value !is MusicStore.Response.Ok) {
            return
        }

        if (isFastScrolling) {
            binding.homeFab.hide()
        } else {
            binding.homeFab.show()
        }
    }

    private fun updateCurrentTab(sortItem: MenuItem, tab: DisplayMode) {
        // Make sure that we update the scrolling view and allowed menu items whenever
        // the tab changes.
        val binding = requireBinding()
        when (tab) {
            DisplayMode.SHOW_SONGS -> {
                updateSortMenu(sortItem, tab)
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_song_list
            }
            DisplayMode.SHOW_ALBUMS -> {
                updateSortMenu(sortItem, tab) { id -> id != R.id.option_sort_album }
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_album_list
            }
            DisplayMode.SHOW_ARTISTS -> {
                updateSortMenu(sortItem, tab) { id -> id == R.id.option_sort_asc }
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_artist_list
            }
            DisplayMode.SHOW_GENRES -> {
                updateSortMenu(sortItem, tab) { id -> id == R.id.option_sort_asc }
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_genre_list
            }
        }
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

            if (option.itemId == R.id.option_sort_asc) {
                option.isChecked = toHighlight.isAscending
            }

            option.isVisible = isVisible(option.itemId)
        }
    }

    private fun handleRecreateTabs(recreate: Boolean) {
        if (recreate) {
            requireBinding().homePager.recreate()
            homeModel.finishRecreateTabs()
        }
    }

    private fun handleLoaderResponse(response: MusicStore.Response?) {
        val binding = requireBinding()
        when (response) {
            is MusicStore.Response.Ok -> binding.homeFab.show()

            // While loading or during an error, make sure we keep the shuffle fab hidden so
            // that any kind of playback is impossible. PlaybackStateManager also relies on this
            // invariant, so please don't change it.
            else -> binding.homeFab.hide()
        }
    }

    private fun handleNavigation(item: Music?) {
        // Note: You will want to add a post call to this if you want to re-introduce a collapsing
        // toolbar.
        when (item) {
            is Song ->
                findNavController().navigate(HomeFragmentDirections.actionShowAlbum(item.album.id))
            is Album ->
                findNavController().navigate(HomeFragmentDirections.actionShowAlbum(item.id))
            is Artist ->
                findNavController().navigate(HomeFragmentDirections.actionShowArtist(item.id))
            is Genre ->
                findNavController().navigate(HomeFragmentDirections.actionShowGenre(item.id))
            else -> {}
        }
    }

    /**
     * By default, ViewPager2's sensitivity is high enough to result in vertical scroll events being
     * registered as horizontal scroll events. Reflect into the internal recyclerview and change the
     * touch slope so that touch actions will act more as a scroll than as a swipe. Derived from:
     * https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
     */
    private fun ViewPager2.reduceSensitivity(by: Int) {
        try {
            val recycler =
                ViewPager2::class.java.getDeclaredField("mRecyclerView").run {
                    isAccessible = true
                    get(this@reduceSensitivity)
                }

            RecyclerView::class.java.getDeclaredField("mTouchSlop").apply {
                isAccessible = true

                val slop = get(recycler) as Int
                set(recycler, slop * by)
            }
        } catch (e: Exception) {
            logE("Unable to reduce ViewPager sensitivity (likely an internal code change)")
            e.logTraceOrThrow()
        }
    }

    /** Forces the view to recreate all fragments contained within it. */
    private fun ViewPager2.recreate() {
        currentItem = 0
        adapter = HomePagerAdapter()
    }

    private inner class HomePagerAdapter :
        FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = homeModel.tabs.size

        override fun createFragment(position: Int): Fragment {
            return when (homeModel.tabs[position]) {
                DisplayMode.SHOW_SONGS -> SongListFragment()
                DisplayMode.SHOW_ALBUMS -> AlbumListFragment()
                DisplayMode.SHOW_ARTISTS -> ArtistListFragment()
                DisplayMode.SHOW_GENRES -> GenreListFragment()
            }
        }
    }
}

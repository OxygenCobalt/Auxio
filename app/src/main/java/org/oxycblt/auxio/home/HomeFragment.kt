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

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeBinding
import org.oxycblt.auxio.home.list.AlbumListFragment
import org.oxycblt.auxio.home.list.ArtistListFragment
import org.oxycblt.auxio.home.list.GenreListFragment
import org.oxycblt.auxio.home.list.SongListFragment
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Indexer
import org.oxycblt.auxio.music.IndexerViewModel
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.getColorStateListSafe
import org.oxycblt.auxio.util.getSystemBarInsetsCompat
import org.oxycblt.auxio.util.launch
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logTraceOrThrow
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.textSafe
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * The main "Launching Point" fragment of Auxio, allowing navigation to the detail views for each
 * respective item.
 * @author OxygenCobalt
 */
class HomeFragment : ViewBindingFragment<FragmentHomeBinding>(), Toolbar.OnMenuItemClickListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()
    private val homeModel: HomeViewModel by activityViewModels()
    private val indexerModel: IndexerViewModel by activityViewModels()

    private var storagePermissionLauncher: ActivityResultLauncher<String>? = null
    private var sortItem: MenuItem? = null

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentHomeBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        // Build the permission launcher here as you can only do it in onCreateView/onCreate
        storagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                indexerModel.reindex()
            }

        binding.homeAppbar.apply {
            addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { _, offset ->
                    val range = binding.homeAppbar.totalScrollRange

                    binding.homeToolbar.alpha = 1f - (abs(offset.toFloat()) / (range.toFloat() / 2))

                    binding.homeContent.updatePadding(
                        bottom = binding.homeAppbar.totalScrollRange + offset)
                })
        }

        binding.homeToolbar.apply {
            sortItem = menu.findItem(R.id.submenu_sorting)
            setOnMenuItemClickListener(this@HomeFragment)
        }

        updateTabConfiguration()

        binding.homeIndexingContainer.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(bottom = insets.getSystemBarInsetsCompat(view).bottom)
            insets
        }

        // Load the track color in manually as it's unclear whether the track actually supports
        // using a ColorStateList in the resources
        binding.homeIndexingProgress.trackColor =
            requireContext().getColorStateListSafe(R.color.sel_track).defaultColor

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

        launch { homeModel.isFastScrolling.collect(::updateFastScrolling) }
        launch { homeModel.currentTab.collect(::updateCurrentTab) }
        launch { homeModel.recreateTabs.collect(::handleRecreateTabs) }
        launch { indexerModel.state.collect(::handleIndexerState) }
        launch { navModel.exploreNavigationItem.collect(::handleNavigation) }
    }

    override fun onDestroyBinding(binding: FragmentHomeBinding) {
        super.onDestroyBinding(binding)
        binding.homeToolbar.setOnMenuItemClickListener(null)
        storagePermissionLauncher = null
        sortItem = null
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
            else -> {
                // Sorting option was selected, mark it as selected and update the mode
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
        val state = indexerModel.state.value
        if (!(state is Indexer.State.Complete && state.response is Indexer.Response.Ok)) {
            return
        }

        if (isFastScrolling) {
            binding.homeFab.hide()
        } else {
            binding.homeFab.show()
        }
    }

    private fun updateCurrentTab(tab: DisplayMode) {
        // Make sure that we update the scrolling view and allowed menu items whenever
        // the tab changes.
        val binding = requireBinding()
        when (tab) {
            DisplayMode.SHOW_SONGS -> {
                updateSortMenu(tab) { id -> id != R.id.option_sort_count }
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_song_list
            }
            DisplayMode.SHOW_ALBUMS -> {
                updateSortMenu(tab) { id -> id != R.id.option_sort_album }
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_album_list
            }
            DisplayMode.SHOW_ARTISTS -> {
                updateSortMenu(tab) { id ->
                    id == R.id.option_sort_asc ||
                        id == R.id.option_sort_name ||
                        id == R.id.option_sort_count ||
                        id == R.id.option_sort_duration
                }
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_artist_list
            }
            DisplayMode.SHOW_GENRES -> {
                updateSortMenu(tab) { id ->
                    id == R.id.option_sort_asc ||
                        id == R.id.option_sort_name ||
                        id == R.id.option_sort_count ||
                        id == R.id.option_sort_duration
                }
                binding.homeAppbar.liftOnScrollTargetViewId = R.id.home_genre_list
            }
        }
    }

    private fun updateSortMenu(displayMode: DisplayMode, isVisible: (Int) -> Boolean) {
        val sortItem = requireNotNull(sortItem) { "Cannot update sort menu while detached" }

        val toHighlight = homeModel.getSortForDisplay(displayMode)

        for (option in sortItem.subMenu) {
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
            updateTabConfiguration()
            homeModel.finishRecreateTabs()
        }
    }

    private fun updateTabConfiguration() {
        if (homeModel.tabs.size == 1) {
            requireBinding().homeTabs.isVisible = false
        }
    }

    private fun handleIndexerState(state: Indexer.State?) {
        val binding = requireBinding()

        when (state) {
            is Indexer.State.Complete -> handleIndexerResponse(binding, state.response)
            is Indexer.State.Indexing -> handleIndexingState(binding, state.indexing)
            null -> {
                logW("Indexer is in indeterminate state, doing nothing")
            }
        }
    }

    private fun handleIndexerResponse(binding: FragmentHomeBinding, response: Indexer.Response) {
        if (response is Indexer.Response.Ok) {
            binding.homeFab.show()
            binding.homeIndexingContainer.visibility = View.INVISIBLE
            binding.homePager.visibility = View.VISIBLE
        } else {
            binding.homeFab.hide()
            binding.homePager.visibility = View.INVISIBLE
            binding.homeIndexingContainer.visibility = View.VISIBLE

            logD("Received non-ok response $response")

            when (response) {
                is Indexer.Response.Ok -> error("Unreachable")
                is Indexer.Response.Err -> {
                    binding.homeIndexingProgress.visibility = View.INVISIBLE
                    binding.homeIndexingStatus.textSafe = getString(R.string.err_index_failed)
                    binding.homeIndexingAction.apply {
                        visibility = View.VISIBLE
                        text = getString(R.string.lbl_retry)
                        setOnClickListener { indexerModel.reindex() }
                    }
                }
                is Indexer.Response.NoMusic -> {
                    binding.homeIndexingProgress.visibility = View.INVISIBLE
                    binding.homeIndexingStatus.textSafe = getString(R.string.err_no_music)
                    binding.homeIndexingAction.apply {
                        visibility = View.VISIBLE
                        text = getString(R.string.lbl_retry)
                        setOnClickListener { indexerModel.reindex() }
                    }
                }
                is Indexer.Response.NoPerms -> {
                    val launcher =
                        requireNotNull(storagePermissionLauncher) {
                            "Cannot access permission launcher while detached"
                        }

                    binding.homeIndexingProgress.visibility = View.INVISIBLE
                    binding.homeIndexingStatus.textSafe = getString(R.string.err_no_perms)
                    binding.homeIndexingAction.apply {
                        visibility = View.VISIBLE
                        text = getString(R.string.lbl_grant)
                        setOnClickListener {
                            launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                }
            }
        }
    }

    private fun handleIndexingState(binding: FragmentHomeBinding, indexing: Indexer.Indexing) {
        binding.homeFab.hide()
        binding.homePager.visibility = View.INVISIBLE
        binding.homeIndexingContainer.visibility = View.VISIBLE
        binding.homeIndexingProgress.visibility = View.VISIBLE
        binding.homeIndexingAction.visibility = View.INVISIBLE

        when (indexing) {
            is Indexer.Indexing.Indeterminate -> {
                binding.homeIndexingStatus.textSafe = getString(R.string.lbl_indexing)
                binding.homeIndexingProgress.isIndeterminate = true
            }
            is Indexer.Indexing.Songs -> {
                binding.homeIndexingStatus.textSafe =
                    getString(R.string.fmt_indexing, indexing.current, indexing.total)
                binding.homeIndexingProgress.apply {
                    isIndeterminate = false
                    max = indexing.total
                    progress = indexing.current
                }
            }
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

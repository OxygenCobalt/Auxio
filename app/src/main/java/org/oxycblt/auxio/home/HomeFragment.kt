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
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.transition.MaterialSharedAxis
import java.lang.reflect.Field
import kotlin.math.abs
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeBinding
import org.oxycblt.auxio.home.list.AlbumListFragment
import org.oxycblt.auxio.home.list.ArtistListFragment
import org.oxycblt.auxio.home.list.GenreListFragment
import org.oxycblt.auxio.home.list.SongListFragment
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.selection.SelectionToolbarOverlay
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.music.system.Indexer
import org.oxycblt.auxio.shared.MainNavigationAction
import org.oxycblt.auxio.util.*

/**
 * The main "Launching Point" fragment of Auxio, allowing navigation to the detail views for each
 * respective item.
 * @author OxygenCobalt
 */
class HomeFragment : ListFragment<FragmentHomeBinding>() {
    private val homeModel: HomeViewModel by androidActivityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()

    // lifecycleObject builds this in the creation step, so doing this is okay.
    private val storagePermissionLauncher: ActivityResultLauncher<String> by lifecycleObject {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            musicModel.reindex(true)
        }
    }

    private val sortItem: MenuItem by lifecycleObject { binding ->
        binding.homeToolbar.menu.findItem(R.id.submenu_sorting)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            // Orientation change will wipe whatever transition we were using prior, which will
            // result in no transition when the user navigates back. Make sure we re-initialize
            // our transitions.
            val axis = savedInstanceState.getInt(KEY_LAST_TRANSITION_AXIS, -1)
            if (axis > -1) {
                initAxisTransitions(axis)
            }
        }
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentHomeBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        binding.homeAppbar.addOnOffsetChangedListener { _, it -> handleAppBarAnimation(it) }
        setupSelectionToolbar(binding.homeSelectionToolbar)
        binding.homeToolbar.setOnMenuItemClickListener {
            handleHomeMenuItem(it)
            true
        }

        setupTabs(binding)

        // Load the track color in manually as it's unclear whether the track actually supports
        // using a ColorStateList in the resources
        binding.homeIndexingProgress.trackColor =
            requireContext().getColorCompat(R.color.sel_track).defaultColor

        binding.homePager.apply {
            adapter = HomePagerAdapter()

            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        homeModel.setCurrentTab(position)
                    }
                })

            TabLayoutMediator(binding.homeTabs, this, AdaptiveTabStrategy(context, homeModel))
                .attach()

            // ViewPager2 will nominally consume window insets, which will then break the window
            // insets applied to the indexing view before API 30. Fix this by overriding the
            // callback with a non-consuming listener.
            setOnApplyWindowInsetsListener { _, insets -> insets }

            // We know that there will only be a fixed amount of tabs, so we manually set this
            // limit to that. This also prevents the appbar lift state from being confused during
            // page transitions.
            offscreenPageLimit = homeModel.tabs.size

            // By default, ViewPager2's sensitivity is high enough to result in vertical scroll
            // events being
            // registered as horizontal scroll events. Reflect into the internal recyclerview and
            // change the
            // touch slope so that touch actions will act more as a scroll than as a swipe. Derived
            // from:
            // https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
            val recycler = VP_RECYCLER_FIELD.get(this@apply)
            val slop = RV_TOUCH_SLOP_FIELD.get(recycler) as Int
            RV_TOUCH_SLOP_FIELD.set(recycler, slop * 3)
        }

        binding.homeFab.setOnClickListener { playbackModel.shuffleAll() }

        // --- VIEWMODEL SETUP ---

        collect(homeModel.recreateTabs, ::handleRecreateTabs)
        collectImmediately(homeModel.currentTab, ::updateCurrentTab)
        collectImmediately(homeModel.songs, homeModel.isFastScrolling, ::updateFab)

        collectImmediately(musicModel.indexerState, ::updateIndexerState)

        collect(navModel.exploreNavigationItem, ::handleNavigation)
        collectImmediately(selectionModel.selected, ::updateSelection)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val enter = enterTransition
        if (enter is MaterialSharedAxis) {
            outState.putInt(KEY_LAST_TRANSITION_AXIS, enter.axis)
        }

        super.onSaveInstanceState(outState)
    }

    private fun handleAppBarAnimation(verticalOffset: Int) {
        val binding = requireBinding()
        val range = binding.homeAppbar.totalScrollRange

        binding.homeSelectionToolbar.alpha =
            1f - (abs(verticalOffset.toFloat()) / (range.toFloat() / 2))

        binding.homeContent.updatePadding(
            bottom = binding.homeAppbar.totalScrollRange + verticalOffset)
    }

    private fun handleHomeMenuItem(item: MenuItem) {
        when (item.itemId) {
            R.id.action_search -> {
                logD("Navigating to search")
                initAxisTransitions(MaterialSharedAxis.Z)
                findNavController().navigate(HomeFragmentDirections.actionShowSearch())
            }
            R.id.action_settings -> {
                logD("Navigating to settings")
                navModel.mainNavigateTo(
                    MainNavigationAction.Directions(MainFragmentDirections.actionShowSettings()))
            }
            R.id.action_about -> {
                logD("Navigating to about")
                navModel.mainNavigateTo(
                    MainNavigationAction.Directions(MainFragmentDirections.actionShowAbout()))
            }
            R.id.submenu_sorting -> {
                // Junk click event when opening the menu
            }
            R.id.option_sort_asc -> {
                item.isChecked = !item.isChecked
                homeModel.updateCurrentSort(
                    homeModel
                        .getSortForTab(homeModel.currentTab.value)
                        .withAscending(item.isChecked))
            }
            else -> {
                // Sorting option was selected, mark it as selected and update the mode
                item.isChecked = true
                homeModel.updateCurrentSort(
                    homeModel
                        .getSortForTab(homeModel.currentTab.value)
                        .withMode(requireNotNull(Sort.Mode.fromItemId(item.itemId))))
            }
        }
    }

    private fun updateCurrentTab(tab: MusicMode) {
        // Make sure that we update the scrolling view and allowed menu items whenever
        // the tab changes.
        when (tab) {
            MusicMode.SONGS -> {
                updateSortMenu(tab) { id -> id != R.id.option_sort_count }
            }
            MusicMode.ALBUMS -> {
                updateSortMenu(tab) { id -> id != R.id.option_sort_album }
            }
            MusicMode.ARTISTS -> {
                updateSortMenu(tab) { id ->
                    id == R.id.option_sort_asc ||
                        id == R.id.option_sort_name ||
                        id == R.id.option_sort_count ||
                        id == R.id.option_sort_duration
                }
            }
            MusicMode.GENRES -> {
                updateSortMenu(tab) { id ->
                    id == R.id.option_sort_asc ||
                        id == R.id.option_sort_name ||
                        id == R.id.option_sort_count ||
                        id == R.id.option_sort_duration
                }
            }
        }

        requireBinding().homeAppbar.liftOnScrollTargetViewId = getTabRecyclerId(tab)
    }

    private fun updateSortMenu(mode: MusicMode, isVisible: (Int) -> Boolean) {
        val sortMenu = requireNotNull(sortItem.subMenu)
        val toHighlight = homeModel.getSortForTab(mode)

        for (option in sortMenu) {
            if (option.itemId == toHighlight.mode.itemId) {
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
            val binding = requireBinding()

            binding.homePager.apply {
                currentItem = 0
                adapter = HomePagerAdapter()
            }

            setupTabs(binding)

            homeModel.finishRecreateTabs()
        }
    }

    private fun updateIndexerState(state: Indexer.State?) {
        val binding = requireBinding()
        when (state) {
            is Indexer.State.Complete -> setupCompleteState(binding, state.response)
            is Indexer.State.Indexing -> setupIndexingState(binding, state.indexing)
            null -> {
                logD("Indexer is in indeterminate state")
                binding.homeIndexingContainer.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupCompleteState(binding: FragmentHomeBinding, response: Indexer.Response) {
        if (response is Indexer.Response.Ok) {
            binding.homeFab.show()
            binding.homeIndexingContainer.visibility = View.INVISIBLE
        } else {
            val context = requireContext()

            binding.homeIndexingContainer.visibility = View.VISIBLE

            logD("Received non-ok response $response")

            when (response) {
                is Indexer.Response.Err -> {
                    binding.homeIndexingProgress.visibility = View.INVISIBLE
                    binding.homeIndexingStatus.text = context.getString(R.string.err_index_failed)
                    binding.homeIndexingAction.apply {
                        visibility = View.VISIBLE
                        text = context.getString(R.string.lbl_retry)
                        setOnClickListener { musicModel.reindex(true) }
                    }
                }
                is Indexer.Response.NoMusic -> {
                    binding.homeIndexingProgress.visibility = View.INVISIBLE
                    binding.homeIndexingStatus.text = context.getString(R.string.err_no_music)
                    binding.homeIndexingAction.apply {
                        visibility = View.VISIBLE
                        text = context.getString(R.string.lbl_retry)
                        setOnClickListener { musicModel.reindex(true) }
                    }
                }
                is Indexer.Response.NoPerms -> {
                    binding.homeIndexingProgress.visibility = View.INVISIBLE
                    binding.homeIndexingStatus.text = context.getString(R.string.err_no_perms)
                    binding.homeIndexingAction.apply {
                        visibility = View.VISIBLE
                        text = context.getString(R.string.lbl_grant)
                        setOnClickListener {
                            storagePermissionLauncher.launch(Indexer.PERMISSION_READ_AUDIO)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun setupIndexingState(binding: FragmentHomeBinding, indexing: Indexer.Indexing) {
        binding.homeIndexingContainer.visibility = View.VISIBLE
        binding.homeIndexingProgress.visibility = View.VISIBLE
        binding.homeIndexingAction.visibility = View.INVISIBLE

        val context = requireContext()

        when (indexing) {
            is Indexer.Indexing.Indeterminate -> {
                binding.homeIndexingStatus.text = context.getString(R.string.lng_indexing)
                binding.homeIndexingProgress.isIndeterminate = true
            }
            is Indexer.Indexing.Songs -> {
                binding.homeIndexingStatus.text =
                    context.getString(R.string.fmt_indexing, indexing.current, indexing.total)
                binding.homeIndexingProgress.apply {
                    isIndeterminate = false
                    max = indexing.total
                    progress = indexing.current
                }
            }
        }
    }

    private fun updateFab(songs: List<Song>, isFastScrolling: Boolean) {
        val binding = requireBinding()
        if (songs.isEmpty() || isFastScrolling) {
            binding.homeFab.hide()
        } else {
            binding.homeFab.show()
        }
    }

    private fun handleNavigation(item: Music?) {
        val action =
            when (item) {
                is Song -> HomeFragmentDirections.actionShowAlbum(item.album.uid)
                is Album -> HomeFragmentDirections.actionShowAlbum(item.uid)
                is Artist -> HomeFragmentDirections.actionShowArtist(item.uid.also { logD(it) })
                is Genre -> HomeFragmentDirections.actionShowGenre(item.uid)
                else -> return
            }

        initAxisTransitions(MaterialSharedAxis.X)
        findNavController().navigate(action)
    }

    private fun updateSelection(selected: List<Music>) {
        val binding = requireBinding()
        if (binding.homeSelectionToolbar.updateSelectionAmount(selected.size) &&
            selected.isNotEmpty()) {
            logD("Significant selection occurred, expanding AppBar")
            // Significant enough change where we want to expand the RecyclerView
            binding.homeAppbar.expandWithRecycler(
                binding.homePager.findViewById(getTabRecyclerId(homeModel.currentTab.value)))
        }
    }

    /** Returns the ID of a RecyclerView that the given [tab] contains */
    private fun getTabRecyclerId(tab: MusicMode) =
        when (tab) {
            MusicMode.SONGS -> R.id.home_song_recycler
            MusicMode.ALBUMS -> R.id.home_album_recycler
            MusicMode.ARTISTS -> R.id.home_artist_recycler
            MusicMode.GENRES -> R.id.home_genre_recycler
        }

    private fun setupTabs(binding: FragmentHomeBinding) {
        val toolbarParams = binding.homeSelectionToolbar.layoutParams as AppBarLayout.LayoutParams
        if (homeModel.tabs.size == 1) {
            // A single tab makes the tab layout redundant, hide it and disable the collapsing
            // behavior.
            binding.homeTabs.isVisible = false
            binding.homeAppbar.setExpanded(true, false)
            toolbarParams.scrollFlags = 0
        } else {
            binding.homeTabs.isVisible = true
            toolbarParams.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        }
    }

    private fun initAxisTransitions(axis: Int) {
        // Sanity check
        check(axis == MaterialSharedAxis.X || axis == MaterialSharedAxis.Z) {
            "Not expecting Y axis transition"
        }
        enterTransition = MaterialSharedAxis(axis, true)
        returnTransition = MaterialSharedAxis(axis, false)
        exitTransition = MaterialSharedAxis(axis, true)
        reenterTransition = MaterialSharedAxis(axis, false)
    }

    private inner class HomePagerAdapter :
        FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = homeModel.tabs.size

        override fun createFragment(position: Int): Fragment {
            return when (homeModel.tabs[position]) {
                MusicMode.SONGS -> SongListFragment()
                MusicMode.ALBUMS -> AlbumListFragment()
                MusicMode.ARTISTS -> ArtistListFragment()
                MusicMode.GENRES -> GenreListFragment()
            }
        }
    }

    companion object {
        private val VP_RECYCLER_FIELD: Field by
            lazyReflectedField(ViewPager2::class, "mRecyclerView")
        private val RV_TOUCH_SLOP_FIELD: Field by
            lazyReflectedField(RecyclerView::class, "mTouchSlop")
        private const val KEY_LAST_TRANSITION_AXIS =
            BuildConfig.APPLICATION_ID + ".key.LAST_TRANSITION_AXIS"
    }
}

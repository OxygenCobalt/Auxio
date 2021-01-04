package org.oxycblt.auxio.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Fade
import androidx.transition.TransitionManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.library.adapters.LibraryAdapter
import org.oxycblt.auxio.library.adapters.SearchAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.applyColor
import org.oxycblt.auxio.ui.getLandscapeSpans
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.requireCompatActivity
import org.oxycblt.auxio.ui.resolveAttr
import org.oxycblt.auxio.ui.toColor

/**
 * A [Fragment] that shows a custom list of [Genre], [Artist], or [Album] data. Also allows for
 * search functionality.
 * FIXME: Heisenleak when navving from search
 * FIXME: Heisenleak on older versions
 */
class LibraryFragment : Fragment(), SearchView.OnQueryTextListener {

    private val libraryModel: LibraryViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLibraryBinding.inflate(inflater)

        val libraryAdapter = LibraryAdapter(::onItemSelection, ::showActionsForItem)
        val searchAdapter = SearchAdapter(::onItemSelection, ::showActionsForItem)

        val sortAction = binding.libraryToolbar.menu.findItem(R.id.submenu_sorting)

        // --- UI SETUP ---

        binding.libraryToolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_search -> {
                        TransitionManager.beginDelayedTransition(
                            binding.libraryToolbar, Fade()
                        )
                        it.expandActionView()
                    }

                    R.id.submenu_sorting -> {
                    }

                    else -> libraryModel.updateSortMode(it.itemId)
                }

                true
            }

            menu.apply {
                val searchAction = findItem(R.id.action_search)
                val searchView = searchAction.actionView as SearchView

                searchView.queryHint = getString(R.string.hint_search_library)
                searchView.maxWidth = Int.MAX_VALUE
                searchView.setOnQueryTextListener(this@LibraryFragment)

                searchAction.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        binding.libraryRecycler.adapter = searchAdapter
                        item.isVisible = false
                        sortAction.isVisible = false

                        libraryModel.resetQuery()

                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        binding.libraryRecycler.adapter = libraryAdapter
                        item.isVisible = true
                        sortAction.isVisible = true

                        libraryModel.resetQuery()

                        return true
                    }
                })
            }
        }

        binding.libraryRecycler.apply {
            adapter = libraryAdapter
            setHasFixedSize(true)

            if (isLandscape(resources)) {
                val spans = getLandscapeSpans(resources)

                layoutManager = GridLayoutManager(requireContext(), spans).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (binding.libraryRecycler.adapter == searchAdapter) {
                                if (searchAdapter.currentList[position] is Header) spans else 1
                            } else 1
                        }
                    }
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        libraryModel.libraryData.observe(viewLifecycleOwner) {
            libraryAdapter.updateData(it)
        }

        libraryModel.searchResults.observe(viewLifecycleOwner) {
            if (binding.libraryRecycler.adapter == searchAdapter) {
                searchAdapter.submitList(it) {
                    binding.libraryRecycler.scrollToPosition(0)
                }
            }
        }

        libraryModel.sortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            val modeId = mode.toMenuId()

            // Highlight the item instead of using a checkable since the checkables just...wont
            // respond to any attempts to make them checked or not.
            sortAction.subMenu.forEach {
                if (it.itemId == modeId) {
                    it.applyColor(accent.first.toColor(requireContext()))
                } else {
                    it.applyColor(resolveAttr(requireContext(), android.R.attr.textColorPrimary))
                }
            }
        }

        detailModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
                libraryModel.updateNavigationStatus(false)

                if (it is Song) {
                    onItemSelection(it.album)
                } else {
                    onItemSelection(it)
                }
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        libraryModel.updateNavigationStatus(false)
    }

    override fun onQueryTextSubmit(query: String): Boolean = false

    override fun onQueryTextChange(query: String): Boolean {
        libraryModel.doSearch(query, requireContext())

        return true
    }

    /**
     * Show the [ActionMenu] actions for an item.
     * @param data The model that the actions should correspond to
     * @param view The anchor view the menu should be bound to.
     */
    private fun showActionsForItem(data: BaseModel, view: View) {
        ActionMenu(requireCompatActivity(), view, data, ActionMenu.FLAG_NONE)
    }

    /**
     * Navigate to an item, or play it, depending on what the given item is.
     * @param baseModel The data things should be done with
     */
    private fun onItemSelection(baseModel: BaseModel) {
        // If the item is a song [That was selected through search], then update the playback
        // to that song instead of doing any navigation
        if (baseModel is Song) {
            val settingsManager = SettingsManager.getInstance()

            playbackModel.playSong(baseModel, settingsManager.songPlaybackMode)
            return
        }

        if (!libraryModel.isNavigating) {
            libraryModel.updateNavigationStatus(true)

            logD("Navigating to the detail fragment for ${baseModel.name}")

            findNavController().navigate(
                when (baseModel) {
                    is Genre -> LibraryFragmentDirections.actionShowGenre(baseModel.id)
                    is Artist -> LibraryFragmentDirections.actionShowArtist(baseModel.id)
                    is Album -> LibraryFragmentDirections.actionShowAlbum(baseModel.id, false)

                    // If given model wasn't valid, then reset the navigation status
                    // and abort the navigation.
                    else -> {
                        libraryModel.updateNavigationStatus(false)
                        return
                    }
                }
            )
        }
    }
}

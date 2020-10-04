package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.library.recycler.LibraryAdapter
import org.oxycblt.auxio.library.recycler.SearchAdapter
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.theme.SHOW_ALBUMS
import org.oxycblt.auxio.theme.SHOW_ARTISTS
import org.oxycblt.auxio.theme.SHOW_GENRES
import org.oxycblt.auxio.theme.applyColor
import org.oxycblt.auxio.theme.applyDivider
import org.oxycblt.auxio.theme.resolveAttr

class LibraryFragment : Fragment(), SearchView.OnQueryTextListener {

    private val musicModel: MusicViewModel by activityViewModels()
    private val libraryModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLibraryBinding.inflate(inflater)

        val libraryAdapter = LibraryAdapter(libraryModel.showMode.value!!) {
            navToItem(it)
        }

        val searchAdapter = SearchAdapter {
            navToItem(it)
        }

        // Toolbar setup
        binding.libraryToolbar.overflowIcon = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_sort_none
        )

        binding.libraryToolbar.menu.apply {
            val item = findItem(R.id.action_search)
            val searchView = item.actionView as SearchView

            // Set up the SearchView itself
            searchView.queryHint = getString(R.string.hint_search_library)
            searchView.setOnQueryTextListener(this@LibraryFragment)
            searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                libraryModel.updateSearchFocusStatus(hasFocus)
                item.isVisible = !hasFocus
            }

            item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    // When opened, update the adapter to the SearchAdapter
                    // And remove the sorting group
                    binding.libraryRecycler.adapter = searchAdapter
                    setGroupVisible(R.id.group_sorting, false)
                    libraryModel.resetQuery()

                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    // When closed, switch back to LibraryAdapter, make the sorting
                    // visible again, and reset the query so that the old results wont show
                    // up if the search is opened again.
                    binding.libraryRecycler.adapter = libraryAdapter
                    setGroupVisible(R.id.group_sorting, true)

                    return true
                }
            })
        }

        binding.libraryToolbar.setOnMenuItemClickListener {
            if (it.itemId != R.id.action_search) {
                libraryModel.updateSortMode(it)
            } else {
                // Do whatever this is in order to make the SearchView focusable.
                (it.actionView as SearchView).isIconified = false

                // Then also do a basic animation
                TransitionManager.beginDelayedTransition(
                    binding.libraryToolbar, Fade()
                )
                it.expandActionView()
            }
            true
        }

        // RecyclerView setup
        binding.libraryRecycler.adapter = libraryAdapter
        binding.libraryRecycler.applyDivider()
        binding.libraryRecycler.setHasFixedSize(true)

        libraryModel.sortMode.observe(viewLifecycleOwner) { mode ->
            Log.d(this::class.simpleName, "Updating sort mode to $mode")

            // Update the adapter with the new data
            libraryAdapter.updateData(
                when (libraryModel.showMode.value) {
                    SHOW_GENRES -> mode.getSortedGenreList(musicModel.genres.value!!)
                    SHOW_ARTISTS -> mode.getSortedArtistList(musicModel.artists.value!!)
                    SHOW_ALBUMS -> mode.getSortedAlbumList(musicModel.albums.value!!)

                    else -> mode.getSortedArtistList(musicModel.artists.value!!)
                }
            )

            // Then update the menu item in the toolbar to reflect the new mode
            binding.libraryToolbar.menu.forEach {
                if (it.itemId == libraryModel.sortMode.value!!.toMenuId()) {
                    it.applyColor(resolveAttr(requireContext(), R.attr.colorPrimary))
                } else {
                    it.applyColor(resolveAttr(requireContext(), android.R.attr.textColorPrimary))
                }
            }
        }

        libraryModel.searchResults.observe(viewLifecycleOwner) {
            if (libraryModel.searchHasFocus) {
                searchAdapter.submitList(it)
            }
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        libraryModel.updateNavigationStatus(false)
    }

    override fun onQueryTextSubmit(query: String): Boolean = false

    override fun onQueryTextChange(query: String): Boolean {
        libraryModel.updateSearchQuery(query, musicModel)

        return false
    }

    private fun navToItem(baseModel: BaseModel) {
        Log.d(this::class.simpleName, "Navigating to the detail fragment for ${baseModel.name}")

        if (!libraryModel.isNavigating) {
            libraryModel.updateNavigationStatus(true)

            findNavController().navigate(
                when (baseModel) {
                    is Genre -> MainFragmentDirections.actionShowGenre(baseModel.id)
                    is Artist -> MainFragmentDirections.actionShowArtist(baseModel.id)
                    is Album -> MainFragmentDirections.actionShowAlbum(baseModel.id, true)

                    else -> return
                }
            )
        }
    }
}

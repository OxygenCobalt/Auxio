package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.ClickListener
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

        // Toolbar setup
        binding.libraryToolbar.overflowIcon = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_sort_none
        )

        binding.libraryToolbar.menu.apply {
            val item = findItem(R.id.action_search)
            val searchView = item.actionView as SearchView

            searchView.queryHint = getString(R.string.hint_search_library)
            searchView.setOnQueryTextListener(this@LibraryFragment)
            searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                this.setGroupVisible(R.id.group_sorting, !hasFocus)

                // Make sure the search item will still be visible, and then do an animation
                item.isVisible = !hasFocus
                TransitionManager.beginDelayedTransition(
                    binding.libraryToolbar, Fade()
                )
                item.collapseActionView()
            }
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

        val libraryAdapter = LibraryAdapter(
            libraryModel.showMode.value!!,
            ClickListener { navToItem(it) }
        )

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

        libraryModel.searchQuery.observe(viewLifecycleOwner) { query ->
            // Update the adapter with the new data
            libraryAdapter.updateData(
                when (libraryModel.showMode.value) {
                    SHOW_GENRES -> musicModel.genres.value!!
                    SHOW_ARTISTS -> musicModel.artists.value!!
                    SHOW_ALBUMS -> musicModel.albums.value!!

                    else -> musicModel.artists.value!!
                }.filter { it.name.contains(query, true) }
            )
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onQueryTextSubmit(query: String): Boolean = false

    override fun onQueryTextChange(query: String): Boolean {
        libraryModel.updateSearchQuery(query)

        return false
    }

    private fun navToItem(baseModel: BaseModel) {
        Log.d(this::class.simpleName, "Navigating to the detail fragment for ${baseModel.name}")

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

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
 
package org.oxycblt.auxio.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSearchBinding
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.adapter.BasicListInstructions
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.util.*

/**
 * The [ListFragment] providing search functionality for the music library.
 *
 * TODO: Better keyboard management
 *
 * TODO: Multi-filtering with chips
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class SearchFragment : ListFragment<Music, FragmentSearchBinding>() {
    override val navModel: NavigationViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    override val selectionModel: SelectionViewModel by activityViewModels()
    private val searchModel: SearchViewModel by viewModels()
    private val searchAdapter = SearchAdapter(this)
    private var imm: InputMethodManager? = null
    private var launchedKeyboard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentSearchBinding.inflate(inflater)

    override fun getSelectionToolbar(binding: FragmentSearchBinding) =
        binding.searchSelectionToolbar

    override fun onBindingCreated(binding: FragmentSearchBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        imm = binding.context.getSystemServiceCompat(InputMethodManager::class)

        binding.searchToolbar.apply {
            // Initialize the current filtering mode.
            menu.findItem(searchModel.getFilterOptionId()).isChecked = true

            setNavigationOnClickListener {
                // Keyboard is no longer needed.
                hideKeyboard()
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener(this@SearchFragment)
        }

        binding.searchEditText.apply {
            addTextChangedListener { text ->
                // Run the search with the updated text as the query
                searchModel.search(text?.toString())
            }

            if (!launchedKeyboard) {
                // Auto-open the keyboard when this view is shown
                showKeyboard(this)
                launchedKeyboard = true
            }
        }

        binding.searchRecycler.adapter = searchAdapter

        // --- VIEWMODEL SETUP ---

        collectImmediately(searchModel.searchResults, ::updateSearchResults)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(navModel.exploreNavigationItem, ::handleNavigation)
        collectImmediately(selectionModel.selected, ::updateSelection)
    }

    override fun onDestroyBinding(binding: FragmentSearchBinding) {
        super.onDestroyBinding(binding)
        binding.searchToolbar.setOnMenuItemClickListener(null)
        binding.searchRecycler.adapter = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (super.onMenuItemClick(item)) {
            return true
        }

        // Ignore junk sub-menu click events
        if (item.itemId != R.id.submenu_filtering) {
            // Is a change in filter mode and not just a junk submenu click, update
            // the filtering within SearchViewModel.
            item.isChecked = true
            searchModel.setFilterOptionId(item.itemId)
            return true
        }

        return false
    }

    override fun onRealClick(item: Music) {
        when (item) {
            is MusicParent -> navModel.exploreNavigateTo(item)
            is Song -> playbackModel.playFrom(item, searchModel.playbackMode)
        }
    }

    override fun onOpenMenu(item: Music, anchor: View) {
        when (item) {
            is Song -> openMusicMenu(anchor, R.menu.menu_song_actions, item)
            is Album -> openMusicMenu(anchor, R.menu.menu_album_actions, item)
            is Artist -> openMusicMenu(anchor, R.menu.menu_artist_actions, item)
            is Genre -> openMusicMenu(anchor, R.menu.menu_artist_actions, item)
        }
    }

    private fun updateSearchResults(results: List<Item>) {
        val binding = requireBinding()
        // Don't show the RecyclerView (and it's stray overscroll effects) when there
        // are no results.
        binding.searchRecycler.isInvisible = results.isEmpty()
        searchAdapter.submitList(results.toMutableList(), BasicListInstructions.DIFF) {
            // I would make it so that the position is only scrolled back to the top when
            // the query actually changes instead of once every re-creation event, but sadly
            // that doesn't seem possible.
            binding.searchRecycler.scrollToPosition(0)
        }
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        searchAdapter.setPlaying(parent ?: song, isPlaying)
    }

    private fun handleNavigation(item: Music?) {
        val action =
            when (item) {
                is Song -> SearchFragmentDirections.actionShowAlbum(item.album.uid)
                is Album -> SearchFragmentDirections.actionShowAlbum(item.uid)
                is Artist -> SearchFragmentDirections.actionShowArtist(item.uid)
                is Genre -> SearchFragmentDirections.actionShowGenre(item.uid)
                else -> return
            }
        // Keyboard is no longer needed.
        hideKeyboard()
        findNavController().navigate(action)
    }

    private fun updateSelection(selected: List<Music>) {
        searchAdapter.setSelected(selected.toSet())
        if (requireBinding().searchSelectionToolbar.updateSelectionAmount(selected.size) &&
            selected.isNotEmpty()) {
            // Make selection of obscured items easier by hiding the keyboard.
            hideKeyboard()
        }
    }

    /**
     * Safely focus the keyboard on a particular [View].
     * @param view The [View] to focus the keyboard on.
     */
    private fun showKeyboard(view: View) {
        view.apply {
            requestFocus()
            postDelayed(200) {
                requireNotNull(imm) { "InputMethodManager was not available" }
                    .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    /** Safely hide the keyboard from this view. */
    private fun hideKeyboard() {
        requireNotNull(imm) { "InputMethodManager was not available" }
            .hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

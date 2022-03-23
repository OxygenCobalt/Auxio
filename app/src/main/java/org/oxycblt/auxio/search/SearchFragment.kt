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
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSearchBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Item
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD

/**
 * A [Fragment] that allows for the searching of the entire music library.
 * @author OxygenCobalt
 */
class SearchFragment : ViewBindingFragment<FragmentSearchBinding>() {
    // SearchViewModel is only scoped to this Fragment
    private val searchModel: SearchViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    private var launchedKeyboard = false

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentSearchBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentSearchBinding, savedInstanceState: Bundle?) {
        val imm = requireContext().getSystemServiceSafe(InputMethodManager::class)

        val searchAdapter =
            SearchAdapter(doOnClick = { item -> onItemSelection(item, imm) }, ::newMenu)

        // --- UI SETUP --

        binding.searchToolbar.apply {
            menu.findItem(searchModel.filterMode?.itemId ?: R.id.option_filter_all).isChecked = true

            setNavigationOnClickListener {
                imm.hide()
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener { item ->
                if (item.itemId != R.id.submenu_filtering) {
                    searchModel.updateFilterModeWithId(item.itemId)
                    item.isChecked = true
                    true
                } else {
                    false
                }
            }
        }

        binding.searchEditText.apply {
            addTextChangedListener { text ->
                // Run the search with the updated text as the query
                searchModel.search(text?.toString() ?: "")
            }

            if (!launchedKeyboard) {
                // Auto-open the keyboard when this view is shown
                requestFocus()
                postDelayed(200) { imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT) }

                launchedKeyboard = true
            }
        }

        binding.searchRecycler.apply {
            adapter = searchAdapter
            applySpans { pos -> searchAdapter.currentList[pos] is Header }
        }

        // --- VIEWMODEL SETUP ---

        searchModel.searchResults.observe(viewLifecycleOwner) { results ->
            updateResults(results, searchAdapter)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            handleNavigation(item)
            imm.hide()
        }
    }

    override fun onResume() {
        super.onResume()
        searchModel.setNavigating(false)
    }

    private fun updateResults(results: List<Item>, searchAdapter: SearchAdapter) {
        val binding = requireBinding()

        searchAdapter.submitList(results) {
            // I would make it so that the position is only scrolled back to the top when
            // the query actually changes instead of once every re-creation event, but sadly
            // that doesn't seem possible.
            binding.searchRecycler.scrollToPosition(0)
        }

        binding.searchRecycler.isInvisible = results.isEmpty()
    }

    private fun handleNavigation(item: Music?) {
        findNavController()
            .navigate(
                when (item) {
                    is Song -> SearchFragmentDirections.actionShowAlbum(item.album.id)
                    is Album -> SearchFragmentDirections.actionShowAlbum(item.id)
                    is Artist -> SearchFragmentDirections.actionShowArtist(item.id)
                    else -> return
                })
    }

    private fun InputMethodManager.hide() {
        hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * Function that handles when an [item] is selected. Handles all datatypes that are selectable.
     */
    private fun onItemSelection(item: Music, imm: InputMethodManager) {
        if (item is Song) {
            playbackModel.playSong(item)

            return
        }

        if (!searchModel.isNavigating) {
            searchModel.setNavigating(true)

            logD("Navigating to the detail fragment for ${item.rawName}")

            findNavController()
                .navigate(
                    when (item) {
                        is Genre -> SearchFragmentDirections.actionShowGenre(item.id)
                        is Artist -> SearchFragmentDirections.actionShowArtist(item.id)
                        is Album -> SearchFragmentDirections.actionShowAlbum(item.id)

                        // If given model wasn't valid, then reset the navigation status
                        // and abort the navigation.
                        else -> {
                            searchModel.setNavigating(false)
                            return
                        }
                    })

            imm.hide()
        }
    }
}

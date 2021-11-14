/*
 * Copyright (c) 2021 Auxio Project
 * SearchFragment.kt is part of Auxio.
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
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD

/**
 * A [Fragment] that allows for the searching of the entire music library.
 * @author OxygenCobalt
 */
class SearchFragment : Fragment() {
    // SearchViewModel is only scoped to this Fragment
    private val searchModel: SearchViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater)

        val imm = requireContext().getSystemServiceSafe(InputMethodManager::class)

        val searchAdapter = SearchAdapter(
            doOnClick = { item ->
                onItemSelection(item, imm)
            },
            ::newMenu
        )
        // --- UI SETUP --

        binding.lifecycleOwner = viewLifecycleOwner

        binding.searchToolbar.apply {
            val itemId = when (searchModel.filterMode) {
                DisplayMode.SHOW_SONGS -> R.id.option_filter_songs
                DisplayMode.SHOW_ALBUMS -> R.id.option_filter_albums
                DisplayMode.SHOW_ARTISTS -> R.id.option_filter_artists
                DisplayMode.SHOW_GENRES -> R.id.option_filter_genres
                null -> R.id.option_filter_all
            }

            menu.findItem(itemId).isChecked = true

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

            // Auto-open the keyboard when this view is shown
            requestFocus()

            postDelayed(200) {
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.searchRecycler.apply {
            adapter = searchAdapter

            applySpans { pos ->
                searchAdapter.currentList[pos] is Header
            }
        }

        // --- VIEWMODEL SETUP ---

        searchModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results) {
                // We've just scrolled back to the top, reset the lifted state
                binding.searchRecycler.scrollToPosition(0)
            }

            if (results.isEmpty()) {
                binding.searchRecycler.visibility = View.INVISIBLE
            } else {
                binding.searchRecycler.visibility = View.VISIBLE
            }
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            findNavController().navigate(
                when (item) {
                    is Song -> SearchFragmentDirections.actionShowAlbum(item.album.id)
                    is Album -> SearchFragmentDirections.actionShowAlbum(item.id)
                    is Artist -> SearchFragmentDirections.actionShowArtist(item.id)

                    else -> return@observe
                }
            )

            imm.hide()
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        searchModel.setNavigating(false)
    }

    private fun InputMethodManager.hide() {
        hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * Function that handles when an [item] is selected.
     * Handles all datatypes that are selectable.
     */
    private fun onItemSelection(item: Music, imm: InputMethodManager) {
        if (item is Song) {
            playbackModel.playSong(item)

            return
        }

        if (!searchModel.isNavigating) {
            searchModel.setNavigating(true)

            logD("Navigating to the detail fragment for ${item.name}")

            findNavController().navigate(
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
                }
            )

            imm.hide()
        }
    }
}

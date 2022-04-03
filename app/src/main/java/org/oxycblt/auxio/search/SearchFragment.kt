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
import android.view.View
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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.Header
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.requireAttached

/**
 * A [Fragment] that allows for the searching of the entire music library.
 * @author OxygenCobalt
 */
class SearchFragment : ViewBindingFragment<FragmentSearchBinding>(), MenuItemListener {
    // SearchViewModel is only scoped to this Fragment
    private val searchModel: SearchViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()

    private val searchAdapter = SearchAdapter(this)
    private var imm: InputMethodManager? = null
    private var launchedKeyboard = false

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentSearchBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentSearchBinding, savedInstanceState: Bundle?) {
        binding.searchToolbar.apply {
            menu.findItem(searchModel.filterMode?.itemId ?: R.id.option_filter_all).isChecked = true

            setNavigationOnClickListener {
                requireImm().hide()
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener { item ->
                if (item.itemId != R.id.submenu_filtering) {
                    searchModel.updateFilterModeWithId(context, item.itemId)
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
                searchModel.search(context, text?.toString())
            }

            if (!launchedKeyboard) {
                // Auto-open the keyboard when this view is shown
                requestFocus()
                postDelayed(200) {
                    requireImm().showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }

                launchedKeyboard = true
            }
        }

        binding.searchRecycler.apply {
            adapter = searchAdapter
            applySpans { pos -> searchAdapter.data.currentList[pos] is Header }
        }

        // --- VIEWMODEL SETUP ---

        searchModel.searchResults.observe(viewLifecycleOwner, ::updateResults)
        navModel.exploreNavigationItem.observe(viewLifecycleOwner, ::handleNavigation)
        musicModel.loaderResponse.observe(viewLifecycleOwner, ::handleLoaderResponse)
    }

    override fun onResume() {
        super.onResume()
        searchModel.setNavigating(false)
    }

    override fun onDestroyBinding(binding: FragmentSearchBinding) {
        super.onDestroyBinding(binding)
        binding.searchRecycler.adapter = null
        imm = null
    }

    override fun onItemClick(item: Item) {
        when (item) {
            is Song -> playbackModel.playSong(item)
            is MusicParent -> navModel.exploreNavigateTo(item)
        }
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        newMenu(anchor, item)
    }

    private fun updateResults(results: List<Item>) {
        if (isDetached) {
            error("Fragment not attached to activity")
        }

        val binding = requireBinding()

        searchAdapter.data.submitList(results.toMutableList()) {
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
                    is Genre -> SearchFragmentDirections.actionShowGenre(item.id)
                    else -> return
                })

        requireImm().hide()
    }

    private fun handleLoaderResponse(response: MusicStore.Response?) {
        if (response is MusicStore.Response.Ok) {
            searchModel.refresh(requireContext())
        }
    }

    private fun requireImm(): InputMethodManager {
        requireAttached()
        val instance = imm
        if (instance != null) {
            return instance
        }
        val newInstance = requireContext().getSystemServiceSafe(InputMethodManager::class)
        imm = newInstance
        return newInstance
    }

    private fun InputMethodManager.hide() {
        hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSearchBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.fragment.MenuFragment
import org.oxycblt.auxio.ui.recycler.Header
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.util.androidViewModels
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logW

/**
 * A [Fragment] that allows for the searching of the entire music library.
 * @author OxygenCobalt
 */
class SearchFragment :
    MenuFragment<FragmentSearchBinding>(), MenuItemListener, Toolbar.OnMenuItemClickListener {

    // SearchViewModel is only scoped to this Fragment
    private val searchModel: SearchViewModel by androidViewModels()

    private val searchAdapter = SearchAdapter(this)
    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }
    private val imm: InputMethodManager by lifecycleObject { binding ->
        binding.context.getSystemServiceSafe(InputMethodManager::class)
    }

    private var launchedKeyboard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        returnTransition = MaterialFadeThrough()
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentSearchBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentSearchBinding, savedInstanceState: Bundle?) {
        binding.searchToolbar.apply {
            menu.findItem(searchModel.filterMode?.itemId ?: R.id.option_filter_all).isChecked = true

            setNavigationOnClickListener {
                imm.hide()
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
                requestFocus()
                postDelayed(200) { imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT) }

                launchedKeyboard = true
            }
        }

        binding.searchRecycler.apply {
            adapter = searchAdapter
            applySpans { pos -> searchAdapter.data.currentList[pos] is Header }
        }

        // --- VIEWMODEL SETUP ---

        collectImmediately(searchModel.searchResults, ::updateResults)
        collect(navModel.exploreNavigationItem, ::handleNavigation)
    }

    override fun onDestroyBinding(binding: FragmentSearchBinding) {
        binding.searchToolbar.setOnMenuItemClickListener(null)
        binding.searchRecycler.adapter = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.submenu_filtering -> {}
            else -> {
                if (item.itemId != R.id.submenu_filtering) {
                    searchModel.updateFilterModeWithId(item.itemId)
                    item.isChecked = true
                }
            }
        }

        return true
    }

    override fun onItemClick(item: Item) {
        when (item) {
            is Song -> playbackModel.play(item, settings.libPlaybackMode)
            is MusicParent -> navModel.exploreNavigateTo(item)
        }
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        when (item) {
            is Song -> musicMenu(anchor, R.menu.menu_song_actions, item)
            is Album -> musicMenu(anchor, R.menu.menu_album_actions, item)
            is Artist -> musicMenu(anchor, R.menu.menu_genre_artist_actions, item)
            is Genre -> musicMenu(anchor, R.menu.menu_genre_artist_actions, item)
            else -> logW("Unexpected datatype when opening menu: ${item::class.java}")
        }
    }

    private fun updateResults(results: List<Item>) {
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

        imm.hide()
    }

    private fun InputMethodManager.hide() {
        hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

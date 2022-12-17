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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSearchBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.fragment.MenuFragment
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.selection.SelectionToolbarOverlay
import org.oxycblt.auxio.ui.selection.SelectionViewModel
import org.oxycblt.auxio.util.*

/**
 * A [Fragment] that allows for the searching of the entire music library.
 * FIXME: Keyboard logic is really wonky
 * @author OxygenCobalt
 */
class SearchFragment :
    MenuFragment<FragmentSearchBinding>(), MenuItemListener, Toolbar.OnMenuItemClickListener, SelectionToolbarOverlay.Callback {

    // SearchViewModel is only scoped to this Fragment
    private val searchModel: SearchViewModel by androidViewModels()
    private val selectionModel: SelectionViewModel by activityViewModels()

    private val searchAdapter = SearchAdapter(this)
    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }
    private val imm: InputMethodManager by lifecycleObject { binding ->
        binding.context.getSystemServiceCompat(InputMethodManager::class)
    }

    private var launchedKeyboard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentSearchBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentSearchBinding, savedInstanceState: Bundle?) {
        binding.searchToolbarOverlay.callback = this

        binding.searchToolbar.apply {
            val itemIdToSelect =
                when (searchModel.filterMode) {
                    MusicMode.SONGS -> R.id.option_filter_songs
                    MusicMode.ALBUMS -> R.id.option_filter_albums
                    MusicMode.ARTISTS -> R.id.option_filter_artists
                    MusicMode.GENRES -> R.id.option_filter_genres
                    null -> R.id.option_filter_all
                }

            menu.findItem(itemIdToSelect).isChecked = true

            setNavigationOnClickListener {
                // Reset selection (navigating to another selectable screen)
                selectionModel.consume()

                // Drop keyboard as it's no longer needed
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

        binding.searchRecycler.adapter = searchAdapter

        // --- VIEWMODEL SETUP ---

        collectImmediately(searchModel.searchResults, ::handleResults)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::handlePlayback)
        collect(navModel.exploreNavigationItem, ::handleNavigation)
        collectImmediately(selectionModel.selected, ::handleSelection)
    }

    override fun onDestroyBinding(binding: FragmentSearchBinding) {
        binding.searchToolbarOverlay.callback = null
        binding.searchToolbar.setOnMenuItemClickListener(null)
        binding.searchRecycler.adapter = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId != R.id.submenu_filtering) {
            searchModel.updateFilterModeWithId(item.itemId)
            item.isChecked = true
        }

        return true
    }

    override fun onClearSelection() {
        selectionModel.consume()
    }

    override fun onPlaySelectionNext() {
        playbackModel.playNext(selectionModel.consume())
        requireContext().showToast(R.string.lng_queue_added)
    }

    override fun onAddSelectionToQueue() {
        playbackModel.addToQueue(selectionModel.consume())
        requireContext().showToast(R.string.lng_queue_added)
    }

    override fun onItemClick(item: Item) {
        check(item is Music) { "Unexpected datatype ${item::class.simpleName}"}
        if (selectionModel.selected.value.isEmpty()) {
            when (item) {
                is Song ->
                    when (settings.libPlaybackMode) {
                        MusicMode.SONGS -> playbackModel.playFromAll(item)
                        MusicMode.ALBUMS -> playbackModel.playFromAlbum(item)
                        MusicMode.ARTISTS -> playbackModel.playFromArtist(item)
                        else -> error("Unexpected playback mode: ${settings.libPlaybackMode}")
                    }
                is MusicParent -> navModel.exploreNavigateTo(item)
            }
        } else {
            selectionModel.select(item)
        }
    }

    override fun onSelect(item: Item) {
        check(item is Music) { "Unexpected datatype ${item::class.simpleName}"}
        selectionModel.select(item)
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        when (item) {
            is Song -> musicMenu(anchor, R.menu.menu_song_actions, item)
            is Album -> musicMenu(anchor, R.menu.menu_album_actions, item)
            is Artist -> musicMenu(anchor, R.menu.menu_artist_actions, item)
            is Genre -> musicMenu(anchor, R.menu.menu_artist_actions, item)
            else -> logW("Unexpected datatype when opening menu: ${item::class.java}")
        }
    }

    private fun handleResults(results: List<Item>) {
        val binding = requireBinding()

        searchAdapter.submitList(results.toMutableList()) {
            // I would make it so that the position is only scrolled back to the top when
            // the query actually changes instead of once every re-creation event, but sadly
            // that doesn't seem possible.
            binding.searchRecycler.scrollToPosition(0)
        }

        binding.searchRecycler.isInvisible = results.isEmpty()
    }

    private fun handlePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        searchAdapter.updateIndicator(parent ?: song, isPlaying)
    }

    private fun handleNavigation(item: Music?) {
        findNavController()
            .navigate(
                when (item) {
                    is Song -> SearchFragmentDirections.actionShowAlbum(item.album.uid)
                    is Album -> SearchFragmentDirections.actionShowAlbum(item.uid)
                    is Artist -> SearchFragmentDirections.actionShowArtist(item.uid)
                    is Genre -> SearchFragmentDirections.actionShowGenre(item.uid)
                    else -> return
                })

        // Reset selection (navigating to another selectable screen)
        selectionModel.consume()

        // Drop keyboard as it's no longer needed
        imm.hide()
    }

    private fun handleSelection(selected: List<Music>) {
        searchAdapter.updateSelection(selected)
        if (requireBinding().searchToolbarOverlay.updateSelectionAmount(selected.size) && selected.isNotEmpty()) {
            imm.hide()
        }
    }

    private fun InputMethodManager.hide() {
        hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

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
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSearchBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.detail.Show
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.menu.MenuViewModel
import org.oxycblt.auxio.list.menu.PendingMenu
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.setFullWidthLookup

/**
 * The [ListFragment] providing search functionality for the music library.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Better keyboard management
 * TODO: Multi-filtering with chips
 */
@AndroidEntryPoint
class SearchFragment : ListFragment<Music, FragmentSearchBinding>() {
    override val detailModel: DetailViewModel by activityViewModels()
    private val menuModel: MenuViewModel by activityViewModels()
    override val selectionModel: SelectionViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
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

        binding.searchNormalToolbar.apply {
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
                searchModel.search(text?.toString()?.trim())
            }

            if (!launchedKeyboard) {
                // Auto-open the keyboard when this view is shown
                this@SearchFragment.logD("Keyboard is not shown yet")
                showKeyboard(this)
                launchedKeyboard = true
            }
        }

        binding.searchRecycler.apply {
            adapter = searchAdapter
            (layoutManager as GridLayoutManager).setFullWidthLookup {
                val item =
                    searchModel.searchResults.value.getOrElse(it) {
                        return@setFullWidthLookup false
                    }
                item is Divider || item is Header
            }
        }

        // --- VIEWMODEL SETUP ---

        collectImmediately(searchModel.searchResults, ::updateSearchResults)
        collect(menuModel.pendingMenu.flow, ::handleMenu)
        collectImmediately(selectionModel.selected, ::updateSelection)
        collect(musicModel.playlistDecision.flow, ::handleDecision)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(detailModel.toShow.flow, ::handleShow)
    }

    override fun onDestroyBinding(binding: FragmentSearchBinding) {
        super.onDestroyBinding(binding)
        binding.searchNormalToolbar.setOnMenuItemClickListener(null)
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
            logD("Filter mode selected")
            item.isChecked = true
            searchModel.setFilterOptionId(item.itemId)
            return true
        }

        return false
    }

    override fun onRealClick(item: Music) {
        when (item) {
            is Song -> playbackModel.playFrom(item, searchModel.playbackMode)
            is Album -> detailModel.showAlbum(item)
            is Artist -> detailModel.showArtist(item)
            is Genre -> detailModel.showGenre(item)
            is Playlist -> detailModel.showPlaylist(item)
        }
    }

    override fun onOpenMenu(item: Music, anchor: View) {
        when (item) {
            is Song -> menuModel.openMenu(R.menu.item_song, item)
            is Album -> menuModel.openMenu(R.menu.item_album, item)
            is Artist -> menuModel.openMenu(R.menu.item_parent, item)
            is Genre -> menuModel.openMenu(R.menu.item_parent, item)
            is Playlist -> menuModel.openMenu(R.menu.item_playlist, item)
        }
    }

    private fun updateSearchResults(results: List<Item>) {
        val binding = requireBinding()
        // Don't show the RecyclerView (and it's stray overscroll effects) when there
        // are no results.
        binding.searchRecycler.isInvisible = results.isEmpty()
        searchAdapter.update(results.toMutableList(), null) {
            // I would make it so that the position is only scrolled back to the top when
            // the query actually changes instead of once every re-creation event, but sadly
            // that doesn't seem possible.
            logD("Update finished, scrolling to top")
            binding.searchRecycler.scrollToPosition(0)
        }
    }

    private fun handleShow(show: Show?) {
        when (show) {
            is Show.SongDetails -> {
                logD("Navigating to ${show.song}")
                findNavController().navigateSafe(SearchFragmentDirections.showSong(show.song.uid))
            }

            // Songs should be scrolled to if the album matches, or a new detail
            // fragment should be launched otherwise.
            is Show.SongAlbumDetails -> {
                logD("Navigating to the album of ${show.song}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showAlbum(show.song.album.uid))
            }

            // If the album matches, no need to do anything. Otherwise launch a new
            // detail fragment.
            is Show.AlbumDetails -> {
                logD("Navigating to ${show.album}")
                findNavController().navigateSafe(SearchFragmentDirections.showAlbum(show.album.uid))
            }

            // Always launch a new ArtistDetailFragment.
            is Show.ArtistDetails -> {
                logD("Navigating to ${show.artist}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showArtist(show.artist.uid))
            }
            is Show.SongArtistDetails -> {
                logD("Navigating to artist choices for ${show.song}")
                findNavController().navigateSafe(SearchFragmentDirections.showArtist(show.song.uid))
            }
            is Show.AlbumArtistDetails -> {
                logD("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showArtist(show.album.uid))
            }
            is Show.GenreDetails -> {
                logD("Navigating to ${show.genre}")
                findNavController().navigateSafe(SearchFragmentDirections.showGenre(show.genre.uid))
            }
            is Show.PlaylistDetails -> {
                logD("Navigating to ${show.playlist}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showPlaylist(show.playlist.uid))
            }
            null -> {}
        }

        // Keyboard is no longer needed.
        hideKeyboard()
    }

    private fun handleDecision(decision: PlaylistDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaylistDecision.Rename -> {
                    logD("Renaming ${decision.playlist}")
                    SearchFragmentDirections.renamePlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Delete -> {
                    logD("Deleting ${decision.playlist}")
                    SearchFragmentDirections.deletePlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Add -> {
                    logD("Adding ${decision.songs.size} to a playlist")
                    SearchFragmentDirections.addToPlaylist(
                        decision.songs.map { it.uid }.toTypedArray())
                }
                is PlaylistDecision.New -> {
                    error("Unexpected decision $decision")
                }
            }
        findNavController().navigateSafe(directions)
        musicModel.playlistDecision.consume()
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        searchAdapter.setPlaying(parent ?: song, isPlaying)
    }

    private fun handleMenu(pendingMenu: PendingMenu?) {
        if (pendingMenu == null) return
        val directions =
            when (pendingMenu) {
                is PendingMenu.ForSong ->
                    SearchFragmentDirections.openSongMenu(
                        pendingMenu.menuRes, pendingMenu.music.uid)
                is PendingMenu.ForAlbum ->
                    SearchFragmentDirections.openAlbumMenu(
                        pendingMenu.menuRes, pendingMenu.music.uid)
                is PendingMenu.ForArtist ->
                    SearchFragmentDirections.openArtistMenu(
                        pendingMenu.menuRes, pendingMenu.music.uid)
                is PendingMenu.ForGenre ->
                    SearchFragmentDirections.openGenreMenu(
                        pendingMenu.menuRes, pendingMenu.music.uid)
                is PendingMenu.ForPlaylist ->
                    SearchFragmentDirections.openPlaylistMenu(
                        pendingMenu.menuRes, pendingMenu.music.uid)
            }
        findNavController().navigateSafe(directions)
        menuModel.pendingMenu.consume()
    }

    private fun updateSelection(selected: List<Music>) {
        searchAdapter.setSelected(selected.toSet())
        val binding = requireBinding()
        if (selected.isNotEmpty()) {
            binding.searchSelectionToolbar.title = getString(R.string.fmt_selected, selected.size)
            if (binding.searchToolbar.setVisible(R.id.search_selection_toolbar)) {
                // New selection started, show the keyboard to make selection easier.
                logD("Significant selection occurred, hiding keyboard")
                hideKeyboard()
            }
        } else {
            binding.searchToolbar.setVisible(R.id.search_normal_toolbar)
        }
    }

    /**
     * Safely focus the keyboard on a particular [View].
     *
     * @param view The [View] to focus the keyboard on.
     */
    private fun showKeyboard(view: View) {
        logD("Launching keyboard")
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
        logD("Hiding keyboard")
        requireNotNull(imm) { "InputMethodManager was not available" }
            .hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

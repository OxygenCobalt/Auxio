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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.list.PlainDivider
import org.oxycblt.auxio.list.PlainHeader
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.MusicParent
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.musikr.Playlist
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.PlaylistMessage
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.playlist.m3u.M3U
import org.oxycblt.auxio.playback.PlaybackDecision
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.setFullWidthLookup
import org.oxycblt.auxio.util.showToast
import timber.log.Timber as L

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
    private val searchModel: SearchViewModel by viewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter(this)
    private var getContentLauncher: ActivityResultLauncher<String>? = null
    private var pendingImportTarget: Playlist? = null
    private var imm: InputMethodManager? = null
    private var launchedKeyboard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentSearchBinding.inflate(inflater)

    override fun getSelectionToolbar(binding: FragmentSearchBinding) =
        binding.searchSelectionToolbar

    override fun onBindingCreated(binding: FragmentSearchBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        imm = binding.context.getSystemServiceCompat(InputMethodManager::class)

        getContentLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri == null) {
                    L.w("No URI returned from file picker")
                    return@registerForActivityResult
                }

                L.d("Received playlist URI $uri")
                musicModel.importPlaylist(uri, pendingImportTarget)
            }

        // --- UI SETUP ---

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
                L.d("Keyboard is not shown yet")
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
                item is PlainDivider || item is PlainHeader
            }
        }

        // --- VIEWMODEL SETUP ---

        collectImmediately(searchModel.searchResults, ::updateSearchResults)
        collectImmediately(listModel.selected, ::updateSelection)
        collect(listModel.menu.flow, ::handleMenu)
        collect(musicModel.playlistDecision.flow, ::handlePlaylistDecision)
        collect(musicModel.playlistMessage.flow, ::handlePlaylistMessage)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(playbackModel.playbackDecision.flow, ::handlePlaybackDecision)
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
            L.d("Filter mode selected")
            item.isChecked = true
            searchModel.setFilterOptionId(item.itemId)
            return true
        }

        return false
    }

    override fun onRealClick(item: Music) {
        when (item) {
            is Song -> playbackModel.play(item, searchModel.playWith)
            is Album -> detailModel.showAlbum(item)
            is Artist -> detailModel.showArtist(item)
            is Genre -> detailModel.showGenre(item)
            is Playlist -> detailModel.showPlaylist(item)
        }
    }

    override fun onOpenMenu(item: Music) {
        when (item) {
            is Song -> listModel.openMenu(R.menu.song, item, searchModel.playWith)
            is Album -> listModel.openMenu(R.menu.album, item)
            is Artist -> listModel.openMenu(R.menu.parent, item)
            is Genre -> listModel.openMenu(R.menu.parent, item)
            is Playlist -> listModel.openMenu(R.menu.playlist, item)
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
            L.d("Update finished, scrolling to top")
            binding.searchRecycler.scrollToPosition(0)
        }
    }

    private fun handleShow(show: Show?) {
        when (show) {
            is Show.SongDetails -> {
                L.d("Navigating to ${show.song}")
                findNavController().navigateSafe(SearchFragmentDirections.showSong(show.song.uid))
            }
            is Show.SongAlbumDetails -> {
                L.d("Navigating to the album of ${show.song}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showAlbum(show.song.album.uid))
            }
            is Show.AlbumDetails -> {
                L.d("Navigating to ${show.album}")
                findNavController().navigateSafe(SearchFragmentDirections.showAlbum(show.album.uid))
            }
            is Show.ArtistDetails -> {
                L.d("Navigating to ${show.artist}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showArtist(show.artist.uid))
            }
            is Show.SongArtistDecision -> {
                L.d("Navigating to artist choices for ${show.song}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showArtistChoices(show.song.uid))
            }
            is Show.AlbumArtistDecision -> {
                L.d("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showArtistChoices(show.album.uid))
            }
            is Show.GenreDetails -> {
                L.d("Navigating to ${show.genre}")
                findNavController().navigateSafe(SearchFragmentDirections.showGenre(show.genre.uid))
            }
            is Show.PlaylistDetails -> {
                L.d("Navigating to ${show.playlist}")
                findNavController()
                    .navigateSafe(SearchFragmentDirections.showPlaylist(show.playlist.uid))
            }
            null -> {}
        }

        // Keyboard is no longer needed.
        hideKeyboard()
    }

    private fun handleMenu(menu: Menu?) {
        if (menu == null) return
        val directions =
            when (menu) {
                is Menu.ForSong -> SearchFragmentDirections.openSongMenu(menu.parcel)
                is Menu.ForAlbum -> SearchFragmentDirections.openAlbumMenu(menu.parcel)
                is Menu.ForArtist -> SearchFragmentDirections.openArtistMenu(menu.parcel)
                is Menu.ForGenre -> SearchFragmentDirections.openGenreMenu(menu.parcel)
                is Menu.ForPlaylist -> SearchFragmentDirections.openPlaylistMenu(menu.parcel)
                is Menu.ForSelection -> SearchFragmentDirections.openSelectionMenu(menu.parcel)
            }
        findNavController().navigateSafe(directions)
        // Keyboard is no longer needed.
        hideKeyboard()
    }

    private fun updateSelection(selected: List<Music>) {
        searchAdapter.setSelected(selected.toSet())
        val binding = requireBinding()
        if (selected.isNotEmpty()) {
            binding.searchSelectionToolbar.title = getString(R.string.fmt_selected, selected.size)
            if (binding.searchToolbar.setVisible(R.id.search_selection_toolbar)) {
                // New selection started, show the keyboard to make selection easier.
                L.d("Significant selection occurred, hiding keyboard")
                hideKeyboard()
            }
        } else {
            binding.searchToolbar.setVisible(R.id.search_normal_toolbar)
        }
    }

    private fun handlePlaylistDecision(decision: PlaylistDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaylistDecision.Import -> {
                    L.d("Importing playlist")
                    pendingImportTarget = decision.target
                    requireNotNull(getContentLauncher) {
                            "Content picker launcher was not available"
                        }
                        .launch(M3U.MIME_TYPE)
                    musicModel.playlistDecision.consume()
                    return
                }
                is PlaylistDecision.Rename -> {
                    L.d("Renaming ${decision.playlist}")
                    SearchFragmentDirections.renamePlaylist(
                        decision.playlist.uid,
                        decision.template,
                        decision.applySongs.map { it.uid }.toTypedArray(),
                        decision.reason)
                }
                is PlaylistDecision.Delete -> {
                    L.d("Deleting ${decision.playlist}")
                    SearchFragmentDirections.deletePlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Export -> {
                    L.d("Exporting ${decision.playlist}")
                    SearchFragmentDirections.exportPlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Add -> {
                    L.d("Adding ${decision.songs.size} to a playlist")
                    SearchFragmentDirections.addToPlaylist(
                        decision.songs.map { it.uid }.toTypedArray())
                }
                is PlaylistDecision.New -> {
                    error("Unexpected decision $decision")
                }
            }
        findNavController().navigateSafe(directions)
    }

    private fun handlePlaylistMessage(message: PlaylistMessage?) {
        if (message == null) return
        requireContext().showToast(message.stringRes)
        musicModel.playlistMessage.consume()
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        searchAdapter.setPlaying(parent ?: song, isPlaying)
    }

    private fun handlePlaybackDecision(decision: PlaybackDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaybackDecision.PlayFromArtist -> {
                    L.d("Launching play from artist dialog for $decision")
                    SearchFragmentDirections.playFromArtist(decision.song.uid)
                }
                is PlaybackDecision.PlayFromGenre -> {
                    L.d("Launching play from artist dialog for $decision")
                    SearchFragmentDirections.playFromGenre(decision.song.uid)
                }
            }
        findNavController().navigateSafe(directions)
    }

    /**
     * Safely focus the keyboard on a particular [View].
     *
     * @param view The [View] to focus the keyboard on.
     */
    private fun showKeyboard(view: View) {
        L.d("Launching keyboard")
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
        L.d("Hiding keyboard")
        requireNotNull(imm) { "InputMethodManager was not available" }
            .hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

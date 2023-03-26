/*
 * Copyright (c) 2021 Auxio Project
 * GenreDetailFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.header.DetailHeaderAdapter
import org.oxycblt.auxio.detail.header.GenreDetailHeaderAdapter
import org.oxycblt.auxio.detail.list.DetailListAdapter
import org.oxycblt.auxio.detail.list.GenreDetailListAdapter
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.navigation.NavigationViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.*

/**
 * A [ListFragment] that shows information for a particular [Genre].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class GenreDetailFragment :
    ListFragment<Music, FragmentDetailBinding>(),
    DetailHeaderAdapter.Listener,
    DetailListAdapter.Listener<Music> {
    private val detailModel: DetailViewModel by activityViewModels()
    override val navModel: NavigationViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    override val selectionModel: SelectionViewModel by activityViewModels()
    // Information about what genre to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an genre.
    private val args: GenreDetailFragmentArgs by navArgs()
    private val genreHeaderAdapter = GenreDetailHeaderAdapter(this)
    private val genreListAdapter = GenreDetailListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentDetailBinding.inflate(inflater)

    override fun getSelectionToolbar(binding: FragmentDetailBinding) =
        binding.detailSelectionToolbar

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.detailToolbar.apply {
            inflateMenu(R.menu.menu_parent_detail)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener(this@GenreDetailFragment)
        }

        binding.detailRecycler.adapter = ConcatAdapter(genreHeaderAdapter, genreListAdapter)

        // --- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setGenreUid(args.genreUid)
        collectImmediately(detailModel.currentGenre, ::updatePlaylist)
        collectImmediately(detailModel.genreList, ::updateList)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(navModel.exploreNavigationItem.flow, ::handleNavigation)
        collectImmediately(selectionModel.selected, ::updateSelection)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailToolbar.setOnMenuItemClickListener(null)
        binding.detailRecycler.adapter = null
        // Avoid possible race conditions that could cause a bad replace instruction to be consumed
        // during list initialization and crash the app. Could happen if the user is fast enough.
        detailModel.genreInstructions.consume()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (super.onMenuItemClick(item)) {
            return true
        }

        val currentGenre = unlikelyToBeNull(detailModel.currentGenre.value)
        return when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(currentGenre)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(currentGenre)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            else -> false
        }
    }

    override fun onRealClick(item: Music) {
        when (item) {
            is Artist -> navModel.exploreNavigateTo(item)
            is Song -> {
                val playbackMode = detailModel.playbackMode
                if (playbackMode != null) {
                    playbackModel.playFrom(item, playbackMode)
                } else {
                    // When configured to play from the selected item, we already have an Genre
                    // to play from.
                    playbackModel.playFromGenre(
                        item, unlikelyToBeNull(detailModel.currentGenre.value))
                }
            }
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    override fun onOpenMenu(item: Music, anchor: View) {
        when (item) {
            is Artist -> openMusicMenu(anchor, R.menu.menu_parent_actions, item)
            is Song -> openMusicMenu(anchor, R.menu.menu_song_actions, item)
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    override fun onPlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentGenre.value))
    }

    override fun onShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentGenre.value))
    }

    override fun onOpenSortMenu(anchor: View) {
        openMenu(anchor, R.menu.menu_genre_sort) {
            // Select the corresponding sort mode option
            val sort = detailModel.genreSongSort
            unlikelyToBeNull(menu.findItem(sort.mode.itemId)).isChecked = true
            // Select the corresponding sort direction option
            val directionItemId =
                when (sort.direction) {
                    Sort.Direction.ASCENDING -> R.id.option_sort_asc
                    Sort.Direction.DESCENDING -> R.id.option_sort_dec
                }
            unlikelyToBeNull(menu.findItem(directionItemId)).isChecked = true
            setOnMenuItemClickListener { item ->
                item.isChecked = !item.isChecked
                detailModel.genreSongSort =
                    when (item.itemId) {
                        // Sort direction options
                        R.id.option_sort_asc -> sort.withDirection(Sort.Direction.ASCENDING)
                        R.id.option_sort_dec -> sort.withDirection(Sort.Direction.DESCENDING)
                        // Any other option is a sort mode
                        else -> sort.withMode(unlikelyToBeNull(Sort.Mode.fromItemId(item.itemId)))
                    }
                true
            }
        }
    }

    private fun updatePlaylist(genre: Genre?) {
        if (genre == null) {
            // Genre we were showing no longer exists.
            findNavController().navigateUp()
            return
        }
        requireBinding().detailToolbar.title = genre.resolveName(requireContext())
        genreHeaderAdapter.setParent(genre)
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        var playingMusic: Music? = null
        if (parent is Artist) {
            playingMusic = parent
        }
        // Prefer songs that might be playing from this genre.
        if (parent is Genre && parent.uid == unlikelyToBeNull(detailModel.currentGenre.value).uid) {
            playingMusic = song
        }
        genreListAdapter.setPlaying(playingMusic, isPlaying)
    }

    private fun handleNavigation(item: Music?) {
        when (item) {
            is Song -> {
                logD("Navigating to another song")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.actionShowAlbum(item.album.uid))
            }
            is Album -> {
                logD("Navigating to another album")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.actionShowAlbum(item.uid))
            }
            is Artist -> {
                logD("Navigating to another artist")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.actionShowArtist(item.uid))
            }
            is Genre -> {
                navModel.exploreNavigationItem.consume()
            }
            else -> {}
        }
    }

    private fun updateList(list: List<Item>) {
        genreListAdapter.update(list, detailModel.genreInstructions.consume())
    }

    private fun updateSelection(selected: List<Music>) {
        genreListAdapter.setSelected(selected.toSet())
        requireBinding().detailSelectionToolbar.updateSelectionAmount(selected.size)
    }
}

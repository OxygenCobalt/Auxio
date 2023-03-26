/*
 * Copyright (c) 2021 Auxio Project
 * AlbumDetailFragment.kt is part of Auxio.
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
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.header.AlbumDetailHeaderAdapter
import org.oxycblt.auxio.detail.list.AlbumDetailListAdapter
import org.oxycblt.auxio.detail.list.DetailListAdapter
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.navigation.NavigationViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.*

/**
 * A [ListFragment] that shows information about an [Album].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class AlbumDetailFragment :
    ListFragment<Song, FragmentDetailBinding>(),
    AlbumDetailHeaderAdapter.Listener,
    DetailListAdapter.Listener<Song> {
    private val detailModel: DetailViewModel by activityViewModels()
    override val navModel: NavigationViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    override val selectionModel: SelectionViewModel by activityViewModels()
    // Information about what album to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an album.
    private val args: AlbumDetailFragmentArgs by navArgs()
    private val albumHeaderAdapter = AlbumDetailHeaderAdapter(this)
    private val albumListAdapter = AlbumDetailListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Detail transitions are always on the X axis. Shared element transitions are more
        // semantically correct, but are also too buggy to be sensible.
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

        // --- UI SETUP --
        binding.detailToolbar.apply {
            inflateMenu(R.menu.menu_album_detail)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener(this@AlbumDetailFragment)
        }

        binding.detailRecycler.adapter = ConcatAdapter(albumHeaderAdapter, albumListAdapter)

        // -- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setAlbumUid(args.albumUid)
        collectImmediately(detailModel.currentAlbum, ::updateAlbum)
        collectImmediately(detailModel.albumList, ::updateList)
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
        detailModel.albumInstructions.consume()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (super.onMenuItemClick(item)) {
            return true
        }

        val currentAlbum = unlikelyToBeNull(detailModel.currentAlbum.value)
        return when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(currentAlbum)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(currentAlbum)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_go_artist -> {
                onNavigateToParentArtist()
                true
            }
            else -> false
        }
    }

    override fun onRealClick(item: Song) {
        // There can only be one album, so a null mode and an ALBUMS mode will function the same.
        playbackModel.playFrom(item, detailModel.playbackMode ?: MusicMode.ALBUMS)
    }

    override fun onOpenMenu(item: Song, anchor: View) {
        openMusicMenu(anchor, R.menu.menu_album_song_actions, item)
    }

    override fun onPlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    override fun onShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    override fun onOpenSortMenu(anchor: View) {
        openMenu(anchor, R.menu.menu_album_sort) {
            // Select the corresponding sort mode option
            val sort = detailModel.albumSongSort
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
                detailModel.albumSongSort =
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

    override fun onNavigateToParentArtist() {
        navModel.exploreNavigateToParentArtist(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    private fun updateAlbum(album: Album?) {
        if (album == null) {
            // Album we were showing no longer exists.
            findNavController().navigateUp()
            return
        }
        requireBinding().detailToolbar.title = album.resolveName(requireContext())
        albumHeaderAdapter.setParent(album)
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        if (parent is Album && parent == unlikelyToBeNull(detailModel.currentAlbum.value)) {
            albumListAdapter.setPlaying(song, isPlaying)
        } else {
            // Clear the ViewHolders if the mode isn't ALL_SONGS
            albumListAdapter.setPlaying(null, isPlaying)
        }
    }

    private fun handleNavigation(item: Music?) {
        val binding = requireBinding()
        when (item) {
            // Songs should be scrolled to if the album matches, or a new detail
            // fragment should be launched otherwise.
            is Song -> {
                if (unlikelyToBeNull(detailModel.currentAlbum.value) == item.album) {
                    logD("Navigating to a song in this album")
                    scrollToAlbumSong(item)
                    navModel.exploreNavigationItem.consume()
                } else {
                    logD("Navigating to another album")
                    findNavController()
                        .navigateSafe(AlbumDetailFragmentDirections.actionShowAlbum(item.album.uid))
                }
            }

            // If the album matches, no need to do anything. Otherwise launch a new
            // detail fragment.
            is Album -> {
                if (unlikelyToBeNull(detailModel.currentAlbum.value) == item) {
                    logD("Navigating to the top of this album")
                    binding.detailRecycler.scrollToPosition(0)
                    navModel.exploreNavigationItem.consume()
                } else {
                    logD("Navigating to another album")
                    findNavController()
                        .navigateSafe(AlbumDetailFragmentDirections.actionShowAlbum(item.uid))
                }
            }

            // Always launch a new ArtistDetailFragment.
            is Artist -> {
                logD("Navigating to another artist")
                findNavController()
                    .navigateSafe(AlbumDetailFragmentDirections.actionShowArtist(item.uid))
            }
            null -> {}
            else -> error("Unexpected datatype: ${item::class.java}")
        }
    }

    private fun scrollToAlbumSong(song: Song) {
        // Calculate where the item for the currently played song is
        val pos = detailModel.albumList.value.indexOf(song)

        if (pos != -1) {
            // Only scroll if the song is within this album.
            val binding = requireBinding()
            binding.detailRecycler.post {
                // Use a custom smooth scroller that will settle the item in the middle of
                // the screen rather than the end.
                val centerSmoothScroller =
                    object : LinearSmoothScroller(context) {
                        init {
                            targetPosition = pos
                        }

                        override fun calculateDtToFit(
                            viewStart: Int,
                            viewEnd: Int,
                            boxStart: Int,
                            boxEnd: Int,
                            snapPreference: Int
                        ): Int =
                            (boxStart + (boxEnd - boxStart) / 2) -
                                (viewStart + (viewEnd - viewStart) / 2)
                    }

                // Make sure to increment the position to make up for the detail header
                binding.detailRecycler.layoutManager?.startSmoothScroll(centerSmoothScroller)

                // If the recyclerview can scroll, its certain that it will have to scroll to
                // correctly center the playing item, so make sure that the Toolbar is lifted in
                // that case.
                binding.detailAppbar.isLifted = binding.detailRecycler.canScroll()
            }
        }
    }

    private fun updateList(list: List<Item>) {
        albumListAdapter.update(list, detailModel.albumInstructions.consume())
    }

    private fun updateSelection(selected: List<Music>) {
        albumListAdapter.setSelected(selected.toSet())
        requireBinding().detailSelectionToolbar.updateSelectionAmount(selected.size)
    }
}

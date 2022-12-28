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
 
package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.material.transition.MaterialSharedAxis
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.recycler.AlbumDetailAdapter
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.canScroll
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ListFragment] that shows information about an [Album].
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumDetailFragment : ListFragment<FragmentDetailBinding>(), AlbumDetailAdapter.Listener {
    private val detailModel: DetailViewModel by activityViewModels()
    // Information about what album to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an album.
    private val args: AlbumDetailFragmentArgs by navArgs()
    private val detailAdapter = AlbumDetailAdapter(this)

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

        binding.detailRecycler.adapter = detailAdapter

        // -- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setAlbumUid(args.albumUid)
        collectImmediately(detailModel.currentAlbum, ::updateAlbum)
        collectImmediately(detailModel.albumList, detailAdapter::submitList)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(navModel.exploreNavigationItem, ::handleNavigation)
        collectImmediately(selectionModel.selected, ::updateSelection)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailToolbar.setOnMenuItemClickListener(null)
        binding.detailRecycler.adapter = null
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

    override fun onRealClick(music: Music) {
        check(music is Song) { "Unexpected datatype: ${music::class.java}" }
        when (Settings(requireContext()).detailPlaybackMode) {
            // "Play from shown item" and "Play from album" functionally have the same
            // behavior since a song can only have one album.
            null,
            MusicMode.ALBUMS -> playbackModel.playFromAlbum(music)
            MusicMode.SONGS -> playbackModel.playFromAll(music)
            MusicMode.ARTISTS -> playbackModel.playFromArtist(music)
            MusicMode.GENRES -> playbackModel.playFromGenre(music)
        }
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        check(item is Song) { "Unexpected datatype: ${item::class.simpleName}" }
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
            val sort = detailModel.albumSort
            unlikelyToBeNull(menu.findItem(sort.mode.itemId)).isChecked = true
            unlikelyToBeNull(menu.findItem(R.id.option_sort_asc)).isChecked = sort.isAscending
            setOnMenuItemClickListener { item ->
                item.isChecked = !item.isChecked
                detailModel.albumSort =
                    if (item.itemId == R.id.option_sort_asc) {
                        sort.withAscending(item.isChecked)
                    } else {
                        sort.withMode(unlikelyToBeNull(Sort.Mode.fromItemId(item.itemId)))
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
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        if (parent is Album && parent == unlikelyToBeNull(detailModel.currentAlbum.value)) {
            detailAdapter.setPlayingItem(song, isPlaying)
        } else {
            // Clear the ViewHolders if the mode isn't ALL_SONGS
            detailAdapter.setPlayingItem(null, isPlaying)
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
                    navModel.finishExploreNavigation()
                } else {
                    logD("Navigating to another album")
                    findNavController()
                        .navigate(AlbumDetailFragmentDirections.actionShowAlbum(item.album.uid))
                }
            }

            // If the album matches, no need to do anything. Otherwise launch a new
            // detail fragment.
            is Album -> {
                if (unlikelyToBeNull(detailModel.currentAlbum.value) == item) {
                    logD("Navigating to the top of this album")
                    binding.detailRecycler.scrollToPosition(0)
                    navModel.finishExploreNavigation()
                } else {
                    logD("Navigating to another album")
                    findNavController()
                        .navigate(AlbumDetailFragmentDirections.actionShowAlbum(item.uid))
                }
            }

            // Always launch a new ArtistDetailFragment.
            is Artist -> {
                logD("Navigating to another artist")
                findNavController()
                    .navigate(AlbumDetailFragmentDirections.actionShowArtist(item.uid))
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

    private fun updateSelection(selected: List<Music>) {
        detailAdapter.setSelectedItems(selected)
        requireBinding().detailSelectionToolbar.updateSelectionAmount(selected.size)
    }
}

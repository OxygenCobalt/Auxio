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

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSmoothScroller
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.recycler.AlbumDetailAdapter
import org.oxycblt.auxio.detail.recycler.SortHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.Header
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.canScroll
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * The [DetailFragment] for an album.
 * @author OxygenCobalt
 */
class AlbumDetailFragment : DetailFragment(), AlbumDetailAdapter.Listener {
    private val args: AlbumDetailFragmentArgs by navArgs()
    private val detailAdapter = AlbumDetailAdapter(this)

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        detailModel.setAlbumId(args.albumId)

        setupToolbar(unlikelyToBeNull(detailModel.currentAlbum.value), R.menu.menu_album_detail) {
            itemId ->
            when (itemId) {
                R.id.action_play_next -> {
                    playbackModel.playNext(unlikelyToBeNull(detailModel.currentAlbum.value))
                    requireContext().showToast(R.string.lbl_queue_added)
                    true
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(unlikelyToBeNull(detailModel.currentAlbum.value))
                    requireContext().showToast(R.string.lbl_queue_added)
                    true
                }
                else -> false
            }
        }

        requireBinding().detailRecycler.apply {
            adapter = detailAdapter
            applySpans { pos ->
                val item = detailAdapter.data.currentList[pos]
                item is Header || item is SortHeader || item is Album
            }
        }

        // -- VIEWMODEL SETUP ---

        detailModel.albumData.observe(viewLifecycleOwner, detailAdapter.data::submitList)
        navModel.exploreNavigationItem.observe(viewLifecycleOwner, ::handleNavigation)
        playbackModel.song.observe(viewLifecycleOwner, ::updateSong)
    }

    override fun onItemClick(item: Item) {
        if (item is Song) {
            playbackModel.playSong(item, PlaybackMode.IN_ALBUM)
        }
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        newMenu(anchor, item)
    }

    override fun onPlayParent() {
        playbackModel.playAlbum(unlikelyToBeNull(detailModel.currentAlbum.value), false)
    }

    override fun onShuffleParent() {
        playbackModel.playAlbum(unlikelyToBeNull(detailModel.currentAlbum.value), true)
    }

    override fun onShowSortMenu(anchor: View) {
        showSortMenu(
            anchor,
            detailModel.albumSort,
            onConfirm = { detailModel.albumSort = it },
            showItem = { it == R.id.option_sort_asc })
    }

    override fun onNavigateToArtist() {
        findNavController()
            .navigate(
                AlbumDetailFragmentDirections.actionShowArtist(
                    unlikelyToBeNull(detailModel.currentAlbum.value).artist.id))
    }

    private fun handleNavigation(item: Music?) {
        val binding = requireBinding()
        when (item) {
            // Songs should be scrolled to if the album matches, or a new detail
            // fragment should be launched otherwise.
            is Song -> {
                if (unlikelyToBeNull(detailModel.currentAlbum.value).id == item.album.id) {
                    logD("Navigating to a song in this album")
                    scrollToItem(item.id)
                    navModel.finishExploreNavigation()
                } else {
                    logD("Navigating to another album")
                    findNavController()
                        .navigate(AlbumDetailFragmentDirections.actionShowAlbum(item.album.id))
                }
            }

            // If the album matches, no need to do anything. Otherwise launch a new
            // detail fragment.
            is Album -> {
                if (unlikelyToBeNull(detailModel.currentAlbum.value).id == item.id) {
                    logD("Navigating to the top of this album")
                    binding.detailRecycler.scrollToPosition(0)
                    navModel.finishExploreNavigation()
                } else {
                    logD("Navigating to another album")
                    findNavController()
                        .navigate(AlbumDetailFragmentDirections.actionShowAlbum(item.id))
                }
            }

            // Always launch a new ArtistDetailFragment.
            is Artist -> {
                logD("Navigating to another artist")
                findNavController()
                    .navigate(AlbumDetailFragmentDirections.actionShowArtist(item.id))
            }
            null -> {}
            else -> logW("Unsupported navigation item ${item::class.java}")
        }
    }

    /** Scroll to an song using its [id]. */
    private fun scrollToItem(id: Long) {
        // Calculate where the item for the currently played song is
        val pos = detailAdapter.data.currentList.indexOfFirst { it.id == id && it is Song }

        if (pos != -1) {
            val binding = requireBinding()
            binding.detailRecycler.post {
                // Make sure to increment the position to make up for the detail header
                binding.detailRecycler.layoutManager?.startSmoothScroll(
                    CenterSmoothScroller(requireContext(), pos))

                // If the recyclerview can scroll, its certain that it will have to scroll to
                // correctly center the playing item, so make sure that the Toolbar is lifted in
                // that case.
                binding.detailAppbar.isLifted = binding.detailRecycler.canScroll()
            }
        }
    }

    /** Updates the queue actions when a song is present or not */
    private fun updateSong(song: Song?) {
        val binding = requireBinding()

        for (item in binding.detailToolbar.menu.children) {
            if (item.itemId == R.id.action_play_next || item.itemId == R.id.action_queue_add) {
                item.isEnabled = song != null
            }
        }

        if (playbackModel.playbackMode.value == PlaybackMode.IN_ALBUM &&
            playbackModel.parent.value?.id == unlikelyToBeNull(detailModel.currentAlbum.value).id) {
            detailAdapter.highlightSong(song, binding.detailRecycler)
        } else {
            // Clear the ViewHolders if the mode isn't ALL_SONGS
            detailAdapter.highlightSong(null, binding.detailRecycler)
        }
    }

    /**
     * [LinearSmoothScroller] subclass that centers the item on the screen instead of snapping to
     * the top or bottom.
     */
    private class CenterSmoothScroller(context: Context, target: Int) :
        LinearSmoothScroller(context) {
        init {
            targetPosition = target
        }

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }
    }
}

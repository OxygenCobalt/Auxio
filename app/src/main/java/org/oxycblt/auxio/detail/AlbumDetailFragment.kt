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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSmoothScroller
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.recycler.AlbumDetailAdapter
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.canScroll
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast

/**
 * The [DetailFragment] for an album.
 * @author OxygenCobalt
 */
class AlbumDetailFragment : DetailFragment() {
    private val args: AlbumDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailModel.setAlbum(args.albumId)

        val detailAdapter = AlbumDetailAdapter(
            playbackModel, detailModel,
            doOnClick = { playbackModel.playSong(it, PlaybackMode.IN_ALBUM) },
            doOnLongClick = { view, data -> newMenu(view, data, ActionMenu.FLAG_IN_ALBUM) }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        setupToolbar(detailModel.curAlbum.value!!, R.menu.menu_album_detail) { itemId ->
            if (itemId == R.id.action_queue_add) {
                playbackModel.playNext(detailModel.curAlbum.value!!)
                requireContext().showToast(R.string.lbl_queue_added)
                true
            } else {
                false
            }
        }

        setupRecycler(detailAdapter) { pos ->
            val item = detailAdapter.currentList[pos]
            item is Header || item is ActionHeader || item is Album
        }

        // -- DETAILVIEWMODEL SETUP ---

        detailModel.albumData.observe(viewLifecycleOwner) { data ->
            detailAdapter.submitList(data)
        }

        detailModel.showMenu.observe(viewLifecycleOwner) { config ->
            if (config != null) {
                showMenu(config) { id ->
                    id == R.id.option_sort_asc
                }
            }
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            when (item) {
                // Songs should be scrolled to if the album matches, or a new detail
                // fragment should be launched otherwise.
                is Song -> {
                    if (detailModel.curAlbum.value!!.id == item.album.id) {
                        scrollToItem(item.id, detailAdapter)

                        detailModel.finishNavToItem()
                    } else {
                        findNavController().navigate(
                            AlbumDetailFragmentDirections.actionShowAlbum(item.album.id)
                        )
                    }
                }

                // If the album matches, no need to do anything. Otherwise launch a new
                // detail fragment.
                is Album -> {
                    if (detailModel.curAlbum.value!!.id == item.id) {
                        binding.detailRecycler.scrollToPosition(0)
                        detailModel.finishNavToItem()
                    } else {
                        findNavController().navigate(
                            AlbumDetailFragmentDirections.actionShowAlbum(item.id)
                        )
                    }
                }

                // Always launch a new ArtistDetailFragment.
                is Artist -> {
                    findNavController().navigate(
                        AlbumDetailFragmentDirections.actionShowArtist(item.id)
                    )
                }

                else -> {
                }
            }
        }

        // --- PLAYBACKVIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (playbackModel.playbackMode.value == PlaybackMode.IN_ALBUM &&
                playbackModel.parent.value?.id == detailModel.curAlbum.value!!.id
            ) {
                detailAdapter.highlightSong(song, binding.detailRecycler)
            } else {
                // Clear the viewholders if the mode isn't ALL_SONGS
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    /**
     * Scroll to an song using its [id].
     */
    private fun scrollToItem(id: Long, adapter: AlbumDetailAdapter) {
        // Calculate where the item for the currently played song is
        val pos = adapter.currentList.indexOfFirst { it.id == id && it is Song }

        if (pos != -1) {
            binding.detailRecycler.post {
                // Make sure to increment the position to make up for the detail header
                binding.detailRecycler.layoutManager?.startSmoothScroll(
                    CenterSmoothScroller(requireContext(), pos)
                )

                // If the recyclerview can scroll, its certain that it will have to scroll to
                // correctly center the playing item, so make sure that the Toolbar is lifted in
                // that case.
                binding.detailAppbar.isLifted = binding.detailRecycler.canScroll()
            }
        }
    }

    /**
     * [LinearSmoothScroller] subclass that centers the item on the screen instead of
     * snapping to the top or bottom.
     */
    private class CenterSmoothScroller(
        context: Context,
        target: Int
    ) : LinearSmoothScroller(context) {
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

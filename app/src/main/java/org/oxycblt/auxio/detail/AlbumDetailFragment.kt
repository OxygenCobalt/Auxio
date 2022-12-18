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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.children
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
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A fragment that shows information for a particular [Album].
 * @author OxygenCobalt
 */
class AlbumDetailFragment : ListFragment<FragmentDetailBinding>() {
    private val detailModel: DetailViewModel by activityViewModels()

    private val args: AlbumDetailFragmentArgs by navArgs()
    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }

    private val detailAdapter =
        AlbumDetailAdapter(
            AlbumDetailAdapter.Callback(
                ::handleClick,
                ::handleOpenItemMenu,
                ::handleSelect,
                ::handlePlay,
                ::handleShuffle,
                ::handleOpenSortMenu,
                ::handleArtistNavigation))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentDetailBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        setupSelectionToolbar(binding.detailSelectionToolbar)

        binding.detailToolbar.apply {
            inflateMenu(R.menu.menu_album_detail)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener {
                handleDetailMenuItem(it)
                true
            }
        }

        binding.detailRecycler.adapter = detailAdapter

        // -- VIEWMODEL SETUP ---

        detailModel.setAlbumUid(args.albumUid)

        collectImmediately(detailModel.currentAlbum, ::updateItem)
        collectImmediately(detailModel.albumData, detailAdapter::submitList)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(navModel.exploreNavigationItem, ::handleNavigation)
        collectImmediately(selectionModel.selected, ::handleSelection)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailRecycler.adapter = null
    }

    override fun onRealClick(music: Music) {
        check(music is Song) { "Unexpected datatype: ${music::class.java}"}
        when (settings.detailPlaybackMode) {
            null,
            MusicMode.ALBUMS -> playbackModel.playFromAlbum(music)
            MusicMode.SONGS -> playbackModel.playFromAll(music)
            MusicMode.ARTISTS -> playbackModel.playFromArtist(music)
            else -> error("Unexpected playback mode: ${settings.detailPlaybackMode}")
        }
    }

    private fun handleOpenItemMenu(item: Item, anchor: View) {
        check(item is Song) { "Unexpected datatype: ${item::class.simpleName}" }
        openMusicMenu(anchor, R.menu.menu_album_song_actions, item)
    }

    private fun handlePlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    private fun handleShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    private fun handleOpenSortMenu(anchor: View) {
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

    private fun handleArtistNavigation() {
        navModel.exploreNavigateTo(unlikelyToBeNull(detailModel.currentAlbum.value).artists)
    }

    private fun handleDetailMenuItem(item: MenuItem) {
        when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(unlikelyToBeNull(detailModel.currentAlbum.value))
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(unlikelyToBeNull(detailModel.currentAlbum.value))
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_go_artist -> {
                handleArtistNavigation()
            }
        }
    }

    private fun updateItem(album: Album?) {
        if (album == null) {
            findNavController().navigateUp()
            return
        }

        requireBinding().detailToolbar.title = album.resolveName(requireContext())
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        val binding = requireBinding()

        for (item in binding.detailToolbar.menu.children) {
            // If there is no playback going in, any queue additions will be wiped as soon as
            // something is played. Disable these actions when playback is going on so that
            // it isn't possible to add anything during that time.
            if (item.itemId == R.id.action_play_next || item.itemId == R.id.action_queue_add) {
                item.isEnabled = song != null
            }
        }

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
                    scrollToItem(item)
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

    /** Scroll to a [song]. */
    private fun scrollToItem(song: Song) {
        // Calculate where the item for the currently played song is
        val pos = detailModel.albumData.value.indexOf(song)

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

    private fun handleSelection(selected: List<Music>) {
        detailAdapter.setSelectedItems(selected)
        requireBinding().detailSelectionToolbar.updateSelectionAmount(selected.size)
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
        ): Int = (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    }

}

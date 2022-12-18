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
import com.google.android.material.transition.MaterialSharedAxis
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.recycler.ArtistDetailAdapter
import org.oxycblt.auxio.detail.recycler.DetailAdapter
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
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A fragment that shows information for a particular [Artist].
 * @author OxygenCobalt
 */
class ArtistDetailFragment : ListFragment<FragmentDetailBinding>() {
    private val detailModel: DetailViewModel by activityViewModels()

    private val args: ArtistDetailFragmentArgs by navArgs()
    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }

    private val detailAdapter =
        ArtistDetailAdapter(
            DetailAdapter.Callback(
                ::handleClick,
                ::handleOpenItemMenu,
                ::handleSelect,
                ::handlePlay,
                ::handleShuffle,
                ::handleOpenSortMenu))

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
            inflateMenu(R.menu.menu_genre_artist_detail)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener {
                handleDetailMenuItem(it)
                true
            }
        }

        binding.detailRecycler.adapter = detailAdapter

        // --- VIEWMODEL SETUP ---

        detailModel.setArtistUid(args.artistUid)

        collectImmediately(detailModel.currentArtist, ::updateItem)
        collectImmediately(detailModel.artistData, detailAdapter::submitList)
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
        when (music) {
            is Song -> {
                when (settings.detailPlaybackMode) {
                    null ->
                        playbackModel.playFromArtist(
                            music, unlikelyToBeNull(detailModel.currentArtist.value))
                    MusicMode.SONGS -> playbackModel.playFromAll(music)
                    MusicMode.ALBUMS -> playbackModel.playFromAlbum(music)
                    MusicMode.ARTISTS -> playbackModel.playFromArtist(music)
                    else -> error("Unexpected playback mode: ${settings.detailPlaybackMode}")
                }
            }
            is Album -> navModel.exploreNavigateTo(music)
            else -> error("Unexpected datatype: ${music::class.simpleName}")
        }
    }

    private fun handleOpenItemMenu(item: Item, anchor: View) {
        when (item) {
            is Song -> openMusicMenu(anchor, R.menu.menu_artist_song_actions, item)
            is Album -> openMusicMenu(anchor, R.menu.menu_artist_album_actions, item)
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    private fun handlePlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentArtist.value))
    }

    private fun handleShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentArtist.value))
    }

    private fun handleOpenSortMenu(anchor: View) {
        openMenu(anchor, R.menu.menu_artist_sort) {
            val sort = detailModel.artistSort
            unlikelyToBeNull(menu.findItem(sort.mode.itemId)).isChecked = true
            unlikelyToBeNull(menu.findItem(R.id.option_sort_asc)).isChecked = sort.isAscending
            setOnMenuItemClickListener { item ->
                item.isChecked = !item.isChecked

                detailModel.artistSort =
                    if (item.itemId == R.id.option_sort_asc) {
                        sort.withAscending(item.isChecked)
                    } else {
                        sort.withMode(unlikelyToBeNull(Sort.Mode.fromItemId(item.itemId)))
                    }

                true
            }
        }
    }

    private fun handleDetailMenuItem(item: MenuItem) {
        when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(unlikelyToBeNull(detailModel.currentArtist.value))
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(unlikelyToBeNull(detailModel.currentArtist.value))
                requireContext().showToast(R.string.lng_queue_added)
            }
        }
    }

    private fun updateItem(artist: Artist?) {
        if (artist == null) {
            findNavController().navigateUp()
            return
        }

        requireBinding().detailToolbar.title = artist.resolveName(requireContext())
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        var item: Item? = null

        if (parent is Album) {
            item = parent
        }

        if (parent is Artist && parent == unlikelyToBeNull(detailModel.currentArtist.value)) {
            item = song
        }

        detailAdapter.setPlayingItem(item, isPlaying)
    }

    private fun handleNavigation(item: Music?) {
        val binding = requireBinding()

        when (item) {
            is Song -> {
                logD("Navigating to another album")
                findNavController()
                    .navigate(ArtistDetailFragmentDirections.actionShowAlbum(item.album.uid))
            }
            is Album -> {
                logD("Navigating to another album")
                findNavController()
                    .navigate(ArtistDetailFragmentDirections.actionShowAlbum(item.uid))
            }
            is Artist -> {
                if (item.uid == detailModel.currentArtist.value?.uid) {
                    logD("Navigating to the top of this artist")
                    binding.detailRecycler.scrollToPosition(0)
                    navModel.finishExploreNavigation()
                } else {
                    logD("Navigating to another artist")
                    findNavController()
                        .navigate(ArtistDetailFragmentDirections.actionShowArtist(item.uid))
                }
            }
            null -> {}
            else -> error("Unexpected datatype: ${item::class.java}")
        }
    }

    private fun handleSelection(selected: List<Music>) {
        detailAdapter.setSelectedItems(selected)
        requireBinding().detailSelectionToolbar.updateSelectionAmount(selected.size)
    }
}

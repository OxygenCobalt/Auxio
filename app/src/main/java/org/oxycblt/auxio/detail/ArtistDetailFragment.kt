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
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ListFragment] that shows information about an [Artist].
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistDetailFragment : ListFragment<FragmentDetailBinding>(), DetailAdapter.Listener {
    private val detailModel: DetailViewModel by activityViewModels()
    // Information about what artist to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an artist.
    private val args: ArtistDetailFragmentArgs by navArgs()
    private val detailAdapter = ArtistDetailAdapter(this)

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

        // --- UI SETUP ---
        binding.detailToolbar.apply {
            inflateMenu(R.menu.menu_genre_artist_detail)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener(this@ArtistDetailFragment)
        }

        binding.detailRecycler.adapter = detailAdapter

        // --- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setArtistUid(args.artistUid)
        collectImmediately(detailModel.currentArtist, ::updateItem)
        collectImmediately(detailModel.artistList, detailAdapter::submitList)
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

        val currentArtist = unlikelyToBeNull(detailModel.currentArtist.value)
        return when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(currentArtist)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(currentArtist)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            else -> false
        }
    }

    override fun onRealClick(music: Music) {
        when (music) {
            is Song -> {
                when (Settings(requireContext()).detailPlaybackMode) {
                    // When configured to play from the selected item, we already have an Artist
                    // to play from.
                    null ->
                        playbackModel.playFromArtist(
                            music, unlikelyToBeNull(detailModel.currentArtist.value))
                    MusicMode.SONGS -> playbackModel.playFromAll(music)
                    MusicMode.ALBUMS -> playbackModel.playFromAlbum(music)
                    MusicMode.ARTISTS -> playbackModel.playFromArtist(music)
                    MusicMode.GENRES -> playbackModel.playFromGenre(music)
                }
            }
            is Album -> navModel.exploreNavigateTo(music)
            else -> error("Unexpected datatype: ${music::class.simpleName}")
        }
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        when (item) {
            is Song -> openMusicMenu(anchor, R.menu.menu_artist_song_actions, item)
            is Album -> openMusicMenu(anchor, R.menu.menu_artist_album_actions, item)
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    override fun onPlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentArtist.value))
    }

    override fun onShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentArtist.value))
    }

    override fun onOpenSortMenu(anchor: View) {
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

    private fun updateItem(artist: Artist?) {
        if (artist == null) {
            // Artist we were showing no longer exists.
            findNavController().navigateUp()
            return
        }

        requireBinding().detailToolbar.title = artist.resolveName(requireContext())
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        val currentArtist = unlikelyToBeNull(detailModel.currentArtist.value)
        val playingItem =
            when (parent) {
                // Always highlight a playing album if it's from this artist.
                is Album -> parent
                // If the parent is the artist itself, use the currently playing song.
                currentArtist -> song
                // Nothing is playing from this artist.
                else -> null
            }

        detailAdapter.setPlayingItem(playingItem, isPlaying)
    }

    private fun handleNavigation(item: Music?) {
        val binding = requireBinding()

        when (item) {
            // Songs should be shown in their album, not in their artist.
            is Song -> {
                logD("Navigating to another album")
                findNavController()
                    .navigate(ArtistDetailFragmentDirections.actionShowAlbum(item.album.uid))
            }
            // Launch a new detail view for an album, even if it is part of
            // this artist.
            is Album -> {
                logD("Navigating to another album")
                findNavController()
                    .navigate(ArtistDetailFragmentDirections.actionShowAlbum(item.uid))
            }
            // If the artist that should be navigated to is this artist, then
            // scroll back to the top. Otherwise launch a new detail view.
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

    private fun updateSelection(selected: List<Music>) {
        detailAdapter.setSelectedItems(selected)
        requireBinding().detailSelectionToolbar.updateSelectionAmount(selected.size)
    }
}

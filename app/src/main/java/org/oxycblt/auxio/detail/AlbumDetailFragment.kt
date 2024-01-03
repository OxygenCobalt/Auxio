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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.header.AlbumDetailHeaderAdapter
import org.oxycblt.auxio.detail.list.AlbumDetailListAdapter
import org.oxycblt.auxio.detail.list.DetailListAdapter
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.PlaylistMessage
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.playback.PlaybackDecision
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.canScroll
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.overrideOnOverflowMenuClick
import org.oxycblt.auxio.util.setFullWidthLookup
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

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
    override val listModel: ListViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()

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
        binding.detailNormalToolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            overrideOnOverflowMenuClick {
                listModel.openMenu(
                    R.menu.detail_album, unlikelyToBeNull(detailModel.currentAlbum.value))
            }
        }

        binding.detailRecycler.apply {
            adapter = ConcatAdapter(albumHeaderAdapter, albumListAdapter)
            (layoutManager as GridLayoutManager).setFullWidthLookup {
                if (it != 0) {
                    val item = detailModel.albumSongList.value[it - 1]
                    item is Divider || item is Header || item is Disc
                } else {
                    true
                }
            }
        }

        // -- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setAlbum(args.albumUid)
        collectImmediately(detailModel.currentAlbum, ::updateAlbum)
        collectImmediately(detailModel.albumSongList, ::updateList)
        collect(detailModel.toShow.flow, ::handleShow)
        collect(listModel.menu.flow, ::handleMenu)
        collectImmediately(listModel.selected, ::updateSelection)
        collect(musicModel.playlistDecision.flow, ::handlePlaylistDecision)
        collect(musicModel.playlistMessage.flow, ::handlePlaylistMessage)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(playbackModel.playbackDecision.flow, ::handlePlaybackDecision)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailNormalToolbar.setOnMenuItemClickListener(null)
        binding.detailRecycler.adapter = null
        // Avoid possible race conditions that could cause a bad replace instruction to be consumed
        // during list initialization and crash the app. Could happen if the user is fast enough.
        detailModel.albumSongInstructions.consume()
    }

    override fun onRealClick(item: Song) {
        playbackModel.play(item, detailModel.playInAlbumWith)
    }

    override fun onOpenMenu(item: Song) {
        listModel.openMenu(R.menu.album_song, item, detailModel.playInAlbumWith)
    }

    override fun onPlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    override fun onShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    override fun onOpenSortMenu() {
        findNavController().navigateSafe(AlbumDetailFragmentDirections.sort())
    }

    override fun onNavigateToParentArtist() {
        detailModel.showArtist(unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    private fun updateAlbum(album: Album?) {
        if (album == null) {
            logD("No album to show, navigating away")
            findNavController().navigateUp()
            return
        }
        requireBinding().detailNormalToolbar.title = album.name.resolve(requireContext())
        albumHeaderAdapter.setParent(album)
    }

    private fun updateList(list: List<Item>) {
        albumListAdapter.update(list, detailModel.albumSongInstructions.consume())
    }

    private fun handleShow(show: Show?) {
        val binding = requireBinding()
        when (show) {
            is Show.SongDetails -> {
                logD("Navigating to ${show.song}")
                findNavController()
                    .navigateSafe(AlbumDetailFragmentDirections.showSong(show.song.uid))
            }

            // Songs should be scrolled to if the album matches, or a new detail
            // fragment should be launched otherwise.
            is Show.SongAlbumDetails -> {
                if (unlikelyToBeNull(detailModel.currentAlbum.value) == show.song.album) {
                    logD("Navigating to a ${show.song} in this album")
                    scrollToAlbumSong(show.song)
                    detailModel.toShow.consume()
                } else {
                    logD("Navigating to the album of ${show.song}")
                    findNavController()
                        .navigateSafe(AlbumDetailFragmentDirections.showAlbum(show.song.album.uid))
                }
            }

            // If the album matches, no need to do anything. Otherwise launch a new
            // detail fragment.
            is Show.AlbumDetails -> {
                if (unlikelyToBeNull(detailModel.currentAlbum.value) == show.album) {
                    logD("Navigating to the top of this album")
                    binding.detailRecycler.scrollToPosition(0)
                    detailModel.toShow.consume()
                } else {
                    logD("Navigating to ${show.album}")
                    findNavController()
                        .navigateSafe(AlbumDetailFragmentDirections.showAlbum(show.album.uid))
                }
            }
            is Show.ArtistDetails -> {
                logD("Navigating to ${show.artist}")
                findNavController()
                    .navigateSafe(AlbumDetailFragmentDirections.showArtist(show.artist.uid))
            }
            is Show.SongArtistDecision -> {
                logD("Navigating to artist choices for ${show.song}")
                findNavController()
                    .navigateSafe(AlbumDetailFragmentDirections.showArtistChoices(show.song.uid))
            }
            is Show.AlbumArtistDecision -> {
                logD("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(AlbumDetailFragmentDirections.showArtistChoices(show.album.uid))
            }
            is Show.GenreDetails,
            is Show.PlaylistDetails -> {
                error("Unexpected show command $show")
            }
            null -> {}
        }
    }

    private fun handleMenu(menu: Menu?) {
        if (menu == null) return
        val directions =
            when (menu) {
                is Menu.ForSong -> AlbumDetailFragmentDirections.openSongMenu(menu.parcel)
                is Menu.ForAlbum -> AlbumDetailFragmentDirections.openAlbumMenu(menu.parcel)
                is Menu.ForSelection -> AlbumDetailFragmentDirections.openSelectionMenu(menu.parcel)
                is Menu.ForArtist,
                is Menu.ForGenre,
                is Menu.ForPlaylist -> error("Unexpected menu $menu")
            }
        findNavController().navigateSafe(directions)
    }

    private fun updateSelection(selected: List<Music>) {
        albumListAdapter.setSelected(selected.toSet())

        val binding = requireBinding()
        if (selected.isNotEmpty()) {
            binding.detailSelectionToolbar.title = getString(R.string.fmt_selected, selected.size)
            binding.detailToolbar.setVisible(R.id.detail_selection_toolbar)
        } else {
            binding.detailToolbar.setVisible(R.id.detail_normal_toolbar)
        }
    }

    private fun handlePlaylistDecision(decision: PlaylistDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaylistDecision.Add -> {
                    logD("Adding ${decision.songs.size} songs to a playlist")
                    AlbumDetailFragmentDirections.addToPlaylist(
                        decision.songs.map { it.uid }.toTypedArray())
                }
                is PlaylistDecision.New,
                is PlaylistDecision.Import,
                is PlaylistDecision.Rename,
                is PlaylistDecision.Delete,
                is PlaylistDecision.Export -> error("Unexpected playlist decision $decision")
            }
        findNavController().navigateSafe(directions)
    }

    private fun handlePlaylistMessage(message: PlaylistMessage?) {
        if (message == null) return
        requireContext().showToast(message.stringRes)
        musicModel.playlistMessage.consume()
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        albumListAdapter.setPlaying(
            song.takeIf { parent == detailModel.currentAlbum.value }, isPlaying)
    }

    private fun handlePlaybackDecision(decision: PlaybackDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaybackDecision.PlayFromArtist -> {
                    logD("Launching play from artist dialog for $decision")
                    AlbumDetailFragmentDirections.playFromArtist(decision.song.uid)
                }
                is PlaybackDecision.PlayFromGenre -> {
                    logD("Launching play from artist dialog for $decision")
                    AlbumDetailFragmentDirections.playFromGenre(decision.song.uid)
                }
            }
        findNavController().navigateSafe(directions)
    }

    private fun scrollToAlbumSong(song: Song) {
        // Calculate where the item for the currently played song is
        val pos = detailModel.albumSongList.value.indexOf(song)

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
                        ) =
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
}

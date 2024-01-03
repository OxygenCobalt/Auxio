/*
 * Copyright (c) 2021 Auxio Project
 * ArtistDetailFragment.kt is part of Auxio.
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
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.header.ArtistDetailHeaderAdapter
import org.oxycblt.auxio.detail.header.DetailHeaderAdapter
import org.oxycblt.auxio.detail.list.ArtistDetailListAdapter
import org.oxycblt.auxio.detail.list.DetailListAdapter
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.PlaylistMessage
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackDecision
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.overrideOnOverflowMenuClick
import org.oxycblt.auxio.util.setFullWidthLookup
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ListFragment] that shows information about an [Artist].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class ArtistDetailFragment :
    ListFragment<Music, FragmentDetailBinding>(),
    DetailHeaderAdapter.Listener,
    DetailListAdapter.Listener<Music> {
    private val detailModel: DetailViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    // Information about what artist to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an artist.
    private val args: ArtistDetailFragmentArgs by navArgs()
    private val artistHeaderAdapter = ArtistDetailHeaderAdapter(this)
    private val artistListAdapter = ArtistDetailListAdapter(this)

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
        binding.detailNormalToolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener(this@ArtistDetailFragment)
            overrideOnOverflowMenuClick {
                listModel.openMenu(
                    R.menu.detail_parent, unlikelyToBeNull(detailModel.currentArtist.value))
            }
        }

        binding.detailRecycler.apply {
            adapter = ConcatAdapter(artistHeaderAdapter, artistListAdapter)
            (layoutManager as GridLayoutManager).setFullWidthLookup {
                if (it != 0) {
                    val item =
                        detailModel.artistSongList.value.getOrElse(it - 1) {
                            return@setFullWidthLookup false
                        }
                    item is Divider || item is Header
                } else {
                    true
                }
            }
        }

        // --- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setArtist(args.artistUid)
        collectImmediately(detailModel.currentArtist, ::updateArtist)
        collectImmediately(detailModel.artistSongList, ::updateList)
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
        detailModel.artistSongInstructions.consume()
    }

    override fun onRealClick(item: Music) {
        when (item) {
            is Album -> detailModel.showAlbum(item)
            is Song -> playbackModel.play(item, detailModel.playInArtistWith)
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    override fun onOpenMenu(item: Music) {
        when (item) {
            is Song -> listModel.openMenu(R.menu.artist_song, item, detailModel.playInArtistWith)
            is Album -> listModel.openMenu(R.menu.artist_album, item)
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    override fun onPlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentArtist.value))
    }

    override fun onShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentArtist.value))
    }

    override fun onOpenSortMenu() {
        findNavController().navigateSafe(ArtistDetailFragmentDirections.sort())
    }

    private fun updateArtist(artist: Artist?) {
        if (artist == null) {
            logD("No artist to show, navigating away")
            findNavController().navigateUp()
            return
        }
        requireBinding().detailNormalToolbar.title = artist.name.resolve(requireContext())
        artistHeaderAdapter.setParent(artist)
    }

    private fun updateList(list: List<Item>) {
        artistListAdapter.update(list, detailModel.artistSongInstructions.consume())
    }

    private fun handleShow(show: Show?) {
        val binding = requireBinding()
        when (show) {
            is Show.SongDetails -> {
                logD("Navigating to ${show.song}")
                findNavController()
                    .navigateSafe(ArtistDetailFragmentDirections.showSong(show.song.uid))
            }

            // Songs should be shown in their album, not in their artist.
            is Show.SongAlbumDetails -> {
                logD("Navigating to the album of ${show.song}")
                findNavController()
                    .navigateSafe(ArtistDetailFragmentDirections.showAlbum(show.song.album.uid))
            }

            // Launch a new detail view for an album, even if it is part of
            // this artist.
            is Show.AlbumDetails -> {
                logD("Navigating to ${show.album}")
                findNavController()
                    .navigateSafe(ArtistDetailFragmentDirections.showAlbum(show.album.uid))
            }

            // If the artist that should be navigated to is this artist, then
            // scroll back to the top. Otherwise launch a new detail view.
            is Show.ArtistDetails -> {
                if (show.artist == detailModel.currentArtist.value) {
                    logD("Navigating to the top of this artist")
                    binding.detailRecycler.scrollToPosition(0)
                    detailModel.toShow.consume()
                } else {
                    logD("Navigating to ${show.artist}")
                    findNavController()
                        .navigateSafe(ArtistDetailFragmentDirections.showArtist(show.artist.uid))
                }
            }
            is Show.SongArtistDecision -> {
                logD("Navigating to artist choices for ${show.song}")
                findNavController()
                    .navigateSafe(ArtistDetailFragmentDirections.showArtistChoices(show.song.uid))
            }
            is Show.AlbumArtistDecision -> {
                logD("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(ArtistDetailFragmentDirections.showArtistChoices(show.album.uid))
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
                is Menu.ForSong -> ArtistDetailFragmentDirections.openSongMenu(menu.parcel)
                is Menu.ForAlbum -> ArtistDetailFragmentDirections.openAlbumMenu(menu.parcel)
                is Menu.ForArtist -> ArtistDetailFragmentDirections.openArtistMenu(menu.parcel)
                is Menu.ForSelection ->
                    ArtistDetailFragmentDirections.openSelectionMenu(menu.parcel)
                is Menu.ForGenre,
                is Menu.ForPlaylist -> error("Unexpected menu $menu")
            }
        findNavController().navigateSafe(directions)
    }

    private fun updateSelection(selected: List<Music>) {
        artistListAdapter.setSelected(selected.toSet())

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
                    ArtistDetailFragmentDirections.addToPlaylist(
                        decision.songs.map { it.uid }.toTypedArray())
                }
                is PlaylistDecision.New,
                is PlaylistDecision.Import,
                is PlaylistDecision.Rename,
                is PlaylistDecision.Export,
                is PlaylistDecision.Delete -> error("Unexpected playlist decision $decision")
            }
        findNavController().navigateSafe(directions)
    }

    private fun handlePlaylistMessage(message: PlaylistMessage?) {
        if (message == null) return
        requireContext().showToast(message.stringRes)
        musicModel.playlistMessage.consume()
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        val currentArtist = unlikelyToBeNull(detailModel.currentArtist.value)
        val playingItem =
            when (parent) {
                // Always highlight a playing album if it's from this artist, and if the currently
                // playing song is contained within.
                is Album -> parent.takeIf { song?.album == it }
                // If the parent is the artist itself, use the currently playing song.
                currentArtist -> song
                // Nothing is playing from this artist.
                else -> null
            }
        artistListAdapter.setPlaying(playingItem, isPlaying)
    }

    private fun handlePlaybackDecision(decision: PlaybackDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaybackDecision.PlayFromArtist ->
                    error("Unexpected playback decision $decision")
                is PlaybackDecision.PlayFromGenre -> {
                    logD("Launching play from artist dialog for $decision")
                    ArtistDetailFragmentDirections.playFromGenre(decision.song.uid)
                }
            }
        findNavController().navigateSafe(directions)
    }
}

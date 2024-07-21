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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSmoothScroller
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.list.AlbumDetailListAdapter
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.PlaylistMessage
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.PlaybackDecision
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ListFragment] that shows information about an [Album].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class AlbumDetailFragment : DetailFragment<Album, Song>() {
    // Information about what album to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an album.
    private val args: AlbumDetailFragmentArgs by navArgs()
    private val albumListAdapter = AlbumDetailListAdapter(this)

    override fun getDetailListAdapter() = albumListAdapter

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

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
        // Avoid possible race conditions that could cause a bad replace instruction to be consumed
        // during list initialization and crash the app. Could happen if the user is fast enough.
        detailModel.albumSongInstructions.consume()
    }

    override fun onRealClick(item: Song) {
        playbackModel.play(item, detailModel.playInAlbumWith)
    }

    override fun onOpenParentMenu() {
        listModel.openMenu(R.menu.detail_album, unlikelyToBeNull(detailModel.currentAlbum.value))
    }

    override fun onOpenMenu(item: Song) {
        listModel.openMenu(R.menu.album_song, item, detailModel.playInAlbumWith)
    }

    override fun onOpenSortMenu() {
        findNavController().navigateSafe(AlbumDetailFragmentDirections.sort())
    }

    private fun updateAlbum(album: Album?) {
        if (album == null) {
            logD("No album to show, navigating away")
            findNavController().navigateUp()
            return
        }

        val binding = requireBinding()
        val context = requireContext()
        val name = album.name.resolve(context)

        binding.detailToolbarTitle.text = name
        binding.detailCover.bind(album)
        // The type text depends on the release type (Album, EP, Single, etc.)
        binding.detailType.text = getString(album.releaseType.stringRes)
        binding.detailName.text = name
        // Artist name maps to the subhead text
        binding.detailSubhead.apply {
            text = album.artists.resolveNames(context)

            // Add a QoL behavior where navigation to the artist will occur if the artist
            // name is pressed.
            setOnClickListener {
                detailModel.showArtist(unlikelyToBeNull(detailModel.currentAlbum.value))
            }
        }

        // Date, song count, and duration map to the info text
        binding.detailInfo.apply {
            // Fall back to a friendlier "No date" text if the album doesn't have date information
            val date = album.dates?.resolveDate(context) ?: context.getString(R.string.def_date)
            val songCount = context.getPlural(R.plurals.fmt_song_count, album.songs.size)
            val duration = album.durationMs.formatDurationMs(true)
            text = context.getString(R.string.fmt_three, date, songCount, duration)
        }

        binding.detailPlayButton?.setOnClickListener {
            playbackModel.play(unlikelyToBeNull(detailModel.currentAlbum.value))
        }
        binding.detailShuffleButton?.setOnClickListener {
            playbackModel.shuffle(unlikelyToBeNull(detailModel.currentAlbum.value))
        }
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
            // RecyclerView will scroll assuming it has the total height of the screen (i.e a
            // collapsed appbar), so we need to collapse the appbar if that's the case.
            binding.detailAppbar.setExpanded(false)
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
            }
        }
    }
}

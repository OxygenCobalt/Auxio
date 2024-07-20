/*
 * Copyright (c) 2021 Auxio Project
 * GenreDetailFragment.kt is part of Auxio.
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
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.list.GenreDetailListAdapter
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.PlaylistMessage
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackDecision
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ListFragment] that shows information for a particular [Genre].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class GenreDetailFragment : DetailFragment<Genre, Music>() {
    // Information about what genre to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an genre.
    private val args: GenreDetailFragmentArgs by navArgs()
    private val genreListAdapter = GenreDetailListAdapter(this)

    override fun getDetailListAdapter() = genreListAdapter

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setGenre(args.genreUid)
        collectImmediately(detailModel.currentGenre, ::updateGenre)
        collectImmediately(detailModel.genreSongList, ::updateList)
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
        detailModel.genreSongInstructions.consume()
    }

    override fun onRealClick(item: Music) {
        when (item) {
            is Artist -> detailModel.showArtist(item)
            is Song -> playbackModel.play(item, detailModel.playInGenreWith)
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    override fun onOpenParentMenu() {
        listModel.openMenu(R.menu.detail_parent, unlikelyToBeNull(detailModel.currentGenre.value))
    }

    override fun onOpenMenu(item: Music) {
        when (item) {
            is Artist -> listModel.openMenu(R.menu.parent, item)
            is Song -> listModel.openMenu(R.menu.song, item, detailModel.playInGenreWith)
            else -> error("Unexpected datatype: ${item::class.simpleName}")
        }
    }

    override fun onOpenSortMenu() {
        findNavController().navigateSafe(GenreDetailFragmentDirections.sort())
    }

    private fun updateGenre(genre: Genre?) {
        if (genre == null) {
            logD("No genre to show, navigating away")
            findNavController().navigateUp()
            return
        }
        val binding = requireBinding()
        val context = requireContext()
        val name = genre.name.resolve(context)
        binding.detailToolbarTitle.text = name
        binding.detailCover.bind(genre)
        binding.detailType.text = context.getString(R.string.lbl_genre)
        binding.detailName.text = genre.name.resolve(context)
        // Nothing about a genre is applicable to the sub-head text.
        binding.detailSubhead.isVisible = false
        // The song and artist count of the genre maps to the info text.
        binding.detailInfo.text =
            context.getString(
                R.string.fmt_two,
                context.getPlural(R.plurals.fmt_artist_count, genre.artists.size),
                context.getPlural(R.plurals.fmt_song_count, genre.songs.size))
        binding.detailPlayButton?.setOnClickListener {
            playbackModel.play(unlikelyToBeNull(detailModel.currentGenre.value))
        }
        binding.detailShuffleButton?.setOnClickListener {
            playbackModel.shuffle(unlikelyToBeNull(detailModel.currentGenre.value))
        }
    }

    private fun updateList(list: List<Item>) {
        genreListAdapter.update(list, detailModel.genreSongInstructions.consume())
    }

    private fun handleShow(show: Show?) {
        when (show) {
            is Show.SongDetails -> {
                logD("Navigating to ${show.song}")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.showSong(show.song.uid))
            }

            // Songs should be scrolled to if the album matches, or a new detail
            // fragment should be launched otherwise.
            is Show.SongAlbumDetails -> {
                logD("Navigating to the album of ${show.song}")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.showAlbum(show.song.album.uid))
            }

            // If the album matches, no need to do anything. Otherwise launch a new
            // detail fragment.
            is Show.AlbumDetails -> {
                logD("Navigating to ${show.album}")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.showAlbum(show.album.uid))
            }

            // Always launch a new ArtistDetailFragment.
            is Show.ArtistDetails -> {
                logD("Navigating to ${show.artist}")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.showArtist(show.artist.uid))
            }
            is Show.SongArtistDecision -> {
                logD("Navigating to artist choices for ${show.song}")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.showArtistChoices(show.song.uid))
            }
            is Show.AlbumArtistDecision -> {
                logD("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(GenreDetailFragmentDirections.showArtistChoices(show.album.uid))
            }
            is Show.GenreDetails -> {
                logD("Navigated to this genre")
                detailModel.toShow.consume()
            }
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
                is Menu.ForSong -> GenreDetailFragmentDirections.openSongMenu(menu.parcel)
                is Menu.ForArtist -> GenreDetailFragmentDirections.openArtistMenu(menu.parcel)
                is Menu.ForGenre -> GenreDetailFragmentDirections.openGenreMenu(menu.parcel)
                is Menu.ForSelection -> GenreDetailFragmentDirections.openSelectionMenu(menu.parcel)
                is Menu.ForAlbum,
                is Menu.ForPlaylist -> error("Unexpected menu $menu")
            }
        findNavController().navigateSafe(directions)
    }

    private fun updateSelection(selected: List<Music>) {
        genreListAdapter.setSelected(selected.toSet())

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
                    GenreDetailFragmentDirections.addToPlaylist(
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
        val currentGenre = unlikelyToBeNull(detailModel.currentGenre.value)
        val playingItem =
            when (parent) {
                // Always highlight a playing artist if it's from this genre, and if the currently
                // playing song is contained within.
                is Artist -> parent.takeIf { song?.run { artists.contains(it) } ?: false }
                // If the parent is the artist itself, use the currently playing song.
                currentGenre -> song
                // Nothing is playing from this artist.
                else -> null
            }
        genreListAdapter.setPlaying(playingItem, isPlaying)
    }

    private fun handlePlaybackDecision(decision: PlaybackDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaybackDecision.PlayFromArtist -> {
                    logD("Launching play from artist dialog for $decision")
                    GenreDetailFragmentDirections.playFromArtist(decision.song.uid)
                }
                is PlaybackDecision.PlayFromGenre -> error("Unexpected playback decision $decision")
            }
        findNavController().navigateSafe(directions)
    }
}

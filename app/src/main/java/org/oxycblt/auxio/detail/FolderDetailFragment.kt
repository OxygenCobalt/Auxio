/*
 * Copyright (c) 2024 Auxio Project
 * FolderDetailFragment.kt is part of Auxio.
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
import org.oxycblt.auxio.detail.list.DetailListAdapter
import org.oxycblt.auxio.detail.list.FolderDetailListAdapter
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.PlaylistMessage
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.playback.PlaybackDecision
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull
import org.oxycblt.musikr.Folder
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song
import timber.log.Timber as L

/**
 * A [ListFragment] that shows information for a particular [Folder].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class FolderDetailFragment : DetailFragment<Folder, Song>(), DetailListAdapter.Listener<Song> {
    // Information about what folder to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an folder.
    private val args: FolderDetailFragmentArgs by navArgs()
    private val folderListAdapter = FolderDetailListAdapter(this)

    override fun getDetailListAdapter() = folderListAdapter

    override fun getToolbarParent() = detailModel.currentFolder.value

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setFolder(args.folderUid)
        collectImmediately(detailModel.currentFolder, ::updateFolder)
        collectImmediately(detailModel.folderSongList, ::updateList)
        collect(detailModel.toShow.flow, ::handleShow)
        collect(listModel.menu.flow, ::handleMenu)
        collectImmediately(listModel.selected, ::updateSelection)
        collect(musicModel.playlistDecision.flow, ::handlePlaylistDecision)
        collect(musicModel.playlistMessage.flow, ::handlePlaylistMessage)
        collectImmediately(
            playbackModel.song,
            playbackModel.parent,
            playbackModel.isPlaying,
            ::updatePlayback,
        )
        collect(playbackModel.playbackDecision.flow, ::handlePlaybackDecision)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailNormalToolbar.setOnMenuItemClickListener(null)
        binding.detailRecycler.adapter = null
        // Avoid possible race conditions that could cause a bad replace instruction to be consumed
        // during list initialization and crash the app. Could happen if the user is fast enough.
        detailModel.folderSongInstructions.consume()
    }

    override fun onPlayParent(parent: Folder) {
        playbackModel.play(parent)
    }

    override fun onShuffleParent(parent: Folder) {
        playbackModel.shuffle(parent)
    }

    override fun onRealClick(item: Song) {
        playbackModel.play(item, detailModel.playInFolderWith)
    }

    override fun onOpenParentMenu() {
        listModel.openMenu(R.menu.detail_folder, unlikelyToBeNull(detailModel.currentFolder.value))
    }

    override fun onOpenMenu(item: Song) {
        listModel.openMenu(R.menu.folder_song, item, detailModel.playInFolderWith)
    }

    override fun onOpenSortMenu() {
        findNavController().navigateSafe(FolderDetailFragmentDirections.sort())
    }

    private fun updateFolder(folder: Folder?) {
        if (folder == null) {
            // Folder we were showing no longer exists.
            findNavController().navigateUp()
            return
        }
        val binding = requireBinding()
        binding.detailNormalToolbar.title = folder.name.resolve(requireContext())

        binding.detailCover.bind(folder)

        binding.detailType.text = binding.context.getString(R.string.lbl_folder)
        binding.detailName.text = folder.name.resolve(binding.context)
        // Nothing about a folder is applicable to the sub-head text.
        binding.detailSubhead.isVisible = false

        val songs = folder.songs
        val durationMs = folder.durationMs
        // The song count of the folder maps to the info text.
        binding.detailInfo.text =
            if (songs.isNotEmpty()) {
                binding.context.getString(
                    R.string.fmt_two,
                    binding.context.getPlural(R.plurals.fmt_song_count, songs.size),
                    durationMs.formatDurationMs(true),
                )
            } else {
                binding.context.getString(R.string.def_song_count)
            }

        val playable = folder.songs.isNotEmpty()
        if (!playable) {
            L.d("Folder is empty, disabling playback options")
        }

        binding.detailPlayButton?.apply {
            isEnabled = playable
            setOnClickListener {
                playbackModel.play(unlikelyToBeNull(detailModel.currentFolder.value))
            }
        }
        binding.detailShuffleButton?.apply {
            isEnabled = playable
            setOnClickListener {
                playbackModel.shuffle(unlikelyToBeNull(detailModel.currentFolder.value))
            }
        }
        setToolbarPlaybackButtonsEnabled(playable)
        updatePlayback(
            playbackModel.song.value,
            playbackModel.parent.value,
            playbackModel.isPlaying.value,
        )
    }

    private fun updateList(list: List<Item>) {
        folderListAdapter.update(list, detailModel.folderSongInstructions.consume())
    }

    private fun handleShow(show: Show?) {
        when (show) {
            is Show.SongDetails -> {
                L.d("Navigating to ${show.song}")
                findNavController()
                    .navigateSafe(FolderDetailFragmentDirections.showSong(show.song.uid))
            }
            is Show.SongAlbumDetails -> {
                L.d("Navigating to the album of ${show.song}")
                findNavController()
                    .navigateSafe(FolderDetailFragmentDirections.showAlbum(show.song.album.uid))
            }
            is Show.AlbumDetails -> {
                L.d("Navigating to ${show.album}")
                findNavController()
                    .navigateSafe(FolderDetailFragmentDirections.showAlbum(show.album.uid))
            }
            is Show.ArtistDetails -> {
                L.d("Navigating to ${show.artist}")
                findNavController()
                    .navigateSafe(FolderDetailFragmentDirections.showArtist(show.artist.uid))
            }
            is Show.SongArtistDecision -> {
                L.d("Navigating to artist choices for ${show.song}")
                findNavController()
                    .navigateSafe(FolderDetailFragmentDirections.showArtistChoices(show.song.uid))
            }
            is Show.AlbumArtistDecision -> {
                L.d("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(FolderDetailFragmentDirections.showArtistChoices(show.album.uid))
            }
            is Show.PlaylistDetails -> {
                L.d("Navigating to ${show.playlist}")
                findNavController()
                    .navigateSafe(FolderDetailFragmentDirections.showPlaylist(show.playlist.uid))
            }
            is Show.FolderDetails -> {
                L.d("Navigated to this folder")
                detailModel.toShow.consume()
            }
            is Show.GenreDetails -> {
                error("Unexpected show command $show")
            }
            null -> {}
        }
    }

    private fun handleMenu(menu: Menu?) {
        if (menu == null) return
        val directions =
            when (menu) {
                is Menu.ForSong -> FolderDetailFragmentDirections.openSongMenu(menu.parcel)
                is Menu.ForPlaylist -> FolderDetailFragmentDirections.openPlaylistMenu(menu.parcel)
                is Menu.ForFolder -> FolderDetailFragmentDirections.openFolderMenu(menu.parcel)
                is Menu.ForSelection ->
                    FolderDetailFragmentDirections.openSelectionMenu(menu.parcel)
                is Menu.ForArtist,
                is Menu.ForAlbum,
                is Menu.ForGenre -> error("Unexpected menu $menu")
            }
        findNavController().navigateSafe(directions)
    }

    private fun updateSelection(selected: List<Music>) {
        folderListAdapter.setSelected(selected.toSet())

        val binding = requireBinding()
        if (selected.isNotEmpty()) {
            binding.detailSelectionToolbar.title = getString(R.string.fmt_selected, selected.size)
        }
        updateMultiToolbar()
    }

    private fun handlePlaylistDecision(decision: PlaylistDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaylistDecision.Rename -> {
                    L.d("Renaming ${decision.playlist}")
                    FolderDetailFragmentDirections.renamePlaylist(
                        decision.playlist.uid,
                        decision.template,
                        decision.applySongs.map { it.uid }.toTypedArray(),
                        decision.reason,
                    )
                }
                is PlaylistDecision.Export -> {
                    L.d("Exporting ${decision.playlist}")
                    FolderDetailFragmentDirections.exportPlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Delete -> {
                    L.d("Deleting ${decision.playlist}")
                    FolderDetailFragmentDirections.deletePlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Add -> {
                    L.d("Adding ${decision.songs.size} songs to a playlist")
                    FolderDetailFragmentDirections.addToPlaylist(
                        decision.songs.map { it.uid }.toTypedArray()
                    )
                }
                is PlaylistDecision.New,
                is PlaylistDecision.Import -> error("Unexpected playlist decision $decision")
            }
        findNavController().navigateSafe(directions)
    }

    private fun handlePlaylistMessage(message: PlaylistMessage?) {
        if (message == null) return
        requireContext().showToast(message.stringRes)
        musicModel.playlistMessage.consume()
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        // Prefer songs that are playing from this folder.
        folderListAdapter.setPlaying(
            song.takeIf { parent == detailModel.currentFolder.value },
            isPlaying,
        )
    }

    private fun handlePlaybackDecision(decision: PlaybackDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaybackDecision.PlayFromArtist -> {
                    L.d("Launching play from artist dialog for $decision")
                    FolderDetailFragmentDirections.playFromArtist(decision.song.uid)
                }
                is PlaybackDecision.PlayFromGenre -> {
                    L.d("Launching play from artist dialog for $decision")
                    FolderDetailFragmentDirections.playFromGenre(decision.song.uid)
                }
            }
        findNavController().navigateSafe(directions)
    }

    private fun updateMultiToolbar() {
        val id =
            when {
                listModel.selected.value.isNotEmpty() -> {
                    L.d("Currently selecting, showing selection toolbar")
                    R.id.detail_selection_toolbar
                }
                else -> {
                    L.d("Using normal toolbar")
                    R.id.detail_normal_toolbar
                }
            }

        requireBinding().detailToolbar.setVisible(id)
    }
}

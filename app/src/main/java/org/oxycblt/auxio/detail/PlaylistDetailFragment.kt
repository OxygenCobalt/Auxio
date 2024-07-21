/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDetailFragment.kt is part of Auxio.
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
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.list.PlaylistDetailListAdapter
import org.oxycblt.auxio.detail.list.PlaylistDragCallback
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.PlaylistMessage
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.external.M3U
import org.oxycblt.auxio.playback.PlaybackDecision
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.ui.DialogAwareNavigationListener
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ListFragment] that shows information for a particular [Playlist].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class PlaylistDetailFragment :
    DetailFragment<Playlist, Song>(), PlaylistDetailListAdapter.Listener {
    // Information about what playlist to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an playlist.
    private val args: PlaylistDetailFragmentArgs by navArgs()
    private val playlistListAdapter = PlaylistDetailListAdapter(this)
    private var touchHelper: ItemTouchHelper? = null
    private var editNavigationListener: DialogAwareNavigationListener? = null
    private var getContentLauncher: ActivityResultLauncher<String>? = null
    private var pendingImportTarget: Playlist? = null

    override fun getDetailListAdapter() = playlistListAdapter

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        editNavigationListener = DialogAwareNavigationListener(detailModel::dropPlaylistEdit)

        getContentLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri == null) {
                    logW("No URI returned from file picker")
                    return@registerForActivityResult
                }

                logD("Received playlist URI $uri")
                musicModel.importPlaylist(uri, pendingImportTarget)
            }

        // --- UI SETUP ---

        binding.detailEditToolbar.apply {
            setNavigationOnClickListener { detailModel.dropPlaylistEdit() }
            setOnMenuItemClickListener(this@PlaylistDetailFragment)
        }

        touchHelper =
            ItemTouchHelper(PlaylistDragCallback(detailModel)).also {
                it.attachToRecyclerView(binding.detailRecycler)
            }

        // --- VIEWMODEL SETUP ---
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setPlaylist(args.playlistUid)
        collectImmediately(
            detailModel.currentPlaylist, detailModel.editedPlaylist, ::updatePlaylist)
        collectImmediately(detailModel.playlistSongList, ::updateList)
        collectImmediately(detailModel.editedPlaylist, ::updateEditedList)
        collect(detailModel.toShow.flow, ::handleShow)
        collect(listModel.menu.flow, ::handleMenu)
        collectImmediately(listModel.selected, ::updateSelection)
        collect(musicModel.playlistDecision.flow, ::handlePlaylistDecision)
        collect(musicModel.playlistMessage.flow, ::handlePlaylistMessage)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(playbackModel.playbackDecision.flow, ::handlePlaybackDecision)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (super.onMenuItemClick(item)) {
            return true
        }

        if (item.itemId == R.id.action_save) {
            detailModel.savePlaylistEdit()
            return true
        }

        return false
    }

    override fun onStart() {
        super.onStart()
        // Once we add the destination change callback, we will receive another initialization call,
        // so handle that by resetting the flag.
        requireNotNull(editNavigationListener) { "NavigationListener was not available" }
            .attach(findNavController())
    }

    override fun onStop() {
        super.onStop()
        requireNotNull(editNavigationListener) { "NavigationListener was not available" }
            .release(findNavController())
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailNormalToolbar.setOnMenuItemClickListener(null)
        touchHelper = null
        binding.detailRecycler.adapter = null
        // Avoid possible race conditions that could cause a bad replace instruction to be consumed
        // during list initialization and crash the app. Could happen if the user is fast enough.
        detailModel.playlistSongInstructions.consume()
        editNavigationListener = null
    }

    override fun onRealClick(item: Song) {
        playbackModel.play(item, detailModel.playInPlaylistWith)
    }

    override fun onStartEdit() {
        detailModel.startPlaylistEdit()
    }

    override fun onPickUp(viewHolder: RecyclerView.ViewHolder) {
        requireNotNull(touchHelper) { "ItemTouchHelper was not available" }.startDrag(viewHolder)
    }

    override fun onOpenParentMenu() {
        listModel.openMenu(
            R.menu.detail_playlist, unlikelyToBeNull(detailModel.currentPlaylist.value))
    }

    override fun onOpenMenu(item: Song) {
        listModel.openMenu(R.menu.playlist_song, item, detailModel.playInPlaylistWith)
    }

    override fun onOpenSortMenu() {
        findNavController().navigateSafe(PlaylistDetailFragmentDirections.sort())
    }

    private fun updatePlaylist(playlist: Playlist?, editedPlaylist: List<Song>?) {
        if (playlist == null) {
            // Playlist we were showing no longer exists.
            findNavController().navigateUp()
            return
        }
        val binding = requireBinding()
        binding.detailToolbarTitle.text = playlist.name.resolve(requireContext())
        binding.detailEditToolbar.title =
            getString(R.string.fmt_editing, playlist.name.resolve(requireContext()))

        if (editedPlaylist != null) {
            logD("Binding edited playlist image")
            binding.detailCover.bind(
                editedPlaylist,
                binding.context.getString(R.string.desc_playlist_image, playlist.name),
                R.drawable.ic_playlist_24)
        } else {
            binding.detailCover.bind(playlist)
        }

        binding.detailType.text = binding.context.getString(R.string.lbl_playlist)
        binding.detailName.text = playlist.name.resolve(binding.context)
        // Nothing about a playlist is applicable to the sub-head text.
        binding.detailSubhead.isVisible = false

        val songs = editedPlaylist ?: playlist.songs
        val durationMs = editedPlaylist?.sumOf { it.durationMs } ?: playlist.durationMs
        // The song count of the playlist maps to the info text.
        binding.detailInfo.text =
            if (songs.isNotEmpty()) {
                binding.context.getString(
                    R.string.fmt_two,
                    binding.context.getPlural(R.plurals.fmt_song_count, songs.size),
                    durationMs.formatDurationMs(true))
            } else {
                binding.context.getString(R.string.def_song_count)
            }

        val playable = playlist.songs.isNotEmpty() && editedPlaylist == null
        if (!playable) {
            logD("Playlist is being edited or is empty, disabling playback options")
        }

        binding.detailPlayButton?.apply {
            isEnabled = playable
            setOnClickListener {
                playbackModel.play(unlikelyToBeNull(detailModel.currentPlaylist.value))
            }
        }
        binding.detailShuffleButton?.apply {
            isEnabled = playable
            setOnClickListener {
                playbackModel.shuffle(unlikelyToBeNull(detailModel.currentPlaylist.value))
            }
        }
    }

    private fun updateList(list: List<Item>) {
        playlistListAdapter.update(list, detailModel.playlistSongInstructions.consume())
    }

    private fun updateEditedList(editedPlaylist: List<Song>?) {
        playlistListAdapter.setEditing(editedPlaylist != null)
        listModel.dropSelection()

        if (editedPlaylist != null) {
            logD("Updating save button state")
            requireBinding().detailEditToolbar.menu.findItem(R.id.action_save).apply {
                isEnabled = editedPlaylist != detailModel.currentPlaylist.value?.songs
            }
        }

        updateMultiToolbar()
    }

    private fun handleShow(show: Show?) {
        when (show) {
            is Show.SongDetails -> {
                logD("Navigating to ${show.song}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showSong(show.song.uid))
            }
            is Show.SongAlbumDetails -> {
                logD("Navigating to the album of ${show.song}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showAlbum(show.song.album.uid))
            }
            is Show.AlbumDetails -> {
                logD("Navigating to ${show.album}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showAlbum(show.album.uid))
            }
            is Show.ArtistDetails -> {
                logD("Navigating to ${show.artist}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showArtist(show.artist.uid))
            }
            is Show.SongArtistDecision -> {
                logD("Navigating to artist choices for ${show.song}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showArtistChoices(show.song.uid))
            }
            is Show.AlbumArtistDecision -> {
                logD("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(
                        PlaylistDetailFragmentDirections.showArtistChoices(show.album.uid))
            }
            is Show.PlaylistDetails -> {
                logD("Navigated to this playlist")
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
                is Menu.ForSong -> PlaylistDetailFragmentDirections.openSongMenu(menu.parcel)
                is Menu.ForPlaylist ->
                    PlaylistDetailFragmentDirections.openPlaylistMenu(menu.parcel)
                is Menu.ForSelection ->
                    PlaylistDetailFragmentDirections.openSelectionMenu(menu.parcel)
                is Menu.ForArtist,
                is Menu.ForAlbum,
                is Menu.ForGenre -> error("Unexpected menu $menu")
            }
        findNavController().navigateSafe(directions)
    }

    private fun updateSelection(selected: List<Music>) {
        playlistListAdapter.setSelected(selected.toSet())

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
                is PlaylistDecision.Import -> {
                    logD("Importing playlist")
                    pendingImportTarget = decision.target
                    requireNotNull(getContentLauncher) {
                            "Content picker launcher was not available"
                        }
                        .launch(M3U.MIME_TYPE)
                    musicModel.playlistDecision.consume()
                    return
                }
                is PlaylistDecision.Rename -> {
                    logD("Renaming ${decision.playlist}")
                    PlaylistDetailFragmentDirections.renamePlaylist(
                        decision.playlist.uid,
                        decision.template,
                        decision.applySongs.map { it.uid }.toTypedArray(),
                        decision.reason)
                }
                is PlaylistDecision.Export -> {
                    logD("Exporting ${decision.playlist}")
                    PlaylistDetailFragmentDirections.exportPlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Delete -> {
                    logD("Deleting ${decision.playlist}")
                    PlaylistDetailFragmentDirections.deletePlaylist(decision.playlist.uid)
                }
                is PlaylistDecision.Add -> {
                    logD("Adding ${decision.songs.size} songs to a playlist")
                    PlaylistDetailFragmentDirections.addToPlaylist(
                        decision.songs.map { it.uid }.toTypedArray())
                }
                is PlaylistDecision.New -> error("Unexpected playlist decision $decision")
            }
        findNavController().navigateSafe(directions)
    }

    private fun handlePlaylistMessage(message: PlaylistMessage?) {
        if (message == null) return
        requireContext().showToast(message.stringRes)
        musicModel.playlistMessage.consume()
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        // Prefer songs that are playing from this playlist.
        playlistListAdapter.setPlaying(
            song.takeIf { parent == detailModel.currentPlaylist.value }, isPlaying)
    }

    private fun handlePlaybackDecision(decision: PlaybackDecision?) {
        if (decision == null) return
        val directions =
            when (decision) {
                is PlaybackDecision.PlayFromArtist -> {
                    logD("Launching play from artist dialog for $decision")
                    PlaylistDetailFragmentDirections.playFromArtist(decision.song.uid)
                }
                is PlaybackDecision.PlayFromGenre -> {
                    logD("Launching play from artist dialog for $decision")
                    PlaylistDetailFragmentDirections.playFromGenre(decision.song.uid)
                }
            }
        findNavController().navigateSafe(directions)
    }

    private fun updateMultiToolbar() {
        val id =
            when {
                detailModel.editedPlaylist.value != null -> {
                    logD("Currently editing playlist, showing edit toolbar")
                    R.id.detail_edit_toolbar
                }
                listModel.selected.value.isNotEmpty() -> {
                    logD("Currently selecting, showing selection toolbar")
                    R.id.detail_selection_toolbar
                }
                else -> {
                    logD("Using normal toolbar")
                    R.id.detail_normal_toolbar
                }
            }

        requireBinding().detailToolbar.setVisible(id)
    }
}

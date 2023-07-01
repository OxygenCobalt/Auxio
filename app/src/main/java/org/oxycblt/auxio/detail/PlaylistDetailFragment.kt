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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.header.DetailHeaderAdapter
import org.oxycblt.auxio.detail.header.PlaylistDetailHeaderAdapter
import org.oxycblt.auxio.detail.list.PlaylistDetailListAdapter
import org.oxycblt.auxio.detail.list.PlaylistDragCallback
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.setFullWidthLookup
import org.oxycblt.auxio.util.share
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [ListFragment] that shows information for a particular [Playlist].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class PlaylistDetailFragment :
    ListFragment<Song, FragmentDetailBinding>(),
    DetailHeaderAdapter.Listener,
    PlaylistDetailListAdapter.Listener,
    NavController.OnDestinationChangedListener {
    override val detailModel: DetailViewModel by activityViewModels()
    override val selectionModel: SelectionViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    // Information about what playlist to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an playlist.
    private val args: PlaylistDetailFragmentArgs by navArgs()
    private val playlistHeaderAdapter = PlaylistDetailHeaderAdapter(this)
    private val playlistListAdapter = PlaylistDetailListAdapter(this)
    private var touchHelper: ItemTouchHelper? = null
    private var initialNavDestinationChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            inflateMenu(R.menu.toolbar_playlist)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener(this@PlaylistDetailFragment)
        }

        binding.detailEditToolbar.apply {
            setNavigationOnClickListener { detailModel.dropPlaylistEdit() }
            setOnMenuItemClickListener(this@PlaylistDetailFragment)
        }

        binding.detailRecycler.apply {
            adapter = ConcatAdapter(playlistHeaderAdapter, playlistListAdapter)
            touchHelper =
                ItemTouchHelper(PlaylistDragCallback(detailModel)).also {
                    it.attachToRecyclerView(this)
                }
            (layoutManager as GridLayoutManager).setFullWidthLookup {
                if (it != 0) {
                    val item =
                        detailModel.playlistList.value.getOrElse(it - 1) {
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
        detailModel.setPlaylist(args.playlistUid)
        collectImmediately(detailModel.currentPlaylist, ::updatePlaylist)
        collectImmediately(detailModel.playlistList, ::updateList)
        collectImmediately(detailModel.editedPlaylist, ::updateEditedList)
        collect(detailModel.toShow.flow, ::handleShow)
        collectImmediately(selectionModel.selected, ::updateSelection)
        collect(musicModel.playlistDecision.flow, ::handleDecision)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
        collect(playbackModel.artistPickerSong.flow, ::handlePlayFromArtist)
        collect(playbackModel.genrePickerSong.flow, ::handlePlayFromGenre)
    }

    override fun onStart() {
        super.onStart()
        // Once we add the destination change callback, we will receive another initialization call,
        // so handle that by resetting the flag.
        initialNavDestinationChange = false
        findNavController().addOnDestinationChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        findNavController().removeOnDestinationChangedListener(this)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailNormalToolbar.setOnMenuItemClickListener(null)
        touchHelper = null
        binding.detailRecycler.adapter = null
        // Avoid possible race conditions that could cause a bad replace instruction to be consumed
        // during list initialization and crash the app. Could happen if the user is fast enough.
        detailModel.playlistInstructions.consume()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // Drop the initial call by NavController that simply provides us with the current
        // destination. This would cause the selection state to be lost every time the device
        // rotates.
        if (!initialNavDestinationChange) {
            initialNavDestinationChange = true
            return
        }
        // Drop any pending playlist edits when navigating away. This could actually happen
        // if the user is quick enough.
        detailModel.dropPlaylistEdit()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (super.onMenuItemClick(item)) {
            return true
        }

        val currentPlaylist = unlikelyToBeNull(detailModel.currentPlaylist.value)
        return when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(currentPlaylist)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(currentPlaylist)
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_rename -> {
                musicModel.renamePlaylist(currentPlaylist)
                true
            }
            R.id.action_delete -> {
                musicModel.deletePlaylist(currentPlaylist)
                true
            }
            R.id.action_share -> {
                requireContext().share(currentPlaylist)
                true
            }
            R.id.action_save -> {
                detailModel.savePlaylistEdit()
                true
            }
            else -> {
                logW("Unexpected menu item selected")
                false
            }
        }
    }

    override fun onRealClick(item: Song) {
        playbackModel.playFromPlaylist(item, unlikelyToBeNull(detailModel.currentPlaylist.value))
    }

    override fun onPickUp(viewHolder: RecyclerView.ViewHolder) {
        requireNotNull(touchHelper) { "ItemTouchHelper was not available" }.startDrag(viewHolder)
    }

    override fun onOpenMenu(item: Song, anchor: View) {
        openMusicMenu(anchor, R.menu.item_playlist_song, item)
    }

    override fun onPlay() {
        playbackModel.play(unlikelyToBeNull(detailModel.currentPlaylist.value))
    }

    override fun onShuffle() {
        playbackModel.shuffle(unlikelyToBeNull(detailModel.currentPlaylist.value))
    }

    override fun onStartEdit() {
        detailModel.startPlaylistEdit()
    }

    override fun onOpenSortMenu(anchor: View) {}

    private fun updatePlaylist(playlist: Playlist?) {
        if (playlist == null) {
            // Playlist we were showing no longer exists.
            findNavController().navigateUp()
            return
        }
        val binding = requireBinding()
        binding.detailNormalToolbar.apply {
            title = playlist.name.resolve(requireContext())
            // Disable options that make no sense with an empty playlist
            val playable = playlist.songs.isNotEmpty()
            if (!playable) {
                logD("Playlist is empty, disabling playback/share options")
            }
            menu.findItem(R.id.action_play_next).isEnabled = playable
            menu.findItem(R.id.action_queue_add).isEnabled = playable
            menu.findItem(R.id.action_share).isEnabled = playable
        }
        binding.detailEditToolbar.title =
            getString(R.string.fmt_editing, playlist.name.resolve(requireContext()))
        playlistHeaderAdapter.setParent(playlist)
    }

    private fun updateList(list: List<Item>) {
        playlistListAdapter.update(list, detailModel.playlistInstructions.consume())
    }

    private fun updateEditedList(editedPlaylist: List<Song>?) {
        playlistListAdapter.setEditing(editedPlaylist != null)
        playlistHeaderAdapter.setEditedPlaylist(editedPlaylist)
        selectionModel.drop()

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

            // Songs should be scrolled to if the album matches, or a new detail
            // fragment should be launched otherwise.
            is Show.SongAlbumDetails -> {
                logD("Navigating to the album of ${show.song}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showAlbum(show.song.album.uid))
            }

            // If the album matches, no need to do anything. Otherwise launch a new
            // detail fragment.
            is Show.AlbumDetails -> {
                logD("Navigating to ${show.album}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showAlbum(show.album.uid))
            }

            // Always launch a new ArtistDetailFragment.
            is Show.ArtistDetails -> {
                logD("Navigating to ${show.artist}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showArtist(show.artist.uid))
            }
            is Show.SongArtistDetails -> {
                logD("Navigating to artist choices for ${show.song}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showArtist(show.song.uid))
            }
            is Show.AlbumArtistDetails -> {
                logD("Navigating to artist choices for ${show.album}")
                findNavController()
                    .navigateSafe(PlaylistDetailFragmentDirections.showArtist(show.album.uid))
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

    private fun updateSelection(selected: List<Music>) {
        playlistListAdapter.setSelected(selected.toSet())

        val binding = requireBinding()
        if (selected.isNotEmpty()) {
            binding.detailSelectionToolbar.title = getString(R.string.fmt_selected, selected.size)
        }
        updateMultiToolbar()
    }

    private fun handleDecision(decision: PlaylistDecision?) {
        if (decision == null) return
        when (decision) {
            is PlaylistDecision.Rename -> {
                logD("Renaming ${decision.playlist}")
                findNavController()
                    .navigateSafe(
                        PlaylistDetailFragmentDirections.renamePlaylist(decision.playlist.uid))
            }
            is PlaylistDecision.Delete -> {
                logD("Deleting ${decision.playlist}")
                findNavController()
                    .navigateSafe(
                        PlaylistDetailFragmentDirections.deletePlaylist(decision.playlist.uid))
            }
            is PlaylistDecision.Add,
            is PlaylistDecision.New -> error("Unexpected decision $decision")
        }
        musicModel.playlistDecision.consume()
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        // Prefer songs that are playing from this playlist.
        playlistListAdapter.setPlaying(
            song.takeIf { parent == detailModel.currentPlaylist.value }, isPlaying)
    }

    private fun handlePlayFromArtist(song: Song?) {
        if (song == null) return
        logD("Launching play from artist dialog for $song")
        findNavController().navigateSafe(AlbumDetailFragmentDirections.playFromArtist(song.uid))
    }

    private fun handlePlayFromGenre(song: Song?) {
        if (song == null) return
        logD("Launching play from genre dialog for $song")
        findNavController().navigateSafe(AlbumDetailFragmentDirections.playFromGenre(song.uid))
    }
    private fun updateMultiToolbar() {
        val id =
            when {
                detailModel.editedPlaylist.value != null -> {
                    logD("Currently editing playlist, showing edit toolbar")
                    R.id.detail_edit_toolbar
                }
                selectionModel.selected.value.isNotEmpty() -> {
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

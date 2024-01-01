/*
 * Copyright (c) 2023 Auxio Project
 * AddToPlaylistDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.decision

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicChoicesBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.navigateSafe

/**
 * A dialog that allows the user to pick a specific playlist to add song(s) to.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class AddToPlaylistDialog :
    ViewBindingMaterialDialogFragment<DialogMusicChoicesBinding>(),
    ClickableListListener<PlaylistChoice>,
    NewPlaylistFooterAdapter.Listener {
    private val musicModel: MusicViewModel by activityViewModels()
    private val pickerModel: PlaylistPickerViewModel by viewModels()
    // Information about what playlist to name for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel playlist information.
    private val args: AddToPlaylistDialogArgs by navArgs()
    private val choiceAdapter = PlaylistChoiceAdapter(this)
    private val footerAdapter = NewPlaylistFooterAdapter(this)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.lbl_playlists).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicChoicesBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogMusicChoicesBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.choiceRecycler.apply {
            itemAnimator = null
            adapter = ConcatAdapter(choiceAdapter, footerAdapter)
        }

        // --- VIEWMODEL SETUP ---
        pickerModel.setSongsToAdd(args.songUids)
        musicModel.playlistDecision.consume()
        collectImmediately(pickerModel.currentSongsToAdd, ::updatePendingSongs)
        collectImmediately(pickerModel.playlistAddChoices, ::updatePlaylistChoices)
    }

    override fun onDestroyBinding(binding: DialogMusicChoicesBinding) {
        super.onDestroyBinding(binding)
        binding.choiceRecycler.adapter = null
    }

    override fun onClick(item: PlaylistChoice, viewHolder: RecyclerView.ViewHolder) {
        musicModel.addToPlaylist(pickerModel.currentSongsToAdd.value ?: return, item.playlist)
        findNavController().navigateUp()
    }

    override fun onNewPlaylist() {
        // TODO: This is a temporary fix. Eventually I want to make this navigate away and
        //  instead have primary fragments launch navigation to the new playlist dialog.
        //  This should be better design (dialog layering is uh... probably not good) and
        //  preserves the existing navigation system.
        //  I could also roll some kind of new playlist textbox into the dialog, but that's
        //  a lot harder.
        val songs = pickerModel.currentSongsToAdd.value ?: return
        findNavController()
            .navigateSafe(
                AddToPlaylistDialogDirections.newPlaylist(
                    songs.map { it.uid }.toTypedArray(), null, PlaylistDecision.New.Reason.ADD))
    }

    private fun updatePendingSongs(songs: List<Song>?) {
        if (songs == null) {
            logD("No songs to show choices for, navigating away")
            findNavController().navigateUp()
        }
    }

    private fun updatePlaylistChoices(choices: List<PlaylistChoice>) {
        choiceAdapter.update(choices, null)
    }
}

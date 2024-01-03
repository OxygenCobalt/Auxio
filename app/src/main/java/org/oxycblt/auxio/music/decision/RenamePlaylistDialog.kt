/*
 * Copyright (c) 2023 Auxio Project
 * RenamePlaylistDialog.kt is part of Auxio.
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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogPlaylistNameBinding
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A dialog allowing the name of a new playlist to be chosen before committing it to the database.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class RenamePlaylistDialog : ViewBindingMaterialDialogFragment<DialogPlaylistNameBinding>() {
    private val musicModel: MusicViewModel by activityViewModels()
    private val pickerModel: PlaylistPickerViewModel by viewModels()
    // Information about what playlist to name for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel playlist information.
    private val args: RenamePlaylistDialogArgs by navArgs()
    private var initializedField = false

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.lbl_rename_playlist)
            .setPositiveButton(R.string.lbl_ok) { _, _ ->
                val pendingRenamePlaylist =
                    unlikelyToBeNull(pickerModel.currentPendingRenamePlaylist.value)
                val chosenName = pickerModel.chosenName.value as ChosenName.Valid
                musicModel.renamePlaylist(
                    pendingRenamePlaylist.playlist,
                    chosenName.value,
                    pendingRenamePlaylist.applySongs,
                    pendingRenamePlaylist.reason)
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogPlaylistNameBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogPlaylistNameBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.playlistName.addTextChangedListener { pickerModel.updateChosenName(it?.toString()) }

        // --- VIEWMODEL SETUP ---
        musicModel.playlistDecision.consume()
        pickerModel.setPlaylistToRename(
            args.playlistUid, args.applySongUids, args.template, args.reason)
        collectImmediately(pickerModel.currentPendingRenamePlaylist, ::updatePlaylistToRename)
        collectImmediately(pickerModel.chosenName, ::updateChosenName)
    }

    private fun updatePlaylistToRename(pendingRenamePlaylist: PendingRenamePlaylist?) {
        if (pendingRenamePlaylist == null) {
            // Nothing to rename anymore.
            findNavController().navigateUp()
            return
        }

        if (!initializedField) {
            val default =
                pendingRenamePlaylist.template
                    ?: pendingRenamePlaylist.playlist.name.resolve(requireContext())
            logD("Name input is not initialized, setting to $default")
            requireBinding().playlistName.setText(default)
            initializedField = true
        }
    }

    private fun updateChosenName(chosenName: ChosenName) {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
            chosenName is ChosenName.Valid
    }
}

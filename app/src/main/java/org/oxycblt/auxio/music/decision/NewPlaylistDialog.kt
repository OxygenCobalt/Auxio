/*
 * Copyright (c) 2023 Auxio Project
 * NewPlaylistDialog.kt is part of Auxio.
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
import android.text.Editable
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
import org.oxycblt.auxio.music.PlaylistDecision
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
class NewPlaylistDialog : ViewBindingMaterialDialogFragment<DialogPlaylistNameBinding>() {
    private val musicModel: MusicViewModel by activityViewModels()
    private val pickerModel: PlaylistPickerViewModel by viewModels()
    // Information about what playlist to name for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel playlist information.
    private val args: NewPlaylistDialogArgs by navArgs()
    private var initializedField = false

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(
                when (args.reason) {
                    PlaylistDecision.New.Reason.NEW,
                    PlaylistDecision.New.Reason.ADD -> R.string.lbl_new_playlist
                    PlaylistDecision.New.Reason.IMPORT -> R.string.lbl_import_playlist
                })
            .setPositiveButton(R.string.lbl_ok) { _, _ ->
                val pendingPlaylist = unlikelyToBeNull(pickerModel.currentPendingNewPlaylist.value)
                val name =
                    when (val chosenName = pickerModel.chosenName.value) {
                        is ChosenName.Valid -> chosenName.value
                        is ChosenName.Empty -> pendingPlaylist.preferredName
                        else -> throw IllegalStateException()
                    }
                // TODO: Navigate to playlist if there are songs in it
                musicModel.createPlaylist(name, pendingPlaylist.songs, pendingPlaylist.reason)
                findNavController().apply {
                    navigateUp()
                    // Do an additional navigation away from the playlist addition dialog, if
                    // needed. If that dialog isn't present, this should be a no-op. Hopefully.
                    navigateUp()
                }
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
        pickerModel.setPendingPlaylist(requireContext(), args.songUids, args.template, args.reason)

        collectImmediately(pickerModel.currentPendingNewPlaylist, ::updatePendingPlaylist)
        collectImmediately(pickerModel.chosenName, ::updateChosenName)
    }

    private fun updatePendingPlaylist(pendingNewPlaylist: PendingNewPlaylist?) {
        if (pendingNewPlaylist == null) {
            logD("No playlist to create, leaving")
            findNavController().navigateUp()
            return
        }
        val binding = requireBinding()
        if (pendingNewPlaylist.template != null) {
            if (initializedField) return
            initializedField = true
            // Need to convert args.existingName to an Editable
            if (args.template != null) {
                binding.playlistName.text = EDITABLE_FACTORY.newEditable(args.template)
            }
        } else {
            binding.playlistName.hint = pendingNewPlaylist.preferredName
        }
    }

    private fun updateChosenName(chosenName: ChosenName) {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
            chosenName is ChosenName.Valid || chosenName is ChosenName.Empty
    }

    private companion object {
        val EDITABLE_FACTORY: Editable.Factory = Editable.Factory.getInstance()
    }
}

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
 
package org.oxycblt.auxio.music.dialog

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogPlaylistNameBinding
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A dialog allowing the name of a new/existing playlist to be edited.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class NewPlaylistDialog : ViewBindingDialogFragment<DialogPlaylistNameBinding>() {
    // activityViewModels is intentional here as the ViewModel will do work that we
    // do not want to cancel after this dialog closes.
    private val dialogModel: PlaylistDialogViewModel by activityViewModels()
    // Information about what playlist to name for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel playlist information.
    private val args: NewPlaylistDialogArgs by navArgs()

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.lbl_new_playlist)
            .setPositiveButton(R.string.lbl_ok) { _, _ -> dialogModel.confirmPendingName() }
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogPlaylistNameBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogPlaylistNameBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.playlistName.apply {
            hint = args.pendingPlaylist.name
            addTextChangedListener {
                dialogModel.updatePendingName(
                    (if (it.isNullOrEmpty()) unlikelyToBeNull(hint) else it).toString())
            }
        }

        dialogModel.setPendingName(args.pendingPlaylist)
        collectImmediately(dialogModel.pendingPlaylistValid, ::updateValid)
    }

    private fun updateValid(valid: Boolean) {
        // Disable the OK button if the name is invalid (empty or whitespace)
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = valid
    }
}

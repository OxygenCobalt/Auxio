/*
 * Copyright (c) 2023 Auxio Project
 * ExportPlaylistDialog.kt is part of Auxio.
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogPlaylistExportBinding
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.external.ExportConfig
import org.oxycblt.auxio.music.external.M3U
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A dialog that allows the user to configure how a playlist will be exported to a file.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class ExportPlaylistDialog : ViewBindingMaterialDialogFragment<DialogPlaylistExportBinding>() {
    private val musicModel: MusicViewModel by activityViewModels()
    private val pickerModel: PlaylistPickerViewModel by viewModels()
    private var createDocumentLauncher: ActivityResultLauncher<String>? = null
    // Information about what playlist to name for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel playlist information.
    private val args: ExportPlaylistDialogArgs by navArgs()

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.lbl_export_playlist)
            .setPositiveButton(R.string.lbl_export, null)
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogPlaylistExportBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: DialogPlaylistExportBinding,
        savedInstanceState: Bundle?
    ) {
        // --- UI SETUP ---
        createDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument(M3U.MIME_TYPE)) { uri
                ->
                if (uri == null) {
                    logW("No URI returned from file picker")
                    return@registerForActivityResult
                }

                val playlist = pickerModel.currentPlaylistToExport.value
                if (playlist == null) {
                    logW("No playlist to export")
                    findNavController().navigateUp()
                    return@registerForActivityResult
                }

                logD("Received playlist URI $uri")
                musicModel.exportPlaylist(playlist, uri, pickerModel.currentExportConfig.value)
                findNavController().navigateUp()
            }

        binding.exportPathsGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val current = pickerModel.currentExportConfig.value
            pickerModel.setExportConfig(
                current.copy(absolute = checkedId == R.id.export_absolute_paths))
        }

        binding.exportWindowsPaths.setOnClickListener { _ ->
            val current = pickerModel.currentExportConfig.value
            pickerModel.setExportConfig(current.copy(windowsPaths = !current.windowsPaths))
        }

        // --- VIEWMODEL SETUP ---
        musicModel.playlistDecision.consume()
        pickerModel.setPlaylistToExport(args.playlistUid)
        collectImmediately(pickerModel.currentPlaylistToExport, ::updatePlaylistToExport)
        collectImmediately(pickerModel.currentExportConfig, ::updateExportConfig)
    }

    override fun onStart() {
        super.onStart()
        (requireDialog() as AlertDialog)
            .getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener { _ ->
                val pendingPlaylist = unlikelyToBeNull(pickerModel.currentPlaylistToExport.value)

                val fileName =
                    pendingPlaylist.name
                        .resolve(requireContext())
                        .replace(SAFE_FILE_NAME_REGEX, "_") + ".m3u"

                requireNotNull(createDocumentLauncher) {
                        "Create document launcher was not available"
                    }
                    .launch(fileName)
            }
    }

    private fun updatePlaylistToExport(playlist: Playlist?) {
        if (playlist == null) {
            logD("No playlist to export, leaving")
            findNavController().navigateUp()
            return
        }
    }

    private fun updateExportConfig(config: ExportConfig) {
        val binding = requireBinding()
        binding.exportPathsGroup.check(
            if (config.absolute) {
                R.id.export_absolute_paths
            } else {
                R.id.export_relative_paths
            })
        if (config.absolute) {
            binding.exportRelativePaths.icon = null
            binding.exportAbsolutePaths.setIconResource(R.drawable.ic_check_24)
        } else {
            binding.exportAbsolutePaths.icon = null
            binding.exportRelativePaths.setIconResource(R.drawable.ic_check_24)
        }
        binding.exportWindowsPaths.isChecked = config.windowsPaths
    }

    private companion object {
        val SAFE_FILE_NAME_REGEX = Regex("[^a-zA-Z0-9.-]")
    }
}

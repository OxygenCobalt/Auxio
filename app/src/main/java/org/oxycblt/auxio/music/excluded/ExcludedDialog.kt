/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.music.excluded

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogExcludedBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.LifecycleDialog
import org.oxycblt.auxio.util.hardRestart
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast

/**
 * Dialog that manages the currently excluded directories.
 * @author OxygenCobalt
 */
class ExcludedDialog : LifecycleDialog() {
    private val excludedModel: ExcludedViewModel by viewModels {
        ExcludedViewModel.Factory(requireContext())
    }

    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogExcludedBinding.inflate(inflater)

        val adapter = ExcludedEntryAdapter { path -> excludedModel.removePath(path) }

        val launcher =
            registerForActivityResult(ActivityResultContracts.OpenDocumentTree(), ::addDocTreePath)

        // --- UI SETUP ---

        binding.excludedRecycler.adapter = adapter

        // Now that the dialog exists, we get the view manually when the dialog is shown
        // and override its click listener so that the dialog does not auto-dismiss when we
        // click the "Add"/"Save" buttons. This prevents the dialog from disappearing in the former
        // and the app from crashing in the latter.
        val dialog = requireDialog() as AlertDialog

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                logD("Opening launcher")
                launcher.launch(null)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                if (excludedModel.isModified) {
                    logD("Committing changes")
                    saveAndRestart()
                } else {
                    logD("Dropping changes")
                    dismiss()
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        excludedModel.paths.observe(viewLifecycleOwner) { paths ->
            adapter.submitList(paths)
            binding.excludedEmpty.isVisible = paths.isEmpty()
        }

        logD("Dialog created")

        return binding.root
    }

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.set_excluded)

        // Don't set the click listener here, we do some custom black magic in onCreateView instead.
        builder.setNeutralButton(R.string.lbl_add, null)
        builder.setPositiveButton(R.string.lbl_save, null)
        builder.setNegativeButton(android.R.string.cancel, null)
    }

    private fun addDocTreePath(uri: Uri?) {
        // A null URI means that the user left the file picker without picking a directory
        if (uri == null) {
            logD("No URI given (user closed the dialog)")
            return
        }

        val path = parseDocTreePath(uri)

        if (path != null) {
            excludedModel.addPath(path)
        } else {
            requireContext().showToast(R.string.err_bad_dir)
        }
    }

    private fun parseDocTreePath(uri: Uri): String? {
        // Turn the raw URI into a document tree URI
        val docUri =
            DocumentsContract.buildDocumentUriUsingTree(
                uri, DocumentsContract.getTreeDocumentId(uri))

        // Turn it into a semi-usable path
        val typeAndPath = DocumentsContract.getTreeDocumentId(docUri).split(":")

        // Only the main drive is supported, since that's all we can get from MediaColumns.DATA
        // Unless I change the system to use the drive/directory system, that is. But there's no
        // demand for that.
        // TODO: You are going to split the queries into pre-Q and post-Q versions, so perhaps
        //  you should try to add external partition support again.
        if (typeAndPath[0] == "primary") {
            return getRootPath() + "/" + typeAndPath.last()
        }

        logD("Unsupported volume ${typeAndPath[0]}")
        return null
    }

    private fun saveAndRestart() {
        excludedModel.save {
            playbackModel.savePlaybackState(requireContext()) { requireContext().hardRestart() }
        }
    }

    /** Get *just* the root path, nothing else is really needed. */
    private fun getRootPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }

    companion object {
        const val TAG = BuildConfig.APPLICATION_ID + ".tag.EXCLUDED"
    }
}

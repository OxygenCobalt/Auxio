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
import android.provider.DocumentsContract
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.delay
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogExcludedBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.hardRestart
import org.oxycblt.auxio.util.launch
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast

/**
 * Dialog that manages the currently excluded directories.
 * @author OxygenCobalt
 */
class ExcludedDialog :
    ViewBindingDialogFragment<DialogExcludedBinding>(), ExcludedAdapter.Listener {
    private val settingsManager = SettingsManager.getInstance()

    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val excludedAdapter = ExcludedAdapter(this)

    override fun onCreateBinding(inflater: LayoutInflater) = DialogExcludedBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        // Don't set the click listener here, we do some custom magic in onCreateView instead.
        builder
            .setTitle(R.string.set_excluded)
            .setNeutralButton(R.string.lbl_add, null)
            .setPositiveButton(R.string.lbl_save, null)
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogExcludedBinding, savedInstanceState: Bundle?) {
        val launcher =
            registerForActivityResult(ActivityResultContracts.OpenDocumentTree(), ::addDocTreePath)

        // Now that the dialog exists, we get the view manually when the dialog is shown
        // and override its click listener so that the dialog does not auto-dismiss when we
        // click the "Add"/"Save" buttons. This prevents the dialog from disappearing in the former
        // and the app from crashing in the latter.
        requireDialog().setOnShowListener {
            val dialog = it as AlertDialog

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                logD("Opening launcher")
                launcher.launch(null)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                if (settingsManager.excludedDirs != excludedAdapter.data.currentList) {
                    logD("Committing changes")
                    saveAndRestart()
                } else {
                    logD("Dropping changes")
                    dismiss()
                }
            }
        }

        binding.excludedRecycler.apply {
            adapter = excludedAdapter
            itemAnimator = null
        }

        val dirs =
            savedInstanceState
                ?.getStringArrayList(KEY_PENDING_DIRS)
                ?.mapNotNull(ExcludedDirectory::fromString)
                ?: settingsManager.excludedDirs

        excludedAdapter.data.addAll(dirs)
        requireBinding().excludedEmpty.isVisible = dirs.isEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(
            KEY_PENDING_DIRS, ArrayList(excludedAdapter.data.currentList.map { it.toString() }))
    }

    override fun onDestroyBinding(binding: DialogExcludedBinding) {
        super.onDestroyBinding(binding)
        binding.excludedRecycler.adapter = null
    }

    override fun onRemoveDirectory(dir: ExcludedDirectory) {
        excludedAdapter.data.remove(dir)
        requireBinding().excludedEmpty.isVisible = excludedAdapter.data.currentList.isEmpty()
    }

    private fun addDocTreePath(uri: Uri?) {
        if (uri == null) {
            // A null URI means that the user left the file picker without picking a directory
            logD("No URI given (user closed the dialog)")
            return
        }

        val dir = parseExcludedUri(uri)
        if (dir != null) {
            excludedAdapter.data.add(dir)
            requireBinding().excludedEmpty.isVisible = false
        } else {
            requireContext().showToast(R.string.err_bad_dir)
        }
    }

    private fun parseExcludedUri(uri: Uri): ExcludedDirectory? {
        // Turn the raw URI into a document tree URI
        val docUri =
            DocumentsContract.buildDocumentUriUsingTree(
                uri, DocumentsContract.getTreeDocumentId(uri))

        // Turn it into a semi-usable path
        val treeUri = DocumentsContract.getTreeDocumentId(docUri)

        // ExcludedDirectory handles the rest
        return ExcludedDirectory.fromString(treeUri)
    }

    private fun saveAndRestart() {
        settingsManager.excludedDirs = excludedAdapter.data.currentList

        // TODO: Dumb stopgap measure until automatic rescanning, REMOVE THIS BEFORE
        //  MAKING ANY RELEASE!!!!!!
        launch {
            delay(1000)
            playbackModel.savePlaybackState(requireContext()) { requireContext().hardRestart() }
        }
    }

    companion object {
        const val TAG = BuildConfig.APPLICATION_ID + ".tag.EXCLUDED"
        const val KEY_PENDING_DIRS = BuildConfig.APPLICATION_ID + ".key.PENDING_DIRS"
    }
}

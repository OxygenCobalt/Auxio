/*
 * Copyright (c) 2021 Auxio Project
 * MusicDirsDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.fs

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicDirsBinding
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast

/**
 * Dialog that manages the music dirs setting.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class MusicDirsDialog :
    ViewBindingDialogFragment<DialogMusicDirsBinding>(), DirectoryAdapter.Listener {
    private val dirAdapter = DirectoryAdapter(this)
    private var openDocumentTreeLauncher: ActivityResultLauncher<Uri?>? = null
    private var storageManager: StorageManager? = null
    @Inject lateinit var musicSettings: MusicSettings

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicDirsBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.set_dirs)
            .setNegativeButton(R.string.lbl_cancel, null)
            .setPositiveButton(R.string.lbl_save) { _, _ ->
                val newDirs = MusicDirectories(dirAdapter.dirs, isUiModeInclude(requireBinding()))
                if (musicSettings.musicDirs != newDirs) {
                    logD("Committing changes")
                    musicSettings.musicDirs = newDirs
                }
            }
    }

    override fun onBindingCreated(binding: DialogMusicDirsBinding, savedInstanceState: Bundle?) {
        val context = requireContext()
        val storageManager =
            context.getSystemServiceCompat(StorageManager::class).also { storageManager = it }

        openDocumentTreeLauncher =
            registerForActivityResult(
                ActivityResultContracts.OpenDocumentTree(), ::addDocumentTreeUriToDirs)

        binding.dirsAdd.apply {
            ViewCompat.setTooltipText(this, contentDescription)
            setOnClickListener {
                logD("Opening launcher")
                val launcher =
                    requireNotNull(openDocumentTreeLauncher) {
                        "Document tree launcher was not available"
                    }

                try {
                    launcher.launch(null)
                } catch (e: ActivityNotFoundException) {
                    // User doesn't have a capable file manager.
                    requireContext().showToast(R.string.err_no_app)
                }
            }
        }

        binding.dirsRecycler.apply {
            adapter = dirAdapter
            itemAnimator = null
        }

        var dirs = musicSettings.musicDirs
        if (savedInstanceState != null) {
            val pendingDirs = savedInstanceState.getStringArrayList(KEY_PENDING_DIRS)
            if (pendingDirs != null) {
                dirs =
                    MusicDirectories(
                        pendingDirs.mapNotNull {
                            Directory.fromDocumentTreeUri(storageManager, it)
                        },
                        savedInstanceState.getBoolean(KEY_PENDING_MODE))
            }
        }

        dirAdapter.addAll(dirs.dirs)
        requireBinding().dirsEmpty.isVisible = dirs.dirs.isEmpty()

        binding.folderModeGroup.apply {
            check(
                if (dirs.shouldInclude) {
                    R.id.dirs_mode_include
                } else {
                    R.id.dirs_mode_exclude
                })

            updateMode()
            addOnButtonCheckedListener { _, _, _ -> updateMode() }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(
            KEY_PENDING_DIRS, ArrayList(dirAdapter.dirs.map { it.toString() }))
        outState.putBoolean(KEY_PENDING_MODE, isUiModeInclude(requireBinding()))
    }

    override fun onDestroyBinding(binding: DialogMusicDirsBinding) {
        super.onDestroyBinding(binding)
        storageManager = null
        openDocumentTreeLauncher = null
        binding.dirsRecycler.adapter = null
    }

    override fun onRemoveDirectory(dir: Directory) {
        dirAdapter.remove(dir)
        requireBinding().dirsEmpty.isVisible = dirAdapter.dirs.isEmpty()
    }

    /**
     * Add a Document Tree [Uri] chosen by the user to the current [MusicDirectories] instance.
     *
     * @param uri The document tree [Uri] to add, chosen by the user. Will do nothing if the [Uri]
     *   is null or not valid.
     */
    private fun addDocumentTreeUriToDirs(uri: Uri?) {
        if (uri == null) {
            // A null URI means that the user left the file picker without picking a directory
            logD("No URI given (user closed the dialog)")
            return
        }

        // Convert the document tree URI into it's relative path form, which can then be
        // parsed into a Directory instance.
        val docUri =
            DocumentsContract.buildDocumentUriUsingTree(
                uri, DocumentsContract.getTreeDocumentId(uri))
        val treeUri = DocumentsContract.getTreeDocumentId(docUri)
        val dir =
            Directory.fromDocumentTreeUri(
                requireNotNull(storageManager) { "StorageManager was not available" }, treeUri)

        if (dir != null) {
            dirAdapter.add(dir)
            requireBinding().dirsEmpty.isVisible = false
        } else {
            requireContext().showToast(R.string.err_bad_dir)
        }
    }

    private fun updateMode() {
        val binding = requireBinding()
        if (isUiModeInclude(binding)) {
            binding.dirsModeExclude.icon = null
            binding.dirsModeInclude.setIconResource(R.drawable.ic_check_24)
            binding.dirsModeDesc.setText(R.string.set_dirs_mode_include_desc)
        } else {
            binding.dirsModeExclude.setIconResource(R.drawable.ic_check_24)
            binding.dirsModeInclude.icon = null
            binding.dirsModeDesc.setText(R.string.set_dirs_mode_exclude_desc)
        }
    }

    /** Get if the UI has currently configured [MusicDirectories.shouldInclude] to be true. */
    private fun isUiModeInclude(binding: DialogMusicDirsBinding) =
        binding.folderModeGroup.checkedButtonId == R.id.dirs_mode_include

    private companion object {
        const val KEY_PENDING_DIRS = BuildConfig.APPLICATION_ID + ".key.PENDING_DIRS"
        const val KEY_PENDING_MODE = BuildConfig.APPLICATION_ID + ".key.SHOULD_INCLUDE"
    }
}

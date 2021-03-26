package org.oxycblt.auxio.settings.blacklist

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogBlacklistBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.ui.LifecycleDialog
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.createToast
import org.oxycblt.auxio.ui.toColor
import kotlin.system.exitProcess

/**
 * Dialog that manages the currently excluded directories.
 * @author OxygenCobalt
 */
class BlacklistDialog : LifecycleDialog() {
    private val blacklistModel: BlacklistViewModel by viewModels {
        BlacklistViewModel.Factory(requireContext())
    }

    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogBlacklistBinding.inflate(inflater)

        val launcher = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree(), ::addDocTreePath
        )

        val accent = Accent.get().color.toColor(requireContext())

        val adapter = BlacklistEntryAdapter { path ->
            blacklistModel.removePath(path)
        }

        // --- UI SETUP ---

        binding.blacklistRecycler.adapter = adapter

        // Dialogs don't know how to into theming, so I have to manually set the accent color
        // to each of the buttons since the overall fragment theme is Neutral.
        binding.blacklistAdd.apply {
            setTextColor(accent)

            setOnClickListener {
                // showFileDialog()
                launcher.launch(null)
            }
        }

        binding.blacklistCancel.apply {
            setTextColor(accent)

            setOnClickListener {
                dismiss()
            }
        }

        binding.blacklistConfirm.apply {
            setTextColor(accent)

            setOnClickListener {
                if (blacklistModel.isModified()) {
                    saveAndRestart()
                } else {
                    dismiss()
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        blacklistModel.paths.observe(viewLifecycleOwner) { paths ->
            adapter.submitList(paths)

            binding.blacklistEmptyText.isVisible = paths.isEmpty()
        }

        logD("Dialog created.")

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        // If we have dismissed the dialog, then just drop any path changes.
        blacklistModel.loadDatabasePaths()
    }

    private fun addDocTreePath(uri: Uri?) {
        uri ?: return

        val path = parseDocTreePath(uri)

        if (path != null) {
            blacklistModel.addPath(path)
        } else {
            getString(R.string.error_bad_dir).createToast(requireContext())
        }
    }

    private fun parseDocTreePath(uri: Uri): String? {
        // Turn the raw URI into a document tree URI
        val docUri = DocumentsContract.buildDocumentUriUsingTree(
            uri, DocumentsContract.getTreeDocumentId(uri)
        )

        // Turn it into a semi-usable path
        val typeAndPath = DocumentsContract.getTreeDocumentId(docUri).split(":")

        // Only the main drive is supported, since thats all we can get from MediaColumns.DATA
        if (typeAndPath[0] == "primary") {
            return getRootPath() + "/" + typeAndPath.last()
        }

        return null
    }

    private fun saveAndRestart() {
        blacklistModel.save {
            playbackModel.savePlaybackState(requireContext(), ::hardRestart)
        }
    }

    private fun hardRestart() {
        logD("Performing hard restart.")

        // Instead of having to do a ton of cleanup and horrible code changes
        // to restart this application non-destructively, I just restart the UI task [There is only
        // one, after all] and then kill the application using exitProcess. Works well enough.
        val intent = Intent(requireContext().applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        exitProcess(0)
    }

    /**
     * Get *just* the root path, nothing else is really needed.
     */
    @Suppress("DEPRECATION")
    private fun getRootPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }
}

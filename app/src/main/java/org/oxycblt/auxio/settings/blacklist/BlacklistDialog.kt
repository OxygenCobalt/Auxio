package org.oxycblt.auxio.settings.blacklist

import android.content.Intent
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
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogBlacklistBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.ui.LifecycleDialog
import org.oxycblt.auxio.ui.createToast
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

        val adapter = BlacklistEntryAdapter { path ->
            blacklistModel.removePath(path)
        }

        val launcher = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree(), ::addDocTreePath
        )

        // --- UI SETUP ---

        binding.blacklistRecycler.adapter = adapter

        // Now that the dialog exists, we get the view manually when the dialog is shown
        // and override its click-listener so that the dialog does not auto-dismiss when we
        // click the "Add"/"Save" buttons. This prevents the dialog from disappearing in the former
        // and the app from crashing in the latter.
        val dialog = requireDialog() as AlertDialog

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                launcher.launch(null)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
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

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.setting_content_blacklist)

        // Dont set the click listener here, we do some custom black magic in onCreateView instead.
        builder.setNeutralButton(R.string.label_add, null)
        builder.setPositiveButton(R.string.label_save, null)
        builder.setNegativeButton(android.R.string.cancel, null)
    }

    private fun addDocTreePath(uri: Uri?) {
        // A null URI means that the user left the file picker without picking a directory
        if (uri == null) {
            return
        }

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

    companion object {
        const val TAG = "TAG_BLACKLIST_DIALOG"
    }
}

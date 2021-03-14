package org.oxycblt.auxio.settings.blacklist

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.files.selectedFolder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentBlacklistBinding
import org.oxycblt.auxio.ui.createToast
import java.io.File

class BlacklistDialog : BottomSheetDialogFragment() {
    private val blacklistModel: BlacklistViewModel by activityViewModels()

    override fun getTheme() = R.style.Theme_BottomSheetFix

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBlacklistBinding.inflate(inflater)

        val adapter = BlacklistEntryAdapter { path ->
            blacklistModel.removePath(path)
        }

        // --- UI SETUP ---

        binding.blacklistRecycler.adapter = adapter
        binding.blacklistAdd.setOnClickListener {
            MaterialDialog(requireActivity()).show {
                positiveButton(R.string.label_add) {
                    onFolderSelected()
                }

                negativeButton()

                folderChooser(
                    requireContext(),
                    initialDirectory = File(Environment.getExternalStorageDirectory().absolutePath),
                    waitForPositiveButton = false,
                    emptyTextRes = R.string.error_no_dirs
                )

                // Still need to force-reenable the positive button even after flagging the
                // disabling as false when setting up the file chooser
                // Gotta love third-party libraries
                setActionButtonEnabled(WhichButton.POSITIVE, true)
            }
        }

        // --- VIEWMODEL SETUP ---

        blacklistModel.paths.observe(viewLifecycleOwner) { paths ->
            adapter.submitList(paths)
        }

        return binding.root
    }

    private fun MaterialDialog.onFolderSelected() {
        selectedFolder()?.absolutePath?.let { path ->
            // Due to how Auxio's navigation flow works, dont allow the main root directory
            // to be excluded, as that would lead to the user being stuck at the "No Music Found"
            // error.
            if (path == Environment.getExternalStorageDirectory().absolutePath) {
                getString(R.string.error_folder_would_brick_app)
                    .createToast(requireContext())

                return
            }

            blacklistModel.addPath(path)
        }
    }
}

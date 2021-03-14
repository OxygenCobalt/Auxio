package org.oxycblt.auxio.settings.blacklist

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.files.selectedFolder
import com.afollestad.materialdialogs.internal.list.DialogRecyclerView
import com.afollestad.materialdialogs.utils.invalidateDividers
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
            showFileDialog()
        }

        binding.blacklistCancel.setOnClickListener {
            dismiss()
        }

        // --- VIEWMODEL SETUP ---

        blacklistModel.paths.observe(viewLifecycleOwner) { paths ->
            adapter.submitList(paths)

            binding.blacklistEmptyText.isVisible = paths.isEmpty()
        }

        return binding.root
    }

    private fun showFileDialog() {
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

            // Once again remove the ugly dividers from the dialog, but now with an even
            // worse solution.
            invalidateDividers(showTop = false, showBottom = false)

            val recycler = (getCustomView() as ViewGroup)
                .children.filterIsInstance<DialogRecyclerView>().firstOrNull()

            recycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    invalidateDividers(showTop = false, showBottom = false)
                }
            })
        }
    }

    private fun MaterialDialog.onFolderSelected() {
        selectedFolder()?.absolutePath?.let { path ->
            // Due to how Auxio's navigation flow works, dont allow the main root directory
            // to be excluded, as that would lead to the user being stuck at the "No Music Found"
            // screen.
            if (path == Environment.getExternalStorageDirectory().absolutePath) {
                getString(R.string.error_folder_would_brick_app)
                    .createToast(requireContext())

                return
            }

            blacklistModel.addPath(path)
        }
    }
}

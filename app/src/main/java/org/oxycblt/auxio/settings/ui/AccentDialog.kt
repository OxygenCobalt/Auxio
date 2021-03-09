package org.oxycblt.auxio.settings.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.utils.invalidateDividers
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.Accent

class AccentDialog : DialogFragment() {
    private val settingsManager = SettingsManager.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Roll my own RecyclerView since [To no surprise whatsoever] Material Dialogs
        // has a bug where ugly dividers will show with the RecyclerView even if you disable them.
        // This is why I hate using third party libraries.
        val recycler = RecyclerView(requireContext()).apply {
            adapter = AccentAdapter { accent ->
                if (accent != Accent.get()) {
                    settingsManager.accent = accent

                    requireActivity().recreate()
                }

                dismiss()
            }

            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )

            post {
                // Combine the width of the recyclerview with the width of an item in order
                // to center the currently selected accent.
                val childWidth = getChildAt(0).width / 2

                (layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(
                        ACCENTS.indexOf(Accent.get()),
                        (width / 2) - childWidth
                    )
            }
        }

        return MaterialDialog(requireActivity())
            .title(R.string.setting_accent)
            .negativeButton(android.R.string.cancel)
            .customView(view = recycler)
            .noDividers()
    }

    private fun MaterialDialog.noDividers(): MaterialDialog {
        invalidateDividers(showTop = false, showBottom = false)

        return this
    }
}

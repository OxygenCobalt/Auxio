package org.oxycblt.auxio.settings.accent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogAccentBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.settings.ui.LifecycleDialog
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.toColor

/**
 * Dialog responsible for showing the list of accents to select.
 * @author OxygenCobalt
 */
class AccentDialog : LifecycleDialog() {
    private val settingsManager = SettingsManager.getInstance()
    private var pendingAccent = Accent.get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogAccentBinding.inflate(inflater)

        savedInstanceState?.getInt(KEY_PENDING_ACCENT)?.let { index ->
            pendingAccent = ACCENTS.getOrElse(index) { pendingAccent }
        }

        // --- UI SETUP ---

        binding.accentRecycler.apply {
            adapter = AccentAdapter(pendingAccent) { accent ->
                pendingAccent = accent

                updateAccent()
            }
        }

        updateAccent()

        logD("Dialog created.")

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_PENDING_ACCENT, ACCENTS.indexOf(pendingAccent))
    }

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.setting_accent)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            if (pendingAccent != Accent.get()) {
                settingsManager.accent = pendingAccent

                requireActivity().recreate()
            }

            dismiss()
        }

        // Negative button just dismisses, no need for a listener.
        builder.setNegativeButton(android.R.string.cancel, null)
    }

    private fun updateAccent() {
        val accentColor = pendingAccent.color.toColor(requireContext())

        (requireDialog() as AlertDialog).apply {
            getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(accentColor)
            getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(accentColor)
        }
    }

    companion object {
        const val TAG = "TAG_ACCENT_DIALOG"
        const val KEY_PENDING_ACCENT = "AXKEY_PEND_ACCENT"
    }
}

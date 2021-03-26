package org.oxycblt.auxio.settings.accent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.oxycblt.auxio.databinding.DialogAccentBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.settings.ui.LifecycleDialog
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.inflater
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

                updateAccent(binding)
            }
        }

        binding.accentConfirm.setOnClickListener {
            if (pendingAccent != Accent.get()) {
                settingsManager.accent = pendingAccent

                requireActivity().recreate()
            }

            dismiss()
        }

        binding.accentCancel.setOnClickListener {
            dismiss()
        }

        updateAccent(binding)

        logD("Dialog created.")

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_PENDING_ACCENT, ACCENTS.indexOf(pendingAccent))
    }

    private fun updateAccent(binding: DialogAccentBinding) {
        val accentColor = pendingAccent.color.toColor(requireContext())

        binding.accentCancel.setTextColor(accentColor)
        binding.accentConfirm.setTextColor(accentColor)
    }

    companion object {
        const val KEY_PENDING_ACCENT = "AXKEY_PEND_ACCENT"
    }
}

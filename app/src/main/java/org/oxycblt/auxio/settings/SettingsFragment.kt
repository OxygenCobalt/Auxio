package org.oxycblt.auxio.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.oxycblt.auxio.databinding.FragmentSettingsBinding
import org.oxycblt.auxio.settings.ui.AboutDialog

/**
 * A container [Fragment] for the settings menu.
 * @author OxygenCobalt
 */
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater)

        binding.settingsToolbar.setOnMenuItemClickListener {
            AboutDialog().show(childFragmentManager, TAG_ABOUT_DIALOG)
            true
        }

        return binding.root
    }

    companion object {
        private const val TAG_ABOUT_DIALOG = "TAG_ABOUT_DIALOG"
    }
}

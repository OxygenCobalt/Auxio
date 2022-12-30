/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music.extractor

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import com.google.android.material.checkbox.MaterialCheckBox
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogSeparatorsBinding
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.context

/**
 * A [ViewBindingDialogFragment] that allows the user to configure the separator characters used to
 * split tags with multiple values.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SeparatorsDialog : ViewBindingDialogFragment<DialogSeparatorsBinding>() {
    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogSeparatorsBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.set_separators)
            .setNegativeButton(R.string.lbl_cancel, null)
            .setPositiveButton(R.string.lbl_save) { _, _ ->
                settings.musicSeparators = getCurrentSeparators()
            }
    }

    override fun onBindingCreated(binding: DialogSeparatorsBinding, savedInstanceState: Bundle?) {
        for (child in binding.separatorGroup.children) {
            if (child is MaterialCheckBox) {
                // Reset the CheckBox state so that we can ensure that state we load in
                // from settings is not contaminated from the built-in CheckBox saved state.
                child.isChecked = false
            }
        }

        // More efficient to do one iteration through the separator list and initialize
        // the corresponding CheckBox for each character instead of doing an iteration
        // through the separator list for each CheckBox.
        (savedInstanceState?.getString(KEY_PENDING_SEPARATORS) ?: settings.musicSeparators)
            ?.forEach {
                when (it) {
                    SEPARATOR_COMMA -> binding.separatorComma.isChecked = true
                    SEPARATOR_SEMICOLON -> binding.separatorSemicolon.isChecked = true
                    SEPARATOR_SLASH -> binding.separatorSlash.isChecked = true
                    SEPARATOR_PLUS -> binding.separatorPlus.isChecked = true
                    SEPARATOR_AND -> binding.separatorAnd.isChecked = true
                    else -> error("Unexpected separator in settings data")
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PENDING_SEPARATORS, getCurrentSeparators())
    }

    /** Get the current separator string configuration from the UI. */
    private fun getCurrentSeparators(): String {
        // Create the separator list based on the checked configuration of each
        // view element. It's generally more stable to duplicate this code instead
        // of use a mapping that could feasibly drift from the actual layout.
        var separators = ""
        val binding = requireBinding()
        if (binding.separatorComma.isChecked) separators += SEPARATOR_COMMA
        if (binding.separatorSemicolon.isChecked) separators += SEPARATOR_SEMICOLON
        if (binding.separatorSlash.isChecked) separators += SEPARATOR_SLASH
        if (binding.separatorPlus.isChecked) separators += SEPARATOR_PLUS
        if (binding.separatorAnd.isChecked) separators += SEPARATOR_AND
        return separators
    }

    companion object {
        private val KEY_PENDING_SEPARATORS = BuildConfig.APPLICATION_ID + ".key.PENDING_SEPARATORS"
        // TODO: Move these to a more "Correct" location?
        private const val SEPARATOR_COMMA = ','
        private const val SEPARATOR_SEMICOLON = ';'
        private const val SEPARATOR_SLASH = '/'
        private const val SEPARATOR_PLUS = '+'
        private const val SEPARATOR_AND = '&'
    }
}

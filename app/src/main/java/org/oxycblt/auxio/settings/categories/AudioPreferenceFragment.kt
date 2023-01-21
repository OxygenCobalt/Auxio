/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.settings.categories

import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.BasePreferenceFragment
import org.oxycblt.auxio.settings.ui.WrappedDialogPreference

/**
 * Audio settings interface.
 * @author Alexander Capehart (OxygenCobalt)
 */
class AudioPreferenceFragment : BasePreferenceFragment(R.xml.preferences_audio) {

    override fun onOpenDialogPreference(preference: WrappedDialogPreference) {
        if (preference.key == getString(R.string.set_key_pre_amp)) {
            findNavController().navigate(AudioPreferenceFragmentDirections.goToPreAmpDialog())
        }
    }
}

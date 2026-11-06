/*
 * Copyright (c) 2026 Auxio Project
 * LibraryPreferenceFragment.kt is part of Auxio.
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

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.settings.BasePreferenceFragment
import org.oxycblt.auxio.settings.ui.WrappedDialogPreference
import org.oxycblt.auxio.util.navigateSafe
import timber.log.Timber as L

/**
 * "Library" settings.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class LibraryPreferenceFragment : BasePreferenceFragment(R.xml.preferences_library) {
    // Must be activity-scoped, not fragment-scoped: MainActivity is the one that observes and
    // shows organizePreview, so this needs to be the exact same MusicViewModel instance.
    private val musicModel: MusicViewModel by activityViewModels()

    override fun onOpenDialogPreference(preference: WrappedDialogPreference) {
        if (preference.key == getString(R.string.set_key_music_dirs)) {
            L.d("Navigating to music locations dialog")
            findNavController()
                .navigateSafe(LibraryPreferenceFragmentDirections.musicLocationsSettings())
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.set_key_reindex) -> musicModel.refresh()
            getString(R.string.set_key_rescan) -> musicModel.rescan()
            getString(R.string.set_key_organize) -> {
                L.d("Requesting organize preview")
                musicModel.previewOrganizeLibrary()
            }
            else -> return super.onPreferenceTreeClick(preference)
        }

        return true
    }
}

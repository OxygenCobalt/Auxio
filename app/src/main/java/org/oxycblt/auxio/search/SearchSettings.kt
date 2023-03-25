/*
 * Copyright (c) 2023 Auxio Project
 * SearchSettings.kt is part of Auxio.
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
 
package org.oxycblt.auxio.search

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.settings.Settings

/**
 * User configuration specific to the search UI.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface SearchSettings : Settings<Nothing> {
    /** The type of Music the search view is currently filtering to. */
    var searchFilterMode: MusicMode?
}

class SearchSettingsImpl @Inject constructor(@ApplicationContext context: Context) :
    Settings.Impl<Nothing>(context), SearchSettings {
    override var searchFilterMode: MusicMode?
        get() =
            MusicMode.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_search_filter), Int.MIN_VALUE))
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_search_filter), value?.intCode ?: Int.MIN_VALUE)
                apply()
            }
        }
}

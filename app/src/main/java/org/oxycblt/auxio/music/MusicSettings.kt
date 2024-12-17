/*
 * Copyright (c) 2023 Auxio Project
 * MusicSettings.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.musikr.fs.MusicLocation
import timber.log.Timber as L

/**
 * User configuration specific to music system.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MusicSettings : Settings<MusicSettings.Listener> {
    /** The current library revision. */
    var revision: UUID
    /** The locations of music to load. */
    var musicLocations: List<MusicLocation>
    /** Whether to exclude non-music audio files from the music library. */
    val excludeNonMusic: Boolean
    /** Whether to be actively watching for changes in the music library. */
    val shouldBeObserving: Boolean
    /** A [String] of characters representing the desired characters to denote multi-value tags. */
    var separators: String
    /** Whether to enable more advanced sorting by articles and numbers. */
    val intelligentSorting: Boolean

    interface Listener {
        /** Called when a setting controlling how music is loaded has changed. */
        fun onIndexingSettingChanged() {}
        /** Called when the [shouldBeObserving] configuration has changed. */
        fun onObservingChanged() {}
    }
}

class MusicSettingsImpl @Inject constructor(@ApplicationContext private val context: Context) :
    Settings.Impl<MusicSettings.Listener>(context), MusicSettings {

    override var revision: UUID
        get() =
            UUID.fromString(
                sharedPreferences.getString(getString(R.string.set_key_library_revision), null)
                    ?: UUID.randomUUID().toString())
        set(value) {
            sharedPreferences.edit {
                putString(getString(R.string.set_key_library_revision), value.toString())
                apply()
            }
        }

    override var musicLocations: List<MusicLocation>
        get() {
            val locations =
                sharedPreferences.getString(getString(R.string.set_key_music_locations), null)
                    ?: return emptyList()
            return MusicLocation.existing(context, locations)
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    getString(R.string.set_key_music_locations), MusicLocation.toString(value))
                this@edit.apply()
            }
        }

    override val excludeNonMusic: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_exclude_non_music), true)

    override val shouldBeObserving: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_observing), false)

    override var separators: String
        // Differ from convention and store a string of separator characters instead of an int
        // code. This makes it easier to use and more extendable.
        get() = sharedPreferences.getString(getString(R.string.set_key_separators), "") ?: ""
        set(value) {
            sharedPreferences.edit {
                putString(getString(R.string.set_key_separators), value)
                apply()
            }
        }

    override val intelligentSorting: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_auto_sort_names), true)

    override fun onSettingChanged(key: String, listener: MusicSettings.Listener) {
        // TODO: Differentiate "hard reloads" (Need the cache) and "Soft reloads"
        //  (just need to manipulate data)
        when (key) {
            getString(R.string.set_key_music_locations),
            getString(R.string.set_key_separators),
            getString(R.string.set_key_auto_sort_names) -> {
                L.d("Dispatching indexing setting change for $key")
                listener.onIndexingSettingChanged()
            }
            getString(R.string.set_key_observing) -> {
                L.d("Dispatching observing setting change")
                listener.onObservingChanged()
            }
        }
    }
}

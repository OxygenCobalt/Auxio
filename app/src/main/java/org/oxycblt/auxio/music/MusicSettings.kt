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
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.mediastore.MediaStoreFS
import org.oxycblt.musikr.fs.saf.SAF
import timber.log.Timber as L

/**
 * Represents the mode for loading music locations.
 */
enum class MusicLocationMode {
    /** Use Storage Access Framework (file picker) to select specific folders */
    SAF,
    /** Use system MediaStore database to load all music */
    SYSTEM
}

/**
 * User configuration specific to music system.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MusicSettings : Settings<MusicSettings.Listener> {
    /** The current library revision. */
    var revision: UUID?
    /** The currently configured SAF query (if any) * */
    val safQuery: SAF.Query?
    /** The currently configured MediaStore query (if any) * */
    val mediaStoreQuery: MediaStoreFS.Query?
    /** Whether to ignore hidden files and directories during music loading. */
    val withHidden: Boolean
    /** Whether to be actively watching for changes in the music library. */
    val shouldBeObserving: Boolean
    /** A [String] of characters representing the desired characters to denote multi-value tags. */
    var separators: String
    /** Whether to enable more advanced sorting by articles and numbers. */
    val intelligentSorting: Boolean
    /** Whether to use the file-system cache for improved loading times. */
    val useFileTreeCache: Boolean
    /** The mode for loading music locations (SAF or System database). */
    var locationMode: MusicLocationMode

    interface Listener {
        /** Called when the current music locations changed. */
        fun onMusicLocationsChanged() {}
        /** Called when a setting controlling how music is loaded has changed. */
        fun onIndexingSettingChanged() {}
        /** Called when the [shouldBeObserving] configuration has changed. */
        fun onObservingChanged() {}
    }
}

class MusicSettingsImpl @Inject constructor(@ApplicationContext private val context: Context) :
    Settings.Impl<MusicSettings.Listener>(context), MusicSettings {

    override var revision: UUID?
        get() =
            sharedPreferences
                .getString(getString(R.string.set_key_library_revision), null)
                ?.let(UUID::fromString)
        set(value) {
            sharedPreferences.edit {
                putString(getString(R.string.set_key_library_revision), value.toString())
                apply()
            }
        }

    override val withHidden: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_with_hidden), false)

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

    override val useFileTreeCache: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_fs_cache), false)

    override var locationMode: MusicLocationMode
        get() {
            val ordinal = sharedPreferences.getInt(getString(R.string.set_key_saf_mode), 0)
            return MusicLocationMode.values().getOrNull(ordinal) ?: MusicLocationMode.SAF
        }
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_saf_mode), value.ordinal)
                apply()
            }
        }

    override val safQuery: SAF.Query?
        get() {
            // SAF query is used when in SAF mode and we have music locations configured
            if (locationMode != MusicLocationMode.SAF) return null
            val locations = musicLocations
            if (locations.isEmpty()) return null
            return SAF.Query(
                source = locations, exclude = excludedLocations, withHidden = withHidden)
        }

    override val mediaStoreQuery: MediaStoreFS.Query?
        get() {
            // MediaStore query is used when in system database mode
            if (locationMode != MusicLocationMode.SYSTEM) return null

            return MediaStoreFS.Query(
                include = emptyList(), // No include filter in system database mode
                exclude = excludedLocations,
                excludeNonMusic = excludeNonMusic)
        }

    private var musicLocations: List<Location.Opened>
        get() {
            val locations =
                sharedPreferences.getString(getString(R.string.set_key_music_locations), null)
                    ?: return emptyList()
            return locations.toOpenedLocations()
        }
        set(value) {
            sharedPreferences.edit {
                putString(getString(R.string.set_key_music_locations), value.stringify())
                commit()
                // Sometimes changing this setting just won't actually trigger the listener.
                // Only this one. No idea why.
                listener?.onMusicLocationsChanged()
            }
        }

    private var excludedLocations: List<Location.Unopened>
        get() {
            val locations =
                sharedPreferences.getString(getString(R.string.set_key_excluded_locations), null)
                    ?: return emptyList()
            return locations.toUnopenedLocations()
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    getString(R.string.set_key_excluded_locations), value.stringifyLocations())
                commit()
                listener?.onMusicLocationsChanged()
            }
        }

    private val excludeNonMusic: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_exclude_non_music), true)

    override fun onSettingChanged(key: String, listener: MusicSettings.Listener) {
        // TODO: Differentiate "hard reloads" (Need the cache) and "Soft reloads"
        //  (just need to manipulate data)
        when (key) {
            getString(R.string.set_key_music_locations),
            getString(R.string.set_key_excluded_locations) -> {
                L.d("Dispatching music locations change")
                listener.onMusicLocationsChanged()
            }
            getString(R.string.set_key_separators),
            getString(R.string.set_key_auto_sort_names),
            getString(R.string.set_key_with_hidden),
            getString(R.string.set_key_exclude_non_music) -> {
                L.d("Dispatching indexing setting change for $key")
                listener.onIndexingSettingChanged()
            }
            getString(R.string.set_key_observing) -> {
                L.d("Dispatching observing setting change")
                listener.onObservingChanged()
            }
        }
    }

    private fun List<Location.Opened>.stringify(): String =
        joinToString(separator = ";") { it.uri.toString().replace(";", "\\;") }

    private fun String.toOpenedLocations(): List<Location.Opened> =
        splitEscaped { it == ';' }
            .mapNotNull { Location.Unopened.from(context, it.toUri())?.open(context) }

    private fun List<Location>.stringifyLocations(): String =
        joinToString(separator = ";") { it.uri.toString().replace(";", "\\;") }

    private fun String.toUnopenedLocations(): List<Location.Unopened> =
        splitEscaped { it == ';' }.mapNotNull { Location.Unopened.from(context, it.toUri()) }

    private inline fun String.splitEscaped(selector: (Char) -> Boolean): List<String> {
        val split = mutableListOf<String>()
        var currentString = ""
        var i = 0

        while (i < length) {
            val a = get(i)
            val b = getOrNull(i + 1)

            if (selector(a)) {
                // Non-escaped separator, split the string here, making sure any stray whitespace
                // is removed.
                split.add(currentString)
                currentString = ""
                i++
                continue
            }

            if (b != null && a == '\\' && selector(b)) {
                // Is an escaped character, add the non-escaped variant and skip two
                // characters to move on to the next one.
                currentString += b
                i += 2
            } else {
                // Non-escaped, increment normally.
                currentString += a
                i++
            }
        }

        if (currentString.isNotEmpty()) {
            // Had an in-progress split string that is now terminated, add it.
            split.add(currentString)
        }

        return split
    }
}

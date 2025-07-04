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
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.locations.LocationMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.unlikelyToBeNull
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.mediastore.MediaStoreFS
import org.oxycblt.musikr.fs.saf.SAF
import timber.log.Timber as L

/**
 * User configuration specific to music system.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MusicSettings : Settings<MusicSettings.Listener> {
    /** The current library revision. */
    var revision: UUID?
    /** The mode for loading music locations (SAF or System database). */
    var locationMode: LocationMode
    /** The currently configured SAF query (if any) * */
    var safQuery: SAF.Query
    /** The currently configured MediaStore query (if any) * */
    var mediaStoreQuery: MediaStoreFS.Query
    /** Whether to be actively watching for changes in the music library. */
    val shouldBeObserving: Boolean
    /** A [String] of characters representing the desired characters to denote multi-value tags. */
    var separators: String
    /** Whether to enable more advanced sorting by articles and numbers. */
    val intelligentSorting: Boolean
    /** Whether to use the file-system cache for improved loading times. */
    val useFileTreeCache: Boolean

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

    override var locationMode: LocationMode
        get() {
            val mode =
                sharedPreferences.getInt(
                    getString(R.string.set_key_saf_mode), IntegerTable.LOCATION_MODE_SAF)
            return LocationMode.fromInt(mode) ?: LocationMode.SAF
        }
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_saf_mode), value.intCode)
                apply()
            }
        }

    override var safQuery: SAF.Query
        get() {
            val locations =
                unlikelyToBeNull(
                        sharedPreferences
                            .getString(getString(R.string.set_key_music_locations), "")
                            .also { L.d("$it") })
                    .toOpenedLocations()
            val excludedLocations =
                unlikelyToBeNull(
                        sharedPreferences.getString(
                            getString(R.string.set_key_excluded_locations), ""))
                    .toUnopenedLocations()
            val withHidden =
                sharedPreferences.getBoolean(getString(R.string.set_key_with_hidden), false)
            return SAF.Query(
                source = locations, exclude = excludedLocations, withHidden = withHidden)
        }
        set(value) {
            sharedPreferences.edit {
                putString(getString(R.string.set_key_music_locations), value.source.stringify())
                putString(getString(R.string.set_key_excluded_locations), value.exclude.stringify())
                putBoolean(getString(R.string.set_key_with_hidden), value.withHidden)
            }
        }

    override var mediaStoreQuery: MediaStoreFS.Query
        get() {
            val filterMode =
                sharedPreferences.getInt(
                    getString(R.string.set_key_filter_mode), IntegerTable.FILTER_MODE_EXCLUDE)
            val filteredLocations =
                unlikelyToBeNull(
                        sharedPreferences.getString(
                            getString(R.string.set_key_filtered_locations), ""))
                    .toUnopenedLocations()
            val excludeNonMusic =
                sharedPreferences.getBoolean(getString(R.string.set_key_exclude_non_music), true)
            return MediaStoreFS.Query(
                mode =
                    when (filterMode) {
                        IntegerTable.FILTER_MODE_INCLUDE -> MediaStoreFS.FilterMode.INCLUDE
                        IntegerTable.FILTER_MODE_EXCLUDE -> MediaStoreFS.FilterMode.EXCLUDE
                        else -> MediaStoreFS.FilterMode.EXCLUDE
                    },
                filtered = filteredLocations,
                excludeNonMusic = excludeNonMusic)
        }
        set(value) {
            sharedPreferences.edit {
                val filterMode =
                    when (value.mode) {
                        MediaStoreFS.FilterMode.INCLUDE -> IntegerTable.FILTER_MODE_INCLUDE
                        MediaStoreFS.FilterMode.EXCLUDE -> IntegerTable.FILTER_MODE_EXCLUDE
                    }
                putInt(getString(R.string.set_key_filter_mode), filterMode)
                putString(
                    getString(R.string.set_key_filtered_locations), value.filtered.stringify())
                putBoolean(getString(R.string.set_key_exclude_non_music), value.excludeNonMusic)
            }
        }

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

    private fun List<Location>.stringify(): String =
        joinToString(separator = ";") { it.uri.toString().replace(";", "\\;") }

    private fun String.toOpenedLocations(): List<Location.Opened> =
        splitEscaped { it == ';' }
            .mapNotNull { Location.Unopened.from(context, it.toUri())?.open(context) }

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

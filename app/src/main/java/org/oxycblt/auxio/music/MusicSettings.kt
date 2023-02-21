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
 
package org.oxycblt.auxio.music

import android.content.Context
import android.os.storage.StorageManager
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.music.storage.Directory
import org.oxycblt.auxio.music.storage.MusicDirectories
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.getSystemServiceCompat

/**
 * User configuration specific to music system.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MusicSettings : Settings<MusicSettings.Listener> {
    /** The configuration on how to handle particular directories in the music library. */
    var musicDirs: MusicDirectories
    /** Whether to exclude non-music audio files from the music library. */
    val excludeNonMusic: Boolean
    /** Whether to be actively watching for changes in the music library. */
    val shouldBeObserving: Boolean
    /** A [String] of characters representing the desired characters to denote multi-value tags. */
    var multiValueSeparators: String
    /** Whether to trim english articles with song sort names. */
    val automaticSortNames: Boolean
    /** The [Sort] mode used in [Song] lists. */
    var songSort: Sort
    /** The [Sort] mode used in [Album] lists. */
    var albumSort: Sort
    /** The [Sort] mode used in [Artist] lists. */
    var artistSort: Sort
    /** The [Sort] mode used in [Genre] lists. */
    var genreSort: Sort
    /** The [Sort] mode used in an [Album]'s [Song] list. */
    var albumSongSort: Sort
    /** The [Sort] mode used in an [Artist]'s [Song] list. */
    var artistSongSort: Sort
    /** The [Sort] mode used in an [Genre]'s [Song] list. */
    var genreSongSort: Sort

    interface Listener {
        /** Called when a setting controlling how music is loaded has changed. */
        fun onIndexingSettingChanged() {}
        /** Called when the [shouldBeObserving] configuration has changed. */
        fun onObservingChanged() {}
    }
}

class MusicSettingsImpl @Inject constructor(@ApplicationContext context: Context) :
    Settings.Impl<MusicSettings.Listener>(context), MusicSettings {
    private val storageManager = context.getSystemServiceCompat(StorageManager::class)

    override var musicDirs: MusicDirectories
        get() {
            val dirs =
                (sharedPreferences.getStringSet(getString(R.string.set_key_music_dirs), null)
                        ?: emptySet())
                    .mapNotNull { Directory.fromDocumentTreeUri(storageManager, it) }
            return MusicDirectories(
                dirs,
                sharedPreferences.getBoolean(getString(R.string.set_key_music_dirs_include), false))
        }
        set(value) {
            sharedPreferences.edit {
                putStringSet(
                    getString(R.string.set_key_music_dirs),
                    value.dirs.map(Directory::toDocumentTreeUri).toSet())
                putBoolean(getString(R.string.set_key_music_dirs_include), value.shouldInclude)
                apply()
            }
        }

    override val excludeNonMusic: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_exclude_non_music), true)

    override val shouldBeObserving: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_observing), false)

    override var multiValueSeparators: String
        // Differ from convention and store a string of separator characters instead of an int
        // code. This makes it easier to use and more extendable.
        get() = sharedPreferences.getString(getString(R.string.set_key_separators), "") ?: ""
        set(value) {
            sharedPreferences.edit {
                putString(getString(R.string.set_key_separators), value)
                apply()
            }
        }

    override val automaticSortNames: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_auto_sort_names), true)

    override var songSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_songs_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_songs_sort), value.intCode)
                apply()
            }
        }

    override var albumSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_albums_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_albums_sort), value.intCode)
                apply()
            }
        }

    override var artistSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_artists_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_artists_sort), value.intCode)
                apply()
            }
        }

    override var genreSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_genres_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_genres_sort), value.intCode)
                apply()
            }
        }

    override var albumSongSort: Sort
        get() {
            var sort =
                Sort.fromIntCode(
                    sharedPreferences.getInt(
                        getString(R.string.set_key_album_songs_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByDisc, Sort.Direction.ASCENDING)

            // Correct legacy album sort modes to Disc
            if (sort.mode is Sort.Mode.ByName) {
                sort = sort.withMode(Sort.Mode.ByDisc)
            }

            return sort
        }
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_album_songs_sort), value.intCode)
                apply()
            }
        }

    override var artistSongSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(
                    getString(R.string.set_key_artist_songs_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByDate, Sort.Direction.DESCENDING)
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_artist_songs_sort), value.intCode)
                apply()
            }
        }

    override var genreSongSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(
                    getString(R.string.set_key_genre_songs_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_genre_songs_sort), value.intCode)
                apply()
            }
        }

    override fun onSettingChanged(key: String, listener: MusicSettings.Listener) {
        // TODO: Differentiate "hard reloads" (Need the cache) and "Soft reloads"
        //  (just need to manipulate data)
        when (key) {
            getString(R.string.set_key_exclude_non_music),
            getString(R.string.set_key_music_dirs),
            getString(R.string.set_key_music_dirs_include),
            getString(R.string.set_key_separators),
            getString(R.string.set_key_auto_sort_names) -> listener.onIndexingSettingChanged()
            getString(R.string.set_key_observing) -> listener.onObservingChanged()
        }
    }
}

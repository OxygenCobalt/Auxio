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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.storage.Directory
import org.oxycblt.auxio.music.storage.MusicDirectories
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.getSystemServiceCompat

/**
 * User configuration specific to music system.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MusicSettings : Settings {
    /** The configuration on how to handle particular directories in the music library. */
    var musicDirs: MusicDirectories
    /** Whether to exclude non-music audio files from the music library. */
    val excludeNonMusic: Boolean
    /** Whether to be actively watching for changes in the music library. */
    val shouldBeObserving: Boolean
    /** A [String] of characters representing the desired characters to denote multi-value tags. */
    var multiValueSeparators: String
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

    private class Real(context: Context) : Settings.Real(context), MusicSettings {
        private val storageManager = context.getSystemServiceCompat(StorageManager::class)

        override var musicDirs: MusicDirectories
            get() {
                val dirs =
                    (sharedPreferences.getStringSet(
                            context.getString(R.string.set_key_music_dirs), null)
                            ?: emptySet())
                        .mapNotNull { Directory.fromDocumentTreeUri(storageManager, it) }
                return MusicDirectories(
                    dirs,
                    sharedPreferences.getBoolean(
                        context.getString(R.string.set_key_music_dirs_include), false))
            }
            set(value) {
                sharedPreferences.edit {
                    putStringSet(
                        context.getString(R.string.set_key_music_dirs),
                        value.dirs.map(Directory::toDocumentTreeUri).toSet())
                    putBoolean(
                        context.getString(R.string.set_key_music_dirs_include), value.shouldInclude)
                    apply()
                }
            }

        override val excludeNonMusic: Boolean
            get() =
                sharedPreferences.getBoolean(
                    context.getString(R.string.set_key_exclude_non_music), true)

        override val shouldBeObserving: Boolean
            get() =
                sharedPreferences.getBoolean(context.getString(R.string.set_key_observing), false)

        override var multiValueSeparators: String
            // Differ from convention and store a string of separator characters instead of an int
            // code. This makes it easier to use and more extendable.
            get() =
                sharedPreferences.getString(context.getString(R.string.set_key_separators), "")
                    ?: ""
            set(value) {
                sharedPreferences.edit {
                    putString(context.getString(R.string.set_key_separators), value)
                    apply()
                }
            }

        override var songSort: Sort
            get() =
                Sort.fromIntCode(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_lib_songs_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByName, true)
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_lib_songs_sort), value.intCode)
                    apply()
                }
            }

        override var albumSort: Sort
            get() =
                Sort.fromIntCode(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_lib_albums_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByName, true)
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_lib_albums_sort), value.intCode)
                    apply()
                }
            }

        override var artistSort: Sort
            get() =
                Sort.fromIntCode(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_lib_artists_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByName, true)
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_lib_artists_sort), value.intCode)
                    apply()
                }
            }

        override var genreSort: Sort
            get() =
                Sort.fromIntCode(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_lib_genres_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByName, true)
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_lib_genres_sort), value.intCode)
                    apply()
                }
            }

        override var albumSongSort: Sort
            get() {
                var sort =
                    Sort.fromIntCode(
                        sharedPreferences.getInt(
                            context.getString(R.string.set_key_detail_album_sort), Int.MIN_VALUE))
                        ?: Sort(Sort.Mode.ByDisc, true)

                // Correct legacy album sort modes to Disc
                if (sort.mode is Sort.Mode.ByName) {
                    sort = sort.withMode(Sort.Mode.ByDisc)
                }

                return sort
            }
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_detail_album_sort), value.intCode)
                    apply()
                }
            }

        override var artistSongSort: Sort
            get() =
                Sort.fromIntCode(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_detail_artist_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByDate, false)
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_detail_artist_sort), value.intCode)
                    apply()
                }
            }

        override var genreSongSort: Sort
            get() =
                Sort.fromIntCode(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_detail_genre_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByName, true)
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_detail_genre_sort), value.intCode)
                    apply()
                }
            }
    }

    companion object {
        /**
         * Get a framework-backed implementation.
         * @param context [Context] required.
         */
        fun from(context: Context): MusicSettings = Real(context)
    }
}

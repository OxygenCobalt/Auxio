/*
 * Copyright (c) 2023 Auxio Project
 * ListSettings.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.settings.Settings

interface ListSettings : Settings<Unit> {
    /** The [Sort] mode used in Song lists. */
    var songSort: Sort
    /** The [Sort] mode used in Album lists. */
    var albumSort: Sort
    /** The [Sort] mode used in Artist lists. */
    var artistSort: Sort
    /** The [Sort] mode used in Genre lists. */
    var genreSort: Sort
    /** The [Sort] mode used in Playlist lists. */
    var playlistSort: Sort
    /** The [Sort] mode used in an Album's Song list. */
    var albumSongSort: Sort
    /** The [Sort] mode used in an Artist's Song list. */
    var artistSongSort: Sort
    /** The [Sort] mode used in a Genre's Song list. */
    var genreSongSort: Sort
}

class ListSettingsImpl @Inject constructor(@ApplicationContext val context: Context) :
    Settings.Impl<Unit>(context), ListSettings {
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

    override var playlistSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_playlists_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
        set(value) {
            sharedPreferences.edit {
                putInt(getString(R.string.set_key_playlists_sort), value.intCode)
                apply()
            }
        }

    override var albumSongSort: Sort
        get() =
            Sort.fromIntCode(
                sharedPreferences.getInt(
                    getString(R.string.set_key_album_songs_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByDisc, Sort.Direction.ASCENDING)
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
}

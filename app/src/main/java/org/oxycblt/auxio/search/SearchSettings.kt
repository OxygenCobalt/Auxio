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
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.settings.Settings

/**
 * User configuration specific to the search UI.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface SearchSettings : Settings<Nothing> {
    /** The type of Music the search view should filter to. */
    var filters: Set<MusicType>
}

class SearchSettingsImpl @Inject constructor(@ApplicationContext context: Context) :
    Settings.Impl<Nothing>(context), SearchSettings {
    override var filters: Set<MusicType>
        get() = buildSet {
            val intCode = sharedPreferences.getInt(getString(R.string.set_key_search_filters), 0)
            if (intCode and BIT_SONGS == BIT_SONGS) add(MusicType.SONGS)
            if (intCode and BIT_ALBUMS == BIT_ALBUMS) add(MusicType.ALBUMS)
            if (intCode and BIT_ARTISTS == BIT_ARTISTS) add(MusicType.ARTISTS)
            if (intCode and BIT_GENRES == BIT_GENRES) add(MusicType.GENRES)
            if (intCode and BIT_PLAYLISTS == BIT_PLAYLISTS) add(MusicType.PLAYLISTS)
        }
        set(value) {
            var intCode = 0
            if (MusicType.SONGS in value) intCode = intCode or BIT_SONGS
            if (MusicType.ALBUMS in value) intCode = intCode or BIT_ALBUMS
            if (MusicType.ARTISTS in value) intCode = intCode or BIT_ARTISTS
            if (MusicType.GENRES in value) intCode = intCode or BIT_GENRES
            if (MusicType.PLAYLISTS in value) intCode = intCode or BIT_PLAYLISTS
            sharedPreferences.edit { putInt(getString(R.string.set_key_search_filters), intCode) }
        }

    override fun migrate() {
        val filter = MusicType.fromIntCode(sharedPreferences.getInt(OLD_KEY_SEARCH_FILTER, 0))
        val intCode =
            when (filter) {
                MusicType.SONGS -> BIT_SONGS
                MusicType.ALBUMS -> BIT_ALBUMS
                MusicType.ARTISTS -> BIT_ARTISTS
                MusicType.GENRES -> BIT_GENRES
                MusicType.PLAYLISTS -> BIT_PLAYLISTS
                else -> 0
            }
        sharedPreferences.edit { putInt(getString(R.string.set_key_search_filters), intCode) }
    }

    private companion object {
        const val OLD_KEY_SEARCH_FILTER = "KEY_SEARCH_FILTER"
        const val BIT_SONGS = 0b00001
        const val BIT_ALBUMS = 0b00010
        const val BIT_ARTISTS = 0b00100
        const val BIT_GENRES = 0b01000
        const val BIT_PLAYLISTS = 0b10000
    }
}

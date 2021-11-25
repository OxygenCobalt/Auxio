/*
 * Copyright (c) 2021 Auxio Project
 * SettingsCompat.kt is part of Auxio.
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

package org.oxycblt.auxio.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import org.oxycblt.auxio.accent.ACCENT_COUNT
import org.oxycblt.auxio.accent.Accent
import org.oxycblt.auxio.playback.state.PlaybackMode

// A couple of utils for migrating from old settings values to the new
// formats used in 1.3.2 & 1.4.0

// TODO: Slate these for removal eventually. There probably isn't that many left who have the
//  old values but 2.0.0 will probably convince most of those to update too.

fun handleThemeCompat(prefs: SharedPreferences): Int {
    if (prefs.contains(OldKeys.KEY_THEME)) {
        // Before the creation of IntListPreference, I used strings to represent the themes.
        // I no longer need to do this.
        val newValue = when (prefs.getStringOrNull(OldKeys.KEY_THEME)) {
            EntryValues.THEME_AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            EntryValues.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            EntryValues.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES

            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        prefs.edit {
            putInt(SettingsManager.KEY_THEME, newValue)
            remove(OldKeys.KEY_THEME)
            apply()
        }

        return newValue
    }

    return prefs.getInt(SettingsManager.KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}

fun handleAccentCompat(prefs: SharedPreferences): Accent {
    if (prefs.contains(OldKeys.KEY_ACCENT1)) {
        var accent = prefs.getInt(OldKeys.KEY_ACCENT1, 5)

        // Correct any accents over yellow to their correct positions
        if (accent > 12) {
            accent--
        }

        // Correct neutral accents to the closest accent [Grey]
        if (accent == 18) {
            accent = 16
        }

        // If there are still any issues with indices, just correct them so a crash doesnt occur.
        if (accent >= ACCENT_COUNT) {
            accent = ACCENT_COUNT - 1
        }

        // Move this to the [also legacy] ACCENT2 field. This makes it easier to convert in the
        // next step.
        prefs.edit {
            putInt(OldKeys.KEY_ACCENT2, accent)
            remove(OldKeys.KEY_ACCENT1)
            apply()
        }

        return Accent(accent)
    }

    if (prefs.contains(OldKeys.KEY_ACCENT2)) {
        var accent = prefs.getInt(OldKeys.KEY_ACCENT2, 5)

        // Blue grey was merged with Light Blue in 2.0.0
        if (accent >= 17) {
            accent = 6
        }

        // Deep Orange was merged with red in 2.0.0
        if (accent == 14) {
            accent = 0
        }

        // Correct accents beyond deep orange (Brown/Grey)
        if (accent > 14) {
            accent--
        }

        prefs.edit {
            putInt(SettingsManager.KEY_ACCENT, accent)
            remove(OldKeys.KEY_ACCENT2)
            apply()
        }
    }

    return Accent(prefs.getInt(SettingsManager.KEY_ACCENT, 5))
}

fun handleSongPlayModeCompat(prefs: SharedPreferences): PlaybackMode {
    if (prefs.contains(OldKeys.KEY_SONG_PLAYBACK_MODE)) {
        val mode = when (prefs.getStringOrNull(OldKeys.KEY_SONG_PLAYBACK_MODE)) {
            EntryValues.IN_GENRE -> PlaybackMode.IN_GENRE
            EntryValues.IN_ARTIST -> PlaybackMode.IN_ARTIST
            EntryValues.IN_ALBUM -> PlaybackMode.IN_ALBUM
            EntryValues.ALL_SONGS -> PlaybackMode.ALL_SONGS

            else -> PlaybackMode.ALL_SONGS
        }

        prefs.edit {
            putInt(SettingsManager.KEY_SONG_PLAYBACK_MODE, mode.toInt())
            remove(OldKeys.KEY_SONG_PLAYBACK_MODE)
            apply()
        }

        return mode
    }

    return PlaybackMode.fromInt(prefs.getInt(SettingsManager.KEY_SONG_PLAYBACK_MODE, Int.MIN_VALUE))
        ?: PlaybackMode.ALL_SONGS
}

/**
 * A verbose shortcut for getString(key, null). Used during string pref migrations
 */
private fun SharedPreferences.getStringOrNull(key: String): String? = getString(key, null)

/**
 * Cache of the old keys used in Auxio.
 */
private object OldKeys {
    const val KEY_ACCENT1 = "KEY_ACCENT"
    const val KEY_ACCENT2 = "KEY_ACCENT2"
    const val KEY_THEME = "KEY_THEME"
    const val KEY_SONG_PLAYBACK_MODE = "KEY_SONG_PLAY_MODE"
}

/**
 * Static cache of old string values used in Auxio
 */
private object EntryValues {
    const val THEME_AUTO = "AUTO"
    const val THEME_LIGHT = "LIGHT"
    const val THEME_DARK = "DARK"

    const val IN_GENRE = "IN_GENRE"
    const val IN_ARTIST = "IN_ARTIST"
    const val IN_ALBUM = "IN_ALBUM"
    const val ALL_SONGS = "ALL_SONGS"
}

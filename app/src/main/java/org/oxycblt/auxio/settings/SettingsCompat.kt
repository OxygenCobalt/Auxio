package org.oxycblt.auxio.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.Accent

// A couple of utils for migrating from old settings values to the new the new
// formats used in 1.3.2 & 1.4.0

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
    if (prefs.contains(OldKeys.KEY_ACCENT)) {
        var accent = prefs.getInt(OldKeys.KEY_ACCENT, 5)

        // Correct any accents over yellow to their correct positions
        if (accent > 12) {
            accent--
        }

        // Correct neutral accents to the closest accent [Grey]
        if (accent == 18) {
            accent = 16
        }

        // If there are still any issues with indices, just correct them so a crash doesnt occur.
        if (accent > ACCENTS.lastIndex) {
            accent = ACCENTS.lastIndex
        }

        prefs.edit {
            putInt(SettingsManager.KEY_ACCENT, accent)
            remove(OldKeys.KEY_ACCENT)
            apply()
        }

        return ACCENTS[accent]
    }

    return ACCENTS[prefs.getInt(SettingsManager.KEY_ACCENT, 5)]
}

fun handleLibDisplayCompat(prefs: SharedPreferences): DisplayMode {
    if (prefs.contains(OldKeys.KEY_LIB_MODE)) {
        val mode = prefs.handleOldDisplayMode(OldKeys.KEY_LIB_MODE) ?: DisplayMode.SHOW_ARTISTS

        prefs.edit {
            putInt(SettingsManager.KEY_LIB_DISPLAY_MODE, mode.toInt())
            remove(OldKeys.KEY_LIB_MODE)
            apply()
        }

        return mode
    }

    return prefs.getData(SettingsManager.KEY_LIB_DISPLAY_MODE, DisplayMode::fromInt)
        ?: DisplayMode.SHOW_ARTISTS
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

    return prefs.getData(SettingsManager.KEY_SONG_PLAYBACK_MODE, PlaybackMode::fromInt)
        ?: PlaybackMode.ALL_SONGS
}

fun handleSearchModeCompat(prefs: SharedPreferences): DisplayMode {
    if (prefs.contains(OldKeys.KEY_SEARCH_FILTER)) {
        val mode = prefs.handleOldDisplayMode(OldKeys.KEY_SEARCH_FILTER) ?: DisplayMode.SHOW_ALL

        prefs.edit {
            putInt(SettingsManager.KEY_SEARCH_FILTER_MODE, mode.toInt())
            remove(OldKeys.KEY_SEARCH_FILTER)
            apply()
        }

        return mode
    }

    return prefs.getData(SettingsManager.KEY_SEARCH_FILTER_MODE, DisplayMode::fromInt)
        ?: DisplayMode.SHOW_ALL
}

private fun SharedPreferences.handleOldDisplayMode(key: String): DisplayMode? {
    return when (getStringOrNull(key)) {
        EntryValues.SHOW_GENRES -> DisplayMode.SHOW_GENRES
        EntryValues.SHOW_ARTISTS -> DisplayMode.SHOW_ARTISTS
        EntryValues.SHOW_ALBUMS -> DisplayMode.SHOW_ALBUMS
        EntryValues.SHOW_SONGS -> DisplayMode.SHOW_SONGS
        EntryValues.SHOW_ALL -> DisplayMode.SHOW_ALL

        else -> null
    }
}

/**
 * Cache of the old keys used in Auxio.
 */
private object OldKeys {
    const val KEY_ACCENT = "KEY_ACCENT"
    const val KEY_THEME = "KEY_THEME"
    const val KEY_LIB_MODE = "KEY_LIBRARY_DISPLAY_MODE"
    const val KEY_SONG_PLAYBACK_MODE = "KEY_SONG_PLAY_MODE"
    const val KEY_SEARCH_FILTER = "KEY_SEARCH"
}

/**
 * Static cache of old string values used in Auxio
 */
private object EntryValues {
    const val THEME_AUTO = "AUTO"
    const val THEME_LIGHT = "LIGHT"
    const val THEME_DARK = "DARK"

    const val SHOW_GENRES = "SHOW_GENRES"
    const val SHOW_ARTISTS = "SHOW_ARTISTS"
    const val SHOW_ALBUMS = "SHOW_ALBUMS"
    const val SHOW_SONGS = "SHOW_SONGS"
    const val SHOW_ALL = "SHOW_ALL"

    const val IN_GENRE = "IN_GENRE"
    const val IN_ARTIST = "IN_ARTIST"
    const val IN_ALBUM = "IN_ALBUM"
    const val ALL_SONGS = "ALL_SONGS"
}

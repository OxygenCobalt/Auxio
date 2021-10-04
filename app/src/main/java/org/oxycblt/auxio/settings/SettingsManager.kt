/*
 * Copyright (c) 2021 Auxio Project
 * SettingsManager.kt is part of Auxio.
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

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.accent.ACCENTS
import org.oxycblt.auxio.accent.Accent
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.SortMode

/**
 * Wrapper around the [SharedPreferences] class that writes & reads values without a context.
 * @author OxygenCobalt
 */
class SettingsManager private constructor(context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        // Poke the song playback mode pref so that it migrates [if it hasnt already]
        handleSongPlayModeCompat(sharedPrefs)

        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    // --- VALUES ---

    /** The current theme */
    val theme: Int
        get() = handleThemeCompat(sharedPrefs)

    /** Whether the dark theme should be black or not */
    val useBlackTheme: Boolean
        get() = sharedPrefs.getBoolean(KEY_BLACK_THEME, false)

    /** The current accent. */
    var accent: Accent
        get() = handleAccentCompat(sharedPrefs)
        set(value) {
            val accentIndex = ACCENTS.indexOf(value)

            check(accentIndex != -1) { "Invalid accent" }

            sharedPrefs.edit {
                putInt(KEY_ACCENT, accentIndex)
                apply()
            }
        }

    /**
     * Whether to display the LoopMode or the shuffle status on the notification.
     * False if loop, true if shuffle.
     */
    val useAltNotifAction: Boolean
        get() = sharedPrefs.getBoolean(KEY_USE_ALT_NOTIFICATION_ACTION, false)

    /**
     * Whether to even loading embedded covers
     */
    val showCovers: Boolean
        get() = sharedPrefs.getBoolean(KEY_SHOW_COVERS, true)

    /** Whether to ignore MediaStore covers */
    val useQualityCovers: Boolean
        get() = sharedPrefs.getBoolean(KEY_QUALITY_COVERS, false)

    /** Whether to do Audio focus. */
    val doAudioFocus: Boolean
        get() = sharedPrefs.getBoolean(KEY_AUDIO_FOCUS, true)

    /** Whether to resume/stop playback when a headset is connected/disconnected. */
    val doPlugMgt: Boolean
        get() = sharedPrefs.getBoolean(KEY_PLUG_MANAGEMENT, true)

    /** What queue to create when a song is selected (ex. From All Songs or Search) */
    val songPlaybackMode: PlaybackMode
        get() = handleSongPlayModeCompat(sharedPrefs)

    /** Whether shuffle should stay on when a new song is selected. */
    val keepShuffle: Boolean
        get() = sharedPrefs.getBoolean(KEY_KEEP_SHUFFLE, true)

    /** Whether to rewind when the back button is pressed. */
    val rewindWithPrev: Boolean
        get() = sharedPrefs.getBoolean(KEY_PREV_REWIND, true)

    /** Whether [org.oxycblt.auxio.playback.state.LoopMode.TRACK] should pause when the track repeats */
    val pauseOnLoop: Boolean
        get() = sharedPrefs.getBoolean(KEY_LOOP_PAUSE, false)

    /** The current filter mode of the search tab */
    var searchFilterMode: DisplayMode?
        get() = DisplayMode.fromFilterInt(sharedPrefs.getInt(KEY_SEARCH_FILTER_MODE, Int.MIN_VALUE))
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_SEARCH_FILTER_MODE, DisplayMode.toFilterInt(value))
                apply()
            }
        }

    var libSongSort: SortMode
        get() = SortMode.fromInt(sharedPrefs.getInt(KEY_LIB_SONGS_SORT, Int.MIN_VALUE))
            ?: SortMode.ASCENDING
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_LIB_SONGS_SORT, value.toInt())
                apply()
            }
        }

    var libAlbumSort: SortMode
        get() = SortMode.fromInt(sharedPrefs.getInt(KEY_LIB_ALBUMS_SORT, Int.MIN_VALUE))
            ?: SortMode.ASCENDING
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_LIB_ALBUMS_SORT, value.toInt())
                apply()
            }
        }

    var libArtistSort: SortMode
        get() = SortMode.fromInt(sharedPrefs.getInt(KEY_LIB_ARTISTS_SORT, Int.MIN_VALUE))
            ?: SortMode.ASCENDING
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_LIB_ARTISTS_SORT, value.toInt())
                apply()
            }
        }

    var libGenreSort: SortMode
        get() = SortMode.fromInt(sharedPrefs.getInt(KEY_LIB_GENRE_SORT, Int.MIN_VALUE))
            ?: SortMode.ASCENDING
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_LIB_GENRE_SORT, value.toInt())
                apply()
            }
        }

    var detailAlbumSort: SortMode
        get() = SortMode.fromInt(sharedPrefs.getInt(KEY_DETAIL_ALBUM_SORT, Int.MIN_VALUE))
            ?: SortMode.ASCENDING
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_DETAIL_ALBUM_SORT, value.toInt())
                apply()
            }
        }

    var detailArtistSort: SortMode
        get() = SortMode.fromInt(sharedPrefs.getInt(KEY_DETAIL_ARTIST_SORT, Int.MIN_VALUE))
            ?: SortMode.YEAR
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_DETAIL_ARTIST_SORT, value.toInt())
                apply()
            }
        }

    var detailGenreSort: SortMode
        get() = SortMode.fromInt(sharedPrefs.getInt(KEY_DETAIL_GENRE_SORT, Int.MIN_VALUE))
            ?: SortMode.ASCENDING
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_DETAIL_GENRE_SORT, value.toInt())
                apply()
            }
        }

    // --- CALLBACKS ---

    private val callbacks = mutableListOf<Callback>()

    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    // --- OVERRIDES ---

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_USE_ALT_NOTIFICATION_ACTION -> callbacks.forEach {
                it.onNotifActionUpdate(useAltNotifAction)
            }

            KEY_SHOW_COVERS -> callbacks.forEach {
                it.onShowCoverUpdate(showCovers)
            }

            KEY_QUALITY_COVERS -> callbacks.forEach {
                it.onQualityCoverUpdate(useQualityCovers)
            }
        }
    }

    /**
     * An interface for receiving some preference updates. Use/Extend this instead of
     * [SharedPreferences.OnSharedPreferenceChangeListener] if possible, as it doesn't require a
     * context.
     */
    interface Callback {
        fun onColorizeNotifUpdate(doColorize: Boolean) {}
        fun onNotifActionUpdate(useAltAction: Boolean) {}
        fun onShowCoverUpdate(showCovers: Boolean) {}
        fun onQualityCoverUpdate(doQualityCovers: Boolean) {}
    }

    companion object {
        const val KEY_THEME = "KEY_THEME2"
        const val KEY_BLACK_THEME = "KEY_BLACK_THEME"
        const val KEY_ACCENT = "KEY_ACCENT2"

        const val KEY_SHOW_COVERS = "KEY_SHOW_COVERS"
        const val KEY_QUALITY_COVERS = "KEY_QUALITY_COVERS"
        const val KEY_USE_ALT_NOTIFICATION_ACTION = "KEY_ALT_NOTIF_ACTION"

        const val KEY_AUDIO_FOCUS = "KEY_AUDIO_FOCUS"
        const val KEY_PLUG_MANAGEMENT = "KEY_PLUG_MGT"

        const val KEY_SONG_PLAYBACK_MODE = "KEY_SONG_PLAY_MODE2"
        const val KEY_KEEP_SHUFFLE = "KEY_KEEP_SHUFFLE"
        const val KEY_PREV_REWIND = "KEY_PREV_REWIND"
        const val KEY_LOOP_PAUSE = "KEY_LOOP_PAUSE"

        const val KEY_SAVE_STATE = "KEY_SAVE_STATE"
        const val KEY_BLACKLIST = "KEY_BLACKLIST"

        const val KEY_SEARCH_FILTER_MODE = "KEY_SEARCH_FILTER"

        const val KEY_LIB_SONGS_SORT = "KEY_SONGS_SORT"
        const val KEY_LIB_ALBUMS_SORT = "KEY_ALBUMS_SORT"
        const val KEY_LIB_ARTISTS_SORT = "KEY_ARTISTS_SORT"
        const val KEY_LIB_GENRE_SORT = "KEY_GENRE_SORT"

        const val KEY_DETAIL_ALBUM_SORT = "KEY_ALBUM_SORT"
        const val KEY_DETAIL_ARTIST_SORT = "KEY_ARTIST_SORT"
        const val KEY_DETAIL_GENRE_SORT = "KEY_GENRE_SORT"

        @Volatile
        private var INSTANCE: SettingsManager? = null

        /**
         * Init the single instance of [SettingsManager]. Done so that every object
         * can have access to it regardless of if it has a context.
         */
        fun init(context: Context): SettingsManager {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = SettingsManager(context)
                }
            }

            return getInstance()
        }

        /**
         * Get the single instance of [SettingsManager].
         */
        fun getInstance(): SettingsManager {
            val instance = INSTANCE

            if (instance != null) {
                return instance
            }

            error("SettingsManager must be initialized with init() before getting its instance.")
        }
    }
}

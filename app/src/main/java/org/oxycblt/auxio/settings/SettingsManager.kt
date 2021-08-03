package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.Accent

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

    /** Whether to colorize the notification */
    val colorizeNotif: Boolean
        get() = sharedPrefs.getBoolean(KEY_COLORIZE_NOTIFICATION, true)

    /**
     * Whether to display the LoopMode or the shuffle status on the notification.
     * False if loop, true if shuffle.
     */
    val useAltNotifAction: Boolean
        get() = sharedPrefs.getBoolean(KEY_USE_ALT_NOTIFICATION_ACTION, false)

    /** What to display on the library. */
    val libraryDisplayMode: DisplayMode
        get() = handleLibDisplayCompat(sharedPrefs)

    /**
     * Whether to even loading embedded covers
     * TODO: Make the UI result of this better?
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

    /** The current [SortMode] of the library. */
    var librarySortMode: SortMode
        get() = sharedPrefs.getData(KEY_LIB_SORT_MODE, SortMode::fromInt) ?: SortMode.ALPHA_DOWN

        set(value) {
            sharedPrefs.edit {
                putInt(KEY_LIB_SORT_MODE, value.toInt())
                apply()
            }
        }

    var albumSortMode: SortMode
        get() = sharedPrefs.getData(KEY_ALBUM_SORT_MODE, SortMode::fromInt) ?: SortMode.NUMERIC_DOWN
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_ALBUM_SORT_MODE, value.toInt())
                apply()
            }
        }

    var artistSortMode: SortMode
        get() = sharedPrefs.getData(KEY_ARTIST_SORT_MODE, SortMode::fromInt) ?: SortMode.NUMERIC_DOWN
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_ARTIST_SORT_MODE, value.toInt())
                apply()
            }
        }

    var genreSortMode: SortMode
        get() = sharedPrefs.getData(KEY_GENRE_SORT_MODE, SortMode::fromInt) ?: SortMode.ALPHA_DOWN
        set(value) {
            sharedPrefs.edit {
                putInt(KEY_GENRE_SORT_MODE, value.toInt())
                apply()
            }
        }

    /** The current filter mode of the search tab */
    var searchFilterMode: DisplayMode
        get() = handleSearchModeCompat(sharedPrefs)

        set(value) {
            sharedPrefs.edit {
                putInt(KEY_SEARCH_FILTER_MODE, value.toInt())
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
            KEY_COLORIZE_NOTIFICATION -> callbacks.forEach {
                it.onColorizeNotifUpdate(colorizeNotif)
            }

            KEY_USE_ALT_NOTIFICATION_ACTION -> callbacks.forEach {
                it.onNotifActionUpdate(useAltNotifAction)
            }

            KEY_LIB_DISPLAY_MODE -> callbacks.forEach {
                it.onLibDisplayModeUpdate(libraryDisplayMode)
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
        fun onLibDisplayModeUpdate(displayMode: DisplayMode) {}
        fun onShowCoverUpdate(showCovers: Boolean) {}
        fun onQualityCoverUpdate(doQualityCovers: Boolean) {}
    }

    companion object {
        const val KEY_THEME = "KEY_THEME2"
        const val KEY_BLACK_THEME = "KEY_BLACK_THEME"
        const val KEY_ACCENT = "KEY_ACCENT2"

        const val KEY_LIB_DISPLAY_MODE = "KEY_LIB_MODE"
        const val KEY_SHOW_COVERS = "KEY_SHOW_COVERS"
        const val KEY_QUALITY_COVERS = "KEY_QUALITY_COVERS"
        const val KEY_COLORIZE_NOTIFICATION = "KEY_COLOR_NOTIF"
        const val KEY_USE_ALT_NOTIFICATION_ACTION = "KEY_ALT_NOTIF_ACTION"

        const val KEY_AUDIO_FOCUS = "KEY_AUDIO_FOCUS"
        const val KEY_PLUG_MANAGEMENT = "KEY_PLUG_MGT"

        const val KEY_SONG_PLAYBACK_MODE = "KEY_SONG_PLAY_MODE2"
        const val KEY_KEEP_SHUFFLE = "KEY_KEEP_SHUFFLE"
        const val KEY_PREV_REWIND = "KEY_PREV_REWIND"
        const val KEY_LOOP_PAUSE = "KEY_LOOP_PAUSE"

        const val KEY_SAVE_STATE = "KEY_SAVE_STATE"
        const val KEY_BLACKLIST = "KEY_BLACKLIST"

        const val KEY_LIB_SORT_MODE = "KEY_LIBRARY_SORT_MODE"
        const val KEY_ALBUM_SORT_MODE = "KEY_ALBUM_SORT"
        const val KEY_ARTIST_SORT_MODE = "KEY_ARTIST_SORT"
        const val KEY_GENRE_SORT_MODE = "KEY_GENRE_SORT"

        const val KEY_SEARCH_FILTER_MODE = "KEY_SEARCH_FILTER"

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

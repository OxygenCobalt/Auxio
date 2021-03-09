package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.logD
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
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    // --- VALUES ---

    /** The current theme */
    val theme: Int
        get() = sharedPrefs.getString(Keys.KEY_THEME, EntryValues.THEME_AUTO)!!.toThemeInt()

    /** The current accent. */
    var accent: Accent
        get() {
            @Suppress("DEPRECATION")
            if (sharedPrefs.contains(Keys.KEY_ACCENT_OLD)) {
                logD("Migrating from old accent to new accent.")

                val newAccent = handleAccentCompat(
                    sharedPrefs.getInt(Keys.KEY_ACCENT_OLD, 5)
                )

                // When converted, write them to the new accent pref and delete the old one.
                sharedPrefs.edit()
                    .putInt(Keys.KEY_ACCENT, newAccent)
                    .remove(Keys.KEY_ACCENT_OLD)
                    .apply()
            }

            return ACCENTS[sharedPrefs.getInt(Keys.KEY_ACCENT, 5)]
        }
        set(value) {
            val accentIndex = ACCENTS.indexOf(value)

            check(accentIndex != -1) { "Invalid accent" }

            sharedPrefs.edit()
                .putInt(Keys.KEY_ACCENT, accentIndex)
                .apply()
        }

    /** Whether to colorize the notification */
    val colorizeNotif: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_COLORIZE_NOTIFICATION, true)

    /**
     * Whether to display the LoopMode or the shuffle status on the notification.
     * False if loop, true if shuffle.
     */
    val useAltNotifAction: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_USE_ALT_NOTIFICATION_ACTION, false)

    /** What to display on the library. */
    val libraryDisplayMode: DisplayMode
        get() = DisplayMode.valueOfOrFallback(
            sharedPrefs.getString(
                Keys.KEY_LIBRARY_DISPLAY_MODE,
                DisplayMode.SHOW_ARTISTS.toString()
            )
        )

    /**
     * Whether to even loading embedded covers
     * TODO: Make the UI result of this better?
     */
    val showCovers: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_SHOW_COVERS, true)

    /** Whether to ignore MediaStore covers */
    val useQualityCovers: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_QUALITY_COVERS, false)

    /** Whether to do Audio focus. */
    val doAudioFocus: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_AUDIO_FOCUS, true)

    /** Whether to resume/stop playback when a headset is connected/disconnected. */
    val doPlugMgt: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_PLUG_MANAGEMENT, true)

    /** What queue to create when a song is selected (ex. From All Songs or Search) */
    val songPlaybackMode: PlaybackMode
        get() = PlaybackMode.valueOfOrFallback(
            sharedPrefs.getString(
                Keys.KEY_SONG_PLAYBACK_MODE,
                PlaybackMode.ALL_SONGS.toString()
            )
        )

    /** What to do at the end of a playlist. */
    val doAtEnd: String
        get() = sharedPrefs.getString(Keys.KEY_AT_END, EntryValues.AT_END_LOOP_PAUSE)
            ?: EntryValues.AT_END_LOOP_PAUSE

    /** Whether shuffle should stay on when a new song is selected. */
    val keepShuffle: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_KEEP_SHUFFLE, true)

    /** Whether to rewind when the back button is pressed. */
    val rewindWithPrev: Boolean
        get() = sharedPrefs.getBoolean(Keys.KEY_PREV_REWIND, true)

    /** The current [SortMode] of the library. */
    var librarySortMode: SortMode
        get() = SortMode.fromInt(
            sharedPrefs.getInt(
                Keys.KEY_LIBRARY_SORT_MODE, SortMode.CONST_SORT_DEFAULT
            )
        ) ?: SortMode.ALPHA_DOWN

        set(value) {
            sharedPrefs.edit()
                .putInt(Keys.KEY_LIBRARY_SORT_MODE, value.toInt())
                .apply()
        }

    /** The current filter mode of the search tab */
    var searchFilterMode: DisplayMode
        get() = DisplayMode.valueOfOrFallback(
            sharedPrefs.getString(
                Keys.KEY_SEARCH_FILTER_MODE, DisplayMode.SHOW_ALL.toString()
            ),
            fallback = DisplayMode.SHOW_ALL
        )
        set(value) {
            sharedPrefs.edit()
                .putString(Keys.KEY_SEARCH_FILTER_MODE, value.toString())
                .apply()
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
            Keys.KEY_COLORIZE_NOTIFICATION -> callbacks.forEach {
                it.onColorizeNotifUpdate(colorizeNotif)
            }

            Keys.KEY_USE_ALT_NOTIFICATION_ACTION -> callbacks.forEach {
                it.onNotifActionUpdate(useAltNotifAction)
            }

            Keys.KEY_LIBRARY_DISPLAY_MODE -> callbacks.forEach {
                it.onLibDisplayModeUpdate(libraryDisplayMode)
            }

            Keys.KEY_SHOW_COVERS -> callbacks.forEach {
                it.onShowCoverUpdate(showCovers)
            }

            Keys.KEY_QUALITY_COVERS -> callbacks.forEach {
                it.onQualityCoverUpdate(useQualityCovers)
            }
        }
    }

    /**
     * Convert the old accent format of <1.3.1 to the accent format of 1.3.2-Onwards,
     * where many accents were changed or removed.
     */
    private fun handleAccentCompat(oldAccent: Int): Int {
        var newAccent = oldAccent

        // Correct any accents over yellow to their correct positions
        if (oldAccent > 12) {
            newAccent--
        }

        // Correct neutral accents to the closest accent [Grey]
        if (newAccent == 18) {
            newAccent = 16
        }

        // If there are still any issues with indices, just correct them so a crash doesnt occur.
        if (newAccent > ACCENTS.lastIndex) {
            newAccent = ACCENTS.lastIndex
        }

        return newAccent
    }

    /**
     * SharedPreferences keys.
     */
    object Keys {
        const val KEY_THEME = "KEY_THEME"
        const val KEY_ACCENT = "KEY_ACCENT2"
        const val KEY_EDGE_TO_EDGE = "KEY_EDGE"
        const val KEY_LIBRARY_DISPLAY_MODE = "KEY_LIBRARY_DISPLAY_MODE"
        const val KEY_SHOW_COVERS = "KEY_SHOW_COVERS"
        const val KEY_QUALITY_COVERS = "KEY_QUALITY_COVERS"
        const val KEY_COLORIZE_NOTIFICATION = "KEY_COLOR_NOTIF"
        const val KEY_USE_ALT_NOTIFICATION_ACTION = "KEY_ALT_NOTIF_ACTION"
        const val KEY_AUDIO_FOCUS = "KEY_AUDIO_FOCUS"
        const val KEY_PLUG_MANAGEMENT = "KEY_PLUG_MGT"
        const val KEY_SONG_PLAYBACK_MODE = "KEY_SONG_PLAY_MODE"
        const val KEY_AT_END = "KEY_AT_END"
        const val KEY_KEEP_SHUFFLE = "KEY_KEEP_SHUFFLE"
        const val KEY_PREV_REWIND = "KEY_PREV_REWIND"

        const val KEY_LIBRARY_SORT_MODE = "KEY_LIBRARY_SORT_MODE"
        const val KEY_SEARCH_FILTER_MODE = "KEY_SEARCH"
        const val KEY_DEBUG_SAVE = "KEY_SAVE_STATE"

        @Deprecated("Use the new KEY_ACCENT instead.")
        const val KEY_ACCENT_OLD = "KEY_ACCENT"
    }

    /**
     * Values for some settings entries that arent important enough to recieve an enum.
     */
    object EntryValues {
        const val THEME_AUTO = "AUTO"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"

        /** Pause and loop at the end. Similar to Spotify. */
        const val AT_END_LOOP_PAUSE = "LOOP_PAUSE"

        /** Loop at the end. Similar to Music Player GO. */
        const val AT_END_LOOP = "LOOP"

        /** Stop at the end. */
        const val AT_END_STOP = "STOP"
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

package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.ui.ACCENTS

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

    val theme: Int
        get() {
            return sharedPrefs.getString(Keys.KEY_THEME, EntryNames.THEME_AUTO)!!.toThemeInt()
        }

    var accent: Pair<Int, Int>
        get() {
            val accentIndex = sharedPrefs.getInt(Keys.KEY_ACCENT, 5)

            // Accent is stored as an index [to be efficient], so retrieve it when done.
            return ACCENTS[accentIndex]
        }
        set(value) {
            val accentIndex = ACCENTS.indexOf(value)

            check(accentIndex != -1) { "Invalid accent" }

            sharedPrefs.edit()
                .putInt(Keys.KEY_ACCENT, accentIndex)
                .apply()
        }

    val edgeEnabled: Boolean
        get() {
            return sharedPrefs.getBoolean(Keys.KEY_EDGE_TO_EDGE, false)
        }

    val colorizeNotif: Boolean
        get() {
            return sharedPrefs.getBoolean(Keys.KEY_COLORIZE_NOTIFICATION, true)
        }

    val useAltNotifAction: Boolean
        get() {
            return sharedPrefs.getBoolean(Keys.KEY_USE_ALT_NOTIFICATION_ACTION, false)
        }

    val libraryDisplayMode: DisplayMode
        get() {
            return DisplayMode.valueOfOrFallback(
                sharedPrefs.getString(
                    Keys.KEY_LIBRARY_DISPLAY_MODE,
                    DisplayMode.SHOW_ARTISTS.toString()
                )
            )
        }

    var librarySortMode: SortMode
        get() {
            return SortMode.fromInt(
                sharedPrefs.getInt(
                    Keys.KEY_LIBRARY_SORT_MODE,
                    SortMode.CONSTANT_ALPHA_DOWN
                )
            ) ?: SortMode.ALPHA_DOWN
        }
        set(value) {
            sharedPrefs.edit()
                .putInt(Keys.KEY_LIBRARY_SORT_MODE, value.toInt())
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
        }
    }

    companion object {
        @Volatile
        private lateinit var INSTANCE: SettingsManager

        /**
         * Init the single instance of [SettingsManager]. Done so that every object
         * can have access to it regardless of if it has a context.
         */
        fun init(context: Context): SettingsManager {
            if (!::INSTANCE.isInitialized) {
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
            check(::INSTANCE.isInitialized) {
                "PrefsManager must be initialized with init() before getting its instance."
            }
            return INSTANCE
        }
    }

    object Keys {
        const val KEY_THEME = "KEY_THEME"
        const val KEY_ACCENT = "KEY_ACCENT"
        const val KEY_EDGE_TO_EDGE = "KEY_EDGE"
        const val KEY_COLORIZE_NOTIFICATION = "KEY_COLOR_NOTIF"
        const val KEY_USE_ALT_NOTIFICATION_ACTION = "KEY_ALT_NOTIF_ACTION"
        const val KEY_LIBRARY_DISPLAY_MODE = "KEY_LIBRARY_DISPLAY_MODE"
        const val KEY_LIBRARY_SORT_MODE = "KEY_LIBRARY_SORT_MODE"
    }

    object EntryNames {
        const val THEME_AUTO = "AUTO"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"
    }

    interface Callback {
        fun onColorizeNotifUpdate(doColorize: Boolean) {}
        fun onNotifActionUpdate(useAltAction: Boolean) {}
        fun onLibDisplayModeUpdate(displayMode: DisplayMode) {}
    }
}

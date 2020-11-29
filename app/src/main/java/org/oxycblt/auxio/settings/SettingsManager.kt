package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.ui.ACCENTS

/**
 * Wrapper around the [SharedPreferences] class that writes & reads values without a context.
 *
 * **Note:** Run any getter in a IO coroutine if possible, as SharedPrefs will read from disk
 * the first time it occurs.
 * @author OxygenCobalt
 */
class SettingsManager private constructor(context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    private val callbacks = mutableListOf<Callback>()

    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    fun getTheme(): Int {
        // Turn the string from SharedPreferences into an actual theme value that can
        // be used, as apparently the preference system provided by androidx doesn't like integers
        // for some...reason.
        return when (sharedPrefs.getString(Keys.KEY_THEME, Theme.THEME_AUTO)) {
            Theme.THEME_AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            Theme.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES

            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    fun getAccent(): Pair<Int, Int> {
        val accentIndex = sharedPrefs.getInt(Keys.KEY_ACCENT, 5)

        return ACCENTS[accentIndex]
    }

    fun setAccent(accent: Pair<Int, Int>) {
        val accentIndex = ACCENTS.indexOf(accent)

        check(accentIndex != -1) { "Invalid accent" }

        sharedPrefs.edit()
            .putInt(Keys.KEY_ACCENT, accentIndex)
            .apply()

        callbacks.forEach {
            it.onAccentUpdate(getAccent())
        }
    }

    fun setLibrarySortMode(sortMode: SortMode) {
        sharedPrefs.edit()
            .putInt(Keys.KEY_LIBRARY_SORT_MODE, sortMode.toInt())
            .apply()
    }

    fun getLibrarySortMode(): SortMode {
        return SortMode.fromInt(
            sharedPrefs.getInt(
                Keys.KEY_LIBRARY_SORT_MODE,
                SortMode.CONSTANT_ALPHA_DOWN
            )
        ) ?: SortMode.ALPHA_DOWN
    }

    fun getEdgeToEdge(): Boolean {
        return sharedPrefs.getBoolean(Keys.KEY_EDGE_TO_EDGE, false)
    }

    // --- OVERRIDES ---

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Keys.KEY_THEME -> {
                callbacks.forEach {
                    it.onThemeUpdate(getTheme())
                }
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
        const val KEY_LIBRARY_SORT_MODE = "KEY_LIBRARY_SORT_MODE"
        const val KEY_THEME = "KEY_THEME"
        const val KEY_ACCENT = "KEY_ACCENT"
        const val KEY_EDGE_TO_EDGE = "KEY_EDGE"
    }

    private object Theme {
        const val THEME_AUTO = "AUTO"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"
    }

    /**
     * An interface for receiving some settings updates.
     * [SharedPreferences.OnSharedPreferenceChangeListener].
     */
    interface Callback {
        fun onThemeUpdate(newTheme: Int) {}
        fun onAccentUpdate(newAccent: Pair<Int, Int>) {}
        fun onEdgeToEdgeUpdate(isEdgeToEdge: Boolean) {}
    }
}

package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
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

    val theme: Int
        get() {
            return sharedPrefs.getString(Keys.KEY_THEME, EntryNames.THEME_AUTO)?.toThemeInt()
                ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
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

    object EntryNames {
        const val THEME_AUTO = "AUTO"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"
    }

    /**
     * An interface for receiving some settings updates.
     * [SharedPreferences.OnSharedPreferenceChangeListener].
     */
    interface Callback
}

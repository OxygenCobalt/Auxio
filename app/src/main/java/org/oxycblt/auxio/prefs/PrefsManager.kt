package org.oxycblt.auxio.prefs

import android.content.Context
import android.content.SharedPreferences
import org.oxycblt.auxio.recycler.SortMode

/**
 * Wrapper around the [SharedPreferences] class that writes & reads values.
 * Please run any getter/setter in a coroutine. Its not required, but it prevents slowdowns
 * on older devices.
 * @author OxygenCobalt
 */
class PrefsManager private constructor(context: Context) {
    private val sharedPrefs = context.getSharedPreferences(
        "auxio_prefs", Context.MODE_PRIVATE
    )

    private lateinit var mLibrarySortMode: SortMode

    fun setLibrarySortMode(sortMode: SortMode) {
        mLibrarySortMode = sortMode

        sharedPrefs.edit()
            .putInt(Keys.KEY_LIBRARY_SORT_MODE, sortMode.toConstant())
            .apply()
    }

    fun getLibrarySortMode(): SortMode {
        if (!::mLibrarySortMode.isInitialized) {
            mLibrarySortMode = SortMode.fromConstant(
                sharedPrefs.getInt(
                    Keys.KEY_LIBRARY_SORT_MODE,
                    SortMode.CONSTANT_ALPHA_DOWN
                )
            ) ?: SortMode.ALPHA_DOWN
        }

        return mLibrarySortMode
    }

    companion object {
        @Volatile
        private lateinit var INSTANCE: PrefsManager

        /**
         * Init the single instance of [PrefsManager]. Done so that every object
         * can have access to it regardless of if it has a context.
         */
        fun init(context: Context): PrefsManager {
            synchronized(this) {
                INSTANCE = PrefsManager(context)

                return getInstance()
            }
        }

        /**
         * Get the single instance of [PrefsManager].
         */
        fun getInstance(): PrefsManager {
            check(::INSTANCE.isInitialized) {
                "PrefsManager must be initialized with init() before getting its instance."
            }
            return INSTANCE
        }
    }

    object Keys {
        const val KEY_LIBRARY_SORT_MODE = "KEY_LIBRARY_SORT_MODE"
    }
}

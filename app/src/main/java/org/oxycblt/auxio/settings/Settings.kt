/*
 * Copyright (c) 2023 Auxio Project
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
import androidx.preference.PreferenceManager

/**
 * Abstract user configuration information. This interface has no functionality whatsoever. Concrete
 * implementations should be preferred instead.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Settings {
    /** Migrate any settings fields from older versions into their new counterparts. */
    fun migrate() {
        throw NotImplementedError()
    }

    /**
     * Add a [SharedPreferences.OnSharedPreferenceChangeListener] to monitor for settings updates.
     * @param listener The [SharedPreferences.OnSharedPreferenceChangeListener] to add.
     */
    fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        throw NotImplementedError()
    }

    /**
     * Unregister a [SharedPreferences.OnSharedPreferenceChangeListener], preventing any further
     * settings updates from being sent to ti.t
     */
    fun removeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        throw NotImplementedError()
    }

    /**
     * A framework-backed [Settings] implementation.
     * @param context [Context] required.
     */
    abstract class Real(protected val context: Context) : Settings {
        protected val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

        override fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        }

        override fun removeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}

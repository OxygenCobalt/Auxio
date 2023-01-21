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
 
package org.oxycblt.auxio.image

import android.content.Context
import androidx.core.content.edit
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD

/**
 * User configuration specific to image loading.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface ImageSettings : Settings<ImageSettings.Listener> {
    /** The strategy to use when loading album covers. */
    val coverMode: CoverMode

    interface Listener {
        /** Called when [coverMode] changes. */
        fun onCoverModeChanged() {}
    }

    private class Real(context: Context) : Settings.Real<Listener>(context), ImageSettings {
        override val coverMode: CoverMode
            get() =
                CoverMode.fromIntCode(
                    sharedPreferences.getInt(getString(R.string.set_key_cover_mode), Int.MIN_VALUE))
                    ?: CoverMode.MEDIA_STORE

        override fun migrate() {
            // Show album covers and Ignore MediaStore covers were unified in 3.0.0
            if (sharedPreferences.contains(OLD_KEY_SHOW_COVERS) ||
                sharedPreferences.contains(OLD_KEY_QUALITY_COVERS)) {
                logD("Migrating cover settings")

                val mode =
                    when {
                        !sharedPreferences.getBoolean(OLD_KEY_SHOW_COVERS, true) -> CoverMode.OFF
                        !sharedPreferences.getBoolean(OLD_KEY_QUALITY_COVERS, true) ->
                            CoverMode.MEDIA_STORE
                        else -> CoverMode.QUALITY
                    }

                sharedPreferences.edit {
                    putInt(getString(R.string.set_key_cover_mode), mode.intCode)
                    remove(OLD_KEY_SHOW_COVERS)
                    remove(OLD_KEY_QUALITY_COVERS)
                }
            }
        }

        override fun onSettingChanged(key: String, listener: Listener) {
            if (key == getString(R.string.set_key_cover_mode)) {
                listOf(key, listener)
            }
        }

        private companion object {
            const val OLD_KEY_SHOW_COVERS = "KEY_SHOW_COVERS"
            const val OLD_KEY_QUALITY_COVERS = "KEY_QUALITY_COVERS"
        }
    }

    companion object {
        /**
         * Get a framework-backed implementation.
         * @param context [Context] required.
         */
        fun from(context: Context): ImageSettings = Real(context)
    }
}

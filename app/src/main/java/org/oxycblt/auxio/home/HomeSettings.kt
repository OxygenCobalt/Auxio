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
 
package org.oxycblt.auxio.home

import android.content.Context
import androidx.core.content.edit
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * User configuration specific to the home UI.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface HomeSettings : Settings {
    /** The tabs to show in the home UI. */
    var homeTabs: Array<Tab>
    /** Whether to hide artists considered "collaborators" from the home UI. */
    val shouldHideCollaborators: Boolean

    private class Real(context: Context) : Settings.Real(context), HomeSettings {
        override var homeTabs: Array<Tab>
            get() =
                Tab.fromIntCode(
                    sharedPreferences.getInt(
                        context.getString(R.string.set_key_lib_tabs), Tab.SEQUENCE_DEFAULT))
                    ?: unlikelyToBeNull(Tab.fromIntCode(Tab.SEQUENCE_DEFAULT))
            set(value) {
                sharedPreferences.edit {
                    putInt(context.getString(R.string.set_key_lib_tabs), Tab.toIntCode(value))
                    apply()
                }
            }

        override val shouldHideCollaborators: Boolean
            get() =
                sharedPreferences.getBoolean(
                    context.getString(R.string.set_key_hide_collaborators), false)
    }

    companion object {
        /**
         * Get a framework-backed implementation.
         * @param context [Context] required.
         */
        fun from(context: Context): HomeSettings = Real(context)
    }
}

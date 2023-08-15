/*
 * Copyright (c) 2023 Auxio Project
 * DialogAwareNavigationListener.kt is part of Auxio.
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
 
package org.oxycblt.auxio.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination

/**
 * A [NavController.OnDestinationChangedListener] that will call [callback] when moving between
 * fragments only (not between dialogs or anything similar).
 *
 * Note: This only works because of special naming used in Auxio's navigation graphs. Keep this in
 * mind when porting to other projects.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class DialogAwareNavigationListener(private val callback: () -> Unit) :
    NavController.OnDestinationChangedListener {
    private var currentDestination: NavDestination? = null

    /**
     * Attach this instance to a [NavController]. This should be done in the onStart method of a
     * Fragment.
     *
     * @param navController The [NavController] to add to.
     */
    fun attach(navController: NavController) {
        currentDestination = null
        navController.addOnDestinationChangedListener(this)
    }

    /**
     * Remove this listener from it's [NavController]. This should be done in the onStop method of a
     * Fragment.
     *
     * @param navController The [NavController] to remove from. Should be the same on used in
     *   [attach].
     */
    fun release(navController: NavController) {
        currentDestination = null
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // Drop the initial call by NavController that simply provides us with the current
        // destination. This would cause the selection state to be lost every time the device
        // rotates.
        val lastDestination = currentDestination
        currentDestination = destination
        if (lastDestination == null) {
            return
        }

        if (!lastDestination.isDialog() && !destination.isDialog()) {
            callback()
        }
    }

    private fun NavDestination.isDialog() = label?.endsWith("dialog") == true
}

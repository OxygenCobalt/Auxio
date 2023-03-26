/*
 * Copyright (c) 2023 Auxio Project
 * MainNavigationAction.kt is part of Auxio.
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
 
package org.oxycblt.auxio.navigation

import androidx.navigation.NavDirections

/**
 * Represents the possible actions within the main navigation graph. This can be used with
 * [NavigationViewModel] to initiate navigation in the main navigation graph from anywhere in the
 * app, including outside the main navigation graph.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed class MainNavigationAction {
    /** Expand the playback panel. */
    object OpenPlaybackPanel : MainNavigationAction()

    /** Collapse the playback bottom sheet. */
    object ClosePlaybackPanel : MainNavigationAction()

    /**
     * Navigate to the given [NavDirections].
     *
     * @param directions The [NavDirections] to navigate to. Assumed to be part of the main
     *   navigation graph.
     */
    data class Directions(val directions: NavDirections) : MainNavigationAction()
}

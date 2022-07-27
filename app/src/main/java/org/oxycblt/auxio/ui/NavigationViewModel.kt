/*
 * Copyright (c) 2022 Auxio Project
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

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logD

/**
 * A ViewModel that handles complicated navigation situations.
 * @author OxygenCobalt
 */
class NavigationViewModel : ViewModel() {
    private val _mainNavigationAction = MutableStateFlow<MainNavigationAction?>(null)
    /** Flag for main fragment navigation. Intended for MainFragment use only. */
    val mainNavigationAction: StateFlow<MainNavigationAction?>
        get() = _mainNavigationAction

    private val _exploreNavigationItem = MutableStateFlow<Music?>(null)
    /**
     * Flag for navigation within the explore fragments. Observe this to coordinate navigation to an
     * item's UI.
     */
    val exploreNavigationItem: StateFlow<Music?>
        get() = _exploreNavigationItem

    /** Notify MainFragment to navigate to the location outlined in [MainNavigationAction]. */
    fun mainNavigateTo(action: MainNavigationAction) {
        if (_mainNavigationAction.value != null) {
            logD("Already navigating, not doing main action")
            return
        }

        logD("Navigating with action $action")
        _mainNavigationAction.value = action
    }

    /** Mark that the main navigation process is done. */
    fun finishMainNavigation() {
        logD("Finishing main navigation process")
        _mainNavigationAction.value = null
    }

    /** Navigate to an item's detail menu, whether a song/album/artist */
    fun exploreNavigateTo(item: Music) {
        if (_exploreNavigationItem.value != null) {
            logD("Already navigation, not doing explore action")
            return
        }

        logD("Navigating to ${item.rawName}")
        _exploreNavigationItem.value = item
    }

    /** Mark that the item navigation process is done. */
    fun finishExploreNavigation() {
        logD("Finishing explore navigation process")
        _exploreNavigationItem.value = null
    }
}

/**
 * Represents the navigation options for the Main Fragment, which tends to be multiple layers above
 * normal fragments. This can be passed to [NavigationViewModel.mainNavigateTo] in order to
 * facilitate navigation without stupid fragment hacks.
 */
sealed class MainNavigationAction {
    /** Expand the playback panel. */
    object Expand : MainNavigationAction()
    /** Collapse the playback panel. */
    object Collapse : MainNavigationAction()
    /** Go to settings. */
    object Settings : MainNavigationAction()
    /** Go to the about page. */
    object About : MainNavigationAction()
    /** Show song details. */
    data class SongDetails(val song: Song) : MainNavigationAction()
}

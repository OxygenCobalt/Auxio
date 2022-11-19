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
import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
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

    private val _exploreNavigationArtists = MutableStateFlow<List<Artist>?>(null)

    /**
     * Flag for navigation within the explore fragments. In this case, it involves an ambiguous list
     * of artist choices.
     */
    val exploreNavigationArtists: StateFlow<List<Artist>?>
        get() = _exploreNavigationArtists

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
            logD("Already navigating, not doing explore action")
            return
        }

        logD("Navigating to ${item.rawName}")
        _exploreNavigationItem.value = item
    }

    /** Navigate to one item out of a list of items. */
    fun exploreNavigateTo(items: List<Artist>) {
        if (_exploreNavigationArtists.value != null) {
            logD("Already navigating, not doing explore action")
            return
        }

        if (items.size == 1) {
            exploreNavigateTo(items[0])
        } else {
            logD("Navigating to a choice of ${items.map { it.rawName }}")
            _exploreNavigationArtists.value = items
        }
    }

    /** Mark that the item navigation process is done. */
    fun finishExploreNavigation() {
        logD("Finishing explore navigation process")
        _exploreNavigationItem.value = null
        _exploreNavigationArtists.value = null
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

    /** Provide raw navigation directions. */
    data class Directions(val directions: NavDirections) : MainNavigationAction()
}

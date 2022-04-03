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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Music

/**
 * A ViewModel that handles complicated navigation situations.
 */
class NavigationViewModel : ViewModel() {
    private val mMainNavigationAction = MutableLiveData<MainNavigationAction?>()
    /** Flag for main fragment navigation. Intended for MainFragment use only. */
    val mainNavigationAction: LiveData<MainNavigationAction?>
        get() = mMainNavigationAction

    private val mExploreNavigationItem = MutableLiveData<Music?>()
    /** Flag for unified navigation. Observe this to coordinate navigation to an item's UI. */
    val exploreNavigationItem: LiveData<Music?>
        get() = mExploreNavigationItem

    /**
     * Notify MainFragment to navigate to the location outlined in [MainNavigationAction].
     */
    fun mainNavigateTo(action: MainNavigationAction) {
        if (mMainNavigationAction.value != null) return
        mMainNavigationAction.value = action
    }

    /** Mark that the main navigation process is done. */
    fun finishMainNavigation() {
        mMainNavigationAction.value = null
    }

    /** Navigate to an item's detail menu, whether a song/album/artist */
    fun exploreNavigateTo(item: Music) {
        if (mExploreNavigationItem.value != null) return
        mExploreNavigationItem.value = item
    }

    /** Mark that the item navigation process is done. */
    fun finishExploreNavigation() {
        mExploreNavigationItem.value = null
    }
}

/**
 * Represents the navigation options for the Main Fragment, which tends to be multiple layers above
 * normal fragments. This can be passed to [NavigationViewModel.mainNavigateTo] in order to
 * facilitate navigation without stupid fragment hacks.
 */
enum class MainNavigationAction {
    /** Expand the playback panel. */
    EXPAND,
    /** Collapse the playback panel. */
    COLLAPSE,
    /** Go to settings. */
    SETTINGS,
    /** Go to the about page. */
    ABOUT,
    /** Go to the queue. */
    QUEUE
}

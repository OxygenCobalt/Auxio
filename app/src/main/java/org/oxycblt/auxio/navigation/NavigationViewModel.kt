/*
 * Copyright (c) 2022 Auxio Project
 * NavigationViewModel.kt is part of Auxio.
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

import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent
import org.oxycblt.auxio.util.logD

/**
 * A [ViewModel] that handles complicated navigation functionality.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: This whole system is very jankily designed, perhaps it's time for a refactor?
 */
class NavigationViewModel : ViewModel() {
    private val _mainNavigationAction = MutableEvent<MainNavigationAction>()
    /**
     * Flag for navigation within the main navigation graph. Only intended for use by MainFragment.
     */
    val mainNavigationAction: Event<MainNavigationAction>
        get() = _mainNavigationAction

    private val _exploreNavigationItem = MutableEvent<Music>()
    /**
     * Flag for navigation within the explore navigation graph. Observe this to coordinate
     * navigation to a specific [Music] item.
     */
    val exploreNavigationItem: Event<Music>
        get() = _exploreNavigationItem

    private val _exploreArtistNavigationItem = MutableEvent<Music>()
    /**
     * Variation of [exploreNavigationItem] for situations where the choice of parent [Artist] to
     * navigate to is ambiguous. Only intended for use by MainFragment, as the resolved choice will
     * eventually be assigned to [exploreNavigationItem].
     */
    val exploreArtistNavigationItem: Event<Music>
        get() = _exploreArtistNavigationItem

    /**
     * Navigate to something in the main navigation graph. This can be used by UIs in the explore
     * navigation graph to trigger navigation in the higher-level main navigation graph. Will do
     * nothing if already navigating.
     *
     * @param action The [MainNavigationAction] to perform.
     */
    fun mainNavigateTo(action: MainNavigationAction) {
        if (_mainNavigationAction.flow.value != null) {
            logD("Already navigating, not doing main action")
            return
        }
        logD("Navigating with action $action")
        _mainNavigationAction.put(action)
    }

    /**
     * Navigate to a given [Music] item. Will do nothing if already navigating.
     *
     * @param music The [Music] to navigate to.
     */
    fun exploreNavigateTo(music: Music) {
        if (_exploreNavigationItem.flow.value != null) {
            logD("Already navigating, not doing explore action")
            return
        }
        logD("Navigating to ${music.rawName}")
        _exploreNavigationItem.put(music)
    }

    /**
     * Navigate to one of the parent [Artist]'s of the given [Song].
     *
     * @param song The [Song] to navigate with. If there are multiple parent [Artist]s, a picker
     *   dialog will be shown.
     */
    fun exploreNavigateToParentArtist(song: Song) {
        exploreNavigateToParentArtistImpl(song, song.artists)
    }

    /**
     * Navigate to one of the parent [Artist]'s of the given [Album].
     *
     * @param album The [Album] to navigate with. If there are multiple parent [Artist]s, a picker
     *   dialog will be shown.
     */
    fun exploreNavigateToParentArtist(album: Album) {
        exploreNavigateToParentArtistImpl(album, album.artists)
    }

    private fun exploreNavigateToParentArtistImpl(item: Music, artists: List<Artist>) {
        if (_exploreArtistNavigationItem.flow.value != null) {
            logD("Already navigating, not doing explore action")
            return
        }

        if (artists.size == 1) {
            exploreNavigateTo(artists[0])
        } else {
            logD("Navigating to a choice of ${artists.map { it.rawName }}")
            _exploreArtistNavigationItem.put(item)
        }
    }
}

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
 
package org.oxycblt.auxio.picker

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.model.Library
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * a [ViewModel] that manages the current music picker state. Make it so that the dialogs just
 * contain the music themselves and then exit if the library changes.
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class PickerViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.Listener {

    private val _currentItem = MutableStateFlow<Music?>(null)
    /** The current item whose artists should be shown in the picker. Null if there is no item. */
    val currentItem: StateFlow<Music?>
        get() = _currentItem

    private val _artistChoices = MutableStateFlow<List<Artist>>(listOf())
    /** The current [Artist] choices. Empty if no item is shown in the picker. */
    val artistChoices: StateFlow<List<Artist>>
        get() = _artistChoices

    private val _genreChoices = MutableStateFlow<List<Genre>>(listOf())
    /** The current [Genre] choices. Empty if no item is shown in the picker. */
    val genreChoices: StateFlow<List<Genre>>
        get() = _genreChoices

    override fun onCleared() {
        musicRepository.removeListener(this)
    }

    override fun onLibraryChanged(library: Library?) {
        if (library != null) {
            refreshChoices()
        }
    }

    /**
     * Set a new [currentItem] from it's [Music.UID].
     * @param uid The [Music.UID] of the [Song] to update to.
     */
    fun setItemUid(uid: Music.UID) {
        val library = unlikelyToBeNull(musicRepository.library)
        _currentItem.value = library.find(uid)
        refreshChoices()
    }

    private fun refreshChoices() {
        when (val item = _currentItem.value) {
            is Song -> {
                _artistChoices.value = item.artists
                _genreChoices.value = item.genres
            }
            is Album -> _artistChoices.value = item.artists
            else -> {}
        }
    }
}

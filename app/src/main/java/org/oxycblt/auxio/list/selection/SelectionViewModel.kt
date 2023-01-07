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
 
package org.oxycblt.auxio.list.selection

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.library.Library

/**
 * A [ViewModel] that manages the current selection.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SelectionViewModel : ViewModel(), MusicStore.Listener {
    private val musicStore = MusicStore.getInstance()

    private val _selected = MutableStateFlow(listOf<Music>())
    /** the currently selected items. These are ordered in earliest selected and latest selected. */
    val selected: StateFlow<List<Music>>
        get() = _selected

    init {
        musicStore.addListener(this)
    }

    override fun onLibraryChanged(library: Library?) {
        if (library == null) {
            return
        }

        // Sanitize the selection to remove items that no longer exist and thus
        // won't appear in any list.
        _selected.value =
            _selected.value.mapNotNull {
                when (it) {
                    is Song -> library.sanitize(it)
                    is Album -> library.sanitize(it)
                    is Artist -> library.sanitize(it)
                    is Genre -> library.sanitize(it)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        musicStore.removeListener(this)
    }

    /**
     * Select a new [Music] item. If this item is already within the selected items, the item will
     * be removed. Otherwise, it will be added.
     * @param music The [Music] item to select.
     */
    fun select(music: Music) {
        val selected = _selected.value.toMutableList()
        if (!selected.remove(music)) {
            selected.add(music)
        }
        _selected.value = selected
    }

    /**
     * Consume the current selection. This will clear any items that were selected prior.
     * @return The list of selected items before it was cleared.
     */
    fun consume() = _selected.value.also { _selected.value = listOf() }
}

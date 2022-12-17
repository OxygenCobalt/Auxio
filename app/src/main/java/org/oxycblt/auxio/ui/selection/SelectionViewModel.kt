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
 
package org.oxycblt.auxio.ui.selection

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.util.logD

/**
 * ViewModel that manages the current selection.
 * @author OxygenCobalt
 */
class SelectionViewModel : ViewModel() {
    private val _selected = MutableStateFlow(listOf<Music>())
    val selected: StateFlow<List<Music>>
        get() = _selected

    /** Select a music item. */
    fun select(item: Music) {
        val items = _selected.value.toMutableList()
        if (items.remove(item)) {
            logD("Unselecting item $item")
            _selected.value = items
        } else {
            logD("Selecting item $item")
            items.add(item)
            _selected.value = items
        }
    }

    /** Clear and return all selected items. */
    fun consume(): List<Music> {
        return _selected.value.also {
            _selected.value = listOf()
        }
    }

    override fun onCleared() {
        super.onCleared()
        logD("Cleared")
    }
}

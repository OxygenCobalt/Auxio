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
    fun select(music: Music) {
        val selected = _selected.value.toMutableList()
        if (selected.remove(music)) {
            logD("Unselecting item $music")
            _selected.value = selected
        } else {
            logD("Selecting item $music")
            selected.add(music)
            _selected.value = selected
        }
    }

    /** Clear and return all selected items. */
    fun consume() = _selected.value.also { _selected.value = listOf() }

    override fun onCleared() {
        super.onCleared()
        logD("Cleared")
    }
}

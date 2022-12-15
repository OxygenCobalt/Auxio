package org.oxycblt.auxio.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.util.logD

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
}
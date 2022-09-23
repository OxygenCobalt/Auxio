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
 
package org.oxycblt.auxio.music.picker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A small ViewModel holding and updating the current song being shown in the picker UI.
 * @author OxygenCobalt
 */
class PickerViewModel : ViewModel(), MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()

    private var _currentItem = MutableStateFlow<Music?>(null)
    val currentItem: StateFlow<Music?> = _currentItem

    fun setSongUid(uid: Music.UID) {
        if (_currentItem.value?.uid == uid) return
        val library = unlikelyToBeNull(musicStore.library)
        val item = requireNotNull(library.find(uid)) { "Invalid song id provided" }
        _currentItem.value = item
    }

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            when (val item = currentItem.value) {
                is Song -> {
                    _currentItem.value = library.sanitize(item)
                }
                is Album -> {
                    _currentItem.value = library.sanitize(item)
                }
                null -> {}
                else -> error("Invalid datatype: ${item::class.java}")
            }
        }
    }
}

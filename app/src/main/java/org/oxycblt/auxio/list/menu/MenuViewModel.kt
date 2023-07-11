/*
 * Copyright (c) 2023 Auxio Project
 * MenuViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.menu

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.util.logW

/**
 * Manages the state information for [MenuDialogFragment] implementations.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class MenuViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentMusic = MutableStateFlow<Music?>(null)
    /** The current [Music] information being shown in a menu dialog. */
    val currentMusic: StateFlow<Music?> = _currentMusic

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        _currentMusic.value = _currentMusic.value?.let { musicRepository.find(it.uid) }
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Set a new [currentMusic] from it's [Music.UID]. [currentMusic] will be updated to align with
     * the new album.
     *
     * @param uid The [Music.UID] of the [Music] to update [currentMusic] to. Must be valid.
     */
    fun setMusic(uid: Music.UID) {
        _currentMusic.value = musicRepository.find(uid)
        if (_currentMusic.value == null) {
            logW("Given Music UID to show was invalid")
        }
    }
}

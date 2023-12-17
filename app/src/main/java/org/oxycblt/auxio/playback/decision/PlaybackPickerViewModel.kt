/*
 * Copyright (c) 2023 Auxio Project
 * PlaybackPickerViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.decision

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * A [ViewModel] that stores the choices shown in the playback picker dialogs.
 *
 * @author OxygenCobalt (Alexander Capehart)
 */
@HiltViewModel
class PlaybackPickerViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentPickerSong = MutableStateFlow<Song?>(null)
    /** The current set of [Artist] choices to show in the picker, or null if to show nothing. */
    val currentPickerSong: StateFlow<Song?>
        get() = _currentPickerSong

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (!changes.deviceLibrary) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        _currentPickerSong.value = _currentPickerSong.value?.run { deviceLibrary.findSong(uid) }
    }

    override fun onCleared() {
        super.onCleared()
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Set the [Music.UID] of the [Song] to show choices for.
     *
     * @param uid The [Music.UID] of the item to show. Must be a [Song].
     */
    fun setPickerSongUid(uid: Music.UID) {
        logD("Opening picker for song $uid")
        _currentPickerSong.value = musicRepository.deviceLibrary?.findSong(uid)
        if (_currentPickerSong.value != null) {
            logW("Given song UID was invalid")
        }
    }
}

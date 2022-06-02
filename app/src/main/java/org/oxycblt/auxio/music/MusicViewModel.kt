/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.music

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.util.logD

/** A [ViewModel] that represents the current music indexing state. */
class MusicViewModel : ViewModel(), MusicStore.LoadCallback {
    private val musicStore = MusicStore.getInstance()

    private val _loadState = MutableStateFlow<MusicStore.LoadState?>(null)
    val loadState: StateFlow<MusicStore.LoadState?> = _loadState

    private var isBusy = false

    /**
     * Initiate the loading process. This is done here since HomeFragment will be the first fragment
     * navigated to and because SnackBars will have the best UX here.
     */
    fun loadMusic(context: Context) {
        if (_loadState.value != null || isBusy) {
            logD("Loader is busy/already completed, not reloading")
            return
        }

        isBusy = true
        _loadState.value = null

        viewModelScope.launch {
            musicStore.load(context, this@MusicViewModel)
            isBusy = false
        }
    }

    /**
     * Reload the music library. Note that this call will result in unexpected behavior in the case
     * that music is reloaded after a loading process has already exceeded.
     */
    fun reloadMusic(context: Context) {
        logD("Reloading music library")
        _loadState.value = null
        loadMusic(context)
    }

    override fun onLoadStateChanged(state: MusicStore.LoadState?) {
        _loadState.value = state
    }
}

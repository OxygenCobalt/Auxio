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

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.system.Indexer

/**
 * A ViewModel representing the current indexing state.
 * @author OxygenCobalt
 */
class MusicViewModel : ViewModel(), Indexer.Callback {
    private val indexer = Indexer.getInstance()

    private val _indexerState = MutableStateFlow<Indexer.State?>(null)
    /** The current music indexing state. */
    val indexerState: StateFlow<Indexer.State?> = _indexerState

    private val _libraryExists = MutableStateFlow(false)
    /** Whether a music library has successfully been loaded. */
    val libraryExists: StateFlow<Boolean> = _libraryExists

    init {
        indexer.registerCallback(this)
    }

    fun reindex() {
        indexer.requestReindex()
    }

    override fun onIndexerStateChanged(state: Indexer.State?) {
        _indexerState.value = state

        if (state is Indexer.State.Complete && state.response is Indexer.Response.Ok) {
            _libraryExists.value = true
        }
    }

    override fun onCleared() {
        indexer.unregisterCallback(this)
    }
}

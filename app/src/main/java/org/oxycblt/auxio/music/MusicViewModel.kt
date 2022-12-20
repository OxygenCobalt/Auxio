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
import org.oxycblt.auxio.util.logD

/**
 * A ViewModel representing the current indexing state.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicViewModel : ViewModel(), Indexer.Callback {
    private val indexer = Indexer.getInstance()

    private val _indexerState = MutableStateFlow<Indexer.State?>(null)
    /** The current music indexing state. */
    val indexerState: StateFlow<Indexer.State?> = _indexerState

    private val _statistics = MutableStateFlow<Statistics?>(null)
    /** The current statistics of the music library. */
    val statistics: StateFlow<Statistics?>
        get() = _statistics

    init {
        indexer.registerCallback(this)
    }

    /** Re-index the music library while using the cache. */
    fun reindex(ignoreCache: Boolean) {
        indexer.requestReindex(ignoreCache)
    }

    override fun onIndexerStateChanged(state: Indexer.State?) {
        logD("New state: $state")
        _indexerState.value = state

        if (state is Indexer.State.Complete && state.response is Indexer.Response.Ok) {
            val library = state.response.library
            _statistics.value =
                Statistics(
                    library.songs.size,
                    library.albums.size,
                    library.artists.size,
                    library.genres.size,
                    library.songs.sumOf { it.durationMs })
        }
    }

    override fun onCleared() {
        indexer.unregisterCallback(this)
    }

    /** Non-manipulated statistics about the music library. */
    data class Statistics(
        /** The amount of songs. */
        val songs: Int,
        /** The amount of albums. */
        val albums: Int,
        /** The amount of artists. */
        val artists: Int,
        /** The amount of genres. */
        val genres: Int,
        /** The total duration of the music library. */
        val durationMs: Long
    )
}

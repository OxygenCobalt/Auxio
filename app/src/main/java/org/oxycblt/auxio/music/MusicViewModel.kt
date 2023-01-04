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
 * A [ViewModel] providing data specific to the music loading process.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicViewModel : ViewModel(), Indexer.Listener {
    private val indexer = Indexer.getInstance()

    private val _indexerState = MutableStateFlow<Indexer.State?>(null)
    /** The current music loading state, or null if no loading is going on. */
    val indexerState: StateFlow<Indexer.State?> = _indexerState

    private val _statistics = MutableStateFlow<Statistics?>(null)
    /** [Statistics] about the last completed music load. */
    val statistics: StateFlow<Statistics?>
        get() = _statistics

    init {
        indexer.registerListener(this)
    }

    override fun onCleared() {
        indexer.unregisterListener(this)
    }

    override fun onIndexerStateChanged(state: Indexer.State?) {
        _indexerState.value = state
        if (state is Indexer.State.Complete) {
            // New state is a completed library, update the statistics values.
            val library = state.result.getOrNull() ?: return
            _statistics.value =
                Statistics(
                    library.songs.size,
                    library.albums.size,
                    library.artists.size,
                    library.genres.size,
                    library.songs.sumOf { it.durationMs })
        }
    }

    /** Requests that the music library should be re-loaded while leveraging the cache. */
    fun refresh() {
        indexer.requestReindex(true)
    }

    /** Requests that the music library be re-loaded without the cache. */
    fun rescan() {
        indexer.requestReindex(false)
    }

    /**
     * Non-manipulated statistics bound the last successful music load.
     * @param songs The amount of [Song]s that were loaded.
     * @param albums The amount of [Album]s that were created.
     * @param artists The amount of [Artist]s that were created.
     * @param genres The amount of [Genre]s that were created.
     * @param durationMs The total duration of all songs in the library, in milliseconds.
     */
    data class Statistics(
        val songs: Int,
        val albums: Int,
        val artists: Int,
        val genres: Int,
        val durationMs: Long
    )
}

/*
 * Copyright (c) 2021 Auxio Project
 * MusicViewModel.kt is part of Auxio.
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.dialog.PendingPlaylist
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent

/**
 * A [ViewModel] providing data specific to the music loading process.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class MusicViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener, MusicRepository.IndexingListener {

    private val _indexingState = MutableStateFlow<IndexingState?>(null)
    /** The current music loading state, or null if no loading is going on. */
    val indexingState: StateFlow<IndexingState?> = _indexingState

    private val _statistics = MutableStateFlow<Statistics?>(null)
    /** [Statistics] about the last completed music load. */
    val statistics: StateFlow<Statistics?>
        get() = _statistics

    private val _pendingNewPlaylist = MutableEvent<PendingPlaylist?>()
    val pendingNewPlaylist: Event<PendingPlaylist?> = _pendingNewPlaylist

    init {
        musicRepository.addUpdateListener(this)
        musicRepository.addIndexingListener(this)
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
        musicRepository.removeIndexingListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (!changes.deviceLibrary) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        _statistics.value =
            Statistics(
                deviceLibrary.songs.size,
                deviceLibrary.albums.size,
                deviceLibrary.artists.size,
                deviceLibrary.genres.size,
                deviceLibrary.songs.sumOf { it.durationMs })
    }

    override fun onIndexingStateChanged() {
        _indexingState.value = musicRepository.indexingState
    }

    /** Requests that the music library should be re-loaded while leveraging the cache. */
    fun refresh() {
        musicRepository.requestIndex(true)
    }

    /** Requests that the music library be re-loaded without the cache. */
    fun rescan() {
        musicRepository.requestIndex(false)
    }

    /**
     * Create a new generic playlist. This will automatically generate a playlist name and then
     * prompt the user to edit the name before the creation finished.
     *
     * @param context The [Context] required to generate the playlist name.
     * @param songs The [Song]s to be contained in the new playlist.
     */
    fun createPlaylist(context: Context, songs: List<Song> = listOf()) {
        val userLibrary = musicRepository.userLibrary ?: return
        var i = 1
        while (true) {
            val possibleName = context.getString(R.string.fmt_def_playlist, i)
            if (userLibrary.playlists.none { it.name.resolve(context) == possibleName }) {
                createPlaylist(possibleName, songs)
                return
            }
            ++i
        }
    }

    /**
     * Create a new generic playlist. This will prompt the user to edit the name before the creation
     * finishes.
     *
     * @param name The preferred name of the new playlist.
     * @param songs The [Song]s to be contained in the new playlist.
     */
    fun createPlaylist(name: String, songs: List<Song> = listOf()) {
        // TODO: Attempt to unify playlist creation flow with dialog model
        _pendingNewPlaylist.put(PendingPlaylist(name, songs.map { it.uid }))
    }

    /**
     * Non-manipulated statistics bound the last successful music load.
     *
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

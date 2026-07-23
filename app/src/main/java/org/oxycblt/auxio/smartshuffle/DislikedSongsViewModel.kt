/*
 * Copyright (c) 2026 Auxio Project
 * DislikedSongsViewModel.kt is part of Auxio.
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

package org.oxycblt.auxio.smartshuffle

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Song
import timber.log.Timber as L

data class DislikedSongItem(val song: Song, val stats: SongStats)

@HiltViewModel
class DislikedSongsViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val smartShuffle: SmartShuffle,
    private val musicRepository: MusicRepository,
) : ViewModel() {
    private val _songs = MutableStateFlow<List<DislikedSongItem>>(emptyList())
    val songs: StateFlow<List<DislikedSongItem>>
        get() = _songs

    fun refresh() {
        val library = musicRepository.library
        if (library == null) {
            _songs.value = emptyList()
            return
        }

        _songs.value =
            smartShuffle.undesirableEntries().mapNotNull { (uidString, stats) ->
                val uid = Music.UID.fromString(uidString) ?: return@mapNotNull null
                val song = library.findSong(uid) ?: return@mapNotNull null
                DislikedSongItem(song, stats)
            }
    }

    fun forgive(song: Song) {
        smartShuffle.forgive(song.uid.toString())
        refresh()
    }

    /** Deletes the song file via MediaStore when possible. Returns true on success. */
    fun delete(song: Song): Boolean {
        val deleted = deleteUri(song.uri)
        if (deleted) {
            smartShuffle.removeSong(song.uid.toString())
            refresh()
        }
        return deleted
    }

    private fun deleteUri(uri: Uri): Boolean {
        return try {
            context.contentResolver.delete(uri, null, null) > 0
        } catch (e: SecurityException) {
            L.w(e, "Unable to delete song at $uri")
            false
        } catch (e: Exception) {
            L.w(e, "Unable to delete song at $uri")
            false
        }
    }
}

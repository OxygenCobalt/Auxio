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
 
package org.oxycblt.auxio.detail

import android.app.Application
import android.media.MediaExtractor
import android.media.MediaFormat
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.recycler.Header
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.util.TaskGuard
import org.oxycblt.auxio.util.application
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * ViewModel that stores data for the detail fragments. This includes:
 * - What item the fragment should be showing
 * - The RecyclerView data for each fragment
 * - The sorts for each type of data
 * @author OxygenCobalt
 *
 * TODO: Unify how detail items are indicated [When playlists are implemented]
 */
class DetailViewModel(application: Application) :
    AndroidViewModel(application), MusicStore.Callback {
    data class DetailSong(val song: Song, val info: SongInfo?)

    data class SongInfo(
        val bitrateKbps: Int?,
        val sampleRate: Int?,
        val resolvedMimeType: MimeType
    )

    private val musicStore = MusicStore.getInstance()
    private val settings = Settings(application)

    private val _currentSong = MutableStateFlow<DetailSong?>(null)
    val currentSong: StateFlow<DetailSong?>
        get() = _currentSong

    private val _currentAlbum = MutableStateFlow<Album?>(null)
    val currentAlbum: StateFlow<Album?>
        get() = _currentAlbum

    private val _albumData = MutableStateFlow(listOf<Item>())
    val albumData: StateFlow<List<Item>>
        get() = _albumData

    var albumSort: Sort
        get() = settings.detailAlbumSort
        set(value) {
            settings.detailAlbumSort = value
            currentAlbum.value?.let(::refreshAlbumData)
        }

    private val _currentArtist = MutableStateFlow<Artist?>(null)
    val currentArtist: StateFlow<Artist?>
        get() = _currentArtist

    private val _artistData = MutableStateFlow(listOf<Item>())
    val artistData: StateFlow<List<Item>> = _artistData

    var artistSort: Sort
        get() = settings.detailArtistSort
        set(value) {
            logD(value)
            settings.detailArtistSort = value
            currentArtist.value?.let(::refreshArtistData)
        }

    private val _currentGenre = MutableStateFlow<Genre?>(null)
    val currentGenre: StateFlow<Genre?>
        get() = _currentGenre

    private val _genreData = MutableStateFlow(listOf<Item>())
    val genreData: StateFlow<List<Item>> = _genreData

    var genreSort: Sort
        get() = settings.detailGenreSort
        set(value) {
            settings.detailGenreSort = value
            currentGenre.value?.let(::refreshGenreData)
        }

    private val songGuard = TaskGuard()

    fun setSongId(id: Long) {
        if (_currentSong.value?.run { song.id } == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val song = requireNotNull(library.songs.find { it.id == id }) { "Invalid song id provided" }
        generateDetailSong(song)
    }

    fun clearSong() {
        songGuard.newHandle()
        _currentSong.value = null
    }

    fun setAlbumId(id: Long) {
        if (_currentAlbum.value?.id == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val album =
            requireNotNull(library.albums.find { it.id == id }) { "Invalid album id provided " }

        _currentAlbum.value = album
        refreshAlbumData(album)
    }

    fun setArtistId(id: Long) {
        if (_currentArtist.value?.id == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val artist =
            requireNotNull(library.artists.find { it.id == id }) { "Invalid artist id provided" }
        _currentArtist.value = artist
        refreshArtistData(artist)
    }

    fun setGenreId(id: Long) {
        if (_currentGenre.value?.id == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val genre =
            requireNotNull(library.genres.find { it.id == id }) { "Invalid genre id provided" }
        _currentGenre.value = genre
        refreshGenreData(genre)
    }

    init {
        musicStore.addCallback(this)
    }

    private fun generateDetailSong(song: Song) {
        _currentSong.value = DetailSong(song, null)
        viewModelScope.launch(Dispatchers.IO) {
            val handle = songGuard.newHandle()
            val info = generateDetailSongInfo(song)
            songGuard.yield(handle)
            _currentSong.value = DetailSong(song, info)
        }
    }

    private fun generateDetailSongInfo(song: Song): SongInfo {
        val extractor = MediaExtractor()

        try {
            extractor.setDataSource(application, song.uri, emptyMap())
        } catch (e: Exception) {
            logW("Unable to extract song attributes.")
            logW(e.stackTraceToString())
            return SongInfo(null, null, song.mimeType)
        }

        val format = extractor.getTrackFormat(0)

        val bitrate =
            try {
                format.getInteger(MediaFormat.KEY_BIT_RATE) / 1000 // bps -> kbps
            } catch (e: Exception) {
                null
            }

        val sampleRate =
            try {
                format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            } catch (e: Exception) {
                null
            }

        val resolvedMimeType =
            if (song.mimeType.fromFormat != null) {
                // ExoPlayer was already able to populate the format.
                song.mimeType
            } else {
                val formatMimeType =
                    try {
                        format.getString(MediaFormat.KEY_MIME)
                    } catch (e: Exception) {
                        null
                    }

                MimeType(song.mimeType.fromExtension, formatMimeType)
            }

        return SongInfo(bitrate, sampleRate, resolvedMimeType)
    }

    private fun refreshAlbumData(album: Album) {
        logD("Refreshing album data")
        val data = mutableListOf<Item>(album)
        data.add(SortHeader(R.string.lbl_songs))

        // To create a good user experience regarding disc numbers, we intersperse
        // items that show the disc number throughout the album's songs. In the case
        // that the album does not have distinct disc numbers, we omit such a header.
        val songs = albumSort.songs(album.songs)
        val byDisc = songs.groupBy { it.disc ?: 1 }
        if (byDisc.size > 1) {
            for (entry in byDisc.entries) {
                val disc = entry.key
                val discSongs = entry.value
                data.add(DiscHeader(disc)) // Ensure ID uniqueness
                data.addAll(discSongs)
            }
        } else {
            data.addAll(songs)
        }

        _albumData.value = data
    }

    private fun refreshArtistData(artist: Artist) {
        logD("Refreshing artist data")
        val data = mutableListOf<Item>(artist)
        val albums = Sort(Sort.Mode.ByYear, false).albums(artist.albums)

        val byReleaseGroup =
            albums.groupBy {
                if (it.releaseType == null) {
                    return@groupBy R.string.lbl_albums
                }

                when (it.releaseType.refinement) {
                    null ->
                        when (it.releaseType) {
                            is ReleaseType.Album -> R.string.lbl_albums
                            is ReleaseType.EP -> R.string.lbl_eps
                            is ReleaseType.Single -> R.string.lbl_singles
                            is ReleaseType.Compilation -> R.string.lbl_compilations
                            is ReleaseType.Soundtrack -> R.string.lbl_soundtracks
                            is ReleaseType.Mixtape -> R.string.lbl_mixtapes
                        }
                    ReleaseType.Refinement.LIVE -> R.string.lbl_live_group
                    ReleaseType.Refinement.REMIX -> R.string.lbl_remix_group
                }
            }

        for (entry in byReleaseGroup.entries.sortedBy { it.key }) {
            data.add(Header(entry.key))
            data.addAll(entry.value)
        }

        data.add(SortHeader(R.string.lbl_songs))
        data.addAll(artistSort.songs(artist.songs))
        _artistData.value = data.toList()
    }

    private fun refreshGenreData(genre: Genre) {
        logD("Refreshing genre data")
        val data = mutableListOf<Item>(genre)
        data.add(SortHeader(R.string.lbl_songs))
        data.addAll(genreSort.songs(genre.songs))
        _genreData.value = data
    }

    // --- CALLBACKS ---

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            val song = currentSong.value
            if (song != null) {
                logD("Song changed, refreshing data")
                val newSong = library.sanitize(song.song)
                if (newSong != null) {
                    generateDetailSong(newSong)
                } else {
                    _currentSong.value = null
                }
            }

            val album = currentAlbum.value
            if (album != null) {
                logD("Album changed, refreshing data")
                val newAlbum = library.sanitize(album).also { _currentAlbum.value = it }
                if (newAlbum != null) {
                    refreshAlbumData(newAlbum)
                }
            }

            val artist = currentArtist.value
            if (artist != null) {
                logD("Artist changed, refreshing data")
                val newArtist = library.sanitize(artist).also { _currentArtist.value = it }
                if (newArtist != null) {
                    refreshArtistData(newArtist)
                }
            }

            val genre = currentGenre.value
            if (genre != null) {
                logD("Genre changed, refreshing data")
                val newGenre = library.sanitize(genre).also { _currentGenre.value = it }
                if (newGenre != null) {
                    refreshGenreData(newGenre)
                }
            }
        }
    }

    override fun onCleared() {
        musicStore.removeCallback(this)
    }
}

data class SortHeader(@StringRes val string: Int) : Item() {
    override val id: Long
        get() = string.toLong()
}

data class DiscHeader(val disc: Int) : Item() {
    override val id: Long
        get() = disc.toLong()
}

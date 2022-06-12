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

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.recycler.DiscHeader
import org.oxycblt.auxio.detail.recycler.SortHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MimeType
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.Header
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * ViewModel that stores data for the [DetailFragment]s. This includes:
 * - What item the fragment should be showing
 * - The RecyclerView data for each fragment
 * - Menu triggers for each fragment
 * @author OxygenCobalt
 */
class DetailViewModel : ViewModel(), MusicStore.Callback {
    data class DetailSong(
        val song: Song,
        val bitrateKbps: Int?,
        val sampleRate: Int?,
        val resolvedMimeType: MimeType
    )

    private val musicStore = MusicStore.getInstance()
    private val settingsManager = SettingsManager.getInstance()

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
        get() = settingsManager.detailAlbumSort
        set(value) {
            settingsManager.detailAlbumSort = value
            currentAlbum.value?.let(::refreshAlbumData)
        }

    private val _currentArtist = MutableStateFlow<Artist?>(null)
    val currentArtist: StateFlow<Artist?>
        get() = _currentArtist

    private val _artistData = MutableStateFlow(listOf<Item>())
    val artistData: StateFlow<List<Item>> = _artistData

    var artistSort: Sort
        get() = settingsManager.detailArtistSort
        set(value) {
            settingsManager.detailArtistSort = value
            currentArtist.value?.let(::refreshArtistData)
        }

    private val _currentGenre = MutableStateFlow<Genre?>(null)
    val currentGenre: StateFlow<Genre?>
        get() = _currentGenre

    private val _genreData = MutableStateFlow(listOf<Item>())
    val genreData: StateFlow<List<Item>> = _genreData

    var genreSort: Sort
        get() = settingsManager.detailGenreSort
        set(value) {
            settingsManager.detailGenreSort = value
            currentGenre.value?.let(::refreshGenreData)
        }

    fun setSongId(context: Context, id: Long) {
        if (_currentSong.value?.run { song.id } == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val song = requireNotNull(library.songs.find { it.id == id }) { "Invalid song id provided" }
        generateDetailSong(context, song)
    }

    fun clearSong() {
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

    private fun generateDetailSong(context: Context, song: Song) {
        viewModelScope.launch {
            _currentSong.value =
                withContext(Dispatchers.IO) {
                    val extractor = MediaExtractor()

                    try {
                        extractor.setDataSource(context, song.uri, emptyMap())
                    } catch (e: Exception) {
                        logW("Unable to extract song attributes.")
                        logW(e.stackTraceToString())
                        return@withContext DetailSong(song, null, null, song.mimeType)
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

                            // Ensure that we don't include the functionally useless
                            // "audio/raw" mime type
                            MimeType(song.mimeType.fromExtension, formatMimeType)
                        }

                    DetailSong(song, bitrate, sampleRate, resolvedMimeType)
                }
        }
    }

    private fun refreshAlbumData(album: Album) {
        logD("Refreshing album data")
        val data = mutableListOf<Item>(album)
        data.add(SortHeader(id = -2, R.string.lbl_songs))

        // To create a good user experience regarding disc numbers, we intersperse
        // items that show the disc number throughout the album's songs. In the case
        // that the album does not have distinct disc numbers, we omit the header.
        val songs = albumSort.songs(album.songs)
        val byDisc = songs.groupBy { it.disc ?: 1 }
        if (byDisc.size > 1) {
            for (entry in byDisc.entries) {
                val disc = entry.key
                val discSongs = entry.value
                data.add(DiscHeader(id = -2L - disc, disc)) // Ensure ID uniqueness
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
        data.add(Header(-2, R.string.lbl_albums))
        data.addAll(Sort.ByYear(false).albums(artist.albums))
        data.add(SortHeader(-3, R.string.lbl_songs))
        data.addAll(artistSort.songs(artist.songs))
        _artistData.value = data.toList()
    }

    private fun refreshGenreData(genre: Genre) {
        logD("Refreshing genre data")
        val data = mutableListOf<Item>(genre)
        data.add(SortHeader(-2, R.string.lbl_songs))
        data.addAll(genreSort.songs(genre.songs))
        _genreData.value = data
    }

    // --- CALLBACKS ---

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            // TODO: Add when we have a context
            //            val song = currentSong.value
            //            if (song != null) {
            //                val newSong = library.sanitize(song.song)
            //                if (newSong != null) {
            //                    generateDetailSong(newSong)
            //                }
            //            }

            val album = currentAlbum.value
            if (album != null) {
                val newAlbum = library.sanitize(album).also { _currentAlbum.value = it }
                if (newAlbum != null) {
                    refreshAlbumData(newAlbum)
                }
            }

            val artist = currentArtist.value
            if (artist != null) {
                val newArtist = library.sanitize(artist).also { _currentArtist.value = it }
                if (newArtist != null) {
                    refreshArtistData(newArtist)
                }
            }

            val genre = currentGenre.value
            if (genre != null) {
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

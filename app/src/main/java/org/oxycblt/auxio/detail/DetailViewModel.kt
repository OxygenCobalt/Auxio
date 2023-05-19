/*
 * Copyright (c) 2021 Auxio Project
 * DetailViewModel.kt is part of Auxio.
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

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.list.EditHeader
import org.oxycblt.auxio.detail.list.SortHeader
import org.oxycblt.auxio.list.BasicHeader
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.music.metadata.AudioProperties
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.util.*

/**
 * [ViewModel] that manages the Song, Album, Artist, and Genre detail views. Keeps track of the
 * current item they are showing, sub-data to display, and configuration.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class DetailViewModel
@Inject
constructor(
    private val musicRepository: MusicRepository,
    private val audioPropertiesFactory: AudioProperties.Factory,
    private val musicSettings: MusicSettings,
    private val playbackSettings: PlaybackSettings
) : ViewModel(), MusicRepository.UpdateListener {
    private var currentSongJob: Job? = null

    // --- SONG ---

    private val _currentSong = MutableStateFlow<Song?>(null)
    /** The current [Song] to display. Null if there is nothing to show. */
    val currentSong: StateFlow<Song?>
        get() = _currentSong

    private val _songAudioProperties = MutableStateFlow<AudioProperties?>(null)
    /** The [AudioProperties] of the currently shown [Song]. Null if not loaded yet. */
    val songAudioProperties: StateFlow<AudioProperties?> = _songAudioProperties

    // --- ALBUM ---

    private val _currentAlbum = MutableStateFlow<Album?>(null)
    /** The current [Album] to display. Null if there is nothing to show. */
    val currentAlbum: StateFlow<Album?>
        get() = _currentAlbum

    private val _albumList = MutableStateFlow(listOf<Item>())
    /** The current list data derived from [currentAlbum]. */
    val albumList: StateFlow<List<Item>>
        get() = _albumList
    private val _albumInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [albumList] in the UI. */
    val albumInstructions: Event<UpdateInstructions>
        get() = _albumInstructions

    /** The current [Sort] used for [Song]s in [albumList]. */
    var albumSongSort: Sort
        get() = musicSettings.albumSongSort
        set(value) {
            musicSettings.albumSongSort = value
            // Refresh the album list to reflect the new sort.
            currentAlbum.value?.let { refreshAlbumList(it, true) }
        }

    // --- ARTIST ---

    private val _currentArtist = MutableStateFlow<Artist?>(null)
    /** The current [Artist] to display. Null if there is nothing to show. */
    val currentArtist: StateFlow<Artist?>
        get() = _currentArtist

    private val _artistList = MutableStateFlow(listOf<Item>())
    /** The current list derived from [currentArtist]. */
    val artistList: StateFlow<List<Item>> = _artistList
    private val _artistInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [artistList] in the UI. */
    val artistInstructions: Event<UpdateInstructions>
        get() = _artistInstructions

    /** The current [Sort] used for [Song]s in [artistList]. */
    var artistSongSort: Sort
        get() = musicSettings.artistSongSort
        set(value) {
            musicSettings.artistSongSort = value
            // Refresh the artist list to reflect the new sort.
            currentArtist.value?.let { refreshArtistList(it, true) }
        }

    // --- GENRE ---

    private val _currentGenre = MutableStateFlow<Genre?>(null)
    /** The current [Genre] to display. Null if there is nothing to show. */
    val currentGenre: StateFlow<Genre?>
        get() = _currentGenre

    private val _genreList = MutableStateFlow(listOf<Item>())
    /** The current list data derived from [currentGenre]. */
    val genreList: StateFlow<List<Item>> = _genreList
    private val _genreInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [artistList] in the UI. */
    val genreInstructions: Event<UpdateInstructions>
        get() = _genreInstructions

    /** The current [Sort] used for [Song]s in [genreList]. */
    var genreSongSort: Sort
        get() = musicSettings.genreSongSort
        set(value) {
            musicSettings.genreSongSort = value
            // Refresh the genre list to reflect the new sort.
            currentGenre.value?.let { refreshGenreList(it, true) }
        }

    // --- PLAYLIST ---

    private val _currentPlaylist = MutableStateFlow<Playlist?>(null)
    /** The current [Playlist] to display. Null if there is nothing to do. */
    val currentPlaylist: StateFlow<Playlist?>
        get() = _currentPlaylist

    private val _playlistList = MutableStateFlow(listOf<Item>())
    /** The current list data derived from [currentPlaylist] */
    val playlistList: StateFlow<List<Item>> = _playlistList
    private val _playlistInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [playlistList] in the UI. */
    val playlistInstructions: Event<UpdateInstructions>
        get() = _playlistInstructions

    private val _editedPlaylist = MutableStateFlow<List<Song>?>(null)
    /**
     * The new playlist songs created during the current editing session. Null if no editing session
     * is occurring.
     */
    val editedPlaylist: StateFlow<List<Song>?>
        get() = _editedPlaylist

    /**
     * The [MusicMode] to use when playing a [Song] from the UI, or null to play from the currently
     * shown item.
     */
    val playbackMode: MusicMode?
        get() = playbackSettings.inParentPlaybackMode

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        // If we are showing any item right now, we will need to refresh it (and any information
        // related to it) with the new library in order to prevent stale items from showing up
        // in the UI.
        val deviceLibrary = musicRepository.deviceLibrary
        if (changes.deviceLibrary && deviceLibrary != null) {
            val song = currentSong.value
            if (song != null) {
                _currentSong.value = deviceLibrary.findSong(song.uid)?.also(::refreshAudioInfo)
                logD("Updated song to ${currentSong.value}")
            }

            val album = currentAlbum.value
            if (album != null) {
                _currentAlbum.value = deviceLibrary.findAlbum(album.uid)?.also(::refreshAlbumList)
                logD("Updated album to ${currentAlbum.value}")
            }

            val artist = currentArtist.value
            if (artist != null) {
                _currentArtist.value =
                    deviceLibrary.findArtist(artist.uid)?.also(::refreshArtistList)
                logD("Updated artist to ${currentArtist.value}")
            }

            val genre = currentGenre.value
            if (genre != null) {
                _currentGenre.value = deviceLibrary.findGenre(genre.uid)?.also(::refreshGenreList)
                logD("Updated genre to ${currentGenre.value}")
            }
        }

        val userLibrary = musicRepository.userLibrary
        if (changes.userLibrary && userLibrary != null) {
            val playlist = currentPlaylist.value
            if (playlist != null) {
                logD("Updated playlist to ${currentPlaylist.value}")
                _currentPlaylist.value =
                    userLibrary.findPlaylist(playlist.uid)?.also(::refreshPlaylistList)
            }
        }
    }

    /**
     * Set a new [currentSong] from it's [Music.UID]. [currentSong] and [songAudioProperties] will
     * be updated to align with the new [Song].
     *
     * @param uid The UID of the [Song] to load. Must be valid.
     */
    fun setSong(uid: Music.UID) {
        logD("Opening Song [uid: $uid]")
        _currentSong.value = musicRepository.deviceLibrary?.findSong(uid)?.also(::refreshAudioInfo)
    }

    /**
     * Set a new [currentAlbum] from it's [Music.UID]. [currentAlbum] and [albumList] will be
     * updated to align with the new [Album].
     *
     * @param uid The [Music.UID] of the [Album] to update [currentAlbum] to. Must be valid.
     */
    fun setAlbum(uid: Music.UID) {
        logD("Opening Album [uid: $uid]")
        _currentAlbum.value =
            musicRepository.deviceLibrary?.findAlbum(uid)?.also(::refreshAlbumList)
    }

    /**
     * Set a new [currentArtist] from it's [Music.UID]. [currentArtist] and [artistList] will be
     * updated to align with the new [Artist].
     *
     * @param uid The [Music.UID] of the [Artist] to update [currentArtist] to. Must be valid.
     */
    fun setArtist(uid: Music.UID) {
        logD("Opening Artist [uid: $uid]")
        _currentArtist.value =
            musicRepository.deviceLibrary?.findArtist(uid)?.also(::refreshArtistList)
    }

    /**
     * Set a new [currentGenre] from it's [Music.UID]. [currentGenre] and [genreList] will be
     * updated to align with the new album.
     *
     * @param uid The [Music.UID] of the [Genre] to update [currentGenre] to. Must be valid.
     */
    fun setGenre(uid: Music.UID) {
        logD("Opening Genre [uid: $uid]")
        _currentGenre.value =
            musicRepository.deviceLibrary?.findGenre(uid)?.also(::refreshGenreList)
    }

    /**
     * Set a new [currentPlaylist] from it's [Music.UID]. If the [Music.UID] differs,
     * [currentPlaylist] and [currentPlaylist] will be updated to align with the new album.
     *
     * @param uid The [Music.UID] of the [Playlist] to update [currentPlaylist] to. Must be valid.
     */
    fun setPlaylistUid(uid: Music.UID) {
        logD("Opening Playlist [uid: $uid]")
        _currentPlaylist.value =
            musicRepository.userLibrary?.findPlaylist(uid)?.also(::refreshPlaylistList)
    }

    /** Start a playlist editing session. Does nothing if a playlist is not being shown. */
    fun startPlaylistEdit() {
        val playlist = _currentPlaylist.value ?: return
        logD("Starting playlist edit")
        _editedPlaylist.value = playlist.songs
        refreshPlaylistList(playlist)
    }

    /**
     * End a playlist editing session and commits it to the database. Does nothing if there was no
     * prior editing session.
     */
    fun confirmPlaylistEdit() {
        val playlist = _currentPlaylist.value ?: return
        val editedPlaylist = (_editedPlaylist.value ?: return).also { _editedPlaylist.value = null }
        musicRepository.rewritePlaylist(playlist, editedPlaylist)
    }

    /**
     * End a playlist editing session and keep the prior state. Does nothing if there was no prior
     * editing session.
     */
    fun dropPlaylistEdit() {
        val playlist = _currentPlaylist.value ?: return
        _editedPlaylist.value = null
        refreshPlaylistList(playlist)
    }

    /**
     * (Visually) move a song in the current playlist. Does nothing if not in an editing session.
     *
     * @param from The start position, in the list adapter data.
     * @param to The destination position, in the list adapter data.
     */
    fun movePlaylistSongs(from: Int, to: Int): Boolean {
        val playlist = _currentPlaylist.value ?: return false
        val editedPlaylist = (_editedPlaylist.value ?: return false).toMutableList()
        val realFrom = from - 2
        val realTo = to - 2
        if (realFrom !in editedPlaylist.indices || realTo !in editedPlaylist.indices) {
            return false
        }
        editedPlaylist.add(realFrom, editedPlaylist.removeAt(realTo))
        _editedPlaylist.value = editedPlaylist
        refreshPlaylistList(playlist, UpdateInstructions.Move(from, to))
        return true
    }

    /**
     * (Visually) remove a song in the current playlist. Does nothing if not in an editing session.
     *
     * @param at The position of the item to remove, in the list adapter data.
     */
    fun removePlaylistSong(at: Int) {
        val playlist = _currentPlaylist.value ?: return
        val editedPlaylist = (_editedPlaylist.value ?: return).toMutableList()
        val realAt = at - 2
        if (realAt !in editedPlaylist.indices) {
            return
        }
        editedPlaylist.removeAt(realAt)
        _editedPlaylist.value = editedPlaylist
        refreshPlaylistList(playlist, UpdateInstructions.Remove(at))
    }

    private fun refreshAudioInfo(song: Song) {
        // Clear any previous job in order to avoid stale data from appearing in the UI.
        currentSongJob?.cancel()
        _songAudioProperties.value = null
        currentSongJob =
            viewModelScope.launch(Dispatchers.IO) {
                val info = audioPropertiesFactory.extract(song)
                yield()
                _songAudioProperties.value = info
            }
    }

    private fun refreshAlbumList(album: Album, replace: Boolean = false) {
        logD("Refreshing album list")
        val list = mutableListOf<Item>()
        val header = SortHeader(R.string.lbl_songs)
        list.add(Divider(header))
        list.add(header)
        val instructions =
            if (replace) {
                // Intentional so that the header item isn't replaced with the songs
                UpdateInstructions.Replace(list.size)
            } else {
                UpdateInstructions.Diff
            }

        // To create a good user experience regarding disc numbers, we group the album's
        // songs up by disc and then delimit the groups by a disc header.
        val songs = albumSongSort.songs(album.songs)
        // Songs without disc tags become part of Disc 1.
        val byDisc = songs.groupBy { it.disc ?: Disc(1, null) }
        if (byDisc.size > 1) {
            logD("Album has more than one disc, interspersing headers")
            for (entry in byDisc.entries) {
                list.add(entry.key)
                list.addAll(entry.value)
            }
        } else {
            // Album only has one disc, don't add any redundant headers
            list.addAll(songs)
        }

        _albumInstructions.put(instructions)
        _albumList.value = list
    }

    private fun refreshArtistList(artist: Artist, replace: Boolean = false) {
        logD("Refreshing artist list")
        val list = mutableListOf<Item>()
        val albums = Sort(Sort.Mode.ByDate, Sort.Direction.DESCENDING).albums(artist.albums)

        val byReleaseGroup =
            albums.groupBy {
                // Remap the complicated ReleaseType data structure into an easier
                // "AlbumGrouping" enum that will automatically group and sort
                // the artist's albums.
                when (it.releaseType.refinement) {
                    ReleaseType.Refinement.LIVE -> AlbumGrouping.LIVE
                    ReleaseType.Refinement.REMIX -> AlbumGrouping.REMIXES
                    null ->
                        when (it.releaseType) {
                            is ReleaseType.Album -> AlbumGrouping.ALBUMS
                            is ReleaseType.EP -> AlbumGrouping.EPS
                            is ReleaseType.Single -> AlbumGrouping.SINGLES
                            is ReleaseType.Compilation -> AlbumGrouping.COMPILATIONS
                            is ReleaseType.Soundtrack -> AlbumGrouping.SOUNDTRACKS
                            is ReleaseType.Mix -> AlbumGrouping.MIXES
                            is ReleaseType.Mixtape -> AlbumGrouping.MIXTAPES
                        }
                }
            }

        logD("Release groups for this artist: ${byReleaseGroup.keys}")

        for (entry in byReleaseGroup.entries.sortedBy { it.key }) {
            val header = BasicHeader(entry.key.headerTitleRes)
            list.add(Divider(header))
            list.add(header)
            list.addAll(entry.value)
        }

        // Artists may not be linked to any songs, only include a header entry if we have any.
        var instructions: UpdateInstructions = UpdateInstructions.Diff
        if (artist.songs.isNotEmpty()) {
            logD("Songs present in this artist, adding header")
            val header = SortHeader(R.string.lbl_songs)
            list.add(Divider(header))
            list.add(header)
            if (replace) {
                // Intentional so that the header item isn't replaced with the songs
                instructions = UpdateInstructions.Replace(list.size)
            }
            list.addAll(artistSongSort.songs(artist.songs))
        }

        _artistInstructions.put(instructions)
        _artistList.value = list.toList()
    }

    private fun refreshGenreList(genre: Genre, replace: Boolean = false) {
        logD("Refreshing genre list")
        val list = mutableListOf<Item>()
        // Genre is guaranteed to always have artists and songs.
        val artistHeader = BasicHeader(R.string.lbl_artists)
        list.add(Divider(artistHeader))
        list.add(artistHeader)
        list.addAll(genre.artists)

        val songHeader = SortHeader(R.string.lbl_songs)
        list.add(Divider(songHeader))
        list.add(songHeader)
        val instructions =
            if (replace) {
                // Intentional so that the header item isn't replaced with the songs
                UpdateInstructions.Replace(list.size)
            } else {
                UpdateInstructions.Diff
            }
        list.addAll(genreSongSort.songs(genre.songs))
        _genreInstructions.put(instructions)
        _genreList.value = list
    }

    private fun refreshPlaylistList(
        playlist: Playlist,
        instructions: UpdateInstructions = UpdateInstructions.Diff
    ) {
        logD(Exception().stackTraceToString())
        logD("Refreshing playlist list")
        val list = mutableListOf<Item>()

        val newInstructions =
            if (playlist.songs.isNotEmpty()) {
                val header = EditHeader(R.string.lbl_songs)
                list.add(Divider(header))
                list.add(header)
                list.addAll(_editedPlaylist.value ?: playlist.songs)
                instructions
            } else {
                UpdateInstructions.Diff
            }

        _playlistInstructions.put(newInstructions)
        _playlistList.value = list
    }

    /**
     * A simpler mapping of [ReleaseType] used for grouping and sorting songs.
     *
     * @param headerTitleRes The title string resource to use for a header created out of an
     *   instance of this enum.
     */
    private enum class AlbumGrouping(@StringRes val headerTitleRes: Int) {
        ALBUMS(R.string.lbl_albums),
        EPS(R.string.lbl_eps),
        SINGLES(R.string.lbl_singles),
        COMPILATIONS(R.string.lbl_compilations),
        SOUNDTRACKS(R.string.lbl_soundtracks),
        MIXES(R.string.lbl_mixes),
        MIXTAPES(R.string.lbl_mixtapes),
        LIVE(R.string.lbl_live_group),
        REMIXES(R.string.lbl_remix_group),
    }
}

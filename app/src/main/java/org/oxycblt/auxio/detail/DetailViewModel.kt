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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.list.DiscHeader
import org.oxycblt.auxio.detail.list.EditHeader
import org.oxycblt.auxio.detail.list.SortHeader
import org.oxycblt.auxio.list.BasicHeader
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.metadata.AudioProperties
import org.oxycblt.auxio.playback.PlaySong
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.unlikelyToBeNull

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
    private val listSettings: ListSettings,
    private val musicRepository: MusicRepository,
    private val audioPropertiesFactory: AudioProperties.Factory,
    private val playbackSettings: PlaybackSettings,
    detailGeneratorFactory: DetailGenerator.Factory
) : ViewModel(), DetailGenerator.Invalidator {
    private val _toShow = MutableEvent<Show>()
    /**
     * A [Show] command that is awaiting a view capable of responding to it. Null if none currently.
     */
    val toShow: Event<Show>
        get() = _toShow

    // --- SONG ---

    private var currentSongJob: Job? = null

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

    private val _albumSongList = MutableStateFlow(listOf<Item>())
    /** The current list data derived from [currentAlbum]. */
    val albumSongList: StateFlow<List<Item>>
        get() = _albumSongList

    private val _albumSongInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [albumSongList] in the UI. */
    val albumSongInstructions: Event<UpdateInstructions>
        get() = _albumSongInstructions

    /** The current [Sort] used for [Song]s in [albumSongList]. */
    val albumSongSort: Sort
        get() = listSettings.albumSongSort

    /** The [PlaySong] instructions to use when playing a [Song] from [Album] details. */
    val playInAlbumWith
        get() = playbackSettings.inParentPlaybackMode ?: PlaySong.FromAlbum

    // --- ARTIST ---

    private val _currentArtist = MutableStateFlow<Artist?>(null)
    /** The current [Artist] to display. Null if there is nothing to show. */
    val currentArtist: StateFlow<Artist?>
        get() = _currentArtist

    private val _artistSongList = MutableStateFlow(listOf<Item>())
    /** The current list derived from [currentArtist]. */
    val artistSongList: StateFlow<List<Item>> = _artistSongList

    private val _artistSongInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [artistSongList] in the UI. */
    val artistSongInstructions: Event<UpdateInstructions>
        get() = _artistSongInstructions

    /** The current [Sort] used for [Song]s in [artistSongList]. */
    val artistSongSort: Sort
        get() = listSettings.artistSongSort

    /** The [PlaySong] instructions to use when playing a [Song] from [Artist] details. */
    val playInArtistWith
        get() = playbackSettings.inParentPlaybackMode ?: PlaySong.FromArtist(currentArtist.value)

    // --- GENRE ---

    private val _currentGenre = MutableStateFlow<Genre?>(null)
    /** The current [Genre] to display. Null if there is nothing to show. */
    val currentGenre: StateFlow<Genre?>
        get() = _currentGenre

    private val _genreSongList = MutableStateFlow(listOf<Item>())
    /** The current list data derived from [currentGenre]. */
    val genreSongList: StateFlow<List<Item>> = _genreSongList

    private val _genreSongInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [artistSongList] in the UI. */
    val genreSongInstructions: Event<UpdateInstructions>
        get() = _genreSongInstructions

    /** The current [Sort] used for [Song]s in [genreSongList]. */
    val genreSongSort: Sort
        get() = listSettings.genreSongSort

    /** The [PlaySong] instructions to use when playing a [Song] from [Genre] details. */
    val playInGenreWith
        get() = playbackSettings.inParentPlaybackMode ?: PlaySong.FromGenre(currentGenre.value)

    // --- PLAYLIST ---

    private val _currentPlaylist = MutableStateFlow<Playlist?>(null)
    /** The current [Playlist] to display. Null if there is nothing to do. */
    val currentPlaylist: StateFlow<Playlist?>
        get() = _currentPlaylist

    private val _playlistSongList = MutableStateFlow(listOf<Item>())
    /** The current list data derived from [currentPlaylist] */
    val playlistSongList: StateFlow<List<Item>> = _playlistSongList

    private val _playlistSongInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for updating [playlistSongList] in the UI. */
    val playlistSongInstructions: Event<UpdateInstructions>
        get() = _playlistSongInstructions

    private val _editedPlaylist = MutableStateFlow<List<Song>?>(null)
    /**
     * The new playlist songs created during the current editing session. Null if no editing session
     * is occurring.
     */
    val editedPlaylist: StateFlow<List<Song>?>
        get() = _editedPlaylist

    /** The [PlaySong] instructions to use when playing a [Song] from [Genre] details. */
    val playInPlaylistWith
        get() =
            playbackSettings.inParentPlaybackMode
                ?: PlaySong.FromPlaylist(unlikelyToBeNull(currentPlaylist.value))

    private val detailGenerator = detailGeneratorFactory.create(this)

    init {
        detailGenerator.attach()
    }

    override fun onCleared() {
        detailGenerator.release()
    }

    override fun invalidate(type: MusicType, replace: Int?) {
        when (type) {
            MusicType.ALBUMS -> {
                val album = detailGenerator.album(currentAlbum.value?.uid ?: return)
                refreshDetail(album, _currentAlbum, _albumSongList, _albumSongInstructions, replace)
            }
            MusicType.ARTISTS -> {
                val artist = detailGenerator.artist(currentArtist.value?.uid ?: return)
                refreshDetail(
                    artist, _currentArtist, _artistSongList, _artistSongInstructions, replace)
            }
            MusicType.GENRES -> {
                val genre = detailGenerator.genre(currentGenre.value?.uid ?: return)
                refreshDetail(genre, _currentGenre, _genreSongList, _genreSongInstructions, replace)
            }
            MusicType.PLAYLISTS -> {
                refreshPlaylist(currentPlaylist.value?.uid ?: return)
            }
            else -> error("Unexpected music type $type")
        }
    }

    /**
     * Navigate to the details (properties) of a [Song].
     *
     * @param song The [Song] to navigate with.
     */
    fun showSong(song: Song) = showImpl(Show.SongDetails(song))

    /**
     * Navigate to the [Album] details of the given [Song], scrolling to the given [Song] as well.
     *
     * @param song The [Song] to navigate with.
     */
    fun showAlbum(song: Song) = showImpl(Show.SongAlbumDetails(song))

    /**
     * Navigate to the details of an [Album].
     *
     * @param album The [Album] to navigate with.
     */
    fun showAlbum(album: Album) = showImpl(Show.AlbumDetails(album))

    /**
     * Navigate to the details of one of the [Artist]s of a [Song] using the corresponding choice
     * dialog. If there is only one artist, this call is identical to [showArtist].
     *
     * @param song The [Song] to navigate with.
     */
    fun showArtist(song: Song) =
        showImpl(
            if (song.artists.size > 1) {
                Show.SongArtistDecision(song)
            } else {
                Show.ArtistDetails(song.artists.first())
            })

    /**
     * Navigate to the details of one of the [Artist]s of an [Album] using the corresponding choice
     * dialog. If there is only one artist, this call is identical to [showArtist].
     *
     * @param album The [Album] to navigate with.
     */
    fun showArtist(album: Album) =
        showImpl(
            if (album.artists.size > 1) {
                Show.AlbumArtistDecision(album)
            } else {
                Show.ArtistDetails(album.artists.first())
            })

    /**
     * Navigate to the details of an [Artist].
     *
     * @param artist The [Artist] to navigate with.
     */
    fun showArtist(artist: Artist) = showImpl(Show.ArtistDetails(artist))

    /**
     * Navigate to the details of a [Genre].
     *
     * @param genre The [Genre] to navigate with.
     */
    fun showGenre(genre: Genre) = showImpl(Show.GenreDetails(genre))

    /**
     * Navigate to the details of a [Playlist].
     *
     * @param playlist The [Playlist] to navigate with.
     */
    fun showPlaylist(playlist: Playlist) = showImpl(Show.PlaylistDetails(playlist))

    private fun showImpl(show: Show) {
        val existing = toShow.flow.value
        if (existing != null) {
            logD("Already have pending show command $existing, ignoring $show")
            return
        }
        _toShow.put(show)
    }

    /**
     * Set a new [currentSong] from it's [Music.UID]. [currentSong] and [songAudioProperties] will
     * be updated to align with the new [Song].
     *
     * @param uid The UID of the [Song] to load. Must be valid.
     */
    fun setSong(uid: Music.UID) {
        logD("Opening song $uid")
        _currentSong.value = musicRepository.deviceLibrary?.findSong(uid)?.also(::refreshAudioInfo)
        if (_currentSong.value == null) {
            logW("Given song UID was invalid")
        }
    }

    /**
     * Set a new [currentAlbum] from it's [Music.UID]. [currentAlbum] and [albumSongList] will be
     * updated to align with the new [Album].
     *
     * @param uid The [Music.UID] of the [Album] to update [currentAlbum] to. Must be valid.
     */
    fun setAlbum(uid: Music.UID) {
        logD("Opening album $uid")
        if (uid === _currentAlbum.value?.uid) {
            return
        }
        val album = detailGenerator.album(uid)
        refreshDetail(album, _currentAlbum, _albumSongList, _albumSongInstructions, null)
        if (_currentAlbum.value == null) {
            logW("Given album UID was invalid")
        }
    }

    /**
     * Apply a new [Sort] to [albumSongList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applyAlbumSongSort(sort: Sort) {
        listSettings.albumSongSort = sort
    }

    /**
     * Set a new [currentArtist] from it's [Music.UID]. [currentArtist] and [artistSongList] will be
     * updated to align with the new [Artist].
     *
     * @param uid The [Music.UID] of the [Artist] to update [currentArtist] to. Must be valid.
     */
    fun setArtist(uid: Music.UID) {
        logD("Opening artist $uid")
        if (uid === _currentArtist.value?.uid) {
            return
        }
        val artist = detailGenerator.artist(uid)
        refreshDetail(artist, _currentArtist, _artistSongList, _artistSongInstructions, null)
    }

    /**
     * Apply a new [Sort] to [artistSongList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applyArtistSongSort(sort: Sort) {
        listSettings.artistSongSort = sort
    }

    /**
     * Set a new [currentGenre] from it's [Music.UID]. [currentGenre] and [genreSongList] will be
     * updated to align with the new album.
     *
     * @param uid The [Music.UID] of the [Genre] to update [currentGenre] to. Must be valid.
     */
    fun setGenre(uid: Music.UID) {
        logD("Opening genre $uid")
        if (uid === _currentGenre.value?.uid) {
            return
        }
        val genre = detailGenerator.genre(uid)
        refreshDetail(genre, _currentGenre, _genreSongList, _genreSongInstructions, null)
    }

    /**
     * Apply a new [Sort] to [genreSongList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applyGenreSongSort(sort: Sort) {
        listSettings.genreSongSort = sort
    }

    /**
     * Set a new [currentPlaylist] from it's [Music.UID]. If the [Music.UID] differs,
     * [currentPlaylist] and [currentPlaylist] will be updated to align with the new album.
     *
     * @param uid The [Music.UID] of the [Playlist] to update [currentPlaylist] to. Must be valid.
     */
    fun setPlaylist(uid: Music.UID) {
        logD("Opening playlist $uid")
        if (uid === _currentPlaylist.value?.uid) {
            return
        }
        refreshPlaylist(uid)
    }

    /** Start a playlist editing session. Does nothing if a playlist is not being shown. */
    fun startPlaylistEdit() {
        val playlist = _currentPlaylist.value ?: return
        logD("Starting playlist edit")
        _editedPlaylist.value = playlist.songs
        refreshPlaylist(playlist.uid)
    }

    /**
     * End a playlist editing session and commits it to the database. Does nothing if there was no
     * prior editing session.
     */
    fun savePlaylistEdit() {
        val playlist = _currentPlaylist.value ?: return
        val editedPlaylist = _editedPlaylist.value ?: return
        logD("Committing playlist edits")
        viewModelScope.launch {
            musicRepository.rewritePlaylist(playlist, editedPlaylist)
            // TODO: The user could probably press some kind of button if they were fast enough.
            //  Think of a better way to handle this state.
            _editedPlaylist.value = null
        }
    }

    /**
     * End a playlist editing session and keep the prior state. Does nothing if there was no prior
     * editing session.
     *
     * @return true if the session was ended, false otherwise.
     */
    fun dropPlaylistEdit(): Boolean {
        val playlist = _currentPlaylist.value ?: return false
        if (_editedPlaylist.value == null) {
            // Nothing to do.
            return false
        }
        _editedPlaylist.value = null
        refreshPlaylist(playlist.uid)
        return true
    }

    /**
     * Apply a [Sort] to the edited playlist. Does nothing if not in an editing session.
     *
     * @param sort The [Sort] to apply.
     */
    fun applyPlaylistSongSort(sort: Sort) {
        val playlist = _currentPlaylist.value ?: return
        _editedPlaylist.value = sort.songs(_editedPlaylist.value ?: return)
        refreshPlaylist(playlist.uid, UpdateInstructions.Replace(2))
    }

    /**
     * (Visually) move a song in the current playlist. Does nothing if not in an editing session.
     *
     * @param from The start position, in the list adapter data.
     * @param to The destination position, in the list adapter data.
     * @return true if the song was moved, false otherwise.
     */
    fun movePlaylistSongs(from: Int, to: Int): Boolean {
        val playlist = _currentPlaylist.value ?: return false
        val editedPlaylist = (_editedPlaylist.value ?: return false).toMutableList()
        val realFrom = from - 1
        val realTo = to - 1
        if (realFrom !in editedPlaylist.indices || realTo !in editedPlaylist.indices) {
            return false
        }
        logD("Moving playlist song from $realFrom [$from] to $realTo [$to]")
        editedPlaylist.add(realFrom, editedPlaylist.removeAt(realTo))
        _editedPlaylist.value = editedPlaylist
        refreshPlaylist(playlist.uid, UpdateInstructions.Move(from, to))
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
        val realAt = at - 1
        if (realAt !in editedPlaylist.indices) {
            return
        }
        logD("Removing playlist song at $realAt [$at]")
        editedPlaylist.removeAt(realAt)
        _editedPlaylist.value = editedPlaylist
        refreshPlaylist(
            playlist.uid,
            if (editedPlaylist.isNotEmpty()) {
                UpdateInstructions.Remove(at, 1)
            } else {
                logD("Playlist will be empty after removal, removing header")
                UpdateInstructions.Remove(at - 1, 3)
            })
    }

    private fun refreshAudioInfo(song: Song) {
        logD("Refreshing audio info")
        // Clear any previous job in order to avoid stale data from appearing in the UI.
        currentSongJob?.cancel()
        _songAudioProperties.value = null
        currentSongJob =
            viewModelScope.launch(Dispatchers.IO) {
                val info = audioPropertiesFactory.extract(song)
                yield()
                logD("Updating audio info to $info")
                _songAudioProperties.value = info
            }
    }

    private fun <T : MusicParent> refreshDetail(
        detail: Detail<T>?,
        parent: MutableStateFlow<T?>,
        list: MutableStateFlow<List<Item>>,
        instructions: MutableEvent<UpdateInstructions>,
        replace: Int?
    ) {
        if (detail == null) {
            parent.value = null
            return
        }
        val newList = mutableListOf<Item>()
        var newInstructions: UpdateInstructions = UpdateInstructions.Diff
        for ((i, section) in detail.sections.withIndex()) {
            val items =
                when (section) {
                    is DetailSection.PlainSection<*> -> {
                        val header =
                            if (section is DetailSection.Songs) SortHeader(section.stringRes)
                            else BasicHeader(section.stringRes)
                        newList.add(Divider(header))
                        newList.add(header)
                        section.items
                    }
                    is DetailSection.Discs -> {
                        val header = SortHeader(section.stringRes)
                        newList.add(Divider(header))
                        newList.add(header)
                        section.discs.flatMap { listOf(DiscHeader(it.key)) + it.value }
                    }
                }
            // Currently only the final section (songs, which can be sorted) are invalidatable
            // and thus need to be replaced.
            if (replace == -1 && i == detail.sections.lastIndex) {
                // Intentional so that the header item isn't replaced with the songs
                newInstructions = UpdateInstructions.Replace(newList.size)
            }
            newList.addAll(items)
        }
        parent.value = detail.parent
        instructions.put(newInstructions)
        list.value = newList
    }

    private fun refreshPlaylist(
        uid: Music.UID,
        instructions: UpdateInstructions = UpdateInstructions.Diff
    ) {
        logD("Refreshing playlist list")
        val edited = editedPlaylist.value
        if (edited == null) {
            val playlist = detailGenerator.playlist(uid)
            refreshDetail(
                playlist, _currentPlaylist, _playlistSongList, _playlistSongInstructions, null)
            return
        }
        val list = mutableListOf<Item>()
        if (edited.isNotEmpty()) {
            val header = EditHeader(R.string.lbl_songs)
            list.add(Divider(header))
            list.add(header)
            list.addAll(edited)
        }
        _playlistSongInstructions.put(instructions)
        _playlistSongList.value = list
    }
}

/**
 * A command for navigation to detail views. These can be handled partially if a certain command
 * cannot occur in a specific view.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface Show {
    /**
     * Navigate to the details (properties) of a [Song].
     *
     * @param song The [Song] to navigate with.
     */
    data class SongDetails(val song: Song) : Show

    /**
     * Navigate to the details of an [Album].
     *
     * @param album The [Album] to navigate with.
     */
    data class AlbumDetails(val album: Album) : Show

    /**
     * Navigate to the [Album] details of the given [Song], scrolling to the given [Song] as well.
     *
     * @param song The [Song] to navigate with.
     */
    data class SongAlbumDetails(val song: Song) : Show

    /**
     * Navigate to the details of an [Artist].
     *
     * @param artist The [Artist] to navigate with.
     */
    data class ArtistDetails(val artist: Artist) : Show

    /**
     * Navigate to the details of one of the [Artist]s of a [Song] using the corresponding choice
     * dialog.
     *
     * @param song The [Song] to navigate with.
     */
    data class SongArtistDecision(val song: Song) : Show

    /**
     * Navigate to the details of one of the [Artist]s of an [Album] using the corresponding
     * decision dialog.
     *
     * @param album The [Album] to navigate with.
     */
    data class AlbumArtistDecision(val album: Album) : Show

    /**
     * Navigate to the details of a [Genre].
     *
     * @param genre The [Genre] to navigate with.
     */
    data class GenreDetails(val genre: Genre) : Show

    /**
     * Navigate to the details of a [Playlist].
     *
     * @param playlist The [Playlist] to navigate with.
     */
    data class PlaylistDetails(val playlist: Playlist) : Show
}

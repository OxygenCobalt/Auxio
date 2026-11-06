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
import android.content.IntentSender
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.playlist.ExportConfig
import org.oxycblt.musikr.playlist.ExternalPlaylistManager
import timber.log.Timber as L

/**
 * A [ViewModel] providing data specific to the music loading process.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class MusicViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val listSettings: ListSettings,
    private val musicRepository: MusicRepository,
) : ViewModel(), MusicRepository.UpdateListener, MusicRepository.IndexingListener {
    private val externalPlaylistManager = ExternalPlaylistManager.from(context)

    private val _indexingState = MutableStateFlow<IndexingState?>(null)

    /** The current music loading state, or null if no loading is going on. */
    val indexingState: StateFlow<IndexingState?> = _indexingState

    /** [Statistics] about the last completed music load. */
    val statistics: StateFlow<Statistics?>
        field = MutableStateFlow<Statistics?>(null)

    /**
     * A [PlaylistDecision] command that is awaiting a view capable of responding to it. Null if
     * none currently.
     */
    val playlistDecision: Event<PlaylistDecision>
        field = MutableEvent<PlaylistDecision>()

    val playlistMessage: Event<PlaylistMessage>
        field = MutableEvent<PlaylistMessage>()

    val songMessage: Event<SongMessage>
        field = MutableEvent<SongMessage>()

    /**
     * A system delete-confirmation prompt awaiting a host [android.app.Activity] able to launch
     * it and report back the result -- see [DeviceFileDeleter] for why this can't just be
     * performed outright from here.
     */
    val deleteRequest: Event<IntentSender>
        field = MutableEvent<IntentSender>()

    /**
     * A pending deletion awaiting the user's own in-app confirmation -- unlike [deleteRequest],
     * this always needs to happen regardless of [org.oxycblt.auxio.music.locations.LocationMode]:
     * a SAF-mode delete goes straight through on the write grant this app already holds on that
     * folder (see [DeviceFileDeleter]), with no system prompt of its own at all, so without this
     * there would be no confirmation step whatsoever before an irreversible delete for the mode
     * every one of this app's real-world location setups actually uses.
     */
    val deleteConfirmation: Event<List<Song>>
        field = MutableEvent<List<Song>>()

    /** A library-reorganize preview awaiting the user's in-app confirmation. */
    val organizePreview: Event<LibraryOrganizer.Plan>
        field = MutableEvent<LibraryOrganizer.Plan>()

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
        val library = musicRepository.library ?: return
        statistics.value =
            Statistics(
                library.songs.size,
                library.albums.size,
                library.artists.size,
                library.genres.size,
                library.songs.sumOf { it.durationMs },
                library.songs.sumOf { it.size },
            )
        L.d("Updated statistics: ${statistics.value}")
    }

    override fun onIndexingStateChanged() {
        _indexingState.value = musicRepository.indexingState
    }

    /** Requests that the music library should be re-loaded while leveraging the cache. */
    fun refresh() {
        L.d("Refreshing library")
        musicRepository.requestIndex(true)
    }

    /** Requests that the music library be re-loaded without the cache. */
    fun rescan() {
        L.d("Rescanning library")
        musicRepository.requestIndex(false)
    }

    /**
     * Create a new generic [Playlist].
     *
     * @param name The name of the new [Playlist]. If null, the user will be prompted for one.
     * @param songs The [Song]s to be contained in the new playlist.
     * @param reason The reason why a new playlist is being created. For all intensive purposes, you
     *   do not need to specify this.
     */
    fun createPlaylist(
        name: String? = null,
        songs: List<Song> = listOf(),
        reason: PlaylistDecision.New.Reason = PlaylistDecision.New.Reason.NEW,
    ) {
        if (name != null) {
            L.d("Creating $name with ${songs.size} songs]")
            viewModelScope.launch(Dispatchers.IO) {
                musicRepository.createPlaylist(name, songs)
                val message =
                    when (reason) {
                        PlaylistDecision.New.Reason.NEW -> PlaylistMessage.NewPlaylistSuccess
                        PlaylistDecision.New.Reason.ADD -> PlaylistMessage.AddSuccess
                        PlaylistDecision.New.Reason.IMPORT -> PlaylistMessage.ImportSuccess
                    }
                playlistMessage.put(message)
            }
        } else {
            L.d("Launching creation dialog for ${songs.size} songs")
            playlistDecision.put(PlaylistDecision.New(songs, null, reason))
        }
    }

    /**
     * Import a playlist from a file [Uri]. Errors pushed to [playlistMessage].
     *
     * @param uri The [Uri] of the file to import. If null, the user will be prompted with a file
     *   picker.
     * @param target The [Playlist] to import to. If null, a new playlist will be created. Note the
     *   [Playlist] will not be renamed to the name of the imported playlist.
     * @see ExternalPlaylistManager
     */
    fun importPlaylist(uri: Uri? = null, target: Playlist? = null) {
        if (uri != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val importedPlaylist = externalPlaylistManager.import(uri)
                if (importedPlaylist == null) {
                    L.e("Could not import playlist")
                    playlistMessage.put(PlaylistMessage.ImportFailed)
                    return@launch
                }

                val library = musicRepository.library ?: return@launch
                val songs =
                    importedPlaylist.paths.mapNotNull {
                        it.firstNotNullOfOrNull(library::findSongByPath)
                    }

                if (songs.isEmpty()) {
                    L.e("No songs found")
                    playlistMessage.put(PlaylistMessage.ImportFailed)
                    return@launch
                }

                if (target !== null) {
                    if (importedPlaylist.name != null && importedPlaylist.name != target.name.raw) {
                        playlistDecision.put(
                            PlaylistDecision.Rename(
                                target,
                                importedPlaylist.name,
                                songs,
                                PlaylistDecision.Rename.Reason.IMPORT,
                            )
                        )
                    } else {
                        musicRepository.rewritePlaylist(target, songs)
                        playlistMessage.put(PlaylistMessage.ImportSuccess)
                    }
                } else {
                    playlistDecision.put(
                        PlaylistDecision.New(
                            songs,
                            importedPlaylist.name,
                            PlaylistDecision.New.Reason.IMPORT,
                        )
                    )
                }
            }
        } else {
            L.d("Launching import picker")
            playlistDecision.put(PlaylistDecision.Import(target))
        }
    }

    /**
     * Export a [Playlist] to a file [Uri]. Errors pushed to [playlistMessage].
     *
     * @param playlist The [Playlist] to export.
     * @param uri The [Uri] to export to. If null, the user will be prompted for one.
     */
    fun exportPlaylist(playlist: Playlist, uri: Uri? = null, config: ExportConfig? = null) {
        if (uri != null && config != null) {
            L.d("Exporting playlist to $uri")
            viewModelScope.launch(Dispatchers.IO) {
                if (externalPlaylistManager.export(playlist, uri, config)) {
                    playlistMessage.put(PlaylistMessage.ExportSuccess)
                } else {
                    playlistMessage.put(PlaylistMessage.ExportFailed)
                }
            }
        } else {
            L.d("Launching export dialog")
            playlistDecision.put(PlaylistDecision.Export(playlist))
        }
    }

    /**
     * Rename the given playlist.
     *
     * @param playlist The [Playlist] to rename,
     * @param name The new name of the [Playlist]. If null, the user will be prompted for a name.
     * @param applySongs The songs to apply to the playlist after renaming. If empty, no songs will
     *   be applied. This argument is internal and does not need to be specified in normal use.
     * @param reason The reason why the playlist is being renamed. This argument is internal and
     *   does not need to be specified in normal use.
     */
    fun renamePlaylist(
        playlist: Playlist,
        name: String? = null,
        applySongs: List<Song> = listOf(),
        reason: PlaylistDecision.Rename.Reason = PlaylistDecision.Rename.Reason.ACTION,
    ) {
        if (name != null) {
            L.d("Renaming $playlist to $name")
            viewModelScope.launch(Dispatchers.IO) {
                musicRepository.renamePlaylist(playlist, name)
                if (applySongs.isNotEmpty()) {
                    musicRepository.rewritePlaylist(playlist, applySongs)
                }
                val message =
                    when (reason) {
                        PlaylistDecision.Rename.Reason.ACTION -> PlaylistMessage.RenameSuccess
                        PlaylistDecision.Rename.Reason.IMPORT -> PlaylistMessage.ImportSuccess
                    }
                playlistMessage.put(message)
            }
        } else {
            L.d("Launching rename dialog for $playlist")
            playlistDecision.put(PlaylistDecision.Rename(playlist, null, applySongs, reason))
        }
    }

    /**
     * Delete a [Playlist].
     *
     * @param playlist The playlist to delete.
     * @param rude Whether to immediately delete the playlist or prompt the user first. This should
     *   be false at almost all times. This argument is internal and does not need to be specified
     *   in normal use.
     */
    fun deletePlaylist(playlist: Playlist, rude: Boolean = false) {
        if (rude) {
            L.d("Deleting $playlist")
            viewModelScope.launch(Dispatchers.IO) {
                musicRepository.deletePlaylist(playlist)
                playlistMessage.put(PlaylistMessage.DeleteSuccess)
            }
        } else {
            L.d("Launching deletion dialog for $playlist")
            playlistDecision.put(PlaylistDecision.Delete(playlist))
        }
    }

    /**
     * Add a [Song] to a [Playlist].
     *
     * @param song The [Song] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(song: Song, playlist: Playlist? = null) {
        L.d("Adding $song to playlist")
        addToPlaylist(listOf(song), playlist)
    }

    /**
     * Add an [Album] to a [Playlist].
     *
     * @param album The [Album] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(album: Album, playlist: Playlist? = null) {
        L.d("Adding $album to playlist")
        addToPlaylist(listSettings.albumSongSort.songs(album.songs), playlist)
    }

    /**
     * Add an [Artist] to a [Playlist].
     *
     * @param artist The [Artist] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(artist: Artist, playlist: Playlist? = null) {
        L.d("Adding $artist to playlist")
        addToPlaylist(listSettings.artistSongSort.songs(artist.songs), playlist)
    }

    /**
     * Add a [Genre] to a [Playlist].
     *
     * @param genre The [Genre] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(genre: Genre, playlist: Playlist? = null) {
        L.d("Adding $genre to playlist")
        addToPlaylist(listSettings.genreSongSort.songs(genre.songs), playlist)
    }

    /**
     * Add [Song]s to a [Playlist].
     *
     * @param songs The [Song]s to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(songs: List<Song>, playlist: Playlist? = null) {
        if (playlist != null) {
            L.d("Adding ${songs.size} songs to $playlist")
            viewModelScope.launch(Dispatchers.IO) {
                musicRepository.addToPlaylist(songs, playlist)
                playlistMessage.put(PlaylistMessage.AddSuccess)
            }
        } else {
            L.d("Launching addition dialog for songs=${songs.size}")
            playlistDecision.put(PlaylistDecision.Add(songs))
        }
    }

    /**
     * Requests permanently deleting the underlying audio files behind [songs] from device
     * storage. Always goes through [deleteConfirmation] first -- see its own doc comment for why
     * this can't be skipped even on a path that's also going to show its own system prompt.
     *
     * @param songs The [Song]s to delete.
     */
    fun deleteSongs(songs: List<Song>) {
        if (songs.isEmpty()) return
        L.d("Launching deletion confirmation for ${songs.size} songs")
        deleteConfirmation.put(songs)
    }

    /**
     * Actually performs a deletion the user has already confirmed via [deleteConfirmation]. See
     * [DeviceFileDeleter] for the version-specific consent flow this goes through; a system
     * confirmation prompt needed along the way surfaces through [deleteRequest] instead of being
     * handled here, since only a host [android.app.Activity] can launch it.
     *
     * @param songs The [Song]s to delete.
     */
    fun confirmDeleteSongs(songs: List<Song>) {
        if (songs.isEmpty()) return
        L.d("Deleting ${songs.size} songs")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = DeviceFileDeleter.delete(context.contentResolver, songs)) {
                is DeviceFileDeleter.Result.NeedsConfirmation -> deleteRequest.put(result.intentSender)
                is DeviceFileDeleter.Result.Done -> finishDelete(result.success)
            }
        }
    }

    /**
     * Reports the outcome of a [deleteRequest] prompt once the host [android.app.Activity] that
     * launched it gets a result back.
     *
     * @param success Whether the user actually went through with the deletion.
     */
    fun onDeleteRequestResult(success: Boolean) {
        L.d("Delete request result: success=$success")
        finishDelete(success)
    }

    private fun finishDelete(success: Boolean) {
        songMessage.put(if (success) SongMessage.DeleteSuccess else SongMessage.DeleteFailed)
        if (success) {
            // Nudge an immediate reindex rather than waiting on the usual watched-folder
            // ContentObserver debounce -- we already know exactly what changed and don't need
            // to wait to find out.
            musicRepository.requestIndex(true)
        }
    }

    /**
     * Computes a preview of what reorganizing the library would do, without touching any files
     * yet -- always surfaces through [organizePreview] for the user to confirm first, since this
     * goes through no system prompt of its own at all (see [LibraryOrganizer]'s doc comment).
     */
    fun previewOrganizeLibrary() {
        val library = musicRepository.library ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val plan =
                LibraryOrganizer.plan(
                    context,
                    library.songs,
                    organizeFolderFormat(),
                    organizeFileFormat(),
                )
            L.d("Computed organize plan: ${plan.moves.size} moves, ${plan.skipped.size} skipped")
            organizePreview.put(plan)
        }
    }

    /**
     * Actually performs a reorganization the user has already confirmed via [organizePreview].
     *
     * @param moves The moves to apply, as given by the confirmed [LibraryOrganizer.Plan].
     */
    fun confirmOrganizeLibrary(moves: List<LibraryOrganizer.PlannedMove>) {
        if (moves.isEmpty()) return
        L.d("Organizing ${moves.size} songs")
        viewModelScope.launch(Dispatchers.IO) {
            val result = LibraryOrganizer.execute(context.contentResolver, moves)
            songMessage.put(
                if (result.failed == 0) SongMessage.OrganizeSuccess else SongMessage.OrganizeFailed)
            if (result.succeeded > 0) {
                musicRepository.requestIndex(true)
            }
        }
    }

    private fun organizeFolderFormat() =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context.getString(R.string.set_key_organize_folder_format),
                context.getString(R.string.set_organize_folder_format_default))
            ?: context.getString(R.string.set_organize_folder_format_default)

    private fun organizeFileFormat() =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context.getString(R.string.set_key_organize_file_format),
                context.getString(R.string.set_organize_file_format_default))
            ?: context.getString(R.string.set_organize_file_format_default)

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
        val durationMs: Long,
        val totalSizeBytes: Long,
    )
}

/**
 * Navigation command for when a [Playlist] must have some operation performed on it by the user.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface PlaylistDecision {
    /**
     * Navigate to a dialog that allows a user to pick a name for a new [Playlist].
     *
     * @param songs The [Song]s to contain in the new [Playlist].
     * @param template An existing playlist name that should be editable in the opened dialog. If
     *   null, a placeholder should be created and shown as a hint instead.
     * @param reason The reason why a new playlist is being created. For all intensive purposes, you
     *   do not need to specify this.
     */
    data class New(val songs: List<Song>, val template: String?, val reason: Reason) :
        PlaylistDecision {
        enum class Reason {
            NEW,
            ADD,
            IMPORT,
        }
    }

    /**
     * Navigate to a file picker to import a playlist from.
     *
     * @param target The [Playlist] to import to. If null, then the file imported will create a new
     *   playlist.
     */
    data class Import(val target: Playlist?) : PlaylistDecision

    /**
     * Navigate to a dialog that allows a user to rename an existing [Playlist].
     *
     * @param playlist The playlist to act on.
     */
    data class Rename(
        val playlist: Playlist,
        val template: String?,
        val applySongs: List<Song>,
        val reason: Reason,
    ) : PlaylistDecision {
        enum class Reason {
            ACTION,
            IMPORT,
        }
    }

    /**
     * Navigate to a dialog that allows the user to export a [Playlist].
     *
     * @param playlist The [Playlist] to export.
     */
    data class Export(val playlist: Playlist) : PlaylistDecision

    /**
     * Navigate to a dialog that confirms the deletion of an existing [Playlist].
     *
     * @param playlist The playlist to act on.
     */
    data class Delete(val playlist: Playlist) : PlaylistDecision

    /**
     * Navigate to a dialog that allows the user to add [Song]s to a [Playlist].
     *
     * @param songs The [Song]s to add to the chosen [Playlist].
     */
    data class Add(val songs: List<Song>) : PlaylistDecision
}

sealed interface PlaylistMessage {
    val stringRes: Int

    data object NewPlaylistSuccess : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.lng_playlist_created
    }

    data object ImportSuccess : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.lng_playlist_imported
    }

    data object ImportFailed : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.err_import_failed
    }

    data object RenameSuccess : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.lng_playlist_renamed
    }

    data object DeleteSuccess : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.lng_playlist_deleted
    }

    data object AddSuccess : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.lng_playlist_added
    }

    data object ExportSuccess : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.lng_playlist_exported
    }

    data object ExportFailed : PlaylistMessage {
        override val stringRes: Int
            get() = R.string.err_export_failed
    }
}

sealed interface SongMessage {
    val stringRes: Int

    data object DeleteSuccess : SongMessage {
        override val stringRes: Int
            get() = R.string.lng_song_deleted
    }

    data object DeleteFailed : SongMessage {
        override val stringRes: Int
            get() = R.string.err_delete_failed
    }

    data object OrganizeSuccess : SongMessage {
        override val stringRes: Int
            get() = R.string.lng_library_organized
    }

    data object OrganizeFailed : SongMessage {
        override val stringRes: Int
            get() = R.string.err_organize_failed
    }
}

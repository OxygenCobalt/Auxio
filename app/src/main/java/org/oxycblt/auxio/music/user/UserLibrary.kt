/*
 * Copyright (c) 2023 Auxio Project
 * UserLibrary.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.user

import java.lang.Exception
import javax.inject.Inject
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

/**
 * Organized library information controlled by the user.
 *
 * Unlike [DeviceLibrary], [UserLibrary]s can be mutated without needing to clone the instance. It
 * is also not backed by library information, rather an app database with in-memory caching. It is
 * generally not expected to create this yourself, and instead rely on MusicRepository.
 *
 * TODO: Communicate errors
 *
 * @author Alexander Capehart
 */
interface UserLibrary {
    /** The current user-defined playlists. */
    val playlists: List<Playlist>

    /**
     * Find a [Playlist] instance corresponding to the given [Music.UID].
     *
     * @param uid The [Music.UID] to search for.
     * @return The corresponding [Song], or null if one was not found.
     */
    fun findPlaylist(uid: Music.UID): Playlist?

    /**
     * Finds a playlist by it's [name]. Since all [Playlist] names must be unique, this will always
     * return at most 1 value.
     *
     * @param name The name [String] to search for.
     */
    fun findPlaylist(name: String): Playlist?

    /** Constructs a [UserLibrary] implementation in an asynchronous manner. */
    interface Factory {
        suspend fun query(): List<RawPlaylist>

        suspend fun create(
            rawPlaylists: List<RawPlaylist>,
            deviceLibrary: DeviceLibrary
        ): MutableUserLibrary
    }
}

/**
 * A mutable instance of [UserLibrary]. Not meant for use outside of the music module. Use
 * [MusicRepository] instead.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MutableUserLibrary : UserLibrary {
    /**
     * Make a new [Playlist].
     *
     * @param name The name of the [Playlist].
     * @param songs The songs to place in the [Playlist].
     */
    suspend fun createPlaylist(name: String, songs: List<Song>)

    /**
     * Rename a [Playlist].
     *
     * @param playlist The [Playlist] to rename.
     * @param name The name of the new [Playlist].
     */
    suspend fun renamePlaylist(playlist: Playlist, name: String)

    /**
     * Delete a [Playlist].
     *
     * @param playlist The playlist to delete.
     */
    suspend fun deletePlaylist(playlist: Playlist)

    /**
     * Add [Song]s to a [Playlist].
     *
     * @param playlist The [Playlist] to add to. Must currently exist.
     */
    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>)

    /**
     * Update the [Song]s of a [Playlist].
     *
     * @param playlist The [Playlist] to update.
     * @param songs The new [Song]s to be contained in the [Playlist].
     */
    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>)
}

class UserLibraryFactoryImpl
@Inject
constructor(private val playlistDao: PlaylistDao, private val musicSettings: MusicSettings) :
    UserLibrary.Factory {
    override suspend fun query() =
        try {
            playlistDao.readRawPlaylists()
        } catch (e: Exception) {
            logE("Unable to read playlists: $e")
            listOf()
        }

    override suspend fun create(
        rawPlaylists: List<RawPlaylist>,
        deviceLibrary: DeviceLibrary
    ): MutableUserLibrary {
        logD("Successfully read ${rawPlaylists.size} playlists")
        // Convert the database playlist information to actual usable playlists.
        val playlistMap = mutableMapOf<Music.UID, PlaylistImpl>()
        for (rawPlaylist in rawPlaylists) {
            val playlistImpl = PlaylistImpl.fromRaw(rawPlaylist, deviceLibrary, musicSettings)
            playlistMap[playlistImpl.uid] = playlistImpl
        }
        return UserLibraryImpl(playlistDao, playlistMap, musicSettings)
    }
}

private class UserLibraryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistMap: MutableMap<Music.UID, PlaylistImpl>,
    private val musicSettings: MusicSettings
) : MutableUserLibrary {
    override fun hashCode() = playlistMap.hashCode()
    override fun equals(other: Any?) = other is UserLibraryImpl && other.playlistMap == playlistMap
    override fun toString() = "UserLibrary(playlists=${playlists.size})"

    override val playlists: List<Playlist>
        get() = playlistMap.values.toList()

    override fun findPlaylist(uid: Music.UID) = playlistMap[uid]

    override fun findPlaylist(name: String) = playlistMap.values.find { it.name.raw == name }

    override suspend fun createPlaylist(name: String, songs: List<Song>) {
        val playlistImpl = PlaylistImpl.from(name, songs, musicSettings)
        synchronized(this) { playlistMap[playlistImpl.uid] = playlistImpl }
        val rawPlaylist =
            RawPlaylist(
                PlaylistInfo(playlistImpl.uid, playlistImpl.name.raw),
                playlistImpl.songs.map { PlaylistSong(it.uid) })
        try {
            playlistDao.insertPlaylist(rawPlaylist)
            logD("Successfully created playlist $name with ${songs.size} songs")
        } catch (e: Exception) {
            logE("Unable to create playlist $name with ${songs.size} songs")
            logE(e.stackTraceToString())
            synchronized(this) { playlistMap.remove(playlistImpl.uid) }
            return
        }
    }

    override suspend fun renamePlaylist(playlist: Playlist, name: String) {
        val playlistImpl =
            synchronized(this) {
                requireNotNull(playlistMap[playlist.uid]) { "Cannot rename invalid playlist" }.also {
                    playlistMap[it.uid] = it.edit(name, musicSettings)
                }
            }
        try {
            playlistDao.replacePlaylistInfo(PlaylistInfo(playlist.uid, name))
            logD("Successfully renamed $playlist to $name")
        } catch (e: Exception) {
            logE("Unable to rename $playlist to $name: $e")
            logE(e.stackTraceToString())
            synchronized(this) { playlistMap[playlistImpl.uid] = playlistImpl }
            return
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistImpl = synchronized(this) {
            requireNotNull(playlistMap[playlist.uid]) { "Cannot remove invalid playlist" }.also {
                playlistMap.remove(it.uid)
            }
        }
        try {
            playlistDao.deletePlaylist(playlist.uid)
            logD("Successfully deleted $playlist")
        } catch (e: Exception) {
            logE("Unable to delete $playlist: $e")
            logE(e.stackTraceToString())
            synchronized(this) { playlistMap[playlistImpl.uid] = playlistImpl }
            return
        }
    }

    override suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>) {
        val playlistImpl = synchronized(this) {
            requireNotNull(playlistMap[playlist.uid]) { "Cannot add to invalid playlist" }.also {
                playlistMap[it.uid] = it.edit { addAll(songs) }
            }
        }

        try {
            playlistDao.insertPlaylistSongs(playlist.uid, songs.map { PlaylistSong(it.uid) })
            logD("Successfully added ${songs.size} songs to $playlist")
        } catch (e: Exception) {
            logE("Unable to add ${songs.size}  songs to $playlist: $e")
            logE(e.stackTraceToString())
            synchronized(this) { playlistMap[playlistImpl.uid] = playlistImpl }
            return
        }
    }

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>) {
        val playlistImpl = synchronized(this) {
            requireNotNull(playlistMap[playlist.uid]) { "Cannot rewrite invalid playlist" }.also {
                playlistMap[it.uid] = it.edit(songs)
            }
        }

        try {
            playlistDao.replacePlaylistSongs(playlist.uid, songs.map { PlaylistSong(it.uid) })
            logD("Successfully rewrote $playlist with ${songs.size} songs")
        } catch (e: Exception) {
            logE("Unable to rewrite $playlist with ${songs.size} songs: $e")
            logE(e.stackTraceToString())
            synchronized(this) { playlistMap[playlistImpl.uid] = playlistImpl }
            return
        }
    }
}

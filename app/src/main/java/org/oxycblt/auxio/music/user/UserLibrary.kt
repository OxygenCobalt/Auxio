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

import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.device.DeviceLibrary

/**
 * Organized library information controlled by the user.
 *
 * Unlike [DeviceLibrary], [UserLibrary]s can be mutated without needing to clone the instance. It
 * is also not backed by library information, rather an app database with in-memory caching. It is
 * generally not expected to create this yourself, and instead rely on MusicRepository.
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

    /** Constructs a [UserLibrary] implementation in an asynchronous manner. */
    interface Factory {
        /**
         * Create a new [UserLibrary].
         *
         * @param deviceLibrary Asynchronously populated [DeviceLibrary] that can be obtained later.
         *   This allows database information to be read before the actual instance is constructed.
         * @return A new [MutableUserLibrary] with the required implementation.
         */
        suspend fun read(deviceLibrary: Channel<DeviceLibrary>): MutableUserLibrary
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
    fun createPlaylist(name: String, songs: List<Song>)

    /**
     * Add [Song]s to a [Playlist].
     *
     * @param playlist The [Playlist] to add to. Must currently exist.
     */
    fun addToPlaylist(playlist: Playlist, songs: List<Song>)
}

class UserLibraryFactoryImpl
@Inject
constructor(private val playlistDao: PlaylistDao, private val musicSettings: MusicSettings) :
    UserLibrary.Factory {
    override suspend fun read(deviceLibrary: Channel<DeviceLibrary>): MutableUserLibrary =
        UserLibraryImpl(playlistDao, deviceLibrary.receive(), musicSettings)
}

private class UserLibraryImpl(
    private val playlistDao: PlaylistDao,
    private val deviceLibrary: DeviceLibrary,
    private val musicSettings: MusicSettings
) : MutableUserLibrary {
    private val playlistMap = mutableMapOf<Music.UID, PlaylistImpl>()
    override val playlists: List<Playlist>
        get() = playlistMap.values.toList()

    init {
        // TODO: Actually read playlists
        createPlaylist("Playlist 1", deviceLibrary.songs.slice(58..100))
    }

    override fun findPlaylist(uid: Music.UID) = playlistMap[uid]

    @Synchronized
    override fun createPlaylist(name: String, songs: List<Song>) {
        val playlistImpl = PlaylistImpl.new(name, songs, musicSettings)
        playlistMap[playlistImpl.uid] = playlistImpl
    }

    @Synchronized
    override fun addToPlaylist(playlist: Playlist, songs: List<Song>) {
        val playlistImpl =
            requireNotNull(playlistMap[playlist.uid]) { "Cannot add to invalid playlist" }
        playlistMap[playlist.uid] = playlistImpl.edit { addAll(songs) }
    }
}

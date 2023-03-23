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
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Playlist
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
    interface Provider {
        /**
         * Create a new [UserLibrary].
         *
         * @param deviceLibrary Asynchronously populated [DeviceLibrary] that can be obtained later.
         *   This allows database information to be read before the actual instance is constructed.
         */
        suspend fun read(deviceLibrary: Channel<DeviceLibrary>): UserLibrary
    }
}

class UserLibraryProviderImpl
@Inject
constructor(private val playlistDao: PlaylistDao, private val musicSettings: MusicSettings) :
    UserLibrary.Provider {
    override suspend fun read(deviceLibrary: Channel<DeviceLibrary>): UserLibrary =
        UserLibraryImpl(playlistDao, deviceLibrary.receive(), musicSettings)
}

private class UserLibraryImpl(
    private val playlistDao: PlaylistDao,
    private val deviceLibrary: DeviceLibrary,
    private val musicSettings: MusicSettings
) : UserLibrary {
    private val playlistMap = mutableMapOf<Music.UID, PlaylistImpl>()
    override val playlists: List<Playlist>
        get() = playlistMap.values.toList()

    init {
        val uid = Music.UID.auxio(MusicMode.PLAYLISTS) { update("Playlist 1".toByteArray()) }
        playlistMap[uid] =
            PlaylistImpl(
                RawPlaylist(
                    PlaylistInfo(uid, "Playlist 1"),
                    deviceLibrary.songs.slice(10..30).map { PlaylistSong(it.uid) }),
                deviceLibrary,
                musicSettings,
            )
    }

    override fun findPlaylist(uid: Music.UID) = playlistMap[uid]
}

/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistImpl.kt is part of Auxio.
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

import android.content.Context
import java.util.*
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.device.DeviceLibrary

class PlaylistImpl
private constructor(
    override val uid: Music.UID,
    override val rawName: String,
    override val sortName: SortName,
    override val songs: List<Song>
) : Playlist {
    override fun resolveName(context: Context) = rawName
    override val rawSortName = null
    override val durationMs = songs.sumOf { it.durationMs }
    override val albums =
        songs.groupBy { it.album }.entries.sortedByDescending { it.value.size }.map { it.key }

    /**
     * Clone the data in this instance to a new [PlaylistImpl] with the given [Song]s.
     *
     * @param songs The new [Song]s to use.
     */
    fun edit(songs: List<Song>) = PlaylistImpl(uid, rawName, sortName, songs)

    /**
     * Clone the data in this instance to a new [PlaylistImpl] with the given [edits].
     *
     * @param edits The edits to make to the [Song]s of the playlist.
     */
    inline fun edit(edits: MutableList<Song>.() -> Unit) = edit(songs.toMutableList().apply(edits))

    companion object {
        /**
         * Create a new instance with a novel UID.
         *
         * @param name The name of the playlist.
         * @param songs The songs to initially populate the playlist with.
         * @param musicSettings [MusicSettings] required for name configuration.
         */
        fun new(name: String, songs: List<Song>, musicSettings: MusicSettings) =
            PlaylistImpl(
                Music.UID.auxio(MusicMode.PLAYLISTS, UUID.randomUUID()),
                name,
                SortName(name, musicSettings),
                songs)

        /**
         * Populate a new instance from a read [RawPlaylist].
         *
         * @param rawPlaylist The [RawPlaylist] to read from.
         * @param deviceLibrary The [DeviceLibrary] to initialize from.
         * @param musicSettings [MusicSettings] required for name configuration.
         */
        fun fromRaw(
            rawPlaylist: RawPlaylist,
            deviceLibrary: DeviceLibrary,
            musicSettings: MusicSettings
        ) =
            PlaylistImpl(
                rawPlaylist.playlistInfo.playlistUid,
                rawPlaylist.playlistInfo.name,
                SortName(rawPlaylist.playlistInfo.name, musicSettings),
                rawPlaylist.songs.mapNotNull { deviceLibrary.findSong(it.songUid) })
    }
}

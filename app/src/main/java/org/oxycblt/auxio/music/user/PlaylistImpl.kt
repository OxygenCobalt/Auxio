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

import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.info.Name

class PlaylistImpl
private constructor(
    override val uid: Music.UID,
    override val name: Name.Known,
    override val songs: List<Song>
) : Playlist {
    override val durationMs = songs.sumOf { it.durationMs }
    override val albums =
        songs.groupBy { it.album }.entries.sortedByDescending { it.value.size }.map { it.key }

    /**
     * Clone the data in this instance to a new [PlaylistImpl] with the given [name].
     *
     * @param name The new name to use.
     * @param musicSettings [MusicSettings] required for name configuration.
     */
    fun edit(name: String, musicSettings: MusicSettings) =
        PlaylistImpl(uid, Name.Known.from(name, null, musicSettings), songs)

    /**
     * Clone the data in this instance to a new [PlaylistImpl] with the given [Song]s.
     *
     * @param songs The new [Song]s to use.
     */
    fun edit(songs: List<Song>) = PlaylistImpl(uid, name, songs)

    /**
     * Clone the data in this instance to a new [PlaylistImpl] with the given [edits].
     *
     * @param edits The edits to make to the [Song]s of the playlist.
     */
    inline fun edit(edits: MutableList<Song>.() -> Unit) = edit(songs.toMutableList().apply(edits))

    override fun equals(other: Any?) =
        other is PlaylistImpl && uid == other.uid && name == other.name && songs == other.songs

    override fun hashCode(): Int {
        var hashCode = uid.hashCode()
        hashCode = 31 * hashCode + name.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
        return hashCode
    }

    companion object {
        /**
         * Create a new instance with a novel UID.
         *
         * @param name The name of the playlist.
         * @param songs The songs to initially populate the playlist with.
         * @param musicSettings [MusicSettings] required for name configuration.
         */
        fun from(name: String, songs: List<Song>, musicSettings: MusicSettings) =
            PlaylistImpl(
                Music.UID.auxio(MusicMode.PLAYLISTS),
                Name.Known.from(name, null, musicSettings),
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
                Name.Known.from(rawPlaylist.playlistInfo.name, null, musicSettings),
                rawPlaylist.songs.mapNotNull { deviceLibrary.findSong(it.songUid) })
    }
}

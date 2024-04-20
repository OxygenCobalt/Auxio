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

import org.oxycblt.auxio.image.extractor.ParentCover
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.info.Name

class PlaylistImpl
private constructor(
    override val uid: Music.UID,
    override val name: Name.Known,
    override val songs: List<Song>
) : Playlist {
    override val durationMs = songs.sumOf { it.durationMs }
    private var hashCode = uid.hashCode()

    init {
        hashCode = 31 * hashCode + name.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    override fun equals(other: Any?) =
        other is PlaylistImpl && uid == other.uid && name == other.name && songs == other.songs

    override fun hashCode() = hashCode

    override fun toString() = "Playlist(uid=$uid, name=$name)"

    override val cover = songs.takeIf { it.isNotEmpty() }?.let { ParentCover.from(it.first(), it) }

    /**
     * Clone the data in this instance to a new [PlaylistImpl] with the given [name].
     *
     * @param name The new name to use.
     * @param nameFactory The [Name.Known.Factory] to interpret name information with.
     */
    fun edit(name: String, nameFactory: Name.Known.Factory) =
        PlaylistImpl(uid, nameFactory.parse(name, null), songs)

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

    companion object {
        /**
         * Create a new instance with a novel UID.
         *
         * @param name The name of the playlist.
         * @param songs The songs to initially populate the playlist with.
         * @param nameFactory The [Name.Known.Factory] to interpret name information with.
         */
        fun from(name: String, songs: List<Song>, nameFactory: Name.Known.Factory) =
            PlaylistImpl(Music.UID.auxio(MusicType.PLAYLISTS), nameFactory.parse(name, null), songs)

        /**
         * Populate a new instance from a read [RawPlaylist].
         *
         * @param rawPlaylist The [RawPlaylist] to read from.
         * @param deviceLibrary The [DeviceLibrary] to initialize from.
         * @param nameFactory The [Name.Known.Factory] to interpret name information with.
         */
        fun fromRaw(
            rawPlaylist: RawPlaylist,
            deviceLibrary: DeviceLibrary,
            nameFactory: Name.Known.Factory
        ) =
            PlaylistImpl(
                rawPlaylist.playlistInfo.playlistUid,
                nameFactory.parse(rawPlaylist.playlistInfo.name, null),
                rawPlaylist.songs.mapNotNull { deviceLibrary.findSong(it.songUid) })
    }
}

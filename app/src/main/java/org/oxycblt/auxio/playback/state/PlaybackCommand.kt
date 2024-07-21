/*
 * Copyright (c) 2024 Auxio Project
 * PlaybackCommand.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.state

import javax.inject.Inject
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackSettings

/**
 * A playback command that can be passed to [PlaybackStateManager] to start new playback.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface PlaybackCommand {
    /** A particular [Song] to play, or null to play the first [Song] in the new queue. * */
    val song: Song?
    /**
     * The [MusicParent] to play from, or null if to play from an non-specific collection of "All
     * [Song]s". *
     */
    val parent: MusicParent?
    /** The queue of [Song]s to play from. * */
    val queue: List<Song>
    /** Whether to shuffle or not. * */
    val shuffled: Boolean

    interface Factory {
        fun song(song: Song, shuffle: ShuffleMode): PlaybackCommand?

        fun songFromAll(song: Song, shuffle: ShuffleMode): PlaybackCommand?

        fun songFromAlbum(song: Song, shuffle: ShuffleMode): PlaybackCommand?

        fun songFromArtist(song: Song, artist: Artist?, shuffle: ShuffleMode): PlaybackCommand?

        fun songFromGenre(song: Song, genre: Genre?, shuffle: ShuffleMode): PlaybackCommand?

        fun songFromPlaylist(song: Song, playlist: Playlist, shuffle: ShuffleMode): PlaybackCommand?

        fun all(shuffle: ShuffleMode): PlaybackCommand?

        fun songs(songs: List<Song>, shuffle: ShuffleMode): PlaybackCommand?

        fun album(album: Album, shuffle: ShuffleMode): PlaybackCommand?

        fun artist(artist: Artist, shuffle: ShuffleMode): PlaybackCommand?

        fun genre(genre: Genre, shuffle: ShuffleMode): PlaybackCommand?

        fun playlist(playlist: Playlist, shuffle: ShuffleMode): PlaybackCommand?
    }
}

enum class ShuffleMode {
    ON,
    OFF,
    IMPLICIT
}

class PlaybackCommandFactoryImpl
@Inject
constructor(
    val playbackManager: PlaybackStateManager,
    val playbackSettings: PlaybackSettings,
    val listSettings: ListSettings,
    val musicRepository: MusicRepository
) : PlaybackCommand.Factory {
    data class PlaybackCommandImpl(
        override val song: Song?,
        override val parent: MusicParent?,
        override val queue: List<Song>,
        override val shuffled: Boolean
    ) : PlaybackCommand

    override fun song(song: Song, shuffle: ShuffleMode) =
        newCommand(song, null, listOf(song), shuffle)

    override fun songFromAll(song: Song, shuffle: ShuffleMode) = newCommand(song, shuffle)

    override fun songFromAlbum(song: Song, shuffle: ShuffleMode) =
        newCommand(song, song.album, listSettings.albumSongSort, shuffle)

    override fun songFromArtist(song: Song, artist: Artist?, shuffle: ShuffleMode) =
        newCommand(song, artist, song.artists, listSettings.artistSongSort, shuffle)

    override fun songFromGenre(song: Song, genre: Genre?, shuffle: ShuffleMode) =
        newCommand(song, genre, song.genres, listSettings.genreSongSort, shuffle)

    override fun songFromPlaylist(song: Song, playlist: Playlist, shuffle: ShuffleMode) =
        newCommand(song, playlist, playlist.songs, shuffle)

    override fun all(shuffle: ShuffleMode) = newCommand(null, shuffle)

    override fun songs(songs: List<Song>, shuffle: ShuffleMode) =
        newCommand(null, null, songs, shuffle)

    override fun album(album: Album, shuffle: ShuffleMode) =
        newCommand(null, album, listSettings.albumSongSort, shuffle)

    override fun artist(artist: Artist, shuffle: ShuffleMode) =
        newCommand(null, artist, listSettings.artistSongSort, shuffle)

    override fun genre(genre: Genre, shuffle: ShuffleMode) =
        newCommand(null, genre, listSettings.genreSongSort, shuffle)

    override fun playlist(playlist: Playlist, shuffle: ShuffleMode) =
        newCommand(null, playlist, playlist.songs, shuffle)

    private fun <T : MusicParent> newCommand(
        song: Song,
        parent: T?,
        parents: List<T>,
        sort: Sort,
        shuffle: ShuffleMode
    ): PlaybackCommand? {
        return if (parent != null) {
            newCommand(song, parent, sort, shuffle)
        } else if (song.genres.size == 1) {
            newCommand(song, parents.first(), sort, shuffle)
        } else {
            null
        }
    }

    private fun newCommand(song: Song?, shuffle: ShuffleMode): PlaybackCommand? {
        val deviceLibrary = musicRepository.deviceLibrary ?: return null
        return newCommand(song, null, deviceLibrary.songs, listSettings.songSort, shuffle)
    }

    private fun newCommand(
        song: Song?,
        parent: MusicParent,
        sort: Sort,
        shuffle: ShuffleMode
    ): PlaybackCommand? {
        val songs = sort.songs(parent.songs)
        return newCommand(song, parent, songs, sort, shuffle)
    }

    private fun newCommand(
        song: Song?,
        parent: MusicParent?,
        queue: Collection<Song>,
        sort: Sort,
        shuffle: ShuffleMode
    ): PlaybackCommand? {
        if (queue.isEmpty() || (song != null && song !in queue)) {
            return null
        }
        return newCommand(song, parent, sort.songs(queue), shuffle)
    }

    private fun newCommand(
        song: Song?,
        parent: MusicParent?,
        queue: List<Song>,
        shuffle: ShuffleMode
    ): PlaybackCommand {
        return PlaybackCommandImpl(song, parent, queue, isShuffled(shuffle))
    }

    private fun isShuffled(shuffle: ShuffleMode) =
        when (shuffle) {
            ShuffleMode.ON -> true
            ShuffleMode.OFF -> false
            ShuffleMode.IMPLICIT -> playbackSettings.keepShuffle && playbackManager.isShuffled
        }
}

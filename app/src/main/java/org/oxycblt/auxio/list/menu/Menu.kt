/*
 * Copyright (c) 2023 Auxio Project
 * Menu.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.menu

import android.os.Parcelable
import androidx.annotation.MenuRes
import kotlinx.parcelize.Parcelize
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaySong

/**
 * Command to navigate to a specific menu dialog configuration.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface Menu {
    /** The menu resource to inflate in the menu dialog. */
    @get:MenuRes val res: Int
    /** A [Parcel] version of this instance that can be used as a navigation argument. */
    val parcel: Parcel

    sealed interface Parcel : Parcelable

    /** Navigate to a [Song] menu dialog. */
    class ForSong(@MenuRes override val res: Int, val song: Song, val playWith: PlaySong) : Menu {
        override val parcel: Parcel
            get() {
                val playWithUid =
                    when (playWith) {
                        is PlaySong.FromArtist -> playWith.which?.uid
                        is PlaySong.FromGenre -> playWith.which?.uid
                        is PlaySong.FromPlaylist -> playWith.which.uid
                        is PlaySong.FromAll,
                        is PlaySong.FromAlbum,
                        is PlaySong.ByItself -> null
                    }

                return Parcel(res, song.uid, playWith.intCode, playWithUid)
            }

        @Parcelize
        data class Parcel(
            val res: Int,
            val songUid: Music.UID,
            val playWithCode: Int,
            val playWithUid: Music.UID?
        ) : Menu.Parcel
    }

    /** Navigate to a [Album] menu dialog. */
    class ForAlbum(@MenuRes override val res: Int, val album: Album) : Menu {
        override val parcel
            get() = Parcel(res, album.uid)

        @Parcelize data class Parcel(val res: Int, val albumUid: Music.UID) : Menu.Parcel
    }

    /** Navigate to a [Artist] menu dialog. */
    class ForArtist(@MenuRes override val res: Int, val artist: Artist) : Menu {
        override val parcel
            get() = Parcel(res, artist.uid)

        @Parcelize data class Parcel(val res: Int, val artistUid: Music.UID) : Menu.Parcel
    }

    /** Navigate to a [Genre] menu dialog. */
    class ForGenre(@MenuRes override val res: Int, val genre: Genre) : Menu {
        override val parcel
            get() = Parcel(res, genre.uid)

        @Parcelize data class Parcel(val res: Int, val genreUid: Music.UID) : Menu.Parcel
    }

    /** Navigate to a [Playlist] menu dialog. */
    class ForPlaylist(@MenuRes override val res: Int, val playlist: Playlist) : Menu {
        override val parcel
            get() = Parcel(res, playlist.uid)

        @Parcelize data class Parcel(val res: Int, val playlistUid: Music.UID) : Menu.Parcel
    }

    class ForSelection(@MenuRes override val res: Int, val songs: List<Song>) : Menu {
        override val parcel: Parcel
            get() = Parcel(res, songs.map { it.uid })

        @Parcelize data class Parcel(val res: Int, val songUids: List<Music.UID>) : Menu.Parcel
    }
}

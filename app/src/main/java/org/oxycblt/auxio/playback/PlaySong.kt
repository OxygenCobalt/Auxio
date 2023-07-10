/*
 * Copyright (c) 2023 Auxio Project
 * PlaySong.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback

import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.Playlist

sealed interface PlaySong {
    val intCode: Int

    object ByItself : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_ITSELF
    }

    object FromAll : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_ITSELF
    }

    object FromAlbum : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_ALBUM
    }

    data class FromArtist(val which: Artist?) : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_ARTIST
    }

    data class FromGenre(val which: Genre?) : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_GENRE
    }

    data class FromPlaylist(val which: Playlist) : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_PLAYLIST
    }

    companion object {
        fun fromPlaybackModeTemporary(playbackMode: MusicMode) =
            when (playbackMode) {
                MusicMode.SONGS -> FromAll
                MusicMode.ALBUMS -> FromAlbum
                MusicMode.ARTISTS -> FromArtist(null)
                MusicMode.GENRES -> FromGenre(null)
                MusicMode.PLAYLISTS -> throw IllegalStateException()
            }

        fun fromIntCode(intCode: Int, inner: Music?): PlaySong? =
            when (intCode) {
                IntegerTable.PLAY_SONG_ITSELF -> ByItself
                IntegerTable.PLAY_SONG_FROM_ALBUM -> FromAlbum
                IntegerTable.PLAY_SONG_FROM_ARTIST ->
                    if (inner is Artist?) {
                        FromArtist(inner)
                    } else {
                        null
                    }
                IntegerTable.PLAY_SONG_FROM_GENRE ->
                    if (inner is Genre?) {
                        FromGenre(inner)
                    } else {
                        null
                    }
                IntegerTable.PLAY_SONG_FROM_PLAYLIST ->
                    if (inner is Playlist) {
                        FromPlaylist(inner)
                    } else {
                        null
                    }
                else -> null
            }
    }
}

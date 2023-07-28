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
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Playlist

/**
 * Configuration to play a song in a desired way.
 *
 * Since songs are not [MusicParent]s, the way the queue is generated around them has a lot more
 * flexibility. The particular strategy used can be configured the user, but it also needs to be
 * transferred between views at points (such as menus). [PlaySong] provides both of these, being a
 * enum-like datatype when configuration is needed, and an algebraic datatype when data transfer is
 * needed.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface PlaySong {
    /**
     * The integer representation of this instance.
     *
     * @see fromIntCode
     */
    val intCode: Int

    /** Play a Song from the entire library of songs. */
    data object FromAll : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_ALL
    }

    /** Play a song from it's album. */
    data object FromAlbum : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_ALBUM
    }

    /**
     * Play a song from (possibly) one of it's [Artist]s.
     *
     * @param which The [Artist] to specifically play from. If null, the user will be prompted for
     *   an [Artist] to choose of the song has multiple. Otherwise, the only [Artist] will be used.
     */
    data class FromArtist(val which: Artist?) : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_ARTIST
    }

    /**
     * Play a song from (possibly) one of it's [Genre]s.
     *
     * @param which The [Genre] to specifically play from. If null, the user will be prompted for a
     *   [Genre] to choose of the song has multiple. Otherwise, the only [Genre] will be used.
     */
    data class FromGenre(val which: Genre?) : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_GENRE
    }

    /**
     * Play a song from one of it's [Playlist]s.
     *
     * @param which The [Playlist] to specifically play from. This must be provided.
     */
    data class FromPlaylist(val which: Playlist) : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_FROM_PLAYLIST
    }

    /** Only play the given song, include nothing else in the queue. */
    data object ByItself : PlaySong {
        override val intCode = IntegerTable.PLAY_SONG_BY_ITSELF
    }

    companion object {
        /**
         * Convert a [PlaySong] integer representation into an instance.
         *
         * @param intCode An integer representation of a [PlaySong]
         * @param which Optional [MusicParent] to automatically populate a [FromArtist],
         *   [FromGenre], or [FromPlaylist] instance. If the type of the [MusicParent] does not
         *   match, it will be considered invalid and null will be returned.
         * @return The corresponding [PlaySong], or null if the [PlaySong] is invalid.
         * @see PlaySong.intCode
         */
        fun fromIntCode(intCode: Int, which: MusicParent? = null): PlaySong? =
            when (intCode) {
                IntegerTable.PLAY_SONG_BY_ITSELF -> ByItself
                IntegerTable.PLAY_SONG_FROM_ALL -> FromAll
                IntegerTable.PLAY_SONG_FROM_ALBUM -> FromAlbum
                IntegerTable.PLAY_SONG_FROM_ARTIST ->
                    if (which is Artist?) {
                        FromArtist(which)
                    } else {
                        null
                    }
                IntegerTable.PLAY_SONG_FROM_GENRE ->
                    if (which is Genre?) {
                        FromGenre(which)
                    } else {
                        null
                    }
                IntegerTable.PLAY_SONG_FROM_PLAYLIST ->
                    if (which is Playlist) {
                        FromPlaylist(which)
                    } else {
                        null
                    }
                else -> null
            }
    }
}

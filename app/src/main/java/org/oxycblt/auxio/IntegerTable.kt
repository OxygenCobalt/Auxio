/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio

/** A table containing all unique integer codes that Auxio uses. */
object IntegerTable {
    /** SongViewHolder */
    const val ITEM_TYPE_SONG = 0xA000
    /** AlbumViewHolder */
    const val ITEM_TYPE_ALBUM = 0xA001
    /** ArtistViewHolder */
    const val ITEM_TYPE_ARTIST = 0xA002
    /** GenreViewHolder */
    const val ITEM_TYPE_GENRE = 0xA003
    /** HeaderViewHolder */
    const val ITEM_TYPE_HEADER = 0xA004
    /** SortHeaderViewHolder */
    const val ITEM_TYPE_SORT_HEADER = 0xA005

    /** AlbumDetailViewHolder */
    const val ITEM_TYPE_ALBUM_DETAIL = 0xA006
    /** AlbumSongViewHolder */
    const val ITEM_TYPE_ALBUM_SONG = 0xA007
    /** ArtistDetailViewHolder */
    const val ITEM_TYPE_ARTIST_DETAIL = 0xA008
    /** ArtistAlbumViewHolder */
    const val ITEM_TYPE_ARTIST_ALBUM = 0xA009
    /** ArtistSongViewHolder */
    const val ITEM_TYPE_ARTIST_SONG = 0xA00A
    /** GenreDetailViewHolder */
    const val ITEM_TYPE_GENRE_DETAIL = 0xA00B
    /** GenreSongViewHolder */
    const val ITEM_TYPE_GENRE_SONG = 0xA00C
    /** DiscHeaderViewHolder */
    const val ITEM_TYPE_DISC_HEADER = 0xA00D

    /** QueueSongViewHolder */
    const val ITEM_TYPE_QUEUE_SONG = 0xA00E

    /** "Music playback" notification code */
    const val PLAYBACK_NOTIFICATION_CODE = 0xA0A0
    /** "Music loading" notification code */
    const val INDEXER_NOTIFICATION_CODE = 0xA0A1
    /** Intent request code */
    const val REQUEST_CODE = 0xA0C0

    /** RepeatMode.NONE */
    const val REPEAT_MODE_NONE = 0xA100
    /** RepeatMode.ALL */
    const val REPEAT_MODE_ALL = 0xA101
    /** RepeatMode.TRACK */
    const val REPEAT_MODE_TRACK = 0xA102

    /** PlaybackMode.IN_GENRE */
    const val PLAYBACK_MODE_IN_GENRE = 0xA103
    /** PlaybackMode.IN_ARTIST */
    const val PLAYBACK_MODE_IN_ARTIST = 0xA104
    /** PlaybackMode.IN_ALBUM */
    const val PLAYBACK_MODE_IN_ALBUM = 0xA105
    /** PlaybackMode.ALL_SONGS */
    const val PLAYBACK_MODE_ALL_SONGS = 0xA106

    /** DisplayMode.NONE (No Longer used but still reserved) */
    // const val DISPLAY_MODE_NONE = 0xA107
    /** DisplayMode.SHOW_GENRES */
    const val DISPLAY_MODE_SHOW_GENRES = 0xA108
    /** DisplayMode.SHOW_ARTISTS */
    const val DISPLAY_MODE_SHOW_ARTISTS = 0xA109
    /** DisplayMode.SHOW_ALBUMS */
    const val DISPLAY_MODE_SHOW_ALBUMS = 0xA10A
    /** DisplayMode.SHOW_SONGS */
    const val DISPLAY_MODE_SHOW_SONGS = 0xA10B

    // Note: Sort integer codes are non-contiguous due to significant amounts of time
    // passing between the additions of new sort modes.

    /** Sort.ByName */
    const val SORT_BY_NAME = 0xA10C
    /** Sort.ByArtist */
    const val SORT_BY_ARTIST = 0xA10D
    /** Sort.ByAlbum */
    const val SORT_BY_ALBUM = 0xA10E
    /** Sort.ByYear */
    const val SORT_BY_YEAR = 0xA10F
    /** Sort.ByDuration */
    const val SORT_BY_DURATION = 0xA114
    /** Sort.ByCount */
    const val SORT_BY_COUNT = 0xA115
    /** Sort.ByDisc */
    const val SORT_BY_DISC = 0xA116
    /** Sort.ByTrack */
    const val SORT_BY_TRACK = 0xA117
    /** Sort.ByDateAdded */
    const val SORT_BY_DATE_ADDED = 0xA118

    /** ReplayGainMode.Off */
    const val REPLAY_GAIN_MODE_OFF = 0xA110
    /** ReplayGainMode.Track */
    const val REPLAY_GAIN_MODE_TRACK = 0xA111
    /** ReplayGainMode.Album */
    const val REPLAY_GAIN_MODE_ALBUM = 0xA112
    /** ReplayGainMode.Dynamic */
    const val REPLAY_GAIN_MODE_DYNAMIC = 0xA113
}

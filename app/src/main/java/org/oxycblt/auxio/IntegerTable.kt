/*
 * Copyright (c) 2022 Auxio Project
 * IntegerTable.kt is part of Auxio.
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

/**
 * A table containing all of the magic integer codes that the codebase has currently reserved. May
 * be non-contiguous.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
object IntegerTable {
    /** SongViewHolder */
    const val VIEW_TYPE_SONG = 0xA000
    /** AlbumViewHolder */
    const val VIEW_TYPE_ALBUM = 0xA001
    /** ArtistViewHolder */
    const val VIEW_TYPE_ARTIST = 0xA002
    /** GenreViewHolder */
    const val VIEW_TYPE_GENRE = 0xA003
    /** PlaylistViewHolder */
    const val VIEW_TYPE_PLAYLIST = 0xA004
    /** BasicHeaderViewHolder */
    const val VIEW_TYPE_BASIC_HEADER = 0xA005
    /** SortHeaderViewHolder */
    const val VIEW_TYPE_SORT_HEADER = 0xA006
    /** AlbumSongViewHolder */
    const val VIEW_TYPE_ALBUM_SONG = 0xA007
    /** ArtistAlbumViewHolder */
    const val VIEW_TYPE_ARTIST_ALBUM = 0xA008
    /** ArtistSongViewHolder */
    const val VIEW_TYPE_ARTIST_SONG = 0xA009
    /** DiscHeaderViewHolder */
    const val VIEW_TYPE_DISC_HEADER = 0xA00A
    /** "Music playback" notification code */
    const val PLAYBACK_NOTIFICATION_CODE = 0xA0A0
    /** "Music loading" notification code */
    const val INDEXER_NOTIFICATION_CODE = 0xA0A1
    /** MainActivity Intent request code */
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
    /** MusicMode.SONGS */
    const val MUSIC_MODE_SONGS = 0xA10B
    /** MusicMode.ALBUMS */
    const val MUSIC_MODE_ALBUMS = 0xA10A
    /** MusicMode.ARTISTS */
    const val MUSIC_MODE_ARTISTS = 0xA109
    /** MusicMode.GENRES */
    const val MUSIC_MODE_GENRES = 0xA108
    /** MusicMode.PLAYLISTS */
    const val MUSIC_MODE_PLAYLISTS = 0xA107
    /** Sort.Mode.ByName */
    const val SORT_BY_NAME = 0xA10C
    /** Sort.Mode.ByArtist */
    const val SORT_BY_ARTIST = 0xA10D
    /** Sort.Mode.ByAlbum */
    const val SORT_BY_ALBUM = 0xA10E
    /** Sort.Mode.ByYear */
    const val SORT_BY_YEAR = 0xA10F
    /** Sort.Mode.ByDuration */
    const val SORT_BY_DURATION = 0xA114
    /** Sort.Mode.ByCount */
    const val SORT_BY_COUNT = 0xA115
    /** Sort.Mode.ByDisc */
    const val SORT_BY_DISC = 0xA116
    /** Sort.Mode.ByTrack */
    const val SORT_BY_TRACK = 0xA117
    /** Sort.Mode.ByDateAdded */
    const val SORT_BY_DATE_ADDED = 0xA118
    /** Sort.Mode.None */
    const val SORT_BY_NONE = 0xA11F
    /** ReplayGainMode.Off (No longer used but still reserved) */
    // const val REPLAY_GAIN_MODE_OFF = 0xA110
    /** ReplayGainMode.Track */
    const val REPLAY_GAIN_MODE_TRACK = 0xA111
    /** ReplayGainMode.Album */
    const val REPLAY_GAIN_MODE_ALBUM = 0xA112
    /** ReplayGainMode.Dynamic */
    const val REPLAY_GAIN_MODE_DYNAMIC = 0xA113
    /** ActionMode.Next */
    const val ACTION_MODE_NEXT = 0xA119
    /** ActionMode.Repeat */
    const val ACTION_MODE_REPEAT = 0xA11A
    /** ActionMode.Shuffle */
    const val ACTION_MODE_SHUFFLE = 0xA11B
    /** CoverMode.Off */
    const val COVER_MODE_OFF = 0xA11C
    /** CoverMode.MediaStore */
    const val COVER_MODE_MEDIA_STORE = 0xA11D
    /** CoverMode.Quality */
    const val COVER_MODE_QUALITY = 0xA11E
}

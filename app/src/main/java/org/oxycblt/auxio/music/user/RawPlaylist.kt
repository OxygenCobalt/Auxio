/*
 * Copyright (c) 2023 Auxio Project
 * RawPlaylist.kt is part of Auxio.
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

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import org.oxycblt.auxio.music.Music

/**
 * Raw playlist information persisted to [UserMusicDatabase].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class RawPlaylist(
    @Embedded val playlistInfo: PlaylistInfo,
    @Relation(
        parentColumn = "playlistUid",
        entityColumn = "songUid",
        associateBy = Junction(PlaylistSongCrossRef::class))
    val songs: List<PlaylistSong>
)

/**
 * UID and name information corresponding to a [RawPlaylist] entry.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Entity data class PlaylistInfo(@PrimaryKey val playlistUid: Music.UID, val name: String)

/**
 * Song information corresponding to a [RawPlaylist] entry.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Entity data class PlaylistSong(@PrimaryKey val songUid: Music.UID)

/**
 * Links individual songs to a playlist entry.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Entity
data class PlaylistSongCrossRef(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val playlistUid: Music.UID,
    @ColumnInfo(index = true) val songUid: Music.UID
)

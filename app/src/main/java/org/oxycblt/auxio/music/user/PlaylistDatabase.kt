/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDatabase.kt is part of Auxio.
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

import androidx.room.*
import org.oxycblt.auxio.music.Music

@Database(
    entities = [PlaylistInfo::class, PlaylistSong::class, PlaylistSongCrossRef::class],
    version = 28,
    exportSchema = false)
@TypeConverters(Music.UID.TypeConverters::class)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}

@Dao
interface PlaylistDao {
    @Transaction @Query("SELECT * FROM PlaylistInfo") fun readRawPlaylists(): List<RawPlaylist>
}

/*
 * Copyright (c) 2024 Auxio Project
 * TrackerDatabase.kt is part of Auxio.
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
 
package org.oxycblt.musikr.track

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackedSongEntity::class], version = 1, exportSchema = false)
internal abstract class TrackerDatabase : RoomDatabase() {
    abstract fun trackedSongsDao(): TrackedSongsDao

    companion object {
        fun from(context: Context) =
            Room.databaseBuilder(
                    context.applicationContext, TrackerDatabase::class.java, "tracked_songs.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}

@Entity internal data class TrackedSongEntity(@PrimaryKey val uid: String, val dateAdded: Long)

@Dao
internal interface TrackedSongsDao {
    @Query("SELECT * FROM TrackedSongEntity WHERE uid = :uid")
    suspend fun selectSong(uid: String): TrackedSongEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(trackedSong: TrackedSongEntity)
}

/*
 * Copyright (c) 2024 Auxio Project
 * StoredCoversDatabase.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.stack.cache

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.oxycblt.auxio.music.Music

@Database(entities = [StoredCover::class], version = 50, exportSchema = false)
abstract class StoredCoversDatabase : RoomDatabase() {
    abstract fun storedCoversDao(): StoredCoversDao
}

@Dao
interface StoredCoversDao {
    @Query("SELECT coverId FROM StoredCover WHERE uid = :uid AND lastModified = :lastModified")
    @TypeConverters(Music.UID.TypeConverters::class)
    suspend fun getStoredCover(uid: Music.UID, lastModified: Long): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setStoredCover(storedCover: StoredCover)
}

@Entity
@TypeConverters(Music.UID.TypeConverters::class)
data class StoredCover(@PrimaryKey val uid: Music.UID, val lastModified: Long, val coverId: String)

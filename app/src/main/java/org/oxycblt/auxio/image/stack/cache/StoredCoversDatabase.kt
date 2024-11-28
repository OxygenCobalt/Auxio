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
    @Query("SELECT perceptualHash FROM StoredCover WHERE uid = :uid AND lastModified = :lastModified")
    fun getCoverFile(uid: Music.UID, lastModified: Long): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setCoverFile(storedCover: StoredCover)
}

@Entity
@TypeConverters(Music.UID.TypeConverters::class)
data class StoredCover(
    @PrimaryKey
    val uid: Music.UID,
    val lastModified: Long,
    val perceptualHash: String
)
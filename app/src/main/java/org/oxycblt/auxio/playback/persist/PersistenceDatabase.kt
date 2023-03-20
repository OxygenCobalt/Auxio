/*
 * Copyright (c) 2023 Auxio Project
 * PersistenceDatabase.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.persist

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
import org.oxycblt.auxio.playback.state.RepeatMode

/**
 * Provides raw access to the database storing the persisted playback state.
 *
 * @author Alexander Capehart
 */
@Database(
    entities = [PlaybackState::class, QueueHeapItem::class, QueueMappingItem::class],
    version = 27,
    exportSchema = false)
@TypeConverters(Music.UID.TypeConverters::class)
abstract class PersistenceDatabase : RoomDatabase() {
    /**
     * Get the current [PlaybackStateDao].
     *
     * @return A [PlaybackStateDao] providing control of the database's playback state tables.
     */
    abstract fun playbackStateDao(): PlaybackStateDao

    /**
     * Get the current [QueueDao].
     *
     * @return A [QueueDao] providing control of the database's queue tables.
     */
    abstract fun queueDao(): QueueDao
}

/**
 * Provides control of the persisted playback state table.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Dao
interface PlaybackStateDao {
    /**
     * Get the previously persisted [PlaybackState].
     *
     * @return The previously persisted [PlaybackState], or null if one was not present.
     */
    @Query("SELECT * FROM ${PlaybackState.TABLE_NAME} WHERE id = 0")
    suspend fun getState(): PlaybackState?

    /** Delete any previously persisted [PlaybackState]s. */
    @Query("DELETE FROM ${PlaybackState.TABLE_NAME}") suspend fun nukeState()

    /**
     * Insert a new [PlaybackState] into the database.
     *
     * @param state The [PlaybackState] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insertState(state: PlaybackState)
}

/**
 * Provides control of the persisted queue state tables.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Dao
interface QueueDao {
    /**
     * Get the previously persisted queue heap.
     *
     * @return A list of persisted [QueueHeapItem]s wrapping each heap item.
     */
    @Query("SELECT * FROM ${QueueHeapItem.TABLE_NAME}") suspend fun getHeap(): List<QueueHeapItem>

    /**
     * Get the previously persisted queue mapping.
     *
     * @return A list of persisted [QueueMappingItem]s wrapping each heap item.
     */
    @Query("SELECT * FROM ${QueueMappingItem.TABLE_NAME}")
    suspend fun getMapping(): List<QueueMappingItem>

    /** Delete any previously persisted queue heap entries. */
    @Query("DELETE FROM ${QueueHeapItem.TABLE_NAME}") suspend fun nukeHeap()

    /** Delete any previously persisted queue mapping entries. */
    @Query("DELETE FROM ${QueueMappingItem.TABLE_NAME}") suspend fun nukeMapping()

    /**
     * Insert new heap entries into the database.
     *
     * @param heap The list of wrapped [QueueHeapItem]s to insert.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insertHeap(heap: List<QueueHeapItem>)

    /**
     * Insert new mapping entries into the database.
     *
     * @param mapping The list of wrapped [QueueMappingItem] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMapping(mapping: List<QueueMappingItem>)
}

@Entity(tableName = PlaybackState.TABLE_NAME)
data class PlaybackState(
    @PrimaryKey val id: Int,
    val index: Int,
    val positionMs: Long,
    val repeatMode: RepeatMode,
    val songUid: Music.UID,
    val parentUid: Music.UID?
) {
    companion object {
        const val TABLE_NAME = "playback_state"
    }
}

@Entity(tableName = QueueHeapItem.TABLE_NAME)
data class QueueHeapItem(@PrimaryKey val id: Int, val uid: Music.UID) {
    companion object {
        const val TABLE_NAME = "queue_heap"
    }
}

@Entity(tableName = QueueMappingItem.TABLE_NAME)
data class QueueMappingItem(
    @PrimaryKey val id: Int,
    val orderedIndex: Int,
    val shuffledIndex: Int
) {
    companion object {
        const val TABLE_NAME = "queue_mapping"
    }
}

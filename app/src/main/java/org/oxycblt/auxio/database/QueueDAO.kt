package org.oxycblt.auxio.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface QueueDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: QueueItem)

    @Transaction
    suspend fun insertAll(items: List<QueueItem>) {
        items.forEach {
            insert(it)
        }
    }

    @Query("SELECT * FROM queue_table WHERE is_user_queue == 1")
    fun getUserQueue(): List<QueueItem>

    @Query("SELECT * FROM queue_table WHERE is_user_queue == 0")
    fun getQueue(): List<QueueItem>

    @Query("DELETE FROM queue_table")
    fun clear()
}

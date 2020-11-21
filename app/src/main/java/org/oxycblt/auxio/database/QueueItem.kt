package org.oxycblt.auxio.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A simplified database entity that represents a given song in the queue.
 */
@Entity(tableName = "queue_table")
data class QueueItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "song_id")
    val songId: Long = Long.MIN_VALUE,

    @ColumnInfo(name = "album_id")
    val albumId: Long = Long.MIN_VALUE,

    @ColumnInfo(name = "is_user_queue")
    val isUserQueue: Boolean = false
)

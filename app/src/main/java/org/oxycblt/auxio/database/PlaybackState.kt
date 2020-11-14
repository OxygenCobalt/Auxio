package org.oxycblt.auxio.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_state_table")
data class PlaybackState(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "song_id")
    val songId: Long = -1L,

    @ColumnInfo(name = "position")
    val position: Long,

    @ColumnInfo(name = "parent_id")
    val parentId: Long = -1L,

    @ColumnInfo(name = "user_queue")
    val userQueueIds: String,

    @ColumnInfo(name = "index")
    val index: Int,

    @ColumnInfo(name = "mode")
    val mode: Int,

    @ColumnInfo(name = "is_shuffling")
    val isShuffling: Boolean,

    @ColumnInfo(name = "shuffle_seed")
    val shuffleSeed: Long,

    @ColumnInfo(name = "loop_mode")
    val loopMode: Int,

    @ColumnInfo(name = "in_user_queue")
    val inUserQueue: Boolean
)

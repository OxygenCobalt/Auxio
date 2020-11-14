package org.oxycblt.auxio.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaybackStateDAO {
    @Insert
    fun insert(playbackState: PlaybackState)

    @Update
    fun update(playbackState: PlaybackState)

    @Query("SELECT * FROM playback_state_table")
    fun getAll(): List<PlaybackState>

    @Query("DELETE FROM playback_state_table")
    fun clear()
}

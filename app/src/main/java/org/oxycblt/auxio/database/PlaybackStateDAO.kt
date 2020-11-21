package org.oxycblt.auxio.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaybackStateDAO {
    @Insert
    fun insert(playbackState: PlaybackState)

    @Query("SELECT * FROM playback_state_table")
    fun getAll(): List<PlaybackState>

    @Query("DELETE FROM playback_state_table")
    fun clear()

    @Query("SELECT * FROM playback_state_table ORDER BY id DESC LIMIT 1")
    fun getRecent(): PlaybackState?
}

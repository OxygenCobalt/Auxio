package org.oxycblt.auxio.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlaybackState::class], version = 1, exportSchema = false)
abstract class PlaybackStateDatabase : RoomDatabase() {
    abstract val playbackStateDAO: PlaybackStateDAO

    companion object {
        @Volatile
        private var INSTANCE: PlaybackStateDatabase? = null

        /**
         * Get/Instantiate the single instance of [PlaybackStateDatabase].
         */
        fun getInstance(context: Context): PlaybackStateDatabase {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaybackStateDatabase::class.java,
                    "playback_state_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

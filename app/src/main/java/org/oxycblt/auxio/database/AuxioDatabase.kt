package org.oxycblt.auxio.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlaybackState::class, QueueItem::class], version = 1, exportSchema = false)
abstract class AuxioDatabase : RoomDatabase() {
    abstract val playbackStateDAO: PlaybackStateDAO
    abstract val queueDAO: QueueDAO

    companion object {
        @Volatile
        private var INSTANCE: AuxioDatabase? = null

        /**
         * Get/Instantiate the single instance of [AuxioDatabase].
         */
        fun getInstance(context: Context): AuxioDatabase {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AuxioDatabase::class.java,
                    "playback_state_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

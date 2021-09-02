/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackStateDatabase.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.state

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.oxycblt.auxio.util.assertBackgroundThread
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.queryAll

/**
 * A SQLite database for managing the persistent playback state and queue.
 * Yes. I know Room exists. But that would needlessly bloat my app and has crippling bugs.
 * TODO: Improve the boundary between this and [PlaybackStateManager]. This would be more
 *  efficient.
 * @author OxygenCobalt
 */
class PlaybackStateDatabase(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        createTable(db, TABLE_NAME_STATE)
        createTable(db, TABLE_NAME_QUEUE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.apply {
            execSQL("DROP TABLE IF EXISTS $TABLE_NAME_STATE")
            execSQL("DROP TABLE IF EXISTS $TABLE_NAME_QUEUE")

            onCreate(this)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, newVersion, oldVersion)
    }

    // --- DATABASE CONSTRUCTION FUNCTIONS ---

    /**
     * Create a table for this database.
     */
    private fun createTable(database: SQLiteDatabase, tableName: String) {
        val command = StringBuilder()
        command.append("CREATE TABLE IF NOT EXISTS $tableName(")

        if (tableName == TABLE_NAME_STATE) {
            constructStateTable(command)
        } else if (tableName == TABLE_NAME_QUEUE) {
            constructQueueTable(command)
        }

        database.execSQL(command.toString())
    }

    /**
     * Construct a [DatabaseState] table
     */
    private fun constructStateTable(command: StringBuilder): StringBuilder {
        command.append("${DatabaseState.COLUMN_ID} LONG PRIMARY KEY,")
            .append("${DatabaseState.COLUMN_SONG_HASH} INTEGER NOT NULL,")
            .append("${DatabaseState.COLUMN_POSITION} LONG NOT NULL,")
            .append("${DatabaseState.COLUMN_PARENT_HASH} INTEGER NOT NULL,")
            .append("${DatabaseState.COLUMN_INDEX} INTEGER NOT NULL,")
            .append("${DatabaseState.COLUMN_MODE} INTEGER NOT NULL,")
            .append("${DatabaseState.COLUMN_IS_SHUFFLING} BOOLEAN NOT NULL,")
            .append("${DatabaseState.COLUMN_LOOP_MODE} INTEGER NOT NULL,")
            .append("${DatabaseState.COLUMN_IN_USER_QUEUE} BOOLEAN NOT NULL)")

        return command
    }

    /**
     * Construct a [DatabaseQueueItem] table
     */
    private fun constructQueueTable(command: StringBuilder): StringBuilder {
        command.append("${DatabaseQueueItem.COLUMN_ID} LONG PRIMARY KEY,")
            .append("${DatabaseQueueItem.COLUMN_SONG_HASH} INTEGER NOT NULL,")
            .append("${DatabaseQueueItem.COLUMN_ALBUM_HASH} INTEGER NOT NULL,")
            .append("${DatabaseQueueItem.COLUMN_IS_USER_QUEUE} BOOLEAN NOT NULL)")

        return command
    }

    // --- INTERFACE FUNCTIONS ---

    /**
     * Clear the previously written [DatabaseState] and write a new one.
     */
    fun writeState(state: DatabaseState) {
        assertBackgroundThread()

        writableDatabase.transaction {
            delete(TABLE_NAME_STATE, null, null)

            this@PlaybackStateDatabase.logD("Wiped state db.")

            val stateData = ContentValues(10).apply {
                put(DatabaseState.COLUMN_ID, state.id)
                put(DatabaseState.COLUMN_SONG_HASH, state.songHash)
                put(DatabaseState.COLUMN_POSITION, state.position)
                put(DatabaseState.COLUMN_PARENT_HASH, state.parentHash)
                put(DatabaseState.COLUMN_INDEX, state.index)
                put(DatabaseState.COLUMN_MODE, state.mode)
                put(DatabaseState.COLUMN_IS_SHUFFLING, state.isShuffling)
                put(DatabaseState.COLUMN_LOOP_MODE, state.loopMode)
                put(DatabaseState.COLUMN_IN_USER_QUEUE, state.inUserQueue)
            }

            insert(TABLE_NAME_STATE, null, stateData)
        }

        logD("Wrote state to database.")
    }

    /**
     * Read the stored [DatabaseState] from the database, if there is one.
     * @return The stored [DatabaseState], null if there isn't one.
     */
    fun readState(): DatabaseState? {
        assertBackgroundThread()

        var state: DatabaseState? = null

        readableDatabase.queryAll(TABLE_NAME_STATE) { cursor ->
            if (cursor.count == 0) return@queryAll

            val songIndex = cursor.getColumnIndexOrThrow(DatabaseState.COLUMN_SONG_HASH)
            val posIndex = cursor.getColumnIndexOrThrow(DatabaseState.COLUMN_POSITION)
            val parentIndex = cursor.getColumnIndexOrThrow(DatabaseState.COLUMN_PARENT_HASH)
            val indexIndex = cursor.getColumnIndexOrThrow(DatabaseState.COLUMN_INDEX)
            val modeIndex = cursor.getColumnIndexOrThrow(DatabaseState.COLUMN_MODE)
            val shuffleIndex = cursor.getColumnIndexOrThrow(DatabaseState.COLUMN_IS_SHUFFLING)
            val loopModeIndex = cursor.getColumnIndexOrThrow(DatabaseState.COLUMN_LOOP_MODE)
            val inUserQueueIndex = cursor.getColumnIndexOrThrow(
                DatabaseState.COLUMN_IN_USER_QUEUE
            )

            cursor.moveToFirst()

            state = DatabaseState(
                songHash = cursor.getInt(songIndex),
                position = cursor.getLong(posIndex),
                parentHash = cursor.getInt(parentIndex),
                index = cursor.getInt(indexIndex),
                mode = cursor.getInt(modeIndex),
                isShuffling = cursor.getInt(shuffleIndex) == 1,
                loopMode = cursor.getInt(loopModeIndex),
                inUserQueue = cursor.getInt(inUserQueueIndex) == 1
            )
        }

        return state
    }

    /**
     * Write a list of [queueItems] to the database, clearing the previous queue present.
     */
    fun writeQueue(queueItems: List<DatabaseQueueItem>) {
        assertBackgroundThread()

        val database = writableDatabase

        database.transaction {
            delete(TABLE_NAME_QUEUE, null, null)
        }

        logD("Wiped queue db.")

        var position = 0

        // Try to write out the entirety of the queue.
        while (position < queueItems.size) {
            var i = position

            database.transaction {
                while (i < queueItems.size) {
                    val item = queueItems[i]
                    i++

                    val itemData = ContentValues(4).apply {
                        put(DatabaseQueueItem.COLUMN_ID, item.id)
                        put(DatabaseQueueItem.COLUMN_SONG_HASH, item.songHash)
                        put(DatabaseQueueItem.COLUMN_ALBUM_HASH, item.albumHash)
                        put(DatabaseQueueItem.COLUMN_IS_USER_QUEUE, item.isUserQueue)
                    }

                    insert(TABLE_NAME_QUEUE, null, itemData)
                }
            }

            // Update the position at the end, if an insert failed at any point, then
            // the next iteration should skip it.
            position = i

            logD("Wrote batch of songs. Position is now at $position")
        }
    }

    /**
     * Read the database for any [DatabaseQueueItem]s.
     * @return A list of any stored [DatabaseQueueItem]s.
     */
    fun readQueue(): List<DatabaseQueueItem> {
        assertBackgroundThread()

        val queueItems = mutableListOf<DatabaseQueueItem>()

        readableDatabase.queryAll(TABLE_NAME_QUEUE) { cursor ->
            if (cursor.count == 0) return@queryAll

            val idIndex = cursor.getColumnIndexOrThrow(DatabaseQueueItem.COLUMN_ID)
            val songIdIndex = cursor.getColumnIndexOrThrow(DatabaseQueueItem.COLUMN_SONG_HASH)
            val albumIdIndex = cursor.getColumnIndexOrThrow(DatabaseQueueItem.COLUMN_ALBUM_HASH)
            val isUserQueueIndex = cursor.getColumnIndexOrThrow(DatabaseQueueItem.COLUMN_IS_USER_QUEUE)

            while (cursor.moveToNext()) {
                queueItems += DatabaseQueueItem(
                    id = cursor.getLong(idIndex),
                    songHash = cursor.getInt(songIdIndex),
                    albumHash = cursor.getInt(albumIdIndex),
                    isUserQueue = cursor.getInt(isUserQueueIndex) == 1
                )
            }
        }

        return queueItems
    }

    companion object {
        const val DB_NAME = "auxio_state_database.db"
        const val DB_VERSION = 4

        const val TABLE_NAME_STATE = "playback_state_table"
        const val TABLE_NAME_QUEUE = "queue_table"

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
                val newInstance = PlaybackStateDatabase(context.applicationContext)
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

/**
 * A database entity that stores a simplified representation of a song in a queue.
 * @property id The database entity's id
 * @property songHash The hash for the song represented
 * @property albumHash The hash for the album represented
 * @property isUserQueue A bool for if this queue item is a user queue item or not
 * @author OxygenCobalt
 */
data class DatabaseQueueItem(
    var id: Long = 0L,
    val songHash: Int,
    val albumHash: Int,
    val isUserQueue: Boolean = false
) {
    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_HASH = "song"
        const val COLUMN_ALBUM_HASH = "album"
        const val COLUMN_IS_USER_QUEUE = "is_user_queue"
    }
}

/**
 * A database entity that stores a compressed variant of the current playback state.
 * @property id - The database key for this state
 * @property songHash - The hash for the currently playing song
 * @property parentHash - The hash for the currently playing parent
 * @property index - The current index in the queue.
 * @property mode - The integer form of the current [org.oxycblt.auxio.playback.state.PlaybackMode]
 * @property isShuffling - A bool for if the queue was shuffled
 * @property loopMode - The integer form of the current [org.oxycblt.auxio.playback.state.LoopMode]
 * @property inUserQueue - A bool for if the state was currently playing from the user queue.
 * @author OxygenCobalt
 */
data class DatabaseState(
    val id: Long = 0L,
    val songHash: Int,
    val position: Long,
    val parentHash: Int,
    val index: Int,
    val mode: Int,
    val isShuffling: Boolean,
    val loopMode: Int,
    val inUserQueue: Boolean
) {
    companion object {
        const val COLUMN_ID = "state_id"
        const val COLUMN_SONG_HASH = "song"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PARENT_HASH = "parent"
        const val COLUMN_INDEX = "_index"
        const val COLUMN_MODE = "mode"
        const val COLUMN_IS_SHUFFLING = "is_shuffling"
        const val COLUMN_LOOP_MODE = "loop_mode"
        const val COLUMN_IN_USER_QUEUE = "is_user_queue"
    }
}

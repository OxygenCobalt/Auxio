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

package org.oxycblt.auxio.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.ui.assertBackgroundThread

/**
 * A SQLite database for managing the persistent playback state and queue.
 * Yes. I know Room exists. But that would needlessly bloat my app and has crippling bugs.
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
     * Construct a [PlaybackState] table
     */
    private fun constructStateTable(command: StringBuilder): StringBuilder {
        command.append("${PlaybackState.COLUMN_ID} LONG PRIMARY KEY,")
            .append("${PlaybackState.COLUMN_SONG_HASH} INTEGER NOT NULL,")
            .append("${PlaybackState.COLUMN_POSITION} LONG NOT NULL,")
            .append("${PlaybackState.COLUMN_PARENT_HASH} INTEGER NOT NULL,")
            .append("${PlaybackState.COLUMN_INDEX} INTEGER NOT NULL,")
            .append("${PlaybackState.COLUMN_MODE} INTEGER NOT NULL,")
            .append("${PlaybackState.COLUMN_IS_SHUFFLING} BOOLEAN NOT NULL,")
            .append("${PlaybackState.COLUMN_LOOP_MODE} INTEGER NOT NULL,")
            .append("${PlaybackState.COLUMN_IN_USER_QUEUE} BOOLEAN NOT NULL)")

        return command
    }

    /**
     * Construct a [QueueItem] table
     */
    private fun constructQueueTable(command: StringBuilder): StringBuilder {
        command.append("${QueueItem.COLUMN_ID} LONG PRIMARY KEY,")
            .append("${QueueItem.COLUMN_SONG_HASH} INTEGER NOT NULL,")
            .append("${QueueItem.COLUMN_ALBUM_HASH} INTEGER NOT NULL,")
            .append("${QueueItem.COLUMN_IS_USER_QUEUE} BOOLEAN NOT NULL)")

        return command
    }

    // --- INTERFACE FUNCTIONS ---

    /**
     * Clear the previously written [PlaybackState] and write a new one.
     */
    fun writeState(state: PlaybackState) {
        assertBackgroundThread()

        writableDatabase.transaction {
            delete(TABLE_NAME_STATE, null, null)

            this@PlaybackStateDatabase.logD("Wiped state db.")

            val stateData = ContentValues(10).apply {
                put(PlaybackState.COLUMN_ID, state.id)
                put(PlaybackState.COLUMN_SONG_HASH, state.songHash)
                put(PlaybackState.COLUMN_POSITION, state.position)
                put(PlaybackState.COLUMN_PARENT_HASH, state.parentHash)
                put(PlaybackState.COLUMN_INDEX, state.index)
                put(PlaybackState.COLUMN_MODE, state.mode)
                put(PlaybackState.COLUMN_IS_SHUFFLING, state.isShuffling)
                put(PlaybackState.COLUMN_LOOP_MODE, state.loopMode)
                put(PlaybackState.COLUMN_IN_USER_QUEUE, state.inUserQueue)
            }

            insert(TABLE_NAME_STATE, null, stateData)
        }

        logD("Wrote state to database.")
    }

    /**
     * Read the stored [PlaybackState] from the database, if there is one.
     * @return The stored [PlaybackState], null if there isn't one.
     */
    fun readState(): PlaybackState? {
        assertBackgroundThread()

        var state: PlaybackState? = null

        readableDatabase.queryAll(TABLE_NAME_STATE) { cursor ->
            if (cursor.count == 0) return@queryAll

            val songIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_SONG_HASH)
            val posIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_POSITION)
            val parentIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_PARENT_HASH)
            val indexIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_INDEX)
            val modeIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_MODE)
            val shuffleIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_IS_SHUFFLING)
            val loopModeIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_LOOP_MODE)
            val inUserQueueIndex = cursor.getColumnIndexOrThrow(
                PlaybackState.COLUMN_IN_USER_QUEUE
            )

            cursor.moveToFirst()

            state = PlaybackState(
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
    fun writeQueue(queueItems: List<QueueItem>) {
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
                        put(QueueItem.COLUMN_ID, item.id)
                        put(QueueItem.COLUMN_SONG_HASH, item.songHash)
                        put(QueueItem.COLUMN_ALBUM_HASH, item.albumHash)
                        put(QueueItem.COLUMN_IS_USER_QUEUE, item.isUserQueue)
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
     * Read the database for any [QueueItem]s.
     * @return A list of any stored [QueueItem]s.
     */
    fun readQueue(): List<QueueItem> {
        assertBackgroundThread()

        val queueItems = mutableListOf<QueueItem>()

        readableDatabase.queryAll(TABLE_NAME_QUEUE) { cursor ->
            if (cursor.count == 0) return@queryAll

            val idIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_ID)
            val songIdIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_SONG_HASH)
            val albumIdIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_ALBUM_HASH)
            val isUserQueueIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_IS_USER_QUEUE)

            while (cursor.moveToNext()) {
                queueItems += QueueItem(
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

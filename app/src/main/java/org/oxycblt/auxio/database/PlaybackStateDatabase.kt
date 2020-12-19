package org.oxycblt.auxio.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.oxycblt.auxio.logD

/**
 * A SQLite database for managing the persistent playback state and queue.
 * Yes, I know androidx has Room which supposedly makes database creation easier, but it also
 * has a crippling bug where it will endlessly allocate rows even if you clear the entire db, so...
 * @author OxygenCobalt
 */
class PlaybackStateDatabase(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        createTable(db, TABLE_NAME_STATE)
        createTable(db, TABLE_NAME_QUEUE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_STATE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_QUEUE")

        onCreate(db)
    }

    // --- DATABASE CONSTRUCTION FUNCTIONS ---

    /**
     * Create a table
     * @param database DB to create the tables on
     * @param tableName The name of the table to create.
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
        command.append("${PlaybackState.COLUMN_SONG_ID} LONG NOT NULL,")
        command.append("${PlaybackState.COLUMN_POSITION} LONG NOT NULL,")
        command.append("${PlaybackState.COLUMN_PARENT_ID} LONG NOT NULL,")
        command.append("${PlaybackState.COLUMN_INDEX} INTEGER NOT NULL,")
        command.append("${PlaybackState.COLUMN_MODE} INTEGER NOT NULL,")
        command.append("${PlaybackState.COLUMN_IS_SHUFFLING} BOOLEAN NOT NULL,")
        command.append("${PlaybackState.COLUMN_LOOP_MODE} INTEGER NOT NULL,")
        command.append("${PlaybackState.COLUMN_IN_USER_QUEUE} BOOLEAN NOT NULL)")

        return command
    }

    /**
     * Construct a [QueueItem] table
     */
    private fun constructQueueTable(command: StringBuilder): StringBuilder {
        command.append("${QueueItem.COLUMN_ID} LONG PRIMARY KEY,")
        command.append("${QueueItem.COLUMN_SONG_ID} LONG NOT NULL,")
        command.append("${QueueItem.COLUMN_ALBUM_ID} LONG NOT NULL,")
        command.append("${QueueItem.COLUMN_IS_USER_QUEUE} BOOLEAN NOT NULL)")

        return command
    }

    // --- INTERFACE FUNCTIONS ---

    /**
     * Clear the previously written [PlaybackState] and write a new one.
     */
    fun writeState(state: PlaybackState) {
        val database = writableDatabase
        database.beginTransaction()

        try {
            database.delete(TABLE_NAME_STATE, null, null)
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()

            logD("Successfully wiped previous state.")
        }

        try {
            database.beginTransaction()

            val stateData = ContentValues(9)

            stateData.put(PlaybackState.COLUMN_ID, state.id)
            stateData.put(PlaybackState.COLUMN_SONG_ID, state.songId)
            stateData.put(PlaybackState.COLUMN_POSITION, state.position)
            stateData.put(PlaybackState.COLUMN_PARENT_ID, state.parentId)
            stateData.put(PlaybackState.COLUMN_INDEX, state.index)
            stateData.put(PlaybackState.COLUMN_MODE, state.mode)
            stateData.put(PlaybackState.COLUMN_IS_SHUFFLING, state.isShuffling)
            stateData.put(PlaybackState.COLUMN_LOOP_MODE, state.loopMode)
            stateData.put(PlaybackState.COLUMN_IN_USER_QUEUE, state.inUserQueue)

            database.insert(TABLE_NAME_STATE, null, stateData)
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()

            logD("Wrote state to database.")
        }
    }

    /**
     * Read the stored [PlaybackState] from the database, if there is one.
     * @return The stored [PlaybackState], null if there isn't one.
     */
    fun readState(): PlaybackState? {
        val database = writableDatabase

        var state: PlaybackState? = null
        val stateCursor: Cursor

        try {
            stateCursor = database.query(
                TABLE_NAME_STATE,
                null, null, null,
                null, null, null
            )

            stateCursor?.use { cursor ->
                // Don't bother if the cursor [and therefore database] has nothing in it.
                if (cursor.count == 0) return@use

                val songIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_SONG_ID)
                val positionIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_POSITION)
                val parentIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_PARENT_ID)
                val indexIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_INDEX)
                val modeIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_MODE)
                val isShufflingIndex =
                    cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_IS_SHUFFLING)
                val loopModeIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_LOOP_MODE)
                val inUserQueueIndex =
                    cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_IN_USER_QUEUE)

                // If there is something in it, get the first item from it, ignoring anything else.
                cursor.moveToFirst()

                state = PlaybackState(
                    songId = cursor.getLong(songIndex),
                    position = cursor.getLong(positionIndex),
                    parentId = cursor.getLong(parentIndex),
                    index = cursor.getInt(indexIndex),
                    mode = cursor.getInt(modeIndex),
                    isShuffling = cursor.getInt(isShufflingIndex) == 1,
                    loopMode = cursor.getInt(loopModeIndex),
                    inUserQueue = cursor.getInt(inUserQueueIndex) == 1
                )
            }
        } finally {
            return state
        }
    }

    /**
     * Write a list of [QueueItem]s to the database, clearing the previous queue present.
     * @param queue The list of [QueueItem]s to be written.
     */
    fun writeQueue(queue: List<QueueItem>) {
        val database = readableDatabase
        database.beginTransaction()

        try {
            database.delete(TABLE_NAME_QUEUE, null, null)
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()

            logD("Successfully wiped queue.")
        }

        logD("Writing to queue.")

        var position = 0

        // Try to write out the entirety of the queue, any failed inserts will be skipped.
        while (position < queue.size) {
            database.beginTransaction()
            var i = position

            try {
                while (i < queue.size) {
                    val item = queue[i]
                    val itemData = ContentValues(4)

                    i++

                    itemData.put(QueueItem.COLUMN_ID, item.id)
                    itemData.put(QueueItem.COLUMN_SONG_ID, item.songId)
                    itemData.put(QueueItem.COLUMN_ALBUM_ID, item.albumId)
                    itemData.put(QueueItem.COLUMN_IS_USER_QUEUE, item.isUserQueue)

                    database.insert(TABLE_NAME_QUEUE, null, itemData)
                }

                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()

                // Update the position at the end, if an insert failed at any point, then
                // the next iteration should skip it.
                position = i

                logD("Wrote batch of $position songs.")
            }
        }
    }

    /**
     * Read the database for any [QueueItem]s.
     * @return A list of any stored [QueueItem]s.
     */
    fun readQueue(): List<QueueItem> {
        val database = readableDatabase

        val queueItems = mutableListOf<QueueItem>()
        val queueCursor: Cursor

        try {
            queueCursor = database.query(
                TABLE_NAME_QUEUE, null, null,
                null, null, null, null
            )

            queueCursor?.use { cursor ->
                if (cursor.count == 0) return@use

                val idIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_ID)
                val songIdIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_SONG_ID)
                val albumIdIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_ALBUM_ID)
                val isUserQueueIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_IS_USER_QUEUE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val songId = cursor.getLong(songIdIndex)
                    val albumId = cursor.getLong(albumIdIndex)
                    val isUserQueue = cursor.getInt(isUserQueueIndex) == 1

                    queueItems.add(
                        QueueItem(id, songId, albumId, isUserQueue)
                    )
                }
            }
        } finally {
            return queueItems
        }
    }

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "auxio_state_database"

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

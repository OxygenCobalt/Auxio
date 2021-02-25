package org.oxycblt.auxio.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Looper
import androidx.core.database.getStringOrNull
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
        db.apply {
            execSQL("DROP TABLE IF EXISTS $TABLE_NAME_STATE")
            execSQL("DROP TABLE IF EXISTS $TABLE_NAME_QUEUE")

            onCreate(this)
        }
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
            .append("${PlaybackState.COLUMN_SONG_NAME} STRING NOT NULL,")
            .append("${PlaybackState.COLUMN_POSITION} LONG NOT NULL,")
            .append("${PlaybackState.COLUMN_PARENT_NAME} STRING NOT NULL,")
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
            .append("${QueueItem.COLUMN_SONG_NAME} LONG NOT NULL,")
            .append("${QueueItem.COLUMN_ALBUM_NAME} LONG NOT NULL,")
            .append("${QueueItem.COLUMN_IS_USER_QUEUE} BOOLEAN NOT NULL)")

        return command
    }

    // --- INTERFACE FUNCTIONS ---

    /**
     * Clear the previously written [PlaybackState] and write a new one.
     */
    fun writeState(state: PlaybackState) {
        assertBackgroundThread()

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

            stateData.apply {
                put(PlaybackState.COLUMN_ID, state.id)
                put(PlaybackState.COLUMN_SONG_NAME, state.songName)
                put(PlaybackState.COLUMN_POSITION, state.position)
                put(PlaybackState.COLUMN_PARENT_NAME, state.parentName)
                put(PlaybackState.COLUMN_INDEX, state.index)
                put(PlaybackState.COLUMN_MODE, state.mode)
                put(PlaybackState.COLUMN_IS_SHUFFLING, state.isShuffling)
                put(PlaybackState.COLUMN_LOOP_MODE, state.loopMode)
                put(PlaybackState.COLUMN_IN_USER_QUEUE, state.inUserQueue)
            }

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
        assertBackgroundThread()

        val database = writableDatabase

        var state: PlaybackState? = null

        try {
            val stateCursor = database.query(
                TABLE_NAME_STATE,
                null, null, null,
                null, null, null
            )

            stateCursor?.use { cursor ->
                // Don't bother if the cursor [and therefore database] has nothing in it.
                if (cursor.count == 0) return@use

                val songIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_SONG_NAME)
                val positionIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_POSITION)
                val parentIndex = cursor.getColumnIndexOrThrow(PlaybackState.COLUMN_PARENT_NAME)
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
                    songName = cursor.getStringOrNull(songIndex) ?: "",
                    position = cursor.getLong(positionIndex),
                    parentName = cursor.getStringOrNull(parentIndex) ?: "",
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
     * Write a list of [queueItems] to the database, clearing the previous queue present.
     */
    fun writeQueue(queueItems: List<QueueItem>) {
        assertBackgroundThread()

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
        while (position < queueItems.size) {
            database.beginTransaction()
            var i = position

            try {
                while (i < queueItems.size) {
                    val item = queueItems[i]
                    val itemData = ContentValues(4)

                    i++

                    itemData.apply {
                        put(QueueItem.COLUMN_ID, item.id)
                        put(QueueItem.COLUMN_SONG_NAME, item.songName)
                        put(QueueItem.COLUMN_ALBUM_NAME, item.albumName)
                        put(QueueItem.COLUMN_IS_USER_QUEUE, item.isUserQueue)
                    }

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
        assertBackgroundThread()

        val database = readableDatabase
        val queueItems = mutableListOf<QueueItem>()

        try {
            val queueCursor = database.query(
                TABLE_NAME_QUEUE, null, null,
                null, null, null, null
            )

            queueCursor?.use { cursor ->
                if (cursor.count == 0) return@use

                val idIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_ID)
                val songIdIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_SONG_NAME)
                val albumIdIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_ALBUM_NAME)
                val isUserQueueIndex = cursor.getColumnIndexOrThrow(QueueItem.COLUMN_IS_USER_QUEUE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val songName = cursor.getStringOrNull(songIdIndex) ?: ""
                    val albumName = cursor.getStringOrNull(albumIdIndex) ?: ""
                    val isUserQueue = cursor.getInt(isUserQueueIndex) == 1

                    queueItems.add(
                        QueueItem(id, songName, albumName, isUserQueue)
                    )
                }
            }
        } finally {
            return queueItems
        }
    }

    private fun assertBackgroundThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            error("Not on a background thread.")
        }
    }

    companion object {
        const val DB_VERSION = 2
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

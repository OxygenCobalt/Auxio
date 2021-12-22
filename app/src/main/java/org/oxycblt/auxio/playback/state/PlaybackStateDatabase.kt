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
import androidx.core.database.getLongOrNull
import androidx.core.database.sqlite.transaction
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.assertBackgroundThread
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.queryAll

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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    private fun nuke(db: SQLiteDatabase) {
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
     * Construct a [StateColumns] table
     */
    private fun constructStateTable(command: StringBuilder): StringBuilder {
        command.append("${StateColumns.COLUMN_ID} LONG PRIMARY KEY,")
            .append("${StateColumns.COLUMN_SONG_HASH} LONG,")
            .append("${StateColumns.COLUMN_POSITION} LONG NOT NULL,")
            .append("${StateColumns.COLUMN_PARENT_HASH} LONG,")
            .append("${StateColumns.COLUMN_QUEUE_INDEX} INTEGER NOT NULL,")
            .append("${StateColumns.COLUMN_PLAYBACK_MODE} INTEGER NOT NULL,")
            .append("${StateColumns.COLUMN_IS_SHUFFLING} BOOLEAN NOT NULL,")
            .append("${StateColumns.COLUMN_LOOP_MODE} INTEGER NOT NULL)")

        return command
    }

    /**
     * Construct a [QueueColumns] table
     */
    private fun constructQueueTable(command: StringBuilder): StringBuilder {
        command.append("${QueueColumns.ID} LONG PRIMARY KEY,")
            .append("${QueueColumns.SONG_HASH} INTEGER NOT NULL,")
            .append("${QueueColumns.ALBUM_HASH} INTEGER NOT NULL)")

        return command
    }

    // --- INTERFACE FUNCTIONS ---

    /**
     * Clear the previously written [SavedState] and write a new one.
     */
    fun writeState(state: SavedState) {
        assertBackgroundThread()

        writableDatabase.transaction {
            delete(TABLE_NAME_STATE, null, null)

            this@PlaybackStateDatabase.logD("Wiped state db.")

            val stateData = ContentValues(10).apply {
                put(StateColumns.COLUMN_ID, 0)
                put(StateColumns.COLUMN_SONG_HASH, state.song?.hash)
                put(StateColumns.COLUMN_POSITION, state.position)
                put(StateColumns.COLUMN_PARENT_HASH, state.parent?.hash)
                put(StateColumns.COLUMN_QUEUE_INDEX, state.queueIndex)
                put(StateColumns.COLUMN_PLAYBACK_MODE, state.playbackMode.toInt())
                put(StateColumns.COLUMN_IS_SHUFFLING, state.isShuffling)
                put(StateColumns.COLUMN_LOOP_MODE, state.loopMode.toInt())
            }

            insert(TABLE_NAME_STATE, null, stateData)
        }

        logD("Wrote state to database.")
    }

    /**
     * Read the stored [SavedState] from the database, if there is one.
     * @param musicStore Required to transform database songs/parents into actual instances
     * @return The stored [SavedState], null if there isn't one.
     */
    fun readState(musicStore: MusicStore): SavedState? {
        assertBackgroundThread()

        var state: SavedState? = null

        readableDatabase.queryAll(TABLE_NAME_STATE) { cursor ->
            if (cursor.count == 0) return@queryAll

            val songIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_SONG_HASH)
            val posIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_POSITION)
            val parentIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_PARENT_HASH)
            val indexIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_QUEUE_INDEX)
            val modeIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_PLAYBACK_MODE)
            val shuffleIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_IS_SHUFFLING)
            val loopModeIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_LOOP_MODE)

            cursor.moveToFirst()

            val song = cursor.getLongOrNull(songIndex)?.let { hash ->
                musicStore.songs.find { it.hash == hash }
            }

            val mode = PlaybackMode.fromInt(cursor.getInt(modeIndex)) ?: PlaybackMode.ALL_SONGS

            val parent = cursor.getLongOrNull(parentIndex)?.let { hash ->
                when (mode) {
                    PlaybackMode.IN_GENRE -> musicStore.genres.find { it.hash == hash }
                    PlaybackMode.IN_ARTIST -> musicStore.artists.find { it.hash == hash }
                    PlaybackMode.IN_ALBUM -> musicStore.albums.find { it.hash == hash }
                    PlaybackMode.ALL_SONGS -> null
                }
            }

            state = SavedState(
                song = song,
                position = cursor.getLong(posIndex),
                parent = parent,
                queueIndex = cursor.getInt(indexIndex),
                playbackMode = mode,
                isShuffling = cursor.getInt(shuffleIndex) == 1,
                loopMode = LoopMode.fromInt(cursor.getInt(loopModeIndex)) ?: LoopMode.NONE,
            )
        }

        return state
    }

    /**
     * Write a queue to the database.
     */
    fun writeQueue(queue: MutableList<Song>) {
        assertBackgroundThread()

        val database = writableDatabase

        database.transaction {
            delete(TABLE_NAME_QUEUE, null, null)
        }

        logD("Wiped queue db.")

        writeQueueBatch(queue, queue.size)
    }

    private fun writeQueueBatch(queue: List<Song>, idStart: Int) {
        logD("Beginning queue write [start: $idStart]")

        val database = writableDatabase
        var position = 0

        while (position < queue.size) {
            var i = position

            database.transaction {
                while (i < queue.size) {
                    val song = queue[i]
                    i++

                    val itemData = ContentValues(4).apply {
                        put(QueueColumns.ID, idStart + i)
                        put(QueueColumns.SONG_HASH, song.hash)
                        put(QueueColumns.ALBUM_HASH, song.album.hash)
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
     * Read a list of queue items from this database.
     * @param musicStore Required to transform database songs into actual song instances
     */
    fun readQueue(musicStore: MusicStore): MutableList<Song> {
        assertBackgroundThread()

        val queue = mutableListOf<Song>()

        readableDatabase.queryAll(TABLE_NAME_QUEUE) { cursor ->
            if (cursor.count == 0) return@queryAll

            val songIndex = cursor.getColumnIndexOrThrow(QueueColumns.SONG_HASH)
            val albumIndex = cursor.getColumnIndexOrThrow(QueueColumns.ALBUM_HASH)

            while (cursor.moveToNext()) {
                musicStore.findSongFast(cursor.getLong(songIndex), cursor.getLong(albumIndex))
                    ?.let { song ->
                        queue.add(song)
                    }
            }
        }

        return queue
    }

    data class SavedState(
        val song: Song?,
        val position: Long,
        val parent: MusicParent?,
        val queueIndex: Int,
        val playbackMode: PlaybackMode,
        val isShuffling: Boolean,
        val loopMode: LoopMode,
    )

    private object StateColumns {
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_HASH = "song"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PARENT_HASH = "parent"
        const val COLUMN_QUEUE_INDEX = "queue_index"
        const val COLUMN_PLAYBACK_MODE = "playback_mode"
        const val COLUMN_IS_SHUFFLING = "is_shuffling"
        const val COLUMN_LOOP_MODE = "loop_mode"
    }

    private object QueueColumns {
        const val ID = "id"
        const val SONG_HASH = "song"
        const val ALBUM_HASH = "album"
    }

    companion object {
        const val DB_NAME = "auxio_state_database.db"
        const val DB_VERSION = 6

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

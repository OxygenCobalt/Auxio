/*
 * Copyright (c) 2021 Auxio Project
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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.queryAll
import org.oxycblt.auxio.util.requireBackgroundThread

/**
 * A SQLite database for managing the persistent playback state and queue. Yes. I know Room exists.
 * But that would needlessly bloat my app and has crippling bugs.
 * @author OxygenCobalt
 */
class PlaybackStateDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        createTable(db, TABLE_NAME_STATE)
        createTable(db, TABLE_NAME_QUEUE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    private fun nuke(db: SQLiteDatabase) {
        logD("Nuking database")
        db.apply {
            execSQL("DROP TABLE IF EXISTS $TABLE_NAME_STATE")
            execSQL("DROP TABLE IF EXISTS $TABLE_NAME_QUEUE")
            onCreate(this)
        }
    }

    // --- DATABASE CONSTRUCTION FUNCTIONS ---

    /** Create a table for this database. */
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

    /** Construct a [StateColumns] table */
    private fun constructStateTable(command: StringBuilder): StringBuilder {
        command
            .append("${StateColumns.COLUMN_ID} LONG PRIMARY KEY,")
            .append("${StateColumns.COLUMN_SONG_ID} LONG,")
            .append("${StateColumns.COLUMN_POSITION} LONG NOT NULL,")
            .append("${StateColumns.COLUMN_PARENT_ID} LONG,")
            .append("${StateColumns.COLUMN_INDEX} INTEGER NOT NULL,")
            .append("${StateColumns.COLUMN_PLAYBACK_MODE} INTEGER NOT NULL,")
            .append("${StateColumns.COLUMN_IS_SHUFFLED} BOOLEAN NOT NULL,")
            .append("${StateColumns.COLUMN_REPEAT_MODE} INTEGER NOT NULL)")

        return command
    }

    /** Construct a [QueueColumns] table */
    private fun constructQueueTable(command: StringBuilder): StringBuilder {
        command
            .append("${QueueColumns.ID} LONG PRIMARY KEY,")
            .append("${QueueColumns.SONG_ID} INTEGER NOT NULL,")
            .append("${QueueColumns.ALBUM_ID} INTEGER NOT NULL)")

        return command
    }

    // --- INTERFACE FUNCTIONS ---

    fun read(library: MusicStore.Library): SavedState? {
        requireBackgroundThread()

        val rawState = readRawState() ?: return null
        val queue = readQueue(library)

        // Correct the index to match up with a possibly shortened queue (file removals/changes)
        var actualIndex = rawState.index
        while (queue.getOrNull(actualIndex)?.id != rawState.songId && actualIndex > -1) {
            actualIndex--
        }

        val parent =
            when (rawState.playbackMode) {
                PlaybackMode.ALL_SONGS -> null
                PlaybackMode.IN_ALBUM -> library.albums.find { it.id == rawState.parentId }
                PlaybackMode.IN_ARTIST -> library.artists.find { it.id == rawState.parentId }
                PlaybackMode.IN_GENRE -> library.genres.find { it.id == rawState.parentId }
            }

        return SavedState(
            index = actualIndex,
            parent = parent,
            queue = queue,
            positionMs = rawState.positionMs,
            repeatMode = rawState.repeatMode,
            isShuffled = rawState.isShuffled,
        )
    }

    private fun readRawState(): RawState? {
        return readableDatabase.queryAll(TABLE_NAME_STATE) { cursor ->
            if (cursor.count == 0) {
                return@queryAll null
            }

            val indexIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_INDEX)

            val posIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_POSITION)
            val playbackModeIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_PLAYBACK_MODE)
            val repeatModeIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_REPEAT_MODE)
            val shuffleIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_IS_SHUFFLED)
            val songIdIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_SONG_ID)
            val parentIdIndex = cursor.getColumnIndexOrThrow(StateColumns.COLUMN_PARENT_ID)

            cursor.moveToFirst()

            RawState(
                index = cursor.getInt(indexIndex),
                positionMs = cursor.getLong(posIndex),
                repeatMode = RepeatMode.fromIntCode(cursor.getInt(repeatModeIndex))
                        ?: RepeatMode.NONE,
                isShuffled = cursor.getInt(shuffleIndex) == 1,
                songId = cursor.getLong(songIdIndex),
                parentId = cursor.getLongOrNull(parentIdIndex),
                playbackMode = PlaybackMode.fromInt(cursor.getInt(playbackModeIndex))
                        ?: PlaybackMode.ALL_SONGS)
        }
    }

    private fun readQueue(library: MusicStore.Library): MutableList<Song> {
        requireBackgroundThread()

        val queue = mutableListOf<Song>()

        readableDatabase.queryAll(TABLE_NAME_QUEUE) { cursor ->
            if (cursor.count == 0) return@queryAll

            val songIndex = cursor.getColumnIndexOrThrow(QueueColumns.SONG_ID)
            val albumIndex = cursor.getColumnIndexOrThrow(QueueColumns.ALBUM_ID)

            while (cursor.moveToNext()) {
                library.findSongFast(cursor.getLong(songIndex), cursor.getLong(albumIndex))?.let {
                    song ->
                    queue.add(song)
                }
            }
        }

        logD("Successfully read queue of ${queue.size} songs")

        return queue
    }

    /** Clear the previously written [SavedState] and write a new one. */
    fun write(state: SavedState?) {
        requireBackgroundThread()

        if (state != null && state.index in state.queue.indices) {
            val rawState =
                RawState(
                    index = state.index,
                    positionMs = state.positionMs,
                    repeatMode = state.repeatMode,
                    isShuffled = state.isShuffled,
                    songId = state.queue[state.index].id,
                    parentId = state.parent?.id,
                    playbackMode =
                        when (state.parent) {
                            null -> PlaybackMode.ALL_SONGS
                            is Album -> PlaybackMode.IN_ALBUM
                            is Artist -> PlaybackMode.IN_ARTIST
                            is Genre -> PlaybackMode.IN_GENRE
                        })

            writeRawState(rawState)
            writeQueue(state.queue)
        } else {
            writeRawState(null)
            writeQueue(null)
        }

        logD("Wrote state to database")
    }

    private fun writeRawState(rawState: RawState?) {
        writableDatabase.transaction {
            delete(TABLE_NAME_STATE, null, null)

            if (rawState != null) {
                val stateData =
                    ContentValues(10).apply {
                        put(StateColumns.COLUMN_ID, 0)
                        put(StateColumns.COLUMN_SONG_ID, rawState.songId)
                        put(StateColumns.COLUMN_POSITION, rawState.positionMs)
                        put(StateColumns.COLUMN_PARENT_ID, rawState.parentId)
                        put(StateColumns.COLUMN_INDEX, rawState.index)
                        put(StateColumns.COLUMN_PLAYBACK_MODE, rawState.playbackMode.intCode)
                        put(StateColumns.COLUMN_IS_SHUFFLED, rawState.isShuffled)
                        put(StateColumns.COLUMN_REPEAT_MODE, rawState.repeatMode.intCode)
                    }

                insert(TABLE_NAME_STATE, null, stateData)
            }
        }
    }

    /** Write a queue to the database. */
    private fun writeQueue(queue: List<Song>?) {
        val database = writableDatabase
        database.transaction { delete(TABLE_NAME_QUEUE, null, null) }

        logD("Wiped queue db")

        if (queue != null) {
            val idStart = queue.size
            logD("Beginning queue write [start: $idStart]")
            var position = 0

            while (position < queue.size) {
                var i = position

                database.transaction {
                    while (i < queue.size) {
                        val song = queue[i]
                        i++

                        val itemData =
                            ContentValues(4).apply {
                                put(QueueColumns.ID, idStart + i)
                                put(QueueColumns.SONG_ID, song.id)
                                put(QueueColumns.ALBUM_ID, song.album.id)
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
    }

    data class SavedState(
        val index: Int,
        val queue: List<Song>,
        val parent: MusicParent?,
        val positionMs: Long,
        val repeatMode: RepeatMode,
        val isShuffled: Boolean,
    )

    private data class RawState(
        val index: Int,
        val positionMs: Long,
        val repeatMode: RepeatMode,
        val isShuffled: Boolean,
        val songId: Long,
        val parentId: Long?,
        val playbackMode: PlaybackMode
    )

    private object StateColumns {
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_ID = "song"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PARENT_ID = "parent"
        const val COLUMN_INDEX = "queue_index"
        const val COLUMN_PLAYBACK_MODE = "playback_mode"
        const val COLUMN_IS_SHUFFLED = "is_shuffling"
        const val COLUMN_REPEAT_MODE = "repeat_mode"
    }

    private object QueueColumns {
        const val ID = "id"
        const val SONG_ID = "song"
        const val ALBUM_ID = "album"
    }

    companion object {
        const val DB_NAME = "auxio_state_database.db"
        const val DB_VERSION = 7

        const val TABLE_NAME_STATE = "playback_state_table"
        const val TABLE_NAME_QUEUE = "queue_table"

        @Volatile private var INSTANCE: PlaybackStateDatabase? = null

        /** Get/Instantiate the single instance of [PlaybackStateDatabase]. */
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

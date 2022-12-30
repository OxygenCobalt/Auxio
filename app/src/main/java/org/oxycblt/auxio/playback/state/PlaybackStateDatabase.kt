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
import android.provider.BaseColumns
import androidx.core.database.sqlite.transaction
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.*

/**
 * A [SQLiteDatabase] that persists the current playback state for future app lifecycles.
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaybackStateDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Here, we have to split the database into two tables. One contains the queue with
        // an indefinite amount of items, and the other contains only one entry consisting
        // of the non-queue parts of the state, such as the playback position.
        db.createTable(TABLE_STATE) {
            append("${BaseColumns._ID} INTEGER PRIMARY KEY,")
            append("${StateColumns.INDEX} INTEGER NOT NULL,")
            append("${StateColumns.POSITION} LONG NOT NULL,")
            append("${StateColumns.REPEAT_MODE} INTEGER NOT NULL,")
            append("${StateColumns.IS_SHUFFLED} BOOLEAN NOT NULL,")
            append("${StateColumns.SONG_UID} STRING,")
            append("${StateColumns.PARENT_UID} STRING")
        }

        db.createTable(TABLE_QUEUE) {
            append("${BaseColumns._ID} INTEGER PRIMARY KEY,")
            append("${QueueColumns.SONG_UID} STRING NOT NULL")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    private fun nuke(db: SQLiteDatabase) {
        logD("Nuking database")
        db.apply {
            execSQL("DROP TABLE IF EXISTS $TABLE_STATE")
            execSQL("DROP TABLE IF EXISTS $TABLE_QUEUE")
            onCreate(this)
        }
    }

    // --- INTERFACE FUNCTIONS ---

    /**
     * Read a persisted [SavedState] from the database.
     * @param library [MusicStore.Library] required to restore [SavedState].
     * @return A persisted [SavedState], or null if one could not be found.
     */
    fun read(library: MusicStore.Library): SavedState? {
        requireBackgroundThread()
        // Read the saved state and queue. If the state is non-null, that must imply an
        // existent, albeit possibly empty, queue.
        val rawState = readRawState() ?: return null
        val queue = readQueue(library)
        // Correct the index to match up with a queue that has possibly been shortened due to
        // song removals.
        var actualIndex = rawState.index
        while (queue.getOrNull(actualIndex)?.uid != rawState.songUid && actualIndex > -1) {
            actualIndex--
        }
        // Restore parent item from the music library. If this fails, then the playback mode
        // reverts to "All Songs", which is considered okay.
        val parent = rawState.parentUid?.let { library.find<MusicParent>(it) }
        return SavedState(
            index = actualIndex,
            parent = parent,
            queue = queue,
            positionMs = rawState.positionMs,
            repeatMode = rawState.repeatMode,
            isShuffled = rawState.isShuffled)
    }

    private fun readRawState() =
        readableDatabase.queryAll(TABLE_STATE) { cursor ->
            if (!cursor.moveToFirst()) {
                // Empty, nothing to do.
                return@queryAll null
            }

            val indexIndex = cursor.getColumnIndexOrThrow(StateColumns.INDEX)
            val posIndex = cursor.getColumnIndexOrThrow(StateColumns.POSITION)
            val repeatModeIndex = cursor.getColumnIndexOrThrow(StateColumns.REPEAT_MODE)
            val shuffleIndex = cursor.getColumnIndexOrThrow(StateColumns.IS_SHUFFLED)
            val songUidIndex = cursor.getColumnIndexOrThrow(StateColumns.SONG_UID)
            val parentUidIndex = cursor.getColumnIndexOrThrow(StateColumns.PARENT_UID)
            RawState(
                index = cursor.getInt(indexIndex),
                positionMs = cursor.getLong(posIndex),
                repeatMode = RepeatMode.fromIntCode(cursor.getInt(repeatModeIndex))
                        ?: RepeatMode.NONE,
                isShuffled = cursor.getInt(shuffleIndex) == 1,
                songUid = Music.UID.fromString(cursor.getString(songUidIndex))
                        ?: return@queryAll null,
                parentUid = cursor.getString(parentUidIndex)?.let(Music.UID::fromString))
        }

    private fun readQueue(library: MusicStore.Library): List<Song> {
        val queue = mutableListOf<Song>()
        readableDatabase.queryAll(TABLE_QUEUE) { cursor ->
            val songIndex = cursor.getColumnIndexOrThrow(QueueColumns.SONG_UID)
            while (cursor.moveToNext()) {
                val uid = Music.UID.fromString(cursor.getString(songIndex)) ?: continue
                val song = library.find<Song>(uid) ?: continue
                queue.add(song)
            }
        }

        logD("Successfully read queue of ${queue.size} songs")
        return queue
    }

    /**
     * Clear the previous [SavedState] and write a new one.
     * @param state The new [SavedState] to write, or null to clear the database entirely.
     */
    fun write(state: SavedState?) {
        requireBackgroundThread()
        // Only bother saving a state if a song is actively playing from one.
        // This is not the case with a null state or a state with an out-of-bounds index.
        if (state != null && state.index in state.queue.indices) {
            // Transform saved state into raw state, which can then be written to the database.
            val rawState =
                RawState(
                    index = state.index,
                    positionMs = state.positionMs,
                    repeatMode = state.repeatMode,
                    isShuffled = state.isShuffled,
                    songUid = state.queue[state.index].uid,
                    parentUid = state.parent?.uid)
            writeRawState(rawState)
            writeQueue(state.queue)
            logD("Wrote state")
        } else {
            writeRawState(null)
            writeQueue(null)
            logD("Cleared state")
        }
    }

    private fun writeRawState(rawState: RawState?) {
        writableDatabase.transaction {
            delete(TABLE_STATE, null, null)

            if (rawState != null) {
                val stateData =
                    ContentValues(7).apply {
                        put(BaseColumns._ID, 0)
                        put(StateColumns.SONG_UID, rawState.songUid.toString())
                        put(StateColumns.POSITION, rawState.positionMs)
                        put(StateColumns.PARENT_UID, rawState.parentUid?.toString())
                        put(StateColumns.INDEX, rawState.index)
                        put(StateColumns.IS_SHUFFLED, rawState.isShuffled)
                        put(StateColumns.REPEAT_MODE, rawState.repeatMode.intCode)
                    }

                insert(TABLE_STATE, null, stateData)
            }
        }
    }

    private fun writeQueue(queue: List<Song>?) {
        writableDatabase.writeList(queue ?: listOf(), TABLE_QUEUE) { i, song ->
            ContentValues(2).apply {
                put(BaseColumns._ID, i)
                put(QueueColumns.SONG_UID, song.uid.toString())
            }
        }
    }

    /**
     * A condensed representation of the playback state that can be persisted.
     * @param index The position of the currently playing item in the queue. Can be -1 if the
     * persisted index no longer exists.
     * @param queue The [Song] queue.
     * @param parent The [MusicParent] item currently being played from
     * @param positionMs The current position in the currently played song, in ms
     * @param repeatMode The current [RepeatMode].
     * @param isShuffled Whether the queue is shuffled or not.
     */
    data class SavedState(
        val index: Int,
        val queue: List<Song>,
        val parent: MusicParent?,
        val positionMs: Long,
        val repeatMode: RepeatMode,
        val isShuffled: Boolean
    )

    /**
     * A lower-level form of [SavedState] that contains additional information to create a more
     * reliable restoration process.
     */
    private data class RawState(
        /** @see SavedState.index */
        val index: Int,
        /** @see SavedState.positionMs */
        val positionMs: Long,
        /** @see SavedState.repeatMode */
        val repeatMode: RepeatMode,
        /** @see SavedState.isShuffled */
        val isShuffled: Boolean,
        /**
         * The [Music.UID] of the [Song] that was originally in the queue at [index]. This can be
         * used to restore the currently playing item in the queue if the index mapping changed.
         */
        val songUid: Music.UID,
        /** @see SavedState.parent */
        val parentUid: Music.UID?
    )

    /** Defines the columns used in the playback state table. */
    private object StateColumns {
        /** @see RawState.index */
        const val INDEX = "queue_index"
        /** @see RawState.positionMs */
        const val POSITION = "position"
        /** @see RawState.isShuffled */
        const val IS_SHUFFLED = "is_shuffling"
        /** @see RawState.repeatMode */
        const val REPEAT_MODE = "repeat_mode"
        /** @see RawState.songUid */
        const val SONG_UID = "song_uid"
        /** @see RawState.parentUid */
        const val PARENT_UID = "parent"
    }

    /** Defines the columns used in the queue table. */
    private object QueueColumns {
        /** @see Music.UID */
        const val SONG_UID = "song_uid"
    }

    companion object {
        private const val DB_NAME = "auxio_playback_state.db"
        private const val DB_VERSION = 8
        private const val TABLE_STATE = "playback_state"
        private const val TABLE_QUEUE = "queue"

        @Volatile private var INSTANCE: PlaybackStateDatabase? = null

        /**
         * Get a singleton instance.
         * @return The (possibly newly-created) singleton instance.
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

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
import androidx.core.database.getIntOrNull
import androidx.core.database.sqlite.transaction
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.library.Library
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
            append("${PlaybackStateColumns.INDEX} INTEGER NOT NULL,")
            append("${PlaybackStateColumns.POSITION} LONG NOT NULL,")
            append("${PlaybackStateColumns.REPEAT_MODE} INTEGER NOT NULL,")
            append("${PlaybackStateColumns.SONG_UID} STRING,")
            append("${PlaybackStateColumns.PARENT_UID} STRING")
        }

        db.createTable(TABLE_QUEUE_HEAP) {
            append("${BaseColumns._ID} INTEGER PRIMARY KEY,")
            append("${QueueHeapColumns.SONG_UID} STRING NOT NULL")
        }

        db.createTable(TABLE_QUEUE_MAPPINGS) {
            append("${BaseColumns._ID} INTEGER PRIMARY KEY,")
            append("${QueueMappingColumns.ORDERED_INDEX} INT NOT NULL,")
            append("${QueueMappingColumns.SHUFFLED_INDEX} INT")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    private fun nuke(db: SQLiteDatabase) {
        logD("Nuking database")
        db.apply {
            execSQL("DROP TABLE IF EXISTS $TABLE_STATE")
            execSQL("DROP TABLE IF EXISTS $TABLE_QUEUE_HEAP")
            execSQL("DROP TABLE IF EXISTS $TABLE_QUEUE_MAPPINGS")
            onCreate(this)
        }
    }

    // --- INTERFACE FUNCTIONS ---

    /**
     * Read a persisted [SavedState] from the database.
     * @param library [Library] required to restore [SavedState].
     * @return A persisted [SavedState], or null if one could not be found.
     */
    fun read(library: Library): SavedState? {
        requireBackgroundThread()
        // Read the saved state and queue. If the state is non-null, that must imply an
        // existent, albeit possibly empty, queue.
        val rawState = readRawPlaybackState() ?: return null
        val rawQueueState = readRawQueueState(library)
        // Restore parent item from the music library. If this fails, then the playback mode
        // reverts to "All Songs", which is considered okay.
        val parent = rawState.parentUid?.let { library.find<MusicParent>(it) }
        return SavedState(
            parent = parent,
            queueState =
                Queue.SavedState(
                    heap = rawQueueState.heap,
                    orderedMapping = rawQueueState.orderedMapping,
                    shuffledMapping = rawQueueState.shuffledMapping,
                    index = rawState.index,
                    songUid = rawState.songUid),
            positionMs = rawState.positionMs,
            repeatMode = rawState.repeatMode)
    }

    private fun readRawPlaybackState() =
        readableDatabase.queryAll(TABLE_STATE) { cursor ->
            if (!cursor.moveToFirst()) {
                // Empty, nothing to do.
                return@queryAll null
            }

            val indexIndex = cursor.getColumnIndexOrThrow(PlaybackStateColumns.INDEX)
            val posIndex = cursor.getColumnIndexOrThrow(PlaybackStateColumns.POSITION)
            val repeatModeIndex = cursor.getColumnIndexOrThrow(PlaybackStateColumns.REPEAT_MODE)
            val songUidIndex = cursor.getColumnIndexOrThrow(PlaybackStateColumns.SONG_UID)
            val parentUidIndex = cursor.getColumnIndexOrThrow(PlaybackStateColumns.PARENT_UID)
            RawPlaybackState(
                index = cursor.getInt(indexIndex),
                positionMs = cursor.getLong(posIndex),
                repeatMode = RepeatMode.fromIntCode(cursor.getInt(repeatModeIndex))
                        ?: RepeatMode.NONE,
                songUid = Music.UID.fromString(cursor.getString(songUidIndex))
                        ?: return@queryAll null,
                parentUid = cursor.getString(parentUidIndex)?.let(Music.UID::fromString))
        }

    private fun readRawQueueState(library: Library): RawQueueState {
        val heap = mutableListOf<Song?>()
        readableDatabase.queryAll(TABLE_QUEUE_HEAP) { cursor ->
            if (cursor.count == 0) {
                // Empty, nothing to do.
                return@queryAll
            }

            val songIndex = cursor.getColumnIndexOrThrow(QueueHeapColumns.SONG_UID)
            while (cursor.moveToNext()) {
                heap.add(Music.UID.fromString(cursor.getString(songIndex))?.let(library::find))
            }
        }
        logD("Successfully read queue of ${heap.size} songs")

        val orderedMapping = mutableListOf<Int?>()
        val shuffledMapping = mutableListOf<Int?>()
        readableDatabase.queryAll(TABLE_QUEUE_MAPPINGS) { cursor ->
            if (cursor.count == 0) {
                // Empty, nothing to do.
                return@queryAll
            }

            val orderedIndex = cursor.getColumnIndexOrThrow(QueueMappingColumns.ORDERED_INDEX)
            val shuffledIndex = cursor.getColumnIndexOrThrow(QueueMappingColumns.SHUFFLED_INDEX)
            while (cursor.moveToNext()) {
                orderedMapping.add(cursor.getInt(orderedIndex))
                cursor.getIntOrNull(shuffledIndex)?.let(shuffledMapping::add)
            }
        }

        return RawQueueState(heap, orderedMapping.filterNotNull(), shuffledMapping.filterNotNull())
    }

    /**
     * Clear the previous [SavedState] and write a new one.
     * @param state The new [SavedState] to write, or null to clear the database entirely.
     */
    fun write(state: SavedState?) {
        requireBackgroundThread()
        // Only bother saving a state if a song is actively playing from one.
        // This is not the case with a null state.
        if (state != null) {
            // Transform saved state into raw state, which can then be written to the database.
            val rawPlaybackState =
                RawPlaybackState(
                    index = state.queueState.index,
                    positionMs = state.positionMs,
                    repeatMode = state.repeatMode,
                    songUid = state.queueState.songUid,
                    parentUid = state.parent?.uid)
            writeRawPlaybackState(rawPlaybackState)
            val rawQueueState =
                RawQueueState(
                    heap = state.queueState.heap,
                    orderedMapping = state.queueState.orderedMapping,
                    shuffledMapping = state.queueState.shuffledMapping)
            writeRawQueueState(rawQueueState)
            logD("Wrote state")
        } else {
            writeRawPlaybackState(null)
            writeRawQueueState(null)
            logD("Cleared state")
        }
    }

    private fun writeRawPlaybackState(rawPlaybackState: RawPlaybackState?) {
        writableDatabase.transaction {
            delete(TABLE_STATE, null, null)

            if (rawPlaybackState != null) {
                val stateData =
                    ContentValues(7).apply {
                        put(BaseColumns._ID, 0)
                        put(PlaybackStateColumns.SONG_UID, rawPlaybackState.songUid.toString())
                        put(PlaybackStateColumns.POSITION, rawPlaybackState.positionMs)
                        put(PlaybackStateColumns.PARENT_UID, rawPlaybackState.parentUid?.toString())
                        put(PlaybackStateColumns.INDEX, rawPlaybackState.index)
                        put(PlaybackStateColumns.REPEAT_MODE, rawPlaybackState.repeatMode.intCode)
                    }

                insert(TABLE_STATE, null, stateData)
            }
        }
    }

    private fun writeRawQueueState(rawQueueState: RawQueueState?) {
        writableDatabase.writeList(rawQueueState?.heap ?: listOf(), TABLE_QUEUE_HEAP) { i, song ->
            ContentValues(2).apply {
                put(BaseColumns._ID, i)
                put(QueueHeapColumns.SONG_UID, unlikelyToBeNull(song).uid.toString())
            }
        }

        val combinedMapping =
            rawQueueState?.run {
                if (shuffledMapping.isNotEmpty()) {
                    orderedMapping.zip(shuffledMapping)
                } else {
                    orderedMapping.map { Pair(it, null) }
                }
            }

        writableDatabase.writeList(combinedMapping ?: listOf(), TABLE_QUEUE_MAPPINGS) { i, pair ->
            ContentValues(3).apply {
                put(BaseColumns._ID, i)
                put(QueueMappingColumns.ORDERED_INDEX, pair.first)
                put(QueueMappingColumns.SHUFFLED_INDEX, pair.second)
            }
        }
    }

    /**
     * A condensed representation of the playback state that can be persisted.
     * @param parent The [MusicParent] item currently being played from.
     * @param queueState The [Queue.SavedState]
     * @param positionMs The current position in the currently played song, in ms
     * @param repeatMode The current [RepeatMode].
     */
    data class SavedState(
        val parent: MusicParent?,
        val queueState: Queue.SavedState,
        val positionMs: Long,
        val repeatMode: RepeatMode,
    )

    /** A lower-level form of [SavedState] that contains individual field-based information. */
    private data class RawPlaybackState(
        /** @see Queue.SavedState.index */
        val index: Int,
        /** @see SavedState.positionMs */
        val positionMs: Long,
        /** @see SavedState.repeatMode */
        val repeatMode: RepeatMode,
        /**
         * The [Music.UID] of the [Song] that was originally in the queue at [index]. This can be
         * used to restore the currently playing item in the queue if the index mapping changed.
         */
        val songUid: Music.UID,
        /** @see SavedState.parent */
        val parentUid: Music.UID?
    )

    /** A lower-level form of [Queue.SavedState] that contains heap and mapping information. */
    private data class RawQueueState(
        /** @see Queue.SavedState.heap */
        val heap: List<Song?>,
        /** @see Queue.SavedState.orderedMapping */
        val orderedMapping: List<Int>,
        /** @see Queue.SavedState.shuffledMapping */
        val shuffledMapping: List<Int>
    )

    /** Defines the columns used in the playback state table. */
    private object PlaybackStateColumns {
        /** @see RawPlaybackState.index */
        const val INDEX = "queue_index"
        /** @see RawPlaybackState.positionMs */
        const val POSITION = "position"
        /** @see RawPlaybackState.repeatMode */
        const val REPEAT_MODE = "repeat_mode"
        /** @see RawPlaybackState.songUid */
        const val SONG_UID = "song_uid"
        /** @see RawPlaybackState.parentUid */
        const val PARENT_UID = "parent"
    }

    /** Defines the columns used in the queue heap table. */
    private object QueueHeapColumns {
        /** @see Music.UID */
        const val SONG_UID = "song_uid"
    }

    /** Defines the columns used in the queue mapping table. */
    private object QueueMappingColumns {
        /** @see Queue.SavedState.orderedMapping */
        const val ORDERED_INDEX = "ordered_index"
        /** @see Queue.SavedState.shuffledMapping */
        const val SHUFFLED_INDEX = "shuffled_index"
    }

    companion object {
        private const val DB_NAME = "auxio_playback_state.db"
        private const val DB_VERSION = 9
        private const val TABLE_STATE = "playback_state"
        private const val TABLE_QUEUE_HEAP = "queue_heap"
        private const val TABLE_QUEUE_MAPPINGS = "queue_mapping"

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

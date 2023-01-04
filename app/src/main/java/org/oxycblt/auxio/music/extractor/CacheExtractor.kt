/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music.extractor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import org.oxycblt.auxio.music.Date
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.parsing.correctWhitespace
import org.oxycblt.auxio.music.parsing.splitEscaped
import org.oxycblt.auxio.util.*

/**
 * Defines an Extractor that can load cached music. This is the first step in the music extraction
 * process and is an optimization to avoid the slow [MediaStoreExtractor] and [MetadataExtractor]
 * extraction process.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface CacheExtractor {
    /** Initialize the Extractor by reading the cache data into memory. */
    fun init()

    /**
     * Finalize the Extractor by writing the newly-loaded [Song.Raw]s back into the cache, alongside
     * freeing up memory.
     * @param rawSongs The songs to write into the cache.
     */
    fun finalize(rawSongs: List<Song.Raw>)

    /**
     * Use the cache to populate the given [Song.Raw].
     * @param rawSong The [Song.Raw] to attempt to populate. Note that this [Song.Raw] will only
     * contain the bare minimum information required to load a cache entry.
     * @return An [ExtractionResult] representing the result of the operation.
     * [ExtractionResult.PARSED] is not returned.
     */
    fun populate(rawSong: Song.Raw): ExtractionResult
}

/**
 * A [CacheExtractor] only capable of writing to the cache. This can be used to load music with
 * without the cache if the user desires.
 * @param context [Context] required to read the cache database.
 * @see CacheExtractor
 * @author Alexander Capehart (OxygenCobalt)
 */
open class WriteOnlyCacheExtractor(private val context: Context) : CacheExtractor {
    override fun init() {
        // Nothing to do.
    }

    override fun finalize(rawSongs: List<Song.Raw>) {
        try {
            // Still write out whatever data was extracted.
            CacheDatabase.getInstance(context).write(rawSongs)
        } catch (e: Exception) {
            logE("Unable to save cache database.")
            logE(e.stackTraceToString())
        }
    }

    override fun populate(rawSong: Song.Raw) =
        // Nothing to do.
        ExtractionResult.NONE
}

/**
 * A [CacheExtractor] that supports reading from and writing to the cache.
 * @param context [Context] required to load
 * @see CacheExtractor
 * @author Alexander Capehart
 */
class ReadWriteCacheExtractor(private val context: Context) : WriteOnlyCacheExtractor(context) {
    private var cacheMap: Map<Long, Song.Raw>? = null
    private var invalidate = false

    override fun init() {
        try {
            // Faster to load the whole database into memory than do a query on each
            // populate call.
            cacheMap = CacheDatabase.getInstance(context).read()
        } catch (e: Exception) {
            logE("Unable to load cache database.")
            logE(e.stackTraceToString())
        }
    }

    override fun finalize(rawSongs: List<Song.Raw>) {
        cacheMap = null
        // Same some time by not re-writing the cache if we were able to create the entire
        // library from it. If there is even just one song we could not populate from the
        // cache, then we will re-write it.
        if (invalidate) {
            logD("Cache was invalidated during loading, rewriting")
            super.finalize(rawSongs)
        }
    }

    override fun populate(rawSong: Song.Raw): ExtractionResult {
        val map = cacheMap ?: return ExtractionResult.NONE

        // For a cached raw song to be used, it must exist within the cache and have matching
        // addition and modification timestamps. Technically the addition timestamp doesn't
        // exist, but to safeguard against possible OEM-specific timestamp incoherence, we
        // check for it anyway.
        val cachedRawSong = map[rawSong.mediaStoreId]
        if (cachedRawSong != null &&
            cachedRawSong.dateAdded == rawSong.dateAdded &&
            cachedRawSong.dateModified == rawSong.dateModified) {
            // No built-in "copy from" method for data classes, just have to assign
            // the data ourselves.
            rawSong.musicBrainzId = cachedRawSong.musicBrainzId
            rawSong.name = cachedRawSong.name
            rawSong.sortName = cachedRawSong.sortName

            rawSong.size = cachedRawSong.size
            rawSong.durationMs = cachedRawSong.durationMs

            rawSong.track = cachedRawSong.track
            rawSong.disc = cachedRawSong.disc
            rawSong.date = cachedRawSong.date

            rawSong.albumMusicBrainzId = cachedRawSong.albumMusicBrainzId
            rawSong.albumName = cachedRawSong.albumName
            rawSong.albumSortName = cachedRawSong.albumSortName
            rawSong.albumTypes = cachedRawSong.albumTypes

            rawSong.artistMusicBrainzIds = cachedRawSong.artistMusicBrainzIds
            rawSong.artistNames = cachedRawSong.artistNames
            rawSong.artistSortNames = cachedRawSong.artistSortNames

            rawSong.albumArtistMusicBrainzIds = cachedRawSong.albumArtistMusicBrainzIds
            rawSong.albumArtistNames = cachedRawSong.albumArtistNames
            rawSong.albumArtistSortNames = cachedRawSong.albumArtistSortNames

            rawSong.genreNames = cachedRawSong.genreNames

            return ExtractionResult.CACHED
        }

        // We could not populate this song. This means our cache is stale and should be
        // re-written with newly-loaded music.
        invalidate = true
        return ExtractionResult.NONE
    }
}

/**
 * Internal [Song.Raw] cache database.
 * @author Alexander Capehart (OxygenCobalt)
 * @see [CacheExtractor]
 */
private class CacheDatabase(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        // Map the cacheable raw song fields to database fields. Cache-able in this context
        // means information independent of the file-system, excluding IDs and timestamps required
        // to retrieve items from the cache.
        db.createTable(TABLE_RAW_SONGS) {
            append("${Columns.MEDIA_STORE_ID} LONG PRIMARY KEY,")
            append("${Columns.DATE_ADDED} LONG NOT NULL,")
            append("${Columns.DATE_MODIFIED} LONG NOT NULL,")
            append("${Columns.SIZE} LONG NOT NULL,")
            append("${Columns.DURATION} LONG NOT NULL,")
            append("${Columns.MUSIC_BRAINZ_ID} STRING,")
            append("${Columns.NAME} STRING NOT NULL,")
            append("${Columns.SORT_NAME} STRING,")
            append("${Columns.TRACK} INT,")
            append("${Columns.DISC} INT,")
            append("${Columns.DATE} STRING,")
            append("${Columns.ALBUM_MUSIC_BRAINZ_ID} STRING,")
            append("${Columns.ALBUM_NAME} STRING NOT NULL,")
            append("${Columns.ALBUM_SORT_NAME} STRING,")
            append("${Columns.ALBUM_TYPES} STRING,")
            append("${Columns.ARTIST_MUSIC_BRAINZ_IDS} STRING,")
            append("${Columns.ARTIST_NAMES} STRING,")
            append("${Columns.ARTIST_SORT_NAMES} STRING,")
            append("${Columns.ALBUM_ARTIST_MUSIC_BRAINZ_IDS} STRING,")
            append("${Columns.ALBUM_ARTIST_NAMES} STRING,")
            append("${Columns.ALBUM_ARTIST_SORT_NAMES} STRING,")
            append("${Columns.GENRE_NAMES} STRING")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    private fun nuke(db: SQLiteDatabase) {
        // No cost to nuking this database, only causes higher loading times.
        logD("Nuking database")
        db.apply {
            execSQL("DROP TABLE IF EXISTS $TABLE_RAW_SONGS")
            onCreate(this)
        }
    }

    /**
     * Read out this database into memory.
     * @return A mapping between the MediaStore IDs of the cache entries and a [Song.Raw] containing
     * the cacheable data for the entry. Note that any filesystem-dependent information (excluding
     * IDs and timestamps) is not cached.
     */
    fun read(): Map<Long, Song.Raw> {
        requireBackgroundThread()
        val start = System.currentTimeMillis()
        val map = mutableMapOf<Long, Song.Raw>()
        readableDatabase.queryAll(TABLE_RAW_SONGS) { cursor ->
            if (cursor.count == 0) {
                // Nothing to do.
                return@queryAll
            }

            val idIndex = cursor.getColumnIndexOrThrow(Columns.MEDIA_STORE_ID)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(Columns.DATE_ADDED)
            val dateModifiedIndex = cursor.getColumnIndexOrThrow(Columns.DATE_MODIFIED)

            val sizeIndex = cursor.getColumnIndexOrThrow(Columns.SIZE)
            val durationIndex = cursor.getColumnIndexOrThrow(Columns.DURATION)

            val musicBrainzIdIndex = cursor.getColumnIndexOrThrow(Columns.MUSIC_BRAINZ_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Columns.NAME)
            val sortNameIndex = cursor.getColumnIndexOrThrow(Columns.SORT_NAME)

            val trackIndex = cursor.getColumnIndexOrThrow(Columns.TRACK)
            val discIndex = cursor.getColumnIndexOrThrow(Columns.DISC)
            val dateIndex = cursor.getColumnIndexOrThrow(Columns.DATE)

            val albumMusicBrainzIdIndex =
                cursor.getColumnIndexOrThrow(Columns.ALBUM_MUSIC_BRAINZ_ID)
            val albumNameIndex = cursor.getColumnIndexOrThrow(Columns.ALBUM_NAME)
            val albumSortNameIndex = cursor.getColumnIndexOrThrow(Columns.ALBUM_SORT_NAME)
            val albumTypesIndex = cursor.getColumnIndexOrThrow(Columns.ALBUM_TYPES)

            val artistMusicBrainzIdsIndex =
                cursor.getColumnIndexOrThrow(Columns.ARTIST_MUSIC_BRAINZ_IDS)
            val artistNamesIndex = cursor.getColumnIndexOrThrow(Columns.ARTIST_NAMES)
            val artistSortNamesIndex = cursor.getColumnIndexOrThrow(Columns.ARTIST_SORT_NAMES)

            val albumArtistMusicBrainzIdsIndex =
                cursor.getColumnIndexOrThrow(Columns.ALBUM_ARTIST_MUSIC_BRAINZ_IDS)
            val albumArtistNamesIndex = cursor.getColumnIndexOrThrow(Columns.ALBUM_ARTIST_NAMES)
            val albumArtistSortNamesIndex =
                cursor.getColumnIndexOrThrow(Columns.ALBUM_ARTIST_SORT_NAMES)

            val genresIndex = cursor.getColumnIndexOrThrow(Columns.GENRE_NAMES)

            while (cursor.moveToNext()) {
                val raw = Song.Raw()
                val id = cursor.getLong(idIndex)

                raw.mediaStoreId = id
                raw.dateAdded = cursor.getLong(dateAddedIndex)
                raw.dateModified = cursor.getLong(dateModifiedIndex)

                raw.size = cursor.getLong(sizeIndex)
                raw.durationMs = cursor.getLong(durationIndex)

                raw.musicBrainzId = cursor.getStringOrNull(musicBrainzIdIndex)
                raw.name = cursor.getString(nameIndex)
                raw.sortName = cursor.getStringOrNull(sortNameIndex)

                raw.track = cursor.getIntOrNull(trackIndex)
                raw.disc = cursor.getIntOrNull(discIndex)
                raw.date = cursor.getStringOrNull(dateIndex)?.let(Date::from)

                raw.albumMusicBrainzId = cursor.getStringOrNull(albumMusicBrainzIdIndex)
                raw.albumName = cursor.getString(albumNameIndex)
                raw.albumSortName = cursor.getStringOrNull(albumSortNameIndex)
                cursor.getStringOrNull(albumTypesIndex)?.let {
                    raw.albumTypes = it.parseSQLMultiValue()
                }

                cursor.getStringOrNull(artistMusicBrainzIdsIndex)?.let {
                    raw.artistMusicBrainzIds = it.parseSQLMultiValue()
                }
                cursor.getStringOrNull(artistNamesIndex)?.let {
                    raw.artistNames = it.parseSQLMultiValue()
                }
                cursor.getStringOrNull(artistSortNamesIndex)?.let {
                    raw.artistSortNames = it.parseSQLMultiValue()
                }

                cursor.getStringOrNull(albumArtistMusicBrainzIdsIndex)?.let {
                    raw.albumArtistMusicBrainzIds = it.parseSQLMultiValue()
                }
                cursor.getStringOrNull(albumArtistNamesIndex)?.let {
                    raw.albumArtistNames = it.parseSQLMultiValue()
                }
                cursor.getStringOrNull(albumArtistSortNamesIndex)?.let {
                    raw.albumArtistSortNames = it.parseSQLMultiValue()
                }

                cursor.getStringOrNull(genresIndex)?.let {
                    raw.genreNames = it.parseSQLMultiValue()
                }

                map[id] = raw
            }
        }

        logD("Read cache in ${System.currentTimeMillis() - start}ms")

        return map
    }

    /**
     * Write a new list of [Song.Raw] to this database.
     * @param rawSongs The new [Song.Raw] instances to cache. Note that any filesystem-dependent
     * information (excluding IDs and timestamps) is not cached.
     */
    fun write(rawSongs: List<Song.Raw>) {
        val start = System.currentTimeMillis()

        writableDatabase.writeList(rawSongs, TABLE_RAW_SONGS) { _, rawSong ->
            ContentValues(22).apply {
                put(Columns.MEDIA_STORE_ID, rawSong.mediaStoreId)
                put(Columns.DATE_ADDED, rawSong.dateAdded)
                put(Columns.DATE_MODIFIED, rawSong.dateModified)

                put(Columns.SIZE, rawSong.size)
                put(Columns.DURATION, rawSong.durationMs)

                put(Columns.MUSIC_BRAINZ_ID, rawSong.musicBrainzId)
                put(Columns.NAME, rawSong.name)
                put(Columns.SORT_NAME, rawSong.sortName)

                put(Columns.TRACK, rawSong.track)
                put(Columns.DISC, rawSong.disc)
                put(Columns.DATE, rawSong.date?.toString())

                put(Columns.ALBUM_MUSIC_BRAINZ_ID, rawSong.albumMusicBrainzId)
                put(Columns.ALBUM_NAME, rawSong.albumName)
                put(Columns.ALBUM_SORT_NAME, rawSong.albumSortName)
                put(Columns.ALBUM_TYPES, rawSong.albumTypes.toSQLMultiValue())

                put(Columns.ARTIST_MUSIC_BRAINZ_IDS, rawSong.artistMusicBrainzIds.toSQLMultiValue())
                put(Columns.ARTIST_NAMES, rawSong.artistNames.toSQLMultiValue())
                put(Columns.ARTIST_SORT_NAMES, rawSong.artistSortNames.toSQLMultiValue())

                put(
                    Columns.ALBUM_ARTIST_MUSIC_BRAINZ_IDS,
                    rawSong.albumArtistMusicBrainzIds.toSQLMultiValue())
                put(Columns.ALBUM_ARTIST_NAMES, rawSong.albumArtistNames.toSQLMultiValue())
                put(Columns.ALBUM_ARTIST_SORT_NAMES, rawSong.albumArtistSortNames.toSQLMultiValue())

                put(Columns.GENRE_NAMES, rawSong.genreNames.toSQLMultiValue())
            }
        }

        logD("Wrote cache in ${System.currentTimeMillis() - start}ms")
    }

    // SQLite does not natively support multiple values, so we have to serialize multi-value
    // tags with separators. Not ideal, but nothing we can do.

    /**
     * Transforms the multi-string list into a SQL-safe multi-string value.
     * @return A single string containing all values within the multi-string list, delimited by a
     * ";". Pre-existing ";" characters will be escaped.
     */
    private fun List<String>.toSQLMultiValue() =
        if (isNotEmpty()) {
            joinToString(";") { it.replace(";", "\\;") }
        } else {
            null
        }

    /**
     * Transforms the SQL-safe multi-string value into a multi-string list.
     * @return A list of strings corresponding to the delimited values present within the original
     * string. Escaped delimiters are converted back into their normal forms.
     */
    private fun String.parseSQLMultiValue() = splitEscaped { it == ';' }.correctWhitespace()

    /** Defines the columns used in this database. */
    private object Columns {
        /** @see Song.Raw.mediaStoreId */
        const val MEDIA_STORE_ID = "msid"
        /** @see Song.Raw.dateAdded */
        const val DATE_ADDED = "date_added"
        /** @see Song.Raw.dateModified */
        const val DATE_MODIFIED = "date_modified"
        /** @see Song.Raw.size */
        const val SIZE = "size"
        /** @see Song.Raw.durationMs */
        const val DURATION = "duration"
        /** @see Song.Raw.musicBrainzId */
        const val MUSIC_BRAINZ_ID = "mbid"
        /** @see Song.Raw.name */
        const val NAME = "name"
        /** @see Song.Raw.sortName */
        const val SORT_NAME = "sort_name"
        /** @see Song.Raw.track */
        const val TRACK = "track"
        /** @see Song.Raw.disc */
        const val DISC = "disc"
        /** @see Song.Raw.date */
        const val DATE = "date"
        /** @see Song.Raw.albumMusicBrainzId */
        const val ALBUM_MUSIC_BRAINZ_ID = "album_mbid"
        /** @see Song.Raw.albumName */
        const val ALBUM_NAME = "album"
        /** @see Song.Raw.albumSortName */
        const val ALBUM_SORT_NAME = "album_sort"
        /** @see Song.Raw.albumTypes */
        const val ALBUM_TYPES = "album_types"
        /** @see Song.Raw.artistMusicBrainzIds */
        const val ARTIST_MUSIC_BRAINZ_IDS = "artists_mbid"
        /** @see Song.Raw.artistNames */
        const val ARTIST_NAMES = "artists"
        /** @see Song.Raw.artistSortNames */
        const val ARTIST_SORT_NAMES = "artists_sort"
        /** @see Song.Raw.albumArtistMusicBrainzIds */
        const val ALBUM_ARTIST_MUSIC_BRAINZ_IDS = "album_artists_mbid"
        /** @see Song.Raw.albumArtistNames */
        const val ALBUM_ARTIST_NAMES = "album_artists"
        /** @see Song.Raw.albumArtistSortNames */
        const val ALBUM_ARTIST_SORT_NAMES = "album_artists_sort"
        /** @see Song.Raw.genreNames */
        const val GENRE_NAMES = "genres"
    }

    companion object {
        private const val DB_NAME = "auxio_music_cache.db"
        private const val DB_VERSION = 1
        private const val TABLE_RAW_SONGS = "raw_songs"

        @Volatile private var INSTANCE: CacheDatabase? = null

        /**
         * Get a singleton instance.
         * @return The (possibly newly-created) singleton instance.
         */
        fun getInstance(context: Context): CacheDatabase {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = CacheDatabase(context.applicationContext)
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

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
import androidx.core.database.sqlite.transaction
import java.io.File
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.queryAll
import org.oxycblt.auxio.util.requireBackgroundThread

/**
 * The extractor that caches music metadata for faster use later. The cache is only responsible for
 * storing "intrinsic" data, as in information derived from the file format and not information from
 * the media database or file system. The exceptions are the database ID and modification times for
 * files, as these are required for the cache to function well.
 * @author OxygenCobalt
 */
class CacheExtractor(private val context: Context, private val noop: Boolean) {
    private var cacheMap: Map<Long, Song.Raw>? = null
    private var shouldWriteCache = noop

    fun init() {
        if (noop) {
            return
        }

        try {
            cacheMap = CacheDatabase.getInstance(context).read()
        } catch (e: Exception) {
            logE("Unable to load cache database.")
            logE(e.stackTraceToString())
        }
    }

    /** Write a list of newly-indexed raw songs to the database. */
    fun finalize(rawSongs: List<Song.Raw>) {
        cacheMap = null

        if (shouldWriteCache) {
            // If the entire library could not be loaded from the cache, we need to re-write it
            // with the new library.
            logD("Cache was invalidated during loading, rewriting")
            try {
                CacheDatabase.getInstance(context).write(rawSongs)
            } catch (e: Exception) {
                logE("Unable to save cache database.")
                logE(e.stackTraceToString())
            }
        }
    }

    /**
     * Maybe copy a cached raw song into this instance, assuming that it has not changed since it
     * was last saved. Returns true if a song was loaded.
     */
    fun populateFromCache(rawSong: Song.Raw): Boolean {
        val map = cacheMap ?: return false

        val cachedRawSong = map[rawSong.mediaStoreId]
        if (cachedRawSong != null &&
            cachedRawSong.dateAdded == rawSong.dateAdded &&
            cachedRawSong.dateModified == rawSong.dateModified) {
            rawSong.musicBrainzId = cachedRawSong.musicBrainzId
            rawSong.name = cachedRawSong.name
            rawSong.sortName = cachedRawSong.sortName

            rawSong.size = cachedRawSong.size
            rawSong.durationMs = cachedRawSong.durationMs
            rawSong.formatMimeType = cachedRawSong.formatMimeType

            rawSong.track = cachedRawSong.track
            rawSong.disc = cachedRawSong.disc
            rawSong.date = cachedRawSong.date

            rawSong.albumMusicBrainzId = cachedRawSong.albumMusicBrainzId
            rawSong.albumName = cachedRawSong.albumName
            rawSong.albumSortName = cachedRawSong.albumSortName
            rawSong.albumReleaseTypes = cachedRawSong.albumReleaseTypes

            rawSong.artistMusicBrainzIds = cachedRawSong.artistMusicBrainzIds
            rawSong.artistNames = cachedRawSong.artistNames
            rawSong.artistSortNames = cachedRawSong.artistSortNames

            rawSong.albumArtistMusicBrainzIds = cachedRawSong.albumArtistMusicBrainzIds
            rawSong.albumArtistNames = cachedRawSong.albumArtistNames
            rawSong.albumArtistSortNames = cachedRawSong.albumArtistSortNames

            rawSong.genreNames = cachedRawSong.genreNames

            return true
        }

        shouldWriteCache = true
        return false
    }
}

private class CacheDatabase(context: Context) :
    SQLiteOpenHelper(context, File(context.cacheDir, DB_NAME).absolutePath, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val command =
            StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS $TABLE_RAW_SONGS(")
                .append("${Columns.MEDIA_STORE_ID} LONG PRIMARY KEY,")
                .append("${Columns.DATE_ADDED} LONG NOT NULL,")
                .append("${Columns.DATE_MODIFIED} LONG NOT NULL,")
                .append("${Columns.SIZE} LONG NOT NULL,")
                .append("${Columns.DURATION} LONG NOT NULL,")
                .append("${Columns.FORMAT_MIME_TYPE} STRING,")
                .append("${Columns.MUSIC_BRAINZ_ID} STRING,")
                .append("${Columns.NAME} STRING NOT NULL,")
                .append("${Columns.SORT_NAME} STRING,")
                .append("${Columns.TRACK} INT,")
                .append("${Columns.DISC} INT,")
                .append("${Columns.DATE} STRING,")
                .append("${Columns.ALBUM_MUSIC_BRAINZ_ID} STRING,")
                .append("${Columns.ALBUM_NAME} STRING NOT NULL,")
                .append("${Columns.ALBUM_SORT_NAME} STRING,")
                .append("${Columns.ALBUM_RELEASE_TYPES} STRING,")
                .append("${Columns.ARTIST_MUSIC_BRAINZ_IDS} STRING,")
                .append("${Columns.ARTIST_NAMES} STRING,")
                .append("${Columns.ARTIST_SORT_NAMES} STRING,")
                .append("${Columns.ALBUM_ARTIST_MUSIC_BRAINZ_IDS} STRING,")
                .append("${Columns.ALBUM_ARTIST_NAMES} STRING,")
                .append("${Columns.ALBUM_ARTIST_SORT_NAMES} STRING,")
                .append("${Columns.GENRE_NAMES} STRING)")

        db.execSQL(command.toString())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = nuke(db)

    private fun nuke(db: SQLiteDatabase) {
        logD("Nuking database")
        db.apply {
            execSQL("DROP TABLE IF EXISTS $TABLE_RAW_SONGS")
            onCreate(this)
        }
    }

    fun read(): Map<Long, Song.Raw> {
        requireBackgroundThread()

        val map = mutableMapOf<Long, Song.Raw>()

        readableDatabase.queryAll(TABLE_RAW_SONGS) { cursor ->
            if (cursor.count == 0) return@queryAll

            val idIndex = cursor.getColumnIndexOrThrow(Columns.MEDIA_STORE_ID)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(Columns.DATE_ADDED)
            val dateModifiedIndex = cursor.getColumnIndexOrThrow(Columns.DATE_MODIFIED)

            val sizeIndex = cursor.getColumnIndexOrThrow(Columns.SIZE)
            val durationIndex = cursor.getColumnIndexOrThrow(Columns.DURATION)
            val formatMimeTypeIndex = cursor.getColumnIndexOrThrow(Columns.FORMAT_MIME_TYPE)

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
            val albumReleaseTypesIndex = cursor.getColumnIndexOrThrow(Columns.ALBUM_RELEASE_TYPES)

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
                raw.formatMimeType = cursor.getStringOrNull(formatMimeTypeIndex)

                raw.musicBrainzId = cursor.getStringOrNull(musicBrainzIdIndex)
                raw.name = cursor.getString(nameIndex)
                raw.sortName = cursor.getStringOrNull(sortNameIndex)

                raw.track = cursor.getIntOrNull(trackIndex)
                raw.disc = cursor.getIntOrNull(discIndex)
                raw.date = cursor.getStringOrNull(dateIndex)?.parseTimestamp()

                raw.albumMusicBrainzId = cursor.getStringOrNull(albumMusicBrainzIdIndex)
                raw.albumName = cursor.getString(albumNameIndex)
                raw.albumSortName = cursor.getStringOrNull(albumSortNameIndex)
                cursor.getStringOrNull(albumReleaseTypesIndex)?.parseMultiValue()?.let {
                    raw.albumReleaseTypes = it
                }

                cursor.getStringOrNull(artistMusicBrainzIdsIndex)?.let {
                    raw.artistMusicBrainzIds = it.parseMultiValue()
                }
                cursor.getStringOrNull(artistNamesIndex)?.let {
                    raw.artistNames = it.parseMultiValue()
                }
                cursor.getStringOrNull(artistSortNamesIndex)?.let {
                    raw.artistSortNames = it.parseMultiValue()
                }

                cursor.getStringOrNull(albumArtistMusicBrainzIdsIndex)?.let {
                    raw.albumArtistMusicBrainzIds = it.parseMultiValue()
                }
                cursor.getStringOrNull(albumArtistNamesIndex)?.let {
                    raw.albumArtistNames = it.parseMultiValue()
                }
                cursor.getStringOrNull(albumArtistSortNamesIndex)?.let {
                    raw.albumArtistSortNames = it.parseMultiValue()
                }

                cursor.getStringOrNull(genresIndex)?.let { raw.genreNames = it.parseMultiValue() }

                map[id] = raw
            }
        }

        return map
    }

    fun write(rawSongs: List<Song.Raw>) {
        var position = 0
        val database = writableDatabase
        database.transaction { delete(TABLE_RAW_SONGS, null, null) }

        logD("Cleared raw songs database")

        while (position < rawSongs.size) {
            var i = position

            database.transaction {
                while (i < rawSongs.size) {
                    val rawSong = rawSongs[i]
                    i++

                    val itemData =
                        ContentValues(22).apply {
                            put(Columns.MEDIA_STORE_ID, rawSong.mediaStoreId)
                            put(Columns.DATE_ADDED, rawSong.dateAdded)
                            put(Columns.DATE_MODIFIED, rawSong.dateModified)

                            put(Columns.SIZE, rawSong.size)
                            put(Columns.DURATION, rawSong.durationMs)
                            put(Columns.FORMAT_MIME_TYPE, rawSong.formatMimeType)

                            put(Columns.MUSIC_BRAINZ_ID, rawSong.name)
                            put(Columns.NAME, rawSong.name)
                            put(Columns.SORT_NAME, rawSong.sortName)

                            put(Columns.TRACK, rawSong.track)
                            put(Columns.DISC, rawSong.disc)
                            put(Columns.DATE, rawSong.date?.toString())

                            put(Columns.ALBUM_MUSIC_BRAINZ_ID, rawSong.albumMusicBrainzId)
                            put(Columns.ALBUM_NAME, rawSong.albumName)
                            put(Columns.ALBUM_SORT_NAME, rawSong.albumSortName)
                            put(
                                Columns.ALBUM_RELEASE_TYPES,
                                rawSong.albumReleaseTypes.toMultiValue())

                            put(
                                Columns.ARTIST_MUSIC_BRAINZ_IDS,
                                rawSong.artistMusicBrainzIds.toMultiValue())
                            put(Columns.ARTIST_NAMES, rawSong.artistNames.toMultiValue())
                            put(Columns.ARTIST_SORT_NAMES, rawSong.artistSortNames.toMultiValue())

                            put(
                                Columns.ALBUM_ARTIST_MUSIC_BRAINZ_IDS,
                                rawSong.albumArtistMusicBrainzIds.toMultiValue())
                            put(Columns.ALBUM_ARTIST_NAMES, rawSong.albumArtistNames.toMultiValue())
                            put(
                                Columns.ALBUM_ARTIST_SORT_NAMES,
                                rawSong.albumArtistSortNames.toMultiValue())

                            put(Columns.GENRE_NAMES, rawSong.genreNames.toMultiValue())
                        }

                    insert(TABLE_RAW_SONGS, null, itemData)
                }
            }

            // Update the position at the end, if an insert failed at any point, then
            // the next iteration should skip it.
            position = i

            logD("Wrote batch of raw songs. Position is now at $position")
        }
    }

    // SQLite does not natively support multiple values, so we have to serialize multi-value
    // tags with separators. Not ideal, but nothing we can do.

    private fun List<String>.toMultiValue() =
        if (isNotEmpty()) {
            joinToString(";") { it.replace(";", "\\;") }
        } else {
            null
        }

    private fun String.parseMultiValue() = splitEscaped { it == ';' }

    private object Columns {
        const val MEDIA_STORE_ID = "msid"
        const val DATE_ADDED = "date_added"
        const val DATE_MODIFIED = "date_modified"

        const val SIZE = "size"
        const val DURATION = "duration"
        const val FORMAT_MIME_TYPE = "fmt_mime"

        const val MUSIC_BRAINZ_ID = "mbid"
        const val NAME = "name"
        const val SORT_NAME = "sort_name"

        const val TRACK = "track"
        const val DISC = "disc"
        const val DATE = "date"

        const val ALBUM_MUSIC_BRAINZ_ID = "album_mbid"
        const val ALBUM_NAME = "album"
        const val ALBUM_SORT_NAME = "album_sort"
        const val ALBUM_RELEASE_TYPES = "album_types"

        const val ARTIST_MUSIC_BRAINZ_IDS = "artists_mbid"
        const val ARTIST_NAMES = "artists"
        const val ARTIST_SORT_NAMES = "artists_sort"

        const val ALBUM_ARTIST_MUSIC_BRAINZ_IDS = "album_artists_mbid"
        const val ALBUM_ARTIST_NAMES = "album_artists"
        const val ALBUM_ARTIST_SORT_NAMES = "album_artists_sort"

        const val GENRE_NAMES = "genres"
    }

    companion object {
        const val DB_NAME = "auxio_music_cache.db"
        const val DB_VERSION = 1

        const val TABLE_RAW_SONGS = "raw_songs"

        @Volatile private var INSTANCE: CacheDatabase? = null

        /** Get/Instantiate the single instance of [CacheDatabase]. */
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
